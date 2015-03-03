package geogebra.web.util.keyboard;

import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.util.Language;
import geogebra.common.util.Unicode;
import geogebra.html5.main.AppW;
import geogebra.html5.main.DrawEquationWeb;
import geogebra.html5.util.DynamicScriptElement;
import geogebra.html5.util.ScriptLoadCallback;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.view.algebra.RadioButtonTreeItem;
import geogebra.web.util.keyboard.TextFieldProcessing.ArrowType;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * on screen keyboard containing mathematical symbols and formulas
 */
public class OnScreenKeyBoard extends PopupPanel implements ClickHandler {

	/**
	 * all supported locales and the associated keyboardLocal, e.g. en_UK - en,
	 * ca - es, de_AT - de
	 */
	public static HashMap<String, String> supportedLocales = new HashMap<String, String>();
	static {

		// supportedLocales.put(Language.Arabic.localeGWT, "ar");
		// supportedLocales.put(Language.Arabic_Morocco.localeGWT, "ar");
		// supportedLocales.put(Language.Arabic_Tunisia.localeGWT, "ar");
		// supportedLocales.put(Language.Armenian.localeGWT, "hy"); some letters
		// missing
		supportedLocales.put(Language.Basque.localeGWT, "es");
		// supportedLocales.put(Language.Bosnian.localeGWT, "sk"); TODO
		// supportedLocales.put(Language.Bulgarian.localeGWT, "bg");
		supportedLocales.put(Language.Catalan.localeGWT, "es");
		// supportedLocales.put(Language.Chinese_Simplified, value);
		// supportedLocales.put(Language.Chinese_Traditional, value);
		// supportedLocales.put(Language.Croatian.localeGWT, "sk"); TODO
		supportedLocales.put(Language.Czech.localeGWT, "cs");
		supportedLocales.put(Language.Danish.localeGWT, "da");
		supportedLocales.put(Language.Dutch.localeGWT, "en");
		// supportedLocales.put(Language.Dutch_Belgium.localeGWT, value);
		supportedLocales.put(Language.English_Australia.localeGWT, "en");
		supportedLocales.put(Language.English_UK.localeGWT, "en");
		supportedLocales.put(Language.English_US.localeGWT, "en");
		supportedLocales.put(Language.Estonian.localeGWT, "et");
		supportedLocales.put(Language.Filipino.localeGWT, "en");
		supportedLocales.put(Language.Finnish.localeGWT, "fi");
		supportedLocales.put(Language.French.localeGWT, "fr");
		supportedLocales.put(Language.Galician.localeGWT, "es");
		// supportedLocales.put(Language.Georgian.localeGWT, "ka");
		supportedLocales.put(Language.German.localeGWT, "de");
		supportedLocales.put(Language.German_Austria.localeGWT, "de");
		supportedLocales.put(Language.Greek.localeGWT, "en");
		// supportedLocales.put(Language.Hebrew.localeGWT, "iw");
		// supportedLocales.put(Language.Hindi.localeGWT, "hi");
		supportedLocales.put(Language.Hungarian.localeGWT, "hu");
		supportedLocales.put(Language.Icelandic.localeGWT, "is");
		supportedLocales.put(Language.Indonesian.localeGWT, "en");
		supportedLocales.put(Language.Italian.localeGWT, "it");
		// supportedLocales.put(Language.Japanese.localeGWT, value);
		// supportedLocales.put(Language.Kazakh.localeGWT, "kk");
		// supportedLocales.put(Language.Korean.localeGWT, "ko");
		supportedLocales.put(Language.Latvian.localeGWT, "en");
		supportedLocales.put(Language.Lithuanian.localeGWT, "lt");
		// supportedLocales.put(Language.Macedonian.localeGWT, "mk");
		supportedLocales.put(Language.Malay.localeGWT, "ms");
		// supportedLocales.put(Language.Mongolian.localeGWT, "mn");
		// supportedLocales.put(Language.Nepalese.localeGWT, "ne");
		supportedLocales.put(Language.Norwegian_Bokmal.localeGWT, "no");
		supportedLocales.put(Language.Norwegian_Nynorsk.localeGWT, "no");
		// supportedLocales.put(Language.Persian.localeGWT, "fa");
		supportedLocales.put(Language.Polish.localeGWT, "en");
		// supportedLocales.put(Language.Portuguese_Brazil.localeGWT, "pt");
		// TODO
		// supportedLocales.put(Language.Portuguese_Portugal.localeGWT, "pt");
		// TODO
		supportedLocales.put(Language.Romanian.localeGWT, "ro");
		// supportedLocales.put(Language.Russian.localeGWT, "ru");
		// supportedLocales.put(Language.Serbian.localeGWT, "sk"); TODO
		// supportedLocales.put(Language.Sinhala.localeGWT, "si");
		supportedLocales.put(Language.Slovak.localeGWT, "sk");
		// supportedLocales.put(Language.Slovenian.localeGWT, "sk"); TODO
		supportedLocales.put(Language.Spanish.localeGWT, "es");
		supportedLocales.put(Language.Spanish_ES.localeGWT, "es");
		supportedLocales.put(Language.Spanish_UY.localeGWT, "es");
		supportedLocales.put(Language.Swedish.localeGWT, "sv");
		// supportedLocales.put(Language.Tamil.localeGWT, "ta");
		// supportedLocales.put(Language.Thai.localeGWT, "th");
		supportedLocales.put(Language.Turkish.localeGWT, "tr");
		// supportedLocales.put(Language.Ukrainian.localeGWT, "uk");
		// supportedLocales.put(Language.Uyghur.localeGWT, "ug");
		supportedLocales.put(Language.Valencian.localeGWT, "es");
		// supportedLocales.put(Language.Vietnamese.localeGWT, value);
		supportedLocales.put(Language.Welsh.localeGWT, "en");
		// supportedLocales.put(Language.Yiddish.localeGWT, "ji");
	}

	private static OnScreenKeyBoard instance;
	private static final int MIN_WIDTH_WITHOUT_SCALING = 823;
	private static final int MIN_WIDTH_FONT = 485;
	private static final int KEY_PER_ROW = 12;
	private static final int NUM_LETTER_BUTTONS = 38;

	private static final String PI = "\u03C0";
	private static final String BACKSPACE = "\u21A4";
	private static final String ENTER = "\u21B2";
	private static final String SHIFT = "\u21E7";
	private static final String ARROW_LEFT = "\u2190";
	private static final String ARROW_RIGHT = "\u2192";
	private static final String COLON_EQUALS = "\u2254";
	private static final String SPACE = "\u0020";
	private static final String ANGLE = "\u2220";
	private static final String MEASURED_ANGLE = "\u2221";
	private static final String ACCENT_ACUTE = "\u00b4";
	private static final String ACCENT_GRAVE = "\u0060";
	private static final String ACCENT_CARON = "\u02c7";
	private static final String ACCENT_CIRCUMFLEX = "\u005e";
	private static final String GREEK = Unicode.alphaBetaGamma;

	private HorizontalPanel contentNumber = new HorizontalPanel();
	private HorizontalPanel contentSpecialChars = new HorizontalPanel();
	// private FlowPanel contentGreek = new FlowPanel();
	// TODO remove for mobile devices
	private FlowPanel contentLetters = new FlowPanel();
	private Widget textField;
	private TextFieldProcessing processing = new TextFieldProcessing();
	private KeyboardMode mode = KeyboardMode.NUMBER;
	private KeyPanel letters;
	private KeyBoardButton switchABCGreek;
	private static AppW app;
	/**
	 * positioning (via setPopupPosition) needs to be enabled in order to
	 * prevent automatic positioning in the constructor
	 */
	public boolean enablePositioning = false;

	/**
	 * listener for updates of the keyboard structure
	 */
	UpdateKeyBoardListener updateKeyBoardListener;

	private RadioButtonTreeItem resetComponent;

	private boolean shiftIsDown = false;
	private boolean greekActive = false;

	/** language of application */
	String keyboardLocale = "";
	private KeyBoardButton shiftButton;
	private KeyBoardButton backspaceButton;

	/**
	 * creates a keyboard instance
	 * 
	 * @param textField
	 *            the textField to receive the key-events
	 * @param listener
	 *            {@link UpdateKeyBoardListener}
	 * @param appW
	 *            {@link AppW}
	 * @return instance of onScreenKeyBoard
	 */
	public static OnScreenKeyBoard getInstance(Widget textField,
	        UpdateKeyBoardListener listener, AppW appW) {
		app = appW;
		if (instance == null) {
			instance = new OnScreenKeyBoard();
		}

		// set keyboard used to false for the old text field
		setUsed(false);
		instance.setTextField(textField);
		// set keyboard used to true for the new text field
		setUsed(true);

		instance.setListener(listener);
		return instance;
	}

	/**
	 * updates the textField of the current instance, if the instance is not
	 * null
	 * 
	 * @param textField
	 *            the new textField
	 */
	public static void setInstanceTextField(Widget textField) {
		if (instance != null) {
			instance.setTextField(textField);
		}
	}

	/**
	 * set whether the keyboard is used at the moment or not
	 * 
	 * @param used
	 *            whether the keyboard is used or not
	 */
	public static void setUsed(boolean used) {
		if (instance != null && instance.textField != null) {
			instance.processing.setKeyBoardUsed(used
			        && instance.contentNumber.isVisible());
		}
	}

	/**
	 * should not be called; use getInstance instead
	 */
	private OnScreenKeyBoard() {
		super(true);
		addStyleName("KeyBoard");
		createKeyBoard();
		setStyleName();
	}

	@Override
	public void setPopupPosition(int left, int top) {
		if (enablePositioning) {
			super.setPopupPosition(left, top);
		}
	}

	private void createKeyBoard() {
		
		// number - keyboard
		createFunctionsKeyPanel();
		createNumbersKeyPanel();
		createControlKeyPanel();

		// letter - keyboard
		createLettersKeyPanel();

		// special characters - keyboard
		createSpecialCharKeyPanel();

		// // TODO needs to be added for mobile devices
		// KeyBoardMenu menu = new KeyBoardMenu(this);
		FlowPanel p = new FlowPanel();
		// p.add(menu);
		contentNumber.addStyleName("KeyBoardContentNumbers");
		p.add(contentNumber);
		p.add(contentLetters);
		p.add(contentSpecialChars);
		p.add(getCloseButton());

		add(p);

		resetKeyboardState();
		
		Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				setStyleName();
			}
		});
	}

	/**
	 * adds a specific styleName to the keyboard (if keyboard has to be scaled
	 * or not)
	 */
	void setStyleName() {
		if (Window.getClientWidth() < MIN_WIDTH_WITHOUT_SCALING) {
			addStyleName("scale");
			removeStyleName("normal");
			removeStyleName("smallerFont");
			if (Window.getClientWidth() < MIN_WIDTH_FONT) {
				addStyleName("smallerFont");
			}
		} else {
			addStyleName("normal");
			removeStyleName("scale");
			removeStyleName("smallerFont");
		}
	}

	private SimplePanel getCloseButton() {
		Image icon = new Image(GuiResources.INSTANCE.keyboard_close());
		icon.addStyleName("closeIcon");
		icon.getElement().setAttribute("draggable", "false");
		SimplePanel closePanel = new SimplePanel(icon);
		closePanel.addStyleName("keyBoardClosePanel");
		closePanel.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				updateKeyBoardListener.showKeyBoard(false, null);
			}
		}, ClickEvent.getType());
		return closePanel;
	}

	private void createFunctionsKeyPanel() {
		KeyPanel functions = new KeyPanel();
		functions.addStyleName("KeyPanelFunction");

		// fill first row
		int index = 0;
		KeyBoardButton newButton = new KeyBoardButton("x", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("y", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("x" + Unicode.Superscript_2,
		        Unicode.Superscript_2 + "", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.SQUARE_ROOT + "", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("", "x^y", this);
		DrawEquationWeb
		        .drawEquationAlgebraView(newButton.getElement(), "x^{y}");
		functions.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton("( )", "()", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.degree + "", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("<", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(">", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(COLON_EQUALS, COLON_EQUALS, this);
		functions.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton("sin", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("cos", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("tan", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("", Unicode.EULER_STRING + "^", this);
		DrawEquationWeb.drawEquationAlgebraView(newButton.getElement(),
 "e^{x}");
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("|x|", "abs", this);
		functions.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton(KeyboardMode.TEXT.getInternalName(),
		        this, true);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(
		        KeyboardMode.SPECIAL_CHARS.getInternalName(), this, true);
		newButton.addStyleName("switchToSpecialChar");
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(GREEK, this, true);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(PI, this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(",", this);
		functions.addToRow(index, newButton);

		contentNumber.add(functions);
	}

	private void createNumbersKeyPanel() {
		KeyPanel numbers = new KeyPanel();
		numbers.addStyleName("KeyPanelNum");

		// fill first row
		int index = 0;
		KeyBoardButton newButton = new KeyBoardButton("7", this);
		numbers.addToRow(index, newButton);
		newButton = new KeyBoardButton("8", this);
		numbers.addToRow(index, newButton);
		newButton = new KeyBoardButton("9", this);
		numbers.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.divide + "", "/", this);
		numbers.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton("4", this);
		numbers.addToRow(index, newButton);
		newButton = new KeyBoardButton("5", this);
		numbers.addToRow(index, newButton);
		newButton = new KeyBoardButton("6", this);
		numbers.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.multiply + "", "*", this);
		numbers.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton("1", this);
		numbers.addToRow(index, newButton);
		newButton = new KeyBoardButton("2", this);
		numbers.addToRow(index, newButton);
		newButton = new KeyBoardButton("3", this);
		numbers.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.minus + "", this);
		numbers.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton("0", this);
		numbers.addToRow(index, newButton);
		newButton = new KeyBoardButton(".", this);
		numbers.addToRow(index, newButton);
		newButton = new KeyBoardButton("=", this);
		numbers.addToRow(index, newButton);
		newButton = new KeyBoardButton("+", this);
		numbers.addToRow(index, newButton);

		contentNumber.add(numbers);
	}

	private void createControlKeyPanel() {
		contentNumber.add(getControlKeyPanel());
	}

	/**
	 * @return
	 */
    private KeyPanel getControlKeyPanel() {
	    KeyPanel control = new KeyPanel();
		control.addStyleName("KeyPanelControl");

		int index = 0;
		KeyBoardButton newButton = new KeyBoardButton(BACKSPACE, this, true);
		newButton.addStyleName("backspace");
		control.addToRow(index, newButton);

		index++;
		newButton = new KeyBoardButton(ENTER, this, true);
		newButton.addStyleName("enter");
		control.addToRow(index, newButton);

		index++;
		newButton = new KeyBoardButton(ARROW_LEFT, this, true);
		newButton.addStyleName("arrow");
		control.addToRow(index, newButton);
		newButton = new KeyBoardButton(ARROW_RIGHT, this, true);
		newButton.addStyleName("arrow");
		control.addToRow(index, newButton);
	    return control;
    }

	private void createLettersKeyPanel() {
		letters = new KeyPanel();
		letters.addStyleName("KeyPanelLetters");
		KeyBoardButton newButton;

		// create first row
		int index = 0;
		for (int i = 0; i < KEY_PER_ROW; i++) {
			newButton = new KeyBoardButton("", this);
			letters.addToRow(index, newButton);
		}

		// create second row
		index++;
		for (int i = 0; i < KEY_PER_ROW; i++) {
			newButton = new KeyBoardButton("", this);
			letters.addToRow(index, newButton);
		}

		// create third row
		index++;
		// fixed button
		shiftButton = new KeyBoardButton(SHIFT, this, true);
		shiftButton.addStyleName("shift");
		letters.addToRow(index, shiftButton);

		for (int i = 0; i < KEY_PER_ROW - 1; i++) {
			newButton = new KeyBoardButton("", this);
			letters.addToRow(index, newButton);
		}
		// fixed button
		backspaceButton = new KeyBoardButton(BACKSPACE, this, true);
		backspaceButton.addStyleName("delete");
		letters.addToRow(index, backspaceButton);

		// fill forth row - fixed buttons for all languages
		index++;
		newButton = new KeyBoardButton(KeyboardMode.NUMBER.getInternalName(),
		        this, true);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(
		        KeyboardMode.SPECIAL_CHARS.getInternalName(), this, true);
		newButton.addStyleName("switchToSpecialChar");
		letters.addToRow(index, newButton);
		switchABCGreek = new KeyBoardButton(GREEK, this, true);
		letters.addToRow(index, switchABCGreek);
		newButton = new KeyBoardButton(SPACE, this);
		newButton.addStyleName("space");
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(ARROW_LEFT, this, true);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(ARROW_RIGHT, this, true);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(ENTER, this, true);
		letters.addToRow(index, newButton);

		contentLetters.add(letters);
	}

	private void createSpecialCharKeyPanel() {
		contentSpecialChars.addStyleName("KeyBoardContentSpecialChars");
		KeyPanel functions = new KeyPanel();
		functions.addStyleName("KeyPanelFunction");

		// fill first row
		int index = 0;
		KeyBoardButton newButton = new KeyBoardButton("ln", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("log", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("nroot", this);
		functions.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton("sinh", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("cosh", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("tanh", this);
		functions.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton("arcsin", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("arccos", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("arctan", this);
		functions.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton(KeyboardMode.TEXT.getInternalName(),
		        this, true);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(KeyboardMode.NUMBER.getInternalName(),
		        this, true);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(GREEK, this, true);
		functions.addToRow(index, newButton);


		KeyPanel chars = new KeyPanel();
		chars.addStyleName("KeyPanelNum");

		// fill first row
		index = 0;
		newButton = new KeyBoardButton(Unicode.IMAGINARY, this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.Infinity + "", this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(
		        ExpressionNodeConstants.strVECTORPRODUCT, this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(
		        ExpressionNodeConstants.strEQUAL_BOOLEAN, this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(ExpressionNodeConstants.strNOT_EQUAL,
		        this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(ExpressionNodeConstants.strNOT, this);
		chars.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton(ExpressionNodeConstants.strLESS_EQUAL,
		        this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(
		        ExpressionNodeConstants.strGREATER_EQUAL, this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(ExpressionNodeConstants.strAND, this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(ExpressionNodeConstants.strOR, this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(ExpressionNodeConstants.strPARALLEL,
		        this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(
		        ExpressionNodeConstants.strPERPENDICULAR, this);
		chars.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton(ExpressionNodeConstants.strIMPLIES, this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(
		        ExpressionNodeConstants.strIS_ELEMENT_OF, this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(ExpressionNodeConstants.strIS_SUBSET_OF,
		        this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(
		        ExpressionNodeConstants.strIS_SUBSET_OF_STRICT, this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(ANGLE, this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(MEASURED_ANGLE, this);
		chars.addToRow(index, newButton);
		
		// fill next row
		index++;
		newButton = new KeyBoardButton(":", this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton(";", this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton("_", this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton("!", this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton("%", this);
		chars.addToRow(index, newButton);
		newButton = new KeyBoardButton("$", this);
		chars.addToRow(index, newButton);

		contentSpecialChars.add(functions);
		contentSpecialChars.add(chars);
		contentSpecialChars.add(getControlKeyPanel());
	}

	@Override
	public void onClick(ClickEvent event) {
		event.preventDefault();
		event.stopPropagation();
		Object source = event.getSource();
		if (source != null && source instanceof KeyBoardButton) {
			String text = ((KeyBoardButton) source).getText();
			if (text.equals(ACCENT_ACUTE) || text.equals(ACCENT_CARON)
			        || text.equals(ACCENT_GRAVE)
			        || text.equals(ACCENT_CIRCUMFLEX)) {
				processing.onAccent(text);
			} else if (text.equals(BACKSPACE)) {
				processing.onBackSpace();
			} else if (text.equals(ENTER)) {
				// make sure enter is processed correctly
				if (resetComponent != null) {
					resetComponent.resetBlockBlur();
					resetComponent = null;
				}

				processing.onEnter();
				if (processing.resetAfterEnter()) {
					updateKeyBoardListener.showKeyBoard(false, null);
				}
			} else if (((KeyBoardButton) source).isNavigationButton()
					&& text.equals(ARROW_LEFT)) {
				processing.onArrow(ArrowType.left);
			} else if (((KeyBoardButton) source).isNavigationButton()
					&& text.equals(ARROW_RIGHT)) {
				// the same arrow is also used for implication
				processing.onArrow(ArrowType.right);
			} else if (text.equals(SHIFT)) {
				processShift();
			} else if (text.equals(GREEK)) {
				setToGreekLetters();
			} else if (text.equals(KeyboardMode.NUMBER.getInternalName())) {
				setKeyboardMode(KeyboardMode.NUMBER);
			} else if (text.equals(KeyboardMode.TEXT.getInternalName())) {
				if (greekActive) {
					greekActive = false;
					switchABCGreek.setCaption(GREEK, true);
					loadLang(this.keyboardLocale);
				}
				setKeyboardMode(KeyboardMode.TEXT);
			} else if (text
			        .equals(KeyboardMode.SPECIAL_CHARS.getInternalName())) {
				setKeyboardMode(KeyboardMode.SPECIAL_CHARS);
			} else {
				processing.insertString(text);
			}

			if (shiftIsDown && !text.equals(SHIFT)
			        && !text.equals(ACCENT_ACUTE) && !text.equals(ACCENT_CARON)
			        && !text.equals(ACCENT_GRAVE)
			        && !text.equals(ACCENT_CIRCUMFLEX)) {
				processShift();
			}

			if (textField != null) {
				// textField could be null after onEnter()

				// TODO set to false for mobile devices
				processing.setFocus(true);
			}
		}
	}

	private void processShift() {
		shiftIsDown = !shiftIsDown;
		String local = greekActive ? Language.Greek.localeGWT : keyboardLocale;
		if (shiftIsDown) {
			updateKeys("shiftDown", local);
		} else {
			updateKeys("lowerCase", local);
		}
	}

	/**
	 * The text field to be used
	 * 
	 * @param textField
	 *            the text field connected to the keyboard
	 */
	public void setTextField(Widget textField) {
		this.textField = textField;
		this.processing.setField(textField);
	}

	private void setListener(UpdateKeyBoardListener listener) {
		this.updateKeyBoardListener = listener;
	}

	/**
	 * @param mode
	 *            the keyboard mode
	 */
	public void setKeyboardMode(final KeyboardMode mode) {


		// TODO focus events might be needed for mobile devices

		this.mode = mode;
		if (mode == KeyboardMode.NUMBER) {
			processing.setKeyBoardModeText(false);
			contentNumber.setVisible(true);
			contentLetters.setVisible(false);
			contentSpecialChars.setVisible(false);
			updateKeyBoardListener.updateKeyBoard(textField);
		} else if (mode == KeyboardMode.TEXT) {
			greekActive = false;
			contentNumber.setVisible(false);
			contentLetters.setVisible(true);
			contentSpecialChars.setVisible(false);
			updateKeyBoardListener.updateKeyBoard(textField);
			processing.setKeyBoardModeText(true);
			updateKeyBoardListener.showInputField();
		} else if (mode == KeyboardMode.SPECIAL_CHARS) {
			processing.setKeyBoardModeText(false);
			contentNumber.setVisible(false);
			contentLetters.setVisible(false);
			contentSpecialChars.setVisible(true);
			updateKeyBoardListener.updateKeyBoard(textField);
		}
		setUsed(true);
	}

	private void setToGreekLetters() {
		setKeyboardMode(KeyboardMode.TEXT);
		greekActive = true;
		switchABCGreek.setCaption(KeyboardMode.TEXT.getInternalName(), true);
		loadLang(Language.Greek.localeGWT);
	}

	/**
	 * @return the keyboard mode
	 */
	public KeyboardMode getKeyboardMode() {
		return mode;
	}

	/**
	 * @return true iff the systems virtual keyboard should be used
	 */
	public boolean useSystemVirtualKeyboard() {
		return mode == KeyboardMode.TEXT;
	}

	/**
	 * set the keyboard state to the default state
	 */
	public void resetKeyboardState() {
		mode = KeyboardMode.NUMBER;
		contentNumber.setVisible(true);
		contentLetters.setVisible(false);
		contentSpecialChars.setVisible(false);

		if (resetComponent != null) {
			resetComponent.resetBlockBlur();
			resetComponent = null;
		}
	}

	public static void setResetComponent(RadioButtonTreeItem rbti) {
		if (instance != null) {
			instance.resetComponent = rbti;
		}
	}

	/**
	 * loads the javascript file and updates the keys to the given language
	 * 
	 * @param lang
	 *            the language
	 */
	private void loadLang(final String lang) {
		ScriptLoadCallback callback = new ScriptLoadCallback() {

			@Override
			public void onLoad() {
				updateKeys("lowerCase", lang);
			}
		};
		DynamicScriptElement script = (DynamicScriptElement) Document.get()
		        .createScriptElement();
		script.setSrc(GWT.getModuleBaseURL() + "js/keyboard_" + lang
		        + ".js");

		script.addLoadHandler(callback);
		Document.get().getBody().appendChild(script);
	}

	/**
	 * updates the keys to the given language
	 * 
	 * @param updateSection
	 *            "lowerCase" or "shiftDown"
	 * @param language
	 *            String
	 */
	void updateKeys(String updateSection, String language) {
		// update letter keys
		ArrayList<KeyBoardButton> buttons = this.letters.getButtons();
		for (int i = 0; i < NUM_LETTER_BUTTONS; i++) {
			KeyBoardButton button = buttons.get(i);
			if (!button.isNavigationButton()) {
				String newCaption = app.getKey(generateKey(i), updateSection,
				        language);
				if (newCaption.equals("")) {
					button.setVisible(false);
					button.getElement().getParentElement()
					        .addClassName("hidden");
				} else {
					button.setVisible(true);
					button.getElement().getParentElement()
					        .removeClassName("hidden");
					button.setCaption(newCaption, true);
				}
			}
		}
		checkStyle();
	}

	/**
	 * we need smaller buttons (shift and backspace) if the third row has only
	 * one button less than the upper two rows. otherwise the buttons would
	 * overlap.
	 */
	private void checkStyle() {
		int first = countVisibleButtons(this.letters.getRows().get(0));
		int second = countVisibleButtons(this.letters.getRows().get(1));
		int third = countVisibleButtons(this.letters.getRows().get(2));

		if (Math.max(first, second) - third < 1) {
			shiftButton.addStyleName("small");
			backspaceButton.addStyleName("small");
		} else {
			shiftButton.removeStyleName("small");
			backspaceButton.removeStyleName("small");
		}

	}

	/**
	 * @param row
	 *            {@link HorizontalPanel}
	 * @return the number of visible buttons of the given row
	 */
	private static int countVisibleButtons(HorizontalPanel row) {
		int numOfButtons = 0;
		for (int i = 0; i < row.getWidgetCount(); i++) {
			if (row.getWidget(i).isVisible()) {
				numOfButtons++;
			}
		}
		return numOfButtons;
	}

	/**
	 * @param i
	 *            index of button
	 * @return key for the translation-files (keyboard.js).
	 */
	private static String generateKey(int i) {
		if (i < KEY_PER_ROW) {
			return "B0_" + i;
		} else if (i < KEY_PER_ROW + KEY_PER_ROW) {
			return "B1_" + (i - KEY_PER_ROW);
		} else {
			return "B2_" + (i - 1 - KEY_PER_ROW - KEY_PER_ROW);
		}
	}

	/**
	 * loads the translation-files for the active language if it is different
	 * from the last loaded language and sets the {@link #keyboardLocale} to the
	 * new language
	 */
	private void checkLanguage() {
		String locale = app.getLocalization().getLocaleStr();
		String newKeyboardLocale = supportedLocales.get(locale);

		if (newKeyboardLocale != null
		        && keyboardLocale.equals(newKeyboardLocale)) {
			return;
		}
		if (newKeyboardLocale != null) {
			this.keyboardLocale = newKeyboardLocale;
		} else {
			this.keyboardLocale = Language.English_US.localeGWT;
		}
		loadLang(this.keyboardLocale);
	}

	@Override
	public void show() {
		checkLanguage();
		super.show();
	}
}