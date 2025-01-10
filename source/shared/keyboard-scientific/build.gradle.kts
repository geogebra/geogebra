plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
}

group = "org.geogebra"
dependencies {
    api(project(":keyboard-base"))
    testImplementation(libs.junit)
}

