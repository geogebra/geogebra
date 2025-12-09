/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.spreadsheet.core;

/**
 * Interface to paste data typed T into the spreadsheet
 *
 * It handles both internal and external data pasting:
 * Internal means from spreadsheet to spreadsheet, while external means it comes from
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