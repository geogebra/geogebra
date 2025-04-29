package org.geogebra.common.spreadsheet.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * An abstraction for spreadsheet cell data conversion.
 * <p>
 * (This prevents direct dependencies on GeoElement and other classes from the kernel package.)
 */
public interface SpreadsheetCellDataSerializer {

	/**
	 * Converts spreadsheet cell data to a string representation for the cell editor.
	 * @param data Spreadsheet cell data.
	 * @return A string representation of the data.
	 */
	@Nonnull String getStringForEditor(@CheckForNull Object data);
}
