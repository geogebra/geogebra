pluginManagement {
    includeBuild("build-logic")
    repositories {
        maven { url = uri("https://repo.geogebra.net/releases") }
        gradlePluginPortal()
    }
}

include("common")
include("ggbjdk")
include("common-jre")
include("xr-base")

include("desktop")
include("jogl2")
include("giac-jni")

include("web-common")
include("web")
include("web-dev")

include("renderer-base")
include("renderer-desktop")
include("renderer-web")
include("editor-base")
include("editor-desktop")
include("editor-web")

include("test")

include("keyboard-base")
include("keyboard-scientific")
include("keyboard-web")

include("carota-web")

include("gwtutil")
include("gwt-generator")

rootProject.name = "geogebra"

rootProject.children.forEach { project ->
    val projectName = project.name
    if (projectName.startsWith("renderer") || projectName.startsWith("editor")) {
        val projectDirName = "retex/$projectName"
        project.projectDir = file(projectDirName)
        assert(project.projectDir.isDirectory)
    }
}