package geogebra.web.util.keyboard;

import geogebra.common.util.Unicode;
import geogebra.html5.main.DrawEquationWeb;
import geogebra.web.css.GuiResources;
import geogebra.web.util.keyboard.TextFieldProcessing.ArrowType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

	private static OnScreenKeyBoard instance;

	private HorizontalPanel contentNumber = new HorizontalPanel();
	private FlowPanel contentGreek = new FlowPanel();
	// TODO remove for mobile devices
	private FlowPanel contentLetters = new FlowPanel();
	private Widget textField;
	private TextFieldProcessing processing = new TextFieldProcessing();

	private static final String PI = "\u03C0";
	private static final String BACKSPACE = "\u21A4";
	private static final String ENTER = "\u21B2";
	private static final String SHIFT = "\u21E7";
	private static final String ARROW_LEFT = "\u2190";
	private static final String ARROW_RIGHT = "\u2192";
	private static final String COLON_EQUALS = "\u2254";
	private static final String SHOW_NUMBERS = "123";
	private static final String SHOW_TEXT = "ABC";
	private static final String SHOW_GREEK = Unicode.alphaBetaGamma;
	private static final String SPACE = "\u0020";

	private KeyboardMode mode = KeyboardMode.NUMBER;
	private KeyPanel letters;
	private KeyPanel greekLetters;
	/**
	 * positioning (via setPopupPosition) needs to be enabled in order to
	 * prevent automatic positioning in the constructor
	 */
	public boolean enablePositioning = false;

	/**
	 * listener for updates of the keyboard structure
	 */
	private UpdateKeyBoardListener updateKeyBoardListener;

	/**
	 * creates a keyboard instance
	 * 
	 * @param textField
	 *            the textField to receive the key-events
	 * @param listener
	 *            {@link UpdateKeyBoardListener}
	 * @return instance of onScreenKeyBoard
	 */
	public static OnScreenKeyBoard getInstance(Widget textField,
	        UpdateKeyBoardListener listener) {
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

		// greek - keyboard
		createGreekLettersKeyPanel();

		// // TODO needs to be added for mobile devices
		// KeyBoardMenu menu = new KeyBoardMenu(this);
		FlowPanel p = new FlowPanel();
		// p.add(menu);
		contentNumber.addStyleName("KeyBoardContentNumbers");
		p.add(contentNumber);
		p.add(contentLetters);
		p.add(contentGreek);
		contentNumber.setVisible(true);
		contentLetters.setVisible(false);
		contentGreek.setVisible(false);

		// closeButton
		p.add(getCloseButton());

		add(p);
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
		newButton = new KeyBoardButton("x²", "²", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.SQUARE_ROOT, this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("", "^", this);
		DrawEquationWeb
		        .drawEquationAlgebraView(newButton.getElement(), "x^{y}");
		functions.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton("( )", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("[ ]", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("<", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(">", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(COLON_EQUALS, ":=", this);
		functions.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton("sin", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("cos", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("tan", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("exp", Unicode.EULER_STRING + "^", this);
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton("ln", this);
		functions.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton(SHOW_TEXT, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("long");
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(SHOW_GREEK, this);
		newButton.addStyleName("colored");
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
		KeyPanel control = new KeyPanel();

		int index = 0;
		KeyBoardButton newButton = new KeyBoardButton(BACKSPACE, this);
		newButton.addStyleName("backspace");
		newButton.addStyleName("colored");
		control.addToRow(index, newButton);

		index++;
		newButton = new KeyBoardButton(ENTER, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("enter");
		control.addToRow(index, newButton);

		index++;
		newButton = new KeyBoardButton(ARROW_LEFT, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("arrow");
		control.addToRow(index, newButton);
		newButton = new KeyBoardButton(ARROW_RIGHT, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("arrow");
		control.addToRow(index, newButton);

		contentNumber.add(control);
	}

	private void createLettersKeyPanel() {
		letters = new KeyPanel();
		letters.addStyleName("KeyPanelLetters");

		// fill first line
		int index = 0;
		KeyBoardButton newButton = new KeyBoardButton("q", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("w", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("e", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("r", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("t", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("y", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("u", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("i", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("o", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("p", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(BACKSPACE, this);
		newButton.addStyleName("colored");
		letters.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton("a", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("s", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("d", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("f", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("g", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("h", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("j", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("k", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("l", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(ENTER, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("long");
		letters.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton(SHIFT, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("shift");
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("z", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("x", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("c", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("v", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("b", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("n", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton("m", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(",", this);
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(SHIFT, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("shift");
		letters.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton(SHOW_NUMBERS, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("long");
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(SHOW_GREEK, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("functionButtonGreek");
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(SPACE, this);
		newButton.addStyleName("space");
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(ARROW_LEFT, this);
		newButton.addStyleName("colored");
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(ARROW_RIGHT, this);
		newButton.addStyleName("colored");
		letters.addToRow(index, newButton);

		contentLetters.add(letters);
	}

	private void createGreekLettersKeyPanel() {
		greekLetters = new KeyPanel();
		greekLetters.addStyleName("KeyPanelLetters");

		// fill first line
		int index = 0;
		KeyBoardButton newButton = new KeyBoardButton(Unicode.alpha + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.beta + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.gamma + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.delta + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.epsilon + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.zeta + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.eta + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.theta + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.kappa + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.lambda + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(BACKSPACE, this);
		newButton.addStyleName("colored");
		greekLetters.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton(Unicode.mu + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.xi + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.rho + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.sigma + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.tau + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.phi + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.phi_symbol + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.chi + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.psi + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(ENTER, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("long");
		greekLetters.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton(SHIFT, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("shift");
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.omega + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.Gamma + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.Delta + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.Theta + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.Pi + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.Sigma + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.Phi + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.Omega + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(SHIFT, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("shift");
		greekLetters.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton(SHOW_NUMBERS, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("functionButtonSwitch");
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(SHOW_TEXT, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("functionButtonSwitch");
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton("", this);
		newButton.addStyleName("space");
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(ARROW_LEFT, this);
		newButton.addStyleName("colored");
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(ARROW_RIGHT, this);
		newButton.addStyleName("colored");
		greekLetters.addToRow(index, newButton);

		contentGreek.add(greekLetters);
	}

	@Override
	public void onClick(ClickEvent event) {
		event.stopPropagation();
		Object source = event.getSource();
		if (source != null && source instanceof KeyBoardButton) {
			String text = ((KeyBoardButton) source).getText();

			if (text.equals(BACKSPACE)) {
				processing.onBackSpace();
			} else if (text.equals(ENTER)) {
				processing.onEnter();
				this.hide();
			} else if (text.equals(SPACE)) {
				processing.onSpace();
			} else if (text.equals(ARROW_LEFT)) {
				processing.onArrow(ArrowType.left);
			} else if (text.equals(ARROW_RIGHT)) {
				processing.onArrow(ArrowType.right);
			} else if (text.equals(SHIFT)) {
				if (mode == KeyboardMode.GREEK) {
					toggleLettersUpperLowerCase(greekLetters);
				} else {
					toggleLettersUpperLowerCase(letters);
				}
			} else if (text.equals(SHOW_NUMBERS)) {
				setKeyboardMode(KeyboardMode.NUMBER);
			} else if (text.equals(SHOW_TEXT)) {
				setKeyboardMode(KeyboardMode.TEXT);
			} else if (text.equals(SHOW_GREEK)) {
				setKeyboardMode(KeyboardMode.GREEK);
			} else {
				processing.insertString(text);
			}

			if (!text.equals(SHIFT)) {
				if (mode == KeyboardMode.GREEK) {
					setLettersToLowerCase(greekLetters);
				} else {
					setLettersToLowerCase(letters);
				}
			}

			if (textField != null) {
				// textField could be null after onEnter()

				// TODO set to false for mobile devices
				processing.setFocus(true);
			}
		}
	}

	private void setLettersToLowerCase(KeyPanel keyPanel) {
		for (HorizontalPanel row : keyPanel.getRows()) {
			for (int i = 0; i < row.getWidgetCount(); i++) {
				if (row.getWidget(i) instanceof KeyBoardButton) {
					KeyBoardButton b = (KeyBoardButton) row.getWidget(i);
					if (b.getCaption().length() == 1
					        && Character.isLetter(b.getCaption().charAt(0))) {
						b.setCaption(b.getCaption().toLowerCase(), true);
					}
				}
			}
		}
	}

	private void toggleLettersUpperLowerCase(KeyPanel keyPanel) {
		for (HorizontalPanel row : keyPanel.getRows()) {
			for (int i = 0; i < row.getWidgetCount(); i++) {
				if (row.getWidget(i) instanceof KeyBoardButton) {
					KeyBoardButton b = (KeyBoardButton) row.getWidget(i);
					if (b.getCaption().length() == 1
					        && Character.isLetter(b.getCaption().charAt(0))) {
						if (Character.isLowerCase(b.getCaption().charAt(0))) {
							b.setCaption(b.getCaption().toUpperCase(), true);
						} else {
							b.setCaption(b.getCaption().toLowerCase(), true);
						}
					}
				}
			}
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
		this.mode = mode;
		if (mode == KeyboardMode.NUMBER) {
			processing.setKeyBoardModeText(false);
			processing.setFocus(false);
			contentNumber.setVisible(true);
			contentLetters.setVisible(false);
			contentGreek.setVisible(false);
			updateKeyBoardListener.updateKeyBoard(textField);
		} else if (mode == KeyboardMode.TEXT) {
			contentNumber.setVisible(false);
			contentGreek.setVisible(false);
			contentLetters.setVisible(true);
			updateKeyBoardListener.updateKeyBoard(textField);
			processing.setKeyBoardModeText(true);
			processing.setFocus(true);
			updateKeyBoardListener.showInputField();
		} else if (mode == KeyboardMode.GREEK) {
			processing.setKeyBoardModeText(false);
			processing.setFocus(false);
			contentNumber.setVisible(false);
			contentLetters.setVisible(false);
			contentGreek.setVisible(true);
			updateKeyBoardListener.updateKeyBoard(textField);
		}
		setUsed(true);
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
		setLettersToLowerCase(letters);
		setLettersToLowerCase(greekLetters);
		contentLetters.setVisible(false);
		contentGreek.setVisible(false);
	}
}