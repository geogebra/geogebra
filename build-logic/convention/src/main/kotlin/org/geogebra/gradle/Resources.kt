package org.geogebra.gradle

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class Resources {
    companion object {
        @Throws(IOException::class)
        fun getString(resourcePath: String): String {
            val ins = Resources::class.java.getResourceAsStream(resourcePath)

            val sb = StringBuilder()
            BufferedReader(InputStreamReader(ins!!, StandardCharsets.UTF_8)).use { reader ->
                var c: Int
                while ((reader.read().also { c = it }) != -1) {
                    sb.append(c.toChar())
                }
            }
            return sb.toString()
        }
    }


}
