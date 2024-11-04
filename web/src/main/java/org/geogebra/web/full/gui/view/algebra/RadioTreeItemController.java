/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.gui.popup.autocompletion.InputSuggestions;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoElement;
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
import org.gwtproject.core.client.JsArray;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.dom.client.Touch;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.dom.client.ClickHandler;
import org.gwtproject.event.dom.client.DoubleClickEvent;
import org.gwtproject.event.dom.client.DoubleClickHandler;
import org.gwtproject.event.dom.client.MouseDownEvent;
import org.gwtproject.event.dom.client.MouseDownHandler;
import org.gwtproject.event.dom.client.MouseEvent;
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
import org.gwtproject.timer.client.Timer;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

import com.himamis.retex.editor.share.editor.MathField;

/**
 * Controller class of a AV item.
 * 
 * @author laszlo
 *
 */
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
	private int editHeight;

	/** whether blur listener is disabled */
	protected boolean preventBlur = false;

	private final InputSuggestions inputSuggestions;

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
		inputSuggestions = new InputSuggestions(item.geo);
		addDomHandlers(item.main);
	}

	protected boolean checkMarbleHit(MouseEvent<?> evt) {
		PointerEvent wrappedEvent = PointerEvent.wrapEventAbsolute(evt, ZeroOffset.INSTANCE);
		int x = wrappedEvent.getX();
		int y = wrappedEvent.getY();
		boolean rightClick = app.isRightClick(wrappedEvent);
		if (isMarbleHit(x, y)) {
			if (rightClick) {
				onRightClick(evt);
				return false;
			}
			return true;
		}

		return false;
	}

	protected boolean isMarbleHit(int x, int y) {
		boolean marbleHit = item.marblePanel != null && item.marblePanel.isHit(x, y);
		if (marbleHit) {
			setAlgebraViewAsFocusedPanel();
		}
		return marbleHit;
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

		return x > left && x < right && y > top && y < bottom;
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
		onPointerDown(wrappedEvent, event);

		CancelEventTimer.avRestoreWidth();
		if (CancelEventTimer.cancelMouseEvent() || checkMarbleHit(event)
				|| app.isRightClick(wrappedEvent)) {
			return;
		}

		setAlgebraViewAsFocusedPanel();

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
		markForEdit();
		if (!item.isInputTreeItem()) {
			return;
		}

		handleAVItem(event);
		item.updateButtonPanelPosition();
	}

	protected void setAlgebraViewAsFocusedPanel() {
		app.getGuiManager().getLayout().getDockManager().setFocusedPanel(App.VIEW_ALGEBRA);
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
		return !checkMarbleHit(event) && !isWidgetHit(item.controls, event);
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
		if (isMarbleHit(wrappedEvent.getX(), wrappedEvent.getY())) {
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
		CancelEventTimer.touchEventOccured();
	}

	@Override
	public void onTouchStart(TouchStartEvent event) {
		event.stopPropagation();
		if (item.isInputTreeItem()) {
			event.preventDefault();
			getAV().resetItems(false);
		} else {
			RadioTreeItem inputTreeItem = getAV().getInputTreeItem();
			if (inputTreeItem != null) {
				inputTreeItem.getController().stopEdit();
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

		if (isMarbleHit(x, y)) {
			getLongTouchManager().cancelTimer();
		}
		markForEdit();
		if (!item.isInputTreeItem()) {
			return;
		}
		// Do NOT prevent default, kills scrolling on touch
		// event.preventDefault();
		handleAVItem(event);

		onPointerDownMainButton(wrappedEvent);
		CancelEventTimer.touchEventOccured();
	}

	protected void onPointerDown(AbstractEvent event, MouseDownEvent nativeEvt) {
		if (event.isRightClick()) {
			onRightClick(nativeEvt);
			return;
		}

		onPointerDownMainButton(event);
	}

	protected void onPointerDownMainButton(AbstractEvent event) {
		if (checkEditing()) {
			if (!getAV().isEditItem()) {
				// e.g. Web.html might not be in editing mode
				// initially (temporary fix)
				item.ensureEditing();
			}
			showKeyboard();
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
			setEditHeight(item.getEditHeight());
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

	public void showKeyboard() {
		app.showKeyboard(item);
	}

	private void editOnTap(boolean active, MouseEvent<?> event) {
		editOnTap(active,
				PointerEvent.wrapEventAbsolute(event, ZeroOffset.INSTANCE));
	}

	protected boolean editOnTap(boolean active, PointerEvent wrappedEvent) {
		if (!markForEdit) {
			return false;
		}
		ensureMoveMode();
		markForEdit = false;
		boolean enable = true;
		if (item.isSliderItem()
				&& !isWidgetHit(item.getDefinitionValuePanel(), wrappedEvent)) {
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

	private void markForEdit() {
		markForEdit = true;
	}

	private void onRightClick(MouseEvent<?> evt) {
		if (!app.isRightClickEnabledForAV()) {
			return;
		}

		if (checkEditing()) {
			return;
		}

		GeoElement geo = item.geo;
		SelectionManager selection = app.getSelectionManager();
		if (geo != null) {
			if (!selection.containsSelectedGeo(geo)) {
				selection.clearSelectedGeos(false);
				selection.addSelectedGeo(geo, true, true);
			}
			// else: keep (multi)selection, already includes clicked object
			double scale = app.getGeoGebraElement().getScaleX();
			app.getGuiManager().showPopupMenu(
					selection.getSelectedGeos(), item.asWidget(), (int) (evt.getX() / scale),
					(int) (evt.getY() / scale));
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
		ensureMoveMode();
		return handleAVItem(evt.getClientX(), evt.getClientY(),
				evt.getNativeButton() == NativeEvent.BUTTON_RIGHT);
	}

	private void ensureMoveMode() {
		if (!EuclidianConstants.isMoveOrSelectionMode(app.getMode())) {
			app.setMoveMode(ModeSetter.DOCK_PANEL);
		}
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

	public int getEditHeight() {
		return editHeight;
	}

	public void setEditHeight(int editHeight) {
		this.editHeight = editHeight - VERTICAL_PADDING;
	}

	public boolean hasMultiGeosSelected() {
		return selectionCtrl.hasMultGeos();
	}

	/**
	 * When setting to true, all input typed treated as text, so the newly
	 * created item will be GeoText.
	 * 
	 * @param value
	 *            to set.
	 */
	protected void setInputAsText(boolean value) {
		inputSuggestions.setForceAsText(value);
		item.onInputModeChange(value);
	}

	/**
	 * @return if input should be treated as text item.
	 */
	public boolean isInputAsText() {
		return inputSuggestions.isTextInput();
	}

	/**
	 * @param keepFocus
	 *            whether focus should stay
	 */
	public void onEnter(boolean keepFocus) {
		// overridden in subclass
	}

	/**
	 * @param mf the input MathField
	 * @return currently typed command
	 */
	public String getCommand(MathField mf) {
		return inputSuggestions.getCommand(mf);
	}
}
