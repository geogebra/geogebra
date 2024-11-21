package org.geogebra.web.full.util.keyboard;

import org.geogebra.keyboard.web.KeyboardConstants;
import org.geogebra.keyboard.web.KeyboardListener;
import org.geogebra.web.full.gui.util.ScriptArea;
import org.geogebra.web.html5.Browser;

/**
 * Connector for keyboard and scripting editor
 */
public class ScriptAreaProcessing implements KeyboardListener {

	private ScriptArea field;

	/**
	 * @param field
	 *            scripting editor
	 */
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
		int cursorPosition = field.getCursorPos();
		String text = field.getText();
		String firstHalf = text.substring(0, cursorPosition);
		String secondHalf = text.substring(cursorPosition + field.getSelectionLength());
		field.setText(firstHalf + "\r" + secondHalf);
		field.setCursorPos(cursorPosition + 1);
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

		if (start > -1) {
			// cursor not at the beginning of text -> delete something
			if (Browser.isAndroid()) {
				field.removeDummyCursor();
			}
			String oldText = field.getText();
			String newText = oldText.substring(0, start)
					+ oldText.substring(end);
			field.setText(newText);
			field.setCursorPos(start);
			if (Browser.isAndroid()) {
				field.addDummyCursor();
			}
		}

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
	public void insertString(String text) {
		field.insertString(text);
		if (text.startsWith("(") || text.startsWith("{")
				|| text.startsWith("[")) {
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
	public boolean isSVCell() {
		return false;
	}

	@Override
	public void endEditing() {
		// not needed
	}

	@Override
	public ScriptArea getField() {
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
