plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.gwt.plugin)
    implementation(libs.spotbugs.plugin)
    implementation(libs.javacc.plugin)
}
