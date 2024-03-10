package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Exports image.
 */
public class ExportImage extends DefaultMenuAction<Void> {

	@Override
	public void execute(Void item, AppWFull app) {
		app.getSelectionManager().clearSelectedGeos();
		app.getActiveEuclidianView().getEuclidianController().widgetsToBackground();
		app.getDialogManager().showExportImageDialog(null);
	}
}
