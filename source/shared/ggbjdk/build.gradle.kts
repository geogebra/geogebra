plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.spotbugs)
}
group = "org.geogebra"
description = "Platform independent supplementary files for graphics support"

dependencies {
    implementation(project(":canvas-base"))
    implementation(libs.spotbugs.annotations)
    implementation(libs.j2objc.annotations)
}
