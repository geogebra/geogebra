import org.docstr.gradle.plugins.gwt.GwtDev
import org.docstr.gradle.plugins.gwt.LogLevel

plugins {
    `java-library`
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.gwt)
    alias(libs.plugins.geogebra.javadoc.workaround)
    alias(libs.plugins.geogebra.gwt.dist)
}

gwt {
    // only compilable module
    modules("com.himamis.retex.editor.JLMEditorExportedLibrary")
    devModules("com.himamis.retex.editor.JLMEditorGWTDev")
    logLevel = LogLevel.DEBUG
}

gwtDistribution {
    distributionName = "jlatexmath"
}

dependencies {
    api(project(":renderer-web"))
    api(project(":editor-base"))
    api(project(":gwtutil"))
    api(libs.gwt.dev)
    api(libs.gwt.user)

    implementation(libs.j2objc.annotations)

    testImplementation(libs.gwt.user)
    testImplementation(libs.gwt.dom)

}

tasks.register<GwtDev>("run") {
    dependsOn(tasks.jar)
    war = file("war")
    description = "Starts a codeserver, and a simple webserver for development"
}

tasks.register<Jar>("jarAll") {
    archiveClassifier = "all"
    from(project.sourceSets.main.get().java)
    from(project.sourceSets.main.get().output)
    from(project(":renderer-base").sourceSets.main.get().java)
    from(project(":renderer-base").sourceSets.main.get().output)
}

tasks.register<Jar>("jarSources") {
    dependsOn(tasks.classes)
    archiveClassifier = "sources"
    from(sourceSets.main.get().allSource)
}

