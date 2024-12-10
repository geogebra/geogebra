import org.docstr.gradle.plugins.gwt.GwtDev

plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.gwt)
    alias(libs.plugins.geogebra.javadoc.workaround)
    alias(libs.plugins.geogebra.gwt.dist)
}

gwt {
    maxHeapSize = "2000M"
    modules("com.himamis.retex.renderer.JLaTeXMathGWTExportedLibrary")
    devModules("com.himamis.retex.renderer.JLaTeXMathGWTDev")
}

gwtDistribution {
    distributionName = "jlatexmath"
}

dependencies {
    api(project(":renderer-base"))
    api(project(":gwtutil"))
    api(files(file("build/generated/sources/annotationProcessor/java/main/")))

    api(libs.gwt.core)
    api(libs.gwt.dev)
    api(libs.gwt.user)
    api(libs.gwt.widgets)

    testImplementation(libs.gwt.user)
    testImplementation(libs.gwt.widgets)

    annotationProcessor(project(":gwt-generator"))
    annotationProcessor(libs.gwt.resources.processor)
}

tasks.compileJava.get().options.sourcepath = files(tasks.processResources.get().destinationDir)
        .builtBy(tasks.processResources)

tasks.register<GwtDev>("run") {
    dependsOn(tasks.jar)
    war = file("war")
    cacheDir = file("build/gwt/devModeCache")
    maxHeapSize = "4096m"
    description = "Starts a codeserver, and a simple webserver for development"
}

tasks.register<Jar>("jarSources") {
    dependsOn(tasks.classes)
    archiveClassifier = "sources"
    from(sourceSets.main.get().allSource)
}