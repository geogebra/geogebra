package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Shows the save file chooser in browsers that support the file system access API,
 * downloads directly in other browsers.
 */
public class SaveLocalAction extends DefaultMenuAction<AppWFull> {
	@Override
	public void execute(AppWFull app) {
		app.getSaveController().showLocalSaveDialog();
	}

}
