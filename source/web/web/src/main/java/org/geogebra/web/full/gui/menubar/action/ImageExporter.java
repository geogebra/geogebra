package org.geogebra.web.full.gui.menubar.action;

import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
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
		String name = app.getExportTitle() + "." + extension;

		app.getFileManager().exportImage(url, name, extension);
		app.dispatchEvent(new Event(
				EventType.EXPORT, null,
				"[\"" + extension + "\"]"));
	}
}
