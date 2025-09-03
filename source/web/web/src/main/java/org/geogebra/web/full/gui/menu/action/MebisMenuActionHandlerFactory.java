package org.geogebra.web.full.gui.menu.action;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.web.full.gui.menubar.action.OpenFileActionMebis;
import org.geogebra.web.full.gui.menubar.action.OpenOfflineFileAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Builds MenuActionHandler instances for Mebis Board.
 */
public class MebisMenuActionHandlerFactory implements MenuActionHandlerFactory {

	private DefaultMenuActionHandlerFactory defaultMenuActionHandlerFactory;

	public MebisMenuActionHandlerFactory(AppWFull app) {
		defaultMenuActionHandlerFactory = new DefaultMenuActionHandlerFactory(app);
	}

	@Override
	public DefaultMenuActionHandler create() {
		DefaultMenuActionHandler actionHandler = defaultMenuActionHandlerFactory.create();
		actionHandler.setMenuAction(Action.SHOW_SEARCH_VIEW, new OpenFileActionMebis());
		actionHandler.setMenuAction(Action.OPEN_OFFLINE_FILE, new OpenOfflineFileAction());

		return actionHandler;
	}
}
