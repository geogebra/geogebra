import org.docstr.gwt.GwtDevModeTask

plugins {
    java
    id("org.docstr.gwt")
}

logger.debug("GWT Conventions: Configuring project {}", name)

val gwtInternal = configurations.create("gwtInternal") {
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType.SOURCES))
        attribute(VerificationType.VERIFICATION_TYPE_ATTRIBUTE, objects.named(VerificationType.MAIN_SOURCES))
    }
    isCanBeConsumed = false
    isCanBeResolved = true
    isTransitive = true

    exclude("org.gwtproject", "gwt-dev")
    resolutionStrategy {
        eachDependency {
            // This dependency needs to be included as a jar
            if (requested.name == "j2objc-annotations") {
                artifactSelection {
                    selectArtifact(ArtifactTypeDefinition.JAR_TYPE, null, null)
                }
            }
        }
    }
}

gwt {
    // https://github.com/gradle/gradle/issues/15383
    versionCatalogs.named("libs").findVersion("gwt").ifPresent {
        gwtVersion = it.requiredVersion
    }

    maxHeapSize = "4096m"
    cacheDir = project.file("build/gwt/unitCache")

    generateJsInteropExports = true

    compiler.apply {
        strict = true

        compileReport = project.hasProperty("greport")
        draftCompile = project.hasProperty("gdraft")

        if (project.hasProperty("gworkers")) {
            localWorkers = project.property("gworkers").toString().toInt()
        }

        if (project.hasProperty("gdetailed")) {
            style = "DETAILED"
        } else {
            classMetadata = false
        }

    }
    logLevel = "TRACE"
}

afterEvaluate {
    tasks.withType<GwtDevModeTask> {
        val path = project.file("build/gwt/devModeCache")
        jvmArgs = listOf(
            "-Dgwt.persistentunitcachedir=${path.absolutePath}",
            "-Djava.io.tmpdir=${path.absolutePath}"
        )
        doFirst {
            delete { path }
            path.mkdirs()
        }
    }
    dependencies.add("gwtDev", files(gwtInternal.incoming.files))
}
