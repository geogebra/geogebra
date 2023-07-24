package org.geogebra.common.spreadsheet.core;

/**
 * Converts data from the arbitrary format stored in {@link TabularData}
 * to something that can be rendered.
 */
public interface SpreadsheetDataConverter {
	public CellRenderer getRenderer(Object data);
}
