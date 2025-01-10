package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Shows the print preview.
 */
public class ShowPrintPreviewAction extends DefaultMenuAction<AppWFull> {

	@Override
	public void execute(AppWFull app) {
		app.getDialogManager().showPrintPreview();
	}
}
