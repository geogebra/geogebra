package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.RationalizeFractionAlgo.checkDecimals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class RationalizationTest extends BaseAppTestSetup {

	private final Rationalization rationalization = new Rationalization();

	@BeforeEach
	public void setUp() {
		setupApp(SuiteSubApp.GRAPHING);
		getKernel().setPrintDecimals(15);
	}

	@ParameterizedTest
	@CsvSource({
			"1 / sqrt(2)",
			"1 / (sqrt(2) + 1)",
			"1 / (1 - sqrt(2))",
			"sqrt(3) / sqrt(2)",
			"(sqrt(3) + 1) / sqrt(2)",
			"(3 (sqrt(3) + 1)) / sqrt(2)",
			"(-5 + sqrt(5)) / (-3 + sqrt(9))",
			"(6 + sqrt(10)) / (-4 + sqrt(9))",
			"(sqrt(3) + 1) / (sqrt(2) - 1)"
	})
	public void testSupported(String definition) {
		GeoElementND geo = evaluateGeoElement(definition);
		ExpressionValue resolution = rationalization.getResolution(geo.getDefinition());
		assertNotNull(resolution);
	}

	@ParameterizedTest
	@CsvSource({
			"1 / (sqrt(2) + sqrt(3))",
			"(sqrt(3) + sqrt(2) + 1) / sqrt(2)",
			"(sqrt(3) + sqrt(2)) / sqrt(2)",
			"1 / sqrt(2.5)",
			"sqrt(1 / 4)",
			"(sqrt(2.5) + 1) / sqrt(2.5)",
			"sqrt(2.5) / (sqrt(2.5) + 1)",
			"1 / sqrt(-2)",
			"2.3 / sqrt(2)",
			"((1 / 2) (sqrt(3) + 1)) / sqrt(2)",
			"(3.2 (sqrt(3) + 1)) / sqrt(2)",
			"((4+sqrt(10+0.0001))/(-2+sqrt(0.0001+10))),",
	})
	public void shouldBeUnsupported(String definition) {
		GeoElementND geo = evaluateGeoElement(definition);
		ExpressionValue resolution = rationalization.getResolution(geo.getDefinition());
		assertNull(resolution);
	}

	@Test
	public void decimalValueShouldBeOK() {
		GeoNumeric num = evaluateGeoElement("1/sqrt(2)");
		num.setSymbolicMode(false, true);
		assertEquals("0.707106781186547",
				num.getFormulaString(StringTemplate.defaultTemplate, true));
		num.setSymbolicMode(true, true);
	}

	@ParameterizedTest
	@CsvSource({
			"1 / sqrt(2), sqrt(2) / 2",
			"2 / sqrt(2), sqrt(2)",
			"1 / (sqrt(2) + 1), sqrt(2) - 1",
			"1 / (sqrt(2) - 1), sqrt(2) + 1",
			"1 / (sqrt(2) - 3), -((sqrt(2) + 3) / 7)",
	})
	public void testRationalizationNumeratorIsConstant(String definition, String expected) {
		rationalizationShouldBe(definition, expected);
	}

	@ParameterizedTest
	@CsvSource({
			"1 / (sqrt(2) + 3), (3 - sqrt(2)) / 7",
			"1 / (1 + sqrt(2)), sqrt(2) - 1",
	})
	public void testRationalizeToNonFraction(String definition, String expected) {
		rationalizationShouldBe(definition, expected);
	}

	@ParameterizedTest
	@CsvSource({
			"1E100/1E99, 10",
			"1E100 /(1E99+sqrt(2)), 10",
			"1E50, 1*10^(50)",
			"(10^10)^5, 100000000000000000000000000000000000000000000000000",
			"1E50/sqrt(2), 70710678118654750000000000000000000000000000000000"
	})
	public void testRationalizeBigNumbers(String definition, String expected) {
		rationalizationShouldBe(definition, expected);
	}

	@ParameterizedTest
	@CsvSource({
			"sqrt(3) / sqrt(2), sqrt(6) / 2",
			"(sqrt(3) + 1) / sqrt(2), (sqrt(6) + sqrt(2)) / 2",
			"(1 + sqrt(3)) / sqrt(2), (sqrt(6) + sqrt(2)) / 2",
			"sqrt(3) / (sqrt(2) - 1), sqrt(6) + sqrt(3)",
			"sqrt(3) / (sqrt(2) + 1), sqrt(6) - sqrt(3)"
	})
	public void testRationalizationNumeratorIsSquareRoot(String definition, String expected) {
		rationalizationShouldBe(definition, expected);
	}

	@ParameterizedTest
	@CsvSource({
			"sqrt(3) / sqrt(3), 1",
			"(2 + sqrt(3)) / (2 + sqrt(3)), 1",
			"(sqrt(3) + 2) / (sqrt(3) + 2), 1",
			"-sqrt(3) / sqrt(3), -1",
			"sqrt(3) / -sqrt(3), -1",
			"-sqrt(3) / -sqrt(3), 1",
			"-(sqrt(3) + 2) / (sqrt(3) + 2), -1",
			"(sqrt(3) + 2) / -(sqrt(3) + 2), -1",
			"-(sqrt(3) + 2) / -(sqrt(3) + 2), 1",
			"(3 * sqrt(2)) / sqrt(18), 1",
			"-sqrt(3) / sqrt(3), -1",
			"(3 * sqrt(3)) / sqrt(3), 3",
			"(-3 * sqrt(3)) / sqrt(3), -3",
			"(3 * sqrt(3)) / -sqrt(3), -3",
			"(-3 * sqrt(3)) / -sqrt(3), 3",
			"(-3 (sqrt(3) + 2)) / (sqrt(3) + 2), -3"
	})
	public void testFractionIsInteger(String definition, String expected) {
		rationalizationShouldBe(definition, expected);
	}

	@Test
	public void testOutputAsLatex() {
		rationalizationShouldBe("2 / sqrt(2)",
				"\\sqrt{2}", StringTemplate.latexTemplate);

		rationalizationShouldBe("sqrt(3) / sqrt(2)",
				"\\frac{\\sqrt{6}}{2}", StringTemplate.latexTemplate);

	}

	private void rationalizationShouldBe(String definition, String expected) {
		rationalizationShouldBe(definition, expected, StringTemplate.defaultTemplate);
	}

	private void rationalizationShouldBe(String definition, String expected, StringTemplate tpl) {
		GeoNumeric num = evaluateGeoElement(definition);
		ExpressionValue resolution = rationalization.getResolution(num.getDefinition());
		assertNotNull(resolution, "resolution is null, " + definition + " is not supported");
		assertEquals(num.evaluateDouble(), resolution.evaluateDouble(),
				Kernel.STANDARD_PRECISION, resolution.toString(tpl));
		assertEquals(expected, resolution.toString(tpl));
	}

	@ParameterizedTest
	@CsvSource({
			"sqrt(3) / sqrt(4), sqrt(3) / 2",
			"sqrt(3) / sqrt(1), sqrt(3)",
			"sqrt(6) / sqrt(2), sqrt(3)",
			"3 / sqrt(8), (3sqrt(2)) / 4",
			"sqrt(2 + 3) / (1 + sqrt(2)), sqrt(10) - sqrt(5)",
			"sqrt(2 + 3) / (1 - sqrt(2)), -sqrt(5) - sqrt(10)",
			"2 / sqrt(2), sqrt(2)",
			"1 / (2 * (1 + sqrt(2))), (sqrt(2) - 1) / 2",
			"1 / (2 * (1 - sqrt(2))), -((1 + sqrt(2)) / 2)",
			"1 / sqrt(3 + 4), sqrt(7) / 7",
			"2 / sqrt(2),sqrt(2)",
			"4 / (sqrt(5) - 1), 1 + sqrt(5)",
			"8 / (sqrt(5) - 1), 2 + 2sqrt(5)",
			"sqrt(2 + 3) / (1 + sqrt(2 + 5)), (sqrt(35) - sqrt(5)) / 6",
			"1 / (2 * (1 + sqrt(2))),(sqrt(2) - 1) / 2",
			"1 / (2 * (1 - sqrt(2))),-((1 + sqrt(2)) / 2)",
			"1 / (2 * sqrt(2)), sqrt(2) / 4",

	})
	public void testSimplifySquareRoots(String definition, String expected) {
		rationalizationShouldBe(definition, expected);
	}

	@Test
	@Issue("APPS-6953")
	public void testRationalizationWithoutAddition() {
		rationalizationShouldBe("(8 sqrt(2))/sqrt(3)", "(8sqrt(6)) / 3");
	}

	@Test
	public void testCheckDecimals() {
		shouldPassToDecimalTest("sqrt(2) / 4");
		shouldPassToDecimalTest("1 + sqrt(2)");
		shouldPassToDecimalTest("(2 (sqrt(5) + 1))");
		shouldNotPassToDecimalTest("1 + sqrt(2.5)");
		shouldNotPassToDecimalTest("16.5 * 2 sqrt(5)");
	}

	private void shouldNotPassToDecimalTest(String command) {
		assertTrue(isPassDecimal(command));
	}

	private void shouldPassToDecimalTest(String command) {
		assertFalse(isPassDecimal(command));
	}

	private boolean isPassDecimal(String command) {
		GeoNumeric numeric = evaluateGeoElement(command);
		numeric.setSymbolicMode(true, true);
		return checkDecimals(numeric.getDefinition());
	}

	@Test
	public void test() {
		rationalizationShouldBe("(-2 + sqrt(7)) / (-9 + sqrt(4))", "(2 - sqrt(7)) / 7");
		rationalizationShouldBe("(-2 + sqrt(3+4)) / (-9 + sqrt(4))",
				"(2 - sqrt(7)) / 7");
		rationalizationShouldBe("(-10 + sqrt(6)) / (5 + sqrt(1))",
				"(sqrt(6) - 10) / 6");
		rationalizationShouldBe("(-8 + sqrt(4)) / (-2 + sqrt(8))",
				"-3 - 3sqrt(2)");
		rationalizationShouldBe("(-8 + sqrt(4)) / (-2 + sqrt(8))", "-3 - 3sqrt(2)");
	}

	@Test
	public void testSimplestForm() {
		rationalizationShouldBe("(7 + sqrt(8)) / (4 + sqrt(8))",
				"(10 - 3sqrt(2)) / 4");
		rationalizationShouldBe("(-10 + sqrt(5)) / (-2 + sqrt(5))",
				"-8sqrt(5) - 15");
		rationalizationShouldBe(genericSqrtFraction(-8, 8, -2, 6),
				"2sqrt(3) + 2sqrt(2) - 4sqrt(6) - 8");

	}

	@Test
	public void testTrivialDenominators() {
		rationalizationShouldBe(genericSqrtFraction(6, 10, -4, 9),
				"-sqrt(10) - 6");
		rationalizationShouldBe(genericSqrtFraction(-5, 5, -3, 9),
				"-\u221e");
	}

	@Test
	public void testWrongDenominator() {
		rationalizationShouldBe(genericSqrtFraction(0, 9, 1, 6),
				"(3sqrt(6) - 3) / 5");
	}

	@Test
	public void testWrongParameter() {
		rationalizationShouldBe(genericSqrtFraction(-10, 10, 5, 9),
				"(sqrt(10) - 10) / 8");
	}

	@Test
	public void testZeros() {
		rationalizationShouldBe("(2 + sqrt(3)) / (4 + sqrt(5))",
				"(8 + 4sqrt(3) - 2sqrt(5) - sqrt(15)) / 11");
		rationalizationShouldBe("(0 + sqrt(3)) / (4 + sqrt(5))",
				"(4sqrt(3) - sqrt(15)) / 11");
		rationalizationShouldBe("(2 + sqrt(0)) / (4 + sqrt(5))",
				"(8 - 2sqrt(5)) / 11");
		rationalizationShouldBe("(2 + sqrt(3)) / (0 + sqrt(5))",
				"(2sqrt(5) + sqrt(15)) / 5");
		rationalizationShouldBe(genericSqrtFraction(-6, 8, 0, 5),
				"(2sqrt(10) - 6sqrt(5)) / 5");
	}

	private String genericSqrtFraction(int a, int b, int c, int d) {
		return "(" + a + " + sqrt(" + b + ")) / " + "(" + c + " + sqrt(" + d + "))";
	}

	@Test
	public void testBadExamples() {
		rationalizationShouldBe(genericSqrtFraction(-8, 4, -2, 8),
				"-3 - 3sqrt(2)");
		rationalizationShouldBe(genericSqrtFraction(-6, 9, -8, 9), "3 / 5");
		rationalizationShouldBe(genericSqrtFraction(3, 1, 9, 9), "1 / 3");
		rationalizationShouldBe(genericSqrtFraction(2, 4, 1, 2),
				"4sqrt(2) - 4");
		rationalizationShouldBe(genericSqrtFraction(-5, 1, 5, 8),
				"(8sqrt(2) - 20) / 17");

	}

	@Test
	public void allShouldBeNumericallyOK() {
		StringBuilder failures = new StringBuilder();
		for (int a = -5; a <= 5; a++) {
			for (int b = 0; b <= 10; b++) {
				for (int c = -5; c <= 5; c++) {
					for (int d = 0; d <= 10; d++) {
						ExpressionNode ex = new ExpressionNode(getKernel(), a)
								.plus(new ExpressionNode(getKernel(), b).sqrt())
								.divide(new ExpressionNode(getKernel(), c)
										.plus(new ExpressionNode(getKernel(), d).sqrt()));
						double expected = ex.evaluateDouble();
						ExpressionValue resolution = rationalization.getResolution(ex);
						if (resolution == null) {
							continue;
						}
						double actual = resolution
								.deepCopy(getKernel()).evaluateDouble();
						if (Double.isFinite(expected) && !DoubleUtil.isEqual(expected, actual)) {
							failures.append(Arrays.toString(
											new double[]{a, b, c, d, actual, expected}))
									.append(ex.toString(StringTemplate.defaultTemplate))
									.append(" -> ").append(resolution).append("\n");
						}
					}
				}
			}
		}
		assertEquals("", failures.toString());
	}

	@ParameterizedTest
	@CsvSource({
			"-1, 5, -2, 5, 3 + sqrt(5)",
			"-5, 2, -5, 3, (25 - 5sqrt(2) + 5sqrt(3) - sqrt(6)) / 22",
			"-5, 2, -3, 10, 2sqrt(5) + 3sqrt(2) - 15 - 5sqrt(10)",
			"-10, 5, -2, 5, -8sqrt(5) - 15",
			"-5, 3, -2, 6, (2sqrt(3) + 3sqrt(2) - 10 - 5sqrt(6)) / 2",
			"-5, 2, -1, 3, (sqrt(6) + sqrt(2) - 5 - 5sqrt(3)) / 2",
			"-5, 2, 1, 6, (5 - sqrt(2) + 2sqrt(3) - 5sqrt(6)) / 5",
			"-4, 2, 1, 2, 6 - 5sqrt(2)",
			"-4, 3, 2,  2, (2sqrt(3) + 4sqrt(2) - 8 - sqrt(6)) / 2",
			"2, 2, 0, 6, (sqrt(6) + sqrt(3)) / 3",
			"-3, 4, 4, 2, (sqrt(2) - 4) / 14",
			"-4, 8, -5, 2, (16 - 6sqrt(2)) / 23",
			"1, 8, -5, 9, -((1 + 2sqrt(2)) / 2)",
			"-5, 0, -5, 2, (25 + 5sqrt(2)) / 23",
			"0, 5, -3, 2, -((3sqrt(5) + sqrt(10)) / 7)",
			"-2, 2, -1, 10, (2sqrt(5) + sqrt(2) - 2 - 2sqrt(10)) / 9",
			"10, 10, -4, 6, -((40 + 10sqrt(6) + 4sqrt(10) + 2sqrt(15)) / 10)",
			"-1, 9, -2, 7, (4 + 2sqrt(7)) / 3",
			"-5, 0, -3, 10, -15 - 5sqrt(10)",
			"9, 3, 0, 8, (9sqrt(2) + sqrt(6)) / 4",
			"-5, 3, 0, 8, (sqrt(6) - 5sqrt(2)) / 4",
			"-1, 3, 0, 10, (sqrt(30) - sqrt(10)) / 10",
			"-5, 3, -2, 6 , (2sqrt(3) + 3sqrt(2) - 10 - 5sqrt(6)) / 2",
			"7, 4, -3, 6, -9 - 3sqrt(6)",
			"6, 7, 5, 2,  (30 - 6sqrt(2) + 5sqrt(7) - sqrt(14)) / 23",
			"-9, 2, 3, 6, (9sqrt(6) + 3sqrt(2) - 27 - 2sqrt(3)) / 3",
			"-3, 2, 7, 7, (3sqrt(7) + 7sqrt(2) - 21 - sqrt(14)) / 42"
	})
	public void testSerializationWithDependentGeos(int a, int b, int c, int d,
			String expected) {
		evaluate("a = " + a);
		evaluate("b = " + b);
		evaluate("c = " + c);
		evaluate("d = " + d);
		rationalizationShouldBe("(a + sqrt(b)) / (c + sqrt(d))", expected);
	}

	@Test
	public void testFormulaText() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
		evaluate("a = -1");
		evaluate("b = 5");
		evaluate("c = -2");
		evaluate("d = 5");
		GeoNumeric e = evaluateGeoElement("e = (a + sqrt(b)) / (c + sqrt(d))");
		e.setSymbolicMode(true, true);
		GeoText text = evaluateGeoElement("FormulaText(e)");
		assertEquals("3 + \\sqrt{5}", text.getTextString());
	}

	@Test
	void testMinusPlacement() {
		rationalizationShouldBe(genericSqrtFraction(10, 10, -4, 6),
				"-((40 + 10sqrt(6) + 4sqrt(10) + 2sqrt(15)) / 10)");
		rationalizationShouldBe(genericSqrtFraction(-3, 2, 7, 7),
				"(3sqrt(7) + 7sqrt(2) - 21 - sqrt(14)) / 42");
	}
}
