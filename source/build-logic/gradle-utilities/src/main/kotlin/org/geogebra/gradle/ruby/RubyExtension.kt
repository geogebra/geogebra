package org.geogebra.gradle.ruby

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

interface RubyExtension {

    /** Ruby version */
    val version: Property<String>

    /** Path to rbenv home directory */
    val rbenvHome: DirectoryProperty

    /** Path to gem bins */
    val gemBinPath: DirectoryProperty

    /** Path to gems */
    val gemPath: DirectoryProperty
}
