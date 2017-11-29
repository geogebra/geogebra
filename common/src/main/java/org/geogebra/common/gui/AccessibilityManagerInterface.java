package org.geogebra.common.gui;

import org.geogebra.common.kernel.geos.GeoElement;

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
	 * Focus algebra input
	 * 
	 * @param force
	 *            force to open AV tab if not active
	 * 
	 * @return algebra input focused
	 */
	public boolean focusInput(boolean force);

	/**
	 * 
	 * @return true if tab is currently on EV geo elements.
	 */
	public boolean isTabOverGeos();

	/**
	 * Determines if current tab press should exit selecting geos and go to GUI
	 * a element.
	 * 
	 * @param isShiftDown
	 *            Determines if shift key is pressed.
	 * @return true if tab should go to GUI.
	 */
	public boolean isCurrentTabExitGeos(boolean isShiftDown);

	/**
	 * Sets if tab is on geos currently.
	 * 
	 * @param b
	 *            to set.
	 * 
	 */
	public void setTabOverGeos(boolean b);

	public void focusGeo(GeoElement geo);
}
