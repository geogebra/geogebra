package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.gui.menu.ActionableItem;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.gui.menu.SubmenuItem;

class SubmenuItemImpl extends AbtractMenuItem implements SubmenuItem {

	private MenuItemGroup group;

	SubmenuItemImpl(Icon icon, String label, ActionableItem... items) {
		super(icon, label);
		group = new MenuItemGroupImpl(items);
	}

	@Override
	public MenuItemGroup getGroup() {
		return group;
	}
}
