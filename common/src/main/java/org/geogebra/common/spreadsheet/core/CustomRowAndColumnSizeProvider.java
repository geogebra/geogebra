package org.geogebra.common.spreadsheet.core;

import java.util.Map;

/**
 * Provides row heights and column widths that have been changed by user
 */
public interface CustomRowAndColumnSizeProvider {

	/**
	 * @return map column=>width containing all widths that are not the default
	 */
	Map<Integer, Integer> getCustomColumnWidths();

	/**
	 * @return map row=>height containing all heights that are not the default
	 */
	Map<Integer, Integer> getCustomRowHeights();
}
