plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
}

group = "org.geogebra"
dependencies {
    testImplementation(libs.junit)
}
