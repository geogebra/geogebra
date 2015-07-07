package org.geogebra.web.web.util.keyboard;

import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.web.gui.view.algebra.EquationEditorListener;
import org.geogebra.web.web.util.keyboardBase.TextFieldProcessingBase;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;

/**
 * manages the processing of the different types of widgets that
 * {@link OnScreenKeyBoard} can work with
 */
public class TextFieldProcessing extends TextFieldProcessingBase {


	public TextFieldProcessing() {
		super();
	}

	/**
	 * @param field
	 *            the field that should receive all actions
	 */
	@Override
	public void setField(MathKeyboardListener field) {
		super.setField(field);
		if (field != null && field instanceof AutoCompleteTextFieldW) {
			state = State.autoCompleteTextField;
		}
	}

	/**
	 * Focus/Blur the text field
	 * 
	 * @param focus
	 *            true: focus; false: blur
	 */
	@Override
	public void setFocus(boolean focus) {
		if (field == null) {
			return;
		}

		if (state == State.autoCompleteTextField) {
			((AutoCompleteTextFieldW) field).setFocus(focus);
		} else {
			super.setFocus(focus);
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
	@Override
	public void onEnter() {
		if (state == State.autoCompleteTextField) {
			NativeEvent event = Document.get().createKeyDownEvent(false, false,
					false, false, ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));

			event = Document.get().createKeyPressEvent(false, false, false,
					false, ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));

			event = Document.get().createKeyUpEvent(false, false, false, false,
					ENTER);
			((AutoCompleteTextFieldW) field).getTextField().onBrowserEvent(
					Event.as(event));
		} else {
			super.onEnter();
		}
	}

	@Override
	public boolean resetAfterEnter() {
		return state == State.equationEditorListener
				&& ((EquationEditorListener) field).resetAfterEnter();
	}

	/**
	 * simulates a backspace key event
	 */
	@Override
	public void onBackSpace() {
		if (state == State.autoCompleteTextField) {
			((AutoCompleteTextFieldW) field).onBackSpace();
		} else {
			super.onBackSpace();
		}
	}

	/**
	 * simulates arrow events
	 * 
	 * @param type
	 *            {@link ArrowType}
	 */
	@Override
	public void onArrow(ArrowType type) {
		if (state == State.autoCompleteTextField) {
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
		} else {
			super.onArrow(type);
		}
	}

	/**
	 * Inserts the given text at the caret position
	 * 
	 * @param text
	 *            text to be inserted
	 */
	@Override
	public void insertString(String text) {
		if (state == State.autoCompleteTextField) {
			((AutoCompleteTextFieldW) field).insertString(text);
			if (text.startsWith("(")) {
				// moves inside the brackets
				onArrow(ArrowType.left);
			} else if (text.equals(KeyboardConstants.A_POWER_X)) {
				((AutoCompleteTextFieldW) field).insertString("^");
			} else if (text.equals("nroot")) {
				((AutoCompleteTextFieldW) field).insertString("()");
				onArrow(ArrowType.left);
			}
		} else {
			super.insertString(text);
		}
	}
}
