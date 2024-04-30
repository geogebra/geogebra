plugins {
    `java-library`
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
}

dependencies {
    api(project(":keyboard-base"))
    testImplementation(libs.junit)
}

