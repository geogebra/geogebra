package org.geogebra.web.html5.gui;


/**
 * Interface for textfields, which has keyboard
 *
 */
public interface HasKeyboardTF {

	public void startOnscreenKeyboardEditing();

	public void endOnscreenKeyboardEditing();

	public void addDummyCursor();

	public void removeDummyCursor();

	public void setFocus(boolean b);

}
