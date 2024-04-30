plugins {
    `java-library`
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.pmd)
}

description = "Developer tools required to compile web platforms"

dependencies {
    api(libs.gwt.widgets)
    api(libs.gwt.user)
    api(libs.gwt.dev)
    api(libs.gwt.core)
    api(libs.elemental2.core)
    api(libs.elemental2.dom)
    api(libs.elemental2.webstorage)
    api(libs.elemental2.media)
    api(libs.elemental2.webgl)
    api(libs.gwt.resources.api)
    api(libs.gwt.timer)

    implementation(project(":common"))
    implementation(libs.autoService)
}
