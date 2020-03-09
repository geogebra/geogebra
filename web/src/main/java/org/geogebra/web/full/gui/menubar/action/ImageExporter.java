package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.web.full.main.AppWFull;

/**
 * Exports image.
 */
class ImageExporter {

	private AppWFull app;
	private String extension;

	/**
	 * @param app       app
	 * @param extension file extension
	 */
	ImageExporter(AppWFull app, String extension) {
		this.app = app;
		this.extension = extension;
	}

	void export(String url) {
		app.getSelectionManager().clearSelectedGeos();
		app
				.getFileManager()
				.showExportAsPictureDialog(
						url,
						app.getExportTitle(),
						extension,
						"ExportAsPicture",
						app);
	}
}
