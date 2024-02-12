package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import org.geogebra.common.spreadsheet.core.Modifiers;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.kernel.GeoElementCellRendererFactory;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.gwtutil.NativePointerEvent;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.GlobalHandlerRegistry;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.KeyDownEvent;
import org.gwtproject.event.dom.client.KeyEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.RequiresResize;
import org.gwtproject.user.client.ui.ScrollPanel;

import com.himamis.retex.editor.share.util.KeyCodes;

import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

public class SpreadsheetPanel extends FlowPanel implements RequiresResize {

	private final Spreadsheet spreadsheet;
	private final GGraphics2DW graphics;
	private final AppW app;

	// The canvas itself cannot be wrapped in a scrollpanel,
	// otherwise there is jumping between scroll event and repaint
	// on high-res screens
	private final ScrollPanel scrollOverlay;
	private final MathTextFieldW mathField;

	/**
	 * @param app application
	 */
	public SpreadsheetPanel(AppW app) {
		Canvas spreadsheetWidget = Canvas.createIfSupported();
		spreadsheetWidget.addStyleName("spreadsheetWidget");
		graphics = new GGraphics2DW(spreadsheetWidget);
		this.app = app;
		addStyleName("spreadsheetPanel");
		KernelTabularDataAdapter tabularData = new KernelTabularDataAdapter(
				app.getSettings().getSpreadsheet());
		app.getKernel().notifyAddAll(tabularData);
		spreadsheet = new Spreadsheet(tabularData, new GeoElementCellRendererFactory(
				new AwtReTexGraphicsBridgeW()));

		app.getKernel().attach(tabularData);
		add(spreadsheetWidget);
		scrollOverlay = new ScrollPanel();
		mathField = new MathTextFieldW(app);
		spreadsheet.setControlsDelegate(new SpreadsheetControlsDelegateW(app, this, mathField));
		FlowPanel scrollContent = new FlowPanel();
		scrollOverlay.setWidget(scrollContent);
		scrollOverlay.setStyleName("spreadsheetScrollOverlay");
		add(scrollOverlay);
		Element spreadsheetElement = scrollContent.getElement();
		GlobalHandlerRegistry registry = app.getGlobalHandlers();
		registry.addEventListener(spreadsheetElement, "pointerdown", event -> {
			NativePointerEvent ptr = Js.uncheckedCast(event);
			spreadsheet.handlePointerDown(getEventX(ptr), getEventY(ptr),
					getModifiers(ptr));
			if (ptr.getButton() == 2) {
				event.preventDefault();
			}
		});
		registry.addEventListener(spreadsheetElement, "pointerup", event -> {
			NativePointerEvent ptr = Js.uncheckedCast(event);
			spreadsheet.handlePointerUp(getEventX(ptr), getEventY(ptr),
					getModifiers(ptr));
			if (!spreadsheet.isEditorActive()) {
				app.hideKeyboard();
			}
		});
		registry.addEventListener(spreadsheetElement, "pointermove", event -> {
			NativePointerEvent ptr = Js.uncheckedCast(event);
			int offsetX = getEventX(ptr);
			int offsetY = getEventY(ptr);
			setCursor(spreadsheet.getCursor(offsetX, offsetY));
			spreadsheet.handlePointerMove(offsetX, offsetY,
					getModifiers(ptr));
		});
		ClickStartHandler.initDefaults(scrollContent, false, true);
		scrollContent.getElement().setTabIndex(0);
		scrollContent.addDomHandler(evt -> {
			spreadsheet.handleKeyPressed(KeyCodes.translateGWTcode(
					evt.getNativeKeyCode()).getJavaKeyCode(),
					Js.asPropertyMap(evt.getNativeEvent()).getAsAny("key").asString(),
					getKeyboardModifiers(evt));
			evt.stopPropagation(); // do not let global event handler interfere
			evt.preventDefault(); // do not scroll the view
		}, KeyDownEvent.getType());
		updateTotalSize();
		DomGlobal.setInterval((ignore) -> {
			repaint();
		}, 200);
		scrollOverlay.addScrollHandler(event -> {
			onScroll();
		});
	}

	private Modifiers getKeyboardModifiers(KeyEvent<?> evt) {
		return new Modifiers(evt.isAltKeyDown(),
				evt.isControlKeyDown(), evt.isShiftKeyDown(), false);
	}

	private int getEventX(NativePointerEvent ptr) {
		return (int) ptr.getOffsetX() - scrollOverlay.getElement()
				.getScrollLeft();
	}

	private int getEventY(NativePointerEvent ptr) {
		return (int) ptr.getOffsetY() - scrollOverlay.getElement()
				.getScrollTop();
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
		Style style = scrollOverlay.getWidget().getElement().getStyle();
		double width = spreadsheet.getTotalWidth();
		double height = spreadsheet.getTotalHeight();
		style.setWidth(width, Unit.PX);
		style.setHeight(height, Unit.PX);
		style.setProperty("maxHeight", height + "px");
		style.setProperty("maxWidth", width + "px");
	}

	@Override
	public void onResize() {
		graphics.setDevicePixelRatio(app.getPixelRatio());
		graphics.setCoordinateSpaceSize(getWidth(), getHeight());
		onScroll();
	}

	private void repaint() {
		graphics.restoreTransform();
		spreadsheet.draw(graphics);
	}

	private void onScroll() {
		int scrollTop = scrollOverlay.getElement().getScrollTop();
		int scrollLeft = scrollOverlay.getElement().getScrollLeft();
		spreadsheet.setViewport(new Rectangle(scrollLeft, scrollLeft + getWidth(),
				scrollTop, scrollTop + getHeight()));
		repaint();
	}

	private int getHeight() {
		return scrollOverlay.getOffsetHeight();
	}

	private int getWidth() {
		return scrollOverlay.getOffsetWidth();
	}

	public MathKeyboardListener getKeyboardListener() {
		return mathField.getKeyboardListener();
	}
}
