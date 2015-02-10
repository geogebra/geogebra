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
	 * Inserts the given text at the caret position
	 * 
	 * @param text
	 *            text to be inserted
	 */
	public void insertString(String text) {
		switch (state) {
		case autoCompleteTextField:
			((AutoCompleteTextFieldW) field).insertString(text);
			break;
		case radioButtonTreeItem:
			if (text.equals("^")) {
				((NewRadioButtonTreeItem) field).insertString("^{}");
				((NewRadioButtonTreeItem) field).keydown(37, false, false,
				        false);
			} else if (text.startsWith(Unicode.EULER_STRING)) {
				((NewRadioButtonTreeItem) field)
				        .insertString(Unicode.EULER_STRING + "^{}");
				((NewRadioButtonTreeItem) field).keydown(37, false, false,
				        false);
			} else if (text.equals("sin") || text.equals("cos")
			        || text.equals("tan")) {
				((NewRadioButtonTreeItem) field).insertString(text + "()");
				((NewRadioButtonTreeItem) field).keydown(37, false, false,
				        false);
			} else if (text.equals(Unicode.SQUARE_ROOT)) {
				((NewRadioButtonTreeItem) field).insertString("\\sqrt{}");
				((NewRadioButtonTreeItem) field).keydown(37, false, false,
				        false);
			} else {
				((NewRadioButtonTreeItem) field).insertString(text);
			}
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
			break;
		case radioButtonTreeItem:
			((NewRadioButtonTreeItem) field).keydown(key, alt, ctrl, shift);
			break;
		}
	}

}
