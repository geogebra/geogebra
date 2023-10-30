package org.geogebra.common.spreadsheet.core;

/**
 * Class to convert TabularData to string.
 */
public final class TabularContent {
	private final TabularData tabularData;

	public TabularContent(TabularData tabularData) {
		this.tabularData = tabularData;
	}

	/**
	 * Converts the range of cell values to a tab separated string.
	 * The returning format is:
	 *
	 * @param range to convert
	 * @return the tabbed string
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
				sb.append('\t');
			}
		}
		return sb.toString();
	}
}
