package org.geogebra.gradle

import java.io.IOException

class Resources {
    companion object {
        @Throws(IOException::class)
        fun getString(resourcePath: String): String {
            val ins = Resources::class.java.getResourceAsStream(resourcePath)
            return ins!!.bufferedReader().use { it.readText() }
        }
    }
}
