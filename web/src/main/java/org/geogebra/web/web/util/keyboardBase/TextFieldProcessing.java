package org.geogebra.web.web.util.keyboardBase;

import org.geogebra.common.main.Localization;

public interface TextFieldProcessing {
	
	/**
	 * arrow keys of the keyboard
	 */
	public enum ArrowType {
		left,
		right
	}
	
	void setFocus(boolean focus);

	void onEnter();
	
	void onBackSpace();
	
	void onArrow(ArrowType type);
	
	void insertString(String text);
	
	void scrollCursorIntoView();

	boolean resetAfterEnter();

	void updateForNewLanguage(Localization localization);
}
