package org.geogebra.common.spreadsheet.core;

import javax.annotation.Nonnull;

/**
 * Interaction with the construction.
 */
public interface SpreadsheetConstructionDelegate {

	/**
	 * Create a pie chart.
	 * @param data The spreadsheet data.
	 * @param range The range in {@code data} from which to create the chart.
	 */
	void createPieChart(@Nonnull TabularData<?> data, @Nonnull TabularRange range);

	/**
	 * Create a bar chart.
	 * @param data The spreadsheet data.
	 * @param range The range in {@code data} from which to create the chart.
	 */
	void createBarChart(@Nonnull TabularData<?> data, @Nonnull TabularRange range);
}
