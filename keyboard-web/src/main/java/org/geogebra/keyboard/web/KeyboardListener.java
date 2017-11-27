package org.geogebra.keyboard.web;

import org.geogebra.web.html5.gui.util.KeyboardLocale;

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
	public static final int BACKSPACE = 8;
	/**
	 * enter
	 */
	public static final int ENTER = '\r'; // 13;
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
	void updateForNewLanguage(KeyboardLocale localization);

	/**
	 * @param text
	 *            true if text
	 */
	public void setKeyBoardModeText(boolean text);

	/**
	 * @return true if spreadsheet view
	 */
	public boolean isSVCell();

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
