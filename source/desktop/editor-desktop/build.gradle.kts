plugins {
    alias(libs.plugins.geogebra.java.library)
    application
}

dependencies {
    api(project(":renderer-desktop"))
    api("org.geogebra:editor-base")
}

application {
    mainClass = "org.geogebra.editor.desktop.Test"
}
