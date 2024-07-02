import Desktop_variants_gradle.Variants.nativesLinuxAmd64
import Desktop_variants_gradle.Variants.nativesMacOSXUniversal
import Desktop_variants_gradle.Variants.nativesWindowsAmd64

plugins {
    application
    alias(libs.plugins.geogebra.java)
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.spotbugs)
    alias(libs.plugins.geogebra.variants)
    alias(libs.plugins.geogebra.sourcesets)
}

description = "Parts of GeoGebra related to desktop platforms"

val e2eTest: SourceSet by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

val e2eTestImplementation: Configuration by configurations.getting
e2eTestImplementation.extendsFrom(configurations.testImplementation.get())

dependencies {
    implementation(project(":common"))
    implementation(project(":common-jre"))
    implementation(project(":editor-desktop"))
    implementation(project(":jogl2"))
    implementation(project(":giac-jni"))
    implementation(libs.jsObject)
    implementation(libs.openGeoProver)
    implementation(libs.jna)
    implementation(libs.echosvg)

    implementation(nativesLinuxAmd64(libs.jogl))
    implementation(nativesWindowsAmd64(libs.jogl))
    implementation(nativesMacOSXUniversal(libs.jogl))

    runtimeOnly(nativesLinuxAmd64(libs.gluegen.rt))
    runtimeOnly(nativesWindowsAmd64(libs.gluegen.rt))
    runtimeOnly(nativesMacOSXUniversal(libs.gluegen.rt))

    runtimeOnly(nativesLinuxAmd64(libs.giac.java))
    runtimeOnly(nativesWindowsAmd64(libs.giac.java))
    runtimeOnly(nativesMacOSXUniversal(libs.giac.java))

    testImplementation(project(":keyboard-base"))
    testImplementation(project(":ggbjdk"))
    testImplementation(testFixtures(project(":common-jre")))
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.hamcrest)
}

application {
    mainClass = "org.geogebra.desktop.GeoGebra3D"
}

run {
    // Copying JOGL related native JARs into the same directory where the non-native JAR takes place.
    // JOGL is simply dumb, it cannot work neither with java.library.path nor classpath or anything. Arrgh.
    val joglVersion = libs.versions.jogl.get()
    val gluegen = project.configurations.runtimeClasspath.get().find { it.name == "gluegen-rt-${joglVersion}.jar" }
    val gluegenNatives = project.configurations.runtimeClasspath.get().filter { it.name.startsWith("gluegen-rt-$joglVersion-natives") }
    val gluegenDir = gluegen!!.parent
    for (gluegenNative in gluegenNatives) {
        copy {
            from(gluegenNative.path)
            into(gluegenDir)
        }
    }
    val jogl = project.configurations.runtimeClasspath.get().find { it.name == "jogl-all-${joglVersion}.jar" }
    val joglNatives = project.configurations.runtimeClasspath.get().filter { it.name.startsWith("jogl-all-$joglVersion-natives") }
    val joglDir = jogl!!.parent
    for (joglNative in joglNatives) {
        copy {
            from(joglNative.path)
            into(joglDir)
        }
    }
}

tasks {
    test {
        systemProperty("ggb.prerelease", project.findProperty("prerelease") ?: "true")

        ignoreFailures = true
        outputs.upToDateWhen { false }
    }

    jar {
        manifest {
            attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ") { it.name }
            attributes["Main-Class"] = "org.geogebra.desktop.GeoGebra3D"
        }
    }

    checkstyleMain {
        enabled = false
    }

    register<Zip>("debugJars") {
        dependsOn("jar")
        description = "Collect all jar files in a single archive. Fast: no proguard or code signing."
        archiveBaseName = "jars"
        destinationDirectory = layout.buildDirectory
        from(layout.buildDirectory.file("libs"))
        doLast {
            configurations.runtimeClasspath.get().forEach { jarFile ->
                copy {
                    from(jarFile)
                    into(layout.buildDirectory.file("libs"))
                }
            }
        }
    }

    register<Test>("e2eTest") {
        description = "Run end-to-end tests"
        testClassesDirs = e2eTest.output.classesDirs
        classpath = e2eTest.runtimeClasspath
    }
}
