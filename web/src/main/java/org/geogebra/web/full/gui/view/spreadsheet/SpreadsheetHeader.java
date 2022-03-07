package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.web.html5.event.PointerEvent;

import com.google.gwt.event.dom.client.KeyDownHandler;

public interface SpreadsheetHeader extends KeyDownHandler {

	void onPointerDown(PointerEvent event);

	void onPointerUp(PointerEvent event);

	void onPointerMove(PointerEvent event);

	void showContextMenu(int x, int y, boolean b);

	boolean isResizing();

	void updateSelection(GPoint p);
}
