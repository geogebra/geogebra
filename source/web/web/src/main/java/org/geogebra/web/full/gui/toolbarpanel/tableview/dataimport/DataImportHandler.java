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

package org.geogebra.web.full.gui.toolbarpanel.tableview.dataimport;

import org.geogebra.common.gui.view.table.importer.DataImporterDelegate;
import org.geogebra.common.gui.view.table.importer.DataImporterError;
import org.geogebra.common.gui.view.table.importer.DataImporterWarning;
import org.geogebra.web.full.main.AppWFull;

public class DataImportHandler implements DataImporterDelegate {
	protected AppWFull appW;
	private String fileName;
	private DataImportSnackbar progressSnackbar;

	/**
	 * data import handler
	 * @param appW - application
	 * @param fileName - file name
	 * @param progressSnackbar - progress showing nackbar
	 */
	public DataImportHandler(AppWFull appW, String fileName, DataImportSnackbar progressSnackbar) {
		this.appW = appW;
		this.fileName = fileName;
		this.progressSnackbar = progressSnackbar;
	}

	@Override
	public boolean onValidationProgress(int currentRow) {
		return true;
	}

	@Override
	public boolean onImportProgress(int currentRow, int totalRowCount) {
		return true;
	}

	@Override
	public void onImportWarning(DataImporterWarning warning, int currentRow) {
		// nothing to do here for now
	}

	@Override
	public void onImportError(DataImporterError error, int currentRow) {
		new DataImportSnackbar(appW, fileName, appW.getCsvHandler());
		progressSnackbar.hide();
	}
}
