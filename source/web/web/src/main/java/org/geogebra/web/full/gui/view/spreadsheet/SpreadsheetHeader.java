/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
