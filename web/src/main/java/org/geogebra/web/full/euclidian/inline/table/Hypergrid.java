package org.geogebra.web.full.euclidian.inline.table;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class Hypergrid {

	/**
	 * edit cell at (x,y) position
	 * @param x coordinate of click
	 * @param y coordinate of click
	 * @return whether the selection was successful
	 */
	@JsOverlay
	public final boolean editAt(int x, int y) {
		try {
			GridCell gridCell = getGridCellFromMousePoint(new Point(x, y));
			editAt(gridCell.cellEvent);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	public native void clearMostRecentSelection();

	private native void editAt(CellEvent e);

	private native GridCell getGridCellFromMousePoint(Point p);
}
