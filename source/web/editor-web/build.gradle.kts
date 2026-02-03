import org.docstr.gwt.GwtDevModeTask
plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.gwt)
    alias(libs.plugins.geogebra.gwt.dist)
}


gwt {
    // only compilable module
    modules.add("org.geogebra.editor.JLMEditorExportedLibrary")
    war = file("war")
    devMode {
        modules.add("org.geogebra.editor.JLMEditorGWTDev")
    }
}

gwtDistribution {
    distributionName = "jlatexmath"
}

dependencies {
    api(project(":renderer-web"))
    api("org.geogebra:editor-base")
    api(project(":gwtutil"))
    api(libs.gwt.dev)
    api(libs.gwt.user)

    implementation(libs.j2objc.annotations)

    testImplementation(libs.gwt.user)
    testImplementation(libs.gwt.dom)
    compileOnly(libs.jakarta.servlet.api)

}

tasks.register<Jar>("jarSources") {
    dependsOn(tasks.classes)
    archiveClassifier = "sources"
    from(sourceSets.main.get().allSource)
}