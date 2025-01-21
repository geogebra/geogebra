plugins {
    `java-test-fixtures`
    jacoco
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.spotbugs)
    alias(libs.plugins.geogebra.sourcesets)
}

group = "org.geogebra"
description = "Common parts of GeoGebra that depends on JRE support."

dependencies {
    api(project(":common"))
    api(libs.mozilla.rhino)

    implementation(project(":giac-jni"))
    implementation(project(":renderer-base"))
    implementation(project(":editor-base"))

    testImplementation(project(":ggbjdk"))
    testImplementation(libs.junit)
    testImplementation(libs.hamcrest)
    testImplementation(libs.mockito.core)

    // Junit 5 support with backward compatibility
    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.junit5.jupiter)
    testImplementation(libs.junit5.vintage)

    testFixturesImplementation(project(":ggbjdk"))
    testFixturesImplementation(libs.junit)
    testFixturesImplementation(libs.hamcrest)
    testFixturesImplementation(libs.mockito.core)
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.test {
    ignoreFailures = true
}

val jacocoSources by configurations.creating {
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(VerificationType.VERIFICATION_TYPE_ATTRIBUTE, objects.named(VerificationType.MAIN_SOURCES))
    }
    isCanBeConsumed = false
    isVisible = false
}

val jacocoClasses by configurations.creating {
    extendsFrom(configurations.implementation.get())
    isCanBeConsumed = false
    isVisible = false
}

val sourceDirs = jacocoSources.files.filter {
    it.isDirectory && it.absolutePath.endsWith("src/main/java")
}

val classes = jacocoClasses.files.filter {
    it.path.replace("\\","/").contains(Regex("source/(shared|jvm)/.*/build/libs"))
}.map {
    zipTree(it).matching {
        exclude("org/apache/**", "edu/**",
                "org/geogebra/common/kernel/barycentric/**")
    }
}.reduce(FileCollection::plus)

tasks.jacocoTestReport {
    reports {
        xml.required = true
        html.required = false
    }
    additionalSourceDirs(*sourceDirs.toTypedArray())
    additionalClassDirs(classes)
}
