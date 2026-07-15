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

import java.util.Random;

import javax.annotation.Nonnull;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Error;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Input.OneVarInput;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Input.RegressionInput;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Input.TwoVarInput;
import org.geogebra.common.spreadsheet.core.SpreadsheetStatistics.Result;
import org.geogebra.common.spreadsheet.kernel.KernelSpreadsheetStatistics;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpreadsheetStatisticsTest implements SpreadsheetStatisticsDelegate {

	private AppCommon app;
	private SpreadsheetController<GeoElement> kernelBackedController;
	private TabularData<GeoElement> kernelTabularData;
	private SpreadsheetStatisticsView.OneVar oneVarStatisticsView;
	private SpreadsheetStatisticsView.TwoVar twoVarStatisticsView;
	private SpreadsheetStatisticsView.Regression regressionView;

	@BeforeEach
	public void setup() {
		app = AppCommonFactory.create();

		kernelTabularData = new KernelTabularDataAdapter(app);
		kernelBackedController = new SpreadsheetController<>(kernelTabularData,
				new SpreadsheetStyling());

		SpreadsheetStatistics statistics = new KernelSpreadsheetStatistics(app.getKernel());
		kernelBackedController.setStatisticsDelegate(this, statistics);
	}

	private enum Content {
		NUMBERS, TEXT
	}

	private void setupTestData(@Nonnull TabularRange range, @Nonnull Content content) {
		Construction construction = app.getKernel().getConstruction();
		Random rnd = new Random(1337); // deterministic prng
		for (int row = range.getFromRow(); row <= range.getToRow(); row++) {
			for (int col = range.getFromColumn(); col <= range.getToColumn(); col++) {
				switch (content) {
				case TEXT -> kernelTabularData.setContent(row, col,
						new GeoText(construction,
								String.valueOf((char) ('A' + rnd.nextInt(26)))));
				case NUMBERS -> kernelTabularData.setContent(row, col,
						new GeoNumeric(construction, rnd.nextDouble()));
				}
			}
		}
	}

	// 1-var Statistics

	@Test
	public void testOneVarStatistics() {
		TabularRange range = new TabularRange(0, 0, 2, 0);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showOneVarStatistics();
		assertNotNull(oneVarStatisticsView);
		assertEquals(new OneVarInput(parseReference("A1:A3")),
				oneVarStatisticsView.getInput());

		Result.Valid result = assertInstanceOf(Result.Valid.class,
				oneVarStatisticsView.getResult());
		assertEquals(11, result.statisticGroups().size());
	}

	@Test
	public void testOneVarStatisticsEmptyRange() {
		kernelBackedController.showOneVarStatistics();
		assertNull(oneVarStatisticsView);
	}

	@Test
	public void testOneVarStatisticsEntireColumn() {
		TabularRange range = new TabularRange(0, 0, 2, 0);
		setupTestData(range, Content.NUMBERS);
		range = new TabularRange(-1, 0, -1, 0);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showOneVarStatistics();
		assertNotNull(oneVarStatisticsView);

		assertEquals(new OneVarInput(
						// an unbounded TabularRange will give a null cell range
						(SpreadsheetReference) null),
				oneVarStatisticsView.getInput());
		assertEquals(new Result.Invalid(Error.NUMERIC_DATA_RANGE_REQUIRED,
				SpreadsheetStatistics.DataRange.X), oneVarStatisticsView.getResult());
	}

	@Test
	public void testOneVarStatisticsAllTextualData() {
		TabularRange range = new TabularRange(0, 0, 2, 0);
		setupTestData(range, Content.TEXT);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showOneVarStatistics();

		assertEquals(new Result.Invalid(Error.NUMERIC_DATA_RANGE_REQUIRED,
				null), oneVarStatisticsView.getResult());
	}

	@Test
	public void testOneVarStatisticsDifferentRangesGiveDifferentResults() {
		TabularRange range = new TabularRange(0, 0, 2, 0);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showOneVarStatistics();

		assertEquals(new OneVarInput(parseReference("A1:A3")),
				oneVarStatisticsView.getInput());
		Result.Valid result1 = assertInstanceOf(Result.Valid.class,
				oneVarStatisticsView.getResult());

		oneVarStatisticsView.setInput(new OneVarInput(
				new SpreadsheetReference(new TabularRange(0, 0, 1, 0))
		));
		assertEquals(new OneVarInput(parseReference("A1:A2")),
				oneVarStatisticsView.getInput());
		Result.Valid result2 = assertInstanceOf(Result.Valid.class,
				oneVarStatisticsView.getResult());

		assertNotEquals(result1.statisticGroups(), result2.statisticGroups());
	}

	@Test
	public void testOneVarStatisticsRecalculatesAfterInvalidState() {
		TabularRange range = new TabularRange(0, 0, 1, 0);
		setupTestData(range, Content.NUMBERS);

		kernelBackedController.select(range, false, false);
		kernelBackedController.showOneVarStatistics();
		assertNotNull(oneVarStatisticsView);
		assertInstanceOf(Result.Valid.class, oneVarStatisticsView.getResult());

		kernelTabularData.removeContentAt(1, 0);
		assertInstanceOf(Result.Invalid.class, oneVarStatisticsView.getResult());

		kernelTabularData.setContent(1, 0,
				new GeoNumeric(app.getKernel().getConstruction(), 1));
		assertInstanceOf(Result.Valid.class, oneVarStatisticsView.getResult());
	}

	@Test
	public void testOneVarStatisticsInvalidRange() {
		TabularRange range = new TabularRange(0, 0, 2, 0);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(new TabularRange(0, 0), false, false);
		kernelBackedController.showOneVarStatistics();
		assertEquals(new Result.Invalid(Error.NUMERIC_DATA_RANGE_REQUIRED,
				SpreadsheetStatistics.DataRange.X), oneVarStatisticsView.getResult());
	}

	@Test
	public void testOneVarStatisticsInvalidRangeDrivesUserAttentionOnFirstTry() {
		TabularRange range = new TabularRange(0, 0, 2, 0);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(new TabularRange(0, 0), false, false);
		kernelBackedController.showOneVarStatistics();
		assertEquals(new Result.Invalid(Error.NUMERIC_DATA_RANGE_REQUIRED,
				SpreadsheetStatistics.DataRange.X), oneVarStatisticsView.getResult());
		
		// Second try
		oneVarStatisticsView.setInput(new OneVarInput(new TabularRange(0, 0)));
		assertEquals(new Result.Invalid(Error.NUMERIC_DATA_RANGE_REQUIRED,
				null), oneVarStatisticsView.getResult());
	}

	// 2-var Statistics

	@Test
	public void testTwoVarStatistics() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showTwoVarStatistics();
		assertNotNull(twoVarStatisticsView);

		assertEquals(new TwoVarInput(
						parseReference("A1:A3"),
						parseReference("B1:B3")),
				twoVarStatisticsView.getInput());

		Result.Valid result = assertInstanceOf(Result.Valid.class,
				twoVarStatisticsView.getResult());
		assertEquals(18, result.statisticGroups().size());
	}

	@Test
	public void testTwoVarStatisticsEmptyRange() {
		kernelBackedController.showTwoVarStatistics();
		assertNull(twoVarStatisticsView);
	}

	@Test
	public void testTwoVarStatisticsAllTextualData() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.TEXT);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showTwoVarStatistics();

		assertEquals(new Result.Invalid(Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
				null), twoVarStatisticsView.getResult());
	}

	@Test
	public void testTwoVarStatisticsDifferentRangesGiveDifferentResults() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showTwoVarStatistics();

		assertEquals(new TwoVarInput(
						parseReference("A1:A3"),
						parseReference("B1:B3")),
				twoVarStatisticsView.getInput());
		Result.Valid result1 = assertInstanceOf(Result.Valid.class,
				twoVarStatisticsView.getResult());

		twoVarStatisticsView.setInput(new TwoVarInput(
				new SpreadsheetReference(new TabularRange(0, 0, 1, 0)),
				new SpreadsheetReference(new TabularRange(0, 1, 1, 1))
		));
		assertEquals(new TwoVarInput(
						parseReference("A1:A2"),
						parseReference("B1:B2")),
				twoVarStatisticsView.getInput());
		Result.Valid result2 = assertInstanceOf(Result.Valid.class,
				twoVarStatisticsView.getResult());

		assertNotEquals(result1.statisticGroups(), result2.statisticGroups());
	}
	
	@Test
	public void testTwoVarStatisticsSingleCellInvalidRange() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		
		kernelBackedController.select(new TabularRange(0, 0), false, false);
		kernelBackedController.showTwoVarStatistics();

		assertEquals(new Result.Invalid(Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
				SpreadsheetStatistics.DataRange.X), twoVarStatisticsView.getResult());
	}
	
	@Test
	public void testTwoVarStatisticsSingleColumnInvalidRange() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);

		kernelBackedController.select(range.firstColumn(), false, false);
		kernelBackedController.showTwoVarStatistics();

		assertEquals(new Result.Invalid(Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
				SpreadsheetStatistics.DataRange.Y), twoVarStatisticsView.getResult());
	}

	@Test
	public void testTwoVarStatisticsInvalidRangeDrivesUserAttentionOnFirstTry() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range.firstColumn(), false, false);
		kernelBackedController.showTwoVarStatistics();
		assertEquals(new Result.Invalid(Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
				SpreadsheetStatistics.DataRange.Y), twoVarStatisticsView.getResult());

		// Second try
		twoVarStatisticsView.setInput(new TwoVarInput(range.firstColumn()));
		assertEquals(new Result.Invalid(Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
				null), twoVarStatisticsView.getResult());
	}

	// Regression

	@Test
	public void testRegression() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showRegression();
		assertNotNull(regressionView);

		assertEquals(new RegressionInput(
						parseReference("A1:A3"),
						parseReference("B1:B3"),
						null),
				regressionView.getInput());

		Result.Valid result = assertInstanceOf(Result.Valid.class, regressionView.getResult());
		assertEquals(4, result.statisticGroups().size());
	}

	@Test
	public void testRegressionEmptyRange() {
		kernelBackedController.showRegression();
		assertNull(regressionView);
	}

	@Test
	public void testRegressionAllTextualData() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.TEXT);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showRegression();

		assertEquals(new Result.Invalid(Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
				null), regressionView.getResult());
	}

	@Test
	public void testRegressionDifferentRangesGiveDifferentResults() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showRegression();

		assertEquals(new RegressionInput(
						parseReference("A1:A3"),
						parseReference("B1:B3"),
						null),
				regressionView.getInput());
		Result.Valid result1 = assertInstanceOf(Result.Valid.class, regressionView.getResult());

		regressionView.setInput(new RegressionInput(
				new SpreadsheetReference(new TabularRange(0, 0, 1, 0)),
				new SpreadsheetReference(new TabularRange(0, 1, 1, 1)),
				null
		));
		assertEquals(new RegressionInput(
						parseReference("A1:A2"),
						parseReference("B1:B2"),
						null),
				regressionView.getInput());
		Result.Valid result2 = assertInstanceOf(Result.Valid.class, regressionView.getResult());

		assertNotEquals(result1.statisticGroups(), result2.statisticGroups());
	}

	@Test
	public void testRegressionDifferentSpecificationGiveDifferentResults() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range, false, false);
		kernelBackedController.showRegression();

		assertEquals(new RegressionInput(
						parseReference("A1:A3"),
						parseReference("B1:B3"),
						null),
				regressionView.getInput());
		Result.Valid result1 = assertInstanceOf(Result.Valid.class, regressionView.getResult());

		regressionView.setInput(new RegressionInput(
				parseReference("A1:A3"),
				parseReference("B1:B3"),
				// Different regression
				regressionView.getRegressionSpecifications().get(1)));
		
		Result.Valid result2 = assertInstanceOf(Result.Valid.class, regressionView.getResult());

		assertNotEquals(result1.statisticGroups(), result2.statisticGroups());
	}

	@Test
	public void testRegressionSingleCellInvalidRange() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);

		kernelBackedController.select(new TabularRange(0, 0), false, false);
		kernelBackedController.showRegression();

		assertEquals(new Result.Invalid(Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
				SpreadsheetStatistics.DataRange.X), regressionView.getResult());
	}

	@Test
	public void testRegressionSingleColumnInvalidRange() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);

		kernelBackedController.select(range.firstColumn(), false, false);
		kernelBackedController.showRegression();

		assertEquals(new Result.Invalid(Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
				SpreadsheetStatistics.DataRange.Y), regressionView.getResult());
	}

	@Test
	public void testRegressionInvalidRangeDrivesUserAttentionOnFirstTry() {
		TabularRange range = new TabularRange(0, 0, 2, 1);
		setupTestData(range, Content.NUMBERS);
		kernelBackedController.select(range.firstColumn(), false, false);
		kernelBackedController.showRegression();
		assertEquals(new Result.Invalid(Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
				SpreadsheetStatistics.DataRange.Y), regressionView.getResult());

		// Second try
		regressionView.setInput(new RegressionInput(range.firstColumn()));
		assertEquals(new Result.Invalid(Error.TWO_NUMERIC_DATA_RANGES_OF_EQUAL_LENGTH_REQUIRED,
				null), regressionView.getResult());
	}

	// -- SpreadsheetStatisticsDelegate --

	@Override
	public void showOneVarStatistics(
			@Nonnull SpreadsheetStatisticsView.OneVar statisticsView) {
		this.oneVarStatisticsView = statisticsView;
	}

	@Override
	public void showTwoVarStatistics(
			@Nonnull SpreadsheetStatisticsView.TwoVar statisticsView) {
		this.twoVarStatisticsView = statisticsView;
	}

	@Override
	public void showRegression(@Nonnull SpreadsheetStatisticsView.Regression statisticsView) {
		this.regressionView = statisticsView;
	}
}
