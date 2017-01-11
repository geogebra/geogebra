package org.geogebra.web.web.util.keyboard;

import org.geogebra.common.main.KeyboardLocale;
import org.geogebra.web.keyboard.KeyboardConstants;
import org.geogebra.web.keyboard.KeyboardListener;
import org.geogebra.web.web.gui.util.ScriptArea;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;

public class ScriptAreaProcessing implements KeyboardListener {

	private ScriptArea field;

	public ScriptAreaProcessing(ScriptArea field) {
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
		// TODO: why don't work
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
	public void onArrow(ArrowType type) {
		int cursorPos = field.getCursorPos();
		switch (type) {
		case left:
			if (cursorPos > 0) {
				field.setCursorPos(cursorPos - 1);
			}
			break;
		case right:
			if (cursorPos < field.getText().length()) {
				field.setCursorPos(cursorPos + 1);
			}
			break;
		}
	}

	@Override
	public void insertString(String text) {
		field.insertString(text);
		if (text.startsWith("(")) {
			// moves inside the brackets
			onArrow(ArrowType.left);
		} else if (text.equals(KeyboardConstants.A_POWER_X)) {
			field.insertString("^");
		} else if ("nroot".equals(text)) {
			field.insertString("()");
			onArrow(ArrowType.left);
		}

	}

	@Override
	public void scrollCursorIntoView() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean resetAfterEnter() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateForNewLanguage(KeyboardLocale localization) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setKeyBoardModeText(boolean text) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSVCell() {
		return false;
	}

	@Override
	public void endEditing() {
		// TODO Auto-generated method stub

	}

	@Override
	public ScriptArea getField() {
		return field;
	}

}
