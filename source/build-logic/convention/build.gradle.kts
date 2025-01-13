plugins {
    `kotlin-dsl`
}

group = "org.geogebra"

dependencies {
    implementation(libs.idea.ext)
    implementation(libs.gwt.plugin)
    implementation(libs.spotbugs.plugin)
    implementation(libs.javacc.plugin)
}
