package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.dialog.ExportImageDialog;
import org.geogebra.web.full.main.AppWFull;

/**
 * Exports PNG.
 */
public final class DownloadPngAction extends DownloadImageAction {

	/**
	 * @param app app
	 */
	public DownloadPngAction(AppWFull app) {
		super(app, "png");
	}

	@Override
	protected void export(AppWFull app) {
		app.getActiveEuclidianView().getEuclidianController().widgetsToBackground();
		exportImage(ExportImageDialog.getExportDataURL(app));
	}

}
