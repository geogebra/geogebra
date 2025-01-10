pluginManagement {
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

include("desktop")
include("editor-desktop")
include("renderer-desktop")
include("jogl2")
includeBuild("../shared")
