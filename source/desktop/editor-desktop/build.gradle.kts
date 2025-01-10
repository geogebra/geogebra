plugins {
    alias(libs.plugins.geogebra.java.library)
    application
}

dependencies {
    api(project(":renderer-desktop"))
    api("com.himamis.retex:editor-base")
}

application {
    mainClass = "com.himamis.retex.editor.desktop.Test"
}
