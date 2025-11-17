package org.geogebra.common.gui.view.table.regression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class RegressionSpecificationTest extends BaseAppTestSetup {
	private TableValuesView view;
	private GeoList listY;
	private int column = 1;

	@BeforeEach
	public void setupTable() {
		setupApp(SuiteSubApp.GRAPHING);
		getApp().setRounding("2d");
		GeoList list = evaluateGeoElement("{1,2,3,4}");
		listY = evaluateGeoElement("{1,8,27,64}");
		GeoList listY2 = evaluateGeoElement("{5,7,5,3}");
		getApp().getSettings().getTable().setValueList(list);
		view = new TableValuesView(getKernel());
		getKernel().attach(view);
		view.add(listY);
		view.showColumn(listY);
		view.add(listY2);
		view.showColumn(listY2);
		column = 1;
	}

	@Test
	public void testRegressionCount() {
		assertEquals(9, new RegressionSpecificationBuilder().getForListSize(listY.size()).size());
	}

	@Test
	public void testLinearRegression() {
		assertEquals("y = a\\ x+b, a = 20.8, b = -27, R\u00b2 = 0.91, r = 0.95",
				getRegressionValues(0));
		assertEquals("20.8x - 27", getRegressionFormula(0));
	}

	@Test
	public void testLogRegression() {
		assertEquals("y = a + b\\cdot \\log(x), a = -7.59, b = 41.02, R\u00b2 = 0.76",
				getRegressionValues(1));
		assertEquals("-7.59 + 41.02ln(x)", getRegressionFormula(1));
	}

	@Test
	public void testPowerRegression() {
		assertEquals("y = a \\cdot x^b, a = 1, b = 3, R\u00b2 = 1",
				getRegressionValues(2));
		assertEquals("1x³", getRegressionFormula(2));
	}

	@Test
	public void testQuadraticRegression() {
		assertEquals("y = a\\ x^{2}+b\\ x+c, a = 7.5, b = -16.7, c = 10.5, R\u00b2 = 1",
				getRegressionValues(3));
		assertEquals("7.5x\u00b2 - 16.7x + 10.5", getRegressionFormula(3));
	}

	@Test
	public void testCubicRegression() {
		assertEquals("y = a\\ x^{3}+b\\ x^{2}+c\\ x+d, a = 1, b = 0, c = 0, d = 0, R\u00b2 = 1",
				getRegressionValues(4));
		assertEquals("x\u00b3 + 0x\u00b2 - 0x + 0", getRegressionFormula(4));
	}

	@Test
	public void testExponentialRegression() {
		assertEquals("y = a \\cdot e^{b\\ x}, a = 0.35, b = 1.37, R\u00b2 = 0.81",
				getRegressionValues(5));
		assertEquals("0.35" + Unicode.EULER_STRING + "^(1.37x)",
				getRegressionFormula(5));
	}

	@Test
	public void testGrowthRegression() {
		assertEquals("y = a \\cdot b^x, a = 0.35, b = 3.93, R\u00b2 = 0.81",
				getRegressionValues(6));
		assertEquals("0.35 * 3.93^x", getRegressionFormula(6));
	}

	@Test
	public void testSinRegression() {
		assertEquals("y = a \\cdot \\sin(b\\ x + c) + d,"
				+ " a = ?, b = ?, c = ?, d = ?, R\u00b2 = ?",
				getRegressionValues(7));
		assertEquals("?", getRegressionFormula(7));

		column = 2;
		assertEquals("y = a \\cdot \\sin(b\\ x + c) + d,"
						+ " a = 2, b = 1.57, c = -1.57, d = 5, R\u00b2 = 1",
				getRegressionValues(7));
		assertEquals("5 + 2sin(1.57x - 1.57)", getRegressionFormula(7));
	}

	@Test
	public void testLogisticRegression() {
		assertEquals("y = \\frac{a}{1 + b\\cdot e^{-c\\ x}},"
				+ " a = 105.06, b = 258.98, c = 1.5, R\u00b2 = 1", getRegressionValues(8));
		assertEquals("105.06 / (1 + 258.98" + Unicode.EULER_STRING + "^(-1.5x))",
				getRegressionFormula(8));
	}

	@ParameterizedTest(name = "{arguments}")
	@CsvSource(value = {"0:20.8x - 27 * 1:a = 20.8, b = -27, r = 0.95",
			"1:11.8x:a = 11.8, r = 0.84",
			"2:7.5x² - 16.7x + 10.5 * 1:a = 7.5, b = -16.7, c = 10.5, r = 1",
			"3:5.81x² - 7.55x:a = 5.81, b = -7.55, r = 1",
			"4:4.26x² - 6.98 * 1:a = 4.26, c = -6.98, r = 0.99",
			"5:3.67x²:a = 3.67, r = 0.98",
			"6:3.44ℯ^(x * 0.76) - 6.76:a = 3.44, b = -6.76, c = 0.76, r = 1",
			"7:0.35ℯ^(1.37x):a = 0.35, b = 1.37, r = 0.9",
			"8:-65.23x⁻¹ + 58.97 * 1:a = -65.23, b = 58.97, r = 0.78",
			"9:21.07x⁻¹:a = 21.07, r = ?",
			"10:-44.73x⁻² + 40.92 * 1:a = -44.73, b = 40.92, r = 0.69",
			"11:9.27x⁻²:a = 9.27, r = ?",
			"12:18.71x^0.5:a = 18.71, r = 0.65"}, delimiter = ':')
	public void testCustomRegressions(int index, String expected, String expectedVals) {
		getApp().getRegressionSpecBuilder().applyRestrictions(
				Set.of(ExamFeatureRestriction.CUSTOM_MMS_REGRESSION_MODELS), ExamType.MMS);
		assertEquals(expected, getRegressionFormula(index));
		assertEquals(expectedVals, getRegressionValues(index));
	}

	@Test
	public void testCustomRegressionCount() {
		getApp().getRegressionSpecBuilder().applyRestrictions(
				Set.of(ExamFeatureRestriction.CUSTOM_MMS_REGRESSION_MODELS), ExamType.MMS);
		assertEquals(13, getApp().getRegressionSpecBuilder()
				.getForListSize(listY.size()).size());
	}

	private String getRegressionFormula(int spec) {
		GeoElement plot = view.plotRegression(column, getSpec(spec));
		return plot.toValueString(StringTemplate.defaultTemplate);
	}

	private String getRegressionValues(int spec) {
		return view.getRegression(column, getSpec(spec)).stream()
				.flatMap(g -> Arrays.stream(g.getValues()))
				.collect(Collectors.joining(", "));
	}

	private RegressionSpecification getSpec(int i) {
		return getApp().getRegressionSpecBuilder().getForListSize(listY.size()).get(i);
	}
}
