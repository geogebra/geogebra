/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
