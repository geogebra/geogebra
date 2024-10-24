package org.geogebra.common.kernel.geos;

import static com.himamis.retex.editor.share.util.Unicode.EULER_STRING;
import static com.himamis.retex.editor.share.util.Unicode.pi;
import static org.geogebra.common.BaseUnitTest.hasProperty;
import static org.geogebra.common.BaseUnitTest.hasValue;
import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionRootExtremum;
import org.geogebra.common.gui.view.algebra.scicalc.LabelHiderCallback;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.SymbolicUtil;
import org.geogebra.desktop.main.AppD;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.TestStringUtil;
import org.geogebra.test.UndoRedoTester;
import org.geogebra.test.annotation.Issue;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.geogebra.test.commands.ErrorAccumulator;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoSymbolicTest extends BaseSymbolicTest {

	private void testValidResultCombinations(String input, String... validResults) {
		AlgebraTestHelper.checkValidResultCombinations(
				input, validResults,
				ap, StringTemplate.testTemplate);
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
				"{{3593 / 1316, -4449 / 1316, 340 / 329}, {-1444 / 329, 1684 / 329, -492 / 329},"
						+ " {2277 / 1316, -2475 / 1316, 351 / 658}}");

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
		t("Substitute(x^2+y^2, {x=ccc, y=bbb})", "bbb^(2) + ccc^(2)");
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
				equalTo("{x = (k + sqrt(k^(2) - 16 * k)) / 2, "
						+ "x = (k - sqrt(k^(2) - 16 * k)) / 2, x = 0}")));

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
				"{(1 / 2 * r^(2) - 1 / 2 * s^(2) + 1 / 2, sqrt(-r^(4) + 2 * r^(2) * "
						+ "s^(2) + 2 * r^(2) - s^(4) + 2 * s^(2) - 1) / 2), "
						+ "(1 / 2 * r^(2) - 1 / 2 * s^(2) + 1 / 2, (-sqrt(-r^(4) + 2 * r^(2) * "
						+ "s^(2) + 2 * r^(2) - s^(4) + 2 * s^(2) - 1)) / 2)}");
		t("D=Element(c,1)",
				"((r^(2) - s^(2) + 1) / 2, sqrt(-r^(4) + 2 * r^(2) * s^(2) + 2 * r^(2) "
						+ "- s^(4) + 2 * s^(2) - 1) / 2)");
		t("E=Element(c,2)",
				"((r^(2) - s^(2) + 1) / 2, (-sqrt(-r^(4) + 2 * r^(2) * s^(2) + 2 * r^(2) "
						+ "- s^(4) + 2 * s^(2) - 1)) / 2)");
		t("Line(D,E)", "x = 1 / 2 * r^(2) - 1 / 2 * s^(2) + 1 / 2");
	}

	/**
	 * <a href="https://www.geogebra.org/m/mxtyvd22">Tutorial</a>
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
	 * <a href="https://www.geogebra.org/m/mxtyvd22#material/gjsw6npx">Tutorial 2</a>
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
	 * <a href="https://www.geogebra.org/m/mxtyvd22#material/vcdtdhjk">Tutorial 3</a>
	 */
	@Test
	public void testTutorial3() {
		t("f(x)=p x^4 + q x^3 + r x^2 + s x + k",
				"p * x^(4) + q * x^(3) + r * x^(2) + s * x + k");
		t("eq1:f(1)=10", "k + p + q + r + s = 10");
		t("eq2:f'(1)=0", "4 * p + 3 * q + 2 * r + s = 0");
		t("eq3:f(4)=-1", "k + 256 * p + 64 * q + 16 * r + 4 * s = -1");
		t("eq4:f''(4)=0", "192 * p + 24 * q + 2 * r = 0");
		t("eq5:f(-3)=0", "k + 81 * p - 27 * q + 9 * r - 3 * s = 0");
		t("u=Solve({eq1, eq2, eq3, eq4, eq5})",
				"{{k = 18659 / 2142, p = 437 / 12852, q = -535 / 2856, "
						+ "r = -311 / 306, s = 63197 / 25704}}");
		t("Substitute(f,u)",
				"437 / 12852 * x^(4) - 535 / 2856 * x^(3) - 311 / 306 * x^(2)"
						+ " + 63197 / 25704 * x + 18659 / 2142");
	}

	/**
	 * <a href="https://www.geogebra.org/m/mxtyvd22#material/ukkups2n">Tutorial 4</a>
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
		t("Solve(f''(x)=0)", "{x = (2 * sqrt(6) + 3) / 3}");
		t("list=Solutions(f''(x)=0)", "{(2 * sqrt(6) + 3) / 3}");
		t("root=Element(list,1)", "(2 * sqrt(6) + 3) / 3");
		t("Numeric(f(root))", Matchers.in(new String[]{"9.091256074573", "9.091256074574"}));
		t("Solve(f'(x)=tan(30deg))", Matchers.in(new String[]{
				"{x = 0.94465136117983, x = 5.126711116934559}",
				"{x = 0.9446513611798301, x = 5.12671111693456}",
				"{x = 0.9446513611798, x = 5.126711116935}"}));
		t("Tangent(2,f)", "y = -15 * sqrt(2) / 4 * x + 33 * sqrt(2) / 2");
	}

	/**
	 * <a href="https://www.geogebra.org/m/mxtyvd22#material/jueqqgec">Tutorial 5</a>
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
		t("Assume(-pi<x<pi, Solutions(sin(x)=cos(x)))",
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
		t("Sum(If(j^2<>j,1,0),j,1,5)", "4");
	}

	@Test
	public void sumShouldNotReplaceInput() {
		GeoSymbolic sum = add("Sum(If(Mod(k,2)==0,k,0),k,0,10)");
		assertEquals("a=Sum(If(Mod(k,2)" + Unicode.QUESTEQ + "0,k,0),k,0,10)",
				sum.getDefinitionForEditor());
	}

	@Test
	public void testProductCommand() {
		t("Product(((k+2)/(k)),k,1,25)", "351");
		t("Product(k^2,k,1,5)", "14400");
		t("Product(k^n,k,1,5)", "2^(n) * 3^(n) * 4^(n) * 5^(n)");
		t("f(x)=Product(sin((π*x)/(n)),n,2,floor(sqrt(x)))",
				"gGbPrOdUcT(sin(π * x / n), n,2, floor(sqrt(x)))");
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
				"(sin(aaa) / cos(aaa) + sin(bbb) / cos(bbb)) "
						+ "/ (1 - sin(aaa) / cos(aaa) * sin(bbb) / cos(bbb))");
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
				"{{d / (a * d - b * c), (-b) / (a * d - b * c)}, "
						+ "{(-c) / (a * d - b * c), a / (a * d - b * c)}}");
		t("{{a,b},{c,d}}^-1",
				"{{d / (a * d - b * c), (-b) / (a * d - b * c)}, "
						+ "{(-c) / (a * d - b * c), a / (a * d - b * c)}}");
		t("Transpose({{a,b},{c,d}})", "{{a, c}, {b, d}}");
		t("EigenValues({{a,b},{c,d}})",
				"{(a + d - sqrt(a^(2) - 2 * a * d + d^(2) + 4 * b * c)) / 2, "
						+ "(a + d + sqrt(a^(2) - 2 * a * d + d^(2) + 4 * b * c)) / 2}");
		t("EigenVectors({{a,b},{c,d}})",
				"{{a - d - sqrt(a^(2) - 2 * a * d + d^(2) + 4 * b * c), "
						+ "a - d + sqrt(a^(2) - 2 * a * d + d^(2) + 4 * b * c)}, {2 * c, 2 * c}}");
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
		assertThat(geo.getAlgebraDescriptionDefault(), startsWith("a(x, y)"));
	}

	@Test
	public void testShouldComputeNumericValue() {
		GeoSymbolic geo = add("f(x)=x");
		assertThat(SymbolicUtil.shouldComputeNumericValue(geo.getValue()), is(false));
		geo = add("f(x)=a*x");
		assertThat(SymbolicUtil.shouldComputeNumericValue(geo.getValue()), is(false));
		add("b=10");
		geo = add("g(x)=b*x");
		assertThat(SymbolicUtil.shouldComputeNumericValue(geo.getValue()), is(false));
		geo = add("g(2)");
		assertThat(SymbolicUtil.shouldComputeNumericValue(geo.getValue()), is(true));
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
		assertThat(geo.getAlgebraDescriptionDefault(), startsWith("a(x)"));
		clean();

		assertLabelStartsWithFx("f(x) = Derivative(x^3 + x^2 + x)");
		assertLabelStartsWithFx("Integral(x^3)");
		assertLabelStartsWithFx("f(x) = TrigSimplify(1 - sin(x)^2)");
		assertLabelStartsWithFx("f(x) = TrigCombine(x)");
		assertLabelStartsWithFx("f(x) = TrigExpand(x)");
		assertLabelStartsWithFx("f(x) = TaylorPolynomial(x,x-5,1)");
		assertLabelStartsWithFx("f(x) = Simplify(x + x + x)");
		assertLabelStartsWithFx("f(x) = PartialFractions(x^2 / (x^2 - 2x + 1))");
		assertLabelStartsWithFx("f(x) = Factor(x^2 + x - 6)");
	}

	@Test
	public void testDerivativeLabelHasFunctionVar() {
		add("b(x) = x");
		GeoSymbolic geo = createGeoWithHiddenLabel("Derivative(b)");
		showLabel(geo);
		assertThat(geo.getAlgebraDescriptionDefault(), startsWith("f(x)"));
	}

	@Test
	public void testNoFunctionVariableLabelInCommandWithNoFunctionOutput() {
		GeoSymbolic function = createGeoWithHiddenLabel("x*x");
		showLabel(function);
		GeoSymbolic extremum = add("A = Extremum(f)");
		assertThat(extremum.getAlgebraDescriptionDefault(), startsWith("A ="));
		clean();

		createGeoWithHiddenLabel("g(x)=x*ℯ^(-x)");
		GeoSymbolic inflectionPoint = add("A = InflectionPoint(g)");
		GeoSymbolic element = add("B = Element(A,1)");
		assertThat(inflectionPoint.getAlgebraDescriptionDefault(), startsWith("A ="));
		assertThat(element.getAlgebraDescriptionDefault(), startsWith("B ="));
	}

	private void assertLabelStartsWithFx(String input) {
		GeoSymbolic geo = createGeoWithHiddenLabel(input);
		assertThat(geo.getTwinGeo(), instanceOf(GeoFunction.class));
		showLabel(geo);
		assertThat(geo.getAlgebraDescriptionDefault(), startsWith("f(x)"));
		clean();
	}

	private void testOutputLabelOfFunctionsWithApostrophe(String input,
			String outputStartsWith) {
		GeoSymbolic firstGeo = createGeoWithHiddenLabel(input);
		assertThat(firstGeo.getTwinGeo(), instanceOf(GeoFunction.class));
		showLabel(firstGeo);
		GeoSymbolic secondGeo = createGeoWithHiddenLabel("f'");
		assertThat(secondGeo.getAlgebraDescriptionDefault(), startsWith(outputStartsWith));
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
		redefineSymbolic(a, "a = p-q", TestErrorHandler.INSTANCE);
		checkInput("a", "a = p - q");
	}

	private void redefineSymbolic(GeoElement geo, String def, ErrorHandler instance) {
		ap.changeGeoElement(geo, def, true, false, instance,
				null);
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
		reload();

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
		assertThat(symbolic, hasValue("0.5"));
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
		GeoSymbolic line = (GeoSymbolic) app.getKernel().lookupLabel("f");
		Suggestion suggestion = SuggestionRootExtremum.get(line);
		Assert.assertNotNull(suggestion);
		suggestion.execute(line);
		Assert.assertNull(SuggestionRootExtremum.get(line));
		Object[] list = app.getKernel().getConstruction().getGeoSetConstructionOrder().toArray();
		((GeoElement) list[list.length - 1]).remove();
		Assert.assertNotNull(SuggestionRootExtremum.get(line));
	}

	@Test
	public void testCASSpecialPointsForNumbers() {
		Assert.assertNull(SuggestionRootExtremum.get(add("1+2")));
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
		assertThat(getSymbolic("a"), hasValue("123456790123456787654320987654321"));
	}

	@Test
	public void testMultivariateFunction() {
		add("f(x, a) = sqrt(x - a)");
		String xml = app.getXML();
		assertThat(xml, containsString("x,a"));
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
		assertThat(vector.getTwinGeo(), CoreMatchers.instanceOf(GeoVector.class));
	}

	@Test
	public void testIntegralIf() {
		add("a(x)=If(0<x<=1,x,1<x<=2,2-x)");
		GeoElement element = add("Integral(a)");
		assertThat(element.toString(StringTemplate.defaultTemplate),
				equalTo("f(x) = If(0 < x ≤ 1, 1 / 2 x², 1 < x ≤ 2, -1 / 2 x² + 2x) + c_{1}"));
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
				equalTo("f(x) = If(5 > x, x²)"));
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
		assertThat(degree, hasValue("1 / 4 " + pi));

		GeoSymbolic realNumber = add("2.222222222222222222222");
		assertThat(realNumber, hasValue("1111111111111111111111 / 500000000000000000000"));

		GeoSymbolic smallNumber = add("2E-20");
		assertThat(smallNumber, hasValue("1 / 50000000000000000000"));

		GeoSymbolic bigNumber = add("1.2345678934534545345345E20");
		assertThat(bigNumber, hasValue("2469135786906909069069 / 20"));
	}

	@Test
	public void testFunctionLikeMultiplication() {
		GeoSymbolic element = add("x(x + 1)");
		assertThat(element, hasValue("x\u00B2 + x"));
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
		assertThat(symbolic, hasValue("1 / 2 x² + 5"));
		assertThat(symbolic.getTwinGeo(), is(notNullValue()));
	}

	@Test
	public void testSolveODEConstant() {
		GeoSymbolic symbolic = add("SolveODE(x)");
		app.getGgbApi().setValue("c_1", 5);
		assertThat(symbolic.getTwinGeo(), hasValue("5 + 1 / 2 x²"));
		assertThat(symbolic, hasValue("c_{1} + 1 / 2 x²"));
	}

	@Test
	public void testFunctionVariableFollowsConstOrder() {
		GeoSymbolic symbolic1 = add("f(u)=u^2");
		assertThat(symbolic1.toValueString(StringTemplate.latexTemplate), is("u^{2}"));
		GeoSymbolic symbolic2 = add("f(5)");
		assertThat(symbolic2.toValueString(StringTemplate.latexTemplate), is("25"));
		GeoSymbolic symbolic3 = add("u=10");
		assertThat(symbolic3.toValueString(StringTemplate.latexTemplate), is("10"));
		GeoSymbolic symbolic4 = add("f(5)");
		assertThat(symbolic4.toValueString(StringTemplate.latexTemplate), is("25"));
	}

	@Test
	public void testIntegralTwinGeoHasSliderValue() {
		GeoSymbolic symbolic = add("Integral(x)");
		GeoNumeric slider = (GeoNumeric) lookup("c_1");
		slider.setValue(10);
		assertThat(symbolic.getTwinGeo().toString(StringTemplate.defaultTemplate),
				equalTo("1 / 2 x² + 10"));
		kernel.getAlgebraProcessor().changeGeoElementNoExceptionHandling(slider, "9",
				new EvalInfo(false), false, null, TestErrorHandler.INSTANCE);
		assertThat(symbolic.getTwinGeo().toString(StringTemplate.defaultTemplate),
				equalTo("1 / 2 x² + 9"));
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
	public void testSymbolicDiffersForPointCommands() {
		GeoSymbolic root = add("Root(x-sqrt(3))");
		GeoSymbolic extremum = add("Extremum(x^2+sqrt(3))");
		GeoSymbolic extremumInterval = add("Extremum(x^2+sqrt(3),-5,5)");
		GeoSymbolic intersect = add("Intersect(x=y,x=sqrt(3))");
		GeoSymbolic intersectBoring = add("Intersect(x=y,x=0)");
		GeoSymbolic asymptote = add("Asymptote(3x/4)");
		assertThat(AlgebraItem.isSymbolicDiffers(root), is(true));
		assertThat(AlgebraItem.isSymbolicDiffers(extremum), is(true));
		assertThat(AlgebraItem.isSymbolicDiffers(extremumInterval), is(true));
		assertThat(AlgebraItem.isSymbolicDiffers(intersect), is(true));
		assertThat(AlgebraItem.isSymbolicDiffers(intersectBoring), is(false));
		assertThat(AlgebraItem.isSymbolicDiffers(asymptote), is(true));
	}

	@Test
	public void testSymbolicDiffersForMode() {
		add("l={1,2,2}");
		GeoSymbolic mode = add("mode=Mode(l)");
		assertThat(AlgebraItem.isSymbolicDiffers(mode), is(false));
	}

	@Test
	public void testRedefineForMode() {
		GeoSymbolic list = add("l={1,2,2}");
		GeoSymbolic mode = add("mode=Mode(l)");
		assertThat(mode, hasValue("{2}"));
		redefineSymbolic(list, "{1,3,3}", TestErrorHandler.INSTANCE);
		assertThat(lookup("mode"), hasValue("{3}"));
	}

	@Test
	public void testNoToggleButtonForSymbolicUndefined() {
		GeoSymbolic solve = add("Solve(0.05>=(1-x)^50)");
		assertThat(AlgebraItem.isSymbolicDiffers(solve), is(true));
	}

	@Test
	public void testToggleSymbolicNumeric() {
		GeoSymbolic solveX = add("Solve(2x=5)");
		GeoSymbolic solveA = add("NSolve(a*a=5)");

		SymbolicUtil.toggleSymbolic(solveX);
		SymbolicUtil.toggleSymbolic(solveA);

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
		assertThat(element, is(CoreMatchers.instanceOf(GeoNumeric.class)));
		GeoNumeric numeric = (GeoNumeric) element;
		assertThat(numeric.getValue(), is(closeTo(10, 0.001)));
	}

	@Test
	public void testFunctionRedefinition() {
		add("f(x) = x");
		GeoSymbolic function = add("f(x) = xx");
		assertThat(function.getTwinGeo(), CoreMatchers.instanceOf(GeoFunction.class));
	}

	@Test
	public void testPrecision() {
		GeoSymbolic derivative = add("Derivative(25.8-0.2ℯ^(-0.025x))");
		assertThat(derivative, hasValue("1 / 200 ℯ^(-1 / 40 x)"));
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
		ExpressionValue value = function.getValue();
		assertNotNull(value);
		assertNotEquals(value.toString(StringTemplate.defaultTemplate), "{?}");
		assertThat(value.toString(StringTemplate.defaultTemplate),
				equalTo("{x = 1.216871488876}"));
	}

	@Test
	public void testSolveChangedToNSolve() {
		add("eq1: (x^2)(e^x)= 5");
		GeoSymbolic function = add("Solve(eq1, x)");
		assertThat(function.getDefinition(StringTemplate.defaultTemplate),
				equalTo("NSolve(eq1, x)"));
	}

	@Test
	public void testSolveNSolveCase1() {
		// Solve and NSolve give identical answers
		GeoSymbolic symbolic = add("Solve(x^2=1)");
		assertThat(AlgebraItem.shouldShowSymbolicOutputButton(symbolic), equalTo(false));

		symbolic = add("NSolve(x^2=1)");
		assertThat(AlgebraItem.shouldShowSymbolicOutputButton(symbolic), equalTo(false));

		symbolic = add("NSolve(sqrt(x)=sqrt(-2-x),x=1)");
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				equalTo("{x = -1}"));
		assertThat(AlgebraItem.shouldShowSymbolicOutputButton(symbolic), equalTo(false));

		// TODO: make these pass in a follow-up ticket
		// 2 variables
		/*
		symbolic = add("Solve({x+y=5, 2x+y=7},{x,y})");
		assertThat(AlgebraItem.shouldShowSymbolicOutputButton(symbolic), equalTo(false));

		symbolic = add("NSolve({x+y=5, 2x+y=7},{x,y})");
		assertThat(AlgebraItem.shouldShowSymbolicOutputButton(symbolic), equalTo(false));
		*/
	}

	@Test
	public void testSolveNSolveCase2() {
		// Solve and NSolve both work and give answers in a different form
		// 1 variable
		GeoSymbolic symbolic = add("Solve(x^2=2)");
		assertThat(AlgebraItem.shouldShowSymbolicOutputButton(symbolic), equalTo(true));
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				equalTo("{x = -sqrt(2), x = sqrt(2)}"));
		SymbolicUtil.toggleSymbolic(symbolic);
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				equalTo("{x = -1.414213562373, x = 1.414213562373}"));

		// 2 variables
		symbolic = add("Solve({x+2y=5,x-3y=7},{x,y})");
		assertThat(AlgebraItem.shouldShowSymbolicOutputButton(symbolic), equalTo(true));
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				equalTo("{{x = 29 / 5, y = -2 / 5}}"));
		SymbolicUtil.toggleSymbolic(symbolic);
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				equalTo("{x = 5.8, y = -0.4}"));
	}

	@Test
	public void testSolveNSolveCase2a() {
		GeoSymbolic symbolic = add("Solve({x²+y=10, x²-y=8},{x,y})");
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				equalTo("{{x = 3, y = 1}, {x = -3, y = 1}}"));
		SymbolicUtil.toggleSymbolic(symbolic);
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				equalTo("{x = 3, y = 1}"));
	}

	@Test
	public void testSolveNSolveCase3() {
		// Solve gives {} or {?} or {x=?} or ? and NSolve gives an answer
		// 1 variable
		GeoSymbolic symbolic = add("Solve(x=cos(x))");
		assertThat(symbolic.getDefinition(StringTemplate.defaultTemplate),
				equalTo("NSolve(x = cos(x))"));
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				equalTo("{x = 0.7390851332152}"));
		assertThat(AlgebraItem.shouldShowSymbolicOutputButton(symbolic), equalTo(false));

		symbolic = add("Solve({x^y=5, x-y=3},{x,y})");
		assertThat(symbolic.getDefinition(StringTemplate.defaultTemplate),
				equalTo("NSolve({x^y = 5, x - y = 3}, {x, y})"));
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				equalTo("{x = 4.134008006438, y = 1.134008006438}"));
		assertThat(AlgebraItem.shouldShowSymbolicOutputButton(symbolic), equalTo(false));

		symbolic = add("sinsolve:=Solve(exp(|sin(x)|)=2)");
		assertThat(symbolic.getDefinition(StringTemplate.defaultTemplate),
				equalTo("NSolve(ℯ^(abs(sin(x))) = 2)"));
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate), anyOf(
				startsWith("{x = -333.7746674752, x = -303.9686412028, x = -208.1109613315, "
						+ "x = -168.880157099, x = -123.2879596848, x = -102.9067113736, "
						+ "x = -98.15521845604, x = -93.48193341286, x = -69.88088457379, "
						+ "x = -65.20759953054, x = -63.59769926661, x = -62.06600687697, "
						+ "x = -60.45610661301, x = -58.92441422337, x = -54.17292130584, "
						+ "x = -52.64122891619"),
				startsWith("{x = -333.7746674754, x = -303.9686412034, x = -208.1109613317,"
						+ " x = -168.880157099, x = -123.2879596848, x = -102.9067113736, "
						+ "x = -98.1552184561, x = -93.48193341287, x = -69.88088457379, "
						+ "x = -65.20759953057, x = -63.59769926661, x = -62.06600687698, "
						+ "x = -60.45610661303, x = -58.92441422339, x = -54.17292130585, "
						+ "x = -52.64122891621")));
		assertThat(AlgebraItem.shouldShowSymbolicOutputButton(symbolic), equalTo(false));

		// 2 variables
		symbolic = add("Solve({x^y=2, x-y=1},{x,y})");
		assertThat(symbolic.getDefinition(StringTemplate.defaultTemplate),
				equalTo("NSolve({x^y = 2, x - y = 1}, {x, y})"));
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				equalTo("{x = 2, y = 1}"));
		assertThat(AlgebraItem.shouldShowSymbolicOutputButton(symbolic), equalTo(false));

	}

	@Test
	public void testSolveNSolveCase3a() {
		// NSolve gives {} or {?} or {x=?} or ? and Solve gives an answer
		GeoSymbolic symbolic = add("NSolve(20=100*x^1000)");

		assertThat(symbolic.getDefinition(StringTemplate.defaultTemplate),
				equalTo("Solve(20 = 100x¹⁰⁰⁰)"));
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				equalTo("{x = -(1 / 5)^(1 / 1000), x = (1 / 5)^(1 / 1000)}"));
		SymbolicUtil.toggleSymbolic(symbolic);
		assertThat(symbolic.getDefinition(StringTemplate.defaultTemplate),
				equalTo("Numeric(Solve(20 = 100x¹⁰⁰⁰))"));
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				equalTo("{x = -0.9983918565382, x = 0.9983918565382}"));
		SymbolicUtil.toggleSymbolic(symbolic);
		assertThat(symbolic.getDefinition(StringTemplate.defaultTemplate),
				equalTo("Solve(20 = 100x¹⁰⁰⁰)"));
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				equalTo("{x = -(1 / 5)^(1 / 1000), x = (1 / 5)^(1 / 1000)}"));
		assertThat(AlgebraItem.shouldShowSymbolicOutputButton(symbolic), equalTo(true));

		symbolic = add("Solve((1-0.0064)^(n)≤0.03,n)");
		assertThat(symbolic.getDefinition(StringTemplate.defaultTemplate),
				equalTo("Solve((1 - 0.0064)^n ≤ 0.03, n)"));
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				equalTo("{n ≥ ln(3 / 100) / ln(621 / 625)}"));
		SymbolicUtil.toggleSymbolic(symbolic);
		assertThat(symbolic.toValueString(StringTemplate.defaultTemplate),
				either(equalTo("{n ≥ 546.1445163345}"))
						.or(equalTo("{n ≥ 546.1445163342}")));
	}

	@Test
	public void testSolveNSolveCase4() {
		// Solve and NSolve both give {} or {?} or {x=?} or ?
		GeoSymbolic symbolic = add("Solve(2^x=-3)");
		assertThat(AlgebraItem.shouldShowSymbolicOutputButton(symbolic), equalTo(false));
		assertThat(GeoFunction.isUndefined(symbolic.toValueString(StringTemplate.defaultTemplate)),
				equalTo(true));
		symbolic = add("NSolve(2^x=-3)");
		assertThat(GeoFunction.isUndefined(symbolic.toValueString(StringTemplate.defaultTemplate)),
				equalTo(true));

		// 2 variables
		symbolic = add("Solve(x^2+y^2=-1, x+y=3)");
		assertThat(GeoFunction.isUndefined(symbolic.toValueString(StringTemplate.defaultTemplate)),
				equalTo(true));
		symbolic = add("NSolve(x^2+y^2=-1, x+y=3)");
		assertThat(GeoFunction.isUndefined(symbolic.toValueString(StringTemplate.defaultTemplate)),
				equalTo(true));
	}

	@Test
	public void testSolveNSolveCase5() {
		GeoSymbolic symbolic = add("Solve(x^2>5)");
		assertThat(AlgebraItem.shouldShowSymbolicOutputButton(symbolic), equalTo(true));
	}

	@Test
	public void testNumericWrapIsNumeric() {
		GeoSymbolic symbolic = add("Solve((1-0.0064)^(x)≤0.03,x)");
		assertThat(symbolic.isSymbolicMode(), equalTo(true));
		SymbolicUtil.toggleSymbolic(symbolic);
		assertThat(symbolic.isSymbolicMode(), equalTo(false));
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
		app.setUndoActive(true);
		add("f(x)=x^2");
		app.storeUndoInfo();
		add("a(x)=Solve(Derivative(f))");
		app.storeUndoInfo();
		add("1+1");
		app.storeUndoInfo();
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
				sin.getTwinGeo(),
				hasValue("0.4115168460675"));
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
				asind.getTwinGeo(),
				hasValue("0.4115168460675"));
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
				equalTo("23.5781784782018°"));
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
				equalTo("11.5369590328155°"));

		GeoSymbolic acosd = add("acosd(1/5)");
		assertThat(
				acosd.getLaTeXDescriptionRHS(true, StringTemplate.numericLatex),
				equalTo("180^{\\circ} \\cdot "
						+ "\\frac{\\operatorname{cos⁻¹} \\left( \\frac{1}{5} \\right)}{\\pi }"));
		acosd.setSymbolicMode(false, false);
		assertThat(
				acosd.getLaTeXDescriptionRHS(true, StringTemplate.numericLatex),
				equalTo("78.4630409671845°"));

		GeoSymbolic atand = add("atand(1/5)");
		assertThat(
				atand.getLaTeXDescriptionRHS(true, StringTemplate.numericLatex),
				equalTo("180^{\\circ} \\cdot "
						+ "\\frac{\\operatorname{tan⁻¹} \\left( \\frac{1}{5} \\right)}{\\pi }"));
		atand.setSymbolicMode(false, false);
		assertThat(
				atand.getLaTeXDescriptionRHS(true, StringTemplate.numericLatex),
				equalTo("11.3099324740202°"));
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
	public void testFactorial() {
		t("(1/2)!", "1 / 2 * sqrt(π)");
		t("a=1/2", "1 / 2");
		t("a!", "1 / 2 * sqrt(π)");
	}

	@Test
	public void testLabelWithEquation() {
		app.setUndoActive(true);
		add("a:f = 1");
		app.storeUndoInfo();
		undoRedo();
		assertThat(getSymbolic("a").toString(StringTemplate.defaultTemplate), is("a: f = 1"));
	}

	@Test
	public void testLabelWithFunction() {
		app.setUndoActive(true);
		add("a:f(x) = 1");
		app.storeUndoInfo();
		undoRedo();
		assertThat(getSymbolic("a").toString(StringTemplate.defaultTemplate), is("a: f(x) = 1"));
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
	public void testNestedFunction() {
		app.setUndoActive(true);

		add("f(x)=1+7*e^(-0.2x)");
		app.storeUndoInfo();

		GeoSymbolic r = add("r(s)=s*(f(s)-1)");
		app.storeUndoInfo();
		assertThat(r.getTwinGeo(), instanceOf(GeoFunction.class));
		assertThat(r.isEuclidianShowable(), is(true));

		undoRedo();
		r = (GeoSymbolic) lookup("r");
		assertThat(r.isEuclidianShowable(), is(true));

		add("f(x) = x");
		r = (GeoSymbolic) lookup("r");
		assertThat(r.isEuclidianShowable(), is(true));
	}

	@Test
	public void testUndoRedoWithUndefinedVariableEquation() {
		app.setUndoActive(true);

		add("f(a,b,x):=a*ln(b x)");
		app.storeUndoInfo();
		add("eq: f(a,b,1)=1");
		app.storeUndoInfo();
		add("1+1");
		app.storeUndoInfo();

		undoRedo();
		GeoSymbolic eq = (GeoSymbolic) lookup("eq");
		assertThat(eq, notNullValue());
	}

	@Test
	public void testCaching() {
		CASGenericInterface cas = kernel.getGeoGebraCAS().getCurrentCAS();

		if (cas instanceof CASgiac) {
			CASgiac casGiac = (CASgiac) cas;
			int cacheSize = casGiac.getCasGiacCacheSize();

			// test input is added to the cache
			t("NSolve(x+x*x+1/2 = 13)", "{x = -4.070714214271, x = 3.070714214271}");
			assertEquals(casGiac.getCasGiacCacheSize(), cacheSize + 2);

			// test nothing is added to the cache, result read from cache
			t("NSolve(x+x*x+1/2 = 13)", "{x = -4.070714214271, x = 3.070714214271}");
			assertEquals(casGiac.getCasGiacCacheSize(), cacheSize + 2);

			int cacheNewSize = casGiac.getCasGiacCacheSize();

			t("Cross((1,1,1),Cross((2,3,4),(5,7,11)))", "(1, 6, -7)");
			assertEquals(casGiac.getCasGiacCacheSize(), cacheNewSize + 2);

			t("Cross((1,1,1),Cross((2,3,4),(5,7,11)))", "(1, 6, -7)");
			assertEquals(casGiac.getCasGiacCacheSize(), cacheNewSize + 2);
		}
	}

	@Test
	public void testIsNotCachingRandomValues() {
		GeoSymbolic symbolic1 = add("RandomBetween(0, 9999999999)");
		GeoSymbolic symbolic2 = add("RandomBetween(0, 9999999999)");
		assertNotEquals(symbolic1.toValueString(StringTemplate.defaultTemplate),
				symbolic2.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	public void testUndoRedoWithSolve() {
		app.setUndoActive(true);

		add("u(x)=-2*10^(-5) x^(3)+1.4*10^(-2) x^(2)-2.4 x+200");
		app.storeUndoInfo();
		add("a(x)=Integral(u,0,340)");
		app.storeUndoInfo();
		add("eq1: ((a)/(3))=Integral(u,0,s)");
		app.storeUndoInfo();
		add("solution = Solve(eq1,s)");
		app.storeUndoInfo();

		undoRedo();
		GeoSymbolic eq = (GeoSymbolic) lookup("solution");
		assertThat(eq, notNullValue());
	}

	@Test
	public void orderShouldNotChange() {
		app.setUndoActive(true);

		t("f(a,x) = a*x^2", "a * x^(2)");
		app.storeUndoInfo();
		t("x", "x");
		app.storeUndoInfo();
		t("x", "x");
		app.storeUndoInfo();
		t("r:=f(a,a)", "a^(3)");
		app.storeUndoInfo();

		assertEquals(3, lookup("r").getConstructionIndex());

		undoRedo();
		assertEquals(3, lookup("r").getConstructionIndex());
	}

	@Test
	public void matrixInvertSymbolic() {
		add("A={{1,2},{3,4}}");
		GeoElement geo = add("Invert(A)");
		assertThat(AlgebraItem.isSymbolicDiffers(geo), is(true));
		assertThat(geo.getAlgebraDescriptionLaTeX(),
				is("m1\\, = \\,\\left(\\begin{array}{rr}-2&1"
						+ "\\\\\\frac{3}{2}&\\frac{-1}{2}\\\\ \\end{array}\\right)"));
	}

	@Test
	public void testRedefinitionWithTwoVariables() {
		add("f(a)=k a^2");
		GeoSymbolic symbolic = add("f(a, k)=k+a^2");
		assertThat(symbolic.toString(StringTemplate.defaultTemplate), is("f(a, k) = a² + k"));
		GeoSymbolic result = add("f(1,2)");
		assertThat(result.toString(StringTemplate.defaultTemplate), is("a = 3"));
	}

	@Test
	public void testArgumentOrderRemainsUnchanged() {
		add("f(x, a) = x^2 + a");
		GeoSymbolic symbolic = add("fs(x,a)=Derivative(f(x,a),x)");
		assertThat(symbolic.getFunctionVariables().length, is(2));
		assertThat(symbolic.getFunctionVariables()[0].getSetVarString(), is("x"));
		assertThat(symbolic.getFunctionVariables()[1].getSetVarString(), is("a"));
	}

	@Test
	public void numericAlternativeCommand() {
		Assume.assumeTrue(!AppD.WINDOWS);
		add("f(x) = -x^2 * e^(-x)");
		add("g(x) = 1 + (f'(x))^2");
		t("Integral(sqrt(g),0,20)", "20.12144888423");
	}

	@Test
	public void testBinomialDistNumericIsDefined() {
		GeoSymbolic binomialDist = add("BinomialDist(230,0.68,140,true)");
		assertThat(binomialDist.toValueString(StringTemplate.defaultTemplate),
				matchesPattern("[0-9]+ / [0-9]+"));
		SymbolicUtil.toggleSymbolic(binomialDist);
		assertThat(binomialDist.toValueString(StringTemplate.defaultTemplate),
				equalTo("0.013281921892"));
	}

	@Test
	public void testApproxResultForLargePowers() {
		String result = AppD.MAC_OS ? "0.9794246092973" : "0.979424609317";
		t("0.99999874^16500", result);
	}

	@Test
	public void testSolutionsString() {
		GeoSymbolic solutions = add("Solutions(x^2=5)");
		assertThat(AlgebraItem.getLatexString(solutions, null, false),
				equalTo("l1\\, = \\,\\left\\{-\\sqrt{5},\\;\\sqrt{5}\\right\\}"));
	}

	@Test
	public void testLengthImprovements() {
		t("Length(5+5i)", "5 * sqrt(2)");
		t("Length(t e x t)", "?");

		add("a=Curve(t,t^2,t,0,5)");
		t("Length(a)", "?");

		add("b=Curve(t,t^2,t-1,t,0,5)");
		t("Length(b)", "?");

		t("Length(-5)", "?");
		t("Length((3,4))", "5");
		t("Length((3,4,5))", "5 * sqrt(2)");
		t("Length({1,2,3})", "3");
		t("Length((1,x))", "sqrt(x^(2) + 1)");
		t("Length((1,2,x))", "sqrt(x^(2) + 5)");
		t("Length(\"hello\")", "5");
		t("Length(x-y^2=0)", "?");
	}

	@Test
	public void testThrowsCircularDefinitionException() {
		GeoElement element = add("c(0,0)");
		redefineSymbolic(element, "C=(0,0)", TestErrorHandler.INSTANCE);
		ErrorAccumulator errAcc = new ErrorAccumulator();
		redefineSymbolic(element, "C(0,0)", errAcc);
		assertThat(element.getDefinition(), is(notNullValue()));
		assertThat(errAcc.getErrors(), equalTo("Circular definition"));
	}

	@Test
	public void testIterationOutput() {
		app.setCasConfig();
		GeoSymbolic geo4args = add("Iteration(2u + 1, u, {0}, 64)");
		assertThat(geo4args, is(nullValue()));
	}

	@Test
	public void testFactorInvalid() {
		app.setCasConfig();
		AlgebraTestHelper.shouldFail("Factor()", "Illegal number of arguments", app);
		StringBuilder consXML = new StringBuilder();
		app.getKernel().getConstruction().getConstructionElementsXML(consXML, false);
		assertThat(consXML.toString(), is(""));
	}

	@Test
	public void testDivisionOfVectors() {
		shouldFail("Vector((1,2))/Vector((3,4))", "division");
		shouldFail("Vector((1,2,3))/Vector((-2,-3))", "division");
		shouldFail("Vector((1,2,3))/Vector((-2,-3,-4))", "division");
		shouldFail("Vector((1,2))/Vector((-2,-3,-4))", "division");
	}

	@Test
	public void testHiddenCommands() {
		shouldFail("ExpSimplify(x)", "Unknown command");
		shouldFail("SolveODEPoint(x,(1,2))", "Unknown command");
	}

	@Test
	public void functionsShouldWorkInNSolve() {
		add("f(x)=.05x^3-.8x^2+3x");
		t("NSolve(2f(x) = f(x+1))",
				"{x = 0.5737788916239, x = 6.672641540783, x = 11.75357956759}");
	}

	@Test
	public void testTake() {
		t("Take({2, 4, 3, 7, 4}, 3)", "{3, 7, 4}");
		t("Take(\"GeoGebra\", 3)", "oGebra");
		t("Take({2, 4, 3, 7, 4}, 3, 4)", "{3, 7}");
		t("Take(\"GeoGebra\", 3, 6)", "oGeb");
	}

	@Test
	public void testInvalidTrigInput() {
		GeoSymbolic invalid = add("tan^(-1)");
		assertThat(invalid, is(nullValue()));
	}

	@Test
	public void shouldNotReplacePiWithDecimal() {
		t("1/sin(pi)", "Infinity");
	}

	/**
	 * like AlgebraItemTest:testIsGeoFraction, but for GeoSymbolic
	 */
	@Test
	public void testIsGeoFraction() {
		GeoElement fraction = add("1+1/3");
		GeoElement solve2 = add("Solve(2x=3,x)");
		assertThat(fraction, instanceOf(GeoSymbolic.class));
		assertThat(AlgebraItem.isGeoFraction(fraction), is(true));
		assertThat(AlgebraItem.isGeoFraction(solve2), is(false));
	}

	@Test
	public void testEvaluatesToFraction() {
		GeoElement element = add("1/2");
		assertThat(AlgebraItem.evaluatesToFraction(element), is(true));
		element = add("0.5");
		assertThat(AlgebraItem.evaluatesToFraction(element), is(true));
		element = add("1");
		assertThat(AlgebraItem.evaluatesToFraction(element), is(false));
	}

	@Test
	public void testCASGeoType() {
		GeoElement element = add("1/2");
		assertThat(AlgebraItem.getCASOutputType(element), is(AlgebraItem.CASOutputType.SYMBOLIC));
		element = add("Slider(0,1)");
		assertThat(AlgebraItem.getCASOutputType(element), is(AlgebraItem.CASOutputType.NUMERIC));
	}

	@Test
	public void testCollectFunctionVariables() {
		GeoSymbolic element = add("x+1");
		assertThat(element.collectVariables().size(), is(1));
		assertThat(element.collectVariables().get(0).toString(StringTemplate.defaultTemplate),
				is("x"));

		element = add("x+y");
		assertThat(element.collectVariables().size(), is(2));
		assertThat(element.collectVariables().get(0).toString(StringTemplate.defaultTemplate),
				is("x"));
		assertThat(element.collectVariables().get(1).toString(StringTemplate.defaultTemplate),
				is("y"));

		element = add("FitPoly({(1,2),(3,4)},1)");
		assertThat(element.collectVariables().size(), is(1));
		assertThat(element.collectVariables().get(0).toString(StringTemplate.defaultTemplate),
				is("x"));

		element = add("Product(n*z, n, 1, z)");
		assertThat(element.collectVariables().size(), is(1));
		assertThat(element.collectVariables().get(0).toString(StringTemplate.defaultTemplate),
				is("z"));
	}

	@Test
	public void testFitPolyLabel() {
		GeoSymbolic geo = createGeoWithHiddenLabel("FitPoly({(1,2),(3,4)},1)");
		showLabel(geo);
		assertThat(geo.getAlgebraDescriptionDefault(), startsWith("f(x)"));
	}

	@Test
	public void testElementOfSyntax() {
		add("l1={1,2,3,4}");
		t("l1(2)", "2");
	}

	/**
	 * APPS-4889
	 */
	@Test
	public void testShouldNotChangeToMultiplication() {
		t("f(x) = x^2", "x^(2)");
		t("g(y) = y^2 + 3", "y^(2) + 3");
		t("h(z) = z / 2", "1 / 2 * z");
		t("i(t) = t^3", "t^(3)");

		t("A = (2, 4, 6)", "(2, 4, 6)");
		t("B = (4, 8)", "(4, 8)");

		t("f(x(A))", "4");
		t("f'(x(A))", "4");

		t("g(y(A))", "19");
		t("h'(z(A))", "1 / 2");

		t("i(x(B))", "64");
		t("i'(y(B))", "192");

		t("Integral(f,x,0,x(A))", "8 / 3");
		t("Integral(f,0,x(A))", "8 / 3");
	}

	/**
	 * APPS-4889
	 */
	@Test
	public void testShouldChangeToMultiplication() {
		t("x(a)", "x * a");
		t("b = 3", "3");
		t("x(b)", "3 * x");
	}

	@Test
	public void testListAsFunction() {
		add("h(x)={x, x + 1}");
		t("h(1)", "{1, 2}");
	}

	@Test
	public void testElementOfMatrix() {
		add("m1={{1,2},{3,4}}");
		t("m1(2,2)", "4");
	}

	@Test
	public void testConstantFunctionsPlottedOnReload() {
		add("f(x) = 3");
		add("g(x) = 2 * 5");
		app.setXML(app.getXML(), true);
		assertEquals(2, app.getActiveEuclidianView().getAllDrawableList().size());
	}

	@Test
	public void testFormulaString() {
		assertThat(add("f=If(x<a,x+1)"),
				hasFormulaString("x + 1, \\;\\;\\;\\; \\left(a > x \\right)"));
		assertThat(add("h=If(x<a,a,b)"),
				hasFormulaString("\\left\\{\\begin{array}{ll} a& : a > x\\\\"
						+ " b& : \\text{otherwise} \\end{array}\\right. "));
		assertThat(add("h=If(x<a,a,x<b,b,c+1)"),
				hasFormulaString("\\left\\{\\begin{array}{ll} a& : a > x"
						+ "\\\\ b& : b > x\\\\ c + 1& : \\text{otherwise} \\end{array}\\right. "));
		assertThat(add("h=If(x<a,a,x<b,b,x<c,c+1)"),
				hasFormulaString("\\left\\{\\begin{array}{ll} a& : a > x"
						+ "\\\\ b& : b > x\\\\ c + 1& : c > x \\end{array}\\right. "));
		assertThat(add("x+x"), hasFormulaString("2 \\; x"));
	}

	@Test
	@Issue("APPS-5428")
	public void testFormulaStringIfCommand() {
		assertThat(add("If(a,b,c)"), hasFormulaString("If \\left(a,\\;b,\\;c \\right)"));
		assertThat(add("If(a<a+1,b,c)"), hasFormulaString("b"));
	}

	@Test
	public void symbolicValueShouldBeUsedToComputeDescendants() {
		GeoSymbolic a = add("a=sin(42deg)");
		a.setSymbolicMode(false, true);
		t("Solve(a/9=sin(x)/10)", "{x = 2 * k_{1} * π + sin⁻¹(10 * "
				+ "cos(4 * π / 15) / 9), x = 2 * k_{1} * π + π - sin⁻¹(10 * cos(4 * π / 15) / 9)}");
	}

	private Matcher<GeoSymbolic> hasFormulaString(String f) {
		return hasProperty("formula",
				geo -> geo.getFormulaString(StringTemplate.latexTemplate, true), f);
	}

	@Test
	public void bracketShouldBeMultiplicationForSymbolicNumbers() {
		add("a=2");
		add("p=0.1");
		t("NSolve(a(4)=x)", "{x = 8}");
		t("p(1-p)", "9 / 100");
	}

	@Test
	public void bracketShouldBeMultiplicationForSymbolicNumbersWithoutDefiningA() {
		t("NSolve(-4 a(2)=16)", "{a = -2}");
	}

	@Test
	public void bracketShouldNotBeMultiplicationForSymbolicVariables() {
		t("Derivative(f(x)*g(x))", "f'(x) * g(x) + g'(x) * f(x)");
	}

	@Test
	public void shouldExpandExpressionInIntegral() {
		t("h=x^2", "x^(2)");
		t("Integral(h,0,1)", "1 / 3");
	}

	@Test
	public void booleansShouldNotHaveNumericValue() {
		GeoSymbolic p = add("IsPrime(4)");
		p.setSymbolicMode(false, true);
		p.update();
		assertThat(SymbolicUtil.shouldComputeNumericValue(p.getValue()), equalTo(false));
		assertThat(p, hasValue("false"));
	}

	@Test
	public void shouldNotHideParametricLabel() {
		GeoElement[] parametric = new GeoElement[] {
				add("g1: X=(1,2)+s (4,5)"),
				add("g2: X=(1,2,3)+s (4,5,6)"),
				add("g3: X=(1,2)+sin(s)*(4,5)+cos(s)*(7,8)"),
				add("g4: X=(1,2,3)+sin(s)*(4,5,6)+cos(s)*(7,8,9)")
		};
		new LabelHiderCallback().callback(parametric);
		for (GeoElement geo: parametric) {
			assertThat(geo.getLabelSimple(), startsWith("g"));
		}
	}

	@Test
	public void shouldHideAutomaticLabel() {
		GeoElement[] parametric = new GeoElement[] {
				add("x=y"),
				add("y=z+x")
		};
		new LabelHiderCallback().callback(parametric);
		for (GeoElement geo: parametric) {
			assertThat(geo.getLabelSimple(), startsWith(LabelManager.HIDDEN_PREFIX));
		}
	}

	@Test
	public void maxCommandShouldHaveSymbolicToggle() {
		t("f(x) = x^2 * 0.6^x + 4", "(3 / 5)^(x) * x^(2) + 4");
		t("A = Max(f, 0, 10)", "(-2 / ln(3 / 5), (4 * (3 / 5)^(-2 / ln(3 / 5)) + "
				+ "4 * (ln(3 / 5))^(2)) / (ln(3 / 5))^(2))");
		GeoSymbolic maxCommand = getSymbolic("A");
		assertTrue(AlgebraItem.isSymbolicDiffers(maxCommand));
	}

	@Test
	public void minCommandShouldHaveSymbolicToggle() {
		t("f(x) = x^2 * 0.6^x + 4", "(3 / 5)^(x) * x^(2) + 4");
		t("A = Min(f, 0, 5)", "(0, 4)");
		GeoSymbolic minCommand = getSymbolic("A");
		assertTrue(AlgebraItem.isSymbolicDiffers(minCommand));
	}

	@Test
	@Issue("APPS-5454")
	public void shouldUseFunctionVariables() {
		GeoSymbolic jd = add("f(x)=floor(x)");
		assertThat(jd.getFunctionVariables().length, equalTo(1));
		assertThat(jd.getVarString(StringTemplate.defaultTemplate), equalTo("x"));
	}

	@Test
	@Issue("APPS-5344")
	public void mistypedParametricShouldFail() {
		t("X=(1,2,3)+r(1,2,3)", "?");
		t("X=(1,2)+s(1,2)", "(s(1, 2) + 1, 2)");
	}

	@Test
	@Issue("APPS-5264")
	public void testIntegral2() {
		t("f(x)=b", "b");
		t("Integral[f]", "b * x + c_{1}");
	}

	@Test
	@Issue("APPS-5477")
	public void parametricLinesShouldReload() {
		add("f: X=(2,3,4)+r (2,2,2)");
		t("f(3)", "(8, 9, 10)");
		reload();
		t("f(3)", "(8, 9, 10)");
	}

	@Test
	@Issue("APPS-5511")
	public void parametricEquation() {
		add("v:=(a,b)");
		t("Solve(v=(1,2),{a,b})", "{{a = 1, b = 2}}");
		t("s2:Solve(v=(1,2))", "{{a = 1, b = 2}}");
		assertEquals("s2 = Solve(v = (1, 2))",
				lookup("s2").getDefinitionForInputBar());
	}

	@Test
	@Issue("APPS-5511")
	public void parametricEquationList() {
		add("v:=(a,b)");
		t("Solve({v=(1,2)},{a,b})", "{{a = 1, b = 2}}");
	}

	@Test
	@Issue({"APPS-1660", "APPS-5511"})
	public void shouldReloadVectors() {
		app.getGgbApi().evalXML("<expression label=\"v\" exp=\"(a, b)\" type=\"vector\"/>\n"
				+ "<element type=\"symbolic\" label=\"v\"></element>");
		assertThat(lookup("v"), hasValue("(a, b)"));
	}

	@Test
	public void twinShouldBeAnEquation() {
		GeoSymbolic original = add("c:a=x+5");
		GeoSymbolic copy = add("c");
		assertThat(original.getTwinGeo(), nullValue());
		assertThat(copy.getTwinGeo(), nullValue());
		assertThat(original, hasValue("a = x + 5"));
		assertThat(copy, hasValue("a = x + 5"));
	}

	@Test
	@Issue("APPS-5658")
	public void matrixMultiplicationShouldResultInFunction() {
		add("m1 = {{1, 0.3}, {1 / 4, 2}}");
		GeoSymbolic result = add("m1 * {{cos(t)}, {sin(t)}}");
		SymbolicUtil.toggleSymbolic(result);
		assertThat(result, hasValue("{{1cos(t) + 0.3sin(t)}, {0.25cos(t) + 2sin(t)}}"));
	}

	@Test
	@Issue("APPS-5893")
	public void twinForSimplifyShouldBeNumber() {
		GeoSymbolic simplify = add("Simplify(2+3)");
		assertThat(simplify.getTwinGeo().getGeoClassType(), is(GeoClass.NUMERIC));
	}

}