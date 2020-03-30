package org.geogebra.web.full.gui.menu.action;

import org.geogebra.common.gui.menu.Action;
import org.geogebra.web.full.gui.menubar.action.DownloadDefaultFormatAction;
import org.geogebra.web.full.gui.menubar.action.OpenFileActionMebis;
import org.geogebra.web.full.gui.menubar.action.OpenOfflineFileAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Builds MenuActionHandler instances for Mebis Tafel.
 */
public class MebisMenuActionHandlerFactory extends DefaultMenuActionHandlerFactory
		implements MenuActionHandlerFactory {

	private AppWFull app;

	public MebisMenuActionHandlerFactory(AppWFull app) {
		super(app);
	}

	@Override
	public DefaultMenuActionHandler create() {
		DefaultMenuActionHandler actionHandler = super.create();
		actionHandler.setMenuAction(Action.SHOW_SEARCH_VIEW, new OpenFileActionMebis());
		actionHandler.setMenuAction(Action.DOWNLOAD_GGS, new DownloadDefaultFormatAction());
		actionHandler.setMenuAction(Action.OPEN_OFFLINE_FILE, new OpenOfflineFileAction());

		return actionHandler;
	}
}
