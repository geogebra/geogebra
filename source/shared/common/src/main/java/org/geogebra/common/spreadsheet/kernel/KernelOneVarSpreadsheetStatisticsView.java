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

import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.dialog.StatisticGroupsBuilder;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.statistics.AlgoCellRange;
import org.geogebra.common.spreadsheet.core.SpreadsheetReference;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Input.OneVarInput;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Result;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatisticsView;
import org.geogebra.common.spreadsheet.core.TabularRange;

public final class KernelOneVarSpreadsheetStatisticsView
		extends KernelSpreadsheetStatisticsView<OneVarInput>
		implements SpreadsheetStatisticsView.OneVar {

	private @CheckForNull AlgoCellRange algoCellRange;

	KernelOneVarSpreadsheetStatisticsView(@Nonnull Kernel kernel,
			@Nonnull StatisticGroupsBuilder statisticGroupsBuilder,
			@Nonnull TabularRange range) {
		super(kernel, statisticGroupsBuilder, new OneVarInput(range), "1VariableStatistics");
	}

	// -- KernelSpreadsheetStatisticsView --

	@Override
	protected @Nonnull Result calculate(@Nonnull OneVarInput input) {
		SpreadsheetReference inputRange = input.cellRange();
		if (inputRange == null || inputRange.isSingleCell()) {
			return newInvalidResult(
					SpreadsheetStatistics.Error.NUMERIC_DATA_RANGE_REQUIRED,
					SpreadsheetStatistics.DataRange.X);
		}
		algoCellRange = setCellRange(inputRange, algoCellRange);
		algoCellRange.compute();
		GeoList list = algoCellRange.getList();

		GeoList cleanedList = statisticGroupsBuilder.getCleanListOneVariable(list);
		if (cleanedList.isEmptyList()) {
			return newInvalidResult(
					SpreadsheetStatistics.Error.NUMERIC_DATA_RANGE_REQUIRED, null);
		}
		List<StatisticGroup> statistics = statisticGroupsBuilder.buildOneVariableStatistics(
				cleanedList, "x");
		if (statistics.isEmpty()) {
			return newInvalidResult(
					SpreadsheetStatistics.Error.NUMERIC_DATA_RANGE_REQUIRED, null);
		}
		return new SpreadsheetStatistics.Result.Valid(statistics);
	}

	@Override
	protected boolean isWithinAlgoRange(@Nonnull GeoElement element) {
		SpreadsheetReference cellRange = getInput().cellRange();
		return cellRange != null && isElementInRange(element, cellRange);
	}

	@Override
	public void tearDown() {
		super.tearDown();

		if (algoCellRange != null) {
			algoCellRange.remove();
			algoCellRange = null;
		}
	}

	// -- View

	@Override
	public void reset() {
		super.reset();

		if (algoCellRange != null) {
			algoCellRange.remove();
			algoCellRange = null;
		}
	}

	@Override
	public int getViewID() {
		return 727;
	}
}
