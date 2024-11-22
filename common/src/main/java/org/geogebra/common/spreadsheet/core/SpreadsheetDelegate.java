package org.geogebra.common.spreadsheet.core;

public interface SpreadsheetDelegate {

	/**
	 * Notifies that the spreadsheet needs redrawing
	 */
	void notifyRepaintNeeded();

}
