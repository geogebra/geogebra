import org.docstr.gradle.plugins.gwt.LogLevel

plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.gwt)
    alias(libs.plugins.geogebra.spotbugs)
    alias(libs.plugins.geogebra.webjars)
}

description = "Web platform code which does not use the GWT Widgets library"

dependencies {
    api(project(":common"))
    api(project(":gwtutil"))
    api(project(":renderer-web"))
    api(project(":editor-web"))
    api(project(":ggbjdk"))
    api(project(":web-dev"))
    api(files(file("build/generated/sources/annotationProcessor/java/main/")))
    api(libs.fflate)
    api(libs.giac.gwt)
    api(libs.gwt.dev)
    api(libs.quickJs)

    annotationProcessor(project(":gwt-generator"))
    annotationProcessor(libs.gwt.resources.processor)
}

tasks.compileJava.get().options.sourcepath = files(tasks.processResources.get().destinationDir)
        .builtBy(tasks.processResources)

gwt {
    modules("org.geogebra.web.WebSimple")
    devModules = modules
    logLevel = LogLevel.TRACE
}
