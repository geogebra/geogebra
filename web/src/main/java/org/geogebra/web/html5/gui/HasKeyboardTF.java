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
	public void startOnscreenKeyboardEditing();

	/** remove dummy cursor when editing ends */
	public void endOnscreenKeyboardEditing();

	/** add a dummy cursor to a textfield */
	public void addDummyCursor();

	/**
	 * Remove the dummy cursor
	 * 
	 * @return the position of removed cursor
	 */
	public int removeDummyCursor();

	/**
	 * @param dummyCursor
	 *            true if dummyCursor active
	 */
	public void toggleDummyCursor(boolean dummyCursor);

	/**
	 * @param b
	 *            set focus true or false
	 */
	public void setFocus(boolean b);

	/**
	 * @param readonly
	 *            true if textfield is readonly
	 */
	public void setReadOnly(boolean readonly);

	/**
	 * get current position of cursor
	 * 
	 * @return position
	 */
	public int getCursorPos();

	/**
	 * set position of cursor
	 * 
	 * @param pos
	 *            position
	 */
	public void setCursorPos(int pos);

	/**
	 * set text of textfield
	 * 
	 * @param text
	 *            text to set
	 */
	public void setValue(String text);

	/**
	 * get text of textfield
	 * 
	 * @return text
	 */
	public String getValue();

	/**
	 * @param handler
	 *            FocusHandler
	 * @return HandlerRegistration
	 */
	public HandlerRegistration addFocusHandler(FocusHandler handler);

	/**
	 * @param handler
	 *            BlurHandler
	 * @return HandlerRegistration
	 */
	public HandlerRegistration addBlurHandler(BlurHandler handler);

}
