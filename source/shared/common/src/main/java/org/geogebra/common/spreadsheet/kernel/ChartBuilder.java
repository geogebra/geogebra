package org.geogebra.common.spreadsheet.kernel;

import javax.annotation.Nullable;

import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularRange;

public final class ChartBuilder {

	/**
	 * Builds the pie chart command with selection range and center (0,0).
	 * @param data - data
	 * @param range - selected range
	 * @return pie chart command, e.g. =PieChart(A1:A3,(0,0))
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
		sb.append("=");
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
}

