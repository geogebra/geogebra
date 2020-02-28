package org.geogebra.web.full.gui.menu;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.menu.ActionableItem;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.SubmenuItem;
import org.geogebra.web.full.gui.menu.action.MenuActionHandler;

class MenuActionRouter {

	private MenuActionHandler menuActionHandler;

	public MenuActionRouter(MenuActionHandler menuActionHandler) {
		this.menuActionHandler = menuActionHandler;
	}

	void handleMenuItem(MenuItem menuItem) {
		if (menuItem instanceof ActionableItem) {
			handleAction(((ActionableItem) menuItem).getAction());
		} else if (menuItem instanceof SubmenuItem) {
			// ToDo
		}
	}

	void handleAction(Action action) {
		menuActionHandler.executeMenuAction(action);
	}
}
