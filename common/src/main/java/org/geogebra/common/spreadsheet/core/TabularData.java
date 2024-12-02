package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.gui.view.spreadsheet.HasTabularValues;
import org.geogebra.common.spreadsheet.style.CellFormat;

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

	void removeContentAt(int row, int column);

	/**
	 * @param row table row
	 * @param column table column
	 * @return content of given cell formatted for external use (clipboard)
	 */
	String serializeContentAt(int row, int column);

	String getColumnName(int column);

	default String getRowName(int row) {
		return String.valueOf(row + 1);
	}

	void addChangeListener(TabularDataChangeListener listener);

	TabularDataPasteInterface<T> getPaste();

	/**
	 * Checks the capacity of the data and expands it if needed.
	 *
	 * @param rows that needed.
	 * @param cols that needed.
	 */
	default void ensureCapacity(int rows, int cols) {
		int maxRows = numberOfRows();
		for (int i = maxRows; i <= rows; i++) {
			insertRowAt(i);
		}

		int maxColumns = numberOfColumns();
		for (int i = maxColumns; i <= cols; i++) {
			insertColumnAt(i);
		}
	}

	CellFormat getFormat();

	/**
	 * @return The (cell value) alignment for the given cell. One of {@link CellFormat}'s
	 * ALIGN_LEFT, ALIGN_CENTER, or ALIGN_RIGHT.
	 */
	int getAlignment(int row, int column);

	boolean hasError(int row, int column);

	default void setCustomRowAndColumnSizeProvider(CustomRowAndColumnSizeProvider provider) {
		// not needed in tests
	}

	String getErrorString();

	CellDragPasteHandler<T> getCellDragPasteHandler();

}
