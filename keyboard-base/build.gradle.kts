plugins {
    `java-library`
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
}

dependencies {
    testImplementation(libs.junit)
}
