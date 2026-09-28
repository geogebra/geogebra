import org.geogebra.gradle.registerAggregateTask

plugins {
    alias(libs.plugins.geogebra.idea)
}

registerAggregateTask("lintSpotless", "Runs Spotless check in all applicable Java projects.")
registerAggregateTask("applySpotless", "Apply Spotless changes in all applicable Java projects.")
registerAggregateTask("lintPmd", "Runs PMD in all applicable Java projects.")
