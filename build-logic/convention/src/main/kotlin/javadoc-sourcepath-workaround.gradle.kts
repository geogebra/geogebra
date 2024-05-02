plugins {
    java
}

// https://issues.gradle.org/browse/GRADLE-2778
// http://discuss.gradle.org/t/javadoc-generation-failed-with-vaadin-dependency/2502/12
tasks.javadoc {
    (options as? StandardJavadocDocletOptions)?.addStringOption("sourcepath", "")
}
