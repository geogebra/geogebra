plugins {
    `java-library`
    id("java-conventions")
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).tags("apiNote", "implNote")
}