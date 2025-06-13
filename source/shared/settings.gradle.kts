pluginManagement {
    includeBuild("../build-logic")
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


include("common")
include("common-jre")
include("ggbjdk")
include("giac-jni")
include("xr-base")
include("renderer-base")
include("editor-base")
include("keyboard-base")
include("keyboard-scientific")
includeBuild("../openrewrite")
