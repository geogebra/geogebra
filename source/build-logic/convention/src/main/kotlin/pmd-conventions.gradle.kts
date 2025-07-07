import org.geogebra.gradle.Resources

plugins {
    pmd
}

pmd {
    isIgnoreFailures = System.getenv("CI") != null
    ruleSets = emptyList()
    ruleSetConfig = resources.text.fromString(Resources.getString("pmd.xml"))
}