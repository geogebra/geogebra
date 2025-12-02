package org.geogebra.gradle.android.svg2xml

import com.android.ide.common.vectordrawable.Svg2Vector
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.tasks.*
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.File
import java.io.FileOutputStream

/** Converts SVG files provided at [inputDir] to Android resource XML files at [outputDir]. */
abstract class ConvertSvgToXmlTask : DefaultTask() {

    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputDirectory
    /** Path to svg input files */
    abstract val inputDir: DirectoryProperty

    @get:OutputDirectory
    /** Path to android xml resource files */
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun convert(inputChanges: InputChanges) {
        inputChanges.getFileChanges(inputDir).forEach { change ->
            if (change.fileType == FileType.DIRECTORY) return

            logger.info("${change.changeType}: ${change.normalizedPath}")
            val targetFile = outputDir.file(change.normalizedPath).get().asFile
            if (change.changeType == ChangeType.REMOVED) {
                targetFile.delete()
            } else {
                val fileName = targetFile.name
                val index = fileName.lastIndexOf(".")
                val baseName = fileName.substring(0, index)
                val newFile = File(targetFile.parent, "$baseName.xml")
                Svg2Vector.parseSvgToXml(change.file.toPath(), FileOutputStream(newFile))
            }
        }
    }
}
