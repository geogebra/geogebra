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

package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import org.geogebra.common.spreadsheet.core.Modifiers;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.core.SpreadsheetDelegate;
import org.geogebra.common.spreadsheet.core.SpreadsheetStyleBarModel;
import org.geogebra.common.spreadsheet.core.ViewportAdjusterDelegate;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.common.util.shape.Size;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.web.KeyCodeUtil;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.awt.GGraphics2DW;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.ScrollPanel;

import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.HTMLElement;
import elemental2.dom.KeyboardEvent;
import elemental2.dom.PointerEvent;
import jsinterop.base.Js;

public class SpreadsheetPanel extends FlowPanel implements RequiresResize {

	public static final int AUTOSCROLL_OFFSET = 30;
	private final Spreadsheet spreadsheet;
	private final GGraphics2DW graphics;
	private final AppW app;

	// The canvas itself cannot be wrapped in a scrollpanel,
	// otherwise there is jumping between scroll event and repaint
	// on high-res screens
	private final ScrollPanel scrollOverlay;
	private final MathTextFieldW mathField;
	private final elemental2.dom.Element spreadsheetElement;
	double moveTimeout;
	int viewportChanges;
	boolean isPointerDown = false;
	private FocusCommand focusCommand;

	/**
	 * @param app application
	 */
	public SpreadsheetPanel(AppW app) {
		Canvas spreadsheetWidget = Canvas.createIfSupported();
		spreadsheetWidget.addStyleName("spreadsheetWidget");
		graphics = new GGraphics2DW(spreadsheetWidget);
		this.app = app;
		addStyleName("spreadsheetPanel");

		mathField = new MathTextFieldW(app, new TemplateCatalog());

		spreadsheet = app.getSpreadsheet();
		if (spreadsheet != null) {
			spreadsheet.setControlsDelegate(initControlsDelegate());
			spreadsheet.setSpreadsheetDelegate(initSpreadsheetDelegate());
			spreadsheet.setViewportAdjustmentHandler(createScrollable());
		}

		add(spreadsheetWidget);
		scrollOverlay = new ScrollPanel();

		FlowPanel scrollContent = new FlowPanel();
		scrollOverlay.setWidget(scrollContent);
		scrollOverlay.setStyleName("spreadsheetScrollOverlay");
		add(scrollOverlay);
		spreadsheetElement = Js.uncheckedCast(scrollContent.getElement());

		GlobalHandlerRegistry registry = app.getGlobalHandlers();

		registry.addEventListener(spreadsheetElement, "pointerdown", event -> {
			PointerEvent ptr = Js.uncheckedCast(event);
			Modifiers modifiers = getModifiers(ptr);
			spreadsheet.handlePointerDown(getEventX(ptr), getEventY(ptr),
					modifiers);
			setPointerCapture(event);
			if (modifiers.secondaryButton || spreadsheet.isEditorActive()) {
				event.preventDefault();
			}
			isPointerDown = true;
			repaint();
		});
		registry.addEventListener(spreadsheetElement, "pointerup", event -> {
			PointerEvent ptr = Js.uncheckedCast(event);
			spreadsheet.handlePointerUp(getEventX(ptr), getEventY(ptr),
					getModifiers(ptr));
			if (!spreadsheet.isEditorActive()) {
				app.hideKeyboard();
			}
			isPointerDown = false;
			repaint();
		});
		registry.addEventListener(spreadsheetElement, "pointermove", event -> {
			PointerEvent ptr = Js.uncheckedCast(event);
			double offsetX = getEventX(ptr);
			double offsetY = getEventY(ptr);
			Modifiers modifiers = getModifiers(ptr);
			DomGlobal.clearTimeout(moveTimeout);
			handlePointerMoved(offsetX, offsetY, modifiers);
		});
		registry.addEventListener(DomGlobal.window, "pointerup", event -> {
			elemental2.dom.Element target = Js.uncheckedCast(event.target);
			if (target.closest(".spreadsheetScrollOverlay,.gwt-PopupPanel,.iconButton,"
					+ ".colorChooser,.tabButton") != null) {
				return;
			}
			spreadsheet.clearSelectionOnly();
			if (spreadsheetIsVisible()) {
				repaint();
			}
		});

		ClickStartHandler.initDefaults(scrollContent, false, true);
		scrollContent.getElement().setTabIndex(0);
		scrollContent.addDomHandler(evt -> {
			spreadsheet.handleKeyPressed(KeyCodeUtil.translateGWTCode(
					evt.getNativeKeyCode()).getJavaKeyCode(),
					getKey(evt.getNativeEvent()),
					getKeyboardModifiers(evt));
			evt.stopPropagation(); // do not let global event handler interfere
			evt.preventDefault(); // do not scroll the view
			repaint();
		}, KeyDownEvent.getType());
		updateTotalSize();
		DomGlobal.setInterval((ignore) -> {
			spreadsheet.scrollForDragIfNeeded();
		}, 20);
		scrollOverlay.addScrollHandler(event -> {
			updateViewport();
			repaint();
		});
	}

	private void handlePointerMoved(double offsetX, double offsetY,
			Modifiers modifiers) {
		DomGlobal.clearTimeout(moveTimeout);
		setCursor(spreadsheet.getCursor(offsetX, offsetY));
		viewportChanges = 0;

		spreadsheet.handlePointerMove(offsetX, offsetY,
					modifiers);
		if (isPointerDown) {
			repaint();
		}
	}

	private void setPointerCapture(Event event) {
		HTMLElement target = Js.uncheckedCast(event.target);
		PointerEvent ptr = Js.uncheckedCast(event);
		target.setPointerCapture(ptr.pointerId);
	}

	private String getKey(NativeEvent nativeEvent) {
		String key = Js.<KeyboardEvent>uncheckedCast(nativeEvent).key;
		return key.length() > 1  ? "" : key;
	}

	private SpreadsheetControlsDelegateW initControlsDelegate() {
		return new SpreadsheetControlsDelegateW(app, this, mathField);
	}

	private SpreadsheetDelegate initSpreadsheetDelegate() {
		return this::repaint;
	}

	/**
	 * Focuses and repaints the spreadsheet
	 */
	public void requestFocus() {
		this.focusCommand = new FocusCommand(spreadsheetElement);
		Scheduler.get().scheduleDeferred(focusCommand);
		repaint();
	}

	private Modifiers getKeyboardModifiers(KeyEvent<?> evt) {
		return new Modifiers(evt.isAltKeyDown(),
				NavigatorUtil.isMacOS() ? evt.isMetaKeyDown() : evt.isControlKeyDown(),
				evt.isShiftKeyDown(), false);
	}

	private double getEventX(PointerEvent ptr) {
		return Math.min(ptr.offsetX - scrollOverlay.getElement()
				.getScrollLeft(), scrollOverlay.getOffsetWidth());
	}

	private double getEventY(PointerEvent ptr) {
		return Math.min(ptr.offsetY - scrollOverlay.getElement()
				.getScrollTop(), scrollOverlay.getOffsetHeight());
	}

	private void setCursor(MouseCursor cursor) {
		setStyleName("cursor_resizeEW", cursor == MouseCursor.RESIZE_X);
		setStyleName("cursor_resizeNS", cursor == MouseCursor.RESIZE_Y);
		setStyleName("cursor_default", cursor == MouseCursor.DRAG_DOT);
	}

	private Modifiers getModifiers(PointerEvent ptr) {
		return new Modifiers(ptr.altKey,
				NavigatorUtil.isMacOS() ? ptr.metaKey : ptr.ctrlKey,
				ptr.shiftKey,
				ptr.button == 2 || (NavigatorUtil.isMacOS() && ptr.ctrlKey));
	}

	private void updateTotalSize() {
		double width = spreadsheet.getTotalWidth();
		double height = spreadsheet.getTotalHeight();
		updateTotalSize(width, height);
	}

	private void updateTotalSize(double width, double height) {
		Style style = scrollOverlay.getWidget().getElement().getStyle();
		style.setWidth(width, Unit.PX);
		style.setHeight(height, Unit.PX);
		style.setProperty("maxHeight", height + "px");
		style.setProperty("maxWidth", width + "px");
	}

	@Override
	public void onResize() {
		graphics.setDevicePixelRatio(app.getPixelRatio());
		graphics.setCoordinateSpaceSize(getWidth(), getHeight());
		updateViewport();
		spreadsheet.scrollEditorIntoView();
		repaint();
	}

	private void repaint() {
		DomGlobal.requestAnimationFrame((ignore) -> {
			double ratio = app.getPixelRatio();
			graphics.getContext().setTransform2(ratio, 0, 0, ratio, 0, 0);
			spreadsheet.draw(graphics);
		});
	}

	private void updateViewport() {
		int scrollTop = scrollOverlay.getElement().getScrollTop();
		int scrollLeft = scrollOverlay.getElement().getScrollLeft();
		spreadsheet.setViewport(new Rectangle(scrollLeft, scrollLeft + getWidth(),
				scrollTop, scrollTop + getHeight()));
	}

	private int getHeight() {
		return scrollOverlay.getOffsetHeight();
	}

	private int getWidth() {
		return scrollOverlay.getOffsetWidth();
	}

	/**
	 * @return The width of the scrollbar used for dragging content with the left mouse button
	 */
	private int getScrollBarWidth() {
		return getWidth() - scrollOverlay.getElement().getClientWidth();
	}

	public MathKeyboardListener getKeyboardListener() {
		return mathField.getKeyboardListener();
	}

	private ViewportAdjusterDelegate createScrollable() {
		return new ViewportAdjusterDelegate() {

			@Override
			public void setScrollPosition(double x, double y) {
				scrollOverlay.setHorizontalScrollPosition((int) Math.round(x));
				scrollOverlay.setVerticalScrollPosition((int) Math.round(y));
				viewportChanges++;
			}

			@Override
			public double getScrollBarWidth() {
				return SpreadsheetPanel.this.getScrollBarWidth();
			}

			@Override
			public void updateScrollableContentSize(Size size) {
				updateTotalSize(size.getWidth(), size.getHeight());
			}
		};
	}

	/**
	 * Commit editor changes and hide the editor.
	 */
	public void saveContentAndHideCellEditor() {
		spreadsheet.saveContentAndHideCellEditor();
	}

	/**
	 * Cancel pending focus request.
	 */
	public void cancelFocus() {
		if (focusCommand != null) {
			focusCommand.cancel();
		}
	}

	public SpreadsheetStyleBarModel getStyleBarModel() {
		return spreadsheet.getStyleBarModel();
	}

	public Spreadsheet getSpreadsheet() {
		return spreadsheet;
	}

	private boolean spreadsheetIsVisible() {
		return !getParent().getParent().getElement().hasClassName("tab-hidden");
	}
}
