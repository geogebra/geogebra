plugins {
    alias(libs.plugins.geogebra.java.library)
    `maven-publish`
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.spotbugs)
    alias(libs.plugins.geogebra.javacc)
}

group = "com.himamis.retex"
version = "0.1"

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
        }
    }
}

dependencies {
    javacc(libs.javacc)
    api(project(":renderer-base"))
    implementation(libs.j2objc.annotations)
    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.junit5.jupiter)
    // Add launcher explicitly to avoid legacy loading
    // https://docs.gradle.org/8.12/userguide/upgrading_version_8.html#manually_declaring_dependencies
    testRuntimeOnly(libs.junit5.launcher)
}

tasks.compileJavacc {
    arguments = mapOf(
            "static" to "false",
            "grammar_encoding" to "UTF-8",
            "unicode_input" to "true",
            "JAVA_TEMPLATE_TYPE" to "modern",
            "LEGACY_EXCEPTION_HANDLING" to "false"
    )
}
tasks.compileJava {
    options.encoding = "UTF-8"
}
