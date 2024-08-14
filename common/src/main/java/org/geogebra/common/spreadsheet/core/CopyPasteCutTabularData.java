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
	 * @param reader gets clipboard content if present and distinct from internal clipboard,
	 *    null otherwise
	 */
	void readExternalClipboard(Consumer<String> reader);

	/**
	 * Range for tiled pasting of tabular data. The size is a multiple of tileHeight vertically
	 * and tileWidth horizontally. At least one copy is created in each direction, i.e.
	 * if destination width < tileWidth, the destination will overflow horizontally,
	 * similar for height.
	 * @param destination destination range
	 * @param tileHeight tile height
	 * @param tileWidth tile tileWidth
	 * @return tiled range
	 */
	static TabularRange getTiledRange(TabularRange destination, int tileHeight, int tileWidth) {
		int minColumn = Math.max(destination.getMinColumn(), 0);
		int minRow = Math.max(destination.getMinRow(), 0);
		int columnMultiplier = Math.max(destination.getWidth() / tileWidth, 1);
		int rowMultiplier = Math.max(destination.getHeight() / tileHeight, 1);
		int maxColumn = minColumn + tileWidth * columnMultiplier - 1;
		int maxRow = minRow + tileHeight * rowMultiplier - 1;
		return new TabularRange(minRow, minColumn, maxRow, maxColumn);
	}

	/**
	 * Range for tiled pasting of tabular data.
	 * @param destination destination
	 * @param pastedData pasted data
	 * @return tiled range
	 */
	static TabularRange getTiledRange(TabularRange destination, String[][] pastedData) {
		return pastedData == null || pastedData.length == 0 || pastedData[0].length == 0 ? null
				: getTiledRange(destination, pastedData.length, pastedData[0].length);
	}
}
