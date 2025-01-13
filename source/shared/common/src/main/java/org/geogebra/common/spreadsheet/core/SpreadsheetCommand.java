package org.geogebra.common.spreadsheet.core;

public enum SpreadsheetCommand {
    SUM("Sum"),
    MEAN("mean");

    public final String command;

    SpreadsheetCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}

