package org.geogebra.common.gui.menu;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A model describing a menu item group. Each group can have
 * an optional title, and has at least a single menu item.
 */
public interface MenuItemGroup {

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
