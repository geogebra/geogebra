package org.geogebra.common.kernel.geos;

import static com.himamis.retex.editor.share.util.Unicode.EULER_STRING;
import static com.himamis.retex.editor.share.util.Unicode.pi;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.geogebra.common.gui.dialog.options.model.ObjectSettingsModel;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.SuggestionRootExtremum;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GeoSymbolicTest {
	private static App app;
	private static AlgebraProcessor ap;

	/**
	 * Create the app
	 */
	@BeforeClass
	public static void setup() {
		app = AlgebraTest.createApp();
		app.getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		app.setRounding("10");
		ap = app.getKernel().getAlgebraProcessor();
		app.getKernel().getGeoGebraCAS().evaluateGeoGebraCAS("1+1", null,
				StringTemplate.defaultTemplate, app.getKernel());
	}

	public static void t(String input, String... expected) {
		AlgebraTestHelper.testSyntaxSingle(input, expected, ap,
				StringTemplate.testTemplate);
	}

	private static void testValidResultCombinations(String input, String... validResults) {
		AlgebraTestHelper.testValidResultCombinations(
				input, validResults,
				ap, StringTemplate.testTemplate);
	}

	public static void t(String input, Matcher<String> expected) {
		AlgebraTestHelper.testSyntaxSingle(input, Collections.singletonList(expected), ap,
				StringTemplate.testTemplate);
	}

	@Before
	public void clean() {
		app.getKernel().clearConstruction(true);
	}

	@Test
	public void expression() {
		t("a=p+q", "p + q");
		checkInput("a", "a = p + q");
	}

	@Test
	public void assignmentOperators() {
		t("b:=p+q", "p + q");
		checkInput("b", "b = p + q");
		t("c:p+q", "p + q");
		checkInput("c", "c = p + q");
	}

	private void checkInput(String label, String expectedInput) {
		assertEquals(expectedInput,
				getSymbolic(label).getDefinitionForEditor());
	}

	@Test
	public void recursiveEquation() {
		t("a=a^2-2", "a = a^(2) - 2");
	}

	@Test
	public void recursiveSystem() {
		t("a = b + b", "2 * b");
		t("b = a - 1", "b = 2 * b - 1");
	}

	@Test
	public void equation() {
		t("x+y=p", "x + y = p");
	}

	@Test
	public void dependentExpression() {
		t("a=p+q", "p + q");
		t("b=2*a", "2 * p + 2 * q");
	}

	@Test
	public void latex() {
		t("a=sqrt(8)", "2 * sqrt(2)");
		String text = getLatex("a");
		assertEquals("a \\, = \\,2 \\; \\sqrt{2}", text);
	}

	private static String getLatex(String string) {
		GeoElement geo1 = getSymbolic(string);
		return geo1.getLaTeXAlgebraDescription(
				geo1.needToShowBothRowsInAV() != DescriptionMode.DEFINITION,
				StringTemplate.latexTemplate);
	}

	@Test
	public void variables() {
		t("f(x,y)=x+y", "x + y");
		assertEquals("f\\left(x, y \\right) \\, = \\,x + y",
				getLatex("f"));
	}

	@Test
	public void plugVariables() {
		t("f(x,y)=x+y", "x + y");
		t("r=f(a+b,a-b)", "2 * a");
	}

	@Test
	public void commands() {
		t("Derivative(a*x^3)", "3 * a * x^(2)");
	}

	@Test
	public void nestedCommands() {
		t("Derivative(Derivative(a*x^3))", "6 * a * x");
		t("Factor(Expand((x-aaa)^2+4x aaa))", "(x + aaa)^(2)");
	}

	@Test
	public void testSequenceCommand() {
		t("2*Sequence(Mod(n,3),n,1,5)", "{2, 4, 0, 2, 4}");
		t("Sequence(Mod(n,3),n,1,5)", "{1, 2, 0, 1, 2}");
		t("Sequence(j,j,1,10)", "{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}");
		t("Sequence(Sequence(k+1/j,k,1,3),j,1,3)",
				"{{2, 3, 4}, {3 / 2, 5 / 2, 7 / 2}, {4 / 3, 7 / 3, 10 / 3}}");
		t("Sequence(Sequence(Sequence(k+1/j+m^2,k,1,2),j,1,2),m,1,2)",
				"{{{3, 4}, {5 / 2, 7 / 2}}, {{6, 7}, {11 / 2, 13 / 2}}}");
		t("Invert(Sequence(Sequence(1/k^2+j^3+(k+j)^2,k,1,3),j,1,3))",
				"{{3593 / 1316, (-4449) / 1316, 340 / 329}, {(-1444) / 329, 1684 / 329, (-492) / 329}, {2277 / 1316, (-2475) / 1316, 351 / 658}}");

	}

	@Test
	public void testLongNumbers() {
		// long output
		t("LCM(Sequence(j, j, 60, 76))", "2601813677319187531200");
		t("111111111111111^2", "12345679012345654320987654321");
		// APPS-1038 long input:
		// t("11111111111111111^2", "123456790123456787654320987654321");
		// t("111111111111111111111111111111^2",
		// "12345679012345679012345679012320987654320987654320987654321");
	}

	@Test
	public void testSubstituteCommand() {
		t("Substitute(x^2+y^2, x=aaa)", "aaa^(2) + y^(2)");
		t("Substitute(x^2+y^2, {x=aaa, y=bbb})", "aaa^(2) + bbb^(2)");
	}

	@Test
	public void testNoCommand() {
		t("x+x", "2 * x");
		t("aaa + aaa", "2 * aaa");
	}

	@Test
	public void testSolveCommand() {
		t("Solve(x*a^2=4*a, a)", "{a = 4 / x, a = 0}");

		t("f(x)=x^3-k*x^2+4*k*x",
				anyOf(equalTo("-k * x^(2) + x^(3) + 4 * k * x"),
						equalTo("x^(3) - k * x^(2) + 4 * k * x")));
		t("Solve(f(x) = 0)", anyOf(
				equalTo("{x = (k - sqrt(k^(2) - 16 * k)) / 2, x ="
						+ " (k + sqrt(k^(2) - 16 * k)) / 2, x = 0}"),
				equalTo("{x = (k + sqrt(k^(2) - 16 * k)) / 2, x = (k - sqrt(k^(2) - 16 * k)) / 2, x = 0}")));

		t("Solve(k(k-16)>0,k)", "{k < 0, k > 16}");
		t("Solve(x^2=4x)", "{x = 0, x = 4}");
		t("Solve({x=4x+y,y+x=2},{x, y})", "{{x = -1, y = 3}}");
		t("Solve(sin(x)=cos(x))", "{x = k_1 * \u03c0 + 1 / 4 * \u03c0}");
		t("Solve(x^2=1)", "{x = -1, x = 1}");
		t("Solve(x^2=a)", "{x = -sqrt(a), x = sqrt(a)}");
		t("Solve({x+y=1, x-y=3})", "{{x = 2, y = -1}}");
		t("Solve({aa+bb=1, aa-bb=3})", "{{aa = 2, bb = -1}}");
		t("Solve({(x, y) = (3, 2) + t*(5, 1), (x, y) = (4, 1) + s*(1, -1)}, {x, y, t, s})",
				"{{x = 3, y = 2, t = 0, s = -1}}");
		testValidResultCombinations(
				"Solve(p x^3 + q x,x)",
				"{x = sqrt(-p * q) / p, x = (-sqrt(-p * q)) / p, x = 0}",
				"{x = (-sqrt(-p * q)) / p, x = sqrt(-p * q) / p, x = 0}");
		testValidResultCombinations(
				"Solve(p x^3 + q x = 0,x)",
				"{x = sqrt(-p * q) / p, x = (-sqrt(-p * q)) / p, x = 0}",
				"{x = (-sqrt(-p * q)) / p, x = sqrt(-p * q) / p, x = 0}");
		testValidResultCombinations(
				"Solve(p x^3 + q x)",
				"{x = sqrt(-p * q) / p, x = (-sqrt(-p * q)) / p, x = 0}",
				"{x = (-sqrt(-p * q)) / p, x = sqrt(-p * q) / p, x = 0}");
		testValidResultCombinations(
				"Solve(p x^3 + q x = 0)",
				"{x = sqrt(-p * q) / p, x = (-sqrt(-p * q)) / p, x = 0}",
				"{x = (-sqrt(-p * q)) / p, x = sqrt(-p * q) / p, x = 0}");
		t("Solve(1-p^2=(1-0.7^2)/4)", "{p = (-sqrt(349)) / 20, p = sqrt(349) / 20}");
		t("NSolve(1-p^2=(1-0.7^2)/4)", "{p = -0.9340770846, p = 0.9340770846}");
	}

	@Test
	public void testNumericCommand() {
		t("Numeric(2/3,10)", "0.6666666667");
		t("Numeric(pi,10)", "3.141592654");
		// wrong
		t("Numeric(pi,100)", pi + "");
		// wrong
		t("Numeric(2pi,100)", "6.2831853072");
	}

	@Test
	public void testMultiStep() {
		t("f(x) = (p x^3 + q x)", "p * x^(3) + q * x");
		t("f'(0)", "q");
		testValidResultCombinations(
				"Solve(f(x)=0)",
				"{x = sqrt(-p * q) / p, x = (-sqrt(-p * q)) / p, x = 0}",
				"{x = (-sqrt(-p * q)) / p, x = sqrt(-p * q) / p, x = 0}");
		testValidResultCombinations(
				"Solve(f=0)",
				"{x = sqrt(-p * q) / p, x = (-sqrt(-p * q)) / p, x = 0}",
				"{x = (-sqrt(-p * q)) / p, x = sqrt(-p * q) / p, x = 0}");
		t("Integral(f,0,1)", "1 / 4 * (p + 2 * q)");
		t("Solve(Integral(f,0,1)=0,p)", "{p = -2 * q}");
	}

	@Test
	public void testMultiStep2() {
		t("h(t):=8/(1+15exp(-0.46t))",
				"8 / (15 * " + EULER_STRING + "^((-23) / 50 * t) + 1)");
		t("a=h(10)-h(0)",
				"(-1) / 2 + 8 / (15 * 1 / nroot(" + EULER_STRING + "^(23),5) + 1)");
		t("b=a/(7-0.6)",
				"5 / 32 * ((-1) / 2 + 8 / (15 / nroot(" + EULER_STRING + "^(23),5) + 1))");
		t("Solve(h''(t)=0)", "{t = 50 / 23 * log(15)}");
		testValidResultCombinations(
				"h'(5.8871)",
				"276 * " + EULER_STRING + "^((-1354033) / 500000) / (1125 * (" + EULER_STRING
						+ "^((-1354033) / 500000))^(2) + 150 * " + EULER_STRING
						+ "^((-1354033) / 500000) + 5)",
				"276 * " + EULER_STRING + "^((-1354033) / 500000) / "
						+ "(150 * " + EULER_STRING + "^((-1354033) / 500000) + "
						+ "1125 * (" + EULER_STRING + "^((-1354033) / 500000))^(2)"
						+ " + 5)");
	}

	@Test
	public void testMultiStep3() {
		t("h_1(tt)=kk tt + dd", "kk * tt + dd");
		t("Solve({h_1(0)=0.6, h_1(12)=7.6}, {kk,dd})", "{{kk = 7 / 12, dd = 3 / 5}}");
		// strange answer with missing underscore in second h_1
		t("Solve({h_1(0)=0.6, h1(12)=7.6}, {kk,dd})", "{{kk = kk, dd = 3 / 5}}");
	}

	@Test
	public void testMultiStep4() {
		t("f(t)=21000000-21000000exp(-0.18t)",
				"-21000000 * " + EULER_STRING + "^((-9) / 50 * t) + 21000000");
		t("b=(f(8)-f(7))/f(7)", "(1 / nroot(" + EULER_STRING + "^(36),25) - 1 / nroot("
				+ EULER_STRING + "^(63),50)) / (1 / nroot(" + EULER_STRING + "^(63),50) - 1)");
		t("Solve(f(t)=20*10^6)", "{t = 50 / 9 * log(21)}");
	}

	@Test
	public void testMultiStep5() {
		t("a(p)=-(-p+1)^2+1", "-(-p + 1)^(2) + 1");
		t("a'(p)", "-2 * p + 2");
		t("Solve(a'(p)>0)", "{p < 1}");
	}

	@Test
	public void testMultiStep6() {
		t("eq1:x^2+y^2=r^2", "x^(2) + y^(2) = r^(2)");
		t("eq2:(x-1)^2+y^2=s^2", "y^(2) + (x - 1)^(2) = s^(2)");
		t("c:Intersect(eq1, eq2)",
				"{(1 / 2 * r^(2) - 1 / 2 * s^(2) + 1 / 2, sqrt(-r^(4) + 2 * r^(2) * s^(2) + 2 * r^(2) - s^(4) + 2 * s^(2) - 1) / 2), (1 / 2 * r^(2) - 1 / 2 * s^(2) + 1 / 2, (-sqrt(-r^(4) + 2 * r^(2) * s^(2) + 2 * r^(2) - s^(4) + 2 * s^(2) - 1)) / 2)}");
		t("d=Element(c,1)",
				"((r^(2) - s^(2) + 1) / 2, sqrt(-r^(4) + 2 * r^(2) * s^(2) + 2 * r^(2) - s^(4) + 2 * s^(2) - 1) / 2)");
		t("e=Element(c,2)",
				"((r^(2) - s^(2) + 1) / 2, (-sqrt(-r^(4) + 2 * r^(2) * s^(2) + 2 * r^(2) - s^(4) + 2 * s^(2) - 1)) / 2)");
		t("Line(d,e)", "x = 1 / 2 * r^(2) - 1 / 2 * s^(2) + 1 / 2");
	}

	/**
	 * https://www.geogebra.org/m/mxtyvd22
	 */
	@Test
	public void testTutorial() {
		t("a+a", "2 * a");
		t("4x+3y-2x+y", "2 * x + 4 * y");
		testValidResultCombinations(
				"(1/(x+y)-1/x)/y",
				"(-1) / (x^(2) + x * y)", "(-1) / (x * y + x^(2))");
		t("(x+y)(x-y)(x-y)", "(x + y) * (x - y)^(2)");
		t("Expand((x+y)(x-y)(x-y))", "x^(3) - x^(2) * y - x * y^(2) + y^(3)");
		t("Factor(x^2+2x+1)", "(x + 1)^(2)");
		t("Factor(x^3-x^2-8x+12)", "(x - 2)^(2) * (x + 3)");
		t("Factor((x^2+2x-15)/(x^3+3x^2-4))", "(x - 3) * (x + 5) / ((x - 1) * (x + 2)^(2))");
		t("Substitute(x^2-2x+23,x,y^2)", "y^(4) - 2 * y^(2) + 23");
		t("Solve(3(x-2)=5x+14)", "{x = -10}");
		t("Solve(2x^2-x=15)", "{x = (-5) / 2, x = 3}");
		t("Solve(2x^2-x=21)", "{x = -3, x = 7 / 2}");
		t("Solve(6x/(x+3)-x/(x-3)=2)", "{x = 1, x = 6}");
		t("Solve(12exp(x)=150)", "{x = log(25 / 2)}");
		t("Solve(cos(x)=sin(x))", "{x = k_1 * " + pi + " + 1 / 4 * " + pi + "}");
		t("Solve(3x+2>-x+8)", "{x > 3 / 2}");
		// doesn't work without space (multiply) APPS-1031
		t("Solve(x (x-5)>x+7)", "{x < -1, x > 7}");
		testValidResultCombinations(
				"Solve(2x+3y=x^2/y,x)",
				"{x = 3 * y, x = -y}", "{x = -y, x = 3 * y}");
		testValidResultCombinations(
				"Solve(2x+3y=x^2/y,y)",
				"{y = 1 / 3 * x, y = -x}", "{y = -x, y = 1 / 3 * x}");
		t("Solve({x+2y+3z=60, 2x-3y+5z=68, -x+y-z=-13})",
				"{{x = 27 / 11, y = 57 / 11, z = 173 / 11}}");
	}

	/**
	 * https://www.geogebra.org/m/mxtyvd22#material/gjsw6npx
	 */
	@Test
	public void testTutorial2() {
		t("f(x)=x^3+6x^2+6x-4", "x^(3) + 6 * x^(2) + 6 * x - 4");
		t("Solve(f=0)", "{x = -sqrt(6) - 2, x = -2, x = sqrt(6) - 2}");
		t("f({-5,0,2.15})", "{-9, -4, 372587 / 8000}");
		t("Solve(f=4)", "{x = -4, x = -sqrt(3) - 1, x = sqrt(3) - 1}");
		t("ff(x,aa)=sqrt(x-aa)", "sqrt(-aa + x)");
		t("ff(x,0)", "sqrt(x)");
		t("ff(x,-1)", "sqrt(x + 1)");
		t("ff(x,2)", "sqrt(x - 2)");
	}

	/**
	 * https://www.geogebra.org/m/mxtyvd22#material/vcdtdhjk
	 */
	@Test
	public void testTutorial3() {
		t("f(x)=p x^4 + q x^3 + r x^2 + s x + k", "p * x^(4) + q * x^(3) + r * x^(2) + s * x + k");
		t("eq1:f(1)=10", "k + p + q + r + s = 10");
		t("eq2:f'(1)=0", "4 * p + 3 * q + 2 * r + s = 0");
		t("eq3:f(4)=-1", "k + 256 * p + 64 * q + 16 * r + 4 * s = -1");
		t("eq4:f''(4)=0", "192 * p + 24 * q + 2 * r = 0");
		t("eq5:f(-3)=0", "k + 81 * p - 27 * q + 9 * r - 3 * s = 0");
		t("u=Solve({eq1, eq2, eq3, eq4, eq5})",
				"{{k = 18659 / 2142, p = 437 / 12852, q = (-535) / 2856, r = (-311) / 306, s = 63197 / 25704}}");
		t("Substitute(f,u)",
				"437 / 12852 * x^(4) - 535 / 2856 * x^(3) - 311 / 306 * x^(2) + 63197 / 25704 * x + 18659 / 2142");
	}

	/**
	 * https://www.geogebra.org/m/mxtyvd22#material/ukkups2n
	 */
	@Test
	public void testTutorial4() {
		t("f(x)=sqrt(x) (x^2-10x+25)", "sqrt(x) * (x^(2) - 10 * x + 25)");
		t("list1=Solutions(f=0)", "{0, 5}");
		t("list2=Solutions(f'(x)=0)", "{1, 5}");
		t("f(list2)", "{16, 0}");
		// not working, same problem in CAS View
		// t("f''(list1)", "{-10, 2 * sqrt(5)}");
		t("f''({1,5})", "{-10, 2 * sqrt(5)}");
		t("f({1,5})", "{16, 0}");
		t("Solve(f''(x)=0)", "{x = (-2 * sqrt(6) + 3) / 3, x = (2 * sqrt(6) + 3) / 3}");
		t("list=Solutions(f''(x)=0)", "{(-2 * sqrt(6) + 3) / 3, (2 * sqrt(6) + 3) / 3}");
		t("root=Element(list,2)", "(2 * sqrt(6) + 3) / 3");
		t("Numeric(f(root))", "9.0912560746");
		t("Solve(f'(x)=tan(30deg))", "{x = 0.9446513612, x = 5.1267111169}");
		t("Tangent(2,f)", "y = -15 * sqrt(2) / 4 * x + 33 * sqrt(2) / 2");
	}

	/**
	 * https://www.geogebra.org/m/mxtyvd22#material/jueqqgec
	 */
	@Test
	public void testTutorial5() {
		t("f(x)=1/25 x^4", "1 / 25 * x^(4)");
		t("g=Invert(f)", "nroot(25 * x,4)");
		t("a=pi Integral(g^2,0,h)", "10 / 3 * sqrt(h) * h * " + pi);
		t("b=Solve(a=500)", "{h = 5 * cbrt(180 * " + pi + ") / " + pi + "}");
		t("Numeric(b)", "{h = 13.1611626882}");
	}

	@Test
	public void testLists() {
		t("f(x)=x^2", "x^(2)");
		t("l1={1,2,3}", "{1, 2, 3}");
		t("f(l1)", "{1, 4, 9}");

		t("f(x)=(3x^3+6x^2-10x+1)", "3 * x^(3) + 6 * x^(2) - 10 * x + 1");
		t("list2=Solutions(f'(x)=0)", "{(-sqrt(14) - 2) / 3, (sqrt(14) - 2) / 3}");
		t("f(list2)", "{1 / 9 * (28 * sqrt(14) + 85), 1 / 9 * (-28 * sqrt(14) + 85)}");
		t("list3={(-sqrt(14) - 2) / 3, (sqrt(14) - 2) / 3}",
				"{1 / 3 * (-sqrt(14) - 2), 1 / 3 * (sqrt(14) - 2)}");
		t("f(list3)", "{1 / 9 * (28 * sqrt(14) + 85), 1 / 9 * (-28 * sqrt(14) + 85)}");
	}

	@Test
	public void testFitPolyCommand() {
		t("FitPoly({(0,0.6), (12,7.6)})", "7 / 12 * x + 3 / 5");
		t("FitPoly({(0,0.3707), (20,0.2091), (10, 0.2428)},2)",
				"4.71E-4 * x^(2) - 0.0175 * x + 0.3707");
	}

	@Test
	public void testLimitCommands() {
		t("Limit(4/(1+exp(-0.7t)),t,infinity)", "4");
		t("Limit(p/(q+exp(-2 t)),t,infinity)", "p / q");
		t("LimitAbove(1/x,0)", "Infinity");
		t("LimitBelow(1/x,0)", "-Infinity");
	}

	@Test
	public void testSolutionsCommand() {
		t("Solutions(x*a^2=4*a, a)", "{4 / x, 0}");
		t("Solutions(x^2=4x)", "{0, 4}");
		t("Solutions({x=4x+y,y+x=2},{x, y})", "{{-1, 3}}");
		t("Solutions(sin(x)=cos(x))",
				"{(-3) / 4 * " + pi + ", 1 / 4 * " + pi + "}");
		t("Solutions(x^2=1)", "{-1, 1}");
		t("Solutions({x+y=1, x-y=3})", "{{2, -1}}");
		t("Solutions({aa+bb=1, aa-bb=3})", "{{2, -1}}");
		t("Solutions(x^2=aaa)", "{-sqrt(aaa), sqrt(aaa)}");
		t("Solutions(y^2=aaa)", "{-sqrt(aaa), sqrt(aaa)}");
		t("Solutions(bbb^2=aaa)", "{-sqrt(aaa), sqrt(aaa)}");
	}

	@Test
	public void testSumCommand() {
		t("Sum(m*(1/2)^(m),m,0,inf)", "2");
		t("Sum(Sum(n*m*(1/2)^(n+m),n,0,inf),m,0,inf)", "4");
	}

	@Test
	public void testIntegralCommand() {
		t("Integral(x*y^2,x,0,2)", "2 * y^(2)");
		t("Integral(x*y^2,x,aaa,bbb)", "y^(2) * ((-1) / 2 * aaa^(2) + 1 / 2 * bbb^(2))");
		t("Integral(Integral(x*y^2,x,0,2),y,0,1)", "2 / 3");
		t("Integral(Integral(x*y^2,x,0,2),y,0,aaa)", "2 / 3 * aaa^(3)");
		t("Integral(exp(-x^2),-inf,inf)", "sqrt(" + pi + ")");
	}

	@Test
	public void testFactorCommand() {
		t("Factor(x^2-1)", "(x - 1) * (x + 1)");
		t("Factor(x^2-a^2 y^2)", "(x - a * y) * (x + a * y)");
	}

	@Test
	public void testExpandCommand() {
		t("Expand((a+b)^3)", "a^(3) + 3 * a^(2) * b + 3 * a * b^(2) + b^(3)");
		t("Expand((x+1/x)^2)", "(x^(4) + 2 * x^(2) + 1) / x^(2)");
		t("Expand((x+(1/aaa)x^2)^2)", "(aaa^(2) * x^(2) + 2 * aaa * x^(3) + x^(4)) / aaa^(2)");
	}

	@Test
	public void testPolynomialCommand() {
		t("Polynomial((x+(1/aaa)x^2)^2)",
				"1 / aaa^(2) * x^(4) + 2 * aaa / aaa^(2) * x^(3) + x^(2)");
	}

	@Test
	public void testCoefficientsCommand() {
		t("Coefficients((x+(1/aaa)x^2)^2)", "{1 / aaa^(2), 2 * aaa / aaa^(2), 1, 0, 0}");
	}

	@Test
	public void testDegreeCommand() {
		t("Degree((x+(1/aaa)x^2)^2)", "2");
	}

	@Test
	public void testTangentCommand() {
		t("Tangent(bbb, y = aaa x^2)", "y = -aaa * bbb^(2) + 2 * aaa * bbb * x");
		t("Tangent((bbb, bbb^2 aaa), y = aaa x^2)",
				"y = -aaa * bbb^(2) + 2 * aaa * bbb * x");
		t("Tangent((1,aaa), y = aaa x^2)", "y = 2 * aaa * x - aaa");
	}

	@Test
	public void testPartialFractionsCommand() {
		t("PartialFractions(x/(x+1))", "1 - 1 / (x + 1)");
		t("PartialFractions(aaa * x/(x+1))", "aaa - aaa / (x + 1)");
	}

	@Test
	public void testSimplifyCommand() {
		t("Simplify(aaa+bbb+aaa+x+y+x+y)", "2 * aaa + bbb + 2 * x + 2 * y");
	}

	@Test
	public void testTrigCommands() {
		t("TrigExpand(tan(aaa+bbb))",
				"(sin(aaa) / cos(aaa) + sin(bbb) / cos(bbb)) / (1 - sin(aaa) / cos(aaa) * sin(bbb) / cos(bbb))");
		t("TrigCombine(sin(aaa)*cos(aaa))", "1 / 2 * sin(2 * aaa)");
		t("TrigSimplify(1-sin(x)^2)", "cos(x)^(2)");
	}

	@Test
	public void testTaylorPolynomialCommand() {
		t("TaylorPolynomial(x^2, a, 1)", "a^(2) + 2 * a * (x - a)");
	}

	@Test
	public void testMatrixCommands() {
		t("Invert({{a,b},{c,d}})",
				"{{d / (a * d - b * c), (-b) / (a * d - b * c)}, {(-c) / (a * d - b * c), a / (a * d - b * c)}}");
		t("{{a,b},{c,d}}^-1",
				"{{d / (a * d - b * c), (-b) / (a * d - b * c)}, {(-c) / (a * d - b * c), a / (a * d - b * c)}}");
		t("Transpose({{a,b},{c,d}})", "{{a, c}, {b, d}}");
		t("EigenValues({{a,b},{c,d}})",
				"{(a + d - sqrt(a^(2) - 2 * a * d + d^(2) + 4 * b * c)) / 2, (a + d + sqrt(a^(2) - 2 * a * d + d^(2) + 4 * b * c)) / 2}");
		t("EigenVectors({{a,b},{c,d}})",
				"{{a - d - sqrt(a^(2) - 2 * a * d + d^(2) + 4 * b * c), a - d + sqrt(a^(2) - 2 * a * d + d^(2) + 4 * b * c)}, {2 * c, 2 * c}}");
		testValidResultCombinations("{{a,b},{c,d}} {{a,b},{c,d}}",
				"{{a^(2) + b * c, a * b + b * d}, {a * c + c * d, d^(2) + b * c}}",
				"{{b * c + a^(2), a * b + b * d}, {a * c + c * d, b * c + d^(2)}}");
		t("{{aa,bb},{cc,dd}} {{ee,ff},{gg,hh}}",
				"{{aa * ee + bb * gg, aa * ff + bb * hh}, {cc * ee + dd * gg, cc * ff + dd * hh}}");
	}

	@Test
	public void testIntersectCommand() {
		t("Intersect(x^2+y^2=5, x+y=sqrt(2))", "{((-sqrt(2)) / 2, 3 * sqrt(2) / 2), (3 * sqrt(2) / 2, (-sqrt(2)) / 2)}");
		t("Intersect(x+y=sqrt(2), y-x=pi)",
				"{((-1) / 2 * " + pi + " + sqrt(2) / 2, 1 / 2 * " + pi + " + sqrt(2) / 2)}");
		t("Intersect((x+8)^2+(y-4)^2=13,(x+4)^2+(y-4)^2=2)",
				"{((-37) / 8, (sqrt(103) + 32) / 8), ((-37) / 8, (-sqrt(103) + 32) / 8)}");
		// t("Intersect((x+1)^2+(y+1)^2=9-4sqrt(2), y^2+(x-2)^2=10)", "");
	}

	@Test
	public void testVectors() {
		// these should give Vector not point
		t("u=(1,2)", "(1, 2)");
		t("u=(1,2,3)", "(1, 2, 3)");

		// wrong GGB-1025
		// t("Length(Vector((3,4)))", "5");
		// t("x(Vector((3,4)))", "3");
		// t("y(Vector((3,4)))", "4");
		// t("z(Vector((3,4)))", "0");
		// t("x(Vector((3,4,5)))", "3");
		// t("y(Vector((3,4,5)))", "4");
		// t("z(Vector((3,4,5)))", "5");
		// t("Dot[Vector[(1,2)],Vector[(3,4)]]", "11");
		// t("Dot[Vector[(a,b)],Vector[(c,d)]]", "p * r + q * s");
		// t("Cross[Vector[(1,2)], Vector[(3,4)]]", "");
		// t("Cross[Vector[(p,q)], Vector[(r,s)]]", "");
		// t("abs(Vector((1,2))", "sqrt(5)");
		// t("UnitVector((1,2))", "");
		// t("UnitVector((p,q))", "");
		// t("UnitPerpendicularVector((1,2))", "");
		// t("UnitPerpendicularVector((p,q))", "");
		// t("PerpendicularVector((1,2))", "");
		// t("PerpendicularVector((p,q))", "");

		t("Dot((p,q),(r,s))", "p * r + q * s");
		t("Dot((1,2),(3,4))", "11");

	}

	@Test
	public void testAngleCommand() {
		t("Angle((1,2),(3,4))", "cos\u207B\u00B9(11 * sqrt(5) / 25)");
		// not working
		// t("Angle[(a,b,c),(d,e,f),(g,h,i)]", "");
	}

	@Test
	public void testReplacingAssignments() {
		t("eq1:x+y=3", "x + y = 3");
		t("eq2:x-y=1", "x - y = 1");
		t("Solve({eq1, eq2})", "{{x = 2, y = 1}}");
	}

	@Test
	public void testPolynomialFit() {
		t("eq1: 9=a*3^3+b*3^2+c*3+d", "9 = 27 * a + 9 * b + 3 * c + d");
		t("eq2: 4=a*2^3+b*2^2+c*2+d", "4 = 8 * a + 4 * b + 2 * c + d");
		t("eq3: 7=a*4^3+b*4^2+c*4+d", "7 = 64 * a + 16 * b + 4 * c + d");
		t("eq4: 1=a*1^3+b*1^2+c*1+d", "1 = a + b + c + d");
		t("Solve({eq1,eq2,eq3,eq4}, {a,b,c,d})",
				"{{a = (-3) / 2, b = 10, c = (-33) / 2, d = 9}}");
	}

	@Test
	public void testCurveSketching() {
		t("f(x)=x^3-2x^2+1", "x^(3) - 2 * x^(2) + 1");
		t("Derivative(f)", "3 * x^(2) - 4 * x");
		t("f''(x)", "6 * x - 4");
		t("Derivative(f, x, 3)", "6");
		t("Solve(f(x) = 0)",
				"{x = (-sqrt(5) + 1) / 2, x = 1, x = (sqrt(5) + 1) / 2}");
		t("Solve(f'(x) = 0)", "{x = 0, x = 4 / 3}");
		t("Solve(f''(x) = 0)", "{x = 2 / 3}");
	}

	@Test
	public void redefinitionInTwoCellsShouldFail() {
		t("a=p+q", "p + q");
		shouldFail("a=p-q", "label is already used");
	}

	@Test
	public void defaultEquationLabel() {
		t("x=y", "x = y");
		t("x=y+a", "x = a + y");
		assertEquals("eq1", app.getGgbApi().getObjectName(0));
		assertEquals("eq2", app.getGgbApi().getObjectName(1));
	}

	@Test
	public void defaultFunctionLabel() {
		t("y=x", "y = x");
		t("y=x+a", "y = a + x");
		assertEquals("f", app.getGgbApi().getObjectName(0));
		assertEquals("g", app.getGgbApi().getObjectName(1));
	}

	@Test
	public void defaultFunctionLHS() {
		t("x", "x");
		t("x+3", "x + 3");
		t("x+y", "x + y");
		assertEquals("f(x)", getObjectLHS("f"));
		assertEquals("g(x)", getObjectLHS("g"));
		assertEquals("a(x, y)", getObjectLHS("a"));
	}

	@Test
	public void redefinitionInOneCellsShouldWork() {
		t("a=p+q", "p + q");
		GeoElement a = getSymbolic("a");
		ap.changeGeoElement(a, "p-q", true, false, TestErrorHandler.INSTANCE,
				null);
		checkInput("a", "a = p - q");
	}

	@Test
	public void constantShouldBeOneRow() {
		t("1", "1");
		GeoElement a = getSymbolic("a");
		assertEquals(DescriptionMode.VALUE, a.needToShowBothRowsInAV());
	}

	@Test
	public void labeledConstantShouldBeOneRow() {
		t("a=7", "7");
		GeoElement a = getSymbolic("a");
		assertEquals(DescriptionMode.VALUE, a.needToShowBothRowsInAV());
	}

	@Test
	public void simpleEquationShouldBeOneRow() {
		t("eq1:x+y=1", "x + y = 1");
		GeoElement a = getSymbolic("eq1");
		assertEquals(DescriptionMode.VALUE, a.needToShowBothRowsInAV());
	}

	@Test
	public void simpleFracShouldBeTwoRows() {
		t("1/2", "1 / 2");
		GeoElement a = getSymbolic("a");
		assertEquals(DescriptionMode.DEFINITION_VALUE, a.needToShowBothRowsInAV());
	}

	@Test
	public void linePropertiesShouldMatchTwin() {
		t("f: x = y", "x = y");

		GeoSymbolic f = getSymbolic("f");
		ObjectSettingsModel model = asList(f);
		model.setLineThickness(7);
		model.setLineStyle(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		assertEquals(8, f.getLineThickness());
		assertEquals(8, f.getTwinGeo().getLineThickness());

		assertEquals(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT,
				f.getLineType());
		assertEquals(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT,
				f.getLineType());
	}

	@Test
	public void pointPropertiesShouldMatchTwin() {
		t("A: (1, 2)", "(1, 2)");

		GeoSymbolic pointA = getSymbolic("A");
		ObjectSettingsModel model = asList(pointA);
		model.setPointSize(7);
		model.setPointStyle(4);
		assertEquals(8, pointA.getPointSize());
		assertEquals(8, ((GeoPoint) pointA.getTwinGeo()).getPointSize());

		assertEquals(4, pointA.getPointStyle());
		assertEquals(4, ((GeoPoint) pointA.getTwinGeo()).getPointStyle());
	}

	@Test
	public void lineShouldSavePropertiesToXML() {
		t("f: x = y", "x = y");

		GeoSymbolic f = getSymbolic("f");
		f.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		f.setLineThickness(8);
		f.setLineOpacity(42);
		app.setXML(app.getXML(), true);

		GeoSymbolic fReloaded = getSymbolic("f");
		assertEquals(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT,
				fReloaded.getLineType());
		assertEquals(8, fReloaded.getLineThickness());
		assertEquals(42, fReloaded.getLineOpacity());

	}

	@Test
	public void testDerivative() {
		t("f(x)=x", "x");
		t("f'", "1");
	}

	@Test
	public void testDerivativeShorthand() {
		t("f(x)=exp(x)", EULER_STRING + "^(x)");
		t("f'(x)=f'(x)", EULER_STRING + "^(x)");
		checkInput("f'", "f'(x) = f'(x)");
	}

	private static GeoSymbolic getSymbolic(String label) {
		GeoElement geo = app.getKernel().lookupLabel(label);
		assertThat(geo, CoreMatchers.instanceOf(GeoSymbolic.class));
		return (GeoSymbolic) geo;
	}

	private ObjectSettingsModel asList(GeoElement f) {
		ArrayList<GeoElement> list = new ArrayList<>();
		list.add(f);
		ObjectSettingsModel model = new ObjectSettingsModel(app) {
		};
		model.setGeoElement(f);
		model.setGeoElementsList(list);
		return model;
	}

	@Test
	public void powerShouldBeOneRow() {
		t("(b+1)^3", "(b + 1)^(3)");
		GeoElement a = getSymbolic("a");
		assertEquals(DescriptionMode.VALUE, a.needToShowBothRowsInAV());
	}

	/**
	 * APPS-1013
	 */
	@Test
	public void updateShouldChangeTheTwin() {
		t("f(x)=x^2", "x^(2)");
		t("f(x)=x^3", "x^(3)");
		GeoElementND twin = getSymbolic("f").getTwinGeo();
		assertEquals("x^(3)", twin.toValueString(StringTemplate.testTemplate));
	}

	@Test
	public void testSymbolicMode() {
		t("a=1/2", "1 / 2");
		GeoSymbolic symbolic = getSymbolic("a");
		symbolic.setSymbolicMode(false, false);
		assertEquals("0.5", symbolic.toValueString(StringTemplate.algebraTemplate));
	}

	@Test
	public void testSymbolicDiffers() {
		t("a=1/2", "1 / 2");
		GeoSymbolic fraction = getSymbolic("a");
		t("l1={1/4, 2,3}", "{1 / 4, 2, 3}");
		GeoSymbolic list = getSymbolic("l1");
		t("eq1:x-3=1/2", "x - 3 = 1 / 2");
		GeoElement equation = getSymbolic("eq1");
		t("l2=Solve(eq1, x)", "{x = 7 / 2}");
		GeoElement solveResult = getSymbolic("l2");

		assertTrue("Fraction should have a symbolic toggle",
				AlgebraItem.isSymbolicDiffers(fraction));
		assertTrue("List of fractions should have a symbolic toggle",
				AlgebraItem.isSymbolicDiffers(list));
		assertFalse("Equation should not have any symbolic toggle",
				AlgebraItem.isSymbolicDiffers(equation));
		assertTrue("Solve result should have a symbolic toggle",
				AlgebraItem.isSymbolicDiffers(solveResult));
	}

	@Test
	public void testStrings() {
		t("\"Hello World!\"", "Hello World!");
		GeoElement element = app.getKernel().getConstruction().getLastGeoElement();
		assertThat(element, instanceOf(GeoText.class));
	}

	@Test
	public void testStringExpression() {
		t("p = 7", "7");
		testValidResultCombinations(
				"p + \" is a prime\"",
				"p is a prime", "7 is a prime");
		GeoElement lastGeoElement = app.getKernel().getConstruction().getLastGeoElement();
		new LabelController().hideLabel(lastGeoElement);
		assertEquals("p + \" is a prime\"",
				lastGeoElement.getDefinitionForEditor());
		assertThat(lastGeoElement, instanceOf(GeoText.class));
	}

	private static void shouldFail(String string, String errorMsg) {
		AlgebraTestHelper.shouldFail(string, errorMsg, app);
	}

	private static String getObjectLHS(String label) {
		GeoElement geo = getSymbolic(label);
		try {
			return geo
					.getAssignmentLHS(StringTemplate.defaultTemplate);
		} catch (Exception e) {
			return "";
		}
	}

	@Test
	public void testCASSpecialPoints() {
		t("f:x", "x");
		GeoSymbolic line = (GeoSymbolic) app.kernel.lookupLabel("f");
		Assert.assertNotNull(SuggestionRootExtremum.get(line));
		SuggestionRootExtremum.get(line).execute(line);
		Assert.assertNull(SuggestionRootExtremum.get(line));
		Object[] list =  app.getKernel().getConstruction().getGeoSetConstructionOrder().toArray();
		((GeoElement) list[list.length - 1]).remove();
		Assert.assertNotNull(SuggestionRootExtremum.get(line));
	}
}
