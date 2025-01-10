package org.geogebra.common.spreadsheet.core;

import java.util.Map;

/**
 * Provides row heights and column widths that have been changed by user
 */
public interface CustomRowAndColumnSizeProvider {

	/**
	 * @return map column=&gt;width containing all widths that are not the default
	 */
	Map<Integer, Double> getCustomColumnWidths();

	/**
	 * @return map row=&gt;height containing all heights that are not the default
	 */
	Map<Integer, Double> getCustomRowHeights();
}
