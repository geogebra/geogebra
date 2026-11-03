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
import java.util.stream.Collectors;

import org.geogebra.common.gui.view.spreadsheet.CellRangeUtil;
import org.geogebra.common.gui.view.spreadsheet.SpreadsheetToolProcessor;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.statistics.Statistic;
import org.geogebra.common.main.App;
import org.geogebra.common.spreadsheet.core.ContextMenuItem;
import org.geogebra.common.spreadsheet.core.SpreadsheetConstructionDelegate;
import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.jspecify.annotations.NonNull;

public class DefaultSpreadsheetConstructionDelegate implements SpreadsheetConstructionDelegate {

	private final @NonNull AlgebraProcessor algebraProcessor;
	private final @NonNull CommandDispatcher commandDispatcher;

	/**
	 * Constructor
	 * @param algebraProcessor the algebra processor
	 */
	public DefaultSpreadsheetConstructionDelegate(@NonNull AlgebraProcessor algebraProcessor) {
		this.algebraProcessor = algebraProcessor;
		this.commandDispatcher = algebraProcessor.getCommandDispatcher();
	}

	private boolean isAllowed(ContextMenuItem.@NonNull Identifier type) {
		if (type == ContextMenuItem.Identifier.POLYLINE
				&& !commandDispatcher.isAllowedByCommandFilters(Commands.PolyLine)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean createObject(
			ContextMenuItem.@NonNull Identifier type, @NonNull List<TabularRange> ranges) {
		if (ranges.isEmpty() || !isAllowed(type)) {
			return false;
		}
		App app = algebraProcessor.getKernel().getApplication();
		List<TabularRange> finiteRanges = ranges.stream()
				.map(range -> range.restrictInfiniteRangeTo(
						app.getSpreadsheetTableModel().getRowCount(),
						app.getSpreadsheetTableModel().getColumnCount()))
				.collect(Collectors.toList());
		if (finiteRanges.stream()
				.allMatch(range -> CellRangeUtil.isEmpty(range, app.getSpreadsheetTableModel()))) {
			return false;
		}
		CellFormat format = new CellFormat(null);
		format.processXMLString(app.getSettings().getSpreadsheet().getCellFormatXml());
		SpreadsheetToolProcessor processor = new SpreadsheetToolProcessor(app, format);
		GeoElementND created;
		if (type == ContextMenuItem.Identifier.TABLE) {
			TabularRange range = finiteRanges.get(0);
			if (!processor.isCreateMatrixPossible(finiteRanges)) {
				return false;
			}
			created = processor.createTableText(
					range.getMinColumn(),
					range.getMaxColumn(),
					range.getMinRow(),
					range.getMaxRow(),
					false,
					false);
		} else {
			if (!processor.isCreatePointListPossible(finiteRanges)) {
				return false;
			}
			GeoElement geo;
			if (type == ContextMenuItem.Identifier.LIST_OF_POINTS) {
				geo = processor.createPointGeoList(finiteRanges, false, true, false, true);
			} else if (type == ContextMenuItem.Identifier.POLYLINE) {
				geo = processor.createPolyLine(finiteRanges, false, true);
			} else {
				return false;
			}
			if (geo != null) {
				app.getKernel().getConstruction().addToConstructionList(geo.getParentAlgorithm(), true);
				geo.setLabel(null);
			}
			created = geo;
		}
		return created != null;
	}

	private GeoElementND[] processCommand(@NonNull String command) {
		return algebraProcessor.processAlgebraCommand(command, true);
	}

	@Override
	public boolean supportsPieChart() {
		return commandDispatcher.isAllowedByCommandFilters(Commands.PieChart);
	}

	@Override
	public void createPieChart(@NonNull TabularData<?> data, @NonNull TabularRange range) {
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
	public void createBarChart(@NonNull TabularData<?> data, @NonNull List<TabularRange> ranges) {
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
	public void createHistogram(@NonNull TabularData<?> data, @NonNull List<TabularRange> ranges) {
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
	public void createLineGraph(@NonNull TabularData<?> data, @NonNull List<TabularRange> ranges) {
		String command = ChartBuilder.getLineGraphCommand(data, ranges);
		if (command == null) {
			return;
		}
		processCommand(command);
	}

	@Override
	public void createLineGraph(@NonNull TabularData<?> data, TabularRange range) {
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
	public void createBoxPlot(@NonNull TabularData<?> data, @NonNull List<TabularRange> ranges) {
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
