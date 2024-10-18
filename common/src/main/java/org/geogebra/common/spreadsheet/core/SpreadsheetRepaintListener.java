package org.geogebra.common.spreadsheet.core;

public interface SpreadsheetRepaintListener {

	/**
	 * Called whenever a change within the Spreadsheet needs to trigger redrawing
	 */
	void notifyRepaintNeeded();

}
