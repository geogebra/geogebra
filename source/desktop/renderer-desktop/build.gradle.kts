plugins {
    alias(libs.plugins.geogebra.java.library)
}

dependencies {
    api("com.himamis.retex:renderer-base")
    testImplementation(libs.junit)
}

