package org.geogebra.web.keyboard;

import org.geogebra.common.main.KeyboardLocale;

/**
 * interface for classes that can receive input from the
 * {@link OnScreenKeyBoardBase}
 */
public interface KeyboardListener {
	
	/**
	 * arrow keys of the keyboard
	 */
	public enum ArrowType {
		left,
		right
	}
	
	enum State {
		empty, autoCompleteTextField, gTextBox, equationEditorListener, inputTreeItem, other;
	}

	/** ASCII */
	public static final int BACKSPACE = 8;
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

	boolean resetAfterEnter();

	/**
	 * change language specific notations
	 * 
	 * @param loc
	 */
	void updateForNewLanguage(KeyboardLocale localization);

	public void setKeyBoardModeText(boolean text);
}
