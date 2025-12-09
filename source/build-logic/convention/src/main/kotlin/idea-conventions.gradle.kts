import org.geogebra.gradle.Resources
import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

plugins {
    id("org.jetbrains.gradle.plugin.idea-ext")
}

val createCodeStyles by tasks.registering {
    doFirst {
        val outputDir = rootDir.resolve(".idea").toPath()
        Resources.copyTo("codeStyles/Project.xml", outputDir)
        Resources.copyTo("codeStyles/codeStyleConfig.xml", outputDir)
        Resources.copyTo("fileTemplates/includes/License.java", outputDir)
        listOf("AnnotationType", "Class", "Enum", "Exception", "Interface", "Record").forEach {
            Resources.copyTo("fileTemplates/internal/${it}.java", outputDir)
        }
    }
}
idea.project.settings.taskTriggers {
    afterSync(createCodeStyles)
}
