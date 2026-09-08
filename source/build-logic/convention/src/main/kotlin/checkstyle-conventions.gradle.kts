import org.geogebra.gradle.Resources

plugins {
    checkstyle
}

checkstyle {
    toolVersion = "12.2.0"
    config = resources.text.fromString(Resources.getString("checkstyle/checkstyle.xml"))
}

tasks.register("lintCheckstyle") {
	description = "Runs Checkstyle in all applicable Java projects."
	dependsOn("checkstyleMain", "checkstyleTest")
}
