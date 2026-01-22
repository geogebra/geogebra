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

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.statistics.Statistic;

/**
 * Interaction with the construction.
 */
public interface SpreadsheetConstructionDelegate {

	/**
	 * Check if pie charts are supported (in the current app).
	 * @return {@code true} if supported, {@code false} otherwise.
	 */
	default boolean supportsPieChart() {
		return true;
	}

	/**
	 * Create a pie chart.
	 * @param data The spreadsheet data.
	 * @param range The range in {@code data} from which to create the chart.
	 */
	void createPieChart(@Nonnull TabularData<?> data, @Nonnull TabularRange range);

	/**
	 * Check if bar charts are supported (in the current app).
	 * @return {@code true} if supported, {@code false} otherwise.
	 */
	default boolean supportsBarChart() {
		return true;
	}

	/**
	 * Create a bar chart.
	 * @param data The spreadsheet data.
	 * @param ranges The list of ranges in {@code data} from which to create the chart.
	 */
	void createBarChart(@Nonnull TabularData<?> data, @Nonnull List<TabularRange> ranges);

	/**
	 * Check if histograms are supported (in the current app).
	 * @return {@code true} if supported, {@code false} otherwise.
	 */
	default boolean supportsHistogram() {
		return true;
	}

	/**
	 * Create a histogram.
	 * @param data The spreadsheet data.
	 * @param ranges The list of ranges in {@code data} from which to create the chart.
	 */
	void createHistogram(@Nonnull TabularData<?> data, @Nonnull List<TabularRange> ranges);

	/**
	 * Check if line graphs are supported (in the current app).
	 * @return {@code true} if supported, {@code false} otherwise.
	 */
	default boolean supportsLineGraph() {
		return true;
	}

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
	 * Check if box plots are supported (in the current app).
	 * @return {@code true} if supported, {@code false} otherwise.
	 */
	default boolean supportsBoxPlot() {
		return true;
	}

	/**
	 * Create a box plot.
	 * @param data The spreadsheet data.
	 * @param ranges The ranges in {@code data} from which to take raw data and frequencies.
	 */
	void createBoxPlot(@Nonnull TabularData<?> data, @Nonnull List<TabularRange> ranges);

	/**
	 * Check if the statistic is supported (in the current app).
	 * @param statistic statistic
	 * @return {@code true} if supported, {@code false} otherwise
	 */
	default boolean supportsStatistic(Statistic statistic) {
		return true;
	}
}
