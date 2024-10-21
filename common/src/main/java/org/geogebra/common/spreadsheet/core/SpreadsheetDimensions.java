package org.geogebra.common.spreadsheet.core;

import java.util.Map;

public interface SpreadsheetDimensions {
	/**
	 * @return column widths
	 */
	Map<Integer, Integer> getWidthMap();

	/**
	 * @return row heights
	 */
	Map<Integer, Integer> getHeightMap();

	int getRows();

	int getColumns();
}
