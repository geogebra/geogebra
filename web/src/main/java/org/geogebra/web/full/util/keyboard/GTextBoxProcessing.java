package org.geogebra.web.full.util.keyboard;

import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.user.client.Event;

/**
 * Connector for keyboard and simple textbox
 */
public class GTextBoxProcessing implements KeyboardListener {
	private GTextBox field;

	/**
	 * @param field
	 *            textbox
	 */
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

		if (start >= 0) {
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
	 *            text to be inserted
	 */
	private void insertAtEnd(String text) {
		int caretPos = field.getCursorPos();
		String oldText = field.getText();
		String newText = oldText.substring(0, caretPos) + text
				+ oldText.substring(caretPos);
		field.setText(newText);
		field.setCursorPos(caretPos + text.length());
	}

	@Override
	public void onArrow(ArrowType type) {

		int cursorPos = field.getCursorPos();

		if (type == ArrowType.left && cursorPos > 0) {
				field.setCursorPos(cursorPos - 1);
		} else if (type == ArrowType.right
				&& cursorPos < field.getText().length()) {
				field.setCursorPos(cursorPos + 1);
		}

	}

	@Override
	public void setKeyBoardModeText(boolean text) {
		// overridden for RTI
	}

	@Override
	public boolean isSVCell() {
		return false;
	}

	@Override
	public void endEditing() {
		// not needed
	}

	@Override
	public GTextBox getField() {
		return field;
	}

	@Override
	public void onKeyboardClosed() {
		// not needed
	}

	@Override
	public void ansPressed() {
		// not needed
	}

	@Override
	public boolean requestsAns() {
		return false;
	}
}
