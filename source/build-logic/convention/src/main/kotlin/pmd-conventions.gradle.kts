import org.geogebra.gradle.Resources

plugins {
    pmd
}

pmd {
    isIgnoreFailures = System.getenv("CI") != null
    toolVersion = "7.28.0"
    ruleSets = emptyList()
    ruleSetConfig = resources.text.fromString(Resources.getString("pmd.xml"))
}

tasks.register("lintPmd") {
	description = "Runs PMD in all applicable Java projects."
	dependsOn("pmdMain", "pmdTest")
}
