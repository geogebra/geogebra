package org.geogebra.keyboard.web;

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
		right,
		/**
		 * up arrow
		 */
		up,
		/**
		 * down arrow
		 */
		down
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

	/**
	 * Ans key pressed
	 */
	void ansPressed();

	/**
	 * Requests the ans key.
	 *
	 * @return true if the listener requests the 'ans' key
	 */
	boolean requestsAns();
}
