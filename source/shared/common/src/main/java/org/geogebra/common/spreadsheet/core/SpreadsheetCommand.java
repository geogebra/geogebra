package org.geogebra.common.spreadsheet.core;

import javax.annotation.Nonnull;

/**
 * Commands that can be executed on spreadsheet ranges.
 */
public enum SpreadsheetCommand {
    SUM("Sum"),
    MEAN("mean");

    public final @Nonnull String command;

    SpreadsheetCommand(@Nonnull String command) {
        this.command = command;
    }

    public @Nonnull String getCommand() {
        return command;
    }
}

