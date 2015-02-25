package geogebra.web.util.keyboard;

import geogebra.common.util.Unicode;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.textbox.GTextBox;
import geogebra.web.gui.view.algebra.NewRadioButtonTreeItem;
import geogebra.web.gui.view.algebra.RadioButtonTreeItem;

import java.util.ArrayList;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

/**
 * manages the processing of the different types of widgets that
 * {@link OnScreenKeyBoard} can work with
 */
public class TextFieldProcessing {

	public enum ArrowType {
		left, right, up, down
	}

	private enum State {
		empty, autoCompleteTextField, gTextBox, radioButtonTreeItem, newRadioButtonTreeItem, other;
	}

	/** ASCII */
	private static final int BACKSPACE = 8;
	private static final int ENTER = 13;
	private static final int SPACE = 32;
	private static final int LBRACE = 40;
	private static final int SLASH = 47;
	private static final int LBRACK = 91;
	private static final int CIRCUMFLEX = 94;
	private static final int T_LOWER_CASE = 116;
	private static final int UNDERSCORE = 95;
	private static final int DOLLAR = 36;
	private static final int PIPE = 124;

	/** Javascript char codes */
	private static final int LEFT_ARROW = 37;
	private static final int RIGHT_ARROW = 39;

	private Widget field;
	private State state = State.empty;
	private ArrayList<String> needsLbrace = new ArrayList<String>();

	public TextFieldProcessing() {
		initNeedsLbrace();
	}

	private void initNeedsLbrace() {
		needsLbrace.add("sin");
		needsLbrace.add("cos");
		needsLbrace.add("tan");
		needsLbrace.add("ln");
		needsLbrace.add("sinh");
		needsLbrace.add("cosh");
		needsLbrace.add("tanh");
		needsLbrace.add("arcsin");
		needsLbrace.add("arccos");
		needsLbrace.add("arctan");
		needsLbrace.add("log");
	}

	public void setField(Widget field) {
		this.field = field;
		if (field == null) {
			state = State.empty;
		} else if (field instanceof AutoCompleteTextFieldW) {
			state = State.autoCompleteTextField;
		} else if (field instanceof GTextBox) {
			state = State.gTextBox;
		} else if (field instanceof NewRadioButtonTreeItem) {
			state = State.newRadioButtonTreeItem;
		} else if (field instanceof RadioButtonTreeItem) {
			state = State.radioButtonTreeItem;
		} else {
			state = State.other;
		}
	}

	/**
	 * Focus/Blur the text field
	 * 
	 * @param focus
	 *            true: focus; false: blur
	 */
	public void setFocus(boolean focus) {
		switch (state) {
		case autoCompleteTextField:
			((AutoCompleteTextFieldW) field).setFocus(focus);
			break;
		case gTextBox:
			((GTextBox) field).setFocus(focus);
			break;
		case radioButtonTreeItem:
		case newRadioButtonTreeItem:
			((RadioButtonTreeItem) field).setFocus(true);
			break;
		}
	}

	public void setKeyBoardUsed(boolean used) {
		if (state == State.autoCompleteTextField) {
			((AutoCompleteTextFieldW) field).setKeyBoardUsed(used);
		}
	}

	public void setKeyBoardModeText(boolean b) {
		if (state == State.autoCompleteTextField) {
			((AutoCompleteTextFieldW) field).setKeyBoardModeText(b);
		}
	}

	/**
	 * simulates an enter key event
	 */
	public void onEnter() {
		switch (state) {
		case autoCompleteTextField:
			NativeEvent event = Document.get().createKeyDownEvent(false, false,
					false, false, ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));

			event = Document.get().createKeyPressEvent(false,
					false, false, false, ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));

			event = Document.get().createKeyUpEvent(false, false,
					false, false, ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));
			break;
		case gTextBox:
			NativeEvent event2 = Document.get().createKeyDownEvent(false,
			        false, false, false, ENTER);
			((GTextBox) field).onBrowserEvent(Event.as(event2));
			break;
		case radioButtonTreeItem:
		case newRadioButtonTreeItem:
			((RadioButtonTreeItem) field).keyup(ENTER, false, false, false);
			break;
		}
	}

	public boolean resetAfterEnter() {
		return state == State.radioButtonTreeItem;
	}

	/**
	 * simulates a backspace key event
	 */
	public void onBackSpace() {
		switch (state) {
		case autoCompleteTextField:
			((AutoCompleteTextFieldW) field).onBackSpace();
			break;
		case gTextBox:
			int start = ((GTextBox) field).getCursorPos();
			int end = start + ((GTextBox) field).getSelectionLength();

			if (((GTextBox) field).getSelectionLength() < 1) {
				// nothing selected -> delete character before cursor
				end = start;
				start--;
			}

			if (start > 0) {
				// cursor not at the beginning of text -> delete something
				String oldText = ((GTextBox) field).getText();
				String newText = oldText.substring(0, start)
						+ oldText.substring(end);
				((GTextBox) field).setText(newText);
				((GTextBox) field).setCursorPos(start);
			}
			break;
		case radioButtonTreeItem:
		case newRadioButtonTreeItem:
			((RadioButtonTreeItem) field).keydown(BACKSPACE, false, false,
			        false);
			break;
		}
	}

	/**
	 * simulates a space key event
	 */
	public void onSpace() {
		switch (state) {
		case autoCompleteTextField:
			((AutoCompleteTextFieldW) field).insertString(" ");
			break;
		case gTextBox:
			insertString(" ");
			break;
		case radioButtonTreeItem:
		case newRadioButtonTreeItem:
			((RadioButtonTreeItem) field).keypress(SPACE, false, false, false);
			break;
		}
	}

	/**
	 * simulates arrow events
	 */
	public void onArrow(ArrowType type) {
		switch (state) {
		case autoCompleteTextField:
			int caretPos = ((AutoCompleteTextFieldW) field).getCaretPosition();
			switch (type) {
			case left:
				if (caretPos > 0)
					((AutoCompleteTextFieldW) field)
							.setCaretPosition(caretPos - 1);
				break;
			case right:
				if (caretPos < ((AutoCompleteTextFieldW) field).getText()
						.length()) {
					((AutoCompleteTextFieldW) field)
							.setCaretPosition(caretPos + 1);
				}
				break;
			}
			break;
		case gTextBox:
			int cursorPos = ((GTextBox) field).getCursorPos();
			switch (type) {
			case left:
				if (cursorPos > 0)
					((GTextBox) field).setCursorPos(cursorPos - 1);
				break;
			case right:
				if (cursorPos < ((GTextBox) field).getText().length()) {
					((GTextBox) field).setCursorPos(cursorPos + 1);
				}
				break;
			}
			break;
		case radioButtonTreeItem:
		case newRadioButtonTreeItem:
			switch (type) {
			case left:
				((RadioButtonTreeItem) field).keydown(LEFT_ARROW, false, false,
						false);
				break;
			case right:
				((RadioButtonTreeItem) field).keydown(RIGHT_ARROW, false,
				        false,
						false);
				break;
			}
			break;
		}
	}

	/**
	 * Inserts the given text at the caret position
	 * 
	 * @param text
	 *            text to be inserted
	 */
	public void insertString(String text) {
		switch (state) {
		case autoCompleteTextField:
			((AutoCompleteTextFieldW) field).insertString(text);
			if (text.startsWith("(") || text.startsWith("[")) {
				// moves inside the brackets
				onArrow(ArrowType.left);
			} else if (text.equals("x^y")) {
				((AutoCompleteTextFieldW) field).insertString("^");
			} else if (text.equals("nroot")) {
				((AutoCompleteTextFieldW) field).insertString("()");
				onArrow(ArrowType.left);
			}
			break;
		case gTextBox:
			String oldText = ((GTextBox) field).getText();
			int caretPos = ((GTextBox) field).getCursorPos();

			String newText = oldText.substring(0, caretPos) + text
					+ oldText.substring(caretPos);
			((GTextBox) field).setText(newText);
			((GTextBox) field).setCursorPos(caretPos + text.length());
			break;
		case radioButtonTreeItem:
		case newRadioButtonTreeItem:
			if (text.equals("^")) {
				if (((RadioButtonTreeItem) field).getText().length() == 0) {
					return;
				}
				((RadioButtonTreeItem) field).keypress(CIRCUMFLEX, false,
				        false, false);
			} else if (text.startsWith(Unicode.EULER_STRING)) {
				((RadioButtonTreeItem) field)
						.insertString(Unicode.EULER_STRING);
				// inserts: ^{}
				((RadioButtonTreeItem) field).keypress(CIRCUMFLEX, false,
						false, false);
			} else if (needsLbrace.contains(text)) {
				((RadioButtonTreeItem) field).insertString(text);
				// inserts: ()
				((RadioButtonTreeItem) field).keypress(LBRACE, false, false,
						false);
			} else if (text.equals("nroot")) {
				((RadioButtonTreeItem) field).insertString("nroo");
				((RadioButtonTreeItem) field).keypress(T_LOWER_CASE, false,
				        false, true);
			} else if (text.equals(Unicode.SQUARE_ROOT + "")) {
				((RadioButtonTreeItem) field).keypress('\u221A', false, false,
						false);
			} else if (text.startsWith("(")) {
				((RadioButtonTreeItem) field).keypress(LBRACE, false, false,
						false);
			} else if (text.equals("abs")) {
				((RadioButtonTreeItem) field).keypress(PIPE, false, false,
						false);
			} else if (text.startsWith("[")) {
				((RadioButtonTreeItem) field).keypress(LBRACK, false, false,
						false);
			} else if (text.equals("/")) {
				((RadioButtonTreeItem) field).keypress(SLASH, false, false,
						false);
			} else if (text.equals("_")) {
				((RadioButtonTreeItem) field).keypress(UNDERSCORE, false,
						false, false);
			} else if (text.equals("$")) {
				((RadioButtonTreeItem) field).keypress(DOLLAR, false, false,
						false);
			} else {
				((RadioButtonTreeItem) field).insertString(text);
				((RadioButtonTreeItem) field).popupSuggestions();
			}
			break;
		}
	}
}
