package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Exports PDF.
 */
public class DownloadPdfAction extends DefaultMenuAction<Void> {

	private AppWFull app;
	private ImageExporter imageExporter;

	/**
	 * @param app app
	 */
	public DownloadPdfAction(AppWFull app) {
		this.app = app;
		imageExporter = new ImageExporter(app, "pdf");
	}

	@Override
	public void execute(Void item, AppWFull app) {
		imageExporter.export(getUrl());
	}

	private String getUrl() {
		return app.getGgbApi().exportPDF(1, null, null);
	}
}
