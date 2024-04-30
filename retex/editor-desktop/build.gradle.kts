plugins {
    `java-library`
    application
}

dependencies {
    api(project(":renderer-desktop"))
    api(project(":editor-base"))
}

application {
    mainClass = "com.himamis.retex.editor.desktop.Test"
}
