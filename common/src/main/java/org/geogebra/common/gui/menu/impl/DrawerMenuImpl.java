package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.gui.menu.DrawerMenu;
import org.geogebra.common.gui.menu.MenuItemGroup;

import java.util.Arrays;
import java.util.List;

class DrawerMenuImpl implements DrawerMenu {

	private String title;
	private List<MenuItemGroup> groups;

	DrawerMenuImpl(String title, MenuItemGroup... groups) {
		this(title, Arrays.asList(groups));
	}

	DrawerMenuImpl(String title, List<MenuItemGroup> groups) {
		this.title = title;
		this.groups = groups;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public List<MenuItemGroup> getMenuItemGroups() {
		return groups;
	}
}
