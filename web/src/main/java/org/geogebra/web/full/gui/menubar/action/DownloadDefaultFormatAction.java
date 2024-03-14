package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Exports ggb or ggs format.
 */
public class DownloadDefaultFormatAction extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(AppWFull app) {
		app.getGuiManager().exportGGB();
	}
}
