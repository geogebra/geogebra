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

package org.geogebra.common.gui.view.table.importer;

/**
 * Data import delegate.
 */
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
