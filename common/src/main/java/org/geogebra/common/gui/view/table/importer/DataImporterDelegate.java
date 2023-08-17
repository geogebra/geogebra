package org.geogebra.common.gui.view.table.importer;

public interface DataImporterDelegate {

	boolean onValidationProgress(int currentRow);
	boolean onImportProgress(int currentRow, int totalNrOfRows);
	void onImportWarning(DataImporterWarning warning, int currentRow);
	void onImportError(DataImporterError error, int currentRow);
}
