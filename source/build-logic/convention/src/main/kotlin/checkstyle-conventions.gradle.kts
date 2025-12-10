import org.geogebra.gradle.Resources

plugins {
    checkstyle
}

checkstyle {
    toolVersion = "12.2.0"
    config = resources.text.fromString(Resources.getString("checkstyle/checkstyle.xml"))
}
