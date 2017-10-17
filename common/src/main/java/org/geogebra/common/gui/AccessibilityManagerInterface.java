package org.geogebra.common.gui;

/**
 * 
 * @author Laszlo
 *
 */
public interface AccessibilityManagerInterface {

	/**
	 * Focus next screen element
	 * 
	 * @param source
	 *            The source element to focus from.
	 */
	public void focusNext(Object source);

	/**
	 * Focus previous screen element
	 * 
	 * @param source
	 *            The source element to focus from.
	 */
	public void focusPrevious(Object source);

	/** Focus main menu */
	public void focusMenu();

	/**
	 * 
	 * @return true if tab is currently on EV geo elements.
	 */
	public boolean isTabOverGeos();

	public void setTabOverGeos(boolean b);

	public boolean hasTabModeChanged(boolean shift);

	public void setTabFromGeosToGui();
}
