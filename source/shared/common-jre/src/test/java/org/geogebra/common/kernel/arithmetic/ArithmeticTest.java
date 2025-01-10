package org.geogebra.common.kernel.arithmetic;

import static org.geogebra.test.commands.AlgebraTestHelper.shouldFail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.test.TestStringUtil;
import org.geogebra.test.annotation.Issue;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class ArithmeticTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Before
	public void clean() {
		getApp().getSettings().getCasSettings().setEnabled(true);
		getApp().getKernel().clearConstruction(true);
	}

	private void t(String input, String expected) {
		t(input, expected, StringTemplate.xmlTemplate);
	}

	private void t(String input, String expected, StringTemplate tpl) {
		AlgebraTestHelper.checkSyntaxSingle(input, new String[] { expected },
				getApp().getKernel().getAlgebraProcessor(), tpl);
	}

	@Test
	public void listArithmetic() {
		t("{1,2,3}*2", "{2, 4, 6}");
		t("{1,2,3}+3", "{4, 5, 6}");
		t("list1:={1,2,3}", "{1, 2, 3}");
		t("listF:={x, 2 * x,3 * x+1}", "{x, (2 * x), (3 * x) + 1}");
		t("matrix1:={{1, 2, 3}, {2, 4, 6}, {3, 6, 9}}",
				"{{1, 2, 3}, {2, 4, 6}, {3, 6, 9}}");
		t("aa:=1", "1");
		t("matrix2:={{aa}}", "{{1}}");
		// app.getKernel().lookupLabel("matrix2").setFixed(true);
		t("list1(1)", "1");
		t("list1(4)", "NaN");
		t("list1(0)", "NaN");
		t("list1(-1)", "3");
		t("list1(-5)", "NaN");
		t("list1(1,2)", "NaN");
		t("listF(1)", "x");
		t("listF(2)", "(2 * x)");
		t("listF(2,7)", "14");
		t("matrix1(2)", "{2, 4, 6}");
		t("matrix1(-1)", "{3, 6, 9}");
		t("matrix1(-5)", "{NaN, NaN, NaN}");
		t("matrix1(2,3)", "6");
		t("matrix1(2,3,4)", "NaN");
		t("matrix1(2,-1)", "6");
		t("matrix1(5,2)", "NaN");
		t("matrix1(2,5)", "NaN");
		t("matrix2(1,2)", "NaN");
		t("matrix2(2,1)", "NaN");
	}

	@Test
	public void logSyntaxTest() {
		t("log_{2}2/(2)", "0.5");
	}

	@Test
	public void infinity() {
		t("0^inf", "0");
		t("1^inf", "NaN");
		t("1^(-inf)", "NaN");
		t("2^inf", "Infinity");
		t("2.1^inf", "Infinity");
		t("(-1)^inf", "NaN");
		t("inf^inf", "Infinity");
		t("?^inf", "NaN");
		t("inf^?", "NaN");
		t("(-inf)^inf", "Infinity");
		t("inf^(-inf)", "0");
		t("(-inf)^(-inf)", "0");
		t("inf^0", "NaN");
		t("inf^(-1)", "0");
		t("1/inf", "0");
		t("0/inf", "0");
		t("1*inf", "Infinity");
		t("2*inf", "Infinity");
		t("inf*1", "Infinity");
		t("inf*-1", "-Infinity");
		t("0*inf", "NaN");
		t("inf*0", "NaN");
		t("inf+1", "Infinity");
		t("inf+0", "Infinity");
		t("inf-0", "Infinity");
		t("inf-1", "Infinity");
		t("1-inf", "-Infinity");
		t("1+inf", "Infinity");
		t("0+inf", "Infinity");
		t("sin(inf)", "NaN");
		t("cos(inf)", "NaN");
		t("tan(inf)", "NaN");
		t("ln(inf)", "Infinity");
		t("exp(inf)", "Infinity");
		t("sin(-inf)", "NaN");
		t("cos(-inf)", "NaN");
		t("tan(-inf)", "NaN");
		t("ln(-inf)", "NaN");
		t("exp(-inf)", "0");
		t("inf * inf", "Infinity");
		t("inf * -inf", "-Infinity");
		t("-inf * inf", "-Infinity");
		t("-inf * -inf", "Infinity");
		t("inf + inf", "Infinity");
		t("inf + -inf", "NaN");
		t("-inf + inf", "NaN");
		t("-inf + -inf", "-Infinity");
		t("inf - inf", "NaN");
		t("inf - -inf", "Infinity");
		t("-inf - inf", "-Infinity");
		t("-inf - -inf", "NaN");
		t("inf / inf", "NaN");
		t("inf / -inf", "NaN");
		t("-inf / inf", "NaN");
		t("-inf / -inf", "NaN");
		t("inf * ?", "NaN");
		t("? * inf", "NaN");
		t("? * ?", "NaN");
		t("inf / ?", "NaN");
		t("? / inf", "NaN");
		t("? / ?", "NaN");
	}

	@Test
	public void testRounding() {
		final int angleUnit = getKernel().getAngleUnit();

		getKernel().setAngleUnit(Kernel.ANGLE_RADIANT);

		t("round(6740340335894, 5)", "6740340335894");
		t("round(6.740340335894E12, 5.0)", "6740340335894");
		t("round(6.740340335894E10, 10.0)", "6.740340335894E10");
		t("round(6.740340335894E10, 7.0)", "6.740340335894E10");
		t("round(6.740340335894E10, 8.0)", "6.740340335894E10");
		t("round(6.740340335894E10, 8.0)", "6.740340335894E10");
		t("round(6.740340335894E10, 10.0)", "6.740340335894E10");
		t("round(6.740340335894E10, 7.0)", "6.740340335894E10");
		t("round(6.740340335894E10, 8.0)", "6.740340335894E10");
		t("round(6.740340335894E10, 10.0)", "6.740340335894E10");
		t("round(6.740340335894E9, 8.0)", "6.740340335894E9");
		t("round(6.740340335894E8, 7.0)", "6.740340335894E8");
		t("round(6.740340335894E7, 8.0)", "6.740340335894E7");

		getKernel().setAngleUnit(angleUnit);
	}

	@Test
	public void functionArithmetic() {
		t("f(x)=x^2", "x^(2)");
		t("g1:f+f", "x^(2) + x^(2)");
		t("g2:f+x", "x^(2) + x");
		t("g3:x+f", "x + x^(2)");
		t("g4:f+f", "x^(2) + x^(2)");
		t("g5:f(x)+f", "x^(2) + x^(2)");
		t("g6(t)=t+f", "t + t^(2)");
		t("g7:f*2", "(x^(2) * 2)");
		t("g8:2+f", "2 + x^(2)");
		t("a(x,y)=x + y", "x + y");
		t("a+f", "x + y + x^(2)");
		t("a+x", "x + y + x");
		t("a+y", "x + y + y");
		t("a+a", "x + y + x + y");
		shouldFail("f+y", "Please check your input", getApp());
		shouldFail("y+f", "Please check your input", getApp());
	}

	@Test
	public void tuples() {
		t("(1..2,1..2)", "{(1, 1), (2, 2)}");
		t("(1,1..5)", "{(1, 1), (1, 2), (1, 3), (1, 4), (1, 5)}");
		t("(1,1..5,6..2)",
				"{(1, 1, 6), (1, 2, 5), (1, 3, 4), (1, 4, 3), (1, 5, 2)}");
		t("(1;(1..5)*2pi/5)",
				TestStringUtil.unicode(
						"{(1; 72deg), (1; 144deg), (1; 216deg), (1; 288deg), (1; 0deg)}"),
				StringTemplate.editTemplate);
	}

	@Test
	public void equationListTest() {
		t("x+y=1..5",
				"{x + y = 1, x + y = 2, x + y = 3, x + y = 4, x + y = 5}");
		t("x^2+y^2=1..5",
				TestStringUtil.unicode(
						"{x^2 + y^2 = 1," + " x^2 + y^2 = 2, x^2 + y^2 = 3,"
								+ " x^2 + y^2 = 4, x^2 + y^2 = 5}"),
				StringTemplate.editTemplate);
		t("f(r)=(r,sin(r)*(1..5))",
				"{(r, (sin(r) * 1)), (r, (sin(r) * 2)), (r, (sin(r) * 3)),"
						+ " (r, (sin(r) * 4)), (r, (sin(r) * 5))}");
		t("f(r)=(r+(1..5),sin(r)*(1..5))",
				"{(r + 1, (sin(r) * 1)), (r + 2, (sin(r) * 2)), (r + 3, (sin(r) * 3)),"
						+ " (r + 4, (sin(r) * 4)), (r + 5, (sin(r) * 5))}");
		t("f(r)=((1..5)*r,sin(r)+1)",
				"{((1 * r), sin(r) + 1), ((2 * r), sin(r) + 1), ((3 * r), sin(r) + 1),"
						+ " ((4 * r), sin(r) + 1), ((5 * r), sin(r) + 1)}");
		t("1..2={sin(x),x}", "{1 = sin(x), x = 2}");
		t("1..2={x}", "?");
		shouldFail("{(1,2)}={(1,3)}", "Invalid equation", getApp());
	}

	@Test
	public void equationListShouldUseColonInDefinition() {
		GeoElement eqnList = add("1..1={x}");
		assertEquals("eq1: 1" + Unicode.ELLIPSIS + "1={x}", eqnList.getDefinitionForEditor());
	}

	@Test
	public void functionsList() {
		t("(1..2)+x*(1..2)", "{1 + (x * 1), 2 + (x * 2)}");
		t("x+y+(1..3)", "{x + y + 1, x + y + 2, x + y + 3}");
		t("list1=(-2..2)", "{-2, -1, 0, 1, 2}");
		t("(list1*t,(1-t)*(1-list1))",
				"{((-2 * t), ((1 - t) * 3)), ((-1 * t), ((1 - t) * 2)), ((0 * t), "
						+ "((1 - t) * 1)), ((1 * t), ((1 - t) * 0)), ((2 * t), ((1 - t) * (-1)))}");
	}

	@Test
	public void xcoordAsFunction() {
		t("x((1,2))", "1");
	}

	@Test
	public void absFunction() {
		AlgebraTestHelper.enableCAS(getApp(), false);
		t("f:abs(x+2)", "abs(x + 2)");
		Assert.assertTrue(((GeoFunction) lookup("f"))
				.isPolynomialFunction(true));
		Assert.assertFalse(((GeoFunction) lookup("f"))
				.isPolynomialFunction(false));
	}

	@Test
	public void xcoordAsMultiplication() {
		t("x(x+1)", "(x * (x + 1))");
		t("x(x+1)^2", "(x * (x + 1)^(2))");
	}

	@Test
	public void derivativeShouldBeHiddenWithNoCas() {
		getApp().getSettings().getCasSettings().setEnabled(false);
		t("f(x)=x", "x");
		t("f'", "NDerivative[f]");
		t("f'(7)", "1");
	}

	@Test
	public void crossProductMissingBracketsTest() {
		t("A = (1, 2)", "(1, 2)");
		t("B = (2, 3)", "(2, 3)");
		t("C = (6, 3)", "(6, 3)");
		t("D = (0, 4)", "(0, 4)");
		t("E = Cross(A - B, C - D)", "7");
		assertEquals("(A - B) " + Unicode.VECTOR_PRODUCT + " (C - D)",
				lookup("E").getDefinition(StringTemplate.defaultTemplate));

		t("F = Cross(A, B)^2", "1");
		assertEquals("(A " + Unicode.VECTOR_PRODUCT + " B)" + Unicode.SUPERSCRIPT_2,
				lookup("F").getDefinition(StringTemplate.defaultTemplate));

		t("G = (1, 2, 3)", "(1, 2, 3)");
		t("H = (2, 3, 4)", "(2, 3, 4)");
		t("I = Cross(G, H)^2", "6");
		assertEquals("(G " + Unicode.VECTOR_PRODUCT + " H)" + Unicode.SUPERSCRIPT_2,
				lookup("I").getDefinition(StringTemplate.defaultTemplate));
	}

	@Test
	public void absFunctionBugFix() {
		getApp().getSettings().getCasSettings().setEnabled(true);
		t("eq1:abs(x-3) = -2", "abs(x - 3) = -2");
		t("eq2:abs(x-3) = 2", "x^2 - 6x = -5");
	}

	@Test
	public void evaluationOfUndefinedFunctionShouldBeUndefined() {
		t("f=Element({x y}, 2)", "?");
		t("f(1, 1)", "NaN");
		t("f((1, 1))", "NaN");
		t("g=Element({x}, 2)", "?");
		t("g(1)", "NaN");
		t("g((1, 1))", "NaN");
	}

	@Test
	public void sumOfUndefinedFunctionsShouldBeUndefined() {
		t("l={x}", "{x}");
		t("l={}", "{}");
		t("f(x):=Element(l,3)", "?");
		assertNull(((GeoFunction) lookup("f")).getFunction());
		t("g:x + 1", "x + 1");
		t("f + g", "?");
		t("g + f", "?");
	}

	@Test
	public void elementOfShouldBeRay() {
		add("l1={Ray((0,0),(1,1)),Ray((1,2),(2,4))}");
		add("a=Slider(1,2,1)");
		GeoLine el = add("r=l1(a)");
		assertEquals(el.getTypeString(), "Ray");
		add("SetValue(a,2)");
		assertThat(el, hasValue("-2x + y = 0"));
		assertThat(el.getStartPoint(), hasValue("(1, 2)"));
	}

	@Test
	public void functionCopyShouldBeDependent() {
		t("f:x", "x");
		t("g(x)=f", "x");
		assertEquals("f(x)",
				lookup("g").getDefinition(StringTemplate.defaultTemplate));
		t("ff(x,y)=x+y", "x + y");
		t("gg(a,b)=ff", "a + b");
		assertEquals("ff(a, b)",
				lookup("gg").getDefinition(StringTemplate.defaultTemplate));
	}

	@Test
	public void inequalityShouldNotHaveExtraBrackets() {
		t("r:4 < x < 5", "4 < x < 5");
		t("a = 1", "1");
		t("b = 2", "2");
		t("p1:a < x", "1 < x");
		t("p2:a < x < b", "1 < x < 2");
		t("p3:(a < x) + (x < b)", "(1 < x) + (x < 2)");
		t("p4:a < (x + x) < b", "1 < x + x < 2");
	}

	@Test
	public void complexPowers() {
		t("real((1+i)^(0..8))",
				"{1, 1, 0, -2, -4, -4, 0, 8, 16}", StringTemplate.editTemplate);
		t("imaginary((1+i)^(0..8))",
				"{0, 1, 2, 2, 0, -4, -8, -8, 0}", StringTemplate.editTemplate);
	}

	@Test
	public void complexTrigonometry() {
		t("tan(atan(1+0.5i))", "1 + 0.5" + Unicode.IMAGINARY,
				StringTemplate.editTemplate);
		t("sin(asin(1+0.5i))", "1 + 0.5" + Unicode.IMAGINARY,
				StringTemplate.editTemplate);
		t("cos(acos(1+0.5i))", "1 + 0.5" + Unicode.IMAGINARY,
				StringTemplate.editTemplate);
	}

	@Test
	public void complexExpIntegral() {
		t("expIntegral(1+2i)", "1.04217 + 3.7015" + Unicode.IMAGINARY,
				StringTemplate.editTemplate);
	}

	@Test
	public void powerWithNegativeFractionAsExponent() {
		t("(-8)^(-(1/3))", "-0.5");
		t("-8^(-1/3)", "-0.5");
		t("-8^(-2/3)", "-0.25");
		t("32^(-1/5)", "0.5");
	}

	@Test
	public void testAVShortIf() {
		t("3,5>x", "If[5 > x, 3]");
	}

	@Test
	public void testSetDifference() {
		t("{{1,2},{3}} \\ {{1,2}}", "{{3}}");
		t("{{1,13-11},{3}} \\ {{1,7-5}}", "{{3}}");
		t("{(1,2),(3,4)} \\ {(1,2)}", "{(3, 4)}");
		t("{(1,2,0),(3,4,0)} \\ {(1,2,0)}", "{(3, 4, 0)}");
	}

	@Test
	public void setDifferenceShouldWorkWithLabeledAndUnlabeled() {
		t("m1={{\"a\",\"b\"},{\"c\",\"d\"},{\"a\",\"b\"}}",
				"{{\"a\", \"b\"}, {\"c\", \"d\"}, {\"a\", \"b\"}}");
		t("l1={\"a\",\"b\"}", "{\"a\", \"b\"}");
		t("m1 \\ {l1}", "{{\"c\", \"d\"}}");
	}

	@Test
	public void testEqualityCheck() {
		// in common-jre this will only work correctly for polynomials
		add("f(x)=x^3");
		add("g(x)=-x^3");
		t("f==g", "false");
		t("f!=g", "true");
		t("f==-g", "true");
		t("f!=-g", "false");
	}

	@Test
	public void testEqualityFunctionLine() {
		add("f(x)=3x+2");
		add("g(x)=1");
		add("ff:y=3x+2");
		add("gg:y=1");
		t("ff==f", "true");
		t("gg==g", "true");
		t("ff==g", "false");
		t("gg==f", "false");
	}

	@Test
	public void testEqualityOfIneqs() {
		assertAreEqual("x>1", "2x>2", true);
		assertAreEqual("x>1", "2x<2", false);
		assertAreEqual("x>1", "x>=1", false);
		assertAreEqual("x>1", "x^3>1", true);
		assertAreEqual("x>1", "x^2>1", false);
		assertAreEqual("x>1", "y>1", false);
	}

	@Test
	public void testEqualityOfIneqsNVar() {
		assertAreEqual("x > y", "2y < 2x", true);
		assertAreEqual("x > y", "2y <= 2x", false);
		assertAreEqual("x > y", "2y > 2x", false);
		assertAreEqual("2x^2 + 2y^2 > 2", "x^2 + x + y^2 + y > x + y + 1", true);
		assertAreEqual("2x^2 + 2y^2 > 2", "x^2 + x + y^2 + 2y > x + y + 1", false);
		assertAreEqual("y > sin(x)", "y > sin(x)", true);
		assertAreEqual("y > sin(x)", "y > cos(x)", false);
	}

	@Test
	public void emptySetsShouldBeEqual() {
		assertAreEqual("0x + 0y > 0", "0x + 0y > 1", true);
		assertAreEqual("x^2 + y^2 <= -1", "x^2 + y^2 < -2", true);
	}

	@Test
	public void emptyAndFullSetsShouldDiffer() {
		assertAreEqual("0x + 0y > 0", "0x + 0y < 1", false);
		assertAreEqual("0x + 0y >= 1", "0x + 0y < 1", false);
		assertAreEqual("x^2 + y^2 <= -1", "x^2 + y^2 > -1", false);
	}

	@Test
	public void fullSetsShouldBeEqual() {
		assertAreEqual("0x + 0y >= 0", "0x + 0y < 1", true);
		assertAreEqual("0x + 0y >= 0", "0x + 0y <= 0", true);
		assertAreEqual("x^2 + y^2 >= -1", "x^2 + y^2 > -2", true);
	}

	@Test
	public void testEqualityOfIneqsUndefined() {
		assertAreEqual("x > y", "x^3 > y^3", "?");
		// needs CAS
		assertAreEqual("y > sin(x)", "y > sin(x + 2pi)", "?");
	}

	@Test
	public void undefinedComparisonShouldReturnFalse() {
		t("a = 1", "1");
		t("? < 1", "false");
		t("? < a", "false");
	}

	@Test
	public void undefinedComparisonInFunctionShouldBeUndefined() {
		t("f(x) = If(x/abs(x)<0,1,2)", "If[x / abs(x) < 0, 1, 2]");
		t("f(0)", "NaN");
	}

	@Test
	public void sufficientPrecisionForMultiplication() {
		t("3 * 325.94", "977.82", StringTemplate.maxDecimals);
		t("325.94 * 3", "977.82", StringTemplate.maxDecimals);
	}

	@Test
	public void sufficientPrecisionForDivision() {
		t("490/0.035", "14000", StringTemplate.maxDecimals);
		t("0.49/0.035", "14", StringTemplate.maxDecimals);
	}

	@Test
	@Issue("APPS-5546")
	public void sufficientPrecisionForAddition() {
		t("1295.1+42.37", "1337.47", StringTemplate.maxDecimals);
		t("200 (1+((0.08)/(525600)))^(525600)", "216.65741221592083", StringTemplate.maxDecimals);
	}

	@Test
	@Issue("APPS-5546")
	public void sufficientPrecisionForSubtraction() {
		t("1295.1-42.37", "1252.73", StringTemplate.maxDecimals);
	}

	@Test
	@Issue("APPS-5546")
	public void sufficientPrecisionForPower() {
		t("123456789.12^2-123456789^2-2*123456789*0.12", "0.0144", StringTemplate.maxDecimals);
	}

	@Test
	public void numberAndBoolOperations() {
		t("5 * true", "5");
		t("true * 5", "5");
		t("5 + true", "6");
		t("true + 5", "6");
		t("5 - true", "4");
		t("true - 5", "-4");
		t("5 / true", "5");
		t("true / 5", "0.2");
	}

	@Test
	public void testImpreciseForDivisionIncludingSlider() {
		GeoNumeric a = add("a = 1");
		a.setAVSliderOrCheckboxVisible(true);
		a.initAlgebraSlider();
		assertTrue(a.getNumber().isImprecise());
		GeoNumeric b = add("a/7.01");
		assertTrue(b.getNumber().isImprecise());
		GeoNumeric c = add("7.01/a");
		assertTrue(c.getNumber().isImprecise());
	}

	@Test
	public void sufficientPrecisionForRepeatedMultiplication() {
		t("a=1000/999", "1.001", StringTemplate.editTemplate);
		StringBuilder power = new StringBuilder("a");
		for (int i = 0; i < 999; i++) {
			power.append("*a");
		}
		t(power.toString(), "2.71964221644285", StringTemplate.maxDecimals);
	}

	@Test
	public void testPolarCoords() {
		add("a=1");
		t("(1,1)+(a;pi)", "(0, 1)", StringTemplate.editTemplate);
		t("(2;a*pi)+(0,0)", "(2; 180" + Unicode.DEGREE_STRING + ")",
				StringTemplate.editTemplate);
	}

	@Test
	public void expandBracketsForImplicitCurve() {
		add("s(x,y)=x+y");
		t("s^3.2+3=0", "(x + y)^(3.2) + 3 = 0");
	}

	@Test
	public void testMixedNumbers() {
		t("2 " + Unicode.INVISIBLE_PLUS + "3 / 4 * 3", "8.25");
		t("2 * 2" + Unicode.INVISIBLE_PLUS + "3 / 4", "5.5");
		t("2 + 2" + Unicode.INVISIBLE_PLUS + "4 / 5", "4.8");
		t("2.5 / 2" + Unicode.INVISIBLE_PLUS + "1 / 2", "1");
		t("(2" + Unicode.INVISIBLE_PLUS + "1 / 2) / 2", "1.25");
	}

	@Test
	public void testRecurringDecimals() {
		t("1.23" + Unicode.OVERLINE + "4" + Unicode.OVERLINE, "1.2343434343434343");
		t("1.3" + Unicode.OVERLINE + " / 0.5", "2.6666666666666665");
		t("1.0" + Unicode.OVERLINE, "1");
		t("1.3" + Unicode.OVERLINE + " * 3", "4");
		t("2.6" + Unicode.OVERLINE + " / 2", "1.3333333333333333");
	}

	@Test
	public void testMultipleMinus() {
		t("--2", "2");
		t("-(-2)", "2");
		t("--3--4", "7");
		t("--2*3", "6");
		t("---4", "-4");
		t("---5 ---3", "-8");
		t("---3 + --2", "-1");
	}

	@Test
	public void testFactorial() {
		t("0!", "1");
		t("6!", "720");
		t("?!", "NaN");
		t("(-5)!", "NaN");
		t("infinity!", "NaN");
		t("(-infinity)!", "NaN");
	}

	@Test
	public void testGamma() {
		t("gamma(5)", "24", StringTemplate.editTemplate);
		t("gamma(-5)", "NaN");
		t("gamma(-1/2)+2*sqrt(pi)", "0");
		t("gamma(infinity)", "NaN");
	}

	@Test
	public void testInvalidImplicitCurve() {
		t("x^3+y^3+z^3=1", "?");
	}

	@Test
	public void multiplicationByScriptShouldNotCrash() {
		GeoElement text = add("\"prefix\"*SlowPlot(x)");
		assertThat(text.toValueString(StringTemplate.latexTemplate),
				equalTo("prefixSlowPlot(x)"));
		assertThat(text.getDefinition(StringTemplate.defaultTemplate),
				equalTo("\"prefix\" SlowPlot(x)"));
	}

	@Test
	public void xorShouldFail() {
		shouldFail("1" + Unicode.XOR + "2", "1 " + Unicode.XOR + " 2", getApp());
	}

	@Test
	@Issue("APPS-5572")
	public void dollarOpsShouldKeepLabel() {
		add("A1=7");
		GeoElement nextRow = add("A2=A$1");
		assertEquals("A2", nextRow.getLabelSimple());
	}

	@Test
	public void implicationKeepsBrackets() {
		add("a=false");
		add("b=false");
		add("c=false");
		GeoElement d = add("d=a->(b->c)");
		assertThat(d, hasValue("true"));
		assertThat(d.getDefinitionForEditor(), equalTo("d=a->(b->c)"
				.replace("->", Unicode.IMPLIES + "")));
		reload();
		assertThat(lookup("d"), hasValue("true"));
	}

	private void assertAreEqual(String first, String second, Object areEqual) {
		getKernel().clearConstruction(false);
		add("f:" + first);
		add("g:" + second);
		t("f==g", String.valueOf(areEqual));
	}
}