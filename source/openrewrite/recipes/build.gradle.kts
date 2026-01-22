plugins {
    id("java-library")
}

group = "org.geogebra.openrewrite"

dependencies {
    api(rewriteLibs.rewrite.static.analysis)
    api(rewriteLibs.rewrite.java)
}