import Gwt_conventions_gradle.MinifyLibTask
import de.aaschmid.gradle.plugins.cpd.Cpd
import groovy.json.JsonOutput
import io.miret.etienne.gradle.sass.CompileSass
import org.apache.tools.ant.taskdefs.condition.Os
import org.docstr.gwt.AbstractBaseTask
import org.docstr.gwt.GwtDevModeTask
import org.geogebra.gradle.getCommonVersion
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.net.URL

plugins {
    alias(libs.plugins.geogebra.java)
    alias(libs.plugins.geogebra.gwt)
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.spotbugs)
    alias(libs.plugins.geogebra.webjars)
    alias(libs.plugins.geogebra.javadoc.workaround)
    alias(libs.plugins.geogebra.app.specs)
    alias(libs.plugins.cpd)
    alias(libs.plugins.sass)
    alias(libs.plugins.geogebra.version) apply false
}

description = "Parts of GeoGebra related to web platforms"

dependencies {
    implementation(project(":carota-web"))
    implementation(project(":web-dev"))
    implementation(project(":web-common"))
    implementation(project(":editor-web"))
    implementation(project(":keyboard-web"))
    implementation(libs.pdfJs)
    implementation(libs.multiplayer)
    implementation(files(file("build/generated/sources/annotationProcessor/java/main/")))

    testImplementation(libs.junit)
    testImplementation(libs.gwt.user)
    testImplementation(libs.gwt.mockito)

    annotationProcessor(project(":gwt-generator"))
    annotationProcessor(libs.gwt.resources.processor)
    compileOnly(libs.jakarta.servlet.api)
}

val warDirRel = "war"

gwt {
    modules = if (project.hasProperty("gmodule")) {
        project.property("gmodule").toString().split(",")
    } else {
        listOf("org.geogebra.web.SuperWeb", "org.geogebra.web.WebSimple",
            "org.geogebra.web.Web", "org.geogebra.web.Tablet3D")
    }

    sourceLevel = "17"

    war = layout.projectDirectory.dir(warDirRel)

    devMode {
        bindAddress = project.findProperty("gbind")?.toString() ?: "localhost"
        superDevMode = true

        val devModule = project.findProperty("gmodule")?.toString()?.replace(",.*$".toRegex(), "") ?: "org.geogebra.web.SuperWeb"
        modules = listOf(devModule)
        startServer = true
    }
}

tasks.compileJava {
    options.sourcepath = files(tasks.processResources.get().destinationDir).builtBy(tasks.processResources)
}

tasks.compileSass {
    // Directory where to output generated CSS:
    outputDir = file("${projectDir}/war/css")

    // Source directory containing sass to compile:
    sourceDir = file("${projectDir}/src/main/resources/scss")

    // Set the output style:
    // Possible values are “expanded” and “compressed”, default is “expanded”.
    style = compressed

    // Source map style:
    //  - file: output source map in a separate file (default)
    //  - embed: embed source map in CSS
    //  - none: do not emit source map.
    sourceMap = none
}

tasks.register<CompileSass>("watchSass") {
    outputDir = file("${projectDir}/war/css")
    sourceDir = file("${projectDir}/src/main/resources/scss")
    style = compressed
    sourceMap = embed

    // Watch sass files in sourceDir for changes
    // (Default is to not watch, compile once and terminate)
    watch()
}

tasks.withType<AbstractBaseTask>().configureEach {
    jvmArgs = listOf("-Xss512M")
}

// Annotation processor scans classpath inefficiently, just increase memory for now
tasks.withType<JavaCompile>().configureEach {
    options.forkOptions.jvmArgs = listOf("-Xmx4g")
}

tasks.withType<Test>().configureEach {
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

tasks.register("run") {
    dependsOn(tasks.gwtDevMode)
}

tasks.withType<Test>().configureEach {
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

val cleanWar by tasks.registering(Delete::class) {
    val deleteTargets = listOf("web", "web3d", "webSimple", "tablet3d", "tabletWin")
    delete(deleteTargets.map { layout.projectDirectory.dir("$warDirRel/$it") })
}

val copyHtml by tasks.registering {
    dependsOn(tasks.compileSass)
    description = "Generate app.html, app-release.html, graphing.html and others"
    group = "application"

    doLast {
        createHtml(true)
    }
}

val copyDevHtml by tasks.registering {
    dependsOn(tasks.compileSass)
    description = "Generate app.html, app-release.html, graphing.html and others without inlined css"
    group = "application"

    doLast {
        createHtml(false)
    }
}

tasks.register("copyHandlebars") {
    dependsOn(tasks.compileSass)
    doLast {
        val handlebarsDir = project.property("handlebarsDir")
        appSpecs.specs.forEach { app ->
            val replaceHtml = if (app.id == "classic") ::classicHtml else ::appHtml

            val templatePath = "${handlebarsDir}/${app.id}-template.handlebars"
            file(templatePath).writeText(handlebars(replaceHtml(app, false, false, true)))
        }
    }
}

tasks.register("prepareS3Upload") {
    dependsOn(tasks.gwtCompile, copyHtml)
    doLast {
        listOf("web3d", "webSimple", "editor", "showcase").forEach { module ->
            val dir = file("war/$module")
            if (dir.exists()) {
                fileTree(dir).forEach { f ->
                    val outFile = file ("$f".replace(dir.absolutePath, "build/s3/$module/"))
                    file(outFile.parent).mkdirs()
                    ant.withGroovyBuilder { "gzip"("src" to f, "zipfile" to outFile) }
                }
            }
            val srcMaps = layout.buildDirectory.file("gwt/deploy/$module/symbolMaps/").get().asFile
            file("build/symbolMapsGz/$module").mkdirs()
            if (srcMaps.exists()) {
                fileTree(srcMaps).forEach { f ->
                    val outFile = file ("$f".replace(srcMaps.absolutePath, "build/symbolMapsGz/$module/"))
                    ant.withGroovyBuilder { "gzip"("src" to f, "zipfile" to outFile) }
                }
            }
        }
    }
}

tasks.register<Copy>("deployIntoWar") {
    dependsOn(copyHtml, tasks.gwtCompile)
    description = "Copies/updates the GWT production compilation directory (web3d, ...) to the war directory."
    into("web3d/sourcemaps/") {
        from(layout.buildDirectory.file("gwt/deploy/web3d/symbolMaps/").get().asFile)
    }
}

tasks.register("mergeDeploy") {
    description = "merge deploy.js and web3d.nocache.js"
    mustRunAfter(tasks.gwtCompile)
    doLast {
        val firstFolder: (files: File) -> String = {files -> files.listFiles()?.let { it[0].name } ?: "" }
        val web3dPermutation = firstFolder(file("./war/web3d/deferredjs"))
        val webSimplePermutation = firstFolder(file("./war/webSimple/deferredjs"))
        val templateJs = "src/main/resources/org/geogebra/web/resources/js/deployggb-template.js"
        val deployText = file(templateJs).readText().replace("%WEB3D_PERMUTATION%", web3dPermutation)
            .replace("%WEBSIMPLE_PERMUTATION%", webSimplePermutation)
        val currentVersion = project.getCommonVersion()
        val localPath = project.findProperty("deployggbRoot")?.toString() ?: "./"
        file("$warDirRel/deployggb.js").writeText(deployText.replace("%MODULE_BASE%", localPath))
        file("$warDirRel/deployggb-latest.js").writeText(deployText.replace("%MODULE_BASE%", "https://www.geogebra.org/apps/latest/"))
        file("$warDirRel/deployggb-${currentVersion}.js").writeText(deployText.replace("%MODULE_BASE%", "https://www.geogebra.org/apps/${currentVersion}/"))
    }
}

tasks.named<GwtDevModeTask>("gwtDevMode") {
    dependsOn(tasks.jar, copyDevHtml)
    args("-style", "PRETTY")
    description = "Starts a codeserver, and a simple webserver for development"

    var sassCompilation: Process? = null
    doFirst {
        val pb = ProcessBuilder(if (Os.isFamily(Os.FAMILY_WINDOWS))
            listOf("cmd", "/c", "START", "/MIN", file("..\\..\\..\\gradlew.bat").absolutePath, "watchSass")
        else
            listOf(file("../../../gradlew").absolutePath, "watchSass")
        )
        pb.directory(file("."))
        sassCompilation = pb.start()
    }
    doLast {
        sassCompilation?.destroy()
    }
}

tasks.register<Zip>("createDraftBundleZip") {
    dependsOn(tasks.compileSass)
    description = "Creates the Math Apps Bundle file."
    destinationDirectory = file(warDirRel)
    archiveBaseName = "geogebra-bundle"
    from(tasks.gwtCompile) {
        include("web3d/**")
        into("GeoGebra/HTML5/5.0")
    }
    from("war/css") {
        include("**")
        into("css")
    }
}

val libDir = "src/main/resources/org/geogebra/web"

val minifyRewritePHYs by tasks.registering(MinifyLibTask::class) {
    sourceFile = project(":web-common").file("${libDir}/resources/js/rewrite_pHYs.js")
}

val minifyCanvas2Pdf by tasks.registering(MinifyLibTask::class) {
    sourceFile = file("${libDir}/pub/js/canvas2pdf.js")
}

val minifyWhammy by tasks.registering(MinifyLibTask::class) {
    sourceFile = file("${libDir}/pub/js/whammy.js")
}

tasks.register("minifyLibs") {
    dependsOn(minifyRewritePHYs, minifyWhammy, minifyCanvas2Pdf)
}

tasks.register("declareAppSpecs") {
    doLast {
        val file = layout.buildDirectory.file("objects/appSpecs").get().asFile
        file.parentFile.mkdirs()
        val fos = FileOutputStream(file)
        val oos = ObjectOutputStream(fos)
        oos.writeObject(appSpecs)
        oos.close()
        fos.close()
    }
}

tasks.register("collectFaIcons") {
    description = "Collect all parameters of the instantiations of the FaIconSpec class"
    doLast {
        val srcDirs = listOf("src/main/java", "../web-common/src/main/java")
        val pattern = """new\s+FaIconSpec\((['"])(.*?)\1\)""".toRegex()
        val parameters = mutableListOf<String>()

        srcDirs.forEach { srcDir ->
            fileTree(srcDir).filter { it.extension == "java" }.forEach { file ->
                val content = file.readText()

                pattern.findAll(content).forEach {
                    val param = it.groupValues[2]
                    parameters.add(param.removePrefix("fa-"))
                }
            }
        }

        if (parameters.isEmpty()) {
            logger.error("No instances for FaIconSpec found!")
        } else {
            val styles = listOf("light", "regular", "solid", "thin")
            val file = file("$projectDir/build/fontAwesomeIcons.txt")
            file.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(mapOf(styles[0] to parameters))))
            logger.lifecycle("File \"fontAwesomeIcons.txt\" created at: ${file.absolutePath}")
        }
    }
}

tasks.test {
    ignoreFailures = System.getenv("CI") != null
    maxHeapSize = "1024m"
}

cpd {
    isIgnoreFailures = System.getenv("CI") != null
    minimumTokenCount = 100
}

tasks.withType<Cpd>().configureEach {
    exclude("**/*.html", "**/*.jj", "**/*.xml", "**/*.svg")
}

val appSpecs = extra.get("appSpecs") as App_specs_convention_gradle.AppSpecs

fun createHtml(inlineCss: Boolean) {
    delete {
        delete(fileTree("war") { include("*.html") })
    }

    copy {
        from(file("../common/src/main/resources/giac"))
        into(warDirRel)
    }

    copy {
        from(file("src/main/resources/org/geogebra/web/resources/war"))
        into(warDirRel)
    }

    appSpecs.specs.forEach { app ->
        val replaceHtml = if (app.id == "classic") ::classicHtml else ::appHtml
        file("war/${app.id}-offline.html").writeText(english(replaceHtml(app, false, true, inlineCss), app.title, ""))
        file("war/${app.id}.html").writeText(english(replaceHtml(app, true, false, inlineCss), app.title, "en"))
        if (app.vendor == "ByCS") {
            file("war/${app.id}-template.html").writeText(
                replaceHtml(
                    app,
                    false,
                    false,
                    false
                ).replace("<!--CODEBASE-->", "/")
            )
        }
    }

    file("war/calculator.html").writeText(file("war/suite.html").readText())
    file("war/notes-murokdev.html").writeText(
        file("war/notes.html").readText().replace(
            "<!--LANGUAGE LINKS-->",
            """<script src="http://localhost:8080/murok.js"></script><script src="http://localhost:8080/canvas2pdf.js"></script><script src="http://localhost:8080/canvas-to-svg.umd.js"></script>"""
        )
    )
}

val html = template("app-template.html")
val splash = template("classic-splash.html")

fun classicHtml(
    app: App_specs_convention_gradle.AppSpec,
    prerelease: Boolean,
    offline: Boolean = false,
    inlineCss: Boolean = false
): String {
    val platformSpecific =
        if (offline) "<!--PLATFORM SPECIFIC CODE--><script src=\"platform.js\"></script>" else "<!--LANGUAGE LINKS-->"
    val inlinedCss = if (inlineCss) getCss() else ""
    return replaceVendor(html, "GeoGebra")
        .replace("<!--SPLASH-->", splash)
        .replace("<!--SPLASH STYLE-->", "<style>" + template("splash-style.css") + "</style>")
        .replace("<!--PRELOADED CSS-->", inlinedCss)
        .replace("<!--ICON-->", "https://www.geogebra.org/apps/icons/geogebra.ico")
        .replace(
            "<!--PLATFORM SPECIFIC CODE-->",
            "<script>prerelease=${prerelease};appOnline=${!offline};</script>" + platformSpecific
        )
        .injectFirebaseMetadata(app.firebaseAppId ?: "", app.measurementId ?: "")
}

fun String.injectFirebaseMetadata(firebaseAppId: String, measurementId: String) =
    replace("<!--FIREBASE APP ID-->", firebaseAppId)
        .replace("<!--MEASUREMENT ID-->", measurementId)

fun template(fileName: String): String {
    val templateDir = "src/main/resources/org/geogebra/web/resources/html"
    return file("$templateDir/$fileName").readText()
}

fun replaceVendor(htmlContent: String, vendor: String): String {
    val htmlContentReplaced = htmlContent.replace("<!--VENDOR-->", vendor)
    if (vendor == "ByCS") {
        return htmlContentReplaced.replace("<!--VENDOR SCRIPTS-->", template("partials/vendor-scripts-bycs.html"))
            .replace("<!--TITLE-->", "ByCS-Board")
    }
    return htmlContentReplaced.replace("<!--VENDOR SCRIPTS-->", template("partials/vendor-scripts-geogebra.html"))
        .replace("<!--TITLE-->", "<!--APP NAME--> - GeoGebra")
}

fun getCss() = css("bundles", "simple-bundle") + css("", "keyboard-styles") + css("bundles", "bundle")

fun downloadAsString(url: String): String = URL(url).readText()

fun css(directory: String?, fileName: String): String {
    val base = if (directory.isNullOrEmpty()) "" else "$directory/"
    val relPath = "$base$fileName.css"
    val text = if (project.hasProperty("downloadStyles")) {
        val ggbVersion = downloadAsString("https://apps-builds.s3-eu-central-1.amazonaws.com/geogebra/tags/version.txt")
        downloadAsString("https://www.geogebra.org/apps/$ggbVersion/css/$relPath")
    } else {
        val fileDir = "war/css/$relPath"
        file(fileDir).readText()
    }
    return "<style id=\"ggbstyle_$fileName\">$text</style>\n"
}

fun appHtml(
    app: App_specs_convention_gradle.AppSpec, prerelease: Boolean,
    offline: Boolean = false,
    inlineCss: Boolean = false
): String {
    val vendor = app.vendor ?: "GeoGebra"
    val appID = app.id.split("-")[0]
    val appTransKey = app.transKey
    var header = if (vendor == "ByCS") "" else template("app-header-beta.html")
        .replace("<!--LOGO-LINK-->", if (offline) "#" else "https://www.geogebra.org")
    header = partials(header, app.partials)
    val inlinedCss = if (inlineCss) getCss() else ""
    val platformSpecific = if (offline) "<script src=\"platform.js\"></script>" else "<!--LANGUAGE LINKS-->"
    val startscreenStyle = "startscreen " + app.id + (if (offline) " offline" else "")
    val appStoreBanner =
        if (app.appStoreId != null) "<meta name=\"apple-itunes-app\" " + "content=\"app-id=${app.appStoreId}\">" else ""
    val splashContent = template("${app.id}-splash.html")
    var splashStyle = "<style>" + file("war/css/app-header.css").readText() + "</style>"
    if (!inlineCss && vendor != "ByCS") {
        splashStyle = "<link rel=stylesheet href=\"css/app-header.css\"/>"
    }
    val firebaseAppId = app.firebaseAppId ?: ""
    val measurementId = app.measurementId ?: ""
    return replaceVendor(html, vendor)
        .replace("<!--SPLASH-->", header + "<div class=\"$startscreenStyle\">$splashContent</div>")
        .replace("<!--SPLASH STYLE-->", splashStyle)
        .replace("<!--PRELOADED CSS-->", inlinedCss)
        .replace(
            "<!--PLATFORM SPECIFIC CODE-->",
            "<script>prerelease=${prerelease};appID=\"${appID}\";appOnline=${!offline};</script>" + platformSpecific
        )
        .replace(
            "<!--ICON-->",
            if (vendor == "ByCS") "/public/assets/board.ico" else "https://www.geogebra.org/apps/icons/${appID}.ico"
        )
        .replace("<!--APP ID-->", appID)
        .replace("<!--APP TRANSKEY-->", appTransKey)
        .replace("<!--APPSTORE BANNER-->", appStoreBanner)
        .injectFirebaseMetadata(firebaseAppId, measurementId)
        .replace("\r\n", "\n") // generate the same on Win and Unix
}

fun String.injectPartial(partialId: String, extension: String = "html") =
    replace("<!--${partialId.uppercase()}-->", template("partials/${partialId}.${extension}"))

fun partials(code: String, appPartials: List<String>): String {
    val partials = appPartials.ifEmpty { listOf("share-button", "signin-button") }
    var returnValue = code
    partials.forEach { returnValue = returnValue.injectPartial(it) }
    return returnValue
}

fun english(raw: String, appName: String, languageTag: String) =
    raw.replace("<!--SIGN IN-->", "Sign in")
        .replace("<!--APP NAME-->", appName)
        .replace("<!--CODEBASE-->", "")
        .replace("<!--LANG-->", languageTag)

fun handlebars(raw: String) =
    raw.replace("<!--SIGN IN-->", "{{signIn}}")
        .replace("<!--APP NAME-->", "{{appName}}")
        .replace("<!--LANG-->", "{{lang}}")
        .replace("<!--CODEBASE-->", "{{codebase}}")
        .replace("<!--LANGUAGE LINKS-->", "{{{languageLinks}}}")
        .injectPartial("inmobitag", "handlebars")
        .injectPartial("firebase", "handlebars")