package org.geogebra.gradle.keychain

import org.geogebra.gradle.common.execute
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

abstract class CreateKeychain : DefaultTask() {

    data class CertificateImport(val path: String, val password: String)

    @get:Inject
    protected abstract val execOps: ExecOperations

    @get:Input
    abstract val keychainName: Property<String>

    @get:Input
    abstract val keychainPassword: Property<String>

    @get:Input
    abstract val certificates: ListProperty<CertificateImport>

    @get:Input
    abstract val allowedApplications: ListProperty<String>

    init {
        allowedApplications.addAll("/usr/bin/codesign", "/usr/bin/productbuild")
    }

    fun cert(path: String, password: String) {
        certificates.add(CertificateImport(path, password))
    }

    fun app(path: String) {
        allowedApplications.add(path)
    }

    fun tryExecute(cmd: String) {
        try {
            execOps.execute(cmd)
        } catch (e: Exception) {
            // ignore
        }
    }

    @TaskAction
    fun createKeychain() {
        val name = keychainName.get()
        val password = keychainPassword.get()
        val certs = certificates.get()

        tryExecute("security delete-keychain $name")

        execOps.execute("security create-keychain -p $password $name")
        var keychains = ""
        ByteArrayOutputStream().use { os ->
            execOps.exec {
                commandLine("security", "list-keychains", "-d", "common")
                standardOutput = os
            }

            keychains = os.toString()
                    .replace("\"", "")
                    .replace("\\s+".toRegex(), " ").trim()
        }
        execOps.execute("security list-keychains -d user -s $name $keychains")
        tryExecute("security list-keychains -s $name $keychains")
        execOps.execute("security set-keychain-settings $name")

        val apps = allowedApplications.get().fold("") { acc, s -> "$acc -T $s" }.trim()
        for (cert in certs) {
            val certPass = if (!cert.password.isNullOrEmpty())  "-P ${cert.password}" else ""
            execOps.execute("md5", cert.path)
            execOps.execute("security import", cert.path, "-k $name $certPass $apps -f pkcs12")
        }
        tryExecute("security import ${System.getenv("HOME")}/AppleWWDRCAG3.cer -k $name $apps");
        if (certs.isNotEmpty()) {
            tryExecute("security set-key-partition-list -S apple-tool:,apple:,codesign: -s -k " +
                    "$password $name")
        }
        execOps.execute("security default-keychain -d user -s $name")
        execOps.execute("security unlock-keychain -p $password $name")
    }
}
