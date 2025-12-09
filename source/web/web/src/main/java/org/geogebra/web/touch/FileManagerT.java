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

package org.geogebra.web.touch;

import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.main.FileManager;
import org.geogebra.web.html5.main.AppW;

public abstract class FileManagerT extends FileManager {

	/**
	 * @param app
	 *            application
	 */
	public FileManagerT(final AppW app) {
		super(app);
	}

	@Override
	public void rename(final String newTitle, final Material mat) {
		rename(newTitle, mat, null);
	}

	@Override
	public void autoSave(int counter) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getAutosaveJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void restoreAutoSavedFile(String json) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteAutoSavedFile() {
		// TODO Auto-generated method stub
	}

	/**
	 * @param m {@link Material}
	 */
	protected void doOpenMaterial(Material m) {
		super.openMaterial(m);
	}

	@Override
	public void export(final App app1) {
		((AppW) app1).getGgbApi().getBase64(true,
				s -> nativeShare(s, app1.getExportTitle()));
	}

	@Override
	public void showExportAsPictureDialog(String url, String filename,
			String extension, String titleKey, App appW) {

		exportImage(url, filename, extension);
		// TODO check if it really happened
		appW.dispatchEvent(
				new Event(EventType.EXPORT, null, "[\"" + extension + "\"]"));
	}

	@Override
	public void refreshAutosaveTimestamp() {
		// TODO Auto-generated method stub

	}
}