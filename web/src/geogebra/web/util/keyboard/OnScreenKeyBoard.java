package geogebra.web.util.keyboard;

import geogebra.common.util.Unicode;
import geogebra.html5.main.DrawEquationWeb;
import geogebra.web.util.keyboard.TextFieldProcessing.ArrowType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * on screen keyboard containing mathematical symbols and formulas
 */
public class OnScreenKeyBoard extends PopupPanel implements ClickHandler {

	private static OnScreenKeyBoard instance;
	private FlowPanel content = new FlowPanel();
	// TODO remove for mobile devices
	private FlowPanel contentLetters = new FlowPanel();
	private Widget textField;
	private TextFieldProcessing processing = new TextFieldProcessing();

	private static final String PI = "\u03C0";
	private static final String BACKSPACE = "\u21A4";
	private static final String ENTER = "\u21B2";
	private static final String SHIFT = "\u21E7";
	private static final String I = "\u03AF";

	private static final String ARROW_LEFT = "\u2190";
	private static final String ARROW_RIGHT = "\u2192";
	// private static final String ARROW_UP = "\u2191";
	// private static final String ARROW_DOWN = "\u2193";

	private KeyboardMode mode = KeyboardMode.NUMBER;

	/**
	 * positioning (via setPopupPosition) needs to be enabled in order to
	 * prevent automatic positioning in the constructor
	 */
	public boolean enablePositioning = false;

	/**
	 * listener for updates of the keyboard structure
	 */
	private UpdateKeyBoardListener updateKeyBoardListener;
	private KeyPanel letters;

	/**
	 * creates a keyboard instance
	 * 
	 * @param textField
	 *            the textField to receive the key-events
	 * @param frameLayoutPanel
	 * @return instance of onScreenKeyBoard
	 */
	public static OnScreenKeyBoard getInstance(
Widget textField,
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
			        && instance.content.isVisible());
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
		String[] icons = new String[] { "x²", "x³", "", "x", "y",
		        "z", // first
		        // line
		        "( )", "[ ]", "<", ">", Unicode.LESS_EQUAL + "",
		        Unicode.GREATER_EQUAL + "", // second line
				"sin", "cos", "tan", PI, "", I, // third line
		        "ln", Unicode.SQUARE_ROOT, ",", ":=", ARROW_LEFT, ARROW_RIGHT // last
																	  // line
		};
		KeyPanel functions = new KeyPanel(icons, 6, this);
		functions.setSpecialButton("²", false, 0, this);
		functions.setSpecialButton("³", false, 1, this);

		functions.setSpecialButton("^", false, 2, this);
		DrawEquationWeb.drawEquationAlgebraView(functions.colum[2].getWidget(0)
				.getElement(), "x^{y}");

		functions.setSpecialButton(Unicode.EULER_STRING + "^", false, 16, this);
		DrawEquationWeb.drawEquationAlgebraView(functions.colum[4].getWidget(2)
				.getElement(), Unicode.EULER_STRING + "^{x}");

		content.add(functions);

		icons = new String[] { "7", "8", "9", Unicode.divide + "", BACKSPACE, // first
																			  // line
		        "4", "5", "6", Unicode.multiply + "", ENTER,// second line
		        "1", "2", "3", Unicode.minus + "", null, // third line
		        "0", ".", "=", "+" // last line
		};
		KeyPanel numbers = new KeyPanel(icons, 5, this);
		numbers.setSpecialButton("/", false, 3, this);
		numbers.setSpecialButton("*", false, 8, this);
		numbers.setSpecialButton(ENTER, true, 9, this);
		content.add(numbers);

		icons = new String[] { "q", "w", "e", "r", "t", "y", "u", "i", "o",
		        "p", // first line
		        "a", "s", "d", "f", "g", "h", "j", "k", "l", null, // second
																   // line
		        SHIFT, "z", "x", "c", "v", "b", "n", "m", ENTER // last line
		};
		letters = new KeyPanel(icons, 10, this);
		contentLetters.add(letters);

		// TODO needs to be added for mobile devices
		KeyBoardMenu menu = new KeyBoardMenu(this);
		FlowPanel p = new FlowPanel();
		p.add(menu);
		content.addStyleName("KeyBoardContent");
		p.add(content);
		p.add(contentLetters);
		contentLetters.setVisible(false);
		add(p);
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
			} else if (text.equals(ARROW_LEFT)) {
				processing.onArrow(ArrowType.left);
			} else if (text.equals(ARROW_RIGHT)) {
				processing.onArrow(ArrowType.right);
			} else if (text.equals(SHIFT)) {
				for (FlowPanel col : letters.colum) {
					for (int i = 0; i < col.getWidgetCount(); i++) {
						if (col.getWidget(i) instanceof KeyBoardButton) {
							KeyBoardButton b = (KeyBoardButton) col
							        .getWidget(i);
							if (Character.isLetter(b.getCaption().charAt(0))) {
								if (Character.isLowerCase(b.getCaption()
								        .charAt(0))) {
									b.setCaption(b.getCaption().toUpperCase(),
									        true);
								} else {
									b.setCaption(b.getCaption().toLowerCase(),
									        true);
								}
							}
						}
					}
				}
			} else {
				processing.insertString(text);
			}

			if (!text.equals(SHIFT)) {
				resetButtons();
			}

			if (textField != null) {
				// textField could be null after onEnter()

				// TODO set to false for mobile devices
				processing.setFocus(true);
			}
		}
	}

	private void resetButtons() {
	    for(FlowPanel col : letters.colum){
	    	for(int i = 0; i < col.getWidgetCount(); i++){
	    		if (col.getWidget(i) instanceof KeyBoardButton){
	    			KeyBoardButton b = (KeyBoardButton) col.getWidget(i);
					if (Character.isLetter(b.getCaption().charAt(0))) {
						b.setCaption(b.getCaption().toLowerCase(), true);
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
			content.setVisible(true);
			contentLetters.setVisible(false);
			updateKeyBoardListener.updateKeyBoard(textField);
		} else if (mode == KeyboardMode.TEXT) {
			content.setVisible(false);
			contentLetters.setVisible(true);
			updateKeyBoardListener.updateKeyBoard(textField);
			processing.setKeyBoardModeText(true);
			processing.setFocus(true);
			updateKeyBoardListener.showInputField();
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
		content.setVisible(true);
		resetButtons();
		contentLetters.setVisible(false);
	}

}