package org.geogebra.common.spreadsheet.core;

/**
 * An abstraction for spreadsheet cell data conversion.
 *
 * (This prevents direct dependencies on GeoElement, etc.)
 */
public interface SpreadsheetCellDataSerializer {

	/**
	 * Converts spreadsheet cell data to a string representation for the cell editor.
	 * @param data Spreadsheet cell data.
	 * @return A string representation of `data`.
	 */
	String getStringForEditor(Object data);
}
