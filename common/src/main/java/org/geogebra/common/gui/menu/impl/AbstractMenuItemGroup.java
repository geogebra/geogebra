package org.geogebra.common.gui.menu.impl;

import javax.annotation.Nullable;

import org.geogebra.common.gui.menu.MenuItemGroup;

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
