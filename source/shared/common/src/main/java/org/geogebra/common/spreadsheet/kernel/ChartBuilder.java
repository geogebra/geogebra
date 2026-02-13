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

package org.geogebra.common.spreadsheet.kernel;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularRange;

public final class ChartBuilder {

	private static final class TabularRangePair {
		private final TabularRange start;
		private final TabularRange end;

		private TabularRangePair(List<TabularRange> ranges) {
			TabularRange range0 = ranges.get(0);
			if (ranges.size() == 1 && range0.getWidth() != 2) {
				start = null;
				end = null;
				return;
			}

			start = range0;
			end = ranges.get(ranges.size() == 2 ? 1 : 0);
		}

		boolean isInvalid() {
			return start == null;
		}
	}

	/**
	 * Builds the pie chart command with selection range and center (0,0).
	 * @param data The spreadsheet data.
	 * @param range The range in {@code data} from which to create the chart.
	 * @return Pie chart command, e.g. =PieChart(A1:A3,(0,0))
	 */
	public static @CheckForNull String getPieChartCommand(TabularData<?> data, TabularRange range) {
		if (range.isEntireColumn()) {
			return getPieChartCommand(data, 0, range.getMinColumn(),
					data.numberOfRows() - 2, range.getMaxColumn());
		} else if (range.isPartialColumn() && !range.isPartialRow() && !range.isEntireRow()) {
			return getPieChartCommand(data, range.getFromRow(),
					range.getFromColumn(), range.getToRow(), range.getToColumn());
		}
		return null;
	}

	private static @Nonnull String getPieChartCommand(TabularData<?> data,
			int fromRow, int fromCol, int toRow, int toCol) {
		StringBuilder sb = new StringBuilder();
		sb.append("PieChart");
		sb.append("(");
		if (fromRow > -1 && fromCol > -1 && toRow > -1 && toCol > -1) {
			sb.append(data.getCellName(fromRow, fromCol)).append(":")
					.append(data.getCellName(toRow, toCol));
		}
		sb.append(",");
		sb.append("(0,0)");
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Builds the bar chart command based on selection range: first column as list of data,
	 * second column as list of frequencies.
	 * @param data The spreadsheet data.
	 * @param ranges List of ranges in {@code data} from which to create the chart.
	 * @return Bar chart command, e.g. =BarChart(A1:A3,B1:B3)
	 */
	public static @CheckForNull String getBarChartCommand(TabularData<?> data, List<TabularRange> ranges) {
		return getChart(data, ranges, "BarChart");
	}

	private static String getChart(TabularData<?> data, List<TabularRange> ranges,
			String chartName) {
		TabularRangePair pair = new TabularRangePair(ranges);
		if (pair.isInvalid()) {
			return null;
		}
		return getChartCommandWithTwoListParameter(chartName, data,
				pair.start.getFromRow(), pair.start.getFromColumn(),
				pair.end.getToRow(), pair.end.getToColumn());
	}

	private static @Nonnull String getChartCommandWithTwoListParameter(String commandName,
			TabularData<?> data, int fromRow, int fromCol, int toRow, int toCol) {
		StringBuilder sb = new StringBuilder();
		sb.append(commandName);
		sb.append("(");
		if (fromRow > -1 && fromCol > -1 && toRow > -1 && toCol > -1) {
			sb.append(data.getCellName(fromRow, fromCol)).append(":")
					.append(data.getCellName(toRow, fromCol));
			sb.append(",").append(data.getCellName(fromRow, toCol)).append(":")
					.append(data.getCellName(toRow, toCol));
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Builds the histogram command based on selection range: first column as list of class
	 * boundaries, second column as list of heights.
	 * @param data The spreadsheet data.
	 * @param ranges List of ranges in {@code data} from which to create the chart.
	 * @return Histogram command, e.g. =Histogram(A1:A3,B1:B3)
	 */
	public static @CheckForNull String getHistogramCommand(TabularData<?> data, List<TabularRange> ranges) {
		return getChart(data, ranges, "Histogram");
	}

	/**
	 * Builds the line graph command based on selection range: first column as list of
	 * x-coordinates, second column as list of y-coordinates.
	 * @param data The spreadsheet data.
	 * @param ranges The range in {@code data} from which to create the chart.
	 * @return Line graph command, e.g. =LineGraph(A1:A3,B1:B3)
	 */
	public static String getLineGraphCommand(TabularData<?> data, List<TabularRange> ranges) {
		return getChart(data, ranges, "LineGraph");
	}

	/**
	 * Builds the line graph command based on selection range: first column as list of
	 * x-coordinates, second column as list of y-coordinates.
	 * @param data The spreadsheet data.
	 * @param range The range in {@code data} from which to create the chart.
	 * @param toColumn The column to use as list of y-coordinates.
	 * @return Line graph command, e.g. =LineGraph(A1:A3,B1:B3)
	 */
	public static String getLineGraphCommand(TabularData<?> data, TabularRange range,
			int toColumn) {
		return getChartCommandWithTwoListParameter("LineGraph", data,
				range.getFromRow(), range.getFromColumn(),
				range.getToRow(), toColumn);
	}

	/**
	 * Builds a BoxPlot command.
	 * @param data The spreadsheet data.
	 * @param ranges The range(s) in {@code data}. Only ranges that have passed validation via
	 * {@link org.geogebra.common.spreadsheet.core.ChartError#validateRangesForBoxPlot(List)}
	 * must be passed, otherwise the resulting command may be garbage.
	 * @return The BoxPlot command
	 */
	public static @Nonnull String getBoxPlotCommand(@Nonnull TabularData<?> data,
			@Nonnull List<TabularRange> ranges) {
		TabularRange rawDataRange = ranges.get(0);
		TabularRange frequenciesRange = null;
		if (ranges.size() > 1) {
			frequenciesRange = ranges.get(1);
		} else {
			// check if the first range is 2xN or Nx2 rectangular
			if (rawDataRange.getHeight() == 2 && rawDataRange.getWidth() > 2) {
				// 2 rows x N columns
				TabularRange newRawDataRange = new TabularRange(
						rawDataRange.getFromRow(),
						rawDataRange.getFromColumn(),
						rawDataRange.getFromRow(),
						rawDataRange.getToColumn());
				frequenciesRange = new TabularRange(
						rawDataRange.getToRow(),
						rawDataRange.getFromColumn(),
						rawDataRange.getToRow(),
						rawDataRange.getToColumn());
				rawDataRange = newRawDataRange;
			} else if (rawDataRange.getWidth() == 2 && rawDataRange.getHeight() > 2) {
				// N rows x 2 columns
				TabularRange newRawDataRange = new TabularRange(
						rawDataRange.getFromRow(),
						rawDataRange.getFromColumn(),
						rawDataRange.getToRow(),
						rawDataRange.getFromColumn());
				frequenciesRange = new TabularRange(
						rawDataRange.getFromRow(),
						rawDataRange.getToColumn(),
						rawDataRange.getToRow(),
						rawDataRange.getToColumn());
				rawDataRange = newRawDataRange;
			}
		}
		StringBuilder sb = new StringBuilder();
		// BoxPlot( <yOffset>, <yScale>, <List of Raw Data> )
		sb.append("BoxPlot(0, 1, ");
		sb.append(formatCellRange(rawDataRange, data));
		if (frequenciesRange != null) {
			// BoxPlot( <yOffset>, <yScale>, <List of Data>, <List of Frequencies>,
			// <Boolean Outliers> )
			sb.append(", ");
			sb.append(formatCellRange(frequenciesRange, data));
			sb.append(", false");
		}
		sb.append(")");
		return sb.toString();
	}

	private static String formatCellRange(@Nonnull TabularRange range,
			@Nonnull TabularData<?> data) {
		return data.getCellName(range.getFromRow(), range.getFromColumn())
				+ ":"
				+ data.getCellName(range.getToRow(), range.getToColumn());
	}
}

