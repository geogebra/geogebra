plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.pmd)
}

description = "GWT Generators"

dependencies {
    implementation("org.geogebra:common")
    implementation(libs.gwt.resources.api)
    implementation(libs.gwt.resources.processor)
}
