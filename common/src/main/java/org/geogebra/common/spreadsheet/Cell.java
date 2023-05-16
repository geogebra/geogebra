package org.geogebra.common.spreadsheet;

final class Cell {

	Object content;

	CellStyle style;

	Cell(Object content, CellStyle style) {
		this.content = content;
		this.style = style;
	}
}
