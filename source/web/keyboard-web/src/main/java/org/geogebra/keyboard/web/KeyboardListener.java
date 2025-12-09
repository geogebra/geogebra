/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
