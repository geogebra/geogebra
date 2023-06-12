package org.geogebra.common.spreadsheet.core;

// unclear if needed, cell content provided by DataSource, style by SpreadsheetStyle
final class Cell {

	Object content;

	CellStyle style;

	Cell(Object content, CellStyle style) {
		this.content = content;
		this.style = style;
	}
}
