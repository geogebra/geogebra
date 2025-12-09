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

import org.geogebra.web.full.gui.menubar.DefaultMenuAction;
import org.geogebra.web.full.main.AppWFull;

public abstract class DownloadImageAction extends DefaultMenuAction<AppWFull> {

	private final ImageExporter imageExporter;

	public DownloadImageAction(AppWFull app, String extension) {
		imageExporter = new ImageExporter(app, extension);
	}

	@Override
	public final void execute(AppWFull app) {
		app.getSelectionManager().clearSelectedGeos();
		export(app);
	}

	protected abstract void export(AppWFull app);

	protected void exportImage(String url) {
		imageExporter.export(url);
	}
}
