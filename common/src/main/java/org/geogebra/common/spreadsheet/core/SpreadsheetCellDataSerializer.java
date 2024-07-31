package org.geogebra.common.spreadsheet.core;

/**
 * An abstraction for spreadsheet cell data conversion.
 *
 * (This prevents direct dependencies on GeoElement and other classes from the kernel package.)
 */
public interface SpreadsheetCellDataSerializer {

	/**
	 * Converts spreadsheet cell data to a string representation for the cell editor.
	 * @param data Spreadsheet cell data.
	 * @return A string representation of the data.
	 */
	String getStringForEditor(Object data);
}
