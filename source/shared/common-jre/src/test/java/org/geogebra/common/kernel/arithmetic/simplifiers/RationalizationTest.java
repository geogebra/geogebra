package org.geogebra.common.kernel.arithmetic.simplifiers;

import static org.geogebra.common.kernel.arithmetic.simplifiers.RationalizeFractionAlgo.checkDecimals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.test.annotation.Issue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class RationalizationTest extends BaseUnitTest {

	final Rationalization rationalization = new Rationalization();

	@Before
	public void setUp() throws Exception {
		getKernel().setPrintDecimals(15);
	}

	@Test
	public void testSupported() {
		shouldBeSupported("1 / sqrt(2)");
		shouldBeSupported("1 / (sqrt(2) + 1)");
		shouldBeSupported("1 / (1 - sqrt(2))");
		shouldBeSupported("sqrt(3) / sqrt(2)");
		shouldBeSupported("(sqrt(3) + 1) / sqrt(2)");
		shouldBeSupported("(3 (sqrt(3) + 1)) / sqrt(2)");
		shouldBeSupported("(sqrt(3) + 1) / (sqrt(2) - 1)");
	}

	private void shouldBeSupported(String definition) {
		GeoElementND geo = add(definition);
		ExpressionValue resolution = rationalization.getResolution(geo.getDefinition());
		assertNotNull(resolution);
	}

	@Test
	public void testUnsupported() {
		shouldBeUnsupported("1 / (sqrt(2) + sqrt(3))");
		shouldBeUnsupported("(sqrt(3) + sqrt(2) + 1) / sqrt(2)");
		shouldBeUnsupported("(sqrt(3) + sqrt(2)) / sqrt(2)");
		shouldBeUnsupported("1 / sqrt(2.5)");
		shouldBeUnsupported("sqrt(1 / 4)");
		shouldBeUnsupported("(sqrt(2.5) + 1) / sqrt(2.5)");
		shouldBeUnsupported("sqrt(2.5) / (sqrt(2.5) + 1)");
		shouldBeUnsupported("1 / sqrt(-2)");
		shouldBeUnsupported("2.3 / sqrt(2)");
		shouldBeUnsupported("((1 / 2) (sqrt(3) + 1)) / sqrt(2)");
		shouldBeUnsupported("(3.2 (sqrt(3) + 1)) / sqrt(2)");
		shouldBeUnsupported("((4+sqrt(10+0.0001))/(-2+sqrt(0.0001+10)))");
	}

	@Issue("APPS-6267")
	@Test
	public void testUnsupportedIfNoSqrtInDenominator() {
		shouldBeUnsupported("1/3");
		shouldBeUnsupported("1/(3 + 2)");
	}

	private void shouldBeUnsupported(String definition) {
		GeoElementND geo = add(definition);
		ExpressionValue resolution = rationalization.getResolution(geo.getDefinition());
		assertNull(resolution);
	}

	@Test
	public void decimalValueShouldBeOK() {
		GeoNumeric num = add("1/sqrt(2)");
		num.setSymbolicMode(false, true);
		assertEquals("0.707106781186547",
				num.getFormulaString(StringTemplate.defaultTemplate, true));
		num.setSymbolicMode(true, true);
	}

	@Test
	public void testRationalizationNumeratorIsConstant() {
		rationalizationShouldBe("1 / sqrt(2)", "sqrt(2) / 2");
		rationalizationShouldBe("2 / sqrt(2)", "sqrt(2)");
		rationalizationShouldBe("1 / (sqrt(2) + 1)", "sqrt(2) - 1");
		rationalizationShouldBe("1 / (sqrt(2) - 1)", "sqrt(2) + 1");
		rationalizationShouldBe("1 / (sqrt(2) - 3)", "(-(sqrt(2) + 3)) / 7");
	}

	@Test
	public void testRationalizeToNonFraction() {
		rationalizationShouldBe("1 / (sqrt(2) + 3)", "(3 - sqrt(2)) / 7");
		rationalizationShouldBe("1 / (1 + sqrt(2))", "sqrt(2) - 1");
	}

	@Test
	public void testRationalizationNumeratorIsSquareRoot() {
		rationalizationShouldBe("sqrt(3) / sqrt(2)", "sqrt(6) / 2");
		rationalizationShouldBe("(sqrt(3) + 1) / sqrt(2)", "(sqrt(6) + sqrt(2)) / 2");
		rationalizationShouldBe("(1 + sqrt(3)) / sqrt(2)", "(sqrt(2) + sqrt(6)) / 2");
		rationalizationShouldBe("sqrt(3) / (sqrt(2) - 1)", "sqrt(3) (sqrt(2) + 1)");
		rationalizationShouldBe("sqrt(3) / (sqrt(2) + 1)", "sqrt(3) (sqrt(2) - 1)");
	}

	@Test
	public void testFractionIsInteger() {
		rationalizationShouldBe("sqrt(3) / sqrt(3)", "1");
		rationalizationShouldBe("(2 + sqrt(3)) / (2 + sqrt(3))", "1");
		rationalizationShouldBe("(sqrt(3) + 2) / (sqrt(3) + 2)", "1");
		rationalizationShouldBe("-sqrt(3) / sqrt(3)", "-1");
		rationalizationShouldBe("sqrt(3) / -sqrt(3)", "-1");
		rationalizationShouldBe("-sqrt(3) / -sqrt(3)", "1");
		rationalizationShouldBe("-(sqrt(3) + 2) / (sqrt(3) + 2)", "-1");
		rationalizationShouldBe("(sqrt(3) + 2) / -(sqrt(3) + 2)", "-1");
		rationalizationShouldBe("-(sqrt(3) + 2) / -(sqrt(3) + 2)", "1");
		rationalizationShouldBe("(3 * sqrt(2)) / sqrt(18)", "1");
		rationalizationShouldBe("-sqrt(3) / sqrt(3)", "-1");
		rationalizationShouldBe("(3 * sqrt(3)) / sqrt(3)", "3");
		rationalizationShouldBe("(-3 * sqrt(3)) / sqrt(3)", "-3");
		rationalizationShouldBe("(3 * sqrt(3)) / -sqrt(3)", "-3");
		rationalizationShouldBe("(-3 * sqrt(3)) / -sqrt(3)", "3");
		rationalizationShouldBe("(-3 (sqrt(3) + 2)) / (sqrt(3) + 2)", "-3");
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
		GeoNumeric num = add(definition);
		ExpressionValue  resolution = rationalization.getResolution(num.getDefinition());
		assertNotNull("resolution is null, " + definition + " is not supported", resolution);
		assertEquals(resolution.toString(tpl), num.evaluateDouble(), resolution.evaluateDouble(),
				Kernel.STANDARD_PRECISION);
		assertEquals(expected, resolution.toString(tpl));
	}

	@Test
	public void testSimplifySquareRoots() {
		rationalizationShouldBe("sqrt(3) / sqrt(4)", "sqrt(3) / 2");
		rationalizationShouldBe("sqrt(3) / sqrt(1)", "sqrt(3)");
		rationalizationShouldBe("sqrt(6) / sqrt(2)", "sqrt(3)");
		rationalizationShouldBe("3 / sqrt(8)", "(3sqrt(2)) / 4");
		rationalizationShouldBe("sqrt(2 + 3) / (1 + sqrt(2))",
				"(sqrt(2) - 1) sqrt(5)");
		rationalizationShouldBe("sqrt(2 + 3) / (1 - sqrt(2))",
				"(-1 - sqrt(2)) sqrt(5)");
		rationalizationShouldBe("2 / sqrt(2)", "sqrt(2)");
		rationalizationShouldBe("1 / (2 * (1 + sqrt(2)))", "(sqrt(2) - 1) / 2");
		rationalizationShouldBe("1 / (2 * (1 - sqrt(2)))", "(-(1 + sqrt(2))) / 2");
	}

	@Test
	public void testCancelGCDs() {
		rationalizationShouldBe("2 / sqrt(2)", "sqrt(2)");
		rationalizationShouldBe("4 / (sqrt(5) - 1)", "sqrt(5) + 1");
		rationalizationShouldBe("8 / (sqrt(5) - 1)", "2 (sqrt(5) + 1)");
	}

	@Test
	public void testShouldBeSimpler() {
		rationalizationShouldBe("sqrt(2 + 3) / (1 + sqrt(2 + 5))",
				"(-(sqrt(5) (1 - sqrt(7)))) / 6");
		rationalizationShouldBe("1 / (2 * (1 + sqrt(2)))", "(sqrt(2) - 1) / 2");

	}

	@Test
	public void testProductInDenominator() {
		rationalizationShouldBe("1 / (2 * (1 - sqrt(2)))", "(-(1 + sqrt(2))) / 2");
		rationalizationShouldBe("1 / (2 * sqrt(2))", "sqrt(2) / 4");
	}

	@Test
	public void testEvaluateUnderSquareRoot() {
		rationalizationShouldBe("1 / sqrt(3 + 4)", "sqrt(7) / 7");
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
		GeoNumeric numeric = add(command);
		numeric.setSymbolicMode(true, true);
		return checkDecimals(numeric.getDefinition());
	}

	@Test
	public void test() {
		rationalizationShouldBe("(-2 + sqrt(7)) / (-9 + sqrt(4))", "(-(sqrt(7) - 2)) / 7");
		rationalizationShouldBe("(-2 + sqrt(3+4)) / (-9 + sqrt(4))",
				"(-(sqrt(7) - 2)) / 7");
		rationalizationShouldBe("(-10 + sqrt(6)) / (5 + sqrt(1))",
				"(sqrt(6) - 10) / 6");
		rationalizationShouldBe("(-8 + sqrt(4)) / (-2 + sqrt(8))",
				"-3 (1 + sqrt(2))");
		rationalizationShouldBe("(-8 + sqrt(4)) / (-2 + sqrt(8))", "-3 (1 + sqrt(2))");
	}

	@Ignore // TODO in APPS-6220
	@Test
	public void testSimplestForm() {
		rationalizationShouldBe("(7 + sqrt(8)) / (4 + sqrt(8))",
				"(-3 sqrt(2)) / 4");
		rationalizationShouldBe("(-10 + sqrt(5)) / (-2 + sqrt(5))",
				"(-8sqrt(5) - 15");
		rationalizationShouldBe(genericSqrtFraction(-8, 8, -2, 6),
				"(-8 + sqrt(8)) / (-2 + sqrt(6))");

	}

	@Test
	public void testTrivialDenominators() {
		rationalizationShouldBe(genericSqrtFraction(6, 10, -4, 9),
				"-(6 + sqrt(10))");
		rationalizationShouldBe(genericSqrtFraction(-5, 5, -3, 9),
				"-\u221e");
	}

	@Test
	public void testWrongDenominator() {
		rationalizationShouldBe(genericSqrtFraction(0, 9, 1, 6),
				"(-3 (1 - sqrt(6))) / 5");
	}

	@Test
	public void testWrongParameter() {
		rationalizationShouldBe(genericSqrtFraction(-10, 10, 5, 9),
				"(sqrt(10) - 10) / 8");
	}

	@Test
	public void testZeros() {
		rationalizationShouldBe("(2 + sqrt(3)) / (4 + sqrt(5))",
					"((2 + sqrt(3)) (4 - sqrt(5))) / 11");
		rationalizationShouldBe("(0 + sqrt(3)) / (4 + sqrt(5))",
				"(sqrt(3) (4 - sqrt(5))) / 11");
		rationalizationShouldBe("(2 + sqrt(0)) / (4 + sqrt(5))",
						"(2 (4 - sqrt(5))) / 11");
		rationalizationShouldBe("(2 + sqrt(3)) / (0 + sqrt(5))",
						"(2sqrt(5) + sqrt(15)) / 5");
		rationalizationShouldBe(genericSqrtFraction(-6, 8, 0, 5),
				"(2sqrt(10) + sqrt(5) (-6)) / 5");
	}

	private String genericSqrtFraction(int a, int b, int c, int d) {
		String s = "(" + a + " + sqrt(" + b + ")) / " + "(" + c + " + sqrt(" + d + "))";
		Log.debug("Generic: " + s);
		return s;
	}

	@Test
	public void testBadExamples() {
		rationalizationShouldBe(genericSqrtFraction(-8, 4, -2, 8),
				"-3 (1 + sqrt(2))");
		rationalizationShouldBe(genericSqrtFraction(-6, 9, -8, 9), "3 / 5");
		rationalizationShouldBe(genericSqrtFraction(3, 1, 9, 9), "1 / 3");
		rationalizationShouldBe(genericSqrtFraction(2, 4, 1, 2),
				"4 (sqrt(2) - 1)");
		rationalizationShouldBe(genericSqrtFraction(-5, 1, 5, 8),
				"(-4 (5 - 2sqrt(2))) / 17");
		shouldBeSupported("(-5 + sqrt(5)) / (-3 + sqrt(9))");
		shouldBeSupported("(6 + sqrt(10)) / (-4 + sqrt(9))");
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

	@Test
	public void testSerializationWithDependentGeos() {
		add("a = -1");
		add("b = 5");
		add("c = -2");
		add("d = 5");
		rationalizationShouldBe("(a + sqrt(b)) / (c + sqrt(d))", "3 + 2sqrt(5) - sqrt(5)");
	}

	@Test
	public void testFormulaText() {
		PreviewFeature.setPreviewFeaturesEnabled(true);
		add("a = -1");
		add("b = 5");
		add("c = -2");
		add("d = 5");
		GeoNumeric e = add("e = (a + sqrt(b)) / (c + sqrt(d))");
		e.setSymbolicMode(true, true);
		GeoText text = add("FormulaText(e)");
		assertEquals("3 + 2 \\; \\sqrt{5} - \\sqrt{5}", text.getTextString());
		PreviewFeature.setPreviewFeaturesEnabled(false);
	}

}
