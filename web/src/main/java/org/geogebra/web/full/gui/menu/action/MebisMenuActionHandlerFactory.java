package org.geogebra.web.full.gui.menu.action;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.web.full.gui.menubar.action.OpenFileActionMebis;
import org.geogebra.web.full.main.AppWFull;

/**
 * Builds MenuActionHandler instances for Mebis Tafel.
 */
public class MebisMenuActionHandlerFactory implements MenuActionHandlerFactory {

	private AppWFull app;

	public MebisMenuActionHandlerFactory(AppWFull app) {
		this.app = app;
	}

	@Override
	public MenuActionHandler create() {
		DefaultMenuActionHandler actionHandler = new DefaultMenuActionHandler(app);
		actionHandler.setMenuAction(Action.SHOW_SEARCH_VIEW, new OpenFileActionMebis());

		return actionHandler;
	}
}
