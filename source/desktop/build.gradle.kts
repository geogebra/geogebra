import org.geogebra.gradle.registerAggregateTask

plugins {
    alias(libs.plugins.geogebra.idea)
}

registerAggregateTask("applySpotless", "Apply Spotless changes in all applicable Java projects.")
