package org.geogebra.web.full.gui.menu.action;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.web.full.gui.menubar.action.ClearAllAction;
import org.geogebra.web.full.gui.menubar.action.ShowSettingsAction;
import org.geogebra.web.full.gui.menubar.action.StartAppAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Builds MenuActionHandler for the Scientific Calculator.
 */
public class ScientificMenuActionHandlerFactory implements MenuActionHandlerFactory {

	private AppWFull app;

	public ScientificMenuActionHandlerFactory(AppWFull app) {
		this.app = app;
	}

	@Override
	public MenuActionHandler create() {
		DefaultMenuActionHandler actionHandler = new DefaultMenuActionHandler(app);
		actionHandler.setMenuAction(Action.CLEAR_CONSTRUCTION, new ClearAllAction(false));
		actionHandler.setMenuAction(Action.SHOW_SETTINGS, new ShowSettingsAction());
		actionHandler.setMenuAction(
				Action.START_GRAPHING, StartAppAction.create(app, "graphing"));
		actionHandler.setMenuAction(
				Action.START_GEOMETRY, StartAppAction.create(app, "geometry"));
		actionHandler.setMenuAction(
				Action.START_GRAPHING_3D, StartAppAction.create(app, "3d"));
		actionHandler.setMenuAction(
				Action.START_CAS_CALCULATOR, StartAppAction.create(app, "calculator"));
		actionHandler.setMenuAction(
				Action.START_CLASSIC, StartAppAction.create(app, "classic"));
		actionHandler.setMenuAction(
				Action.START_CAS_CALCULATOR, StartAppAction.create(app, "cas"));
		return actionHandler;
	}
}
