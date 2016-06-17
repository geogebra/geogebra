package org.geogebra.web.web.util.keyboard;

import org.geogebra.common.main.KeyboardLocale;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.keyboard.KeyboardConstants;
import org.geogebra.web.keyboard.KeyboardListener;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;

public class AutocompleteProcessing implements KeyboardListener {

	private AutoCompleteTextFieldW field;

	public AutocompleteProcessing(AutoCompleteTextFieldW field) {
		this.field = field;
	}

	@Override
	public void setFocus(boolean focus) {
		if (field == null) {
			return;
		}

		field.setFocus(focus);

	}

	@Override
	public void setKeyBoardModeText(boolean b) {
		field.setKeyBoardModeText(b);
	}

	@Override
	public void onEnter() {
		NativeEvent event = Document.get().createKeyDownEvent(false, false,
				false, false, ENTER);
		field.getTextField().onBrowserEvent(Event.as(event));

		event = Document.get().createKeyPressEvent(false, false, false, false,
				ENTER);
		field.getTextField().onBrowserEvent(Event.as(event));

		event = Document.get().createKeyUpEvent(false, false, false, false,
				ENTER);
		field.getTextField().onBrowserEvent(Event.as(event));
	}

	@Override
	public void onBackSpace() {
		field.onBackSpace();

		}

	@Override
	public void insertString(String text) {

		field.insertString(text);
		if (text.startsWith("(")) {
			// moves inside the brackets
			onArrow(ArrowType.left);
		} else if (text.equals(KeyboardConstants.A_POWER_X)) {
			field.insertString("^");
		} else if (text.equals("nroot")) {
			field.insertString("()");
			onArrow(ArrowType.left);
		}
		}

	@Override
	public void onArrow(ArrowType type) {

		int caretPos = field.getCaretPosition();
		switch (type) {
		case left:
			if (caretPos > 0)
				field.setCaretPosition(caretPos - 1);
			break;
		case right:
			if (caretPos < field.getText().length()) {
				field.setCaretPosition(caretPos + 1);
			}
			break;
			}

		}


	@Override
	public boolean resetAfterEnter() {
		return false;
	}

	public void scrollCursorIntoView() {
		// TODO Auto-generated method stub
	}

	public void updateForNewLanguage(KeyboardLocale localization) {
		// overridden for RTI
	}
}
