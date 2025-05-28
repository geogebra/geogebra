package org.geogebra.common.gui.menu.impl;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.menu.MenuItemGroup;

abstract class AbstractMenuItemGroup implements MenuItemGroup {

	private String title;

	AbstractMenuItemGroup() {
		this(null);
	}

	AbstractMenuItemGroup(String title) {
		this.title = title;
	}

	@Override
	public @CheckForNull String getTitle() {
		return title;
	}
}
