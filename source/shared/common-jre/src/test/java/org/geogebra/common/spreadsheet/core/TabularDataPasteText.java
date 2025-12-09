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

public class TabularDataPasteText implements TabularDataPasteInterface<String> {
	@Override
	public void pasteInternal(TabularData<String> tabularData,
			TabularClipboard<String> clipboard, TabularRange destination) {
		for (int row = 0; row < clipboard.numberOfRows(); row++) {
			for (int column = 0; column < clipboard.numberOfColumns(); column++) {
				tabularData.setContent(destination.getFromRow() + row,
						destination.getFromColumn() + column,
						clipboard.contentAt(row, column));
			}
		}
	}

	@Override
	public void pasteExternal(TabularData<String> tabularData, String[][] clipboardContent,
			TabularRange destination) {
		throw new UnsupportedOperationException();
	}
}
