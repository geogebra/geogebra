import org.geogebra.gradle.Resources

plugins {
    checkstyle
}

checkstyle {
    toolVersion = "10.26.1"
    config = resources.text.fromString(Resources.getString("checkstyle/checkstyle.xml"))
}
