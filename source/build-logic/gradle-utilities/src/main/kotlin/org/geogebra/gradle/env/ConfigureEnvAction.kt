package org.geogebra.gradle.env

import org.geogebra.gradle.common.platform
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.mapProperty

interface ConfigureEnvironment {
    fun put(key: String, value: Any)
    fun put(key: String, value: Provider<out Any>)
}

private class ConfigureEnvironmentAction(objects: ObjectFactory) : Action<Task>, ConfigureEnvironment {

    private val env = objects.mapProperty<String, Any>()

    override fun put(key: String, value: Any) {
        env.put(key, value)
    }

    override fun put(key: String, value: Provider<out Any>) {
        env.put(key, value)
    }

    override fun execute(t: Task): Unit = with(t as Exec) {
        val map = env.get()
        val appends = map.entries.filter { it.key.contains("+") }.groupBy { it.key.split("+")[0] }
        map.filterKeys { !it.contains("+") }.forEach { (key, value) ->
            environment(key, value)
        }
        appends.forEach { (key, values) ->
            var finalValue = ""
            for (value in values) {
                finalValue += "${value.value}${platform.pathSeparator}"
            }
            finalValue += "${System.getenv(key)}"
            environment(key, finalValue)
        }
    }
}

/**
 * Configures the environment for this task.
 */
fun Exec.configureEnvironment(action: ConfigureEnvironment.() -> Unit) {
    val configureEnvironmentAction = actions.filterIsInstance<ConfigureEnvironmentAction>().firstOrNull()
        ?: ConfigureEnvironmentAction(project.objects).also { doFirst(it) }
    action(configureEnvironmentAction)
}

fun Exec.provideExecutable(provider: Provider<out Any>) {
    doFirst { executable(provider.get()) }
}