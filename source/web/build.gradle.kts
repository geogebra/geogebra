import org.geogebra.gradle.registerAggregateTask

plugins {
    alias(libs.plugins.geogebra.idea)
}

registerAggregateTask("unitTest", "Runs unit tests in all Java projects.")
registerAggregateTask("lintSpotless", "Runs Spotless check in all applicable Java projects.")
registerAggregateTask("applySpotless", "Apply Spotless changes in all applicable Java projects.")
registerAggregateTask("lintPmd", "Runs PMD in all applicable Java projects.")
registerAggregateTask("lintSpotBugs", "Runs SpotBugs in all applicable Java projects.")
