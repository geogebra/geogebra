import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.spotbugs)
    alias(libs.plugins.geogebra.sourcesets)
    alias(libs.plugins.geogebra.javacc)
}

description = "Common parts of GeoGebra for various platforms"

dependencies {
    javacc(libs.javacc)
    api(project(":editor-base"))
    api(project(":renderer-base"))
    api(libs.spotbugs.annotations)
    api(libs.findbugs.annotations)
    implementation(libs.j2objc.annotations)
}

tasks.compileJavacc {
    arguments = mapOf(
            "static" to "false",
            "JAVA_TEMPLATE_TYPE" to "modern",
            "LEGACY_EXCEPTION_HANDLING" to "false"
    )
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.register("versionBump") {
    doLast {
        var version = "undef"
        val pattern = " VERSION_STRING = \"(.*)\"".toRegex()
        val constants = file("../common/src/main/java/org/geogebra/common/GeoGebraConstants.java")

        constants.useLines { lines ->
            for (line in lines) {
                val match = pattern.find(line)
                if (match != null) {
                    version = match.destructured.component1()
                    break
                }
            }
        }
        val parts = version.split(".").map { it.toInt() }.toMutableList()
        parts[2]++
        parts[3] = 0
        version = parts.joinToString(".")

        var text = constants.readText()
        val dateFormat = DateTimeFormatter.ofPattern("dd MMMM Y", Locale.US)
        text = text.replace("BUILD_DATE.*".toRegex(), "BUILD_DATE = \"${LocalDate.now().format(dateFormat)}\";")
        text = text.replace("final String VERSION_STRING.*".toRegex(), "final String VERSION_STRING = \"$version\";")
        constants.writeText(text)

        // version.txt for Jenkins build number
        file("build/").mkdirs()
        file("build/version.txt").writeText(version)
    }
}


tasks.test {
    ignoreFailures = true
}

tasks.spotbugsMain {
    maxHeapSize = "1g"
}
