package org.geogebra.web.web.util.keyboard;

import org.geogebra.common.main.KeyboardLocale;
import org.geogebra.web.keyboard.KeyboardConstants;
import org.geogebra.web.keyboard.KeyboardListener;
import org.geogebra.web.web.gui.util.ScriptArea;

public class ScriptAreaProcessing implements KeyboardListener {

	private ScriptArea field;

	public ScriptAreaProcessing(ScriptArea field) {
		this.field = field;
	}

	public void setFocus(boolean focus) {
		if (field == null) {
			return;
		}

		field.setFocus(focus);
	}

	public void onEnter() {
		// TODO Auto-generated method stub

	}

	public void onBackSpace() {
		// TODO Auto-generated method stub

	}

	public void onArrow(ArrowType type) {
		// TODO Auto-generated method stub

	}

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

	public void scrollCursorIntoView() {
		// TODO Auto-generated method stub

	}

	public boolean resetAfterEnter() {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateForNewLanguage(KeyboardLocale localization) {
		// TODO Auto-generated method stub

	}

	public void setKeyBoardModeText(boolean text) {
		// TODO Auto-generated method stub

	}

	public boolean isSVCell() {
		// TODO Auto-generated method stub
		return false;
	}

	public void endEditing() {
		// TODO Auto-generated method stub

	}

	public Object getField() {
		// TODO Auto-generated method stub
		return null;
	}

}
