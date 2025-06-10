package org.geogebra.common.spreadsheet.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.spreadsheet.HasTabularValues;

/**
 * Interacting with the structure and contents of tabular data.
 * @apiNote All indices (e.g., row, column) are 0-based.
 */
public interface TabularData<T> extends HasTabularValues<T> {

	// -- Delegates & Listeners --

	/**
	 * @return cell processor
	 */
	@Nonnull SpreadsheetCellProcessor getCellProcessor();

	/**
	 * @return provider of paste operations
	 */
	@CheckForNull TabularDataPasteInterface<T> getPaste();

	/**
	 * @return utility for pasting data by pointer drag
	 */
	@CheckForNull CellDragPasteHandler getCellDragPasteHandler();

	/**
	 * Add a listener for data update and data dimension changes.
	 * @param listener change listener
	 */
	void addChangeListener(@Nonnull TabularDataChangeListener listener);

	// -- Structure --

	/**
	 * Insert a row at the given index, shifting all subsequent rows down by one.
	 * @param row Index of new row.
	 */
	void insertRowAt(int row);

	/**
	 * Delete the content at the given row index, and shift all subsequent rows up by one.
	 * @param row Index of row to delete.
	 */
	void deleteRowAt(int row);

	/**
	 * Insert a column at the given index, shifting all subsequent columns right by one.
	 * @param column Index of column to add.
	 */
	void insertColumnAt(int column);

	/**
	 * Delete the content at the given cell index, and shift all subsequence columns left by one.
	 * @param column Index of column to delete.
	 */
	void deleteColumnAt(int column);

	/**
	 * Expand the size of the data if necessary (i.e., if the given number of rows or columns
	 * is less than the current size, nothing happens).
	 * @param numberOfRows Minimum number of rows.
	 * @param numberOfColumns Minimum number of columns.
	 */
	default void ensureCapacity(int numberOfRows, int numberOfColumns) {
		int rows = numberOfRows();
		for (int i = rows; i <= numberOfRows; i++) {
			insertRowAt(i);
		}
		int columns = numberOfColumns();
		for (int i = columns; i <= numberOfColumns; i++) {
			insertColumnAt(i);
		}
	}

	/**
	 * Get name of a column.
	 * @param column column index
	 * @return column name
	 */
	@Nonnull String getColumnName(int column);

	/**
	 * Get name of a row.
	 * @param row row index
	 * @return row name
	 */
	default @Nonnull String getRowName(int row) {
		return String.valueOf(row + 1);
	}

	/**
	 * Get a cell name.
	 * @param row row index
	 * @param column column index
	 * @return cell name
	 */
	default @Nonnull String getCellName(int row, int column) {
		return getColumnName(column) + getRowName(row);
	}

	// -- Content --

	/**
	 * Set the content of cell (row, column), replacing any existing content.
	 * Will grow the size of the data (number of rows/columns) if row/column is outside the
	 * current size.
	 * @param row Row index of cell.
	 * @param column Column index of cell.
	 * @param content The content for (row, column). If {@code null}, clears the cell.
	 */
	void setContent(int row, int column, @CheckForNull Object content);

	/**
	 * Replace the content of cell (row, column) with {@code null}.
	 * Will not shrink the size of the data (number of rows/columns) if row/column is the
	 * last cell with content on the right or bottom edge.
	 * @param row Row index of cell.
	 * @param column Column index of cell.
	 */
	void removeContentAt(int row, int column);

	/**
	 * @param row Row index
	 * @param column Column index
	 * @return true if the cell at (row, column) contains a text object (GeoText).
	 */
	boolean isTextContentAt(int row, int column);

	/**
	 * Remove "empty cell" flag.
	 * @param row Row index of cell.
	 * @param column Column index of cell.
	 */
	default void markNonEmpty(int row, int column) {
		// not needed in tests
	}

	/**
	 * Serialize cell content (e.g. for clipboard).
	 * @param row Row index of cell.
	 * @param column Column index of cell.
	 * @return Content of given cell formatted for external use (clipboard), or an empty string
	 * if there is no content at (row, column).
	 */
	@Nonnull String serializeContentAt(int row, int column);

	/**
	 * Check for errors in spreadsheet data.
	 * @param row Row index of cell.
	 * @param column Column index of cell.
	 * @return {@code true} if cell (row, column) currently has an error.
	 */
	boolean hasError(int row, int column);

	/**
	 * @return A generic error message to display for cells with errors.
	 */
	String getErrorString();
}
