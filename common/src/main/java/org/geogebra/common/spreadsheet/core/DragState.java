package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.util.MouseCursor;

/**
 * Represents ongoing drag operation
 */
final class DragState {
	final MouseCursor cursor;
	final int startColumn;
	final int startRow;

	/**
	 * @param cursor cursor type for desktop devices
	 * @param startRow drag start row (-1 for header)
	 * @param startColumn drag start column (-1 for header)
	 */
	DragState(MouseCursor cursor, int startRow, int startColumn) {
		this.cursor = cursor;
		this.startRow = startRow;
		this.startColumn = startColumn;
	}

	boolean isModifyingOperation() {
		return cursor != MouseCursor.DEFAULT;
	}
}
