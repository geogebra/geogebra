package org.geogebra.common.spreadsheet.core;

/**
 * Provides copy, paste and cut capability for tabular data.
 * Implementations should handle internal (cell to cell) and external (cell from/to clipboard)
 * operations as well, if both are applicable.
 */
public interface CopyPasteCutTabularData {

	/**
	 * @param source range of cells to copy.
	 */
	void copy(TabularRange source);

	/**
	 * Copy cells ensuring that the content is copied by value (deep copy).
	 *
	 * @param source range of cells to copy.
	 */
	void copyDeep(TabularRange source);

	/**
	 * Paste previously copied content to the destination range of cells
	 * @param destination to paste content to.
	 */
	void paste(TabularRange destination);

	/**
	 * Paste previously copied content to the given row, column
	 * as the start of the destination.
	 *
	 * @param startRow to paste content to.
	 * @param startColumn to paste content to.
	 */
	void paste(int startRow, int startColumn);

	/**
	 * Cuts range of cells.
	 *
	 * @param range of cells to cut.
	 */
	void cut(TabularRange range);

	/**
	 * Selects one or multiple destination ranges to which content was pasted to
	 */
	void selectPastedContent();
}
