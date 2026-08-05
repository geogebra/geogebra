plugins {
    id("java-library")
}

group = "org.geogebra.openrewrite"

dependencies {
    api(rewriteLibs.rewrite.static.analysis)
    api(rewriteLibs.rewrite.java)
    api(rewriteLibs.rewrite.testing.frameworks)
    api("org.openrewrite.recipe:rewrite-migrate-java:3.41.0")
}
