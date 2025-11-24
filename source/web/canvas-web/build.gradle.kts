plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.spotbugs)
}

dependencies {
    implementation("org.geogebra:canvas-base")
    implementation("org.geogebra:ggbjdk")
    api(project(":gwtutil"))
    api(libs.elemental2.dom)
    api(libs.gwt.widgets)
}