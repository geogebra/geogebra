package org.geogebra.common.spreadsheet.kernel;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.spreadsheet.core.SpreadsheetConstructionDelegate;
import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularRange;

public class DefaultSpreadsheetConstructionDelegate implements SpreadsheetConstructionDelegate {

	@Nonnull
	private final AlgebraProcessor algebraProcessor;

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
	public void createBarChart(@Nonnull TabularData<?> data, @Nonnull TabularRange range) {
		String command = ChartBuilder.getBarChartCommand(data, range);
		if (command == null) {
			return;
		}
		processCommand(command);
	}

	@Override
	public void createHistogram(@Nonnull TabularData<?> data, @Nonnull TabularRange range) {
		String command = ChartBuilder.getHistogramCommand(data, range);
		if (command == null) {
			return;
		}
		processCommand(command);
	}
}

