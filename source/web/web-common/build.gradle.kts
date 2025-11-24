
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
    api("org.geogebra:common")
    api(project(":canvas-web"))
    api(project(":gwtutil"))
    api(project(":renderer-web"))
    api(project(":editor-web"))
    api("org.geogebra:ggbjdk")
    api(project(":web-dev"))
    api(files(file("build/generated/sources/annotationProcessor/java/main/")))
    api(files(file("build/generated/sources/webjars/java/main/")))
    api(libs.fflate)
    api(libs.pdfJs)
    api(libs.giac.gwt)
    api(libs.gwt.dev)
    api(libs.quickJs)

    annotationProcessor(project(":gwt-generator"))
    annotationProcessor(libs.gwt.resources.processor)
    compileOnly(libs.jakarta.servlet.api)
}

tasks.compileJava.get().options.sourcepath = files(tasks.processResources.get().destinationDir)
        .builtBy(tasks.processResources)

gwt {
    modules.add("org.geogebra.web.WebSimple")
}
