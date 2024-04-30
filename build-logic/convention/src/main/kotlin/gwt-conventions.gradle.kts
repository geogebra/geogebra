plugins {
    java
    id("org.docstr.gwt")
}

fun resolveDependencies(configurations: ConfigurationContainer) {
    configurations.configureEach {
        if (arrayOf("implementation", "api").contains(name)) {
            dependencies.configureEach {
                if (this is ProjectDependency) {
                    if (dependencyProject.pluginManager.hasPlugin("java")) {
                        logger.debug("GWT Conventions: Adding sources dependency to project {}", dependencyProject.name)
                        val javaSources = dependencyProject.sourceSets.main.get().allSource.srcDirs
                        val javaSourceFiles = files(javaSources)

                        project.dependencies.add("gwt", javaSourceFiles)

                        logger.debug("GWT Conventions: Recursively adding dependencies from {}", dependencyProject.name)
                        resolveDependencies(dependencyProject.configurations)
                    } else {
                        logger.debug("GWT Conventions: Dependency has no java plugin applied. {}", dependencyProject.name)
                    }
                } else if (this is MinimalExternalModuleDependency) {
                    logger.debug("GWT Conventions: Adding sources dependency to external dependency {}", this)
                    val dependencyProvider = provider { this }
                    val variant = project.dependencies.variantOf(dependencyProvider) {
                        classifier("sources")
                    }

                    project.dependencies.add("gwt", variant)
                } else if (this is FileCollectionDependency) {
                    logger.debug("GWT Conventions: Adding dependency to file collection {}", files)

                    project.dependencies.add("gwt", this)
                }
            }
        }
    }
}

logger.debug("GWT Conventions: Configuring project {}", name)

resolveDependencies(configurations)

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