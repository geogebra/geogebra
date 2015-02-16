package geogebra.web.util.keyboard;

import geogebra.common.util.Unicode;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.textbox.GTextBox;
import geogebra.web.gui.view.algebra.NewRadioButtonTreeItem;

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

	private Widget field;
	private State state = State.empty;

	private enum State {
		empty, autoCompleteTextField, gTextBox, radioButtonTreeItem, other;
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
			((NewRadioButtonTreeItem) field).setFocus(true);
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
			NativeEvent event = Document.get().createKeyUpEvent(false, false,
			        false, false, 13);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
			        Event.as(event));
			break;
		case gTextBox:
			NativeEvent event2 = Document.get().createKeyDownEvent(false,
					false, false, false, 13);
			((GTextBox) field).onBrowserEvent(Event.as(event2));
			break;
		case radioButtonTreeItem:
			((NewRadioButtonTreeItem) field).keyup(13, false, false, false);
			break;
		}
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
			((NewRadioButtonTreeItem) field).keydown(8, false, false, false);
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
			((NewRadioButtonTreeItem) field).keypress(32, false, false, false);
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
			switch (type) {
			case left:
				((NewRadioButtonTreeItem) field).keydown(37, false, false,
						false);
				break;
			case right:
				((NewRadioButtonTreeItem) field).keydown(39, false, false,
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
			if (text.equals("^")) {
				if (((NewRadioButtonTreeItem) field).getText().length() == 0) {
					return;
				}
				((NewRadioButtonTreeItem) field).keypress(94, false, false,
						false);
			} else if (text.startsWith(Unicode.EULER_STRING)) {
				((NewRadioButtonTreeItem) field)
						.insertString(Unicode.EULER_STRING);
				// inserts: ^{}
				((NewRadioButtonTreeItem) field).keypress(94, false, false,
						false);
			} else if (text.equals("sin") || text.equals("cos")
					|| text.equals("tan") || text.equals("ln")) {
				((NewRadioButtonTreeItem) field).insertString(text);
				// inserts: ()
				((NewRadioButtonTreeItem) field).keypress(40, false, false,
						false);
			} else if (text.equals(Unicode.SQUARE_ROOT)) {
				((NewRadioButtonTreeItem) field).insertString("\\sqrt{}");
				// move one position back (inside the brackets)
				((NewRadioButtonTreeItem) field).keydown(37, false, false,
						false);
			} else if (text.startsWith("(")) {
				((NewRadioButtonTreeItem) field).keypress(40, false, false,
						false);
			} else if (text.startsWith("[")) {
				((NewRadioButtonTreeItem) field).keypress(91, false, false,
						false);
			} else if (text.equals("/")) {
				((NewRadioButtonTreeItem) field).keypress(47, false, false,
						false);
			} else {
				((NewRadioButtonTreeItem) field).insertString(text);
				((NewRadioButtonTreeItem) field).popupSuggestions();
			}
			break;
		}
	}

}
