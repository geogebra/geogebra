package org.geogebra.web.full.gui.components;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.TextRendererSettings;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.util.SyntaxAdapterImplWithPaste;
import org.geogebra.web.full.gui.view.algebra.RetexKeyboardListener;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.accessibility.AccessibleInputBox;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.util.EventUtil;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Style;
import org.gwtproject.event.dom.client.BlurEvent;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.user.client.ui.HasWidgets;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Widget;

import com.himamis.retex.editor.share.editor.UnhandledArrowListener;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * MathField capable editor widget for the web.
 *
 * @author Laszlo
 */
public class MathFieldEditor implements IsWidget, HasKeyboardPopup, BlurHandler {

	private static final int PADDING_TOP = 8;

	private final AppWFull app;
	private final GeoGebraFrameFull frame;
	private KeyboardFlowPanel main;
	private MathFieldW mathField;
	private RetexKeyboardListener retexListener;
	private boolean preventBlur;
	private List<BlurHandler> blurHandlers;
	private String label = "";
	private boolean useKeyboardButton = true;
	private boolean editable = true;
	private String errorText;
	/**
	 * null: not an input box
	 * empty list: input box with no variables
	 * non-empty list: input box with variables
	 */
	private @CheckForNull List<String> inputBoxFunctionVars;

	/**
	 * Constructor
	 *
	 * @param app
	 *            The application.
	 * @param listener
	 *            listener for the MathField
	 */
	public MathFieldEditor(App app, MathFieldListener listener) {
		this(app);
		createMathField(listener, getDefaultModel());
		mathField.getInputTextArea().getElement().setAttribute("data-test", "mathFieldTextArea");
		main.getElement().setAttribute("data-test", "mathFieldEditor");
	}

	protected static MetaModel getDefaultModel() {
		MetaModel model = new MetaModel();
		model.enableSubstitutions();
		model.setForceBracketAfterFunction(true);
		return model;
	}

	/**
	 * @param app application
	 */
	public MathFieldEditor(App app) {
		this.app = (AppWFull) app;
		this.frame = this.app.getAppletFrame();
	}

	protected void createMathField(MathFieldListener listener, MetaModel model) {
		main = new KeyboardFlowPanel();
		Canvas canvas = Canvas.createIfSupported();

		mathField = new MathFieldW(new SyntaxAdapterImplWithPaste(app.getKernel()), main,
				canvas, listener, model, app.getEditorFeatures());
		mathField.setExpressionReader(ScreenReader.getExpressionReader(app));
		mathField.setOnBlur(this);
		updatePixelRatio();
		app.addWindowResizeListener(this::updatePixelRatio);

		if (!main.getStyleName().contains("errorStyle")) {
			getMathField().setBackgroundColor("rgba(255,255,255,0)");
		}
		app.getGlobalHandlers().addEventListener(mathField.asWidget().getElement(),
				"pointerdown", (evt) -> {
			app.sendKeyboardEvent(true);
			setKeyboardVisibility(true);
		});
		main.add(mathField);
		retexListener = new RetexKeyboardListener(canvas, mathField);
		initEventHandlers();
	}

	private void updatePixelRatio() {
		mathField.setPixelRatio(app.getPixelRatio());
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
		mathField.setOnFocus(evt -> main.getParent().addStyleName("focusState"));
	}

	/**
	 * Add a blur handler.
	 * @param handler blur handler
	 */
	public void addBlurHandler(BlurHandler handler) {
		blurHandlers.add(handler);
	}

	/**
	 * Called when editor was clicked.
	 */
	public void editorClicked() {
		if (editable) {
			preventBlur = true;
		}
		requestFocus();
	}

	/**
	 * Focus the editor
	 */
	public void requestFocus() {
		if (editable) {
			if (inputBoxFunctionVars != null) {
				mathField.setInputBoxFunctionVariables(inputBoxFunctionVars);
			}
			mathField.requestViewFocus(() -> preventBlur = false);
			app.sendKeyboardEvent(true);
			setKeyboardVisibility(true);
		}
	}

	/**
	 * Move focus to the editor.
	 */
	public void focus() {
		mathField.setFocus(true);
	}

	/**
	 * Scroll horizontally if needed to bring the cursor into view.
	 */
	public void scrollCursorVisibleHorizontally() {
		mathField.scrollParentHorizontally(main);
	}

	/**
	 * Scroll content vertically if needed.
	 */
	public void scrollVertically() {
		mathField.scrollParentVertically(main, PADDING_TOP);
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
		if (!"?".equals(text)) {
			main.removeStyleName("errorStyle");
		}
		mathField.parse(text);
	}

	/**
	 * Sets editor font size.
	 * @param fontSize to set.
	 */
	public void setFontSize(double fontSize) {
		mathField.setFontSize(fontSize);
	}

	/**
	 * Sets editor font type.
	 * @param fontType to set.
	 */
	public void setFontType(int fontType) {
		mathField.setFontType(fontType);
	}

	/**
	 * Add style to the editor.
	 *
	 * @param style to add.
	 */
	public void addStyleName(String style) {
		main.addStyleName(style);
	}

	/**
	 * Remove style to the editor.
	 *
	 * @param style to remove.
	 */
	public void removeStyleName(String style) {
		main.removeStyleName(style);
	}

	/**
	 * @return mathFieldW
	 */
	public MathFieldW getMathField() {
		return mathField;
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
	 * Shows or hides the keyboard, if user preference allows it
	 *
	 * @param show to show or hide the keyboard.
	 */
	public void setKeyboardVisibility(boolean show) {
		if (!frame.isKeyboardShowing() && !show) {
			return;
		}

		if (useKeyboardButton) {
			frame.showKeyboard(show, retexListener, true);
		} else {
			frame.doShowKeyboard(show, retexListener);
		}
	}

	/**
	 * Shows or hides the keyboard.
	 *
	 * @param show to show or hide the keyboard.
	 */
	public void forceKeyboardVisibility(boolean show) {
		if (!frame.isKeyboardShowing() && !show) {
			return;
		}
		frame.doShowKeyboard(show, retexListener);
	}

	/**
	 * Stops editing and closes keyboard.
	 */
	public void reset() {
		mathField.setFocus(false);
		app.sendKeyboardEvent(false);
		setKeyboardVisibility(false);
	}

	/**
	 *
	 * @return the Style object of the editor.
	 */
	public Style getStyle() {
		return main.getElement().getStyle();
	}

	/**
	 * Add editor to a container
	 *
	 * @param parent to add.
	 */
	public void attach(HasWidgets.ForIsWidget parent) {
		if (!main.isAttached()) {
			parent.add(main);
		} else {
			mathField.rebuild();
		}
	}

	/**
	 * Show or hide.
	 * @param visible whether to show this
	 */
	public void setVisible(boolean visible) {
		main.setVisible(visible);
	}

	public boolean isVisible() {
		return main.isVisible();
	}

	/**
	 * Update screen reader description
	 */
	public void updateAriaLabel() {
		String fullDescription = label;
		if (errorText != null) {
			fullDescription += " " + errorText;
		}
		mathField.setAriaLabel(fullDescription.trim());
	}

	/**
	 * Update the value for screen reader.
	 */
	public void updateAriaValue() {
		mathField.setAriaValue(mathField.getDescription());
	}

	/**
	 * @param label
	 *            editor label
	 */
	public void setLabel(String label) {
		this.label = label;
		updateAriaLabel();
	}

	/**
	 * @return keyboard listener
	 */
	public MathKeyboardListener getKeyboardListener() {
		return retexListener;
	}

	/**
	 * sets whether the editor should use the show keyboard button logic
	 */
	public void setUseKeyboardButton(boolean useKeyboardButton) {
		this.useKeyboardButton = useKeyboardButton;
	}

	/**
	 * Toggle between text and math mode
	 * @param paramTextMode true for text mode
	 */
	public void setTextMode(boolean paramTextMode) {
		mathField.setPlainTextMode(paramTextMode);
	}

	/**
	 * @param error error text or null to reset
	 */
	public void setErrorText(String error) {
		Dom.toggleClass(main, "errorStyle", error != null);
		errorText = error;
	}

	/**
	 * @param editable whether editing is allowed
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
		getMathField().setEnabled(editable);
		Dom.toggleClass(asWidget(), "disabled", !editable);
	}

	protected String getErrorMessage() {
		return AccessibleInputBox.getErrorText(app.getLocalization());
	}

	/**
	 * Remove all listeners
	 */
	public void removeListeners() {
		blurHandlers.clear();
		main.clear();
		main.removeFromParent();
		mathField.asWidget().removeFromParent();
		mathField.setOnBlur(null);
		mathField.setChangeListener(null);
		mathField.getInternal().setFieldListener(null);
		mathField.getInternal().setSyntaxAdapter(null);
		mathField.setExpressionReader(null);
	}

	/**
	 * Sets right margin.
	 * @param rightMargin right margin
	 */
	public void setRightMargin(int rightMargin) {
		mathField.setRightMargin(rightMargin);
	}

	/**
	 * Adjust caret position.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public void adjustCaret(double x, double y) {
		mathField.adjustCaret((int) x, (int) y, 1);
	}

	/**
	 * Select entry at given coordinates.
	 * @param x pixel x-coordinate
	 * @param y pixel y-coordinate
	 */
	public void selectEntryAt(int x, int y) {
		mathField.getInternal().selectEntryAt(x, y);
	}

	/**
	 * Set unhandled arrow listener to the editor.
	 * @param listener unhandled arrow listener
	 */
	public void setUnhandledArrowListener(UnhandledArrowListener listener) {
		mathField.getInternal().setUnhandledArrowListener(listener);
	}

	/**
	 * Apply settings to math input field
	 * @param settings renderer settings
	 */
	public void setTextRendererSettings(TextRendererSettings settings) {
		mathField.setFixMargin(settings.getFixMargin());
		mathField.setMinHeight(settings.getMinHeight());
		mathField.setRightMargin(settings.getRightMargin());
		mathField.setBottomOffset(settings.getBottomOffset());
	}

	public void setInputBoxFunctionVars(List<String> inputBoxFunctionVars) {
		this.inputBoxFunctionVars = inputBoxFunctionVars;
	}
}
