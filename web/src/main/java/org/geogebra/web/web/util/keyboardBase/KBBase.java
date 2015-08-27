package org.geogebra.web.web.util.keyboardBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.common.util.Language;
import org.geogebra.common.util.Unicode;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.main.LocalizationW;
import org.geogebra.web.html5.util.DynamicScriptElement;
import org.geogebra.web.html5.util.ScriptLoadCallback;
import org.geogebra.web.html5.util.keyboard.UpdateKeyBoardListener;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.util.keyboard.HasKeyboard;
import org.geogebra.web.web.util.keyboard.KeyboardConstants;
import org.geogebra.web.web.util.keyboard.KeyboardMode;
import org.geogebra.web.web.util.keyboardBase.KeyBoardButtonFunctionalBase.Action;
import org.geogebra.web.web.util.keyboardBase.KeyBoardProcessable.ArrowType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * on screen keyboard containing mathematical symbols and formulas
 */
public class KBBase extends PopupPanel {

	private static final int LOWER_HEIGHT = 350;

	private HasKeyboard hasKeyboard;

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
		supportedLocales.put(Language.Bosnian.localeGWT, "sl");
		// supportedLocales.put(Language.Bulgarian.localeGWT, "bg");
		supportedLocales.put(Language.Catalan.localeGWT, "ca");
		// supportedLocales.put(Language.Chinese_Simplified, value);
		// supportedLocales.put(Language.Chinese_Traditional, value);
		supportedLocales.put(Language.Croatian.localeGWT, "sl");
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
		supportedLocales.put(Language.Portuguese_Brazil.localeGWT, "pt");
		supportedLocales.put(Language.Portuguese_Portugal.localeGWT, "pt");
		supportedLocales.put(Language.Romanian.localeGWT, "ro");
		// supportedLocales.put(Language.Russian.localeGWT, "ru");
		supportedLocales.put(Language.Serbian.localeGWT, "sl");
		// supportedLocales.put(Language.Sinhala.localeGWT, "si");
		supportedLocales.put(Language.Slovak.localeGWT, "sk");
		supportedLocales.put(Language.Slovenian.localeGWT, "sl");
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

	/**
	 * minimum width of the whole application to use normal font (small font
	 * otherwise)
	 */
	protected static final int MIN_WIDTH_FONT = 485;

	/**
	 * number of buttons in each horizontal line of buttons on the keyboard
	 */
	protected static final int KEY_PER_ROW = 12;

	/**
	 * number of buttons with one letter on them
	 */
	protected static final int NUM_LETTER_BUTTONS = 38;

	// text of the buttons:
	/**
	 * Greek letters
	 */
	protected static final String GREEK = Unicode.alphaBetaGamma;
	/**
	 * letters of the given language
	 */
	protected static final String TEXT = KeyboardMode.TEXT.getInternalName();
	/**
	 * numbers and basic math
	 */
	protected static final String NUMBER = KeyboardMode.NUMBER
			.getInternalName();
	/**
	 * advanced math
	 */
	protected static final String SPECIAL_CHARS = KeyboardMode.SPECIAL_CHARS
			.getInternalName();

	/**
	 * 
	 */
	protected static final String PAGE_ONE_OF_TWO = "1/2";
	protected static final String PAGE_TWO_OF_TWO = "2/2";

	// images of the buttons:
	private final ImageResource SHIFT = GuiResources.INSTANCE.keyboard_shift();
	private final ImageResource SHIFT_DOWN = GuiResources.INSTANCE
			.keyboard_shiftDown();
	private final ImageResource ENTER = GuiResources.INSTANCE.keyboard_enter();
	private final ImageResource BACKSPACE = GuiResources.INSTANCE
			.keyboard_backspace();
	private final ImageResource ARROW_LEFT = GuiResources.INSTANCE
			.keyboard_arrowLeft();
	private final ImageResource ARROW_RIGHT = GuiResources.INSTANCE
			.keyboard_arrowRight();

	protected HorizontalPanel contentNumber = new HorizontalPanel();
	protected HorizontalPanel contentSpecialChars = new HorizontalPanel();
	protected FlowPanel contentLetters = new FlowPanel();
	public KeyBoardProcessable processField;
	protected KeyboardMode mode = KeyboardMode.NUMBER;
	protected KeyPanelBase letters;
	protected KeyBoardButtonBase switchABCGreek;
	private int numVisibleButtons;

	/**
	 * application that is used
	 */
	protected App app;

	protected boolean accentDown = false;
	private KeyBoardButtonBase accentButton;

	/** contains the unicode string for the specific letter with acute accent */
	private HashMap<String, String> accentAcute = new HashMap<String, String>();

	/** contains the unicode string for the specific letter with grave accent */
	private HashMap<String, String> accentGrave = new HashMap<String, String>();

	/** contains the unicode string for the specific letter with caron accent */
	private HashMap<String, String> accentCaron = new HashMap<String, String>();

	/** contains the unicode string for the specific letter with circumflex */
	private HashMap<String, String> accentCircumflex = new HashMap<String, String>();

	/**
	 * positioning (via setPopupPosition) needs to be enabled in order to
	 * prevent automatic positioning in the constructor
	 */
	public boolean enablePositioning = false;

	/**
	 * listener for updates of the keyboard structure
	 */
	public UpdateKeyBoardListener updateKeyBoardListener;

	protected boolean shiftIsDown = false;
	protected boolean greekActive = false;
	protected boolean keyboardWanted = false;

	/** language of application */
	protected String keyboardLocale = "";
	private KeyBoardButtonFunctionalBase shiftButton;
	private KeyBoardButtonBase backspaceButton;
	protected LocalizationW loc;

	/**
	 * buttons that need to be updated when the language is changed and their
	 * default label (which can be found in loc.getPlain)
	 */
	protected HashMap<KeyBoardButtonBase, String> updateButton = new HashMap<KeyBoardButtonBase, String>();

	protected KeyPanelBase firstPageChars;
	protected KeyPanelBase secondPageChars;
	protected SimplePanel specialCharContainer;

	private boolean isSmallKeyboard = false;

	protected void initAccentAcuteLetters() {
		accentAcute.put("a", "\u00e1");
		accentAcute.put("A", "\u00c1");
		accentAcute.put("e", "\u00e9");
		accentAcute.put("E", "\u00C9");
		accentAcute.put("i", "\u00ed");
		accentAcute.put("I", "\u00cd");
		accentAcute.put("l", "\u013A");
		accentAcute.put("L", "\u0139");
		accentAcute.put("o", "\u00f3");
		accentAcute.put("O", "\u00d3");
		accentAcute.put("r", "\u0155");
		accentAcute.put("R", "\u0154");
		accentAcute.put("u", "\u00fa");
		accentAcute.put("U", "\u00da");
		accentAcute.put("y", "\u00fd");
		accentAcute.put("Y", "\u00dd");
	}

	protected void initAccentGraveLetters() {
		accentGrave.put("a", "\u00e0");
		accentGrave.put("A", "\u00c0");
		accentGrave.put("e", "\u00e8");
		accentGrave.put("E", "\u00C8");
		accentGrave.put("i", "\u00ec");
		accentGrave.put("I", "\u00cc");
		accentGrave.put("o", "\u00f2");
		accentGrave.put("O", "\u00d2");
		accentGrave.put("u", "\u00f9");
		accentGrave.put("U", "\u00d9");
	}

	protected void initAccentCaronLetters() {
		accentCaron.put("c", "\u010d");
		accentCaron.put("C", "\u010c");
		accentCaron.put("d", "\u010F");
		accentCaron.put("D", "\u010e");
		accentCaron.put("e", "\u011b");
		accentCaron.put("E", "\u011A");
		accentCaron.put("l", "\u013E");
		accentCaron.put("L", "\u013D");
		accentCaron.put("n", "\u0148");
		accentCaron.put("N", "\u0147");
		accentCaron.put("r", "\u0159");
		accentCaron.put("R", "\u0158");
		accentCaron.put("s", "\u0161");
		accentCaron.put("S", "\u0160");
		accentCaron.put("t", "\u0165");
		accentCaron.put("T", "\u0164");
		accentCaron.put("z", "\u017e");
		accentCaron.put("Z", "\u017d");
	}

	protected void initAccentCircumflexLetters() {
		accentCircumflex.put("a", "\u00e2");
		accentCircumflex.put("A", "\u00c2");
		accentCircumflex.put("e", "\u00ea");
		accentCircumflex.put("E", "\u00Ca");
		accentCircumflex.put("i", "\u00ee");
		accentCircumflex.put("I", "\u00ce");
		accentCircumflex.put("o", "\u00f4");
		accentCircumflex.put("O", "\u00d4");
		accentCircumflex.put("u", "\u00fb");
		accentCircumflex.put("U", "\u00db");
	}

	/**
	 * should not be called; use getInstance instead
	 * 
	 * @param autoHide
	 *            whether or not the popup should be automatically hidden when
	 *            the user clicks outside of it or the history token changes.
	 */
	public KBBase(boolean autoHide) {
		super(autoHide);
	}

	@Override
	public void setPopupPosition(int left, int top) {
		if (enablePositioning) {
			super.setPopupPosition(left, top);
		}
	}

	protected void createKeyBoard() {
		this.updateButton = new HashMap<KeyBoardButtonBase, String>();

		// number - keyboard
		createFunctionsKeyPanel();
		createNumbersKeyPanel();
		createControlKeyPanel();

		// letter - keyboard
		createLettersKeyPanel();

		// special characters - keyboard
		createSpecialCharKeyPanel();

		FlowPanel p = new FlowPanel();
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
				updateSize();
				setStyleName();
			}
		});
	}

	/**
	 * sets width of the onScreenKeyboard and adds specific styleNames if
	 * keyboard needs to be scaled
	 */
	public void updateSize() {
		// -10 because of padding, -2 for applet border
		this.setWidth(app.getWidth() - 12 + "px");

		if (app.getHeight() <= LOWER_HEIGHT && !isSmallKeyboard) {
			this.addStyleName("lowerHeight");
			updateHeight();
			this.isSmallKeyboard = !this.isSmallKeyboard;
		} else if (app.getHeight() > LOWER_HEIGHT && isSmallKeyboard) {
			this.removeStyleName("lowerHeight");
			updateHeight();
			this.isSmallKeyboard = !this.isSmallKeyboard;
		}
	}

	private void updateHeight() {
		if (hasKeyboard != null) {
			hasKeyboard.updateKeyboardHeight();
		}
	}

	/**
	 * adds a specific styleName to the keyboard (if keyboard has to be scaled
	 * or not)
	 */
	public void setStyleName() {
		if (app.getWidth() < getMinWidthWithoutScaling()) {
			addStyleName("scale");
			removeStyleName("normal");
			removeStyleName("smallerFont");
			if (app.getWidth() < MIN_WIDTH_FONT) {
				addStyleName("smallerFont");
			}
		} else {
			addStyleName("normal");
			removeStyleName("scale");
			removeStyleName("smallerFont");
		}
	}

	/**
	 * check the minimum width. Either width of ABC panel or 123 panel. 70 =
	 * width of button; 82 = padding
	 * 
	 * @return
	 */
	private int getMinWidthWithoutScaling() {
		int abc = numVisibleButtons * 70 + 82;
		int numbers = 850;
		return Math.max(abc, numbers);
	}

	protected SimplePanel getCloseButton() {
		NoDragImage image = new NoDragImage(GuiResources.INSTANCE
				.keyboard_close().getSafeUri().asString());
		image.addStyleName("closeIcon");
		SimplePanel closePanel = new SimplePanel(image);
		closePanel.addStyleName("keyBoardClosePanel");
		closePanel.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				keyboardWanted = false;
				updateKeyBoardListener.keyBoardNeeded(false, null);
			}
		}, ClickEvent.getType());
		return closePanel;
	}

	protected void createFunctionsKeyPanel() {
		KeyPanelBase functions = new KeyPanelBase();
		functions.addStyleName("KeyPanelFunction");

		// fill first row
		int index = 0;
		addButton("x", index, functions);
		addButton("y", index, functions);
		addButton("a^2", Unicode.Superscript_2 + "", index, functions)
				.addStyleName("supScript");
		addButton(Unicode.SQUARE_ROOT + "", index, functions);

		// fill next row
		index++;
		addButton("a^x", KeyboardConstants.A_POWER_X, index, functions)
				.addStyleName("supScript");
		addButton("|x|", "abs", index, functions);
		addButton(Unicode.DEGREE, index, functions);
		addButton(Unicode.PI_STRING, index, functions);

		// fill next row
		index++;
		addButton("(", index, functions);
		addButton(")", index, functions);
		addButton("<", index, functions);
		addButton(">", index, functions);

		// fill next row
		index++;
		addFunctionalButton(index, functions, TEXT, Action.SWITCH_KEYBOARD)
				.addStyleName("switchKeyboard");
		addFunctionalButton(index, functions, SPECIAL_CHARS,
				Action.SWITCH_KEYBOARD).addStyleName("switchKeyboard");
		addFunctionalButton(index, functions, GREEK, Action.SWITCH_KEYBOARD)
				.addStyleName("switchKeyboard");
		addButton(",", index, functions);

		contentNumber.add(functions);
	}

	protected void createNumbersKeyPanel() {
		KeyPanelBase numbers = new KeyPanelBase();
		numbers.addStyleName("KeyPanelNum");

		// fill first row
		int index = 0;
		addButton("7", index, numbers);
		addButton("8", index, numbers);
		addButton("9", index, numbers);

		// addButton(Unicode.DIVIDE, "/", index, numbers);

		// better for MathQuill to know this syntax as well:
		addButton(Unicode.DIVIDE, Unicode.DIVIDE, index, numbers);

		// fill next row
		index++;
		addButton("4", index, numbers);
		addButton("5", index, numbers);
		addButton("6", index, numbers);

		// addButton(Unicode.MULTIPLY + "", "*", index, numbers);

		// better for MathQuill to know this syntax as well:
		addButton(Unicode.MULTIPLY + "", Unicode.MULTIPLY + "", index, numbers);

		// fill next row
		index++;
		addButton("1", index, numbers);
		addButton("2", index, numbers);
		addButton("3", index, numbers);
		addButton(Unicode.MINUS + "", index, numbers);

		// fill next row
		index++;
		addButton("0", index, numbers);
		addButton(".", index, numbers);
		addButton("=", index, numbers);
		addButton("+", index, numbers);

		contentNumber.add(numbers);
	}

	protected void createControlKeyPanel() {
		contentNumber.add(getControlKeyPanel());
	}

	/**
	 * @return
	 */
	protected KeyPanelBase getControlKeyPanel() {
		KeyPanelBase control = new KeyPanelBase();
		control.addStyleName("KeyPanelControl");

		int index = 0;
		addFunctionalButton(BACKSPACE, Action.BACKSPACE, index, control)
				.addStyleName("backspace");

		index++;
		addFunctionalButton(ENTER, Action.ENTER, index, control).addStyleName(
				"enter");

		index++;
		addFunctionalButton(ARROW_LEFT, Action.ARROW_LEFT, index, control)
				.addStyleName("arrow");
		addFunctionalButton(ARROW_RIGHT, Action.ARROW_RIGHT, index, control)
				.addStyleName("arrow");

		return control;
	}

	protected void createLettersKeyPanel() {
		contentLetters.addStyleName("contentLetters");
		letters = new KeyPanelBase();
		letters.addStyleName("KeyPanelLetters");

		// create first row
		int index = 0;
		for (int i = 0; i < KEY_PER_ROW; i++) {
			addButton("", index, letters);
		}

		// create second row
		index++;
		for (int i = 0; i < KEY_PER_ROW; i++) {
			addButton("", index, letters);
		}

		// create third row
		index++;
		shiftButton = addFunctionalButton(SHIFT, Action.SHIFT, index, letters);
		shiftButton.addStyleName("shift");

		for (int i = 0; i < KEY_PER_ROW - 1; i++) {
			addButton("", index, letters);
		}
		backspaceButton = addFunctionalButton(BACKSPACE, Action.BACKSPACE,
				index, letters);
		backspaceButton.addStyleName("delete");

		// fill forth row - fixed buttons for all languages
		index++;
		addFunctionalButton(index, letters, NUMBER, Action.SWITCH_KEYBOARD)
				.addStyleName("switchKeyboard");
		addFunctionalButton(index, letters, SPECIAL_CHARS,
				Action.SWITCH_KEYBOARD).addStyleName("switchKeyboard");
		switchABCGreek = addFunctionalButton(index, letters, GREEK,
				Action.SWITCH_KEYBOARD);
		switchABCGreek.addStyleName("switchKeyboard");
		addButton(" ", index, letters).addStyleName("space");
		addFunctionalButton(ARROW_LEFT, Action.ARROW_LEFT, index, letters);
		addFunctionalButton(ARROW_RIGHT, Action.ARROW_RIGHT, index, letters);
		addFunctionalButton(ENTER, Action.ENTER, index, letters);

		contentLetters.add(letters);
	}

	protected void createSpecialCharKeyPanel() {
		contentSpecialChars.addStyleName("KeyBoardContentSpecialChars");
		KeyPanelBase functions = new KeyPanelBase();
		functions.addStyleName("KeyPanelFunction");

		// fill first row
		int index = 0;
		updateButton.put(
				addButton(loc.getPlain("Function.sin"), index, functions),
				"Function.sin");
		updateButton.put(
				addButton(loc.getPlain("Function.cos"), index, functions),
				"Function.cos");
		updateButton.put(
				addButton(loc.getPlain("Function.tan"), index, functions),
				"Function.tan");
		addButton("e^x", Unicode.EULER_STRING + "^", index, functions)
				.addStyleName("supScript");

		// fill second row
		index++;
		KeyBoardButtonBase button = addButton(loc.getPlain("Function.sin")
				+ "^-1", "arcsin", index, functions);
		button.addStyleName("supScript");
		updateButton.put(button, "Function.sin" + "^-1");

		button = addButton(loc.getPlain("Function.cos") + "^-1", "arccos",
				index, functions);
		button.addStyleName("supScript");
		updateButton.put(button, "Function.cos" + "^-1");

		button = addButton(loc.getPlain("Function.tan") + "^-1", "arctan",
				index, functions);
		button.addStyleName("supScript");
		updateButton.put(button, "Function.tan" + "^-1");
		addButton("ln", index, functions);

		// fill third row
		index++;
		updateButton.put(
				addButton(loc.getPlain("Function.sinh"), "sinh", index,
						functions), "Function.sinh");
		updateButton.put(
				addButton(loc.getPlain("Function.cosh"), "cosh", index,
						functions), "Function.cosh");
		updateButton.put(
				addButton(loc.getPlain("Function.tanh"), "tanh", index,
						functions), "Function.tanh");
		addButton("log_10", "log", index, functions);

		// fill forth row
		index++;
		addFunctionalButton(index, functions, TEXT, Action.SWITCH_KEYBOARD)
				.addStyleName("switchKeyboard");
		addFunctionalButton(index, functions, NUMBER, Action.SWITCH_KEYBOARD)
				.addStyleName("switchKeyboard");
		addFunctionalButton(index, functions, GREEK, Action.SWITCH_KEYBOARD)
				.addStyleName("switchKeyboard");
		addButton("nroot", index, functions);

		firstPageChars = new KeyPanelBase();
		firstPageChars.addStyleName("KeyPanelNum");

		// fill first row
		index = 0;
		addButton("[", index, firstPageChars);
		addButton("]", index, firstPageChars);
		addButton("!", index, firstPageChars);
		addButton(Unicode.IMAGINARY, index, firstPageChars);

		// fill second row
		index++;
		addButton("{", index, firstPageChars);
		addButton("}", index, firstPageChars);
		addButton("a_n", "_", index, firstPageChars);
		addButton(Unicode.OPEN_DOUBLE_QUOTE + " " + Unicode.CLOSE_DOUBLE_QUOTE,
				"quotes", index, firstPageChars);


		// fill third row
		index++;
		addButton(Unicode.LESS_EQUAL + "", index, firstPageChars);
		addButton(Unicode.GREATER_EQUAL + "", index, firstPageChars);
		addButton("%", index, firstPageChars);
		addButton("$", index, firstPageChars);

		// fill forth row
		index++;
		addFunctionalButton(index, firstPageChars, PAGE_ONE_OF_TWO,
				Action.SWITCH_KEYBOARD).addStyleName("switchKeyboard");
		addButton(Unicode.COLON_EQUALS, index, firstPageChars);
		addButton(":", index, firstPageChars);
		addButton(";", index, firstPageChars);

		/** create second page of special chars */
		secondPageChars = new KeyPanelBase();
		secondPageChars.addStyleName("KeyPanelNum");

		// fill first row
		index = 0;
		addButton(Unicode.INFINITY + "", index, secondPageChars);
		addButton(Unicode.QUESTEQ, index, secondPageChars);
		addButton(Unicode.NOTEQUAL, index, secondPageChars);
		addButton("&", index, secondPageChars);

		// fill second row
		index++;
		addButton(Unicode.AND, index, secondPageChars);
		addButton(Unicode.OR, index, secondPageChars);
		addButton(Unicode.IMPLIES, index, secondPageChars);
		addButton(Unicode.NOT, index, secondPageChars);

		// fill third row
		index++;
		addButton(Unicode.IS_ELEMENT_OF + "", index, secondPageChars);
		addButton(Unicode.IS_SUBSET_OF_STRICT + "", index, secondPageChars);
		addButton(Unicode.IS_SUBSET_OF + "", index, secondPageChars);
		addButton(Unicode.ANGLE, index, secondPageChars);

		// fill forth row
		index++;
		addFunctionalButton(index, secondPageChars, PAGE_TWO_OF_TWO,
				Action.SWITCH_KEYBOARD).addStyleName("switchKeyboard");
		addButton(Unicode.PARALLEL, index, secondPageChars);
		addButton(Unicode.PERPENDICULAR + "", index, secondPageChars);
		addButton(Unicode.VECTOR_PRODUCT + "", index, secondPageChars);

		specialCharContainer = new SimplePanel();
		specialCharContainer.add(firstPageChars);

		contentSpecialChars.add(functions);
		contentSpecialChars.add(specialCharContainer);
		contentSpecialChars.add(getControlKeyPanel());
	}

	/**
	 * adds a button to the row with index {@code row} within the given
	 * keyPanel. Use this only for {@link KeyBoardButtonBase} with same caption
	 * and feedback.
	 * 
	 * @param caption
	 *            of button
	 * @param index
	 *            of row
	 * @param panel
	 *            {@link KeyPanelBase}
	 * @return {@link KeyBoardButtonBase}
	 */
	protected KeyBoardButtonBase addButton(String caption, int index,
			KeyPanelBase panel) {
		return addButton(caption, caption, index, panel);
	}

	/**
	 * adds a button to the row with index {@code row} within the given
	 * keyPanel.
	 * 
	 * @param caption
	 *            of button
	 * @param feedback
	 *            of button
	 * @param index
	 *            of row
	 * @param panel
	 *            {@link KeyPanelBase}
	 * @return {@link KeyBoardButtonBase}
	 */
	protected KeyBoardButtonBase addButton(String caption, String feedback,
			int index, KeyPanelBase panel) {
		KeyBoardButtonBase button = new KeyBoardButtonBase(caption, feedback,
				this);
		panel.addToRow(index, button);
		return button;
	}

	/**
	 * adds a functional button to the row with index {@code row} within the
	 * given keyPanel. Use this only for {@link KeyBoardButtonFunctionalBase}
	 * with an String as caption.
	 * 
	 * @param index
	 *            of row
	 * @param keyPanel
	 *            {@link KeyPanelBase}
	 * @param caption
	 *            of button
	 * @param action
	 *            {@link Action}
	 * @return {@link KeyBoardButtonFunctionalBase}
	 */
	protected KeyBoardButtonFunctionalBase addFunctionalButton(int index,
			KeyPanelBase keyPanel, String caption, Action action) {
		KeyBoardButtonFunctionalBase button = new KeyBoardButtonFunctionalBase(
				caption, this, action);
		keyPanel.addToRow(index, button);
		return button;
	}

	/**
	 * adds a functional button to the row with index {@code row} within the
	 * given keyPanel. Use this only for {@link KeyBoardButtonFunctionalBase}
	 * with an image.
	 * 
	 * @param image
	 *            of the button
	 * @param index
	 *            of row
	 * @param keyPanel
	 *            {@link KeyPanelBase}
	 * @param action
	 *            {@link Action}
	 * @return {@link KeyBoardButtonFunctionalBase}
	 */
	protected KeyBoardButtonFunctionalBase addFunctionalButton(
			ImageResource image, Action action, int index, KeyPanelBase keyPanel) {
		KeyBoardButtonFunctionalBase button = new KeyBoardButtonFunctionalBase(
				image, this, action);
		keyPanel.addToRow(index, button);
		return button;
	}

	/**
	 * processes the click on one of the keyboard buttons
	 * 
	 * @param btn
	 *            the button that was clicked
	 * @param type
	 *            the type of click (mouse vs. touch)
	 */
	public void onClick(KeyBoardButtonBase btn,
			@SuppressWarnings("unused") PointerEventType type) {
		if (btn instanceof KeyBoardButtonFunctionalBase) {
			KeyBoardButtonFunctionalBase button = (KeyBoardButtonFunctionalBase) btn;

			switch (button.getAction()) {
			case SHIFT:
				removeAccents();
				processShift();
				break;
			case BACKSPACE:
				processField.onBackSpace();
				break;
			case ENTER:
				processField.onEnter();
				if (processField.resetAfterEnter()) {
					updateKeyBoardListener.keyBoardNeeded(false, null);
				}
				break;
			case ARROW_LEFT:
				processField.onArrow(ArrowType.left);
				break;
			case ARROW_RIGHT:
				processField.onArrow(ArrowType.right);
				break;
			case SWITCH_KEYBOARD:
				String caption = button.getCaption();
				if (caption.equals(GREEK)) {
					setToGreekLetters();
				} else if (caption.equals(NUMBER)) {
					setKeyboardMode(KeyboardMode.NUMBER);
				} else if (caption.equals(TEXT)) {
					if (greekActive) {
						greekActive = false;
						switchABCGreek.setCaption(GREEK);
						loadLang(this.keyboardLocale);
					}
					if (shiftIsDown) {
						processShift();
					}
					if (accentDown) {
						removeAccents();
					}
					setKeyboardMode(KeyboardMode.TEXT);
				} else if (caption.equals(SPECIAL_CHARS)) {
					setKeyboardMode(KeyboardMode.SPECIAL_CHARS);
				}
			}
		} else {

			String text = btn.getFeedback();

			if (isAccent(text)) {
				processAccent(text, btn);
			} else {
				processField.insertString(text);
				if (accentDown) {
					removeAccents();
				}
			}

			if (shiftIsDown && !isAccent(text)) {
				processShift();
			}

			processField.setFocus(true);
		}

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				Scheduler.get().scheduleDeferred(
						new Scheduler.ScheduledCommand() {
							public void execute() {
								processField.scrollCursorIntoView();
							}
						});
			}
		});
	}

	/**
	 * set the text field that will receive the input from the keyboard
	 * 
	 * @param processing
	 *            the text field to be used
	 */
	public void setProcessing(KeyBoardProcessable processing) {
		this.processField = processing;
	}

	protected void processAccent(String accent, KeyBoardButtonBase source) {
		if (accentDown && source != accentButton) {
			removeAccents();
			setToAccents(accent, source);
		} else if (!accentDown) {
			setToAccents(accent, source);
		} else {
			removeAccents();
		}
	}

	protected void removeAccents() {
		for (KeyBoardButtonBase button : letters.getButtons()) {
			if (hasAccent(button.getCaption())) {
				button.setCaption(getWithoutAccent(button.getCaption()));
			}
		}
		if (accentButton != null) {
			accentButton.removeStyleName("accentDown");
		}
		accentDown = false;
	}

	/**
	 * @param accent
	 */
	protected void setToAccents(String accent, KeyBoardButtonBase source) {
		accentButton = source;
		accentButton.addStyleName("accentDown");

		HashMap<String, String> accents = getAccentList(accent);

		for (KeyBoardButtonBase button : letters.getButtons()) {
			if (canHaveAccent(button.getCaption(), accents)) {
				button.setCaption(accents.get(button.getCaption()));
			}
		}
		accentDown = true;
	}

	/**
	 * @param accent
	 * @return
	 */
	protected HashMap<String, String> getAccentList(String accent) {
		HashMap<String, String> accents;
		if (accent.equals(Unicode.ACCENT_ACUTE)) {
			accents = accentAcute;
		} else if (accent.equals(Unicode.ACCENT_CARON)) {
			accents = accentCaron;
		} else if (accent.equals(Unicode.ACCENT_CIRCUMFLEX)) {
			accents = accentCircumflex;
		} else {
			accents = accentGrave;
		}
		return accents;
	}

	private static boolean canHaveAccent(String letter,
			HashMap<String, String> accents) {
		return accents.get(letter) != null;
	}

	protected boolean hasAccent(String letter) {
		return accentAcute.containsValue(letter)
				|| accentCaron.containsValue(letter)
				|| accentCircumflex.containsValue(letter)
				|| accentGrave.containsValue(letter);
	}

	/**
	 * check {@link #hasAccent(String)} before calling this
	 * 
	 * @param letter
	 *            String
	 * @return letter without accent
	 */
	protected String getWithoutAccent(String letter) {
		HashMap<String, String> accents;
		if (accentAcute.containsValue(letter)) {
			accents = accentAcute;
		} else if (accentCaron.containsValue(letter)) {
			accents = accentCaron;
		} else if (accentCircumflex.containsValue(letter)) {
			accents = accentCircumflex;
		} else {
			accents = accentGrave;
		}

		Set<String> keys = accents.keySet();
		for (String key : keys) {
			if (accents.get(key).equals(letter)) {
				return key;
			}
		}
		return letter;
	}

	/**
	 * @param text
	 * @return {@code true} if the given text is an accent
	 */
	protected static boolean isAccent(String text) {
		return text.equals(Unicode.ACCENT_ACUTE)
				|| text.equals(Unicode.ACCENT_CARON)
				|| text.equals(Unicode.ACCENT_GRAVE)
				|| text.equals(Unicode.ACCENT_CIRCUMFLEX);
	}

	protected void processShift() {
		shiftIsDown = !shiftIsDown;
		String local = greekActive ? Language.Greek.localeGWT : keyboardLocale;
		if (shiftIsDown) {
			shiftButton.setPicture(SHIFT_DOWN);
			updateKeys("shiftDown", local);
		} else {
			shiftButton.setPicture(SHIFT);
			updateKeys("lowerCase", local);
		}
	}

	public void setListener(UpdateKeyBoardListener listener) {
		this.updateKeyBoardListener = listener;
	}

	/**
	 * @param mode
	 *            the keyboard mode
	 */
	public void setKeyboardMode(final KeyboardMode mode) {
		this.mode = mode;
		if (mode == KeyboardMode.NUMBER) {
			contentNumber.setVisible(true);
			contentLetters.setVisible(false);
			contentSpecialChars.setVisible(false);
		} else if (mode == KeyboardMode.TEXT) {
			greekActive = false;
			contentNumber.setVisible(false);
			contentLetters.setVisible(true);
			contentSpecialChars.setVisible(false);
			updateKeyBoardListener.showInputField();
		} else if (mode == KeyboardMode.SPECIAL_CHARS) {
			contentNumber.setVisible(false);
			contentLetters.setVisible(false);
			contentSpecialChars.setVisible(true);
		}
	}

	protected void setToGreekLetters() {
		setKeyboardMode(KeyboardMode.TEXT);
		greekActive = true;
		switchABCGreek.setCaption(KeyboardMode.TEXT.getInternalName());
		loadLang(Language.Greek.localeGWT);
		if (shiftIsDown) {
			processShift();
		}
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
		if (shiftIsDown) {
			processShift();
		}
	}

	/**
	 * loads the javascript file and updates the keys to the given language
	 * 
	 * @param lang
	 *            the language
	 */
	protected void loadLang(final String lang) {
		ScriptLoadCallback callback = new ScriptLoadCallback() {

			@Override
			public void onLoad() {
				updateKeys("lowerCase", lang);
				setStyleName();
			}
		};
		DynamicScriptElement script = (DynamicScriptElement) Document.get()
				.createScriptElement();
		script.setSrc(GWT.getModuleBaseURL() + "js/keyboard_" + lang + ".js");

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
	protected void updateKeys(String updateSection, String language) {
		// update letter keys
		ArrayList<KeyBoardButtonBase> buttons = this.letters.getButtons();
		for (int i = 0; i < NUM_LETTER_BUTTONS; i++) {
			KeyBoardButtonBase button = buttons.get(i);
			if (!(button instanceof KeyBoardButtonFunctionalBase)) {

				String newCaption = getNewCaption(generateKey(i),
						updateSection, language);

				if (newCaption.equals("")) {
					button.setVisible(false);
					button.getElement().getParentElement()
							.addClassName("hidden");
				} else {
					button.setVisible(true);
					button.getElement().getParentElement()
							.removeClassName("hidden");
					button.setCaption(newCaption);
				}
			}
		}

		// update e.g. button with sin/cos/tan according to the new language
		for (KeyBoardButtonBase b : updateButton.keySet()) {
			String captionPlain = updateButton.get(b);
			if (captionPlain.endsWith("^-1")) {
				// e.g. for "sin^-1" only "sin" is translated
				captionPlain = captionPlain.substring(0,
						captionPlain.lastIndexOf("^-1"));
				// always use the English output (e.g. "arcsin")
				b.setCaption(loc.getPlain(captionPlain) + "^-1", false);
			} else {
				// use language specific output
				b.setCaption(loc.getPlain(captionPlain), true);
			}
		}

		processField.updateForNewLanguage(loc);

		checkStyle();
	}

	/**
	 * get translations for the onScreenKeyboard-buttons
	 * 
	 * @param key
	 *            String to translate
	 * @param updateSection
	 *            "lowerCase" or "shiftDown"
	 * @param language
	 *            language of the key
	 * @return String for keyboardButton
	 */
	protected String getNewCaption(String key, String updateSection,
			String language) {
		return loc.getKey(key, updateSection, language);
	}

	/**
	 * we need smaller buttons (shift and backspace) if the third row has only
	 * one button less than the upper two rows. otherwise the buttons would
	 * overlap.
	 */
	protected void checkStyle() {
		int first = countVisibleButtons(this.letters.getRows().get(0));
		int second = countVisibleButtons(this.letters.getRows().get(1));
		int third = countVisibleButtons(this.letters.getRows().get(2));
		this.numVisibleButtons = Math.max(first, second);

		if (numVisibleButtons - third < 1) {
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
	protected static String generateKey(int i) {
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
	protected void checkLanguage() {
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

	/**
	 * @return {@code true} if the keyboard should be shown
	 */
	public boolean shouldBeShown() {
		return this.keyboardWanted;
	}

	public void showOnFocus() {
		this.keyboardWanted = true;
	}

	public void setHasKeyboard(HasKeyboard hasKeyboard) {
		this.hasKeyboard = hasKeyboard;
	}

}