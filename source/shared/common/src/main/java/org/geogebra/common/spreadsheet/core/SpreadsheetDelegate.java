package org.geogebra.common.spreadsheet.core;

/**
 * The view that hosts the spreadsheet.
 */
@FunctionalInterface
public interface SpreadsheetDelegate {

	/**
	 * Notifies that the spreadsheet needs redrawing
	 */
	void notifyRepaintNeeded();
}
