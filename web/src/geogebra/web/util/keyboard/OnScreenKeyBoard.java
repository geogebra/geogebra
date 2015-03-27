package geogebra.web.util.keyboard;

import geogebra.common.main.App;
import geogebra.common.util.Language;
import geogebra.common.util.Unicode;
import geogebra.html5.main.AppW;
import geogebra.html5.util.DynamicScriptElement;
import geogebra.html5.util.ScriptLoadCallback;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.NoDragImage;
import geogebra.web.gui.view.algebra.AlgebraViewWeb;
import geogebra.web.gui.view.algebra.RadioButtonTreeItem;
import geogebra.web.util.keyboard.KeyBoardButtonFunctional.Action;
import geogebra.web.util.keyboard.TextFieldProcessing.ArrowType;

import java.util.ArrayList;
import java.util.HashMap;

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

	private static HashMap<App, OnScreenKeyBoard> instances;
	private static final int MIN_WIDTH_FONT = 485;
	private static final int KEY_PER_ROW = 12;
	private static final int NUM_LETTER_BUTTONS = 38;

	/** text of the buttons */
	private static final String GREEK = Unicode.alphaBetaGamma;
	private static final String TEXT = KeyboardMode.TEXT.getInternalName();
	private static final String NUMBER = KeyboardMode.NUMBER.getInternalName();
	private static final String SPECIAL_CHARS = KeyboardMode.SPECIAL_CHARS
	        .getInternalName();

	/** images of the buttons */
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
	private int numVisibleButtons;
	private AppW app;
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
	private boolean keyboardWanted = false;

	/** language of application */
	String keyboardLocale = "";
	private KeyBoardButtonFunctional shiftButton;
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

		if (instances == null) {
			instances = new HashMap<App,OnScreenKeyBoard>();
		}
		OnScreenKeyBoard instance = instances.get(appW);
		if (instance == null) {
			instance = new OnScreenKeyBoard(appW);
			instances.put(appW,instance);
		}

		// set keyboard used to false for the old text field
		instance.setUsed(false);
		instance.setTextField(textField == null ? ((AlgebraViewWeb) appW
		        .getAlgebraView()).getInputTreeItem() : textField);
		// set keyboard used to true for the new text field
		instance.setUsed(true);

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
	public static void setInstanceTextField(App app, Widget textField) {
		if (instances != null && instances.get(app) != null) {
			instances.get(app).setTextField(textField);
		}
	}

	/**
	 * set whether the keyboard is used at the moment or not
	 * 
	 * @param used
	 *            whether the keyboard is used or not
	 */
	public void setUsed(boolean used) {
		if (this.textField != null) {
			this.processing.setKeyBoardUsed(used
			        && this.contentNumber.isVisible());
		}
	}

	/**
	 * should not be called; use getInstance instead
	 * 
	 * @param appW
	 */
	private OnScreenKeyBoard(AppW appW) {
		super(true);
		this.app = appW;
		addStyleName("KeyBoard");
		createKeyBoard();
		if (this.app.isPrerelease()) {
			// this is needed until we support other fonts than Latin
			supportedLocales.put(Language.Korean.localeGWT, "ko");
		}
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
				updateSize();
				setStyleName();
			}
		});
	}

	/**
	 * sets width of the onScreenKeyboard and adds specific styleNames if
	 * keyboard needs to be scaled
	 */
	void updateSize() {
		// -10 because of padding
		this.setWidth(app.getWidth() - 10 + "px");
	}

	/**
	 * adds a specific styleName to the keyboard (if keyboard has to be scaled
	 * or not)
	 */
	private void setStyleName() {
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

	private SimplePanel getCloseButton() {
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

	private void createFunctionsKeyPanel() {
		KeyPanel functions = new KeyPanel();
		functions.addStyleName("KeyPanelFunction");

		// fill first row
		int index = 0;
		addButton("x", index, functions);
		addButton("y", index, functions);
		addButton("x^2", KeyboardConstants.SQUARE, index, functions)
		        .addStyleName("supScript");
		addButton(KeyboardConstants.SQUARE_ROOT, index, functions);
		addButton("x^y", KeyboardConstants.X_POWER_Y, index,
		        functions).addStyleName("supScript");

		// fill next row
		index++;
		addButton("( )", "()", index, functions);
		addButton(KeyboardConstants.DEGREE, index, functions);
		addButton("<", index, functions);
		addButton(">", index, functions);
		addButton(KeyboardConstants.COLON_EQUALS, index, functions);

		// fill next row
		index++;
		addButton("sin", index, functions);
		addButton("cos", index, functions);
		addButton("tan", index, functions);
		addButton("e^x", KeyboardConstants.EULER + "^", index, functions)
		        .addStyleName("supScript");
		addButton("|x|", "abs", index, functions);

		// fill next row
		index++;
		addFunctionalButton(index, functions, TEXT, Action.SWITCH_KEYBOARD);
		addFunctionalButton(index, functions, SPECIAL_CHARS, Action.SWITCH_KEYBOARD).addStyleName("switchToSpecialChar");
		addFunctionalButton(index, functions, GREEK, Action.SWITCH_KEYBOARD);
		
		addButton(KeyboardConstants.PI, index, functions);
		addButton(",", index, functions);

		contentNumber.add(functions);
	}

	private void createNumbersKeyPanel() {
		KeyPanel numbers = new KeyPanel();
		numbers.addStyleName("KeyPanelNum");

		// fill first row
		int index = 0;
		addButton("7", index, numbers);
		addButton("8", index, numbers);
		addButton("9", index, numbers);
		addButton(KeyboardConstants.DIVIDE, "/", index, numbers);

		// fill next row
		index++;
		addButton("4", index, numbers);
		addButton("5", index, numbers);
		addButton("6", index, numbers);
		addButton(KeyboardConstants.MULTIPLY, "*", index, numbers);

		// fill next row
		index++;
		addButton("1", index, numbers);
		addButton("2", index, numbers);
		addButton("3", index, numbers);
		addButton(KeyboardConstants.MINUS, index, numbers);

		// fill next row
		index++;
		addButton("0", index, numbers);
		addButton(".", index, numbers);
		addButton("=", index, numbers);
		addButton("+", index, numbers);

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

	private void createLettersKeyPanel() {
		contentLetters.addStyleName("contentLetters");
		letters = new KeyPanel();
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
		addFunctionalButton(index, letters, NUMBER, Action.SWITCH_KEYBOARD);
		addFunctionalButton(index, letters, SPECIAL_CHARS, Action.SWITCH_KEYBOARD).addStyleName("switchToSpecialChar");
		switchABCGreek = addFunctionalButton(index, letters, GREEK,
		        Action.SWITCH_KEYBOARD);
		addButton(KeyboardConstants.SPACE, index, letters)
		        .addStyleName("space");
		addFunctionalButton(ARROW_LEFT, Action.ARROW_LEFT, index, letters);
		addFunctionalButton(ARROW_RIGHT, Action.ARROW_RIGHT, index, letters);
		addFunctionalButton(ENTER, Action.ENTER, index, letters);

		contentLetters.add(letters);
	}

	private void createSpecialCharKeyPanel() {
		contentSpecialChars.addStyleName("KeyBoardContentSpecialChars");
		KeyPanel functions = new KeyPanel();
		functions.addStyleName("KeyPanelFunction");

		// fill first row
		int index = 0;
		addButton("ln", index, functions);
		addButton("log_10", "log", index, functions);
		addButton("nroot", index, functions);

		// fill second row
		index++;
		addButton("sinh", index, functions);
		addButton("cosh", index, functions);
		addButton("tanh", index, functions);

		// fill third row
		index++;
		addButton("arcsin", index, functions);
		addButton("arccos", index, functions);
		addButton("arctan", index, functions);

		// fill forth row
		index++;
		addFunctionalButton(index, functions, TEXT, Action.SWITCH_KEYBOARD);
		addFunctionalButton(index, functions, NUMBER, Action.SWITCH_KEYBOARD);
		addFunctionalButton(index, functions, GREEK, Action.SWITCH_KEYBOARD);


		KeyPanel chars = new KeyPanel();
		chars.addStyleName("KeyPanelNum");

		// fill first row
		index = 0;
		addButton(KeyboardConstants.IMAGINARY, index, chars);
		addButton(KeyboardConstants.INFINITY + "", index, chars);
		addButton(KeyboardConstants.VECTOR_PRODUCT, index, chars);
		addButton(KeyboardConstants.EQUAL_BOOLEAN, index, chars);
		addButton(KeyboardConstants.NOT_EQUAL, index, chars);
		addButton(KeyboardConstants.NOT, index, chars);

		// fill second row
		index++;
		addButton(KeyboardConstants.LESS_EQUAL, index, chars);
		addButton(KeyboardConstants.GREATER_EQUAL, index, chars);
		addButton(KeyboardConstants.AND, index, chars);
		addButton(KeyboardConstants.OR, index, chars);
		addButton(KeyboardConstants.PARALLEL, index, chars);
		addButton(KeyboardConstants.PERPENDICULAR, index, chars);
		
		// fill third row
		index++;
		addButton(KeyboardConstants.IMPLIES, index, chars);
		addButton(KeyboardConstants.IS_ELEMENT_OF, index, chars);
		addButton(KeyboardConstants.IS_SUBSET_OF, index, chars);
		addButton(KeyboardConstants.IS_SUBSET_OF_STRICT, index, chars);
		addButton(KeyboardConstants.ANGLE, index, chars);
		addButton(KeyboardConstants.MEASURED_ANGLE, index, chars);

		// fill forth row
		index++;
		addButton(":", index, chars);
		addButton(";", index, chars);
		addButton("_", index, chars);
		addButton("!", index, chars);
		addButton("%", index, chars);
		addButton("$", index, chars);

		contentSpecialChars.add(functions);
		contentSpecialChars.add(chars);
		contentSpecialChars.add(getControlKeyPanel());
	}

	/**
	 * adds a button to the row with index {@code row} within the given
	 * keyPanel. Use this only for {@link KeyBoardButton} with same caption and
	 * feedback.
	 * 
	 * @param caption
	 *            of button
	 * @param index
	 *            of row
	 * @param panel
	 *            {@link KeyPanel}
	 * @return {@link KeyBoardButton}
	 */
	private KeyBoardButton addButton(String caption, int index, KeyPanel panel) {
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
	 *            {@link KeyPanel}
	 * @return {@link KeyBoardButton}
	 */
	private KeyBoardButton addButton(String caption, String feedback,
	        int index,
	        KeyPanel panel) {
		KeyBoardButton button = new KeyBoardButton(caption, feedback, this);
		panel.addToRow(index, button);
		return button;
	}

	/**
	 * adds a functional button to the row with index {@code row} within the
	 * given keyPanel. Use this only for {@link KeyBoardButtonFunctional} with
	 * an String as caption.
	 * 
	 * @param index
	 *            of row
	 * @param keyPanel
	 *            {@link KeyPanel}
	 * @param caption
	 *            of button
	 * @param action
	 *            {@link Action}
	 * @return {@link KeyBoardButtonFunctional}
	 */
	private KeyBoardButtonFunctional addFunctionalButton(int index,
	        KeyPanel keyPanel, String caption, Action action) {
		KeyBoardButtonFunctional button = new KeyBoardButtonFunctional(caption,
		        this, action);
		keyPanel.addToRow(index, button);
		return button;
	}
	
	/**
	 * adds a functional button to the row with index {@code row} within the
	 * given keyPanel. Use this only for {@link KeyBoardButtonFunctional} with
	 * an image.
	 * 
	 * @param image
	 *            of the button
	 * @param index
	 *            of row
	 * @param keyPanel
	 *            {@link KeyPanel}
	 * @param action
	 *            {@link Action}
	 * @return {@link KeyBoardButtonFunctional}
	 */
	private KeyBoardButtonFunctional addFunctionalButton(ImageResource image,
	        Action action, int index, KeyPanel keyPanel) {
		KeyBoardButtonFunctional button = new KeyBoardButtonFunctional(image,
		        this, action);
		keyPanel.addToRow(index, button);
		return button;
	}

	@Override
	public void onClick(ClickEvent event) {
		event.preventDefault();
		event.stopPropagation();
		Object source = event.getSource();
		if (source != null && source instanceof KeyBoardButtonFunctional) {
			KeyBoardButtonFunctional button = (KeyBoardButtonFunctional) source;

			switch (button.getAction()) {
			case SHIFT:
				processShift();
				break;
			case BACKSPACE:
				processing.onBackSpace();
				break;
			case ENTER:
				// make sure enter is processed correctly
				if (resetComponent != null) {
					resetComponent.resetBlockBlur();
					resetComponent = null;
				}
				processing.onEnter();
				if (processing.resetAfterEnter()) {
					updateKeyBoardListener.keyBoardNeeded(false, null);
				}
				break;
			case ARROW_LEFT:
				processing.onArrow(ArrowType.left);
				break;
			case ARROW_RIGHT:
				processing.onArrow(ArrowType.right);
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
					setKeyboardMode(KeyboardMode.TEXT);
				} else if (caption.equals(SPECIAL_CHARS)) {
					setKeyboardMode(KeyboardMode.SPECIAL_CHARS);
				}
			}
		} else if (source != null && source instanceof KeyBoardButton) {

			String text = ((KeyBoardButton) source).getFeedback();
			if (isAccent(text)) {
				processing.onAccent(text);
			} else {
				processing.insertString(text);
			}
			if (shiftIsDown && !isAccent(text)) {
				processShift();
			}

			if (textField != null) {
				// textField could be null after onEnter()

				// TODO set to false for mobile devices
				processing.setFocus(true);
			}
		}

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				processing.scrollCursorIntoView();
			}
		});
	}

	/**
	 * @param text
	 * @return {@code true} if the given text is an accent
	 */
	private static boolean isAccent(String text) {
		return text.equals(KeyboardConstants.ACCENT_ACUTE)
		        || text.equals(KeyboardConstants.ACCENT_CARON)
		        || text.equals(KeyboardConstants.ACCENT_GRAVE)
		        || text.equals(KeyboardConstants.ACCENT_CIRCUMFLEX);
	}

	private void processShift() {
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
		} else if (mode == KeyboardMode.TEXT) {
			greekActive = false;
			contentNumber.setVisible(false);
			contentLetters.setVisible(true);
			contentSpecialChars.setVisible(false);
			processing.setKeyBoardModeText(true);
			updateKeyBoardListener.showInputField();
		} else if (mode == KeyboardMode.SPECIAL_CHARS) {
			processing.setKeyBoardModeText(false);
			contentNumber.setVisible(false);
			contentLetters.setVisible(false);
			contentSpecialChars.setVisible(true);
		}
		setUsed(true);
	}

	private void setToGreekLetters() {
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
		if (resetComponent != null) {
			resetComponent.resetBlockBlur();
			resetComponent = null;
		}
	}

	public void setResetComponent(RadioButtonTreeItem rbti) {

		this.resetComponent = rbti;

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
				setStyleName();
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
			if (!(button instanceof KeyBoardButtonFunctional)) {
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
					button.setCaption(newCaption);
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

	/**
	 * @return {@code true} if the keyboard should be shown
	 */
	public boolean shouldBeShown() {
		return this.keyboardWanted;
	}

	@Override
	public void show() {
		this.keyboardWanted = true;
		updateSize();
		checkLanguage();
		super.show();
	}
}