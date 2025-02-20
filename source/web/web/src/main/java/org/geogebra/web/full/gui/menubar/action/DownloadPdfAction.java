package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.kernel.commands.CmdExportImage;
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
		app.getGgbApi().exportPDF(1, null, this::exportImage, null, CmdExportImage.PDF_DPI);
	}
}
