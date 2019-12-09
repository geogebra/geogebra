package org.geogebra.common.gui.menu;

import java.util.List;

/**
 * A menu item that contains a list of menu items as submenu.
 */
public interface SubmenuItem extends MenuItem {

	/**
	 * Get the submenu items.
	 *
	 * @return items
	 */
	List<ActionableItem> getItems();
}
