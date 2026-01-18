pluginManagement {
    includeBuild("../build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
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

include("canvas-desktop")
include("desktop")
include("editor-desktop")
include("renderer-desktop")
include("jogl2")
includeBuild("../shared")
