package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Exports collada DAE.
 */
public class DownloadColladaDaeAction extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(AppWFull app) {
		app.exportCollada(false);
	}
}
