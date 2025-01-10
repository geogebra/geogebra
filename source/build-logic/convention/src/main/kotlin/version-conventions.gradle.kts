import org.geogebra.gradle.getCommonVersion

tasks.register("writeVersion") {
    description = "Writes the GeoGebra version to the version.txt file in the build directory."
    doLast {
        mkdir(layout.buildDirectory)
        layout.buildDirectory.file("version.txt").get().asFile.writeText(getCommonVersion())
    }
}