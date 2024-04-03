package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Exports collada HTML.
 */
public class DownloadColladaHtmlAction extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(AppWFull app) {
		app.exportCollada(true);
	}
}
