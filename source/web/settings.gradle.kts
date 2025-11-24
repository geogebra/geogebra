pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("../build-logic")
}

dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://repo.geogebra.net/releases") }
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }
}

include("canvas-web")
include("carota-web")
include("editor-web")
include("gwt-generator")
include("gwtutil")
include("keyboard-web")
include("renderer-web")
include("uitest")
include("web")
include("web-common")
include("web-dev")
includeBuild("../shared")
