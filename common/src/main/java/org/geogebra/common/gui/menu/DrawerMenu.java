package org.geogebra.common.gui.menu;

import java.io.Serializable;
import java.util.List;

/**
 * A model describing a drawer menu. Each menu consists of a title and groups of menu items.
 */
public interface DrawerMenu extends Serializable {

	/**
	 * Get the title of the drawer menu.
	 *
	 * @return title
	 */
	String getTitle();

	/**
	 * Get the menu item groups.
	 *
	 * @return groups
	 */
	List<MenuItemGroup> getMenuItemGroups();
}
