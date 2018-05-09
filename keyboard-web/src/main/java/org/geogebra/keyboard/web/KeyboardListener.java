package org.geogebra.keyboard.web;

import org.geogebra.common.main.Localization;

/**
 * interface for classes that can receive input from the {@link TabbedKeyboard}
 */
public interface KeyboardListener {
	/**
	 * arrow keys of the keyboard
	 */
	public enum ArrowType {
		/**
		 * left arrow
		 */
		left,
		/**
		 * right arrow
		 */
		right
	}

	/** ASCII */
	int BACKSPACE = 8;
	/**
	 * enter
	 */
	int ENTER = '\r'; // 13;
	/**
	 * Focus/Blur the text field
	 * 
	 * @param focus
	 *            true: focus; false: blur
	 */
	void setFocus(boolean focus);

	/**
	 * simulates an enter key event
	 */
	void onEnter();

	/**
	 * simulates a backspace key event
	 */
	void onBackSpace();

	/**
	 * simulates arrow events
	 * 
	 * @param type
	 *            {@link ArrowType}
	 */
	void onArrow(ArrowType type);

	/**
	 * Inserts the given text at the caret position
	 * 
	 * @param text
	 *            text to be inserted
	 */
	void insertString(String text);

	/**
	 * Method just used for RadioButtonTreeItem for now
	 */
	void scrollCursorIntoView();

	/**
	 * @return false
	 */
	boolean resetAfterEnter();

	/**
	 * change language specific notations
	 * @param localization
	 *            localization
	 */
	void updateForNewLanguage(Localization localization);

	/**
	 * @param text
	 *            true if text
	 */
	void setKeyBoardModeText(boolean text);

	/**
	 * @return true if spreadsheet view
	 */
	boolean isSVCell();

	/**
	 * end editing
	 */
	void endEditing();

	/**
	 * @return process field
	 */
	Object getField();

	/**
	 * on keyboard closed
	 */
	void onKeyboardClosed();
}
