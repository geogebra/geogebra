package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.util.CancelEventTimer;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;

public class SpreadsheetHeaderController
		implements MouseDownHandler, MouseUpHandler, MouseMoveHandler {

	private SpreadsheetHeader header;

	public SpreadsheetHeaderController(SpreadsheetHeader header) {
		this.header = header;
	}

	@Override
	public void onMouseDown(MouseDownEvent e) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		e.preventDefault();
		PointerEvent event = PointerEvent.wrapEvent(e, ZeroOffset.INSTANCE);
		header.onPointerDown(event);
	}

	@Override
	public void onMouseUp(MouseUpEvent e) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		e.preventDefault();
		PointerEvent event = PointerEvent.wrapEvent(e, ZeroOffset.INSTANCE);
		header.onPointerUp(event);
	}

	@Override
	public void onMouseMove(MouseMoveEvent e) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		e.preventDefault();
		PointerEvent event = PointerEvent.wrapEvent(e, ZeroOffset.INSTANCE);
		header.onPointerMove(event);
	}

}
