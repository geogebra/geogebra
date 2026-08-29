import org.geogebra.gradle.Resources

plugins {
    pmd
}

pmd {
    isIgnoreFailures = System.getenv("CI") != null
    toolVersion = "7.27.0"
    ruleSets = emptyList()
    ruleSetConfig = resources.text.fromString(Resources.getString("pmd.xml"))
}
