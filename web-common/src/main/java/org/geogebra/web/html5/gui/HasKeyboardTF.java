package org.geogebra.web.html5.gui;

import org.geogebra.web.html5.gui.util.MathKeyboardListener;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Interface for textfields, which has keyboard
 *
 */
public interface HasKeyboardTF extends MathKeyboardListener {

	/** add dummy cursor when editing starts */
	void startOnscreenKeyboardEditing();

	/** remove dummy cursor when editing ends */
	void endOnscreenKeyboardEditing();

	/** add a dummy cursor to a textfield */
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
	 * set text of textfield
	 * 
	 * @param text
	 *            text to set
	 */
	void setValue(String text);

	/**
	 * get text of textfield
	 * 
	 * @return text
	 */
	String getValue();

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

	void onBackSpace();
}
