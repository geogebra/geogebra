import org.docstr.gradle.plugins.gwt.LogLevel

plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.gwt)
}

dependencies {
    api(project(":keyboard-base"))
    api(project(":keyboard-scientific"))
    api(project(":gwtutil"))

    implementation(project(":common")) //needed for Unicode and Language
    implementation(project(":web-dev")) //needed for Unicode and Language
    implementation(files(layout.buildDirectory.file("generated/sources/annotationProcessor/java/main/")))
    implementation(libs.gwt.widgets)

    annotationProcessor(project(":gwt-generator"))
    annotationProcessor(libs.gwt.resources.processor)
}

tasks.compileJava {
    options.sourcepath = files(tasks.processResources.get().destinationDir).builtBy(tasks.processResources)
}

val module = "org.geogebra.keyboard.KeyboardWeb"

gwt {
    modules(module)
    devModules(module)

    logLevel = LogLevel.INFO
}
