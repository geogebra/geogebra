package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.util.MouseCursor;

// TODO naming: Maybe "Action" is not the best name - this looks like a dumb container
//  (for drag operation data?)
final class DragAction {
	final MouseCursor cursor;
	final int column;
	final int row;

	/**
	 * @param cursor cursor type for desktop devices
	 * @param row drag start row (-1 for header)
	 * @param column drag start column (-1 for header)
	 */
	DragAction(MouseCursor cursor, int row, int column) {
		this.cursor = cursor;
		this.row = row;
		this.column = column;
	}
}
