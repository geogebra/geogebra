package org.geogebra.web.keyboard;

import org.geogebra.common.main.Localization;

/**
 * interface for classes that can receive input from the
 * {@link OnScreenKeyBoardBase}
 */
public interface KeyBoardProcessable {
	
	/**
	 * arrow keys of the keyboard
	 */
	public enum ArrowType {
		left,
		right
	}
	
	enum State {
		empty, autoCompleteTextField, gTextBox, equationEditorListener, newRadioButtonTreeItem, other;
	}

	/** ASCII */
	public static final int BACKSPACE = 8;
	public static final int ENTER = '\r'; // 13;

	void setFocus(boolean focus);

	void onEnter();
	
	void onBackSpace();
	
	void onArrow(ArrowType type);
	
	void insertString(String text);
	
	void scrollCursorIntoView();

	boolean resetAfterEnter();

	void updateForNewLanguage(Localization localization);
}
