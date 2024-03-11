package org.geogebra.web.full.gui.menu.action;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Implements handling of the menu actions.
 */
class DefaultMenuActionHandler implements MenuActionHandler {

	private AppWFull app;
	private Map<Action, MenuAction<AppWFull>> actionMap = new HashMap<>();

	/**
	 * Create a DefaultMenuActionHandler
	 * @param app app
	 */
	DefaultMenuActionHandler(AppWFull app) {
		this.app = app;
	}

	@Override
	public void executeMenuAction(Action action) {
		actionMap.get(action).execute(app);
	}

	void setMenuAction(Action action, MenuAction<AppWFull> menuAction) {
		actionMap.put(action, menuAction);
	}
}
