package org.geogebra.web.html5.gui.accessibility;

/**
 * Adapter for tabbing to the main controls (menu, AV)
 */
public interface SideBarAccessibilityAdapter {

	/** Sets focus to Burger menu */
	void focusMenu();

	/**
	 * Sets focus to AV Input
	 * 
	 * @param force
	 *            force to open AV tab if not active
	 * @param forceFade
	 *            force fade during animation
	 * @return if input can be focused.
	 */
	boolean focusInput(boolean force, boolean forceFade);

}
