plugins {
    alias(libs.plugins.geogebra.java.library)
}

description = "Parts of GeoGebra related to Java OpenGL"

dependencies {
    api(libs.gluegen.rt)
    api(libs.jogl)
}

// project has a test directory, but has no executable tests
tasks.named("test") {
    enabled = false
}
