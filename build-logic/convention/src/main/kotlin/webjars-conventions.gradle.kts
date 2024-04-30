import java.nio.file.Paths

plugins {
    java
    id("org.docstr.gwt")
}

val webjarsPath: Provider<RegularFile> = layout.buildDirectory.file("generated/sources/webjars/java/main/")

dependencies {
    gwt(files(webjarsPath))
}

tasks.register("extractJs") {
    doLast {
        configurations.runtimeClasspath.get().forEach {
            val jarPath = Paths.get(file(".").absolutePath).relativize(Paths.get(it.toString()))
            if (jarPath.toString().contains("webjars.npm")) {
                copy {
                    from(zipTree(it).matching { include("**/*.js") })
                    into(webjarsPath)
                    eachFile {
                        path = "${
                            it.name
                                    .replace("-[0-9].*".toRegex(), "")
                                    .replace("-", "_")
                        }/${
                            path.split('/')
                                    .slice(5 downTo -1)
                                    .joinToString("/")
                        }"
                    }
                }
            }
        }
    }
}

tasks.compileJava {
    dependsOn("extractJs")
}
