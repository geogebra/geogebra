package org.geogebra.common.spreadsheet.kernel;

import javax.annotation.Nullable;

import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularRange;

public final class ChartBuilder {

	/**
	 * Builds the pie chart command with selection range and center (0,0).
	 * @param data The spreadsheet data.
	 * @param range The range in {@code data} from which to create the chart.
	 * @return Pie chart command, e.g. =PieChart(A1:A3,(0,0))
	 */
	@Nullable
	public static String getPieChartCommand(TabularData<?> data, TabularRange range) {
		if (range.isEntireColumn()) {
			return getPieChartCommand(data, 0, range.getMinColumn(),
					data.numberOfRows() - 2, range.getMaxColumn());
		} else if (range.isPartialColumn() && !range.isPartialRow() && !range.isEntireRow()) {
			return getPieChartCommand(data, range.getFromRow(),
					range.getFromColumn(), range.getToRow(), range.getToColumn());
		}
		return null;
	}

	private static String getPieChartCommand(TabularData<?> data,
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
	 * @param range The range in {@code data} from which to create the chart.
	 * @return Bar chart command, e.g. =BarChart(A1:A3,B1:B3)
	 */
	@Nullable
	public static String getBarChartCommand(TabularData<?> data, TabularRange range) {
		if (range.getWidth() == 2) {
			return getChartCommandWithTwoListParameter("BarChart", data,
					range.getFromRow(), range.getFromColumn(),
					range.getToRow(), range.getToColumn());
		}
		return null;
	}

	private static String getChartCommandWithTwoListParameter(String commandName,
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
	 * @param range The range in {@code data} from which to create the chart.
	 * @return Histogram command, e.g. =Histogram(A1:A3,B1:B3)
	 */
	@Nullable
	public static String getHistogramCommand(TabularData<?> data, TabularRange range) {
		if (range.getWidth() == 2) {
			return getChartCommandWithTwoListParameter("Histogram", data,
					range.getFromRow(), range.getFromColumn(),
					range.getToRow(), range.getToColumn());
		}
		return null;
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
}

