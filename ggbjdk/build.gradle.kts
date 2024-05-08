plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.spotbugs)
}
description = "Platform independent supplementary files for graphics support"

dependencies {
    implementation(project(":common"))
    implementation(libs.spotbugs.annotations)
    implementation(libs.j2objc.annotations)
}
