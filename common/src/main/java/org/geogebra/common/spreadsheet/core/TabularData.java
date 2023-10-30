package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.gui.view.spreadsheet.HasTabularValues;

/**
 * Interacting with the structure and contents of tabular data.
 */
public interface TabularData<T> extends HasTabularValues<T> {

	// structure
	void reset(int rows, int columns);

	void insertRowAt(int row);

	void deleteRowAt(int row);

	void insertColumnAt(int column);

	void deleteColumnAt(int column);

	// content
	void setContent(int row, int column, Object content);

	String getColumnName(int column);

	void addChangeListener(TabularDataChangeListener listener);

	String getEditableString(int row, int column);

	TabularDataPasteInterface getPaste();

	/**
	 * Checks the capacity of the data and expands it if needed.
	 *
	 * @param rows that needed.
	 * @param cols that needed.
	 */
	default void ensureCapacity(int rows, int cols) {
		int maxRows = numberOfRows();
		if (maxRows < rows + 1) {
			for (int i = maxRows; i <= rows; i++) {
				insertRowAt(maxRows);
			}
		}

		int maxColumns = numberOfColumns();
		if (maxColumns < cols + 1) {
			for (int i = maxColumns; i <= cols; i++) {
				insertColumnAt(maxColumns);
			}
		}
	}
}