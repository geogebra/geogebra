import com.github.spotbugs.snom.SpotBugsTask
import org.geogebra.gradle.Resources

plugins {
    id("com.github.spotbugs")
}

spotbugs {
    ignoreFailures = true
    excludeFilter = resources.text.fromUri(Resources::class.java.getResource("/spotbugs.xml")!!.toURI()).asFile()
    jvmArgs = listOf("-Dfindbugs.sf.comment=true")
}

tasks.withType<SpotBugsTask> {
    reports {
        create("xml") {
            required = true
        }
    }
}
