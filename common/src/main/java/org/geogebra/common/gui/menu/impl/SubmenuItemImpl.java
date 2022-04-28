package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.gui.menu.ActionableItem;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.gui.menu.SubmenuItem;

class SubmenuItemImpl extends AbtractMenuItem implements SubmenuItem {

	private MenuItemGroup group;
	private String bottomText;

	SubmenuItemImpl(Icon icon, String label, String bottomText, ActionableItem... items) {
		super(icon, label);
		this.bottomText = bottomText;
		group = new MenuItemGroupImpl(items);
	}

	@Override
	public MenuItemGroup getGroup() {
		return group;
	}

	@Override
	public String getBottomText() {
		return bottomText;
	}
}
