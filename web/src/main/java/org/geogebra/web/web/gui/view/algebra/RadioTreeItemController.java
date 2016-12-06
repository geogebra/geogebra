/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.web.web.gui.view.algebra;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.gui.util.LongTouchTimer.LongTouchHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.EventUtil;
import org.geogebra.web.html5.util.sliderPanel.SliderWJquery;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.layout.panels.AlgebraStyleBarW;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Controller class of a AV item.
 * 
 * @author laszlo
 *
 */
@SuppressWarnings("javadoc")
public class RadioTreeItemController
		implements ClickHandler, DoubleClickHandler, MouseDownHandler,
		MouseUpHandler,
		MouseOverHandler,
		MouseMoveHandler,
		MouseOutHandler, TouchStartHandler, TouchMoveHandler, TouchEndHandler,
		LongTouchHandler {

	protected AppW app;
	RadioTreeItem item;
	private LongTouchManager longTouchManager;
	protected AVSelectionController selectionCtrl;

	private boolean markForEdit = false;
	public long latestTouchEndTime = 0;

	public RadioTreeItemController(RadioTreeItem item) {
		this.item = item;
		this.app = item.app;
		selectionCtrl = getAV().getSelectionCtrl();
		addDomHandlers(item.main);
	}

	protected boolean isMarbleHit(MouseEvent<?> evt) {
		if (item.marblePanel != null
				&& item.marblePanel.isHit(evt.getClientX(), evt.getClientY())) {
			return true;
		}

		return false;
	}

	static boolean isWidgetHit(Widget w, MouseEvent<?> evt) {
		return isWidgetHit(w, evt.getClientX(), evt.getClientY());

	}

	static boolean isWidgetHit(Widget w, PointerEvent evt) {
		return isWidgetHit(w, evt.getX(), evt.getY());

	}

	private static boolean isWidgetHit(Widget w, int x, int y) {
		if (w == null) {
			return false;
		}
		int left = w.getAbsoluteLeft();
		int top = w.getAbsoluteTop();
		int right = left + w.getOffsetWidth();
		int bottom = top + w.getOffsetHeight();

		return (x > left && x < right && y > top && y < bottom);
	}

	@Override
	public void onDoubleClick(DoubleClickEvent evt) {
		evt.stopPropagation();

		if (app.has(Feature.AV_SINGLE_TAP_EDIT)) {
			return;
		}

		if (isMarbleHit(evt)) {
			return;
		}

		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		if (checkEditing())
			return;

		startEdit(evt.isControlKeyDown());
	}

	private boolean checkEditing() {
		return item.commonEditingCheck();
	}

	protected boolean isEditing() {
		return item.isEditing();
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		if (item.geo == null) {
			return;
		}

		ToolTipManagerW.sharedInstance()
				.showToolTip(item.geo.getLongDescriptionHTML(true, true));

	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		ToolTipManagerW.sharedInstance().showToolTip(null);
	}


	public void onMouseDown(MouseDownEvent event) {
		app.closePopups();
		event.stopPropagation();

		if (CancelEventTimer.cancelMouseEvent()
				|| isMarbleHit(event)) {
			return;
		}

		if (checkEditing()) {
			// keep focus in editor
			event.preventDefault();
			return;
		}

		if (markForEdit()) {
			return;
		}

		PointerEvent wrappedEvent = PointerEvent.wrapEventAbsolute(event,
				ZeroOffset.instance);
		onPointerDown(wrappedEvent);
		handleAVItem(event);
		item.updateButtonPanelPosition();

	}

	private void updateSelectionByMode(boolean ctrl, boolean shift) {
		int mode = app.getActiveEuclidianView().getMode();

		if (mode == EuclidianConstants.MODE_MOVE
				|| mode == EuclidianConstants.MODE_SELECTION_LISTENER) {

			updateSelection(ctrl, shift);
		}

	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		SliderWJquery.stopSliders();
		event.stopPropagation();
		if (item.isEditing()) {
			return;
		}

		if (app.has(Feature.AV_SINGLE_TAP_EDIT)
				&& !isMarbleHit(event)) {
			editOnTap(item.isEditing(), event);
		}
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (app.has(Feature.AV_SINGLE_TAP_EDIT) && Browser.isTabletBrowser()) {
			// scroll cancels edit request.
			markForEdit = false;
		}

		if (app.has(Feature.AV_SCROLL)) {
			event.preventDefault();
		}
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {

		event.stopPropagation();

		if (item.isInputTreeItem()) {
			// this might cause strange behaviour
			setFocus(true);
		}

		JsArray<Touch> touches = event.getTargetTouches().length() == 0
				? event.getChangedTouches() : event.getTargetTouches();

		boolean active = item.isEditing();

		PointerEvent wrappedEvent = PointerEvent.wrapEvent(touches.get(0),
				ZeroOffset.instance);
		if (editOnTap(active, wrappedEvent)) {
			return;
		}

		long time = System.currentTimeMillis();
		if (time - latestTouchEndTime < 500) {
			// ctrl key, shift key for TouchEndEvent? interesting...
			latestTouchEndTime = time;
			if (!checkEditing()) {
				startEdit(false // event.isControlKeyDown(),
				// event.isShiftKeyDown()
				);
			}
		} else {
			latestTouchEndTime = time;
		}
		getLongTouchManager().cancelTimer();
		onPointerUp(wrappedEvent);
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		event.stopPropagation();
		if (app.has(Feature.AV_SINGLE_TAP_EDIT)) {
			markForEdit = false;
		}
		// event.preventDefault();
		int x = EventUtil.getTouchOrClickClientX(event);
		int y = EventUtil.getTouchOrClickClientY(event);
		getLongTouchManager().rescheduleTimerIfRunning(this, x, y);
		JsArray<Touch> targets = event.getTargetTouches();
		AbstractEvent wrappedEvent = PointerEvent.wrapEvent(targets.get(0),
				ZeroOffset.instance);
		onPointerMove(wrappedEvent);
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.stopPropagation();
		if (item.isEditing()) {
			return;
		}
		if (markForEdit()) {
			return;
		}
		// Do NOT prevent default, kills scrolling on touch
		// event.preventDefault();
		int x = EventUtil.getTouchOrClickClientX(event);
		int y = EventUtil.getTouchOrClickClientY(event);
		getLongTouchManager().scheduleTimer(this, x, y);

		handleAVItem(event);

		AbstractEvent wrappedEvent = PointerEvent.wrapEvent(event,
				ZeroOffset.instance);
		onPointerDown(wrappedEvent);
		CancelEventTimer.touchEventOccured();
	}

	protected void onPointerDown(AbstractEvent event) {
		if (event.isRightClick()) {
			onRightClick(event.getX(), event.getY());
			return;
		}

		if (checkEditing()) {
			if (!getAV().isEditItem()) {
				// e.g. Web.html might not be in editing mode
				// initially (temporary fix)
				item.ensureEditing();
			}
			item.showKeyboard();
			item.removeDummy();
			((PointerEvent) event).getWrappedEvent().stopPropagation();
			if (item.isInputTreeItem()) {
				// put earlier, maybe it freezes afterwards?
				setFocus(true);
			}

		}
		if (app.getActiveEuclidianView()
				.getMode() == EuclidianConstants.MODE_MOVE
				|| app.getActiveEuclidianView()
						.getMode() == EuclidianConstants.MODE_SELECTION_LISTENER) {
			updateSelection(event.isControlDown(), event.isShiftDown());
		}

	}

	/**
	 * 
	 * @param event
	 *            mouse move event
	 */
	protected void onPointerMove(AbstractEvent event) {
		// used to tell EuclidianView to handle mouse over
	}

	protected void onPointerUp(AbstractEvent event) {
		selectionCtrl.setSelectHandled(false);

		GeoElement geo = item.geo;

		if (checkEditing()) {
			if (item.isInputTreeItem()) {
				AlgebraStyleBarW styleBar = getAV().getStyleBar(false);
				if (styleBar != null) {
					styleBar.update(null);
				}
			}
			return;
		}

		// Alt click: copy definition to input field
		if (geo != null && event.isAltDown() && app.showAlgebraInput()) {
			// F3 key: copy definition to input bar
			if (!checkEditing()) {
				startEdit(event.isControlDown());
				return;
			}
		}
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		int mode = ev.getMode();
		if (mode != EuclidianConstants.MODE_MOVE
				&& mode != EuclidianConstants.MODE_SELECTION_LISTENER) {
			// let euclidianView know about the click
			ev.clickedGeo(geo, app.isControlDown(event));
		}
		ev.mouseMovedOver(null);

		// previously av.setFocus, but that scrolls AV and seems not to be
		// necessary
		item.getElement().focus();

		AlgebraStyleBarW styleBar = getAV().getStyleBar(false);

		if (styleBar != null) {
			styleBar.update(geo);
		}

	}

	/**
	 * Adds the needed event handlers to FlowPanel
	 * 
	 * @param panel
	 *            add events to.
	 */
	protected void addDomHandlers(FlowPanel panel) {
		panel.addDomHandler(this, DoubleClickEvent.getType());
		panel.addDomHandler(this, ClickEvent.getType());
		panel.addDomHandler(this, MouseOverEvent.getType());
		panel.addDomHandler(this, MouseOutEvent.getType());
		panel.addDomHandler(this, MouseMoveEvent.getType());
		panel.addDomHandler(this, MouseDownEvent.getType());
		panel.addDomHandler(this, MouseUpEvent.getType());
		panel.addDomHandler(this, TouchStartEvent.getType());
		panel.addDomHandler(this, TouchMoveEvent.getType());
		panel.addDomHandler(this, TouchEndEvent.getType());

	}

	/**
	 * @param ctrl
	 */
	protected void startEdit(boolean ctrl) {
		EuclidianViewInterfaceCommon ev = app.getActiveEuclidianView();
		selectionCtrl.clear();
		ev.resetMode();
		if (!item.hasGeo() || ctrl) {
			return;
		}

		GeoElement geo = item.geo;
		if (!isEditing()) {
			geo.setAnimating(false);
			getAV().startEditItem(geo);

			if (app.has(Feature.AV_INPUT_BUTTON_COVER)
					&& !app.has(Feature.AV_SINGLE_TAP_EDIT)) {
				item.hideControls();
			}

			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				public void execute() {

					item.adjustStyleBar();
				}
			});

			showKeyboard();
			setFocus(true);
		}

	}

	protected void showKeyboard() {
		item.showKeyboard();

	}

	private void editOnTap(boolean active, MouseEvent<?> event) {

		editOnTap(active,
				PointerEvent.wrapEventAbsolute(event, ZeroOffset.instance));
	}

	private boolean editOnTap(boolean active, PointerEvent wrappedEvent) {
		if (!(app.has(Feature.AV_SINGLE_TAP_EDIT) && markForEdit)) {
			return false;
		}

		markForEdit = false;
		boolean enable = true;
		if ((item.isSliderItem()
				&& !isWidgetHit(item.getPlainTextItem(), wrappedEvent))) {
			enable = false;
			if (active) {
				item.stopEditing();
			}

		}

		if (enable && (!active || item.isInputTreeItem())) {
			Log.debug("[AVTAP] single tap edit begins");
			longTouchManager.cancelTimer();
			item.selectItem(true);
			startEdit(wrappedEvent.isControlDown());
			updateSelectionByMode(wrappedEvent.isControlDown(),
					wrappedEvent.isShiftDown());
		}

		return true;

	}

	public LongTouchManager getLongTouchManager() {
		return longTouchManager;
	}

	public void setLongTouchManager(LongTouchManager longTouchManager) {
		this.longTouchManager = longTouchManager;
	}

	private boolean markForEdit() {
		if (app.has(Feature.AV_SINGLE_TAP_EDIT)) {
			if (markForEdit) {
				return true;
			}
			markForEdit = true;
			Log.debug("[AVTAP] single tap is about to start");
			// app.getSelectionManager().clearSelectedGeos();
			getAV().unselectActiveItem();

			return true;
		}

		return false;

	}

	@Override
	public void handleLongTouch(int x, int y) {
		if (app.has(Feature.AV_SINGLE_TAP_EDIT) && item.isEditing()) {
			item.cancelEditing();
			// return;
		}

		onRightClick(x, y);
	}

	private void onRightClick(int x, int y) {
		if (checkEditing())
			return;

		GeoElement geo = item.geo;
		SelectionManager selection = app.getSelectionManager();
		GPoint point = new GPoint(x + Window.getScrollLeft(),
				y + Window.getScrollTop());
		if (geo != null) {
			if (selection.containsSelectedGeo(geo)) {// popup
				// menu for
				// current
				// selection
				// (including
				// selected
				// object)
				((GuiManagerW) app.getGuiManager())
						.showPopupMenu(selection.getSelectedGeos(), item.av,
								point);
			} else {// select only this object and popup menu
				selection.clearSelectedGeos(false);
				selection.addSelectedGeo(geo, true, true);
				ArrayList<GeoElement> temp = new ArrayList<GeoElement>();
				temp.add(geo);

				((GuiManagerW) app.getGuiManager()).showPopupMenu(temp, item.av,
						point);
			}
		}
	}

	@Override
	public void onClick(ClickEvent evt) {
		evt.stopPropagation();
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}
		PointerEvent wrappedEvent = PointerEvent.wrapEvent(evt,
				ZeroOffset.instance);
		onPointerUp(wrappedEvent);
	}

	void handleAVItem(MouseEvent<?> evt) {
		handleAVItem(evt.getClientX(), evt.getClientY(),
				evt.getNativeButton() == NativeEvent.BUTTON_RIGHT);
	}

	private void handleAVItem(TouchStartEvent evt) {
		if (evt.getTouches().length() == 0) {
			return;
		}

		Touch t = evt.getTouches().get(0);
		if (handleAVItem(t.getClientX(), t.getClientY(), false)) {
			evt.preventDefault();
		}
	}

	/**
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param rightClick
	 *            wheher rght click was used
	 */
	protected boolean handleAVItem(int x, int y, boolean rightClick) {

		if (!selectionCtrl.isSelectHandled()
				&& !app.has(Feature.AV_SINGLE_TAP_EDIT)) {
			item.selectItem(true);
		}

		return false;

	}
	public void updateSelection(boolean separated, boolean continous) {
		GeoElement geo = item.geo;
		if (geo == null) {
			selectionCtrl.clear();
			getAV().updateSelection();
		} else {
			selectionCtrl.select(geo, separated, continous);
			if (separated && !selectionCtrl.contains(geo)) {
				selectionCtrl.setSelectHandled(true);
				getAV().selectRow(geo, false);
			} else if (continous) {
				getAV().updateSelection();
			}

		}
	}

	private AlgebraViewW getAV() {
		return item.getAV();
	}

	public void setFocus(boolean b) {
		item.setFocus(b, false);
	}

	public AppW getApp() {
		return app;
	}

}

