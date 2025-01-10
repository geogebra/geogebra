package org.geogebra.common.gui.menu;

/**
 * A menu item that contains a list of menu items as submenu.
 */
public interface SubmenuItem extends MenuItem {

	/**
	 * Get the submenu item group.
	 *
	 * @return group
	 */
	MenuItemGroup getGroup();

	/**
	 * Text shown at the bottom of panel
	 * (e.g. version number in case of Help and Feedback submenu)
	 *
	 * @return bottom text
	 */
	String getBottomText();
}
