plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.pmd)
    alias(libs.plugins.geogebra.checkstyle)
    alias(libs.plugins.geogebra.spotbugs)
}
group = "org.geogebra"
description = "Common canvas library"

dependencies {
    api(libs.spotbugs.annotations)
}