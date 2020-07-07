package org.geogebra.web.full.gui.menu.action;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.web.full.gui.menubar.action.ClearAllAction;
import org.geogebra.web.full.gui.menubar.action.ShowSettingsAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Builds MenuActionHandler for the Scientific Calculator.
 */
public class ScientificMenuActionHandlerFactory implements MenuActionHandlerFactory {

	private final DefaultMenuActionHandlerFactory factory;

	public ScientificMenuActionHandlerFactory(AppWFull app) {
		factory = new DefaultMenuActionHandlerFactory(app);
	}

	@Override
	public MenuActionHandler create() {
		DefaultMenuActionHandler actionHandler = factory.create();
		actionHandler.setMenuAction(Action.CLEAR_CONSTRUCTION, new ClearAllAction(false));
		actionHandler.setMenuAction(Action.SHOW_SETTINGS, new ShowSettingsAction());
		return actionHandler;
	}
}
