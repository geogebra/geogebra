package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.main.AppWFull;

/**
 * Exports PDF.
 */
public class DownloadPdfAction extends DownloadImageAction {

	public DownloadPdfAction(AppWFull app) {
		super(app, "pdf");
	}

	@Override
	protected void export(AppWFull app) {
		app.getGgbApi().exportPDF(1, null, (pdf) -> exportImage(pdf), null);
	}
}
