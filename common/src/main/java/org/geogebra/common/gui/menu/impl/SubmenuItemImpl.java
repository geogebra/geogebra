package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.gui.menu.ActionableItem;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.SubmenuItem;

import java.util.Arrays;
import java.util.List;

class SubmenuItemImpl extends AbtractMenuItem implements SubmenuItem {

	private List<ActionableItem> items;

	SubmenuItemImpl(Icon icon, String label, ActionableItem... items) {
		super(icon, label);
		this.items = Arrays.asList(items);
	}

	@Override
	public List<ActionableItem> getItems() {
		return items;
	}
}
