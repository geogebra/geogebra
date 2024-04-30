plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.geogebra.spotbugs)
}

group = "com.himamis.retex"
version = "0.1"

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
        }
    }
}

dependencies {
    implementation(libs.j2objc.annotations)
}

tasks.register<Jar>("jarSources") {
    dependsOn(tasks.classes)
    archiveClassifier = "sources"
    from(sourceSets.main.get().allSource)
}
