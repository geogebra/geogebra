package org.geogebra.web.full.gui.components;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.FormatConverterImpl;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.view.algebra.RetexKeyboardListener;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.util.EventUtil;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.web.MathFieldScroller;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * MathField capable editor widget for the web.
 *
 * @author Laszlo
 */
public class MathFieldEditor implements IsWidget, HasKeyboardPopup, ClickListener, BlurHandler {

	private static final int PADDING_LEFT = 2;
	private static final int PADDING_TOP = 8;

	private final Kernel kernel;
	private final AppWFull app;
	private final GeoGebraFrameFull frame;
	private KeyboardFlowPanel main;
	private MathFieldW mathField;
	private MathFieldScroller scroller;
	private RetexKeyboardListener retexListener;
	private boolean preventBlur;
	private List<BlurHandler> blurHandlers;

	/**
	 * Constructor
	 *
	 * @param app
	 *            The application.
	 * @param listener
	 * 			  listener for the MathField
	 */
	public MathFieldEditor(App app, MathFieldListener listener) {
		this.app = (AppWFull) app;
		kernel = this.app.getKernel();
		this.frame = this.app.getAppletFrame();
		createMathField(listener, app.has(Feature.MOW_DIRECT_FORMULA_CONVERSION));
		initEventHandlers();
	}

	private void createMathField(MathFieldListener listener, boolean directFormulaConversion) {
		main = new KeyboardFlowPanel();
		Canvas canvas = Canvas.createIfSupported();
		mathField = new MathFieldW(new FormatConverterImpl(kernel), main,
				canvas, listener,
				directFormulaConversion,
				null);
		mathField.setClickListener(this);
		mathField.setOnBlur(this);
		mathField.getInputTextArea().getElement().setAttribute("data-test", "mathFieldTextArea");
		scroller = new MathFieldScroller(main);
		main.add(mathField);
		main.getElement().setAttribute("data-test", "mathFieldEditor");
		retexListener = new RetexKeyboardListener(canvas, mathField);
	}

	private void initEventHandlers() {
		blurHandlers = new ArrayList<>();
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

	public void addBlurHandler(BlurHandler handler) {
		blurHandlers.add(handler);
	}

	/**
	 * Called when editor was clicked.
	 */
	private void editorClicked() {
		preventBlur = true;
		requestFocus();
	}

	/**
	 * Focus the editor
	 */
	public void requestFocus() {
		app.getGlobalKeyDispatcher().setFocused(true);
		mathField.requestViewFocus(new Runnable() {
			@Override
			public void run() {
				preventBlur = false;
			}
		});
		setKeyboardVisibility(true);
	}

	public void focus() {
		mathField.setFocus(true);
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
	 *
	 * @return the text of the editor.
	 */
	public String getText() {
		return mathField.getText();
	}

	/**
	 * Sets editor text.
	 * @param text to set.
	 */
	public void setText(String text) {
		mathField.setText(text, false);
	}

	/**
	 * Sets editor font size.
	 * @param fontSize to set.
	 */
	public void setFontSize(double fontSize) {
		mathField.setFontSize(fontSize);
	}

	/**
	 * Add style to the editor.
	 *
	 * @param style to add.
	 */
	public void addStyleName(String style) {
		main.addStyleName(style);
	}


	public void removeStyleName(String hidden) {main.removeStyleName(hidden);}
	/**
	 * @return mathFieldW
	 */
	public MathFieldW getMathField() {
		return mathField;
	}

	@Override
	public void onPointerDown(int x, int y) {
		setKeyboardVisibility(true);
	}

	@Override
	public void onPointerUp(int x, int y) {
		// not used
	}

	@Override
	public void onPointerMove(int x, int y) {
		// not used
	}

	@Override
	public void onLongPress(int x, int y) {
		// not used
	}

	@Override
	public void onScroll(int dx, int dy) {
		// not used
	}

	@Override
	public void onBlur(BlurEvent event) {
		if (preventBlur || !mathField.hasFocus()) {
			return;
		}

		mathField.setFocus(false);

		for (BlurHandler handler: blurHandlers) {
			handler.onBlur(event);
		}
	}

	/**
	 * Shows or hides the keyboard.
	 *
	 * @param show to show or hide the keyboard.
	 */
	public void setKeyboardVisibility(boolean show) {
		if (frame.isKeyboardShowing() == show) {
			return;
		}

		frame.doShowKeyBoard(show, retexListener);
	}

	/**
	 * Stops editing and closes keyboard.
	 */
	public void reset() {
		mathField.setFocus(false);
		setKeyboardVisibility(false);
	}

	/**
	 * Sets background color for the editor.
	 * @param backgroundColor  the color to set.
	 */
	public void setBackgroundColor(GColor backgroundColor) {
		String cssColor = toCssColor(backgroundColor);
		main.getElement().getStyle().setBackgroundColor(cssColor);
		mathField.setBackgroundCssColor(cssColor);
	}

	private static String toCssColor(GColor color) {
		return "#" + StringUtil.toHexString(color);
	}

	public void setForegroundColor(GColor foregroundColor) {
		mathField.setForegroundCssColor(toCssColor(foregroundColor));
	}

	/**
	 *
	 * @return the Style object of the editor.
	 */
	public Style getStyle() {
		return main.getElement().getStyle();
	}
}
