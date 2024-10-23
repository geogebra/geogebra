package org.geogebra.common.spreadsheet.core;

/**
 * An abstraction for spreadsheet cell input processing.
 *
 * (This prevents direct dependencies on AlgebraProcessor and other classes from the kernel
 * package.)
 */
public interface SpreadsheetCellProcessor {

	/**
	 * Process spreadsheet cell input.
	 * @param input The input from the cell editor.
	 * @param row The row identifying the cell being edited.
	 * @param column The row identifying the cell being edited.
	 */
	void process(String input, int row, int column);

	/**
	 * Mark error for cell input.
	 */
	void markError();
}
