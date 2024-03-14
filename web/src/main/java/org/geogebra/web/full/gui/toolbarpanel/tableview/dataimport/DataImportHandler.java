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
