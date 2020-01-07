package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.gui.menu.MenuItem;

import java.util.Arrays;
import java.util.List;

class MenuItemGroupImpl extends AbstractMenuItemGroup {

	private List<MenuItem> menuItems;

	MenuItemGroupImpl(MenuItem... menuItems) {
		this(Arrays.asList(menuItems));
	}

	MenuItemGroupImpl(List<MenuItem> menuItems) {
		this(null, menuItems);
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
