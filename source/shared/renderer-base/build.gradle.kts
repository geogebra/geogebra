plugins {
    alias(libs.plugins.geogebra.java.library)
    alias(libs.plugins.geogebra.spotbugs)
    `maven-publish`
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
    api(libs.findbugs.annotations)
}

tasks.register<Jar>("jarSources") {
    dependsOn(tasks.classes)
    archiveClassifier = "sources"
    from(sourceSets.main.get().allSource)
}
