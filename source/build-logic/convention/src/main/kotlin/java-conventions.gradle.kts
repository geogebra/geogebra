plugins {
    java
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).tags("apiNote", "implNote")
}