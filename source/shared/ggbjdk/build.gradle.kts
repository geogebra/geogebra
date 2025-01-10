plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.spotbugs)
}
group = "org.geogebra"
description = "Platform independent supplementary files for graphics support"

dependencies {
    implementation(project(":common"))
    implementation(libs.spotbugs.annotations)
    implementation(libs.j2objc.annotations)
}
