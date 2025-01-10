package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.web.html5.event.PointerEvent;
import org.gwtproject.event.dom.client.KeyDownHandler;

public interface SpreadsheetHeader extends KeyDownHandler {

	void onPointerDown(PointerEvent event);

	void onPointerUp(PointerEvent event);

	void onPointerMove(PointerEvent event);

	void showContextMenu(int x, int y, boolean b);

	boolean isResizing();

	void updateSelection(SpreadsheetCoords p);
}
