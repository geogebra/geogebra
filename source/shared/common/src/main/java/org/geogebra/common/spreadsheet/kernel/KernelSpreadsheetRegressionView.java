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

import org.geogebra.common.gui.view.table.dialog.RegressionBuilder;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.dialog.StatisticGroupsBuilder;
import org.geogebra.common.gui.view.table.regression.RegressionSpecification;
import org.geogebra.common.gui.view.table.regression.RegressionSpecificationBuilder;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.statistics.AlgoCellRange;
import org.geogebra.common.spreadsheet.core.SpreadsheetReference;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Input.RegressionInput;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Result;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatisticsView;
import org.geogebra.common.spreadsheet.core.TabularRange;

public final class KernelSpreadsheetRegressionView
		extends KernelSpreadsheetStatisticsView<RegressionInput>
		implements SpreadsheetStatisticsView.Regression {

	private @CheckForNull AlgoCellRange algoCellRangeX;
	private @CheckForNull AlgoCellRange algoCellRangeY;
	private final @Nonnull List<RegressionSpecification> regressionSpecifications;

	KernelSpreadsheetRegressionView(@Nonnull Kernel kernel,
			@Nonnull StatisticGroupsBuilder statisticGroupsBuilder,
			@Nonnull RegressionSpecificationBuilder regressionSpecificationBuilder,
			@Nonnull TabularRange range) {
		super(kernel, statisticGroupsBuilder, new RegressionInput(range), "Regression");
		this.regressionSpecifications = regressionSpecificationBuilder.getForListSize(2);
	}

	// -- KernelSpreadsheetStatisticsView --

	@Override
	protected @Nonnull Result calculate(@Nonnull RegressionInput input) {
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
		RegressionSpecification regressionSpecification = input.regression();
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
	protected boolean isWithinAlgoRange(@Nonnull GeoElement element) {
		SpreadsheetReference cellRangeX = getInput().cellRangeX();
		SpreadsheetReference cellRangeY = getInput().cellRangeY();
		return cellRangeX != null && isElementInRange(element, cellRangeX)
				|| cellRangeY != null && isElementInRange(element, cellRangeY);
	}

	@Override
	public @Nonnull List<RegressionSpecification> getRegressionSpecifications() {
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
	}

	@Override
	public int getViewID() {
		return 747;
	}
}
