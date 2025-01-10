plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.javadoc.workaround)
}

description = "Developer tools required to compile web platforms"

dependencies {
    implementation("org.geogebra:common")
    implementation(project(":gwtutil"))
}
