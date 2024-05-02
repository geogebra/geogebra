description = "GeoGebra"

apply(from = "gradle-scripts/dependencies.gradle")

subprojects {
    afterEvaluate {
        // forward command line properties to every test JVM
        tasks.withType<Test>().configureEach {
            project.properties.forEach { (property, value) ->
                if (value is String) {
                    systemProperty(property.trim(), value)
                }
            }
        }
    }
}

// https://gist.github.com/mashimom/891a55878eda510d316e
if (JavaVersion.current().isJava8Compatible) {
    allprojects {
        tasks.withType<Javadoc>().configureEach {
            (options as? StandardJavadocDocletOptions)?.apply {
                addStringOption("Xdoclint:none", "-quiet")
                addStringOption("tag", "example:X")
            }
        }
    }
}
