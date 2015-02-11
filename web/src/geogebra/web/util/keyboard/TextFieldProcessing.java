package geogebra.web.util.keyboard;

import geogebra.common.util.Unicode;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
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
		empty, autoCompleteTextField, radioButtonTreeItem, other;
	}

	public void setField(Widget field) {
		this.field = field;
		if (field == null) {
			state = State.empty;
		} else if (field instanceof AutoCompleteTextFieldW) {
			state = State.autoCompleteTextField;
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
		case radioButtonTreeItem:
			((NewRadioButtonTreeItem) field).keydown(8, false, false, false);
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
				onArrow(ArrowType.left);
			}
			break;
		case radioButtonTreeItem:
			boolean stepBack = true;
			if (text.equals("^")) {
				if (((NewRadioButtonTreeItem) field).getText().length() == 0) {
					return;
				}
				((NewRadioButtonTreeItem) field).insertString("^{}");
			} else if (text.startsWith(Unicode.EULER_STRING)) {
				((NewRadioButtonTreeItem) field)
				        .insertString(Unicode.EULER_STRING + "^{}");
			} else if (text.equals("sin") || text.equals("cos")
					|| text.equals("tan") || text.equals("ln")) {
				((NewRadioButtonTreeItem) field).insertString(text + "()");
			} else if (text.equals(Unicode.SQUARE_ROOT)) {
				((NewRadioButtonTreeItem) field).insertString("\\sqrt{}");
			} else if (text.startsWith("(")) {
				((NewRadioButtonTreeItem) field)
						.insertString("\\left({}\\right)");
			} else if (text.startsWith("[")) {
				((NewRadioButtonTreeItem) field)
						.insertString("\\left[{}\\right]");
			} else if (text.equals("/")) {
				((NewRadioButtonTreeItem) field).keypress(47, false, false,
						false);
				stepBack = false;
			} else {
				((NewRadioButtonTreeItem) field).insertString(text);
				stepBack = false;
			}
			if (stepBack) {
				((NewRadioButtonTreeItem) field).keydown(37, false, false,
				        false);
			}
			((NewRadioButtonTreeItem) field).popupSuggestions();
			break;
		}
	}

	/**
	 * simulates an enter key event
	 * 
	 * @param key
	 *            key code
	 * @param alt
	 *            pressed or not
	 * @param ctrl
	 *            pressed or not
	 * @param shift
	 *            pressed or not
	 */
	public void onKeydown(int key, boolean alt, boolean ctrl, boolean shift) {
		switch (state) {
		case autoCompleteTextField:
			NativeEvent event = Document.get().createKeyDownEvent(ctrl, alt,
			        shift, false, key);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
			        Event.as(event));

			NativeEvent event2 = Document.get().createKeyPressEvent(ctrl, alt,
					shift, false, key);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event2));

			NativeEvent event3 = Document.get().createKeyUpEvent(ctrl, alt,
					shift, false, key);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event3));
			break;
		case radioButtonTreeItem:
			((NewRadioButtonTreeItem) field).keydown(key, alt, ctrl, shift);
			break;
		}
	}

}
