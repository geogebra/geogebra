package org.geogebra.gradle

import org.gradle.api.Project

/**
 * Registers a task that depends on tasks with the same name in all applicable subprojects.
 * @param name task name
 * @param descriptionText task description
 */
fun Project.registerAggregateTask(name: String, descriptionText: String) {
	tasks.register(name) {
		description = descriptionText
		dependsOn(subprojects.map { project ->
			project.tasks.matching { task -> task.name == name }
		})
	}
}
