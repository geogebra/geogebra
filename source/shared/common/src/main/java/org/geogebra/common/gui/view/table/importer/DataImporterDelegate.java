package org.geogebra.common.gui.view.table.importer;

public interface DataImporterDelegate {

	/**
	 * Informs the delegate about data validation progress.
	 * @param currentRow The current row of data being validated.
	 * @return false to cancel validation (and import) after the current row, or
	 * true to continue validation.
	 */
	boolean onValidationProgress(int currentRow);

	/**
	 * Informs the delegate about data import progress.
	 * @param currentRow The current row of data being imported.
	 * @param totalRowCount The total number of rows being imported.
	 * @return false to cancel import after the current row, or
	 * true to continue importing.
	 */
	boolean onImportProgress(int currentRow, int totalRowCount);

	/**
	 * Informs the delegate about a non-fatal problem during data import.
	 * Import will continue after this warning.
	 * @param warning The import warning.
	 * @param currentRow The row in which the problem occurred.
	 */
	void onImportWarning(DataImporterWarning warning, int currentRow);

	/**
	 * Informs the delegate about fatal problems during data import.
	 * Import will be aborted after this line.
	 * @param error The import error.
	 * @param currentRow The row in which the problem occurred.
	 */
	void onImportError(DataImporterError error, int currentRow);
}
