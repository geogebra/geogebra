import org.geogebra.gradle.registerAggregateTask

plugins {
    alias(libs.plugins.geogebra.idea)
}

registerAggregateTask("unitTest", "Runs unit tests in all Java projects.")
registerAggregateTask("lintCheckstyle", "Runs Checkstyle in all applicable Java projects.")
registerAggregateTask("lintPmd", "Runs PMD in all applicable Java projects.")
registerAggregateTask("lintSpotBugs", "Runs SpotBugs in all applicable Java projects.")
