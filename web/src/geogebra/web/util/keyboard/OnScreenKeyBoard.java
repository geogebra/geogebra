package geogebra.web.util.keyboard;

import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.gui.app.GGWFrameLayoutPanel;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * on screen keyboard containing mathematical symbols and formulas
 */
public class OnScreenKeyBoard extends PopupPanel implements ClickHandler {

	private static OnScreenKeyBoard instance;
	private static boolean inUse;
	private FlowPanel content = new FlowPanel();
	private AutoCompleteTextFieldW textField;

	private static final String PI = "\u03C0";
	private static final String SQUARE_ROOT = "\u221A";
	// private static final String CUBIC_ROOT = "\u221B"; // TODO use (no valid
	                                                   // input)
	private static final String BACKSPACE = "\u21A4";
	private static final String ENTER = "\u21B2";
	// private static final String E = "\u212F"; // TODO use (not displayed
	                                          // correctly)
	private static final String I = "\u03AF";

	private KeyboardMode mode = KeyboardMode.NUMBER;

	/**
	 * positioning (via setPopupPosition) needs to be enabled in order to
	 * prevent automatic positioning in the constructor
	 */
	public boolean enablePositioning = false;
	private GGWFrameLayoutPanel frameLayoutPanel;

	/**
	 * creates a keyboard instance
	 * 
	 * @param textField
	 *            the textField to receive the key-events
	 * @param frameLayoutPanel
	 * @return instance of onScreenKeyBoard
	 */
	public static OnScreenKeyBoard getInstance(
	        AutoCompleteTextFieldW textField,
	        GGWFrameLayoutPanel frameLayoutPanel) {
		if (instance == null) {
			instance = new OnScreenKeyBoard();
		}
		instance.setTextField(textField);
		instance.setFrameLayoutPanel(frameLayoutPanel);
		return instance;
	}

	/**
	 * @return false if no instance was created or the instance is not used or
	 *         the special buttons are not visible; true otherwise
	 */
	public static boolean isInstanceVisible() {
		return inUse && instance != null && instance.content.isVisible();
	}

	/**
	 * set whether the keyboard is used at the moment or not
	 * 
	 * @param used
	 *            whether the keyboard is used or not
	 */
	public static void setUsed(boolean used) {
		inUse = used;
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

	/**
	 * Enter-key was pressed
	 */
	public void onEnter() {
		NativeEvent event = Document.get().createKeyUpEvent(false, false,
		        false, false, 13);
		textField.getTextField().onBrowserEvent(Event.as(event));
		this.hide();
	}

	private void createKeyBoard() {
		String[] icons = new String[] { "a²", "a³", "a^b", "x", "y", "z", // first
		        // line
		        "(", ")", "[", "]", "<", ">", // second line
		        "sin", "cos", "tan", PI, "e", I, // third line
		        "ln", SQUARE_ROOT, ",", ":=" // last line
		};
		KeyPanel functions = new KeyPanel(icons, 6, this);
		functions.setSpecialButton("²", false, 0, this);
		functions.setSpecialButton("³", false, 1, this);
		functions.setSpecialButton("^", false, 2, this);
		content.add(functions);

		icons = new String[] { "7", "8", "9", "/", BACKSPACE, // first line
		        "4", "5", "6", "*", ENTER,// second line
		        "1", "2", "3", "-", null, // third line
		        "0", ".", "=", "+" // last line
		};
		KeyPanel numbers = new KeyPanel(icons, 5, this);
		numbers.setSpecialButton(ENTER, true, 9, this);
		content.add(numbers);

		KeyBoardMenu menu = new KeyBoardMenu(this);
		FlowPanel p = new FlowPanel();
		p.add(menu);
		content.addStyleName("KeyBoardContent");
		p.add(content);
		add(p);
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source != null && source instanceof KeyBoardButton) {
			String text = ((KeyBoardButton) source).getText();

			if (text.equals(BACKSPACE)) {
				textField.onBackSpace();
			} else if (text.equals(ENTER)) {
				onEnter();
			} else {
				textField.insertString(text);
			}
			if (textField != null) {
				// textField could be null after onEnter()
				textField.setFocus(false);
			}
		}

		event.stopPropagation();
	}

	/**
	 * The text field to be used
	 * 
	 * @param textField
	 *            the text field connected to the keyboard
	 */
	public void setTextField(AutoCompleteTextFieldW textField) {
		this.textField = textField;
	}

	private void setFrameLayoutPanel(GGWFrameLayoutPanel frameLayoutPanel) {
		this.frameLayoutPanel = frameLayoutPanel;
	}

	/**
	 * @param mode
	 *            the keyboard mode
	 */
	public void setKeyboardMode(final KeyboardMode mode) {
		this.mode = mode;
		if (mode == KeyboardMode.NUMBER) {
			textField.setFocus(false);
			content.setVisible(true);
			frameLayoutPanel.updateKeyBoard(textField);
		} else if (mode == KeyboardMode.TEXT) {
			content.setVisible(false);
			frameLayoutPanel.updateKeyBoard(textField);
			textField.setFocus(true);
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
		content.setVisible(true);
	}



}