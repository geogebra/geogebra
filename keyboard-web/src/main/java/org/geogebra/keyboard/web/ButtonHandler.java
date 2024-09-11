package org.geogebra.keyboard.web;

import org.geogebra.common.euclidian.event.PointerEventType;

/**
 * keyboard button handler interface
 *
 */
public interface ButtonHandler {
	/**
	 * processes the click on one of the keyboard buttons
	 * 
	 * @param btn
	 *            the button that was clicked
	 * @param type
	 *            the type of click (mouse vs. touch)
	 */
	void onClick(BaseKeyboardButton btn, PointerEventType type);

	/**
	 * Stop keyboard repeating command
	 */
	void buttonPressEnded();

}
