package org.geogebra.gradle.android.sdk

import org.geogebra.gradle.common.isMac
import org.geogebra.gradle.common.isWindows
import org.geogebra.gradle.common.platform
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.support.unzipTo
import org.gradle.process.ExecOperations
import java.io.ByteArrayInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.URI
import javax.inject.Inject

abstract class AndroidSdkPlugin : Plugin<Project> {

    @get:Inject
    protected abstract val javaToolchainService: JavaToolchainService

    @get:Inject 
    protected abstract val execOps: ExecOperations

    companion object {
        const val ANDROID_SDK_EXTENSION_NAME = "androidSdk"
    }

    override fun apply(target: Project) {
        with(target) {
            val android = project.extensions.create<AndroidSdkExtension>(ANDROID_SDK_EXTENSION_NAME)

            with(android) {
                sdkVersion.convention("11076708")
                localProperties.convention(project.rootProject.layout.projectDirectory.file("local.properties"))
                home.convention(layout.projectDirectory.dir(defaultAndroidSdkLocation))
            }

            afterEvaluate {
                writeLocalProperties(android)
                downloadAndroidSdk(android)
                installAndroidSdk(android)
            }
        }
    }

    private fun Project.downloadAndroidSdk(android: AndroidSdkExtension) {
        if (android.home.dir("cmdline-tools").get().asFile.exists()) {
            return
        }
        val platform = if (isMac) "mac" else if (isWindows) "win" else "linux"
        logger.warn("Downloading Android SDK...")
        val sdkFilename = "commandlinetools-$platform-${android.sdkVersion.get()}_latest.zip"
        val sdkUrl = "https://dl.google.com/android/repository/${sdkFilename}"
        mkdir(project.layout.buildDirectory)
        val outputZip = project.layout.buildDirectory.file("androidsdk.zip")
        URI(sdkUrl).toURL().openStream().use {
            it.copyTo(FileOutputStream(outputZip.get().asFile))
        }
        val cmdlineToolsSrc = project.layout.buildDirectory.dir("androidsdk")
        // Unzip cmdline-tools
        unzipTo(
            cmdlineToolsSrc.get().asFile,
            outputZip.get().asFile
        )
        copy {
            from(cmdlineToolsSrc.map { it.dir("cmdline-tools") }) {
                include("**/*")
            }
            into(android.home.map { it.dir("cmdline-tools/latest") })
        }
        if (!isWindows) {
            execOps.exec {
                executable("chmod")
                args("-R", "+x")
                args(android.home.file("cmdline-tools/latest/bin").get())
            }
        }
    }

    private fun Project.installAndroidSdk(android: AndroidSdkExtension) {
        logger.warn("Installing Android SDK...")
        val launcher = javaToolchainService.launcherFor {
            languageVersion = JavaLanguageVersion.of(17)
        }
        val cmdlineToolsPath = possibleSdkManagerPaths.map {
            android.home.file("$it/sdkmanager${platform.executableExtension}").get()
        }.firstOrNull { it.asFile.exists() }

        if (cmdlineToolsPath == null) {
            throw GradleException(
                "Could not cmdline-tools path in the Android sdk at ${android.home.get()}. " +
                        "Please specify the sdk location explicitly at android.home gradle extension."
            )
        }
        execOps.exec {
            executable(cmdlineToolsPath)
            environment("JAVA_HOME", launcher.get().metadata.installationPath)
            args("--sdk_root=${android.home.get()}")
            args(android.packages.get())
            standardInput = ByteArrayInputStream("yes".toByteArray())
            standardOutput = OutputStream.nullOutputStream()
            errorOutput = OutputStream.nullOutputStream()
        }
        logger.warn("Done")
    }

    private fun writeLocalProperties(android: AndroidSdkExtension) {
        val file = android.localProperties.get().asFile
        val normalizedPath = android.home.get().asFile.absolutePath.replace('\\', '/')
        file.writeText("sdk.dir=${normalizedPath}")
    }

    private val defaultAndroidSdkLocation = System.getenv(platform.variables.home) + when {
        isMac -> "/Library/Android/sdk"
        isWindows -> "/Android/sdk"
        else -> "/Android/Sdk"
    }

    private val possibleSdkManagerPaths = listOf("cmdline-tools/bin", "cmdline-tools/latest/bin", "tools/bin")
}