package org.geogebra.web.full.gui.toolbarpanel.tableview.dataimport;

import org.geogebra.common.gui.view.table.importer.DataImporterDelegate;
import org.geogebra.common.gui.view.table.importer.DataImporterError;
import org.geogebra.common.gui.view.table.importer.DataImporterWarning;
import org.geogebra.web.full.main.AppWFull;
import org.gwtproject.timer.client.Timer;

public class DataImportHandler implements DataImporterDelegate {
	protected AppWFull appW;
	private String fileName;
	private boolean continueImport = true;
	private DataImportSnackbar progressSnackbar;
	private Timer showSnackbar = new Timer() {
		@Override
		public void run() {
			progressSnackbar.show();
		}
	};

	/**
	 * data import handler
	 * @param appW - application
	 * @param fileName - file name
	 */
	public DataImportHandler(AppWFull appW, String fileName) {
		this.appW = appW;
		this.fileName = fileName;
		progressSnackbar = new DataImportSnackbar(appW, fileName, () -> cancelImport());
	}

	@Override
	public boolean onValidationProgress(int currentRow) {
		return true;
	}

	@Override
	public boolean onImportProgress(int currentRow, int totalRowCount) {
		if (currentRow == totalRowCount) {
			if (progressSnackbar.isVisible()) {
				progressSnackbar.hide();
			} else {
				showSnackbar.cancel();
			}
		}
		return continueImport;
	}

	@Override
	public void onImportWarning(DataImporterWarning warning, int currentRow) {
		// nothing to do here for now
	}

	@Override
	public void onImportError(DataImporterError error, int currentRow) {
		showSnackbar.cancel();
		progressSnackbar.hide();
		new DataImportSnackbar(appW, fileName, this::cancelImport, appW.getCsvHandler());
	}

	public void scheduleSnackbar() {
		showSnackbar.schedule(2000);
	}

	private void cancelImport() {
		continueImport = false;
	}
}
