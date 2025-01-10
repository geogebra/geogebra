package org.geogebra.common.spreadsheet.core;

public class SpreadsheetCoords {
	public int row;
	public int column;

	public SpreadsheetCoords() {
		// (0, 0)
	}

	/**
	 * @param row spreadsheet row (0 based)
	 * @param column spreadsheet column (0 based)
	 */
	public SpreadsheetCoords(int row, int column) {
		this.row = row;
		this.column = column;
	}

	/**
	 * @param other other coordinates
	 */
	public void setLocation(SpreadsheetCoords other) {
		this.column = other.column;
		this.row = other.row;
	}

	/**
	 * @param row new row
	 * @param column new column
	 */
	public void setLocation(int row, int column) {
		this.column = column;
		this.row = row;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SpreadsheetCoords
				&& (row == ((SpreadsheetCoords) obj).row)
				&& (column == ((SpreadsheetCoords) obj).column);
	}

	@Override
	public int hashCode() {
		return 100 * row + column;
	}
}
