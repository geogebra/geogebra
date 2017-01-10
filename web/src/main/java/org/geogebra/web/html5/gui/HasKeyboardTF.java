package org.geogebra.web.html5.gui;

import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;

/**
 * Interface for textfields, which has keyboard
 *
 */
public interface HasKeyboardTF extends MathKeyboardListener {

	public void startOnscreenKeyboardEditing();

	public void endOnscreenKeyboardEditing();

	public void addDummyCursor();

	/**
	 * Remove the dummy cursor
	 * 
	 * @return the position of removed cursor
	 */
	public int removeDummyCursor();

	public void setFocus(boolean b);

}
