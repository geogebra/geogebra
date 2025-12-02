package org.geogebra.gradle.android.sdk

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

interface AndroidSdkExtension {

    val sdkVersion: Property<String>

    val home: DirectoryProperty

    val localProperties: RegularFileProperty

    val packages: SetProperty<String>
}

fun buildTools(version: String) = "build-tools;$version"

val platformTools: String get() = "platform-tools"

fun platform(version: String) = "platforms;android-$version"

fun systemImage(
    androidVersion: String, image: SystemImage, architecture: Architecture = defaultArchitecture
) = "system-images;android-$androidVersion;${image.description};${architecture.description}"

val defaultArchitecture: Architecture
    get() = when (System.getProperty("os.arch")) {
        "aarch64" -> Architecture.Arm64v8a
        else -> Architecture.X86_64
    }

enum class SystemImage(val description: String) {
    Default("default"), GoogleApis("google-apis"), PlayStore("google-apis-playstore");
}

enum class Architecture(val description: String) {
    X86_64("x86_64"), Arm64v8a("arm64-v8a")
}