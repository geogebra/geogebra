package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.gui.menu.MenuItem;

import java.util.Arrays;
import java.util.List;

class MenuItemGroupImpl extends AbstractMenuItemGroup {

	private List<MenuItem> menuItems;

	MenuItemGroupImpl(MenuItem... menuItems) {
		this(null, menuItems);
	}

	MenuItemGroupImpl(String title, MenuItem... menuItems) {
		super(title);
		this.menuItems = Arrays.asList(menuItems);
	}

	@Override
	public List<MenuItem> getMenuItems() {
		return menuItems;
	}
}
