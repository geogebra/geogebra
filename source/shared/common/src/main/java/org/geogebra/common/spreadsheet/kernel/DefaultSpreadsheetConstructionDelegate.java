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

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.spreadsheet.core.SpreadsheetConstructionDelegate;
import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularRange;

public class DefaultSpreadsheetConstructionDelegate implements SpreadsheetConstructionDelegate {

	private final @Nonnull AlgebraProcessor algebraProcessor;
	private final @CheckForNull CommandFilter commandFilter;

	/**
	 * Constructor
	 * @param algebraProcessor the algebra processor
	 * @param commandFilter the command filter of the current app (coming from AppConfig)
	 */
	public DefaultSpreadsheetConstructionDelegate(@Nonnull AlgebraProcessor algebraProcessor,
			@CheckForNull CommandFilter commandFilter) {
		this.algebraProcessor = algebraProcessor;
		this.commandFilter = commandFilter;
	}

	private void processCommand(@Nonnull String command) {
		algebraProcessor.processAlgebraCommand(command, true);
	}

	@Override
	public boolean supportsPieChart() {
		if (commandFilter == null) {
			return true;
		}
		return commandFilter.isCommandAllowed(Commands.PieChart);
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
	public boolean supportsBarChart() {
		if (commandFilter == null) {
			return true;
		}
		return commandFilter.isCommandAllowed(Commands.BarChart);
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
	public boolean supportsHistogram() {
		if (commandFilter == null) {
			return true;
		}
		return commandFilter.isCommandAllowed(Commands.Histogram);
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
	public boolean supportsLineGraph() {
		if (commandFilter == null) {
			return true;
		}
		return commandFilter.isCommandAllowed(Commands.LineGraph);
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
	public boolean supportsBoxPlot() {
		if (commandFilter == null) {
			return true;
		}
		return commandFilter.isCommandAllowed(Commands.BoxPlot);
	}

	@Override
	public void createBoxPlot(@Nonnull TabularData<?> data, @Nonnull List<TabularRange> ranges) {
		String command = ChartBuilder.getBoxPlotCommand(data, ranges);
		processCommand(command);
	}
}

