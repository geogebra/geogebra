plugins {
    alias(libs.plugins.geogebra.java.library)
}

group = "org.geogebra"
dependencies {
    implementation(project(":common"))
}