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

import org.geogebra.common.gui.view.table.dialog.RegressionBuilder;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.dialog.StatisticGroupsBuilder;
import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
import org.geogebra.common.gui.view.table.regression.RegressionSpecificationBuilder;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.statistics.AlgoCellRange;
import org.geogebra.common.spreadsheet.core.SpreadsheetReference;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Input.RegressionInput;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Result;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.StatisticsReferenceDelegate;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatisticsView;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.util.debug.Log;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public final class KernelSpreadsheetRegressionView
		extends KernelSpreadsheetStatisticsView<RegressionInput>
		implements SpreadsheetStatisticsView.Regression {

	private @Nullable AlgoCellRange algoCellRangeX;
	private @Nullable AlgoCellRange algoCellRangeY;
	private final @NonNull List<RegressionSpecification> regressionSpecifications;
	private RegressionSpecification regressionSpecification;

	KernelSpreadsheetRegressionView(@NonNull Kernel kernel,
			@NonNull StatisticGroupsBuilder statisticGroupsBuilder,
			@NonNull RegressionSpecificationBuilder regressionSpecificationBuilder,
			@NonNull TabularRange range,
			@NonNull StatisticsReferenceDelegate statisticsReferenceDelegate) {
		super(kernel, statisticGroupsBuilder, new RegressionInput(range), "Regression",
				statisticsReferenceDelegate);
		this.regressionSpecifications = regressionSpecificationBuilder.getForListSize(2);
		recalculate();
	}

	// -- KernelSpreadsheetStatisticsView --

	@Override
	protected @NonNull Result calculate(@NonNull RegressionInput input) {
		SpreadsheetReference inputRangeX = input.cellRangeX();
		if (inputRangeX == null || inputRangeX.isSingleCell()) {
			return newInvalidResult(
					SpreadsheetStatistics.Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
					SpreadsheetStatistics.DataRange.X);
		}
		SpreadsheetReference inputRangeY = input.cellRangeY();
		if (inputRangeY == null || inputRangeY.isSingleCell()) {
			return newInvalidResult(
					SpreadsheetStatistics.Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
					SpreadsheetStatistics.DataRange.Y);
		}
		algoCellRangeX = setCellRange(inputRangeX, algoCellRangeX);
		algoCellRangeX.compute();
		GeoList listX = algoCellRangeX.getList();

		algoCellRangeY = setCellRange(inputRangeY, algoCellRangeY);
		algoCellRangeY.compute();
		GeoList listY = algoCellRangeY.getList();

		GeoList[] cleanedLists = statisticGroupsBuilder.getCleanListsTwoVariable(listX, listY);
		if (cleanedLists.length < 2
				|| cleanedLists[0].isEmptyList() || cleanedLists[1].isEmptyList()
				|| cleanedLists[0].size() != cleanedLists[1].size()) {
			return newInvalidResult(
					SpreadsheetStatistics.Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
					null);
		}
		regressionSpecification = input.regression();
		if (regressionSpecification == null) {
			regressionSpecification = regressionSpecifications.get(0);
		}
		List<StatisticGroup> statistics = new RegressionBuilder(cleanedLists[0], cleanedLists[1])
				.getRegression(regressionSpecification);
		if (statistics.isEmpty()) {
			return newInvalidResult(
					SpreadsheetStatistics.Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
					null);
		}
		return new SpreadsheetStatistics.Result.Valid(statistics);
	}
	
	@Override
	protected boolean isWithinAlgoRange(@NonNull GeoElement element) {
		SpreadsheetReference cellRangeX = getInput().cellRangeX();
		SpreadsheetReference cellRangeY = getInput().cellRangeY();
		return cellRangeX != null && isElementInRange(element, cellRangeX)
				|| cellRangeY != null && isElementInRange(element, cellRangeY);
	}

	@Override
	public @NonNull List<RegressionSpecification> getRegressionSpecifications() {
		return regressionSpecifications;
	}

	@Override
	public void tearDown() {
		super.tearDown();

		if (algoCellRangeX != null) {
			algoCellRangeX.remove();
			algoCellRangeX = null;
		}
		if (algoCellRangeY != null) {
			algoCellRangeY.remove();
			algoCellRangeY = null;
		}
		regressionSpecification = null;
	}

	// -- View

	@Override
	public void reset() {
		super.reset();

		if (algoCellRangeX != null) {
			algoCellRangeX.remove();
			algoCellRangeX = null;
		}
		if (algoCellRangeY != null) {
			algoCellRangeY.remove();
			algoCellRangeY = null;
		}
		regressionSpecification = null;
	}

	@Override
	public void plotResult() {
		getResult(); // ensure result calculated
		if (regressionSpecification != null && algoCellRangeX != null && algoCellRangeY != null) {
			Command command = regressionSpecification.buildCommand(kernel,
					new MyVecNode(kernel, algoCellRangeX.getList(), algoCellRangeY.getList()));
			EvalInfo info = new EvalInfo(true, true)
					.withSymbolicMode(kernel.getSymbolicMode());
			try {
				kernel.getAlgebraProcessor().processValidExpression(command, info);
				kernel.getApplication().storeUndoInfo();
			} catch (Exception e) {
				Log.error(e);
			}
		}
	}

	@Override
	public int getViewID() {
		return 747;
	}
}
