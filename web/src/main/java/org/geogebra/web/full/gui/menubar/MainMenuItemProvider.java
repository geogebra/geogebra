package org.geogebra.web.full.gui.menubar;

import java.util.ArrayList;

/**
 * Action provider for the main menu
 */
public interface MainMenuItemProvider {

	/**
	 * Add app-specific menus to the list
	 *
	 * @param menus
	 *            menu list
	 */
	void addMenus(ArrayList<Submenu> menus);

	/**
	 * @return whether to add signin menu
	 */
	boolean hasSigninMenu();

}
