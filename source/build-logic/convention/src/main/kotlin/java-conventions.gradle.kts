plugins {
    java
    id("rewrite-conventions")
    id("com.diffplug.spotless")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    with(options as StandardJavadocDocletOptions) {
        tags("apiNote", "implNote")
        addStringOption("Xmaxwarns", "1")
    }
}

tasks.withType<JavaCompile> {
   options.encoding = "UTF-8"
}

tasks.register("unitTest") {
    description = "Runs unit tests."
    dependsOn("test")
}

spotless {
    java {
        // don't need to set target, it is inferred from java
        targetExclude(fileTree("build/generated") { include("**/*.java") })
        // apply a specific flavor of google-java-format
        palantirJavaFormat("2.97.0").style("GOOGLE")
        importOrder("java", "javax", "org", "com")
        leadingSpacesToTabs(2) // Google style creates 2 spaces => 1 tab
        // fix formatting of type annotations
        formatAnnotations()
        // make sure every file has the following copyright header.
        licenseHeader("""/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

""").onlyIfContentMatches("(?s)^(?!// vendored).*package org.geogebra.*")

    }
}

tasks.register("ciCheck") {
    description = "Run CI tests and checks"
    dependsOn("test")
    dependsOn("spotlessCheck")
    pluginManager.withPlugin("com.github.spotbugs") {
        dependsOn("spotbugsMain")
    }
    pluginManager.withPlugin("pmd") {
        dependsOn("pmdMain", "pmdTest")
    }
}

tasks.register("lintSpotless") {
    description = "Runs Spotless check."
    dependsOn("spotlessCheck")
}

tasks.register("applySpotless") {
    description = "Applies Spotless fix."
    dependsOn("spotlessApply")
}
