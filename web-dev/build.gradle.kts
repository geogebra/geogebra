plugins {
    `java-library`
    alias(libs.plugins.geogebra.javadoc.workaround)
}

description = "Developer tools required to compile web platforms"

dependencies {
    implementation(project(":common"))
    implementation(project(":gwtutil"))
}
