package org.geogebra.common.spreadsheet.core;

@FunctionalInterface
public interface SpreadsheetRepaintListener {

	/**
	 * Notifies the spreadsheet to be repainted
	 */
	void notifyRepaintNeeded();

}
