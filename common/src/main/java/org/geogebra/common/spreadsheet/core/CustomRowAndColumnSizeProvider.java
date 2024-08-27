package org.geogebra.common.spreadsheet.core;

import java.util.Map;

/**
 * Provides row heights and column widths that have been changed by user
 */
public interface CustomRowAndColumnSizeProvider {

	/**
	 * @param widths output map column=>width containing all widths that are not the default
	 */
	void getCustomColumnWidths(Map<Integer, Integer> widths);

	/**
	 * @param heights output map row=>height containing all heights that are not the default
	 */
	void getCustomRowHeights(Map<Integer, Integer> heights);
}
