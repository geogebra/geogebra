pluginManagement {
    includeBuild("../build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }
    repositories {
        maven { url = uri("https://repo.geogebra.net/releases") }
        mavenCentral()
    }
}

include("canvas-base")
include("common")
include("common-jre")
include("ggbjdk")
include("giac-jni")
include("xr-base")
include("renderer-base")
include("editor-base")
include("keyboard-base")
include("keyboard-scientific")
// includeBuild("../openrewrite") // Commented out - optional tool not in repository
