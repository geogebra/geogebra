plugins {
    `java-library`
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.pmd)
}

dependencies {
    implementation(libs.elemental2.core)
    implementation(libs.elemental2.dom)
    implementation(libs.gwt.timer)
    implementation(libs.murok)
    implementation(project(":gwtutil"))
}
