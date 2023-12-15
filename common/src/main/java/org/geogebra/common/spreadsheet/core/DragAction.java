package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.util.MouseCursor;

public class DragAction {
	final MouseCursor activeCursor;
	final int column;
	final int row;

	/**
	 * @param mouseCursor cursor type for desktop devices
	 * @param row drag start row (-1 for header)
	 * @param column drag start column (-1 for header)
	 */
	public DragAction(MouseCursor mouseCursor, int row, int column) {
		this.activeCursor = mouseCursor;
		this.row = row;
		this.column = column;
	}
}
