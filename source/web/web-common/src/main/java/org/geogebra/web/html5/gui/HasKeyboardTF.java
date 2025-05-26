package org.geogebra.web.html5.gui;

import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.gwtproject.event.dom.client.BlurHandler;
import org.gwtproject.event.dom.client.FocusHandler;
import org.gwtproject.event.dom.client.KeyPressHandler;
import org.gwtproject.event.shared.HandlerRegistration;

/**
 * Interface for text fields which can use onscreen keyboard
 *
 */
public interface HasKeyboardTF extends MathKeyboardListener {

	/** add dummy cursor when editing starts */
	void startOnscreenKeyboardEditing();

	/** remove dummy cursor when editing ends */
	void endOnscreenKeyboardEditing();

	/** add a dummy cursor to a text field */
	void addDummyCursor();

	/**
	 * Remove the dummy cursor
	 * 
	 * @return the position of removed cursor
	 */
	int removeDummyCursor();

	/**
	 * @param readonly
	 *            true if textfield is readonly
	 */
	void setReadOnly(boolean readonly);

	/**
	 * get current position of cursor
	 * 
	 * @return position
	 */
	int getCursorPos();

	/**
	 * set position of cursor
	 * 
	 * @param pos
	 *            position
	 */
	void setCursorPos(int pos);

	/**
	 * set text of text field
	 * 
	 * @param text
	 *            text to set
	 */
	void setValue(String text);

	/**
	 * @param handler
	 *            FocusHandler
	 * @return HandlerRegistration
	 */
	HandlerRegistration addFocusHandler(FocusHandler handler);

	/**
	 * @param handler
	 *            BlurHandler
	 * @return HandlerRegistration
	 */
	HandlerRegistration addBlurHandler(BlurHandler handler);

	/**
	 * Adds key handler to the tetxtfield
	 *
	 * @param handler
	 *            Keypresshandler
	 * @return registration
	 */
	public HandlerRegistration addKeyPressHandler(KeyPressHandler handler);

	/**
	 * Handle the backspace key.
	 */
	void onBackSpace();
}
