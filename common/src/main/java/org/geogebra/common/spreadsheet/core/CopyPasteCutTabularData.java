package org.geogebra.common.spreadsheet.core;

import java.util.function.Consumer;

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
	 * @param externalContent external clipboard content
	 */
	void paste(TabularRange destination, String[][] externalContent);

	/**
	 * Paste previously copied content to the given row, column
	 * as the start of the destination.
	 *
	 * @param startRow to paste content to.
	 * @param startColumn to paste content to.
	 */
	void paste(int startRow, int startColumn, String[][] externalContent);

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

	/**
	 * Provides asynchronous access to clipboard
	 * @param reader gets clipboard content if present, null otherwise
	 */
	void readExternalClipboard(Consumer<String> reader);
}
