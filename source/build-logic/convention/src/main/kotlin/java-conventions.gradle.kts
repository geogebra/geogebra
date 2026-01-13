plugins {
    java
    id("rewrite-conventions")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).tags("apiNote", "implNote")
}

tasks.withType<JavaCompile> {
   options.encoding = "UTF-8"
}

tasks.register("ciCheck") {
    description = "Run CI tests and checks"
    dependsOn("test")
    pluginManager.withPlugin("com.github.spotbugs") {
        dependsOn("spotbugsMain")
    }
    pluginManager.withPlugin("pmd") {
        dependsOn("pmdMain")
    }
    pluginManager.withPlugin("checkstyle") {
        dependsOn("checkstyleMain", "checkstyleTest")
    }
}