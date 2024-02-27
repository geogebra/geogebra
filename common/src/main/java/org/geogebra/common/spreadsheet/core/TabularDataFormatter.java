package org.geogebra.common.spreadsheet.core;

/**
 * Class to convert TabularData to string.
 * Format: colunms are separated by \t and rows are separated by \n .
 */
public final class TabularDataFormatter {

	/**
	 * Converts the range of cell values to a separated string
	 * by tabs between columns and newlines between rows.
	 *
	 * @param range to convert.
	 * @return the string representation.
	 */
	public static String format(TabularData tabularData, TabularRange range) {
		StringBuilder sb = new StringBuilder();
		for (int row = range.getFromRow(); row < range.getToRow() + 1; row++) {
			for (int column = range.getFromColumn(); column < range.getToColumn() + 1; column++) {
				Object value = tabularData.contentAt(row, column);
				sb.append(value);
				if (column != range.getToColumn()) {
					sb.append('\t');
				}
			}
			if (row != range.getToRow()) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}
}
