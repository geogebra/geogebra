plugins {
    alias(libs.plugins.geogebra.java.library)
}

dependencies {
    api(project(":renderer-base"))
    testImplementation(libs.junit)
}

