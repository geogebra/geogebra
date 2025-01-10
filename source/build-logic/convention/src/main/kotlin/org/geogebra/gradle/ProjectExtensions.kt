package org.geogebra.gradle

import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException

fun Project.getCommonVersion(): String {
    val relativePath = "src/main/java/org/geogebra/common/GeoGebraConstants.java"
    val pattern = Regex(" VERSION_STRING = \"(.*)\"")
    val file = try {
        gradle.includedBuild("shared").projectDir.resolve("common/$relativePath")
    } catch (e: UnknownDomainObjectException) {
        project(":common").projectDir.resolve(relativePath)
    }
    var version = "undef"
    file.useLines { lines ->
        val match = lines.map { pattern.find(it) }.filterNotNull().first()
        version = match.groupValues[1]
    }
    return version
}