package org.geogebra.desktop.euclidian;

import java.awt.Cursor;

import org.geogebra.common.util.MouseCursor;

public class CursorMap {

	/**
	 * @param cursor cursor type
	 * @return AWT cursor
	 */
	public static Cursor get(MouseCursor cursor) {
		switch (cursor) {
		case RESIZE_X:
			return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
		case RESIZE_Y:
			return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		default:
			return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		}
	}
}
