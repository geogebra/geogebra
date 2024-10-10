package org.geogebra.common.spreadsheet.core;

/**
 * Interface to paste data typed T into the spreadsheet
 *
 * It handles both internal and external data pasting:
 * Internal means from spreadsheet to spreadsheed, while exterlal means it comes from
 * the system clipboard, its data must be parsed and the corresponding
 * cell elements of T has to be created.
 *
 * @param <T> the main datatype of the cells,
 */
public interface TabularDataPasteInterface<T> {

	/**
	 * Paste data within tabularData
	 * @param tabularData to paste to.
	 * @param clipboard with the internal cell data.
	 * @param destination range of tabularData to paste to
	 */
	void pasteInternal(TabularData<T> tabularData, TabularClipboard<T> clipboard,
		TabularRange destination);

	/**
	 * Paste data from system clipboard tabularData.
	 *
	 * @param tabularData to paste to.
	 * @param clipboardContent the serialized cell data.
	 * @param destination range of tabularData to paste to
	 */
	void pasteExternal(TabularData<T> tabularData, String[][] clipboardContent,
		TabularRange destination);
}