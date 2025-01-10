package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.menu.ActionableItem;
import org.geogebra.common.gui.menu.Icon;

class ActionableItemImpl extends AbstractMenuItem implements ActionableItem {

	private final Action action;

	ActionableItemImpl(String label, Action action) {
		this(null, label, action);
	}

	ActionableItemImpl(Icon icon, String label, Action action) {
		super(icon, label);
		this.action = action;
	}

	@Override
	public Action getAction() {
		return action;
	}
}
