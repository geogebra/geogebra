package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.web.html5.event.PointerEvent;
import org.gwtproject.event.dom.client.KeyDownHandler;

/**
 * Classic spreadsheet header for rows or columns.
 */
public interface SpreadsheetHeader extends KeyDownHandler {

	void onPointerDown(PointerEvent event);

	void onPointerUp(PointerEvent event);

	void onPointerMove(PointerEvent event);

	/**
	 * Show context menu.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param b whether position should be relative
	 */
	void showContextMenu(int x, int y, boolean b);

	boolean isResizing();

	void updateSelection(SpreadsheetCoords p);
}
