package org.geogebra.common.spreadsheet.core;

/**
 * An abstraction for spreadsheet cell value processing.
 *
 * (This prevents direct dependencies on Kernel, etc.)
 */
public interface SpreadsheetCellProcessor {
	void process(String input, int row, int column);
}
