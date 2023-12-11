package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import org.geogebra.common.spreadsheet.core.Modifiers;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.kernel.GeoElementCellRendererFactory;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.gwtutil.NativePointerEvent;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.ScrollPanel;

import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

public class SpreadsheetPanel extends FlowPanel implements RequiresResize {

	private final Canvas spreadsheetWidget;
	private final Spreadsheet spreadsheet;
	private final ScrollPanel scrollableParent;
	private final GGraphics2DW graphics;

	/**
	 * @param app application
	 * @param parent parent tab
	 */
	public SpreadsheetPanel(AppW app, ScrollPanel parent) {
		this.scrollableParent = parent;
		spreadsheetWidget = Canvas.createIfSupported();
		spreadsheetWidget.addStyleName("spreadsheetWidget");
		graphics = new GGraphics2DW(spreadsheetWidget);
		addStyleName("spreadsheetPanel");
		KernelTabularDataAdapter tabularData = new KernelTabularDataAdapter(
				app.getSettings().getSpreadsheet());
		app.getKernel().notifyAddAll(tabularData);
		spreadsheet = new Spreadsheet(tabularData, new GeoElementCellRendererFactory());
		app.getKernel().attach(tabularData);
		add(spreadsheetWidget);
		Element spreadsheetElement = spreadsheetWidget.getElement();
		GlobalHandlerRegistry registry = app.getGlobalHandlers();
		registry.addEventListener(spreadsheetElement, "pointerdown", event -> {
			NativePointerEvent ptr = Js.uncheckedCast(event);
			spreadsheet.handlePointerDown((int) ptr.getOffsetX(), (int) ptr.getOffsetY(),
					getModifiers(ptr));
		});
		registry.addEventListener(spreadsheetElement, "pointerup", event -> {
			NativePointerEvent ptr = Js.uncheckedCast(event);
			spreadsheet.handlePointerUp((int) ptr.getOffsetX(), (int) ptr.getOffsetY(),
					getModifiers(ptr));
		});
		registry.addEventListener(spreadsheetElement, "pointermove", event -> {
			NativePointerEvent ptr = Js.uncheckedCast(event);
			int offsetX = (int) ptr.getOffsetX();
			int offsetY = (int) ptr.getOffsetY();
			setCursor(spreadsheet.getCursor(offsetX, offsetY));
			spreadsheet.handlePointerMove(offsetX, offsetY,
					getModifiers(ptr));
		});
		updateTotalSize();
		DomGlobal.setInterval((ignore) -> {
			repaint();
		}, 200);
		parent.addScrollHandler(event -> {
			onScroll();
		});
	}

	private void setCursor(MouseCursor cursor) {
		setStyleName("cursor_resizeEW", cursor == MouseCursor.RESIZE_X);
		setStyleName("cursor_resizeNS", cursor == MouseCursor.RESIZE_Y);
		setStyleName("cursor_default", cursor == MouseCursor.DRAG_DOT);
	}

	private Modifiers getModifiers(NativePointerEvent ptr) {
		return new Modifiers(ptr.getAltKey(), ptr.getCtrlKey(), ptr.getShiftKey(),
				ptr.getButton() == 2);
	}

	private void updateTotalSize() {
		Style style = getElement().getStyle();
		double width = spreadsheet.getTotalWidth();
		double height = spreadsheet.getTotalHeight();
		style.setWidth(width, Unit.PX);
		style.setHeight(height, Unit.PX);
		style.setProperty("maxHeight", height + "px");
		style.setProperty("maxWidth", width + "px");
	}

	@Override
	public void onResize() {
		spreadsheetWidget.setCoordinateSpaceHeight(getHeight());
		spreadsheetWidget.setCoordinateSpaceWidth(getWidth());
		onScroll();
	}

	private void repaint() {
		graphics.restoreTransform();
		spreadsheet.draw(graphics);
	}

	private void onScroll() {
		Style style = spreadsheetWidget.getElement().getStyle();
		int scrollTop = scrollableParent.getElement().getScrollTop();
		style.setTop(scrollTop, Unit.PX);
		int scrollLeft = scrollableParent.getElement().getScrollLeft();
		style.setLeft(scrollLeft, Unit.PX);
		spreadsheet.setViewport(new Rectangle(scrollLeft, scrollLeft + getWidth(),
				scrollTop, scrollTop + getHeight()));
		repaint();
	}

	private int getHeight() {
		return scrollableParent.getOffsetHeight();
	}

	private int getWidth() {
		return scrollableParent.getOffsetWidth();
	}
}
