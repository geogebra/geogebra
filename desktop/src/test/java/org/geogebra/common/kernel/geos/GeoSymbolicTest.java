package org.geogebra.common.kernel.geos;

import static com.himamis.retex.editor.share.util.Unicode.EULER_STRING;
import static com.himamis.retex.editor.share.util.Unicode.pi;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.SuggestionRootExtremum;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.TestStringUtil;
import org.geogebra.test.UndoRedoTester;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoSymbolicTest extends BaseSymbolicTest {

	private void testValidResultCombinations(String input, String... validResults) {
		AlgebraTestHelper.testValidResultCombinations(
				input, validResults,
				ap, StringTemplate.testTemplate);
	}

	public void t(String input, Matcher<String> expected) {
		AlgebraTestHelper.testSyntaxSingle(input, Collections.singletonList(expected), ap,
				StringTemplate.testTemplate);
	}

	@Before
	public void clean() {
		app.getKernel().clearConstruction(true);
		app.setCasConfig();
		app.getKernel().setAngleUnit(app.getConfig().getDefaultAngleUnit());
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
				getSymbolic(label).getDefinitionForInputBar());
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
		assertEquals("a\\, = \\,2 \\; \\sqrt{2}", text);
	}

	private String getLatex(String string) {
		GeoElement geo1 = getSymbolic(string);
		return geo1.getLaTeXAlgebraDescription(
				geo1.getDescriptionMode() != DescriptionMode.DEFINITION,
				StringTemplate.latexTemplate);
	}

	@Test
	public void variables() {
		t("f(x,y)=x+y", "x + y");
		assertEquals("f\\left(x, y \\right)\\, = \\,x + y",
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
				"{{3593 / 1316, -4449 / 1316, 340 / 329}, {-1444 / 329, 1684 / 329, -492 / 329}, {2277 / 1316, -2475 / 1316, 351 / 658}}");

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
		t("Substitute(x^2+y^2, {x=ccc, y=bbb})", "ccc^(2) + bbb^(2)");
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
		t("Solve(x^2=1)", "{x = -1, x = 1}");
		t("Solve(x^2=a)", "{x = -sqrt(a), x = sqrt(a)}");
		t("Solve({x+y=1, x-y=3})", "{{x = 2, y = -1}}");
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
		t("NSolve(1-p^2=(1-0.7^2)/4)", "{p = -0.9340770846135, p = 0.9340770846135}");
	}

	@Test
	public void testSolveCommandCustomVar() {
		t("Solve({aa+bb=1, aa-bb=3})", "{{aa = 2, bb = -1}}");
	}

	@Test
	public void testNumericCommand() {
		t("Numeric(745/1137)", "0.6552330694811");
		tn("Numeric(2/3,10)", "0.6666666667");
		tn("Numeric(pi,10)", "3.141592654");
		tn("Numeric(pi,100)", "3.14159265358979323846264338327950288419716939937"
				+ "5105820974944592307816406286208998628034825342117068");
		tn("Numeric(2pi,100)", "6.2831853071795864769252867665590057683943387987"
				+ "50211641949889184615632812572417997256069650684234136");
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
				"8 / (15 * " + EULER_STRING + "^(-23 / 50 * t) + 1)");
		t("a=h(10)-h(0)",
				"-1 / 2 + 8 / (15 * 1 / nroot(" + EULER_STRING + "^(23),5) + 1)");
		t("b=a/(7-0.6)",
				"5 / 32 * (-1 / 2 + 8 / (15 / nroot(" + EULER_STRING + "^(23),5) + 1))");
		t("Solve(h''(t)=0)", "{t = 50 / 23 * ln(15)}");
		testValidResultCombinations(
				"h'(5.8871)",
				"276 * " + EULER_STRING + "^(-1354033 / 500000) / (1125 * (" + EULER_STRING
						+ "^(-1354033 / 500000))^(2) + 150 * " + EULER_STRING
						+ "^(-1354033 / 500000) + 5)",
				"276 * " + EULER_STRING + "^(-1354033 / 500000) / "
						+ "(150 * " + EULER_STRING + "^(-1354033 / 500000) + "
						+ "1125 * (" + EULER_STRING + "^(-1354033 / 500000))^(2)"
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
				"-21000000 * " + EULER_STRING + "^(-9 / 50 * t) + 21000000");
		t("b=(f(8)-f(7))/f(7)", "(1 / nroot(" + EULER_STRING + "^(36),25) - 1 / nroot("
				+ EULER_STRING + "^(63),50)) / (1 / nroot(" + EULER_STRING + "^(63),50) - 1)");
		t("Solve(f(t)=20*10^6)", "{t = 50 / 9 * ln(21)}");
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
		t("D=Element(c,1)",
				"((r^(2) - s^(2) + 1) / 2, sqrt(-r^(4) + 2 * r^(2) * s^(2) + 2 * r^(2) - s^(4) + 2 * s^(2) - 1) / 2)");
		t("E=Element(c,2)",
				"((r^(2) - s^(2) + 1) / 2, (-sqrt(-r^(4) + 2 * r^(2) * s^(2) + 2 * r^(2) - s^(4) + 2 * s^(2) - 1)) / 2)");
		t("Line(D,E)", "x = 1 / 2 * r^(2) - 1 / 2 * s^(2) + 1 / 2");
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
				"-1 / (x^(2) + x * y)", "-1 / (x * y + x^(2))");
		t("(x+y)(x-y)(x-y)", "(x + y) * (x - y)^(2)");
		t("Expand((x+y)(x-y)(x-y))", "x^(3) - x^(2) * y - x * y^(2) + y^(3)");
		t("Factor(x^2+2x+1)", "(x + 1)^(2)");
		t("Factor(x^3-x^2-8x+12)", "(x - 2)^(2) * (x + 3)");
		t("Factor((x^2+2x-15)/(x^3+3x^2-4))", "(x - 3) * (x + 5) / ((x - 1) * (x + 2)^(2))");
		t("Substitute(x^2-2x+23,x,y^2)", "y^(4) - 2 * y^(2) + 23");
		t("Solve(3(x-2)=5x+14)", "{x = -10}");
		t("Solve(2x^2-x=15)", "{x = -5 / 2, x = 3}");
		t("Solve(2x^2-x=21)", "{x = -3, x = 7 / 2}");
		t("Solve(6x/(x+3)-x/(x-3)=2)", "{x = 1, x = 6}");
		t("Solve(12exp(x)=150)", "{x = ln(25 / 2)}");
		testValidResultCombinations("Solve(cos(x)=sin(x))",
				"{x = k_{1} * " + pi + " + 1 / 4 * " + pi + "}",
				"{x = 2 * k_{1} * " + pi + " - 3 / 4 * π, x = 2 * k_{2} * " + pi + " + 1 / 4 * π}");
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
				"{{k = 18659 / 2142, p = 437 / 12852, q = -535 / 2856, r = -311 / 306, s = 63197 / 25704}}");
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
		t("Numeric(f(root))", Matchers.in(new String[]{"9.091256074573", "9.091256074574"}));
		t("Solve(f'(x)=tan(30deg))", "{x = 0.9446513611798, x = 5.126711116935}");
		t("Tangent(2,f)", "y = -15 * sqrt(2) / 4 * x + 33 * sqrt(2) / 2");
	}

	/**
	 * https://www.geogebra.org/m/mxtyvd22#material/jueqqgec
	 */
	@Test
	public void testTutorial5() {
		t("f(x)=1/25 x^4", "1 / 25 * x^(4)");
		testValidResultCombinations("g=Invert(f)", "nroot(25 * x,4)", "nroot(25,4) * nroot(x,4)");
		t("a=pi Integral(g^2,0,h)", "10 / 3 * sqrt(h) * h * " + pi);
		t("b=Solve(a=500)", "{h = 5 * cbrt(180 * " + pi + ") / " + pi + "}");
		t("Numeric(b)", "{h = 13.16116268824}");
	}

	@Test
	public void testLists() {
		t("f(x)=x^2", "x^(2)");
		t("l1={1,2,3}", "{1, 2, 3}");
		t("f(l1)", "{1, 4, 9}");
	}

	@Test
	public void testMoreLists() {
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
				"{-3 / 4 * " + pi + ", 1 / 4 * " + pi + "}");
		t("Solutions(x^2=1)", "{-1, 1}");
		t("Solutions({x+y=1, x-y=3})", "{{2, -1}}");
		t("Solutions({aa+bb=1, aa-bb=3})", "{{2, -1}}");
		t("Solutions(x^2=aaa)", "{-sqrt(aaa), sqrt(aaa)}");
		t("Solutions(y^2=aaa)", "{-sqrt(aaa), sqrt(aaa)}");
	}

	@Test
	public void testSolutionsCommandCustomVar() {
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
		t("Integral(x*y^2,x,aaa,bbb)", "y^(2) * (-1 / 2 * aaa^(2) + 1 / 2 * bbb^(2))");
		t("Integral(Integral(x*y^2,x,0,2),y,0,1)", "2 / 3");
		t("Integral(Integral(x*y^2,x,0,2),y,0,q)", "2 / 3 * q^(3)");
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
		t("Polynomial((x!)/(x-2)!)", "x^(2) - x");
		t("Polynomial((x!)/(x-2)!, x)", "x^(2) - x");
		t("Polynomial((y!)/(y-2)!, y)", "y^(2) - y");
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
		t("Tangent((d, d^2 c), y = c x^2)",
				"y = -c * d^(2) + 2 * c * d * x");
		t("Tangent((1,c), y = c x^2)", "y = 2 * c * x - c");
	}

	@Test
	public void testPartialFractionsCommand() {
		t("PartialFractions(x/(x+1))", "1 - 1 / (x + 1)");
		t("PartialFractions(aaa * x/(x+1))", "aaa - aaa / (x + 1)");
	}

	@Test
	public void testSimplifyCommand() {
		t("f = Simplify(aaa+bbb+aaa+x+y+x+y)", "2 * aaa + bbb + 2 * x + 2 * y");
		t("Simplify(x+x)", "2 * x");
	}

	@Test
	public void testTrigExpand() {
		t("TrigExpand(tan(aaa+bbb))",
				"(sin(aaa) / cos(aaa) + sin(bbb) / cos(bbb)) / (1 - sin(aaa) / cos(aaa) * sin(bbb) / cos(bbb))");
		t("TrigExpand(x)", "x");
		t("TrigExpand(sin(x)sin(x/3))",
				"1 / 2 * cos(2 * x / 3) - 1 / 2 * cos(4 * x / 3)");
		t("r1 = TrigExpand(3sin(x) sin(x / 3) / x²)",
				"3 / (2 * x^(2)) * cos(2 * x / 3) - 3 / (2 * x^(2)) * cos(4 * x / 3)");
	}

	@Test
	public void testTrigCombine() {
		t("f(x) = TrigCombine(sin(aaa)*cos(aaa))", "1 / 2 * sin(2 * aaa)");
	}

	@Test
	public void testTrigSimplify() {
		t("TrigSimplify(1-sin(x)^2)", "(cos(x))^(2)");
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
	}

	@Test
	public void testMatrixMultiplication() {
		t("{{aa,bb},{cc,dd}} {{pp,ff},{gg,hh}}",
				"{{aa * pp + bb * gg, aa * ff + bb * hh}, {cc * pp + dd * gg, cc * ff + dd * hh}}");
	}

	@Test
	public void testIntersectCommand() {
		t("Intersect(x^2+y^2=5, x+y=sqrt(2))",
				"{((-sqrt(2)) / 2, 3 * sqrt(2) / 2), (3 * sqrt(2) / 2, (-sqrt(2)) / 2)}");
		t("Intersect(x+y=sqrt(2), y-x=pi)",
				"{(-1 / 2 * " + pi + " + sqrt(2) / 2, 1 / 2 * " + pi + " + sqrt(2) / 2)}");
		t("Intersect((x+8)^2+(y-4)^2=13,(x+4)^2+(y-4)^2=2)",
				"{(-37 / 8, (sqrt(103) + 32) / 8), (-37 / 8, (-sqrt(103) + 32) / 8)}");
		// t("Intersect((x+1)^2+(y+1)^2=9-4sqrt(2), y^2+(x-2)^2=10)", "");
	}

	@Test
	public void testAngleCommandFiltered() {
		GeoSymbolic symbolic = add("Angle((1,2),(3,4))");
		assertThat(symbolic, is(nullValue()));
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
				"{{a = -3 / 2, b = 10, c = -33 / 2, d = 9}}");
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
	public void testFunctionVariableLabelInCommandsMultiVariableFunction() {
		GeoSymbolic geo = createGeoWithHiddenLabel("Integral(x³+3x y, x)");
		showLabel(geo);
		assertTrue(geo.getAlgebraDescriptionDefault().startsWith("a(x, y)"));
	}

	@Test
	public void testFunctionsWithApostrophe() {
		testOutputLabelOfFunctionsWithApostrophe("Integral(x)", "x");
		testOutputLabelOfFunctionsWithApostrophe("TaylorPolynomial(x^2, 3, 1)", "6");
	}

	@Test
	public void testFunctionVariableLabelInCommandsFunctions() {
		GeoSymbolic derivative1 = createGeoWithHiddenLabel("Derivative(x^2)");
		showLabel(derivative1);
		GeoSymbolic var = createGeoWithHiddenLabel("f(2)");
		assertEquals("4", var.getAlgebraDescriptionDefault());
		clean();

		GeoSymbolic geo = createGeoWithHiddenLabel("Derivate(x)");
		showLabel(geo);
		assertTrue(geo.getAlgebraDescriptionDefault().startsWith("a(x)"));
		clean();

		testOutputLabel("f(x) = Derivative(x^3 + x^2 + x)", "f(x)");
		testOutputLabel("Integral(x^3)", "f(x)");
		testOutputLabel("f(x) = TrigSimplify(1 - sin(x)^2)", "f(x)");
		testOutputLabel("f(x) = TrigCombine(x)", "f(x)");
		testOutputLabel("f(x) = TrigExpand(x)", "f(x)");
		testOutputLabel("f(x) = TaylorPolynomial(x,x-5,1)", "f(x)");
		testOutputLabel("f(x) = Simplify(x + x + x)", "f(x)");
		testOutputLabel("f(x) = PartialFractions(x^2 / (x^2 - 2x + 1))", "f(x)");
		testOutputLabel("f(x) = Factor(x^2 + x - 6)", "f(x)");
	}

	@Test
	public void testDerivativeLabelHasFunctionVar() {
		add("b(x) = x");
		GeoSymbolic geo = createGeoWithHiddenLabel("Derivative(b)");
		showLabel(geo);
		assertTrue(geo.getAlgebraDescriptionDefault().startsWith("f(x)"));
	}

	private void testOutputLabel(String input, String outputStartsWith) {
		GeoSymbolic geo = createGeoWithHiddenLabel(input);
		assertTrue(geo.getTwinGeo() instanceof GeoFunction);
		showLabel(geo);
		assertTrue(geo.getAlgebraDescriptionDefault().startsWith(outputStartsWith));
		clean();
	}

	private void testOutputLabelOfFunctionsWithApostrophe(String input,
			String outputStartsWith) {
		GeoSymbolic firstGeo = createGeoWithHiddenLabel(input);
		assertTrue(firstGeo.getTwinGeo() instanceof GeoFunction);
		showLabel(firstGeo);
		GeoSymbolic secondGeo = createGeoWithHiddenLabel("f'");
		assertTrue(secondGeo.getAlgebraDescriptionDefault().startsWith(outputStartsWith));
		clean();
	}

	private GeoSymbolic createGeoWithHiddenLabel(String text) {
		GeoSymbolic geoElement = add(text);
		new LabelController().hideLabel(geoElement);
		return geoElement;
	}

	private void showLabel(GeoSymbolic geoSymbolic) {
		new LabelController().showLabel(geoSymbolic);
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
		ap.changeGeoElement(a, "a = p-q", true, false, TestErrorHandler.INSTANCE,
				null);
		checkInput("a", "a = p - q");
	}

	@Test
	public void constantShouldBeOneRow() {
		t("1", "1");
		GeoElement a = app.getKernel().lookupLabel("a");
		assertEquals(DescriptionMode.VALUE, a.getDescriptionMode());
	}

	@Test
	public void labeledConstantShouldBeOneRow() {
		t("a=7", "7");
		GeoElement a = app.getKernel().lookupLabel("a");
		assertEquals(DescriptionMode.VALUE, a.getDescriptionMode());
	}

	@Test
	public void simpleEquationShouldBeOneRow() {
		t("eq1:x+y=1", "x + y = 1");
		GeoElement a = getSymbolic("eq1");
		assertEquals(DescriptionMode.VALUE, a.getDescriptionMode());
	}

	@Test
	public void simpleFracShouldBeTwoRows() {
		t("1/2", "1 / 2");
		GeoElement a = getSymbolic("a");
		assertEquals(DescriptionMode.DEFINITION_VALUE, a.getDescriptionMode());
	}

	@Test
	public void linePropertiesShouldMatchTwin() {
		t("f: x = y", "x = y");

		GeoSymbolic f = getSymbolic("f");
		f.setLineType(EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT);
		f.setLineThickness(8);
		f.updateRepaint();

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
		pointA.setPointSize(8);
		pointA.setPointStyle(4);
		pointA.updateRepaint();

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

	private GeoSymbolic getSymbolic(String label) {
		GeoElement geo = app.getKernel().lookupLabel(label);
		assertThat(geo, CoreMatchers.instanceOf(GeoSymbolic.class));
		return (GeoSymbolic) geo;
	}

	@Test
	public void powerShouldBeOneRow() {
		t("(b+1)^3", "(b + 1)^(3)");
		GeoElement a = getSymbolic("a");
		assertEquals(DescriptionMode.VALUE, a.getDescriptionMode());
	}

	/**
	 * APPS-1013
	 */
	@Test
	public void updateInSameRowShouldChangeTheTwin() {
		t("f(x)=x^2", "x^(2)");
		t("f(x)=x^3", infoWithRedefine("f"), "x^(3)");
		checkInput("f", TestStringUtil.unicode("f(x) = x^3"));
	}

	@Test
	public void functionAssignmentInSecondRowShouldBeEquation() {
		t("f(x)=x^2", "x^(2)");
		t("f(x)=x^3", infoWithRedefine(null), "x^(2) = x^(3)");
		reload();
		t("f", "x^(2)");
		t("eq1", "x^(2) = x^(3)");
	}

	@Test
	public void equationWithFunction() {
		t("f(x,a,b)=-a ln(b*x)", "-a * ln(b * x)");
		t("eq1:a/(-1)=1", "-a = 1");
		t("f(1, a,b)=1", "-a * ln(b) = 1"); // autolabeling here
		t("Solve({eq1,eq2},{a,b})",
				"{{a = -1, b = " + Unicode.EULER_STRING + "}}");
	}

	private EvalInfo infoWithRedefine(String object) {
		return new EvalInfo(true).withLabelRedefinitionAllowedFor(object)
				.withSymbolicMode(SymbolicMode.SYMBOLIC_AV);
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
		assertEquals("p+\" is a prime\"",
				lastGeoElement.getDefinitionForEditor());
		assertThat(lastGeoElement, instanceOf(GeoText.class));
	}

	@Test
	public void testSimplificationShowsBothRows1() {
		GeoSymbolic symbolic = add("x + x");
		assertThat(symbolic.getDescriptionMode(), is(DescriptionMode.DEFINITION_VALUE));
	}

	@Test
	public void testSimplificationShowsBothRows2() {
		GeoSymbolic symbolic = add("(x + 1) * (x - 1)");
		assertThat(symbolic.getDescriptionMode(), is(DescriptionMode.DEFINITION_VALUE));
	}

	private void shouldFail(String string, String errorMsg) {
		AlgebraTestHelper.shouldFail(string, errorMsg, app);
	}

	private String getObjectLHS(String label) {
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
		Object[] list = app.getKernel().getConstruction().getGeoSetConstructionOrder().toArray();
		((GeoElement) list[list.length - 1]).remove();
		Assert.assertNotNull(SuggestionRootExtremum.get(line));
	}

	@Test
	public void handlePreviewPointsTest() {
		add("f:x^2 - 2");
		add("g:x^3 - 1");
		add("h:x");
		updateSpecialPoints("f");
		Assert.assertEquals(8, numberOfSpecialPoints());
		updateSpecialPoints("g");
		Assert.assertEquals(6, numberOfSpecialPoints());
		updateSpecialPoints("h");
		Assert.assertEquals(6, numberOfSpecialPoints());
	}

	@Test
	public void testLargeNumbersAreParsedCorrectly() {
		add("a=11111111111111111^2");
		String result = getSymbolic("a").toValueString(StringTemplate.giacTemplate);
		Assert.assertEquals("123456790123456787654320987654321", result);
	}

	@Test
	public void testMultivariateFunction() {
		add("f(x, a) = sqrt(x - a)");
		String xml = app.getXML();
		Assert.assertTrue(xml.contains("x,a"));
		app.setXML(xml, true);
		GeoSymbolic symbolic = getSymbolic("f");
		Assert.assertEquals("f(x, a) = sqrt(-a + x)",
				symbolic.toString(StringTemplate.defaultTemplate));
	}

	@Test
	public void testUndoRedoWorksWhenLabelIsHidden() {
		LabelController labelController = new LabelController();
		GeoElement element = add("x");
		labelController.hideLabel(element);
		app.setXML(app.getXML(), true);
	}

	@Test
	public void testSliderCommandCreatesSlider() {
		GeoNumeric element = add("Slider(1, 10)");
		Assert.assertTrue(element.isShowingExtendedAV());
		Assert.assertTrue(DoubleUtil.isEqual(element.getIntervalMin(), 1));
		Assert.assertTrue(DoubleUtil.isEqual(element.getIntervalMax(), 10));
	}

	@Test
	public void testUndoRedoKeepsShowingIntegralArea() {
		GeoSymbolic integralArea = add("a(x)=Integral(xx,2,3)");
		Assert.assertTrue(integralArea.isEuclidianVisible());
		Assert.assertTrue(integralArea.getTwinGeo().isEuclidianVisible());

		app.setXML(app.getXML(), true);
		integralArea = (GeoSymbolic) app.getKernel().lookupLabel("a");

		Assert.assertTrue(integralArea.isEuclidianVisible());
		Assert.assertTrue(integralArea.getTwinGeo().isEuclidianVisible());
	}

	private int numberOfSpecialPoints() {
		if (app.getSpecialPointsManager().getSelectedPreviewPoints() == null) {
			return 0;
		}
		return app.getSpecialPointsManager().getSelectedPreviewPoints().size();
	}

	private void updateSpecialPoints(String string) {
		app.getSpecialPointsManager()
				.updateSpecialPoints(app.getKernel().lookupLabel(string));
	}

	/**
	 * Emulate file reload
	 */
	private void reload() {
		app.setXML(app.getXML(), true);
	}

	@Test
	public void testCreationWithLabel() {
		GeoSymbolic vector = add("v=(1,1)");
		assertThat(vector.getTwinGeo(), CoreMatchers.<GeoElementND>instanceOf(GeoVector.class));
	}

	@Test
	public void testShorthandIfAccepted() {
		kernel.setUndoActive(true);
		kernel.initUndoInfo();
		add("f(x)=x^2,x<5");
		kernel.storeUndoInfo();
		undoRedo();
		GeoElement element = lookup("f");
		assertThat(element.toString(StringTemplate.defaultTemplate),
				equalTo("f(x) = If(5 > x,x²)"));
	}

	@Test
	public void testIfArgumentFiltered() {
		GeoSymbolic element = add("If(x>5, x^2, x<5, x)");
		assertThat(element.getTwinGeo(), is(nullValue()));
	}

	@Test
	public void testRedefinitionKeepsConstant() {
		add("f(x) = Integral(x)");
		// redefine geo
		add("f(x) = Integral(x)");
		GeoElement element = lookup("c_2");
		assertThat(element, is(nullValue()));
	}

	@Test
	public void testRadians() {
		GeoSymbolic angle = add("1rad");
		assertThat(
				angle.getDefinition(StringTemplate.defaultTemplate),
				equalTo("1 rad"));
		assertThat(
				angle.getValueForInputBar(),
				equalTo("1 rad"));
		assertThat(
				angle.getTwinGeo().toValueString(StringTemplate.defaultTemplate),
				equalTo("1 rad"));
	}

	@Test
	public void testSolveEuclidianHidden() {
		add("eq1: x + y = 2");
		add("eq2: x - y = 3");
		GeoSymbolic element = add("Solve({eq1, eq2}, {x, y})");
		assertThat(element.showInEuclidianView(), is(false));
	}

	@Test
	public void testNumbersOutput() {
		GeoSymbolic degree = add("45" + Unicode.DEGREE_STRING);
		assertThat(degree.toValueString(StringTemplate.defaultTemplate), is("1 / 4 " + pi));

		GeoSymbolic realNumber = add("2.222222222222222222222");
		assertThat(realNumber.toValueString(StringTemplate.defaultTemplate),
				is("1111111111111111111111 / 500000000000000000000"));

		GeoSymbolic smallNumber = add("2E-20");
		assertThat(smallNumber.toValueString(StringTemplate.defaultTemplate),
				is("1 / 50000000000000000000"));

		GeoSymbolic bigNumber = add("1.2345678934534545345345E20");
		assertThat(bigNumber.toValueString(StringTemplate.defaultTemplate),
				is("2469135786906909069069 / 20"));
	}

	@Test
	public void testFunctionLikeMultiplication() {
		GeoSymbolic element = add("x(x + 1)");
		assertThat(element.toValueString(StringTemplate.defaultTemplate), is("x\u00B2 + x"));
	}

	@Test
	public void testFunctionLikeMultiplicationSolve() {
		assertSameAnswer("Solve(x(x-5)>x+7)", "Solve(x (x-5)>x+7)");
		assertSameAnswer("Solve(y(y+1),y)", "Solve(y (y+1),y)");
		assertSameAnswer("Solve(z(z+1),z)", "Solve(z (z+1),z)");
	}

	@Test
	public void testRemoveUndefinedCommand() {
		t("l1=Sequence(Sequence(If(ii>j,ii),ii,1,j+1),j,1,5)",
				"{{?, 2}, {?, ?, 3}, {?, ?, ?, 4}, {?, ?, ?, ?, 5}, {?, ?, ?, ?, ?, 6}}");
		t("RemoveUndefined(Sequence(If(IsInteger(a^2/2),a^2,?),a,1,10))", "{4, 16, 36, 64, 100}");
		t("Sequence(RemoveUndefined(Element(l1,ii)),ii,1,Length(l1))", "{{2}, {3}, {4}, {5}, {6}}");
		t("RemoveUndefined({1,2,3,4,4})", "{1, 2, 3, 4, 4}");
		t("RemoveUndefined({})", "{}");
		t("RemoveUndefined({123456789123456789,?})", "{123456789123456789}");
		t("RemoveUndefined({?,1,2,?,3,4,4,?,?})", "{1, 2, 3, 4, 4}");
		t("RemoveUndefined(1)", "?");
	}

	@Test
	public void tetIsIntegerCommand() {
		t("IsInteger(1)", "true");
		t("IsInteger(44/2)", "true");
		t("IsInteger(44/3)", "false");
		t("IsInteger(1.5)", "false");
		t("IsInteger(pi)", "false");
		t("IsInteger(123456789123456789.1)", "false");
	}

	private void assertSameAnswer(String input1, String input2) {
		GeoSymbolic solve1 = add(input1);
		GeoSymbolic solve2 = add(input2);
		assertThat(solve1.toValueString(StringTemplate.defaultTemplate),
				is(solve2.toValueString(StringTemplate.defaultTemplate)));
	}

	@Test
	public void testSubstituteConstant() {
		add("f(x)=IntegralSymbolic(x)");
		add("a=5");
		GeoSymbolic symbolic = add("g(x)=Substitute(f(x), c_{1}, a)");
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate), is("1 / 2 x² + 5"));
		assertThat(symbolic.getTwinGeo(), is(notNullValue()));
	}

	@Test
	public void testIntegralTwinGeoHasSliderValue() {
		GeoSymbolic symbolic = add("Integral(x)");
		GeoNumeric slider = (GeoNumeric) lookup("c_1");
		slider.setValue(10);
		assertThat(symbolic.getTwinGeo().toString(StringTemplate.defaultTemplate),
				equalTo("1 / 2 x² + 10"));
	}

	@Test
	public void testPlotSolveIsEuclidianVisible() {
		GeoSymbolic symbolic = add("PlotSolve(x^2-2)");
		assertThat(symbolic.isEuclidianVisible(), is(true));
	}

	@Test
	public void testSymbolicDiffersForSolve() {
		GeoSymbolic solveX_1 = add("Solve(2x=5)");
		GeoSymbolic solveX_2 = add("Solve(2x=6)");

		GeoSymbolic solveA_1 = add("Solve(a*a=5)");
		GeoSymbolic solveA_2 = add("Solve(a*a=4)");

		assertThat(AlgebraItem.isSymbolicDiffers(solveX_1), is(true));
		assertThat(AlgebraItem.isSymbolicDiffers(solveX_2), is(false));
		assertThat(AlgebraItem.isSymbolicDiffers(solveA_1), is(true));
		assertThat(AlgebraItem.isSymbolicDiffers(solveA_2), is(false));
	}

	@Test
	public void testToggleSymbolicNumeric() {
		GeoSymbolic solveX = add("Solve(2x=5)");
		GeoSymbolic solveA = add("NSolve(a*a=5)");

		AlgebraItem.toggleSymbolic(solveX);
		AlgebraItem.toggleSymbolic(solveA);

		assertThat(Commands.NSolve.getCommand(),
				is(solveX.getDefinition().getTopLevelCommand().getName()));

		assertThat(Commands.Solve.getCommand(),
				is(solveA.getDefinition().getTopLevelCommand().getName()));
	}

	@Test
	public void testChangingSliderValue() {
		add("Integral(x)");
		lookup("c_1");
		GeoElement element = add("c_1=10");
		assertThat(element, is(CoreMatchers.<GeoElement>instanceOf(GeoNumeric.class)));
		GeoNumeric numeric = (GeoNumeric) element;
		assertThat(numeric.getValue(), is(closeTo(10, 0.001)));
	}

	@Test
	public void testFunctionRedefinition() {
		add("f(x) = x");
		GeoSymbolic function = add("f(x) = xx");
		assertThat(function.getTwinGeo(), CoreMatchers.<GeoElementND>instanceOf(GeoFunction.class));
	}

	@Test
	public void testPrecision() {
		GeoSymbolic derivative = add("Derivative(25.8-0.2ℯ^(-0.025x))");
		assertThat(
				derivative.toValueString(StringTemplate.defaultTemplate),
				equalTo("1 / 200 ℯ^(-1 / 40 x)"));
	}

	@Test
	public void testMin() {
		t("Min({-2, 12, -23, 17, 15})", "-23");
		t("Min(2 < x < 3)", "2");
		t("Min(12, 15)", "12");
		t("Min(ℯ^x*x^3,-4,-2)", "(-3, -27 / ℯ^(3))");
		t("Min({1, 2, 3, 4, 5}, {0, 3, 4, 2, 3})", "2");
		t("Min(1, 2)", "1");
		t("Min(2, 1, 3)", "1");
		t("Min(1, 2, -1, 4)", "-1");
		t("Min(1/2 < x < " + pi + ")", "1 / 2");
	}

	@Test
	public void testMax() {
		t("Max({-2, 12, -23, 17, 15})", "17");
		t("Max(2 < x < 3)", "3");
		t("Max(12, 15)", "15");
		t("Max(exp(x)x^2,-3,-1)", "(-2, 4 / ℯ^(2))");
		t("Max({1, 2, 3, 4, 5}, {5, 3, 4, 2, 0})", "4");
		t("Max(1, 2)", "2");
		t("Max(2, 3, 1)", "3");
		t("Max(1, 2, 4, -2)", "4");
		t("Max(1/2 < x < " + pi + ")", pi + "");
	}

	@Test
	public void testSolveNotReturnUndefined() {
		add("eq1: (x^2)(e^x)= 5");
		GeoSymbolic function = add("Solve(eq1, x)");
		assertNotEquals(function.getValue().toString(StringTemplate.defaultTemplate), "{?}");
		assertThat(function.getValue().toString(StringTemplate.defaultTemplate),
				equalTo("{x = 1.2168714889}"));
	}

	@Test
	public void testSolveChangedToNSolve() {
		add("eq1: (x^2)(e^x)= 5");
		GeoSymbolic function = add("Solve(eq1, x)");
		assertThat(function.getDefinition(StringTemplate.defaultTemplate),
				equalTo("NSolve(eq1,x)"));
	}

	@Test
	public void testQuartiles() {
		add("l1 = {-2, 12, -23, 17, 15}");
		add("l2 = {1,2,3,4}");
		add("l3 = {1,4,2,7,5,3}");
		add("l4 = {-6,4,6}");
		add("l5 = {1,4,2,6,4}");
		add("l6 = {2,4,4,7}");

		t("Quartile1(l1)", "-12.5");
		t("Quartile3(l1)", "16");
		t("Quartile1(l2)", "1.5");
		t("Quartile3(l2)", "3.5");
		t("Quartile1(l3)", "2");
		t("Quartile3(l3)", "5");
		t("Quartile1(l4)", "-6");
		t("Quartile3(l4)", "6");
		t("Quartile1(l5)", "1.5");
		t("Quartile3(l5)", "5");
		t("Quartile1(l6)", "3");
		t("Quartile3(l6)", "5.5");

		t("Quartile1({6,4,6})", "4");
		t("Quartile3({6,4,6})", "6");
		t("Quartile1({1,2})", "1");
		t("Quartile3({1,2})", "2");
		t("Quartile1({1,1})", "1");
		t("Quartile3({1,1})", "1");
		t("Quartile1({6,-2,12,7,8,4,9})", "4");
		t("Quartile3({6,-2,12,7,8,4,9})", "9");
		t("Quartile1({1})", "?");
		t("Quartile3({1})", "?");
		t("Quartile1({})", "?");
		t("Quartile3({})", "?");
		t("Quartile1({1,2,5,4,7})", "1.5");
		t("Quartile3({1,2,5,4,7})", "6");
		t("Quartile1({2,2,3})", "2");
		t("Quartile3({2,2,3})", "3");
		t("Quartile1({2,3,3})", "2");
		t("Quartile3({2,3,3})", "3");
	}

	@Test
	public void testInnerNestedCommands() {
		add("f(x)=x^2");
		add("a(x)=Solve(Derivative(f))");
		add("1+1");
		undoRedo();
		int n = kernel.getConstruction().steps();
		assertThat(n, equalTo(3));
	}

	@Test
	public void testSinNumericInRadians() {
		GeoSymbolic sin = add("sin⁻¹(0.4)");
		assertThat(
				sin.getDefinition(StringTemplate.defaultTemplate),
				equalTo("sin⁻¹(0.4)"));
		assertThat(
				sin.getValueForInputBar(),
				equalTo("sin⁻¹(2 / 5)"));
		assertThat(
				sin.getTwinGeo().toValueString(StringTemplate.defaultTemplate),
				equalTo("0.4115168461"));
	}

	@Test
	public void testAsinNumericInRadians() {
		GeoSymbolic asind = add("asin(0.4)");
		assertThat(
				asind.getDefinition(StringTemplate.defaultTemplate),
				equalTo("sin⁻¹(0.4)"));
		assertThat(
				asind.getValueForInputBar(),
				equalTo("sin⁻¹(2 / 5)"));
		assertThat(
				asind.getTwinGeo().toValueString(StringTemplate.defaultTemplate),
				equalTo("0.4115168461"));
	}

	@Test
	public void testAsindNumericInDegrees() {
		GeoSymbolic asind = add("asind(0.4)");
		assertThat(
				asind.getDefinition(StringTemplate.defaultTemplate),
				equalTo("asind(0.4)"));
		assertThat(
				asind.getValueForInputBar(),
				equalTo("180° sin⁻¹(2 / 5) / π"));

		asind.setSymbolicMode(false, false);
		assertThat(
				asind.getValueForInputBar(),
				equalTo("23.5781784782°"));
	}

	@Test
	public void testArcdFunctionsReturnDegrees() {
		GeoSymbolic asind = add("asind(1/5)");
		assertThat(
				asind.getLaTeXDescriptionRHS(true, StringTemplate.numericLatex),
				equalTo("180^{\\circ} \\cdot "
						+ "\\frac{\\operatorname{sin⁻¹} \\left( \\frac{1}{5} \\right)}{\\pi }"));
		asind.setSymbolicMode(false, false);
		assertThat(
				asind.getLaTeXDescriptionRHS(true, StringTemplate.numericLatex),
				equalTo("11.5369590328°"));

		GeoSymbolic acosd = add("acosd(1/5)");
		assertThat(
				acosd.getLaTeXDescriptionRHS(true, StringTemplate.numericLatex),
				equalTo("180^{\\circ} \\cdot "
						+ "\\frac{\\operatorname{cos⁻¹} \\left( \\frac{1}{5} \\right)}{\\pi }"));
		acosd.setSymbolicMode(false, false);
		assertThat(
				acosd.getLaTeXDescriptionRHS(true, StringTemplate.numericLatex),
				equalTo("78.4630409672°"));

		GeoSymbolic atand = add("atand(1/5)");
		assertThat(
				atand.getLaTeXDescriptionRHS(true, StringTemplate.numericLatex),
				equalTo("180^{\\circ} \\cdot "
						+ "\\frac{\\operatorname{tan⁻¹} \\left( \\frac{1}{5} \\right)}{\\pi }"));
		atand.setSymbolicMode(false, false);
		assertThat(
				atand.getLaTeXDescriptionRHS(true, StringTemplate.numericLatex),
				equalTo("11.309932474°"));
	}

	@Test
	public void testAssumeCommand() {
		t("Assume(a > 0, Integral(exp(-a x), 0, infinity))", "1 / a");
		t("Assume(n>0, Solve(log(n^2*(x/n)^lg(x))=log(x^2), x))",
				"{x = 100, x = n}");
		t("Assume(x<2,Simplify(sqrt(x-2sqrt(x-1))))", "-sqrt(x - 1) + 1");
		t("Assume(x>2,Simplify(sqrt(x-2sqrt(x-1))))", "sqrt(x - 1) - 1");
		t("Assume(k>0, Extremum(k*3*x^2/4-2*x/2))",
				"{(2 / (3 * k), -1 / (3 * k))}");
		t("Assume(k>0, InflectionPoint(0.25 k x^3 - 0.5x^2 + k))",
				"{(2 / (3 * k), (27 * k^(3) - 4) / (27 * k^(2)))}");
	}

	@Test
	public void testExtremum() {
		GeoSymbolic extremum = add("Extremum(x*ln(x^2))");
		GeoList twin = (GeoList) extremum.getTwinGeo();
		assertThat(twin.size(), equalTo(2));
	}

	@Test
	public void testVariableAfterUndo() {
		UndoRedoTester undoRedo = new UndoRedoTester(app);
		undoRedo.setupUndoRedo();

		GeoSymbolic a = add("a = 3");
		app.storeUndoInfo();
		assertThat(a.getDefinitionForInputBar(), is("a = 3"));
		add("b = 3");
		app.storeUndoInfo();
		a = undoRedo.getAfterUndo("a");
		assertThat(a.getDefinitionForInputBar(), is("a = 3"));
	}

	@Test
	public void testRounding() {
		kernel.setPrintFigures(20);
		GeoSymbolic number = add("11.3 * 1.5");
		AlgebraItem.toggleSymbolic(number);
		String output = AlgebraItem.getOutputTextForGeoElement(number);
		assertThat(output, equalTo("16.95"));
		// Reset
		kernel.setPrintDecimals(5);
	}
}
