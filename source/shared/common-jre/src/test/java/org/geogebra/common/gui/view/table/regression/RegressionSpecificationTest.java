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

package org.geogebra.common.gui.view.table.regression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTestSetup;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup;
import org.geogebra.common.gui.view.table.dialog.StatisticGroup.Row;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.restrictions.FeatureRestriction;
import org.geogebra.common.util.AttributedString;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RegressionSpecificationTest extends BaseExamTestSetup {
	private TableValues view;
	private AttributedString model;
	private String formula;
	private AttributedString fitQuality;
	private String coefficient;
	private String pmmc;

	@BeforeEach
	void setupTable() {
		setupApp(SuiteSubApp.GRAPHING);
		getApp().setRounding("2d");
		model = new AttributedString(getLocalization().getMenu("Stats.Model"));
		formula = getLocalization().getMenu("Stats.Formula");
		fitQuality = new AttributedString(getLocalization().getMenu("Stats.FitQuality"));
		coefficient = getLocalization().getMenu("CoefficientOfDetermination");
		pmmc = getLocalization().getMenu("Stats.PMCC");
		view = setupTableValues("{1,2,3,4}", "{1,8,27,64}", "{5,7,5,3}", "{-1,-8,-27,-64}");
	}

	@Test
	void testRegressionCount() {
		assertEquals(9, view.getRegressionSpecifications(1).size());
	}

	@Test
	void testLinearRegression() {
		RegressionSpecification linearRegression =
				view.getRegressionSpecifications(1).get(0);
		assertEquals(
				List.of(
						new StatisticGroup(
								model,
								List.of(
										new Row(formula, "y = a\\ x+b", true, null),
										new Row("a", "20.8", false, "20.8"),
										new Row("b", "-27", false, "-27"))),
						new StatisticGroup(
								fitQuality,
								List.of(
										new Row(coefficient, "R\u00b2 = 0.91", false, "0.9051046"),
										new Row(pmmc, "r = 0.95", false, "0.9513699")))),
				withLowPrecisionClipboard(view.getRegression(1, linearRegression)));
	}

	@Test
	@Issue("APPS-7328")
	void testLinearRegressionNegative() {
		RegressionSpecification linearRegression =
				view.getRegressionSpecifications(3).get(0);
		assertEquals(
				List.of(
						new StatisticGroup(
								model,
								List.of(
										new Row(formula, "y = a\\ x+b", true, null),
										new Row("a", "-20.8", false, "-20.8"),
										new Row("b", "27", false, "27"))),
						new StatisticGroup(
								fitQuality,
								List.of(
										new Row(coefficient, "R\u00b2 = 0.91", false, "0.9051046"),
										new Row(pmmc, "r = -0.95", false, "-0.9513699")))),
				withLowPrecisionClipboard(view.getRegression(3, linearRegression)));
	}

	@Test
	void testLogRegression() {
		RegressionSpecification logRegression = view.getRegressionSpecifications(1).get(1);
		assertEquals(
				List.of(
						new StatisticGroup(
								model,
								List.of(
										new Row(formula, "y = a + b\\cdot \\ln(x)", true, null),
										new Row("a", "-7.59", false, "-7.5946143"),
										new Row("b", "41.02", false, "41.024622"))),
						new StatisticGroup(
								fitQuality, List.of(new Row(coefficient, "R\u00b2 = 0.76", false, "0.7634906")))),
				withLowPrecisionClipboard(view.getRegression(1, logRegression)));
	}

	@Test
	void testPowerRegression() {
		RegressionSpecification powerRegression =
				view.getRegressionSpecifications(1).get(2);
		assertEquals(
				List.of(
						new StatisticGroup(
								model,
								List.of(
										new Row(formula, "y = a \\cdot x^b", true, null),
										new Row("a", "1", false, "1"),
										new Row("b", "3", false, "3"))),
						new StatisticGroup(
								fitQuality, List.of(new Row(coefficient, "R\u00b2 = 1", false, "1")))),
				withLowPrecisionClipboard(view.getRegression(1, powerRegression)));
	}

	@Test
	void testQuadraticRegression() {
		RegressionSpecification quadraticRegression =
				view.getRegressionSpecifications(1).get(3);
		assertEquals(
				List.of(
						new StatisticGroup(
								model,
								List.of(
										new Row(formula, "y = a\\ x^{2}+b\\ x+c", true, null),
										new Row("a", "7.5", false, "7.5"),
										new Row("b", "-16.7", false, "-16.7"),
										new Row("c", "10.5", false, "10.5"))),
						new StatisticGroup(
								fitQuality, List.of(new Row(coefficient, "R\u00b2 = 1", false, "0.9992469")))),
				withLowPrecisionClipboard(view.getRegression(1, quadraticRegression)));
	}

	@Test
	void testCubicRegression() {
		RegressionSpecification cubicRegression =
				view.getRegressionSpecifications(1).get(4);
		assertEquals(
				List.of(
						new StatisticGroup(
								model,
								List.of(
										new Row(formula, "y = a\\ x^{3}+b\\ x^{2}+c\\ x+d", true, null),
										new Row("a", "1", false, "1"),
										new Row("b", "0", false, "0"),
										new Row("c", "0", false, "0"),
										new Row("d", "0", false, "0"))),
						new StatisticGroup(
								fitQuality, List.of(new Row(coefficient, "R\u00b2 = 1", false, "1")))),
				withLowPrecisionClipboard(view.getRegression(1, cubicRegression)));
	}

	@Test
	void testExponentialRegression() {
		RegressionSpecification exponentialRegression =
				view.getRegressionSpecifications(1).get(5);
		assertEquals(
				List.of(
						new StatisticGroup(
								model,
								List.of(
										new Row(formula, "y = a \\cdot e^{b\\ x}", true, null),
										new Row("a", "0.35", false, "0.3535534"),
										new Row("b", "1.37", false, "1.3693045"))),
						new StatisticGroup(
								fitQuality, List.of(new Row(coefficient, "R\u00b2 = 0.81", false, "0.8076908")))),
				withLowPrecisionClipboard(view.getRegression(1, exponentialRegression)));
	}

	@Test
	void testGrowthRegression() {
		RegressionSpecification growthRegression =
				view.getRegressionSpecifications(1).get(6);
		assertEquals(
				List.of(
						new StatisticGroup(
								model,
								List.of(
										new Row(formula, "y = a \\cdot b^x", true, null),
										new Row("a", "0.35", false, "0.3535534"),
										new Row("b", "3.93", false, "3.9326144"))),
						new StatisticGroup(
								fitQuality, List.of(new Row(coefficient, "R\u00b2 = 0.81", false, "0.8076908")))),
				withLowPrecisionClipboard(view.getRegression(1, growthRegression)));
	}

	@Test
	void testSinRegression() {
		RegressionSpecification sinRegression = view.getRegressionSpecifications(1).get(7);
		assertEquals(
				List.of(
						new StatisticGroup(
								model,
								List.of(
										new Row(formula, "y = a \\cdot \\sin(b\\ x + c) + d", true, null),
										new Row("a", "?", false, "?"),
										new Row("b", "?", false, "?"),
										new Row("c", "?", false, "?"),
										new Row("d", "?", false, "?"))),
						new StatisticGroup(
								fitQuality, List.of(new Row(coefficient, "R\u00b2 = ?", false, "?")))),
				withLowPrecisionClipboard(view.getRegression(1, sinRegression)));

		assertEquals(
				List.of(
						new StatisticGroup(
								model,
								List.of(
										new Row(formula, "y = a \\cdot \\sin(b\\ x + c) + d", true, null),
										new Row("a", "2", false, "2"),
										new Row("b", "1.57", false, "1.5707963"),
										new Row("c", "-1.57", false, "-1.5707963"),
										new Row("d", "5", false, "5"))),
						new StatisticGroup(
								fitQuality, List.of(new Row(coefficient, "R\u00b2 = 1", false, "1")))),
				withLowPrecisionClipboard(view.getRegression(2, sinRegression)));
	}

	@Test
	void testLogisticRegression() {
		RegressionSpecification logisticRegression =
				view.getRegressionSpecifications(1).get(8);
		assertEquals(
				List.of(
						new StatisticGroup(
								model,
								List.of(
										new Row(formula, "y = \\frac{a}{1 + b\\cdot e^{-c\\ x}}", true, null),
										new Row("a", "105.06", false, "105.0625538"),
										new Row("b", "258.98", false, "258.9842766"),
										new Row("c", "1.5", false, "1.5000152"))),
						new StatisticGroup(
								fitQuality, List.of(new Row(coefficient, "R\u00b2 = 1", false, "0.9996562")))),
				withLowPrecisionClipboard(view.getRegression(1, logisticRegression)));
	}

	@ParameterizedTest(name = "{arguments}")
	@Issue("APPS-7328")
	@CsvSource(
			value = {
				"0;1;20.8x - 27",
				"0;3;-20.8x + 27",
				"1;1;-7.59 + 41.02ln(x)",
				"2;1;1x³",
				"3;1;7.5x² - 16.7x + 10.5",
				"4;1;x³ + 0x² - 0x + 0",
				"5;1;0.35ℯ^(1.37x)",
				"6;1;0.35 * 3.93^x",
				"7;1;?",
				"7;2;5 + 2sin(1.57x - 1.57)",
				"8;1;105.06 / (1 + 258.98ℯ^(-1.5x))"
			},
			delimiter = ';')
	void testRegressionFormulas(int specificationIndex, int column, String expected) {
		assertEquals(
				expected,
				view.plotRegression(
								column, view.getRegressionSpecifications(column).get(specificationIndex))
						.toValueString(
								StringTemplate.defaultTemplate.deriveWithoutCoefficientSimplification()));
	}

	@Test
	void testCustomLinearRegression() {
		startExam(ExamType.MMS);
		getApp()
				.getRegressionSpecBuilder()
				.applyRestrictions(Set.of(FeatureRestriction.CUSTOM_MMS_REGRESSION_MODELS));
		RegressionSpecification linearRegression =
				view.getRegressionSpecifications(1).get(0);
		assertEquals(
				List.of(
						new StatisticGroup(
								model,
								List.of(new Row("a", "20.8", false, "20.8"), new Row("b", "-27", false, "-27"))),
						new StatisticGroup(fitQuality, List.of(new Row(pmmc, "r = 0.95", false, "0.9513699")))),
				withLowPrecisionClipboard(view.getRegression(1, linearRegression)));
	}

	@Test
	void testCustomExponentialPlusConstantRegression() {
		startExam(ExamType.MMS);
		getApp()
				.getRegressionSpecBuilder()
				.applyRestrictions(Set.of(FeatureRestriction.CUSTOM_MMS_REGRESSION_MODELS));
		RegressionSpecification exponentialPlusConstantRegression =
				view.getRegressionSpecifications(1).get(6);
		assertEquals(
				List.of(
						new StatisticGroup(
								model,
								List.of(
										new Row("a", "3.44", false, "3.4380718"),
										new Row("b", "-6.76", false, "-6.7619424"),
										new Row("c", "0.76", false, "0.7564421"))),
						new StatisticGroup(fitQuality, List.of(new Row(pmmc, "r = 1", false, "0.9997548")))),
				withLowPrecisionClipboard(view.getRegression(1, exponentialPlusConstantRegression)));
	}

	@Test
	void testCustomExponentialRegression() {
		startExam(ExamType.MMS);
		getApp()
				.getRegressionSpecBuilder()
				.applyRestrictions(Set.of(FeatureRestriction.CUSTOM_MMS_REGRESSION_MODELS));
		RegressionSpecification exponentialRegression =
				view.getRegressionSpecifications(1).get(7);
		assertEquals(
				List.of(
						new StatisticGroup(
								model,
								List.of(
										new Row("a", "0.35", false, "0.3535534"),
										new Row("b", "1.37", false, "1.3693045"))),
						new StatisticGroup(fitQuality, List.of(new Row(pmmc, "r = 0.9", false, "0.8987162")))),
				withLowPrecisionClipboard(view.getRegression(1, exponentialRegression)));
	}

	@ParameterizedTest(name = "{arguments}")
	@CsvSource(
			value = {
				"0;20.8x - 27 * 1",
				"1;11.8x",
				"2;7.5x² - 16.7x + 10.5 * 1",
				"3;5.81x² - 7.55x",
				"4;4.26x² - 6.98 * 1",
				"5;3.67x²",
				"6;3.44ℯ^(x * 0.76) - 6.76",
				"7;0.35ℯ^(1.37x)",
				"8;-65.23x⁻¹ + 58.97 * 1",
				"9;21.07x⁻¹",
				"10;-44.73x⁻² + 40.92 * 1",
				"11;9.27x⁻²",
				"12;18.71x^0.5"
			},
			delimiter = ';')
	void testMmsRegressionFormulas(int specificationIndex, String expected) {
		startExam(ExamType.MMS);
		getApp()
				.getRegressionSpecBuilder()
				.applyRestrictions(Set.of(FeatureRestriction.CUSTOM_MMS_REGRESSION_MODELS));
		assertEquals(
				expected,
				view.plotRegression(1, view.getRegressionSpecifications(1).get(specificationIndex))
						.toValueString(
								StringTemplate.defaultTemplate.deriveWithoutCoefficientSimplification()));
	}

	@Test
	void testCustomRegressionCount() {
		startExam(ExamType.MMS);
		getApp()
				.getRegressionSpecBuilder()
				.applyRestrictions(Set.of(FeatureRestriction.CUSTOM_MMS_REGRESSION_MODELS));
		assertEquals(13, view.getRegressionSpecifications(1).size());
	}

	private List<StatisticGroup> withLowPrecisionClipboard(List<StatisticGroup> groups) {
		return groups.stream()
				.map(group -> new StatisticGroup(
						group.heading(),
						group.rows().stream()
								.map(row -> new Row(
										row.label(), row.value(), row.isLaTeX(), lowPrecision(row.clipboardValue())))
								.toList()))
				.toList();
	}

	private String lowPrecision(String clipboardValue) {
		try {
			return new BigDecimal(clipboardValue)
					.setScale(7, RoundingMode.HALF_UP)
					.stripTrailingZeros()
					.toPlainString();
		} catch (Exception exception) {
			return clipboardValue;
		}
	}
}
