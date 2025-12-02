package org.geogebra.gradle.keychain

import org.geogebra.gradle.common.execute
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class DeleteKeychain : DefaultTask() {

    @get:Inject
    protected abstract val execOps: ExecOperations

    @get:Input
    abstract val keychainName: Property<String>

    @TaskAction
    fun delete() {
        execOps.execute("security delete-keychain ${keychainName.get()}")
    }
}