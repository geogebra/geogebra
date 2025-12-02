package org.geogebra.gradle.keychain;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class KeychainPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        tasks.whenTaskAdded {
            if (this is CreateKeychain) {
                tasks.register<DeleteKeychain>("${this.name}Cleanup") {
                    keychainName.set(this.keychainName)
                }
            }
        }
    }
}
