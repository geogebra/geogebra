plugins {
    alias(libs.plugins.geogebra.java.library)
}

dependencies {
    api("com.himamis.retex:renderer-base")
    api(project(":canvas-desktop"))
    testImplementation(libs.junit)
}

