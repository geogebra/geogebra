package org.geogebra.web.full.gui.view.spreadsheet;

import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.LongTouchTimer;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseDownHandler;
import org.gwtproject.event.dom.client.MouseMoveEvent;
import org.gwtproject.event.dom.client.MouseMoveHandler;
import org.gwtproject.event.dom.client.MouseUpEvent;
import org.gwtproject.event.dom.client.MouseUpHandler;
import org.gwtproject.event.dom.client.TouchEndEvent;
import org.gwtproject.event.dom.client.TouchEndHandler;
import org.gwtproject.event.dom.client.TouchMoveEvent;
import org.gwtproject.event.dom.client.TouchMoveHandler;
import org.gwtproject.event.dom.client.TouchStartEvent;
import org.gwtproject.event.dom.client.TouchStartHandler;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.ui.Grid;

public class SpreadsheetHeaderController
		implements MouseDownHandler, MouseUpHandler, MouseMoveHandler,
		LongTouchTimer.LongTouchHandler, TouchStartHandler, TouchMoveHandler, TouchEndHandler {

	private final SpreadsheetHeader header;
	private final LongTouchManager longTouchManager;
	private final Grid grid;
	private final MyTableW table;
	protected boolean isMouseDown = false;

	/**
	 * @param header header widget for rows or columns
	 * @param grid header grid (single row/column)
	 * @param table spreadsheet table
	 */
	public SpreadsheetHeaderController(SpreadsheetHeader header,
			Grid grid, MyTableW table) {
		this.header = header;
		this.grid = grid;
		this.table = table;
		longTouchManager = LongTouchManager.getInstance();
	}

	@Override
	public void onMouseDown(MouseDownEvent e) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		e.preventDefault();
		PointerEvent event = PointerEvent.wrapEvent(e, ZeroOffset.INSTANCE);
		onPointerDown(event);
	}

	private void onPointerDown(PointerEvent event) {
		Event.setCapture(grid.getElement());
		isMouseDown = true;
		if (table.getEditor().isEditing()) {
			table.getEditor().setAllowProcessGeo(true);
			table.getEditor().stopCellEditing();
			table.getEditor().setAllowProcessGeo(false);
			table.finishEditing(false);
		}
		header.onPointerDown(event);
	}

	private void onPointerUp(PointerEvent event) {
		Event.releaseCapture(grid.getElement());
		isMouseDown = false;
		boolean rightClick = event.isRightClick();
		AppW app = (AppW) table.getApplication();
		if (rightClick) {
			if (!app.letShowPopupMenu()) {
				return;
			}

			SpreadsheetCoords p = table.getIndexFromPixel(
					SpreadsheetMouseListenerW.getAbsoluteX(event.getWrappedEvent(), app),
					SpreadsheetMouseListenerW.getAbsoluteY(event.getWrappedEvent(), app));

			if (p == null) {
				return;
			}

			// if click is outside current selection then change selection
			if (p.row < table.minSelectionRow
					|| p.row > table.maxSelectionRow
					|| p.column < table.minSelectionColumn
					|| p.column > table.maxSelectionColumn) {
				header.updateSelection(p);
			}

			header.showContextMenu(event.getX(), event.getY(), true);
		}
		header.onPointerUp(event);
	}

	@Override
	public void onMouseUp(MouseUpEvent e) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		e.preventDefault();
		PointerEvent event = PointerEvent.wrapEvent(e, ZeroOffset.INSTANCE);
		onPointerUp(event);
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

	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.preventDefault();
		PointerEvent e = PointerEvent.wrapEvent(event, ZeroOffset.INSTANCE);
		longTouchManager.scheduleTimer(this, e.getX(), e.getY());
		onPointerDown(e);
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		longTouchManager.cancelTimer();
		event.preventDefault();
		PointerEvent e = PointerEvent.wrapEvent(event, ZeroOffset.INSTANCE);
		onPointerUp(e);
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		event.preventDefault();
		PointerEvent e = PointerEvent.wrapEvent(event, ZeroOffset.INSTANCE);
		if (header.isResizing()) {
			// resizing a column cancel long touch
			longTouchManager.cancelTimer();
		} else {
			longTouchManager.rescheduleTimerIfRunning(this, e.getX(), e.getY(),
					false);
		}
		header.onPointerMove(e);
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void handleLongTouch(int x, int y) {
		header.showContextMenu(x, y, false);
	}

}
