package org.geogebra.gradle.common

import org.gradle.process.ExecOperations
import org.gradle.process.ExecResult

fun ExecOperations.execute(vararg command: String): ExecResult = exec {
    // even bits need splitting by space, odd bits are literals (don't split)
    commandLine(command.flatMapIndexed({ i, s -> s.trim().split(" \u0000"[i % 2]) }))
}
