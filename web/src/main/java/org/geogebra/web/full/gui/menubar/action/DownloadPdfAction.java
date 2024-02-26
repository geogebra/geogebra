package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Exports PDF.
 */
public class DownloadPdfAction extends DefaultMenuAction<AppWFull> {

	private ImageExporter imageExporter;

	/**
	 * @param app app
	 */
	public DownloadPdfAction(AppWFull app) {
		imageExporter = new ImageExporter(app, "pdf");
	}

	@Override
	public void execute(AppWFull app) {
		app.getGgbApi().exportPDF(1, null, (pdf) -> {
			imageExporter.export(pdf);
		}, null);
	}
}
