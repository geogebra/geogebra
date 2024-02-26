package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.gui.dialog.ExportImageDialog;
import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

/**
 * Exports PNG.
 */
public class DownloadPngAction extends DefaultMenuAction<AppWFull> {

	private AppWFull app;
	private ImageExporter imageExporter;

	/**
	 * @param app app
	 */
	public DownloadPngAction(AppWFull app) {
		this.app = app;
		imageExporter = new ImageExporter(app, "png");
	}

	@Override
	public void execute(AppWFull app) {
		app.getActiveEuclidianView().getEuclidianController().widgetsToBackground();
		imageExporter.export(getUrl());
	}

	private String getUrl() {
		return ExportImageDialog.getExportDataURL(app);
	}
}
