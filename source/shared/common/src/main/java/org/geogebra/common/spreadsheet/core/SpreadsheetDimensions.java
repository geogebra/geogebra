package org.geogebra.common.spreadsheet.core;

import java.util.Map;

public interface SpreadsheetDimensions {
	/**
	 * @return column widths
	 */
	Map<Integer, Double> getWidthMap();

	/**
	 * @return row heights
	 */
	Map<Integer, Double> getHeightMap();

	int getRows();

	int getColumns();
}
