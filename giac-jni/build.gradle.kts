plugins {
    alias(libs.plugins.geogebra.java.library)
}

description = "Java Native Interface to connect Giac and GeoGebra"

tasks.register("unzipNatives") {
    val nativesDir = layout.buildDirectory.file("libs/natives")
    description = "Unzips Giac native DLLs. Only Linux is fully supported right now."
    outputs.dir(nativesDir)
    doLast {
        // Copying native Giac DLLs into the same directory where the non-native JAR takes place.
        val giacNatives = project(":desktop").configurations.getByName("runtime").filter { it.name.contains("giac") && it.name.contains("natives") }
        for (giacNative in giacNatives) {
            val myBit = System.getProperty("sun.arch.data.model")
            val thisBit = if (giacNative.name.contains("i586")) 32 else 64
            println("file $giacNative ($thisBit) can run on a $myBit machine?")
            if (myBit == thisBit.toString()) {
                ant.withGroovyBuilder {
                    "unzip"("src" to giacNative.path, "dest" to nativesDir)
                }

                if (org.gradle.internal.os.OperatingSystem.current().isLinux) { // FIXME: do something similar for Windows if needed
                    val sofile = file("$nativesDir/libjavagiac64.so")
                    if (sofile.exists()) {
                        ant.withGroovyBuilder {
                            "move"("file" to sofile, "tofile" to "$nativesDir/libjavagiac.so")
                        }
                    }
                }
            }
        }
    }
}

tasks.register<Exec>("testJni") {
    dependsOn("unzipNatives", tasks.build)
    description = "Tests the JNI executable."
    workingDir = layout.buildDirectory.file("libs").get().asFile
    commandLine("java", "-cp", "giac-jni.jar", "-Djava.library.path=natives", "javagiac.minigiac")
}
