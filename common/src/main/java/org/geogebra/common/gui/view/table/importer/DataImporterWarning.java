package org.geogebra.common.gui.view.table.importer;

/**
 * Non-fatal problems during data import.
 */
public enum DataImporterWarning {

	/** Possibly incorrect number format */
	NUMBER_FORMAT_WARNING,

	/** Parts of the data was skipped on import. */
	DATA_SIZE_LIMIT_EXCEEDED
}
