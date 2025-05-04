package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.web.html5.event.PointerEvent;
import org.gwtproject.event.dom.client.KeyDownHandler;

/**
 * Classic spreadsheet header for rows or columns.
 */
public interface SpreadsheetHeader extends KeyDownHandler {

	/**
	 * Handle pointer down event.
	 * @param event pointer down event
	 */
	void onPointerDown(PointerEvent event);

	/**
	 * Handle pointer up event.
	 * @param event pointer up event
	 */
	void onPointerUp(PointerEvent event);

	/**
	 * Handle pointer move event.
	 * @param event pointer move event
	 */
	void onPointerMove(PointerEvent event);

	/**
	 * Show the context menu.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param b whether position should be relative
	 */
	void showContextMenu(int x, int y, boolean b);

	/**
	 * @return whether resizing is active
	 */
	boolean isResizing();

	/**
	 * Update selection.
	 * @param selectedCell selected cell
	 */
	void updateSelection(SpreadsheetCoords selectedCell);
}
