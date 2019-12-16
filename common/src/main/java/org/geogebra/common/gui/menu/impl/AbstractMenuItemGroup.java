package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.gui.menu.MenuItemGroup;

import javax.annotation.Nullable;

abstract class AbstractMenuItemGroup implements MenuItemGroup {

	private String title;

	AbstractMenuItemGroup() {
		this(null);
	}

	AbstractMenuItemGroup(String title) {
		this.title = title;
	}

	@Nullable
	@Override
	public String getTitle() {
		return title;
	}
}
