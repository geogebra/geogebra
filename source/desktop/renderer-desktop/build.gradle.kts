plugins {
    alias(libs.plugins.geogebra.java.library)
}

dependencies {
    api("com.himamis.retex:renderer-base")
    api(project(":canvas-desktop"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    // Add launcher explicitly to avoid legacy loading
    // https://docs.gradle.org/8.12/userguide/upgrading_version_8.html#manually_declaring_dependencies
    testRuntimeOnly(libs.junit.launcher)
}

tasks.test {
    useJUnitPlatform()
}
