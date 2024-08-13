package org.geogebra.common.spreadsheet.core;

/**
 * Class to convert TabularData to string.
 * Format: columns are separated by \t and rows are separated by \n .
 */
public final class TabularDataFormatter<T> {

	private final TabularData<T> data;
	private final SpreadsheetCellDataSerializer serializer;

	/**
	 * @param data tabular data
	 * @param serializer cell serializer
	 */
	public TabularDataFormatter(TabularData<T> data, SpreadsheetCellDataSerializer serializer) {
		this.data = data;
		this.serializer = serializer;
	}

	/**
	 * Converts the range of cell values to a separated string
	 * by tabs between columns and newlines between rows.
	 *
	 * @param range to convert.
	 * @return the string representation.
	 */
	public String toString(TabularRange range) {
		StringBuilder sb = new StringBuilder();
		for (int row = range.getFromRow(); row < range.getToRow() + 1; row++) {
			for (int column = range.getFromColumn(); column < range.getToColumn() + 1; column++) {
				String value = serializer.getStringForEditor(data.contentAt(row, column));
				sb.append(value.startsWith("=") ? value.substring(1) : value);
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
