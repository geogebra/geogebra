/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.gui.layout.panels.AlgebraStyleBarW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.event.PointerEvent;
import org.geogebra.web.html5.event.ZeroOffset;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.LongTouchManager;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.EventUtil;
import org.gwtproject.timer.client.Timer;

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
public class RadioTreeItemController implements ClickHandler,
		DoubleClickHandler, MouseDownHandler, MouseUpHandler, MouseMoveHandler,
		TouchStartHandler, TouchMoveHandler, TouchEndHandler {

	private static final int VERTICAL_PADDING = 20;
	protected AppWFull app;
	RadioTreeItem item;
	private LongTouchManager longTouchManager;
	protected AVSelectionController selectionCtrl;
	protected boolean editing = false;

	private boolean markForEdit = false;
	private long latestTouchEndTime = 0;
	private int editHeigth;
	private boolean inputAsText = false;
	/** whether blur listener is disabled */
	protected boolean preventBlur = false;

	/**
	 * Creates controller for given item.
	 * 
	 * @param item
	 *            AV item
	 */
	public RadioTreeItemController(RadioTreeItem item) {
		this.item = item;
		this.app = item.app;
		selectionCtrl = getAV().getSelectionCtrl();
		addDomHandlers(item.main);
	}

	protected boolean isMarbleHit(MouseEvent<?> evt) {
		return isMarbleHit(
				PointerEvent.wrapEventAbsolute(evt, ZeroOffset.INSTANCE));
	}

	protected boolean isMarbleHit(PointerEvent wrappedEvent) {
		return isMarbleHit(wrappedEvent.getX(), wrappedEvent.getY(),
				app.isRightClick(wrappedEvent));
	}

	protected boolean isMarbleHit(int x, int y, boolean rightClick) {
		if (item.marblePanel != null && item.marblePanel.isHit(x, y)) {
			if (!Browser.isTabletBrowser() && rightClick) {

				onRightClick(x, y);
				return false;
			}
			return true;
		}

		return false;
	}

	protected static boolean isWidgetHit(Widget w, MouseEvent<?> evt) {
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

		// single click starts edit => nothing to do
	}

	private boolean checkEditing() {
		return item.commonEditingCheck();
	}

	public boolean isEditing() {
		return editing;
	}

	protected void setEditing(boolean value) {
		editing = value;
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		event.stopPropagation();

		PointerEvent wrappedEvent = PointerEvent.wrapEventAbsolute(event,
				ZeroOffset.INSTANCE);
		onPointerDown(wrappedEvent);

		CancelEventTimer.avRestoreWidth();
		if (CancelEventTimer.cancelMouseEvent() || isMarbleHit(event)
				|| app.isRightClick(wrappedEvent)) {
			return;
		}

		app.getGuiManager().getLayout().getDockManager().setFocusedPanel(App.VIEW_ALGEBRA);

		if (checkEditing()) {
			// keep focus in editor
			event.preventDefault();
			if (isEditing()) {
				item.adjustCaret(event.getClientX(), event.getClientY());
			}
			if (isEditing() && !item.isInputTreeItem()) {
				return;
			}
		}
		if (!isEditing()) {
			app.closePopups();
		}

		if (markForEdit() && !item.isInputTreeItem()) {
			return;
		}

		handleAVItem(event);
		item.updateButtonPanelPosition();
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}

		event.stopPropagation();
		if (isEditing()) {
			return;
		}

		if (canEditStart(event)) {
			editOnTap(isEditing(), event);
			// MOW-85 move to the very left
			item.adjustCaret(event.getClientX(), event.getClientY());
		}
	}

	/**
	 * Determines if the item can be edited at that point that event has
	 * happened. For example editing is not allowed clicking on marbles.
	 * 
	 * @param event
	 *            The mouse event
	 * @return if editing can start or not.
	 */
	protected boolean canEditStart(MouseEvent<?> event) {
		return !isMarbleHit(event) && !isWidgetHit(item.controls, event);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (CancelEventTimer.cancelMouseEvent()) {
			return;
		}

		if (Browser.isTabletBrowser()) {
			// scroll cancels edit request.
			markForEdit = false;
		}

		event.preventDefault();
	}

	@Override
	public void onTouchEnd(TouchEndEvent event) {
		event.stopPropagation();
		preventBlur();
		if (item.isInputTreeItem()) {
			showKeyboard();
			setFocusDeferred();
			event.preventDefault();
		}

		JsArray<Touch> touches = event.getTargetTouches().length() == 0
				? event.getChangedTouches() : event.getTargetTouches();

		boolean active = isEditing();

		PointerEvent wrappedEvent = PointerEvent.wrapEvent(touches.get(0),
				ZeroOffset.INSTANCE);
		if (isMarbleHit(wrappedEvent)) {
			return;
		}

		if (editOnTap(active, wrappedEvent)) {
			onPointerUp(wrappedEvent);
			CancelEventTimer.touchEventOccured();
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

	private void setFocusDeferred() {
		Scheduler.get().scheduleDeferred(() -> setFocus(true));
	}

	/**
	 * Prevent blur in the next 200ms
	 */
	public void preventBlur() {
		this.preventBlur = true;
		Timer t = new Timer() {

			@Override
			public void run() {
				preventBlur = false;
			}
		};
		t.schedule(200);
	}

	@Override
	public void onTouchMove(TouchMoveEvent event) {
		event.stopPropagation();

		markForEdit = false;

		if (item.isInputTreeItem()) {
			event.preventDefault();
		}
		JsArray<Touch> targets = event.getTargetTouches();
		AbstractEvent wrappedEvent = PointerEvent.wrapEvent(targets.get(0),
				ZeroOffset.INSTANCE);
		onPointerMove(wrappedEvent);
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.stopPropagation();
		if (item.isInputTreeItem()) {
			event.preventDefault();
			getAV().resetItems(false);
		} else {
			if (getAV().getInputTreeItem() != null) {
				getAV().getInputTreeItem().getController().stopEdit();
			}
		}

		AbstractEvent wrappedEvent = PointerEvent.wrapEvent(event,
				ZeroOffset.INSTANCE);

		int x = EventUtil.getTouchOrClickClientX(event);
		int y = EventUtil.getTouchOrClickClientY(event);

		if (isEditing()) {
			item.adjustCaret(x, y);
			return;
		}

		if (isMarbleHit(x, y, false)) {
			getLongTouchManager().cancelTimer();
		}

		if (markForEdit() && !item.isInputTreeItem()) {
			return;
		}
		// Do NOT prevent default, kills scrolling on touch
		// event.preventDefault();
		handleAVItem(event);

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
		updateSelection(event.isControlDown(), event.isShiftDown());
	}

	/**
	 * Inform listeners about editing start
	 * 
	 * @param eventType
	 *            editor event type
	 */
	protected void dispatchEditEvent(EventType eventType) {
		app.dispatchEvent(new Event(eventType, item.getGeo(), null));
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
		if (!EuclidianConstants.isMoveOrSelectionMode(mode)
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
		panel.addDomHandler(this, MouseMoveEvent.getType());
		panel.addDomHandler(this, MouseDownEvent.getType());
		panel.addDomHandler(this, MouseUpEvent.getType());
		panel.addBitlessDomHandler(this, TouchStartEvent.getType());
		panel.addBitlessDomHandler(this, TouchMoveEvent.getType());
		panel.addBitlessDomHandler(this, TouchEndEvent.getType());
	}

	/**
	 * @param ctrl
	 *            whether control was pressed
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
			setEditHeigth(item.getEditHeight());
			getAV().startEditItem(geo);
			Scheduler.get().scheduleDeferred(() -> item.adjustStyleBar());
			showKeyboard();
		}
	}

	/**
	 * Stop editing.
	 */
	public void stopEdit() {
		if (!editing) {
			return;
		}
		item.stopEditing(item.getText(), null, true);
	}

	protected void showKeyboard() {
		item.showKeyboard();
	}

	private void editOnTap(boolean active, MouseEvent<?> event) {
		editOnTap(active,
				PointerEvent.wrapEventAbsolute(event, ZeroOffset.INSTANCE));
	}

	protected boolean editOnTap(boolean active, PointerEvent wrappedEvent) {
		if (!markForEdit) {
			return false;
		}

		markForEdit = false;
		boolean enable = true;
		if ((item.isSliderItem()
				&& !isWidgetHit(item.getDefinitionValuePanel(), wrappedEvent))) {
			enable = false;
			if (active) {
				stopEdit();
			}
		}

		if (enable && (!active || item.isInputTreeItem())) {
			boolean shift = wrappedEvent.isShiftDown();
			boolean ctrl = wrappedEvent.isControlDown();

			longTouchManager.cancelTimer();
			if (!shift && !ctrl) {
				startEdit(false);
			}
			updateSelection(ctrl, shift);
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
		if (markForEdit) {
			return true;
		}
		markForEdit = true;
		return true;
	}

	private void onRightClick(int x, int y) {
		if (!app.isRightClickEnabledForAV()) {
			return;
		}

		if (checkEditing()) {
			return;
		}

		GeoElement geo = item.geo;
		SelectionManager selection = app.getSelectionManager();
		GPoint point = new GPoint(x + Window.getScrollLeft(),
				y + Window.getScrollTop());
		if (geo != null) {
			if (selection.containsSelectedGeo(geo)) {
				// popup menu for current selection
				// (including selected object)
				app.getGuiManager().showPopupMenu(
						selection.getSelectedGeos(), item.getAV(), point);
			} else { // select only this object and popup menu
				selection.clearSelectedGeos(false);
				selection.addSelectedGeo(geo, true, true);
				ArrayList<GeoElement> temp = new ArrayList<>();
				temp.add(geo);

				app.getGuiManager().showPopupMenu(temp,
						item.getAV(), point);
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
				ZeroOffset.INSTANCE);
		onPointerUp(wrappedEvent);
	}

	boolean handleAVItem(MouseEvent<?> evt) {
		return handleAVItem(evt.getClientX(), evt.getClientY(),
				evt.getNativeButton() == NativeEvent.BUTTON_RIGHT);
	}

	protected void handleAVItem(TouchStartEvent evt) {
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
		return false;
	}

	/**
	 * Update selection with this geo.
	 * 
	 * @param separated
	 *            wehther to keep previously selected geos (ctrl pressed)
	 * @param continous
	 *            whether tokeep it continuous (shift pressed)
	 */
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
		item.setFocus(b);
	}

	public AppW getApp() {
		return app;
	}

	/**
	 * Remove edited geo from construction and clear editor.
	 */
	public void removeGeo() {
		item.geo.remove();
		item.setText(""); // make sure the text is not resubmitted on focus lost
		getAV().setActiveTreeItem(null);
	}

	public int getEditHeigth() {
		return editHeigth;
	}

	public void setEditHeigth(int editHeigth) {
		this.editHeigth = editHeigth - VERTICAL_PADDING;
	}

	public boolean hasMultiGeosSelected() {
		return selectionCtrl.hasMultGeos();
	}

	/**
	 * When setting to true, all input typed treated as text, so the newly
	 * created item will be GeoText.
	 * 
	 * used in LatexTreeItemController
	 * 
	 * @param value
	 *            to set.
	 */
	protected void setInputAsText(boolean value) {
		inputAsText = value;
		item.setInputAsText(value);
	}

	public void forceInputAsText() {
		setInputAsText(true);
	}

	/**
	 * @return if input should be treated as text item.
	 */
	public boolean isInputAsText() {
		return inputAsText || (item.geo != null && item.geo.isGeoText()
				&& !((GeoText) item.geo).isTextCommand());
	}

	/**
	 * @param keepFocus
	 *            whether focus should stay
	 * @param createSliders
	 *            whether to create sliders
	 */
	public void onEnter(boolean keepFocus, boolean createSliders) {
		// overridden in subclass
	}
}
