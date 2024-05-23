plugins {
    java
    id("org.javacc.javacc")
}

sourceSets {
    main {
        java {
            srcDirs(
                    tasks.compileJavacc.get().outputDirectory,
                    tasks.compileJavacc.get().inputDirectory
            )
        }
    }
}

tasks.register<Delete>("cleanJavacc") {
    description = "Cleans the javacc generated files."
    delete(tasks.compileJavacc)
}
