package geogebra.web.util.keyboard;

import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.util.Unicode;
import geogebra.html5.main.DrawEquationWeb;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.view.algebra.RadioButtonTreeItem;
import geogebra.web.util.keyboard.TextFieldProcessing.ArrowType;

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

	private static OnScreenKeyBoard instance;
	private static final int MIN_WIDTH_WITHOUT_SCALING = 823;
	private static final int MIN_WIDTH_FONT = 485;

	private HorizontalPanel contentNumber = new HorizontalPanel();
	private HorizontalPanel contentSpecialChars = new HorizontalPanel();
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
	private static final String SPACE = "\u0020";
	private static final String ANGLE = "\u2220";
	private static final String MEASURED_ANGLE = "\u2221";

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
	UpdateKeyBoardListener updateKeyBoardListener;

	private RadioButtonTreeItem resetComponent;

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

		// greek - keyboard
		createGreekLettersKeyPanel();

		// special characters - keyboard
		createSpecialCharKeyPanel();

		// // TODO needs to be added for mobile devices
		// KeyBoardMenu menu = new KeyBoardMenu(this);
		FlowPanel p = new FlowPanel();
		// p.add(menu);
		contentNumber.addStyleName("KeyBoardContentNumbers");
		p.add(contentNumber);
		p.add(contentLetters);
		p.add(contentGreek);
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
		newButton = new KeyBoardButton("", "^", this);
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
		        this);
		newButton.addStyleName("colored");
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(
		        KeyboardMode.SPECIAL_CHARS.getInternalName(), this);
		newButton.addStyleName("switchToSpecialChar");
		newButton.addStyleName("colored");
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(KeyboardMode.GREEK.getInternalName(),
		        this);
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
		contentNumber.add(getControlKeyPanel());
	}

	/**
	 * @return
	 */
    private KeyPanel getControlKeyPanel() {
	    KeyPanel control = new KeyPanel();
		control.addStyleName("KeyPanelControl");

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
		newButton.setNavigationButton(true);
		newButton.addStyleName("colored");
		newButton.addStyleName("arrow");
		control.addToRow(index, newButton);
		newButton = new KeyBoardButton(ARROW_RIGHT, this);
		newButton.setNavigationButton(true);
		newButton.addStyleName("colored");
		newButton.addStyleName("arrow");
		control.addToRow(index, newButton);
	    return control;
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
		newButton.addStyleName("longEnter");
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
		newButton = new KeyBoardButton(KeyboardMode.NUMBER.getInternalName(),
		        this);
		newButton.addStyleName("colored");
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(
		        KeyboardMode.SPECIAL_CHARS.getInternalName(), this);
		newButton.addStyleName("switchToSpecialChar");
		newButton.addStyleName("colored");
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(KeyboardMode.GREEK.getInternalName(),
		        this);
		newButton.addStyleName("colored");
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(SPACE, this);
		newButton.addStyleName("space");
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(ARROW_LEFT, this);
		newButton.setNavigationButton(true);
		newButton.addStyleName("colored");
		letters.addToRow(index, newButton);
		newButton = new KeyBoardButton(ARROW_RIGHT, this);
		newButton.setNavigationButton(true);
		newButton.addStyleName("colored");
		letters.addToRow(index, newButton);

		contentLetters.add(letters);
	}

	private void createGreekLettersKeyPanel() {
		greekLetters = new KeyPanel();
		greekLetters.addStyleName("KeyPanelLetters");

		// fill first line
		int index = 0;
		KeyBoardButton newButton = new KeyBoardButton(Unicode.sigmaf + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.epsilon + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.rho + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.tau + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.upsilon + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.theta + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.iota + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.omicron + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.pi + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(BACKSPACE, this);
		newButton.addStyleName("colored");
		greekLetters.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton(Unicode.alpha + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.sigma + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.delta + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.phi + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.gamma + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.eta + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.xi + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.kappa + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.lambda + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(ENTER, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("longEnter");
		greekLetters.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton(SHIFT, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("shift");
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.zeta + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.chi + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.psi + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.omega + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.beta + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.nu + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(Unicode.mu + "", this);
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(SHIFT, this);
		newButton.addStyleName("colored");
		newButton.addStyleName("shift");
		greekLetters.addToRow(index, newButton);

		// fill next row
		index++;
		newButton = new KeyBoardButton(KeyboardMode.NUMBER.getInternalName(),
		        this);
		newButton.addStyleName("colored");
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(KeyboardMode.TEXT.getInternalName(),
		        this);
		newButton.addStyleName("colored");
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(
		        KeyboardMode.SPECIAL_CHARS.getInternalName(), this);
		newButton.addStyleName("switchToSpecialChar");
		newButton.addStyleName("colored");
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton("", this);
		newButton.addStyleName("space");
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(ARROW_LEFT, this);
		newButton.setNavigationButton(true);
		newButton.addStyleName("colored");
		greekLetters.addToRow(index, newButton);
		newButton = new KeyBoardButton(ARROW_RIGHT, this);
		newButton.setNavigationButton(true);
		newButton.addStyleName("colored");
		greekLetters.addToRow(index, newButton);

		contentGreek.add(greekLetters);
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
		        this);
		newButton.addStyleName("colored");
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(KeyboardMode.NUMBER.getInternalName(),
		        this);
		newButton.addStyleName("colored");
		functions.addToRow(index, newButton);
		newButton = new KeyBoardButton(KeyboardMode.GREEK.getInternalName(),
		        this);
		newButton.addStyleName("colored");
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

			if (text.equals(BACKSPACE)) {
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
			} else if (text.equals(SPACE)) {
				processing.onSpace();
			} else if (((KeyBoardButton) source).isNavigationButton()
					&& text.equals(ARROW_LEFT)) {
				processing.onArrow(ArrowType.left);
			} else if (((KeyBoardButton) source).isNavigationButton()
					&& text.equals(ARROW_RIGHT)) {
				// the same arrow is also used for implication
				processing.onArrow(ArrowType.right);
			} else if (text.equals(SHIFT)) {
				if (mode == KeyboardMode.GREEK) {
					toggleLettersUpperLowerCase(greekLetters);
				} else {
					toggleLettersUpperLowerCase(letters);
				}
			} else if (text.equals(KeyboardMode.NUMBER.getInternalName())) {
				setKeyboardMode(KeyboardMode.NUMBER);
			} else if (text.equals(KeyboardMode.TEXT.getInternalName())) {
				setKeyboardMode(KeyboardMode.TEXT);
			} else if (text.equals(KeyboardMode.GREEK.getInternalName())) {
				setKeyboardMode(KeyboardMode.GREEK);
			} else if (text
			        .equals(KeyboardMode.SPECIAL_CHARS.getInternalName())) {
				setKeyboardMode(KeyboardMode.SPECIAL_CHARS);
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

	private static void setLettersToLowerCase(KeyPanel keyPanel) {
		for (HorizontalPanel row : keyPanel.getRows()) {
			for (int i = 0; i < row.getWidgetCount(); i++) {
				if (row.getWidget(i) instanceof KeyBoardButton) {
					KeyBoardButton b = (KeyBoardButton) row.getWidget(i);
					if (b.getCaption().length() == 1
					        && b.getCaption().charAt(0) == Unicode.sigmaf) {
						b.setVisible(true);
					} else if (b.getCaption().length() == 1
					        && Character.isLetter(b.getCaption().charAt(0))) {
						b.setCaption(b.getCaption().toLowerCase(), true);
					}
				}
			}
		}
	}

	private static void toggleLettersUpperLowerCase(KeyPanel keyPanel) {
		for (HorizontalPanel row : keyPanel.getRows()) {
			for (int i = 0; i < row.getWidgetCount(); i++) {
				if (row.getWidget(i) instanceof KeyBoardButton) {
					KeyBoardButton b = (KeyBoardButton) row.getWidget(i);
					if (b.getCaption().length() == 1
					        && Character.isLetter(b.getCaption().charAt(0))) {
						if (b.getCaption().charAt(0) == Unicode.sigmaf) {
							b.setVisible(!b.isVisible());
						} else if (Character.isLowerCase(b.getCaption().charAt(
						        0))) {
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

		// TODO focus events might be needed for mobile devices

		this.mode = mode;
		if (mode == KeyboardMode.NUMBER) {
			processing.setKeyBoardModeText(false);
			// processing.setFocus(false);
			contentNumber.setVisible(true);
			contentLetters.setVisible(false);
			contentGreek.setVisible(false);
			contentSpecialChars.setVisible(false);
			updateKeyBoardListener.updateKeyBoard(textField);
		} else if (mode == KeyboardMode.TEXT) {
			contentNumber.setVisible(false);
			contentGreek.setVisible(false);
			contentLetters.setVisible(true);
			contentSpecialChars.setVisible(false);
			updateKeyBoardListener.updateKeyBoard(textField);
			processing.setKeyBoardModeText(true);
			// processing.setFocus(true);
			updateKeyBoardListener.showInputField();
		} else if (mode == KeyboardMode.GREEK) {
			processing.setKeyBoardModeText(false);
			// processing.setFocus(false);
			contentNumber.setVisible(false);
			contentLetters.setVisible(false);
			contentGreek.setVisible(true);
			contentSpecialChars.setVisible(false);
			updateKeyBoardListener.updateKeyBoard(textField);
		} else if (mode == KeyboardMode.SPECIAL_CHARS) {
			processing.setKeyBoardModeText(false);
			// processing.setFocus(false);
			contentNumber.setVisible(false);
			contentLetters.setVisible(false);
			contentGreek.setVisible(false);
			contentSpecialChars.setVisible(true);
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
}