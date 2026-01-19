plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.gwt)
    alias(libs.plugins.geogebra.javadoc.workaround)
    alias(libs.plugins.geogebra.gwt.dist)
}

gwt {
    war = file("war")
    modules.add("com.himamis.retex.renderer.JLaTeXMathGWTExportedLibrary")
    devMode {
        modules.add("com.himamis.retex.renderer.JLaTeXMathGWTDev")
    }
}

gwtDistribution {
    distributionName = "jlatexmath"
}

dependencies {
    api("com.himamis.retex:renderer-base")
    api(project(":gwtutil"))
    api(project(":canvas-web"))
    implementation("org.geogebra:ggbjdk")
    api(files(file("build/generated/sources/annotationProcessor/java/main/")))

    api(libs.gwt.core)
    api(libs.gwt.dev)
    api(libs.gwt.user)
    api(libs.gwt.widgets)

    testImplementation(libs.gwt.user)
    testImplementation(libs.gwt.widgets)

    annotationProcessor(project(":gwt-generator"))
    annotationProcessor(libs.gwt.resources.processor)
    compileOnly(libs.jakarta.servlet.api)
}

tasks.compileJava.get().options.sourcepath = files(tasks.processResources.get().destinationDir)
        .builtBy(tasks.processResources)

tasks.register<Jar>("jarSources") {
    dependsOn(tasks.classes)
    archiveClassifier = "sources"
    from(sourceSets.main.get().allSource)
}