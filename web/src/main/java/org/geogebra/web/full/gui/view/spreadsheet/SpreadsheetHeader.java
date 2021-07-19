package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;

import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;

public interface SpreadsheetHeader extends KeyDownHandler,
		LongTouchHandler, TouchStartHandler, TouchMoveHandler, TouchEndHandler {

	void onPointerDown(PointerEvent event);

	void onPointerUp(PointerEvent event);

	void onPointerMove(PointerEvent event);

}
