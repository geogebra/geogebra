plugins {
    alias(libs.plugins.geogebra.java.library)
}

description = "Parts of GeoGebra related to Java OpenGL"

dependencies {
    api(libs.gluegen.rt)
    api(libs.jogl)
}
