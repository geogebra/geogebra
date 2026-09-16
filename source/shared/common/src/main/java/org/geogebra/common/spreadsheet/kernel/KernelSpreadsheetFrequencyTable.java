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

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoSort;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.statistics.AlgoCellRange;
import org.geogebra.common.kernel.statistics.AlgoFrequency;
import org.geogebra.common.main.Localization;
import org.geogebra.common.spreadsheet.core.SpreadsheetReference;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatisticsView;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.states.MutableState;
import org.geogebra.common.states.State;
import org.geogebra.editor.share.util.Unicode;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class KernelSpreadsheetFrequencyTable
		extends KernelSpreadsheetStatisticsView<SpreadsheetStatistics.Input.FrequencyTableInput>
		implements SpreadsheetStatisticsView.FrequencyTable {

	private @Nullable AlgoCellRange dataCellRange;
	private @Nullable AlgoCellRange classCellRange;
	private final MutableState<Boolean> classesVisible = new MutableState<>(false);

	KernelSpreadsheetFrequencyTable(
			@NonNull Kernel kernel,
			TabularRange input,
			SpreadsheetStatistics.@NonNull StatisticsReferenceDelegate statisticsReferenceDelegate) {
		super(
				kernel,
				null,
				new SpreadsheetStatistics.Input.FrequencyTableInput(input),
				"FrequencyTable",
				statisticsReferenceDelegate);
	}

	@Override
	protected boolean isWithinAlgoRange(@NonNull GeoElement element) {
		SpreadsheetReference cellRangeX = getInput().dataRange();
		SpreadsheetReference cellRangeY = getInput().classesRange();
		return cellRangeX != null && isElementInRange(element, cellRangeX)
				|| cellRangeY != null && isElementInRange(element, cellRangeY);
	}

	@Override
	protected SpreadsheetStatistics.@NonNull Result calculate(
			SpreadsheetStatistics.Input.@NonNull FrequencyTableInput input) {
		SpreadsheetReference cellRange = input.dataRange();
		boolean valueGrouping = input.grouping() == SpreadsheetStatistics.Grouping.VALUES;
		classesVisible.set(!valueGrouping);
		if (cellRange == null || cellRange.isSingleCell()) {
			return newInvalidResult(
					SpreadsheetStatistics.Error.NUMERIC_DATA_RANGE_REQUIRED,
					SpreadsheetStatistics.DataRange.X);
		}
		dataCellRange = setCellRange(cellRange, dataCellRange);
		Construction cons = kernel.getConstruction();
		if (valueGrouping) {
			AlgoFrequency algoFrequency = new AlgoFrequency(
					cons, new GeoBoolean(cons, input.cumulative()), null, dataCellRange.getList());
			algoFrequency.remove();
			if (!algoFrequency.getResult().isDefined()) {
				return newInvalidResult(
						SpreadsheetStatistics.Error.INVALID_INPUT, SpreadsheetStatistics.DataRange.X);
			}
			return tabularResult(algoFrequency.getValue(), algoFrequency.getResult());
		} else {
			SpreadsheetReference cellRangeY = input.classesRange();
			if (cellRangeY == null || cellRangeY.isSingleCell()) {
				return new SpreadsheetStatistics.Result.Invalid(
						SpreadsheetStatistics.Error.NUMERIC_DATA_RANGE_REQUIRED,
						SpreadsheetStatistics.DataRange.Y);
			}
			classCellRange = setCellRange(cellRangeY, classCellRange);
			AlgoSort sortedClasses = new AlgoSort(cons, classCellRange.getList());
			sortedClasses.remove();
			AlgoFrequency algoFrequency = new AlgoFrequency(
					cons,
					new GeoBoolean(cons, input.cumulative()),
					sortedClasses.getResult(),
					dataCellRange.getList());
			algoFrequency.remove();
			if (!algoFrequency.getResult().isDefined()) {
				return newInvalidResult(
						SpreadsheetStatistics.Error.INVALID_INPUT, SpreadsheetStatistics.DataRange.X);
			}
			return tabularResultClass(sortedClasses.getResult(), algoFrequency.getResult());
		}
	}

	private SpreadsheetStatistics.@NonNull Result tabularResult(GeoList values, GeoList frequencies) {
		List<List<String>> rows = new ArrayList<>();
		for (int i = 0; i < values.size(); i++) {
			rows.add(List.of(
					values.get(i).toValueString(StringTemplate.defaultTemplate),
					frequencies.get(i).toValueString(StringTemplate.defaultTemplate)));
		}
		Localization loc = kernel.getLocalization();
		List<String> headings = List.of(loc.getMenu("Value"), loc.getMenu("Frequency"));
		return new SpreadsheetStatistics.Result.Tabular(headings, rows);
	}

	private SpreadsheetStatistics.@NonNull Result tabularResultClass(
			GeoList boundaries, GeoList frequencies) {
		List<List<String>> rows = new ArrayList<>();
		for (int i = 0; i < frequencies.size(); i++) {
			StringTemplate template = StringTemplate.defaultTemplate;
			String interval = " " + Unicode.LESS_EQUAL + " x "
					+ (i == frequencies.size() - 1 ? Unicode.LESS_EQUAL : "<") + " ";
			rows.add(List.of(
					boundaries.get(i).toValueString(template)
							+ interval
							+ boundaries.get(i + 1).toValueString(template),
					frequencies.get(i).toValueString(template)));
		}
		Localization loc = kernel.getLocalization();
		List<String> headings = List.of(loc.getMenu("Interval"), loc.getMenu("Frequency"));
		return new SpreadsheetStatistics.Result.Tabular(headings, rows);
	}

	@Override
	public void tearDown() {
		super.tearDown();
		if (dataCellRange != null) {
			dataCellRange.remove();
			dataCellRange = null;
		}
		if (classCellRange != null) {
			classCellRange.remove();
			classCellRange = null;
		}
	}

	@Override
	public int getViewID() {
		return 787;
	}

	@Override
	public State<Boolean> getClassesVisible() {
		return classesVisible;
	}

	@Override
	public void updateInputRange(
			@NonNull SpreadsheetReference reference, SpreadsheetStatistics.DataRange range) {
		setInput(
				range == SpreadsheetStatistics.DataRange.X
						? new SpreadsheetStatistics.Input.FrequencyTableInput(
								reference,
								getInput().classesRange(),
								getInput().grouping(),
								getInput().cumulative())
						: new SpreadsheetStatistics.Input.FrequencyTableInput(
								getInput().dataRange(),
								reference,
								getInput().grouping(),
								getInput().cumulative()));
	}
}
