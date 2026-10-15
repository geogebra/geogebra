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

package org.geogebra.common.spreadsheet.core;

import static org.geogebra.common.spreadsheet.core.SpreadsheetReferenceParsing.parseReference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.spreadsheet.kernel.KernelSpreadsheetStatistics;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.common.util.MockedCasValues;
import org.geogebra.common.util.MockedCasValuesExtension;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockedCasValuesExtension.class)
class SpreadsheetStatisticsForCasTest extends BaseAppTestSetup {

	private SpreadsheetController<GeoElement> kernelBackedController;

	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.CAS);
		TabularData<GeoElement> kernelTabularData = new KernelTabularDataAdapter(getApp());
		kernelBackedController =
				new SpreadsheetController<>(kernelTabularData, new SpreadsheetStyling());
		SpreadsheetStatistics statistics = new KernelSpreadsheetStatistics(getKernel());
		kernelBackedController.setStatisticsDelegate(() -> {}, statistics);
	}

	@Test
	@MockedCasValues({"Evaluate(1) -> 1", "Evaluate(5) -> 5", "Round(1, 13) -> 1", "Round(5, 13) -> 5"
	})
	void testFrequencyTableCAS() {
		evaluate("A1=1");
		evaluate("A2=1");
		evaluate("A3=5");
		kernelBackedController.select(new TabularRange(0, 0, 2, 0), false, false);
		kernelBackedController.showFrequencyTable();
		SpreadsheetStatisticsView.FrequencyTable view =
				(SpreadsheetStatisticsView.FrequencyTable) kernelBackedController.getStatisticsView();
		assertNotNull(view);

		assertEquals(
				new SpreadsheetStatistics.Input.FrequencyTableInput(
						parseReference("A1:A3"), null, SpreadsheetStatistics.Grouping.VALUES, false),
				view.getInput());
		assertEquals(
				new SpreadsheetStatistics.Result.Tabular(
						List.of("Value", "Frequency"), List.of(List.of("1", "2"), List.of("5", "1"))),
				view.getResult());
	}

	@Test
	@MockedCasValues({"Evaluate(1) -> 1", "Evaluate(5) -> 5", "Round(1, 13) -> 1", "Round(5, 13) -> 5"
	})
	void testFrequencyTableWithClassesCAS() {
		evaluate("A1=1");
		evaluate("A2=1");
		evaluate("A3=5");
		kernelBackedController.select(new TabularRange(0, 0, 2, 0), false, false);
		kernelBackedController.showFrequencyTable();
		SpreadsheetStatisticsView.FrequencyTable view =
				(SpreadsheetStatisticsView.FrequencyTable) kernelBackedController.getStatisticsView();
		assertNotNull(view);
		view.setInput(new SpreadsheetStatistics.Input.FrequencyTableInput(
				parseReference("A1:A3"),
				parseReference("A1:A2"),
				SpreadsheetStatistics.Grouping.INTERVALS,
				false));
		assertEquals(
				new SpreadsheetStatistics.Result.Tabular(
						List.of("Interval", "Frequency"), List.of(List.of("1 ≤ x ≤ 1", "2"))),
				view.getResult());
	}
}
