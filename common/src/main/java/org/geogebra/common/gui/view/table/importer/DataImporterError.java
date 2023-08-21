package org.geogebra.common.gui.view.table.importer;

/**
 * Reasons why data import may fail.
 */
public enum DataImporterError {

	/** Unspecified error */
	UNKNOWN_ERROR,

	/** Unexpected data format */
	DATA_FORMAT_ERROR,

	/** Inconsistent number of columns.
	Can also be caused by inconsistent column separators (mixing ',' and ';', for example).
	*/
	INCONSISTENT_COLUMNS
}
