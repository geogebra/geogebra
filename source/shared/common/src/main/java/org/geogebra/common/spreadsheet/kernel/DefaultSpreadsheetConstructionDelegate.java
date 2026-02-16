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

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.statistics.Statistic;
import org.geogebra.common.spreadsheet.core.SpreadsheetConstructionDelegate;
import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularRange;

public class DefaultSpreadsheetConstructionDelegate implements SpreadsheetConstructionDelegate {

	private final @Nonnull AlgebraProcessor algebraProcessor;
	private final @Nonnull CommandDispatcher commandDispatcher;

	/**
	 * Constructor
	 * @param algebraProcessor the algebra processor
	 */
	public DefaultSpreadsheetConstructionDelegate(@Nonnull AlgebraProcessor algebraProcessor) {
		this.algebraProcessor = algebraProcessor;
		this.commandDispatcher = algebraProcessor.getCommandDispatcher();
	}

	private GeoElementND[] processCommand(@Nonnull String command) {
		return algebraProcessor.processAlgebraCommand(command, true);
	}

	@Override
	public boolean supportsPieChart() {
		return commandDispatcher.isAllowedByCommandFilters(Commands.PieChart);
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
		return commandDispatcher.isAllowedByCommandFilters(Commands.BarChart);
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
		return commandDispatcher.isAllowedByCommandFilters(Commands.Histogram);
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
		return commandDispatcher.isAllowedByCommandFilters(Commands.LineGraph);
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
		return commandDispatcher.isAllowedByCommandFilters(Commands.BoxPlot);
	}

	@Override
	public void createBoxPlot(@Nonnull TabularData<?> data, @Nonnull List<TabularRange> ranges) {
		String command = ChartBuilder.getBoxPlotCommand(data, ranges);
		GeoElementND[] result = processCommand(command);
		if (result != null && result.length > 0 && result[0] != null) {
			GeoElementND element = result[0];
			element.setFixed(false);
		}
	}

	@Override
	public boolean supportsStatistic(Statistic statistic) {
		return commandDispatcher.isAllowedByCommandFilters(statistic.command);
	}
}

