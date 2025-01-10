package org.geogebra.common.gui.menu;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A model describing a menu item group. Each group can have
 * an optional title, and has at least a single menu item.
 */
public interface MenuItemGroup extends Serializable {

	/**
	 * Get the title of the menu item group.
	 *
	 * @return title
	 */
	@Nullable String getTitle();

	/**
	 * Get the menu items of this group.
	 *
	 * @return menu items
	 */
	List<MenuItem> getMenuItems();
}
