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


afterEvaluate {
    dependencies.add("gwt", files(gwtInternal.incoming.files))
}

gwt {
    // https://github.com/gradle/gradle/issues/15383
    versionCatalogs.named("libs").findVersion("gwt").ifPresent {
        gwtVersion = it.requiredVersion
    }

    maxHeapSize = "2000M"

    jsInteropExports.setGenerate(true)

    compiler.apply {
        strict = true
        disableCastChecking = true

        compileReport = project.hasProperty("greport")
        draftCompile = project.hasProperty("gdraft")
        soycDetailed = project.hasProperty("gsoyc")

        if (project.hasProperty("gworkers")) {
            localWorkers = project.property("gworkers").toString().toInt()
        }

        if (project.hasProperty("gdetailed")) {
            style = org.docstr.gradle.plugins.gwt.Style.DETAILED
        } else {
            disableClassMetadata = true
        }

    }
}