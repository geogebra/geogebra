package org.geogebra.web.full.gui.components;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.FormatConverterImpl;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.view.algebra.RetexKeyboardListener;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.util.EventUtil;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.web.MathFieldScroller;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * MathField capable editor widget for the web.
 *
 * @author Laszlo
 */
public class MathFieldEditor implements IsWidget, HasKeyboardPopup {

	private static final int PADDING_LEFT = 2;
	private static final int PADDING_TOP = 8;

	private final Kernel kernel;
	private final GeoGebraFrameFull frame;
	private final AppWFull app;
	private FlowPanel main;
	private MathFieldW mathField;
	private MathFieldScroller scroller;
	private RetexKeyboardListener retexListener = null;
	private Canvas canvas;

	/**
	 * Constructor
	 *
	 * @param app
	 *            The application.
	 */
	public MathFieldEditor(App app, MathFieldListener listener) {
		this.app = (AppWFull) app;
		kernel = this.app.getKernel();
		frame = this.app.getAppletFrame();
		createMathField(listener, app.has(Feature.MOW_DIRECT_FORMULA_CONVERSION));
		initEventHandlers();
	}

	private void createMathField(MathFieldListener listener, boolean directFormulaConversion) {
		main = new FlowPanel();
		canvas = Canvas.createIfSupported();
		mathField = new MathFieldW(new FormatConverterImpl(kernel), main,
				canvas, listener,
				directFormulaConversion,
				null);
		scroller = new MathFieldScroller(main);
		main.add(mathField);
	}

	private void initEventHandlers() {
		EventUtil.stopPointer(main.getElement());

		ClickStartHandler.init(main,
				new ClickStartHandler(false, true) {

					@Override
					public void onClickStart(int x, int y,
											 PointerEventType type) {
						editorClicked();
					}
				});
	}

	/**
	 * Called when editor was clicked.
	 */
	protected void editorClicked() {
		requestFocus();
	}

	/**
	 * Focus the editor
	 */
	public void requestFocus() {
		main.getElement().focus();
		app.getGlobalKeyDispatcher().setFocused(true);
		mathField.setFocus(true);
		initAndShowKeyboard();
	}

	/**
	 * Scroll content horizontally if needed.
	 */
	public void scrollHorizontally() {
		scroller.scrollHorizontallyToCursor(PADDING_LEFT);
	}

	/**
	 * Scroll content vertically if needed.
	 */
	public void scrollVertically() {
		scroller.scrollVerticallyToCursor(PADDING_TOP);
	}

	@Override
	public Widget asWidget() {
		return main;
	}

	/**
	 * Sets editor text.
	 * @param text to set.
	 */
	public void setText(String text) {
		mathField.setText(text, false);
	}

	/**
	 * Add style to the editor.
	 *
	 * @param style to add.
	 */
	public void addStyleName(String style) {
		main.addStyleName(style);
	}

	private void initAndShowKeyboard() {
		if (retexListener == null) {
			retexListener = new RetexKeyboardListener(canvas, mathField);
		}

		setKeyboardVisible(true);
	}

	private void setKeyboardVisible(boolean visible) {
		frame.showKeyBoard(visible, retexListener,
				true);
	}
}
