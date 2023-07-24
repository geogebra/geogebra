package org.geogebra.common.spreadsheet.core;

public class SpreadsheetEventHandler {

	private final Spreadsheet spreadsheet;
	private final TableLayout layout;

	public SpreadsheetEventHandler(Spreadsheet spreadsheet,
			TableLayout layout) {
		this.spreadsheet = spreadsheet;
		this.layout = layout;
	}

}
