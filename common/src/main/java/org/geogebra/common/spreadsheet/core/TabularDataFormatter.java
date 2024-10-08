package org.geogebra.common.spreadsheet.core;

/**
 * Class to convert TabularData to string.
 * Format: columns are separated by \t and rows are separated by \n .
 */
public final class TabularDataFormatter<T> {

	private final TabularData<T> data;

	/**
	 * @param data tabular data
	 */
	public TabularDataFormatter(TabularData<T> data) {
		this.data = data;
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
				sb.append(data.serializeContentAt(row, column));
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
