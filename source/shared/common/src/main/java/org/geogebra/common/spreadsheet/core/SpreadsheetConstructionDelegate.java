package org.geogebra.common.spreadsheet.core;

import java.util.List;

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
	 * @param ranges The list of ranges in {@code data} from which to create the chart.
	 */
	void createBarChart(@Nonnull TabularData<?> data, @Nonnull List<TabularRange> ranges);

	/**
	 * Create a histogram.
	 * @param data The spreadsheet data.
	 * @param ranges The list of ranges in {@code data} from which to create the chart.
	 */
	void createHistogram(@Nonnull TabularData<?> data, @Nonnull List<TabularRange> ranges);

	/**
	 * Create a line graph.
	 * @param data The spreadsheet data.
	 * @param range The range in {@code data} from which to create the graph.
	 */
	void createLineGraph(@Nonnull TabularData<?> data, @Nonnull TabularRange range);

	/**
	 * Create a line graph.
	 * @param data The spreadsheet data.
	 * @param ranges The list of ranges in {@code data} from which to create the graph.
	 */
	void createLineGraph(@Nonnull TabularData<?> data, @Nonnull List<TabularRange> ranges);

	/**
	 * Create a box plot.
	 * @param data The spreadsheet data.
	 * @param ranges The ranges in {@code data} from which to take raw data and frequencies.
	 */
	void createBoxPlot(@Nonnull TabularData<?> data, @Nonnull List<TabularRange> ranges);
}
