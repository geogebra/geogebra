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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.gui.view.table.regression.RegressionSpecificationBuilder;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Error;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Grouping;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Input.FrequencyTableInput;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Input.OneVarInput;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Input.RegressionInput;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Input.TwoVarInput;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Result;
import org.geogebra.common.spreadsheet.kernel.KernelSpreadsheetStatistics;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.common.util.shape.Point;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpreadsheetStatisticsTest {

	private AppCommon app;
	private SpreadsheetController<GeoElement> kernelBackedController;
	private TabularData<GeoElement> kernelTabularData;

	@BeforeEach
	void setup() {
		app = AppCommonFactory.create();

		kernelTabularData = new KernelTabularDataAdapter(app);
		kernelBackedController =
				new SpreadsheetController<>(kernelTabularData, new SpreadsheetStyling());

		SpreadsheetStatistics statistics = new KernelSpreadsheetStatistics(app.getKernel());
		kernelBackedController.setStatisticsDelegate(() -> {}, statistics);
	}

	private enum Content {
		NUMBERS,
		TEXT
	}

	private void setupTestData(@NonNull TabularRange range, @NonNull Content content) {
		Construction construction = app.getKernel().getConstruction();
		Random rnd = new Random(1337); // deterministic prng
		for (int row = range.getFromRow(); row <= range.getToRow(); row++) {
			for (int col = range.getFromColumn(); col <= range.getToColumn(); col++) {
				switch (content) {
					case TEXT ->
						kernelTabularData.setContent(row, col, new GeoText(construction, String.valueOf((char)
								('A' + rnd.nextInt(26)))));
					case NUMBERS ->
						kernelTabularData.setContent(row, col, new GeoNumeric(construction, rnd.nextDouble()));
				}
			}
		}
	}

	private void setupTextColumn(int column, String... values) {
		Construction construction = app.getKernel().getConstruction();
		for (int row = 0; row < values.length; row++) {
			kernelTabularData.setContent(row, column, new GeoText(construction, values[row]));
		}
	}

	private void setupColumn(int column, double... values) {
		Construction construction = app.getKernel().getConstruction();
		for (int row = 0; row < values.length; row++) {
			kernelTabularData.setContent(row, column, new GeoNumeric(construction, values[row]));
		}
	}

	// 1-var Statistics

	@Test
	void testOneVarStatistics() {
		TabularRange range = new TabularRange(0, 0, 2, 0);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showOneVarStatistics();
		assertNotNull(oneVarStatisticsView());
		assertEquals(
				new OneVarInput(parseReference("A1:A3")), oneVarStatisticsView().getInput());

		Result.GroupList result =
				assertInstanceOf(Result.GroupList.class, oneVarStatisticsView().getResult());
		assertEquals(11, result.statisticGroups().size());
	}

	@Test
	void testOneVarStatisticsEmptyRange() {
		kernelBackedController.showOneVarStatistics();
		assertNull(oneVarStatisticsView());
	}

	@Test
	void testOneVarStatisticsEntireColumn() {
		TabularRange range = new TabularRange(0, 0, 2, 0);
		setupTestData(range, Content.NUMBERS);
		range = new TabularRange(-1, 0, -1, 0);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showOneVarStatistics();
		assertNotNull(oneVarStatisticsView());

		assertEquals(
				new OneVarInput(parseReference("A1:A100")), oneVarStatisticsView().getInput());
	}

	@Test
	void testOneVarStatisticsAllTextualData() {
		TabularRange range = new TabularRange(0, 0, 2, 0);
		setupTestData(range, Content.TEXT);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showOneVarStatistics();

		assertEquals(
				new Result.Invalid(Error.NUMERIC_DATA_RANGE_REQUIRED, null),
				oneVarStatisticsView().getResult());
	}

	@Test
	void testOneVarStatisticsDifferentRangesGiveDifferentResults() {
		TabularRange range = new TabularRange(0, 0, 2, 0);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showOneVarStatistics();

		assertEquals(
				new OneVarInput(parseReference("A1:A3")), oneVarStatisticsView().getInput());
		Result.GroupList result1 =
				assertInstanceOf(Result.GroupList.class, oneVarStatisticsView().getResult());

		oneVarStatisticsView()
				.setInput(new OneVarInput(new SpreadsheetReference(new TabularRange(0, 0, 1, 0))));
		oneVarStatisticsView().commitInput();
		assertEquals(
				new OneVarInput(parseReference("A1:A2")), oneVarStatisticsView().getInput());
		Result.GroupList result2 =
				assertInstanceOf(Result.GroupList.class, oneVarStatisticsView().getResult());

		assertNotEquals(result1.statisticGroups(), result2.statisticGroups());
	}

	@Test
	@Issue("APPS-7888")
	void testStatisticsInputCanBeRemovedOneCharacterAtATime() {
		setupTestData(new TabularRange(0, 0, 2, 0), Content.NUMBERS);
		kernelBackedController.select(new TabularRange(0, 0, 2, 0), false, false);
		kernelBackedController.showOneVarStatistics();
		assertEquals(
				new OneVarInput(parseReference("A1:A3")), oneVarStatisticsView().getInput());

		oneVarStatisticsView().setInput(new OneVarInput(parseReference("A1:A")));
		assertEquals(
				new OneVarInput((SpreadsheetReference) null), oneVarStatisticsView().getInput());

		oneVarStatisticsView().setInput(new OneVarInput(parseReference("A1:")));
		assertEquals(
				new OneVarInput((SpreadsheetReference) null), oneVarStatisticsView().getInput());

		oneVarStatisticsView().setInput(new OneVarInput(parseReference("A1")));
		assertEquals(new OneVarInput(parseReference("A1")), oneVarStatisticsView().getInput());
	}

	@Test
	void testSetInputBeforeFirstGetResultShouldKeepInitialResult() {
		setupTestData(new TabularRange(0, 0, 2, 0), Content.NUMBERS);
		setupTestData(new TabularRange(0, 2, 1, 2), Content.NUMBERS);
		kernelBackedController.select(new TabularRange(0, 0, 2, 0), false, false);
		kernelBackedController.showOneVarStatistics();

		oneVarStatisticsView().setInput(new OneVarInput(parseReference("C1:C2")));
		Result.GroupList initialResult =
				assertInstanceOf(Result.GroupList.class, oneVarStatisticsView().getResult());

		oneVarStatisticsView().commitInput();
		Result.GroupList committedResult =
				assertInstanceOf(Result.GroupList.class, oneVarStatisticsView().getResult());

		assertNotEquals(initialResult.statisticGroups(), committedResult.statisticGroups());
	}

	@Test
	void testOneVarStatisticsRecalculatesAfterInvalidState() {
		TabularRange range = new TabularRange(0, 0, 1, 0);
		setupTestData(range, Content.NUMBERS);

		kernelBackedController.select(range, false, false);
		kernelBackedController.showOneVarStatistics();
		assertNotNull(oneVarStatisticsView());
		assertInstanceOf(Result.GroupList.class, oneVarStatisticsView().getResult());

		kernelTabularData.removeContentAt(1, 0);
		assertInstanceOf(Result.Invalid.class, oneVarStatisticsView().getResult());

		kernelTabularData.setContent(1, 0, new GeoNumeric(app.getKernel().getConstruction(), 1));
		assertInstanceOf(Result.GroupList.class, oneVarStatisticsView().getResult());
	}

	@Test
	void testOneVarStatisticsInvalidRange() {
		TabularRange range = new TabularRange(0, 0, 2, 0);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(new TabularRange(0, 0), false, false);
		kernelBackedController.showOneVarStatistics();
		assertEquals(
				new Result.Invalid(Error.NUMERIC_DATA_RANGE_REQUIRED, SpreadsheetStatistics.DataRange.X),
				oneVarStatisticsView().getResult());
	}

	@Test
	void testOneVarStatisticsInvalidRangeDrivesUserAttentionOnFirstTry() {
		TabularRange range = new TabularRange(0, 0, 2, 0);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(new TabularRange(0, 0), false, false);
		kernelBackedController.showOneVarStatistics();
		assertEquals(
				new Result.Invalid(Error.NUMERIC_DATA_RANGE_REQUIRED, SpreadsheetStatistics.DataRange.X),
				oneVarStatisticsView().getResult());

		// Second try
		oneVarStatisticsView().setInput(new OneVarInput(new TabularRange(0, 0)));
		oneVarStatisticsView().commitInput();
		assertEquals(
				new Result.Invalid(Error.NUMERIC_DATA_RANGE_REQUIRED, null),
				oneVarStatisticsView().getResult());
	}

	@Test
	void testInvalidInputAfterValidResultShouldNotDriveUserAttention() {
		TabularRange range = new TabularRange(0, 0, 2, 0);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showOneVarStatistics();
		assertInstanceOf(Result.GroupList.class, oneVarStatisticsView().getResult());

		oneVarStatisticsView().setInput(new OneVarInput(parseReference("A1")));
		oneVarStatisticsView().commitInput();

		assertEquals(
				new Result.Invalid(Error.NUMERIC_DATA_RANGE_REQUIRED, null),
				oneVarStatisticsView().getResult());
	}

	@Test
	void testStatisticsReferencesShouldUpdateWhenInputBecomesValidOrInvalid() {
		setupTestData(new TabularRange(0, 0, 1, 0), Content.NUMBERS);
		kernelBackedController.select(new TabularRange(0, 0), false, false);
		kernelBackedController.showOneVarStatistics();
		assertNull(kernelBackedController.getStatisticsReferences());

		oneVarStatisticsView().setFocusedDataRange(SpreadsheetStatistics.DataRange.X);

		assertEquals(new OneVarInput(parseReference("A1")), oneVarStatisticsView().getInput());
		assertEquals(
				new SpreadsheetReferences(List.of(), parseReference("A1")),
				kernelBackedController.getStatisticsReferences());

		oneVarStatisticsView().setInput(new OneVarInput(parseReference("A1:A")));
		assertEquals(
				new SpreadsheetReferences(List.of(), null),
				kernelBackedController.getStatisticsReferences());

		oneVarStatisticsView().setInput(new OneVarInput(parseReference("A1:A2")));
		assertEquals(
				new SpreadsheetReferences(List.of(), parseReference("A1:A2")),
				kernelBackedController.getStatisticsReferences());

		oneVarStatisticsView().setFocusedDataRange(null);
		assertNull(kernelBackedController.getStatisticsReferences());
	}

	@Test
	void testValidInputRangeShouldUpdateForInvalidCalculation() {
		kernelBackedController.select(new TabularRange(0, 0), false, false);
		kernelBackedController.showOneVarStatistics();
		oneVarStatisticsView().setInput(new OneVarInput(parseReference("A1:A2")));
		assertInstanceOf(Result.Invalid.class, oneVarStatisticsView().getResult());
		assertNull(kernelBackedController.getStatisticsReferences());

		oneVarStatisticsView().setFocusedDataRange(SpreadsheetStatistics.DataRange.X);
		assertEquals(
				new SpreadsheetReferences(List.of(), parseReference("A1:A2")),
				kernelBackedController.getStatisticsReferences());
	}

	@Test
	void testInputShouldOnlyRecalculateWhenCommitted() {
		setupTestData(new TabularRange(0, 0, 2, 0), Content.NUMBERS);
		setupTestData(new TabularRange(0, 2, 1, 2), Content.NUMBERS);
		kernelBackedController.select(new TabularRange(0, 0, 2, 0), false, false);
		kernelBackedController.showOneVarStatistics();
		AtomicInteger inputUpdates = new AtomicInteger(0);
		AtomicInteger resultUpdates = new AtomicInteger(0);
		oneVarStatisticsView().setInputChangeListener(input -> inputUpdates.incrementAndGet());
		oneVarStatisticsView().setResultChangeListener(result -> resultUpdates.incrementAndGet());
		oneVarStatisticsView().setFocusedDataRange(SpreadsheetStatistics.DataRange.X);
		Result originalResult = oneVarStatisticsView().getResult();
		assertEquals(0, inputUpdates.get());
		assertEquals(0, resultUpdates.get());

		oneVarStatisticsView().setInput(new OneVarInput(parseReference("C1:C2")));

		assertEquals(
				new OneVarInput(parseReference("C1:C2")), oneVarStatisticsView().getInput());
		assertEquals(originalResult, oneVarStatisticsView().getResult());
		assertEquals(1, inputUpdates.get());
		assertEquals(0, resultUpdates.get());

		oneVarStatisticsView().setInput(new OneVarInput(parseReference("C1:C2")));

		assertEquals(2, inputUpdates.get());

		oneVarStatisticsView().commitInput();

		assertNotEquals(originalResult, oneVarStatisticsView().getResult());
		assertEquals(1, resultUpdates.get());
	}

	@Test
	void testDraggingWithoutFocusedStatisticsRangeShouldNotUpdateInput() {
		setupTestData(new TabularRange(0, 0, 2, 2), Content.NUMBERS);
		kernelBackedController.select(new TabularRange(0, 0, 2, 0), false, false);
		kernelBackedController.showOneVarStatistics();
		OneVarInput originalInput = oneVarStatisticsView().getInput();

		dragCells(0, 2, 1, 2);

		assertEquals(originalInput, oneVarStatisticsView().getInput());
	}

	@Test
	void testDraggingShouldUpdateFocusedYRangeAndPreserveXRange() {
		setupTestData(new TabularRange(0, 0, 2, 2), Content.NUMBERS);
		kernelBackedController.select(new TabularRange(0, 0, 2, 1), false, false);
		kernelBackedController.showTwoVarStatistics();
		TwoVarInput originalInput = twoVarStatisticsView().getInput();
		twoVarStatisticsView().setFocusedDataRange(SpreadsheetStatistics.DataRange.Y);

		dragCells(0, 2, 2, 2);

		assertEquals(
				new TwoVarInput(originalInput.cellRangeX(), parseReference("C1:C3")),
				twoVarStatisticsView().getInput());
	}

	@Test
	void testDraggingShouldPreviewFocusedStatisticsRangeAndCommitOnPointerUp() {
		setupTestData(new TabularRange(0, 0, 2, 2), Content.NUMBERS);
		kernelBackedController.select(new TabularRange(0, 0, 2, 1), false, false);
		kernelBackedController.showRegression();
		regressionView().setFocusedDataRange(SpreadsheetStatistics.DataRange.X);
		RegressionInput originalInput = regressionView().getInput();
		Result originalResult = regressionView().getResult();
		AtomicInteger inputUpdates = new AtomicInteger(0);
		AtomicInteger resultUpdates = new AtomicInteger(0);
		regressionView().setInputChangeListener(input -> inputUpdates.incrementAndGet());
		regressionView().setResultChangeListener(result -> resultUpdates.incrementAndGet());
		Point start = SpreadsheetTestHelpers.getCellCenter(kernelBackedController, 0, 2);
		Point end = SpreadsheetTestHelpers.getCellCenter(kernelBackedController, 2, 2);

		kernelBackedController.handlePointerDown(start.x, start.y, Modifiers.NONE);
		kernelBackedController.handlePointerMove(end.x, end.y, Modifiers.NONE);

		assertEquals(
				new RegressionInput(
						parseReference("C1:C3"), originalInput.cellRangeY(), originalInput.regression()),
				regressionView().getInput());
		assertEquals(originalResult, regressionView().getResult());
		assertEquals(0, resultUpdates.get());
		assertEquals(2, inputUpdates.get());

		kernelBackedController.handlePointerUp(end.x, end.y, Modifiers.NONE);

		assertEquals(
				new RegressionInput(
						parseReference("C1:C3"), originalInput.cellRangeY(), originalInput.regression()),
				regressionView().getInput());
		assertEquals(1, resultUpdates.get());
		assertEquals(3, inputUpdates.get());
	}

	@Test
	void testClosingStatisticsViewShouldClearState() {
		setupTestData(new TabularRange(0, 0, 2, 0), Content.NUMBERS);
		kernelBackedController.select(new TabularRange(0, 0, 2, 0), false, false);
		kernelBackedController.showOneVarStatistics();
		assertNotNull(kernelBackedController.getStatisticsView());
		assertNull(kernelBackedController.getStatisticsReferences());

		oneVarStatisticsView().setFocusedDataRange(SpreadsheetStatistics.DataRange.X);
		assertNotNull(kernelBackedController.getStatisticsReferences());

		kernelBackedController.closeStatisticsView();

		assertNull(kernelBackedController.getStatisticsView());
		assertNull(kernelBackedController.getStatisticsReferences());
	}

	@Test
	void testStatisticsDelegateShouldReceiveCurrentView() {
		List<SpreadsheetStatisticsView<?>> notifiedViews = new ArrayList<>();
		kernelBackedController.setStatisticsDelegate(
				() -> notifiedViews.add(kernelBackedController.getStatisticsView()),
				new KernelSpreadsheetStatistics(app.getKernel()));
		setupTestData(new TabularRange(0, 0, 2, 1), Content.NUMBERS);
		kernelBackedController.select(new TabularRange(0, 0, 2, 1), false, false);

		kernelBackedController.showOneVarStatistics();
		SpreadsheetStatisticsView<?> oneVarView = kernelBackedController.getStatisticsView();
		kernelBackedController.showTwoVarStatistics();
		SpreadsheetStatisticsView<?> twoVarView = kernelBackedController.getStatisticsView();
		kernelBackedController.closeStatisticsView();

		assertEquals(3, notifiedViews.size());
		assertEquals(oneVarView, notifiedViews.get(0));
		assertEquals(twoVarView, notifiedViews.get(1));
		assertNull(notifiedViews.get(2));
	}

	// 2-var Statistics

	@Test
	void testTwoVarStatistics() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showTwoVarStatistics();
		assertNotNull(twoVarStatisticsView());

		assertEquals(
				new TwoVarInput(parseReference("A1:A3"), parseReference("B1:B3")),
				twoVarStatisticsView().getInput());

		Result.GroupList result =
				assertInstanceOf(Result.GroupList.class, twoVarStatisticsView().getResult());
		assertEquals(18, result.statisticGroups().size());
	}

	@Test
	void testFocusedReferenceShouldBeSeparatedFromUnfocusedReferences() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showTwoVarStatistics();

		twoVarStatisticsView().setFocusedDataRange(SpreadsheetStatistics.DataRange.X);
		assertEquals(
				new SpreadsheetReferences(List.of(parseReference("B1:B3")), parseReference("A1:A3")),
				kernelBackedController.getStatisticsReferences());

		twoVarStatisticsView().setFocusedDataRange(SpreadsheetStatistics.DataRange.Y);
		assertEquals(
				new SpreadsheetReferences(List.of(parseReference("A1:A3")), parseReference("B1:B3")),
				kernelBackedController.getStatisticsReferences());

		twoVarStatisticsView().setFocusedDataRange(SpreadsheetStatistics.DataRange.X);
		twoVarStatisticsView()
				.setInput(new TwoVarInput(parseReference("A1:A3"), parseReference("A1:A3")));
		assertEquals(
				new SpreadsheetReferences(List.of(parseReference("A1:A3")), parseReference("A1:A3")),
				kernelBackedController.getStatisticsReferences());

		twoVarStatisticsView().setInput(new TwoVarInput(null, parseReference("B1:B3")));
		assertEquals(
				new SpreadsheetReferences(List.of(parseReference("B1:B3")), null),
				kernelBackedController.getStatisticsReferences());

		twoVarStatisticsView().setFocusedDataRange(null);
		assertNull(kernelBackedController.getStatisticsReferences());
	}

	@Test
	void testTwoVarStatisticsEmptyRange() {
		kernelBackedController.showTwoVarStatistics();
		assertNull(twoVarStatisticsView());
	}

	@Test
	void testTwoVarStatisticsAllTextualData() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.TEXT);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showTwoVarStatistics();

		assertEquals(
				new Result.Invalid(Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED, null),
				twoVarStatisticsView().getResult());
	}

	@Test
	void testTwoVarStatisticsDifferentRangesGiveDifferentResults() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showTwoVarStatistics();

		assertEquals(
				new TwoVarInput(parseReference("A1:A3"), parseReference("B1:B3")),
				twoVarStatisticsView().getInput());
		Result.GroupList result1 =
				assertInstanceOf(Result.GroupList.class, twoVarStatisticsView().getResult());

		twoVarStatisticsView()
				.setInput(new TwoVarInput(
						new SpreadsheetReference(new TabularRange(0, 0, 1, 0)),
						new SpreadsheetReference(new TabularRange(0, 1, 1, 1))));
		twoVarStatisticsView().commitInput();
		assertEquals(
				new TwoVarInput(parseReference("A1:A2"), parseReference("B1:B2")),
				twoVarStatisticsView().getInput());
		Result.GroupList result2 =
				assertInstanceOf(Result.GroupList.class, twoVarStatisticsView().getResult());

		assertNotEquals(result1.statisticGroups(), result2.statisticGroups());
	}

	@Test
	void testTwoVarStatisticsSingleCellInvalidRange() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);

		kernelBackedController.select(new TabularRange(0, 0), false, false);
		kernelBackedController.showTwoVarStatistics();

		assertEquals(
				new Result.Invalid(
						Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
						SpreadsheetStatistics.DataRange.X),
				twoVarStatisticsView().getResult());
	}

	@Test
	void testTwoVarStatisticsSingleColumnInvalidRange() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);

		kernelBackedController.select(range.firstColumn(), false, false);
		kernelBackedController.showTwoVarStatistics();

		assertEquals(
				new Result.Invalid(
						Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
						SpreadsheetStatistics.DataRange.Y),
				twoVarStatisticsView().getResult());
	}

	@Test
	void testTwoVarStatisticsInvalidRangeDrivesUserAttentionOnFirstTry() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range.firstColumn(), false, false);
		kernelBackedController.showTwoVarStatistics();
		assertEquals(
				new Result.Invalid(
						Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
						SpreadsheetStatistics.DataRange.Y),
				twoVarStatisticsView().getResult());

		// Second try
		twoVarStatisticsView().setInput(new TwoVarInput(range.firstColumn()));
		twoVarStatisticsView().commitInput();
		assertEquals(
				new Result.Invalid(Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED, null),
				twoVarStatisticsView().getResult());
	}

	@Test
	void testTwoVarStatisticsEntireColumns() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		TabularRange columns = new TabularRange(-1, 0, -1, 1);
		kernelBackedController.select(columns, false, false);
		kernelBackedController.showTwoVarStatistics();

		assertEquals(
				new TwoVarInput(parseReference("A1:A100"), parseReference("B1:B100")),
				twoVarStatisticsView().getInput());
	}

	// Regression

	@Test
	void testRegression() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showRegression();
		assertNotNull(regressionView());

		assertEquals(
				new RegressionInput(parseReference("A1:A3"), parseReference("B1:B3"), null),
				regressionView().getInput());

		Result.GroupList result =
				assertInstanceOf(Result.GroupList.class, regressionView().getResult());
		assertEquals(4, result.statisticGroups().size());
	}

	@Test
	void testRegressionEmptyRange() {
		kernelBackedController.showRegression();
		assertNull(regressionView());
	}

	@Test
	void testRegressionAllTextualData() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.TEXT);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showRegression();

		assertEquals(
				new Result.Invalid(Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED, null),
				regressionView().getResult());
	}

	@Test
	void testRegressionDifferentRangesGiveDifferentResults() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showRegression();

		assertEquals(
				new RegressionInput(parseReference("A1:A3"), parseReference("B1:B3"), null),
				regressionView().getInput());
		Result.GroupList result1 =
				assertInstanceOf(Result.GroupList.class, regressionView().getResult());

		regressionView()
				.setInput(new RegressionInput(
						new SpreadsheetReference(new TabularRange(0, 0, 1, 0)),
						new SpreadsheetReference(new TabularRange(0, 1, 1, 1)),
						null));
		regressionView().commitInput();
		assertEquals(
				new RegressionInput(parseReference("A1:A2"), parseReference("B1:B2"), null),
				regressionView().getInput());
		Result.GroupList result2 =
				assertInstanceOf(Result.GroupList.class, regressionView().getResult());

		assertNotEquals(result1.statisticGroups(), result2.statisticGroups());
	}

	@Test
	void testRegressionDifferentSpecificationGiveDifferentResults() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showRegression();

		assertEquals(
				new RegressionInput(parseReference("A1:A3"), parseReference("B1:B3"), null),
				regressionView().getInput());
		Result.GroupList result1 =
				assertInstanceOf(Result.GroupList.class, regressionView().getResult());

		regressionView()
				.setInput(new RegressionInput(
						parseReference("A1:A3"),
						parseReference("B1:B3"),
						// Different regression
						regressionView().getRegressionSpecifications().get(1)));
		regressionView().commitInput();

		Result.GroupList result2 =
				assertInstanceOf(Result.GroupList.class, regressionView().getResult());

		assertNotEquals(result1.statisticGroups(), result2.statisticGroups());
	}

	@Test
	void testRegressionEntireColumns() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		TabularRange columns = new TabularRange(-1, 0, -1, 1);
		kernelBackedController.select(columns, false, false);
		kernelBackedController.showRegression();

		assertEquals(
				new RegressionInput(parseReference("A1:A100"), parseReference("B1:B100"), null),
				regressionView().getInput());
	}

	@Test
	void testRegressionPlot() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showRegression();
		regressionView().plotResult();
		GeoElement function = Objects.requireNonNull(app.getKernel().lookupLabel("f"));
		assertEquals("-0.13547x + 0.9208", function.toValueString(StringTemplate.editTemplate));
		regressionView()
				.setInput(new SpreadsheetStatistics.Input.RegressionInput(
						parseReference("A1:A3"),
						parseReference("B1:B3"),
						new RegressionSpecificationBuilder().getForListSize(3).get(2)));
		regressionView().commitInput();
		regressionView().plotResult();
		assertEquals(
				"0.7787x^-0.09252",
				app.getKernel().lookupLabel("g").toValueString(StringTemplate.editTemplate));
	}

	@Test
	void testAllRegressionOptionsAvailable() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showRegression();
		assertEquals(10, regressionView().getRegressionSpecifications().size());
	}

	@Test
	void testRegressionSingleCellInvalidRange() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);

		kernelBackedController.select(new TabularRange(0, 0), false, false);
		kernelBackedController.showRegression();

		assertEquals(
				new Result.Invalid(
						Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
						SpreadsheetStatistics.DataRange.X),
				regressionView().getResult());
	}

	@Test
	void testRegressionSingleColumnInvalidRange() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);

		kernelBackedController.select(range.firstColumn(), false, false);
		kernelBackedController.showRegression();

		assertEquals(
				new Result.Invalid(
						Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
						SpreadsheetStatistics.DataRange.Y),
				regressionView().getResult());
	}

	@Test
	void testRegressionInvalidRangeDrivesUserAttentionOnFirstTry() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range.firstColumn(), false, false);
		kernelBackedController.showRegression();
		assertEquals(
				new Result.Invalid(
						Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
						SpreadsheetStatistics.DataRange.Y),
				regressionView().getResult());

		// Second try
		regressionView().setInput(new RegressionInput(range.firstColumn()));
		regressionView().commitInput();
		assertEquals(
				new Result.Invalid(Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED, null),
				regressionView().getResult());
	}

	// Frequency table

	@Test
	void testFrequencyTable() {
		setupColumn(0, 1, 2, 2, 3, 3, 3);
		kernelBackedController.select(new TabularRange(0, 0, 5, 0), false, false);
		kernelBackedController.showFrequencyTable();
		assertNotNull(frequencyTableView());

		assertEquals(
				new FrequencyTableInput(parseReference("A1:A6"), null, Grouping.VALUES, false),
				frequencyTableView().getInput());
		assertEquals(
				new Result.Tabular(
						List.of("Value", "Frequency"),
						List.of(List.of("1", "1"), List.of("2", "2"), List.of("3", "3"))),
				frequencyTableView().getResult());
	}

	@Test
	void testFrequencyTableTitle() {
		setupColumn(0, 1, 2, 2);
		kernelBackedController.select(new TabularRange(0, 0, 2, 0), false, false);
		kernelBackedController.showFrequencyTable();

		assertEquals("FrequencyTable", frequencyTableView().getTitleLocalizationKey());
	}

	@Test
	void testFrequencyTableEmptyRange() {
		kernelBackedController.showFrequencyTable();
		assertNull(frequencyTableView());
	}

	@Test
	void testFrequencyTableEntireColumn() {
		setupColumn(0, 1, 2, 2);
		kernelBackedController.select(new TabularRange(-1, 0, -1, 0), false, false);
		kernelBackedController.showFrequencyTable();

		assertEquals(
				new FrequencyTableInput(parseReference("A1:A100"), null, Grouping.VALUES, false),
				frequencyTableView().getInput());
		assertEquals(
				new Result.Tabular(
						List.of("Value", "Frequency"), List.of(List.of("1", "1"), List.of("2", "2"))),
				frequencyTableView().getResult());
	}

	@Test
	void testFrequencyTableTextualData() {
		setupTextColumn(0, "b", "a", "b", "c", "b");
		kernelBackedController.select(new TabularRange(0, 0, 4, 0), false, false);
		kernelBackedController.showFrequencyTable();

		assertEquals(
				new Result.Tabular(
						List.of("Value", "Frequency"),
						List.of(List.of("a", "1"), List.of("b", "3"), List.of("c", "1"))),
				frequencyTableView().getResult());
	}

	@Test
	void testFrequencyTableCumulative() {
		setupColumn(0, 1, 2, 2, 3, 3, 3);
		kernelBackedController.select(new TabularRange(0, 0, 5, 0), false, false);
		kernelBackedController.showFrequencyTable();

		frequencyTableView()
				.setInput(new FrequencyTableInput(parseReference("A1:A6"), null, Grouping.VALUES, true));
		frequencyTableView().commitInput();

		assertEquals(
				new Result.Tabular(
						List.of("Value", "Frequency"),
						List.of(List.of("1", "1"), List.of("2", "3"), List.of("3", "6"))),
				frequencyTableView().getResult());
	}

	@Test
	void testFrequencyTableIntervals() {
		setupColumn(0, 1, 2, 2, 3, 3, 3);
		setupColumn(1, 0, 2, 4);
		kernelBackedController.select(new TabularRange(0, 0, 5, 1), false, false);
		kernelBackedController.showFrequencyTable();
		assertEquals(
				new FrequencyTableInput(
						parseReference("A1:A6"), parseReference("B1:B6"), Grouping.VALUES, false),
				frequencyTableView().getInput());

		frequencyTableView()
				.setInput(new FrequencyTableInput(
						parseReference("A1:A6"), parseReference("B1:B3"), Grouping.INTERVALS, false));
		frequencyTableView().commitInput();

		assertEquals(
				new Result.Tabular(
						List.of("Interval", "Frequency"),
						List.of(
								List.of("0 " + Unicode.LESS_EQUAL + " x < 2", "1"),
								List.of("2 " + Unicode.LESS_EQUAL + " x " + Unicode.LESS_EQUAL + " 4", "5"))),
				frequencyTableView().getResult());
	}

	@Test
	void testFrequencyTableIntervalsCumulative() {
		setupColumn(0, 1, 2, 2, 3, 3, 3);
		setupColumn(1, 0, 2, 4);
		kernelBackedController.select(new TabularRange(0, 0, 5, 0), false, false);
		kernelBackedController.showFrequencyTable();

		frequencyTableView()
				.setInput(new FrequencyTableInput(
						parseReference("A1:A6"), parseReference("B1:B3"), Grouping.INTERVALS, true));
		frequencyTableView().commitInput();

		Result.Tabular result =
				assertInstanceOf(Result.Tabular.class, frequencyTableView().getResult());
		assertEquals(
				List.of("1", "6"), result.data().stream().map(row -> row.get(1)).toList());
	}

	@Test
	void testFrequencyTableSingleCellInvalidRange() {
		setupColumn(0, 1, 2, 2);
		kernelBackedController.select(new TabularRange(0, 0), false, false);
		kernelBackedController.showFrequencyTable();

		assertEquals(
				new Result.Invalid(Error.NUMERIC_DATA_RANGE_REQUIRED, SpreadsheetStatistics.DataRange.X),
				frequencyTableView().getResult());
	}

	@Test
	void testFrequencyTableIntervalsWithoutClassRange() {
		setupColumn(0, 1, 2, 2);
		kernelBackedController.select(new TabularRange(0, 0, 2, 0), false, false);
		kernelBackedController.showFrequencyTable();
		assertInstanceOf(Result.Tabular.class, frequencyTableView().getResult());

		frequencyTableView()
				.setInput(
						new FrequencyTableInput(parseReference("A1:A3"), null, Grouping.INTERVALS, false));
		frequencyTableView().commitInput();

		assertEquals(
				new Result.Invalid(Error.NUMERIC_DATA_RANGE_REQUIRED, SpreadsheetStatistics.DataRange.Y),
				frequencyTableView().getResult());
	}

	@Test
	void testFrequencyTableIntervalsNotNumeric() {
		setupColumn(0, 1, 2, 2);
		setupTextColumn(1, "foo", "bar");
		kernelBackedController.select(new TabularRange(0, 0, 2, 0), false, false);
		kernelBackedController.showFrequencyTable();
		assertInstanceOf(Result.Tabular.class, frequencyTableView().getResult());

		frequencyTableView()
				.setInput(new FrequencyTableInput(
						parseReference("A1:A3"), parseReference("B1:B2"), Grouping.INTERVALS, false));
		frequencyTableView().commitInput();

		assertEquals(
				new Result.Invalid(Error.INVALID_INPUT, null), frequencyTableView().getResult());
	}

	@Test
	void testFrequencyTableIntervalsWithSingleCellClassRange() {
		setupColumn(0, 1, 2, 2);
		setupColumn(1, 0);
		kernelBackedController.select(new TabularRange(0, 0, 2, 1), false, false);
		kernelBackedController.showFrequencyTable();

		frequencyTableView()
				.setInput(new FrequencyTableInput(
						parseReference("A1:A3"), parseReference("B1"), Grouping.INTERVALS, false));
		frequencyTableView().commitInput();

		assertEquals(
				new Result.Invalid(Error.NUMERIC_DATA_RANGE_REQUIRED, SpreadsheetStatistics.DataRange.Y),
				frequencyTableView().getResult());
	}

	@Test
	void testFrequencyTableUpdatesWhenDataChanges() {
		setupColumn(0, 1, 2, 2);
		kernelBackedController.select(new TabularRange(0, 0, 2, 0), false, false);
		kernelBackedController.showFrequencyTable();
		assertEquals(
				new Result.Tabular(
						List.of("Value", "Frequency"), List.of(List.of("1", "1"), List.of("2", "2"))),
				frequencyTableView().getResult());

		kernelTabularData.removeContentAt(2, 0);

		assertEquals(
				new Result.Tabular(
						List.of("Value", "Frequency"), List.of(List.of("1", "1"), List.of("2", "1"))),
				frequencyTableView().getResult());

		kernelTabularData.setContent(2, 0, new GeoNumeric(app.getKernel().getConstruction(), 1));

		assertEquals(
				new Result.Tabular(
						List.of("Value", "Frequency"), List.of(List.of("1", "2"), List.of("2", "1"))),
				frequencyTableView().getResult());
	}

	@Test
	void testFrequencyTableShouldOnlyRecalculateWhenCommitted() {
		setupColumn(0, 1, 2, 2, 3, 3, 3);
		kernelBackedController.select(new TabularRange(0, 0, 5, 0), false, false);
		kernelBackedController.showFrequencyTable();
		Result originalResult = frequencyTableView().getResult();
		AtomicInteger inputUpdates = new AtomicInteger(0);
		AtomicInteger resultUpdates = new AtomicInteger(0);
		frequencyTableView().setInputChangeListener(input -> inputUpdates.incrementAndGet());
		frequencyTableView().setResultChangeListener(result -> resultUpdates.incrementAndGet());

		frequencyTableView()
				.setInput(new FrequencyTableInput(parseReference("A1:A6"), null, Grouping.VALUES, true));

		assertEquals(1, inputUpdates.get());
		assertEquals(0, resultUpdates.get());
		assertEquals(originalResult, frequencyTableView().getResult());

		frequencyTableView().commitInput();

		assertEquals(1, resultUpdates.get());
		assertNotEquals(originalResult, frequencyTableView().getResult());
	}

	@Test
	void testFrequencyTableReplacesPreviousStatisticsView() {
		setupColumn(0, 1, 2, 2);
		kernelBackedController.select(new TabularRange(0, 0, 2, 0), false, false);
		kernelBackedController.showOneVarStatistics();
		assertInstanceOf(
				SpreadsheetStatisticsView.OneVar.class, kernelBackedController.getStatisticsView());

		kernelBackedController.showFrequencyTable();

		assertInstanceOf(
				SpreadsheetStatisticsView.FrequencyTable.class, kernelBackedController.getStatisticsView());

		kernelBackedController.closeStatisticsView();

		assertNull(kernelBackedController.getStatisticsView());
	}

	/**
	 * Expected: boundaries {4, 2, 0} produce labels matching the sorted intervals used for
	 * counting: 0 <= x < 2 with frequency 1, and 2 <= x <= 4 with frequency 5.
	 * Current failure: frequencies use sorted boundaries, but labels use the original order,
	 * producing 4 <= x < 2 and 2 <= x <= 0 with frequencies 1 and 5, respectively.
	 */
	@Test
	void testFrequencyTableDescendingBoundariesMatchIntervalLabels() {
		setupColumn(0, 1, 2, 2, 3, 3, 3);
		setupColumn(1, 4, 2, 0);
		kernelBackedController.select(new TabularRange(0, 0, 5, 0), false, false);
		kernelBackedController.showFrequencyTable();
		frequencyTableView()
				.setInput(new FrequencyTableInput(
						parseReference("A1:A6"), parseReference("B1:B3"), Grouping.INTERVALS, false));
		frequencyTableView().commitInput();

		assertEquals(
				new Result.Tabular(
						List.of("Interval", "Frequency"),
						List.of(
								List.of("0 " + Unicode.LESS_EQUAL + " x < 2", "1"),
								List.of("2 " + Unicode.LESS_EQUAL + " x " + Unicode.LESS_EQUAL + " 4", "5"))),
				frequencyTableView().getResult());
	}

	/**
	 * Expected: switching to Intervals shows Class Boundaries even when Data is the invalid
	 * single-cell reference A1, because visibility depends on grouping rather than data validity.
	 * Current failure: data validation returns before updating visibility, so Class Boundaries
	 * remains hidden and the assertion receives false instead of true.
	 */
	@Test
	void testFrequencyTableClassesVisibleWithInvalidData() {
		kernelBackedController.select(new TabularRange(0, 0), false, false);
		kernelBackedController.showFrequencyTable();
		frequencyTableView().getResult();
		frequencyTableView()
				.setInput(new FrequencyTableInput(parseReference("A1"), null, Grouping.INTERVALS, false));
		frequencyTableView().commitInput();

		assertEquals(true, frequencyTableView().getClassesVisible().get());
	}

	private SpreadsheetStatisticsView.OneVar oneVarStatisticsView() {
		return (SpreadsheetStatisticsView.OneVar) kernelBackedController.getStatisticsView();
	}

	private SpreadsheetStatisticsView.TwoVar twoVarStatisticsView() {
		return (SpreadsheetStatisticsView.TwoVar) kernelBackedController.getStatisticsView();
	}

	private SpreadsheetStatisticsView.Regression regressionView() {
		return (SpreadsheetStatisticsView.Regression) kernelBackedController.getStatisticsView();
	}

	private SpreadsheetStatisticsView.FrequencyTable frequencyTableView() {
		return (SpreadsheetStatisticsView.FrequencyTable) kernelBackedController.getStatisticsView();
	}

	private void dragCells(int fromRow, int fromColumn, int toRow, int toColumn) {
		Point start = SpreadsheetTestHelpers.getCellCenter(kernelBackedController, fromRow, fromColumn);
		Point end = SpreadsheetTestHelpers.getCellCenter(kernelBackedController, toRow, toColumn);
		kernelBackedController.handlePointerDown(start.x, start.y, Modifiers.NONE);
		kernelBackedController.handlePointerMove(end.x, end.y, Modifiers.NONE);
		kernelBackedController.handlePointerUp(end.x, end.y, Modifiers.NONE);
	}
}
