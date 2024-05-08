import org.geogebra.gradle.Resources

plugins {
    pmd
}

pmd {
    isIgnoreFailures = true
    ruleSets = emptyList()
    ruleSetConfig = resources.text.fromString(Resources.getString("/pmd.xml"))
}