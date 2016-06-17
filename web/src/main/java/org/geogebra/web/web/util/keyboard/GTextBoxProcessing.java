package org.geogebra.web.web.util.keyboard;

import org.geogebra.common.main.KeyboardLocale;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.keyboard.KeyboardListener;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;

public class GTextBoxProcessing implements KeyboardListener {
	GTextBox field;

	public GTextBoxProcessing(GTextBox field) {
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
	public void onEnter() {

		NativeEvent event2 = Document.get().createKeyDownEvent(false, false,
				false, false, ENTER);
		field.onBrowserEvent(Event.as(event2));
	}

	@Override
	public void onBackSpace() {
	
		int start = field.getCursorPos();
		int end = start + field.getSelectionLength();

		if (field.getSelectionLength() < 1) {
			// nothing selected -> delete character before cursor
			end = start;
			start--;
		}

		if (start > 0) {
			// cursor not at the beginning of text -> delete something
			String oldText = field.getText();
			String newText = oldText.substring(0, start)
					+ oldText.substring(end);
			field.setText(newText);
			field.setCursorPos(start);
		}
		
	}

	@Override
	public void insertString(String text) {

		insertAtEnd(text);
	}

	/**
	 * only for {@link GTextBox}
	 * 
	 * @param text
	 */
	private void insertAtEnd(String text) {
		String oldText = field.getText();
		int caretPos = field.getCursorPos();

		String newText = oldText.substring(0, caretPos) + text
				+ oldText.substring(caretPos);
		field.setText(newText);
		field.setCursorPos(caretPos + text.length());
	}

	@Override
	public void onArrow(ArrowType type) {

		int cursorPos = field.getCursorPos();
		switch (type) {
		case left:
			if (cursorPos > 0)
				field.setCursorPos(cursorPos - 1);
			break;
		case right:
			if (cursorPos < field.getText().length()) {
				field.setCursorPos(cursorPos + 1);
			}
			break;
		}

	}

	@Override
	public boolean resetAfterEnter() {
		// overridden for RTI
		return false;
	}

	public void setKeyBoardModeText(boolean text) {
		// overridden for RTI

	}

	public void scrollCursorIntoView() {
		// overridden for RTI

	}

	public void updateForNewLanguage(KeyboardLocale localization) {
		// overridden for RTI
	}
}
