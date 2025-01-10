package org.geogebra.common.gui.menu.impl;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.gui.menu.MenuItem;

/**
 * Implementation of AbstractMenuItemGroup.
 */
public class MenuItemGroupImpl extends AbstractMenuItemGroup {

	private List<MenuItem> menuItems;

	public MenuItemGroupImpl(List<MenuItem> menuItems) {
		this(null, menuItems);
	}

	MenuItemGroupImpl(MenuItem... menuItems) {
		this(Arrays.asList(menuItems));
	}

	MenuItemGroupImpl(String title, MenuItem... menuItems) {
		this(title, Arrays.asList(menuItems));
	}

	MenuItemGroupImpl(String title, List<MenuItem> menuItems) {
		super(title);
		this.menuItems = menuItems;
	}

	@Override
	public List<MenuItem> getMenuItems() {
		return menuItems;
	}
}
