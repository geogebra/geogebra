plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

repositories {
    google()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("keychainPlugin") {
            id = "org.geogebra.gradle.keychain"
            displayName = "GeoGebra Keychain Plugin"
            description = "Keychain plugin for MacOS based projects"
            implementationClass = "org.geogebra.gradle.keychain.KeychainPlugin"
        }
        create("androidSdkPlugin") {
            id = "org.geogebra.gradle.android.sdk"
            displayName = "Android SDK Plugin"
            description = "Plugin downloads and prepapres the Android SDK"
            implementationClass = "org.geogebra.gradle.android.sdk.AndroidSdkPlugin"
        }
        create("rubyPlugin") {
            id = "org.geogebra.gradle.ruby"
            displayName = "Rbenv Ruby Plugin"
            description = "Downloads Ruby with Rbenv and provides tasks to install and execute gems"
            implementationClass = "org.geogebra.gradle.ruby.RubyPlugin"
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    compileOnly(libs.android.sdk.tools)
    compileOnly(libs.android.gradle.plugin)
}

group = "org.geogebra.gradle"
version = "0.1.35"

publishing {
    repositories {
        maven {
            url = uri("https://repo.geogebra.net/releases")
            credentials {
                username = findProperty("mavenUsername").toString()
                password = findProperty("mavenPassword").toString()
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}
