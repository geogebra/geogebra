package org.geogebra.common.spreadsheet.kernel;

import java.util.List;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.spreadsheet.core.SpreadsheetConstructionDelegate;
import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularRange;

public class DefaultSpreadsheetConstructionDelegate implements SpreadsheetConstructionDelegate {

	private final @Nonnull AlgebraProcessor algebraProcessor;

	public DefaultSpreadsheetConstructionDelegate(@Nonnull AlgebraProcessor algebraProcessor) {
		this.algebraProcessor = algebraProcessor;
	}

	private void processCommand(@Nonnull String command) {
		algebraProcessor.processAlgebraCommand(command, true);
	}

	@Override
	public void createPieChart(@Nonnull TabularData<?> data, @Nonnull TabularRange range) {
		String command = ChartBuilder.getPieChartCommand(data, range);
		if (command == null) {
			return;
		}
		processCommand(command);
	}

	@Override
	public void createBarChart(@Nonnull TabularData<?> data, @Nonnull List<TabularRange> ranges) {
		String command = ChartBuilder.getBarChartCommand(data, ranges);
		if (command == null) {
			return;
		}
		processCommand(command);
	}

	@Override
	public void createHistogram(@Nonnull TabularData<?> data, @Nonnull List<TabularRange> ranges) {
		String command = ChartBuilder.getHistogramCommand(data, ranges);
		if (command == null) {
			return;
		}
		processCommand(command);
	}

	@Override
	public void createLineGraph(@Nonnull TabularData<?> data, @Nonnull List<TabularRange> ranges) {
		String command = ChartBuilder.getLineGraphCommand(data, ranges);
		if (command == null) {
			return;
		}
		processCommand(command);
	}

	@Override
	public void createLineGraph(@Nonnull TabularData<?> data, TabularRange range) {
		for (int toCol = range.getFromColumn() + 1; toCol <= range.getToColumn(); toCol++) {
			String command = ChartBuilder.getLineGraphCommand(data, range, toCol);
			processCommand(command);
		}
	}

	@Override
	public void createBoxPlot(@Nonnull TabularData<?> data, @Nonnull List<TabularRange> ranges) {
		String command = ChartBuilder.getBoxPlotCommand(data, ranges);
		processCommand(command);
	}
}

