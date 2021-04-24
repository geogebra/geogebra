package org.geogebra.web.full.gui.menu.action;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.web.full.gui.menubar.action.SwitchCalculatorAction;
import org.geogebra.web.full.main.AppWFull;

public class SuiteMenuActionHandlerFactory implements MenuActionHandlerFactory {
	private final DefaultMenuActionHandlerFactory factory;

	public SuiteMenuActionHandlerFactory(AppWFull app) {
		factory = new DefaultMenuActionHandlerFactory(app);
	}

	@Override
	public MenuActionHandler create() {
		DefaultMenuActionHandler actionHandler = factory.create();
		actionHandler.setMenuAction(Action.SWITCH_CALCULATOR, new SwitchCalculatorAction());
		return actionHandler;
	}
}