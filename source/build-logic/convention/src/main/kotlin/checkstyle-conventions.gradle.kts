import org.geogebra.gradle.Resources

plugins {
    checkstyle
}

checkstyle {
    toolVersion = "10.21.3"
    config = resources.text.fromString(Resources.getString("checkstyle/checkstyle.xml"))
}
