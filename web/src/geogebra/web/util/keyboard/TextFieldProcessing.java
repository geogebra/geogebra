package geogebra.web.util.keyboard;

import geogebra.common.util.Unicode;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.textbox.GTextBox;
import geogebra.web.gui.view.algebra.NewRadioButtonTreeItem;
import geogebra.web.gui.view.algebra.RadioButtonTreeItem;

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
		empty, autoCompleteTextField, gTextBox, radioButtonTreeItem, newRadioButtonTreeItem, other;
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
					false, false, 13);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));

			event = Document.get().createKeyPressEvent(false,
					false, false, false, 13);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));

			event = Document.get().createKeyUpEvent(false, false,
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
		case newRadioButtonTreeItem:
			((RadioButtonTreeItem) field).keyup(13, false, false, false);
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
			((RadioButtonTreeItem) field).keydown(8, false, false, false);
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
			((RadioButtonTreeItem) field).keypress(32, false, false, false);
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
				((RadioButtonTreeItem) field).keydown(37, false, false,
						false);
				break;
			case right:
				((RadioButtonTreeItem) field).keydown(39, false, false,
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
		case newRadioButtonTreeItem:
			if (text.equals("^")) {
				if (((RadioButtonTreeItem) field).getText().length() == 0) {
					return;
				}
				((RadioButtonTreeItem) field).keypress(94, false, false, false);
			} else if (text.startsWith(Unicode.EULER_STRING)) {
				((RadioButtonTreeItem) field)
						.insertString(Unicode.EULER_STRING);
				// inserts: ^{}
				((RadioButtonTreeItem) field).keypress(94, false, false,
						false);
			} else if (text.equals("sin") || text.equals("cos")
					|| text.equals("tan") || text.equals("ln")) {
				((RadioButtonTreeItem) field).insertString(text);
				// inserts: ()
				((RadioButtonTreeItem) field).keypress(40, false, false,
						false);
			} else if (text.equals(Unicode.SQUARE_ROOT)) {
				((RadioButtonTreeItem) field).insertString("\\sqrt{}");
				// move one position back (inside the brackets)
				((RadioButtonTreeItem) field).keydown(37, false, false,
						false);
			} else if (text.startsWith("(")) {
				((RadioButtonTreeItem) field).keypress(40, false, false,
						false);
			} else if (text.startsWith("[")) {
				((RadioButtonTreeItem) field).keypress(91, false, false,
						false);
			} else if (text.equals("/")) {
				((RadioButtonTreeItem) field).keypress(47, false, false,
						false);
			} else {
				((RadioButtonTreeItem) field).insertString(text);
				((RadioButtonTreeItem) field).popupSuggestions();
			}
			break;
		}
	}

}
