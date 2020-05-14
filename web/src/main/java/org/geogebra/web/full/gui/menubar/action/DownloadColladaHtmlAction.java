package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Exports collada HTML.
 */
public class DownloadColladaHtmlAction extends DefaultMenuAction<Void> {

	@Override
	public void execute(Void item, AppWFull app) {
		app.exportCollada(true);
	}
}
