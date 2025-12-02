package org.geogebra.gradle.common

import org.apache.tools.ant.taskdefs.condition.Os

val isWindows = Os.isFamily(Os.FAMILY_WINDOWS)

val isMac = Os.isFamily(Os.FAMILY_MAC)

sealed class Variables(val home: String, val path: String) {
    object Unix : Variables("HOME", "PATH")
    object Windows : Variables("LOCALAPPDATA", "Path")
}

sealed class Platform(val variables: Variables, val pathSeparator: String, val executableExtension: String) {
    object Unix : Platform(Variables.Unix, ":", "")
    object Windows : Platform(Variables.Windows, ";", ".bat")
}

val platform = if (isWindows) Platform.Windows else Platform.Unix
