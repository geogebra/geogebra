package org.geogebra.gradle

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class Resources {
    companion object {
        @Throws(IOException::class)
        fun getString(resourcePath: String): String {
            val ins = Resources::class.java.getResourceAsStream("/$resourcePath")
            return ins!!.bufferedReader().use { it.readText() }
        }

        @Throws(IOException::class)
        fun copyTo(resourcePath: String, outputPath: Path) {
            val ins = Resources::class.java.getResourceAsStream("/$resourcePath")
            checkNotNull(ins)
            val destination = outputPath.resolve(resourcePath)
            destination.parent.toFile().mkdirs()
            Files.copy(ins, destination, StandardCopyOption.REPLACE_EXISTING)
        }
    }
}
