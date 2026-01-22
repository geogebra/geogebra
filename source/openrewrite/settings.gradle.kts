dependencyResolutionManagement {
    versionCatalogs {
        create("rewriteLibs") {
            from(files("gradle/rewriteLibs.versions.toml"))
        }
    }
    repositories {
        mavenCentral()
    }
}

include("recipes")