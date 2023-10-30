package org.geogebra.common.spreadsheet.core;

/**
 * Class to convert TabularData to string.
 * Format: colunms are separated by \t and rows are separated by \n .
 */
public final class TabularContent {
	private final TabularData tabularData;

	public TabularContent(TabularData tabularData) {
		this.tabularData = tabularData;
	}

	/**
	 * Converts the range of cell values to a separated string
	 * by tabs between columns and newlines between rows.
	 *
	 * @param range to convert.
	 * @return the string representation.
	 */
	String toString(TabularRange range) {
		StringBuilder sb = new StringBuilder();
		for (int row = range.fromRow; row < range.toRow + 1; row++) {
			for (int column = range.fromCol; column < range.toCol + 1; column++) {
				Object value = tabularData.contentAt(row, column);
				sb.append(value);
				if (column != range.toCol) {
					sb.append('\t');
				}
			}
			if (row != range.toRow) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}
}
