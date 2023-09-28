package org.geogebra.web.full.gui.toolbarpanel.tableview.dataimport;

import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.gui.view.table.importer.DataImporterDelegate;
import org.geogebra.common.gui.view.table.importer.DataImporterError;
import org.geogebra.common.gui.view.table.importer.DataImporterWarning;
import org.geogebra.web.full.main.AppWFull;

public class DataImportHandler implements DataImporterDelegate {
	protected AppWFull appW;
	private String fileName;
	private boolean continueImport = true;
	private DataImportSnackbar progressSnackbar;

	/**
	 * data import handler
	 * @param appW - application
	 * @param fileName - file name
	 */
	public DataImportHandler(AppWFull appW, String fileName) {
		this.appW = appW;
		this.fileName = fileName;
	}

	@Override
	public boolean onValidationProgress(int currentRow) {
		return true;
	}

	private TableValuesView getTable() {
		return (TableValuesView) appW.getGuiManager().getTableValuesView();
	}

	@Override
	public boolean onImportProgress(int currentRow, int totalRowCount) {
		return continueImport;
	}

	@Override
	public void onImportWarning(DataImporterWarning warning, int currentRow) {
		// nothing to do here for now
	}

	@Override
	public void onImportError(DataImporterError error, int currentRow) {
		new DataImportSnackbar(appW, fileName, this::cancelImport, appW.getCsvHandler());
	}

	private void cancelImport() {
		continueImport = false;
	}
}
