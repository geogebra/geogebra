package org.geogebra.common.kernel.commands;

import static com.himamis.retex.editor.share.util.Unicode.DEGREE_STRING;
import static org.geogebra.common.BaseUnitTest.isDefined;
import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.euclidian.ScreenReaderAdapter;
import org.geogebra.common.io.XmlTestUtil;
import org.geogebra.common.jre.headless.EuclidianViewNoGui;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoConicFivePoints;
import org.geogebra.common.kernel.algos.AlgoIntersectPolyLines;
import org.geogebra.common.kernel.algos.AlgoTableText;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.ImageManager;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.geogebra.test.commands.CommandSignatures;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.himamis.retex.editor.share.util.Unicode;

public class CommandsTestCommon {
	static AppCommon3D app;
	static AlgebraProcessor ap;
	static List<Integer> signature;
	protected static int syntaxes = -1000;

	protected static void tRound(String s, String... expected) {
		t(s, StringTemplate.editTemplate, expected);
	}

	protected static void t(String s, String... expected) {
		testSyntax(s, AlgebraTestHelper.getMatchers(expected), app, ap,
				StringTemplate.xmlTemplate);
	}

	protected static void t(String s, StringTemplate tpl, String... expected) {
		testSyntax(s, AlgebraTestHelper.getMatchers(expected), app, ap,
				tpl);
	}

	protected static void t(String s, Matcher<String> expected) {
		testSyntax(s, Collections.singletonList(expected), app, ap,
				StringTemplate.xmlTemplate);
	}

	protected static void testSyntax(String s, List<Matcher<String>> expected,
			App app1, AlgebraProcessor proc, StringTemplate tpl) {
		app1.getEuclidianView1().getEuclidianController().clearZoomerAnimationListeners();
		if (syntaxes == -1000) {
			Throwable t = new Throwable();
			String cmdName = t.getStackTrace()[2].getMethodName().substring(3);
			try {
				Commands.valueOf(cmdName);
			} catch (Exception e) {
				cmdName = t.getStackTrace()[3].getMethodName().substring(3);
			}

			signature = CommandSignatures.getSignature(cmdName, app1);
			if (signature != null) {
				syntaxes = signature.size();
				AlgebraTestHelper.dummySyntaxesShouldFail(cmdName, signature,
						app1);
			}
			Log.debug(cmdName);
		}
		syntaxes--;
		AlgebraTestHelper.checkSyntaxSingle(s, expected, proc, tpl);
	}

	@Before
	public void resetSyntaxes() {
		resetSyntaxCounter();
		app.getKernel().clearConstruction(true);
		app.setActiveView(App.VIEW_EUCLIDIAN);
		GeoImplicitCurve.setFastDrawThreshold(10000);
	}

	public static void resetSyntaxCounter() {
		syntaxes = -1000;
	}

	@After
	public void checkSyntaxes() {
		checkSyntaxesStatic();
		if (app.getKernel().getConstruction().getGeoSetLabelOrder().size() < 20) {
			XmlTestUtil.checkCurrentXML(app);
		}
	}

	/**
	 * Assert that there are no unchecked syntaxes left
	 */
	public static void checkSyntaxesStatic() {
		assertTrue("unchecked syntaxes: " + syntaxes + signature,
				syntaxes <= 0);
	}

	/**
	 * Create the app
	 */
	@BeforeClass
	public static void setupApp() {
		app = AppCommonFactory.create3D();
		ap = app.getKernel().getAlgebraProcessor();
		app.setRandomSeed(42);
	}

	protected static GeoElement get(String label) {
		return app.getKernel().lookupLabel(label);
	}

	protected static String deg(String string) {
		return string + "*" + DEGREE_STRING;
	}

	@Test
	public void testQuadricExpr() {
		t("-y^2=z-1", "-y" + Unicode.SUPERSCRIPT_2 + " + 0z"
				+ Unicode.SUPERSCRIPT_2 + " - z = -1");
		t("y^2=1-z", "y" + Unicode.SUPERSCRIPT_2 + " + 0z"
				+ Unicode.SUPERSCRIPT_2 + " + z = 1");
	}

	@Test
	public void operationSequence() {
		assertEquals(StringUtil.preprocessForParser("1..2", false),
				"1" + Unicode.ELLIPSIS + "2");
		t("3.2..7.999", "{3, 4, 5, 6, 7, 8}");
		t("-3.2..3.2", "{-3, -2, -1, 0, 1, 2, 3}");
		t("3.2..-2", "{3, 2, 1, 0, -1, -2}");
		t("seqa=2*(1..5)", "{2, 4, 6, 8, 10}");
		assertEquals(
				"<expression label=\"seqa\" exp=\"(2 * (1" + Unicode.ELLIPSIS
						+ "5))\"/>",
				app.getGgbApi().getXML("seqa").split("\n")[0]);
		t("seqa=(1..3)+3", "{4, 5, 6}");
		assertEquals(
				"<expression label=\"seqa\" exp=\"(1" + Unicode.ELLIPSIS
						+ "3) + 3\"/>",
				app.getGgbApi().getXML("seqa").split("\n")[0]);
	}

	@Test
	public void testDivision() {
		t("Division[x^4-3x^2+x-4, x+1]", "{x^(3) - x^(2) - (2 * x) + 3, -7}");
		t("Div[x^4-3x^2+x-4, x+1]", "x^(3) - x^(2) - (2 * x) + 3");
		t("Mod[x^4-3x^2+x-4, x+1]", "-7");

		t("Division[x^4-3x^2+x-4, x^5]", "{0, x^(4) - (3 * x^(2)) + x - 4}");
		t("Div[x^4-3x^2+x-4, x^5]", "0");
		t("Mod[x^4-3x^2+x-4, x^5]", "x^(4) - (3 * x^(2)) + x - 4");

		t("Division[x^4-3x^2+x-4, x^2+x+1]", "{x^(2) - x - 3, (5 * x) - 1}");
		t("Div[x^4-3x^2+x-4, x^2+x+1]", "x^(2) - x - 3");
		t("Mod[x^4-3x^2+x-4, x^2+x+1]", "(5 * x) - 1");

		// result will change with GGB-1895
		// t("Division[x^4-3x^2+x-4, 2x+1]",
		// "{(0.5 * x^(3)) - (0.25 * x^(2)) - (1.375 * x) + 1.1875, -5.1875}");
		// t("Division[5x^4-3x^2+x-4, 3x+1]",
		// "{(1.6666666666666667 * x^(3)) - (0.5555555555555556 * x^(2)) -
		// (0.8148148148148149 * x) + 0.6049382716049383, -4.604938271604938}");
		// t("Division[2x+1,0x+0]", "");

		t("Division[2x+1,x+1]", "{2, -1}");
		t("Div[2x+1,x+1]", "2");
		t("Mod[2x+1,x+1]", "-1");

		t("Division[x+1,x+1]", "{1, 0}");
		t("Div[x+1,x+1]", "1");
		t("Mod[x+1,x+1]", "0");

		t("Division[2x+1,x-1]", "{2, 3}");
		t("Div[2x+1,x-1]", "2");
		t("Mod[2x+1,x-1]", "3");

		t("Division[2x+2,0x+2]", "{x + 1, 0}");
		t("Div[2x+2,0x+2]", "x + 1");
		t("Mod[2x+2,0x+2]", "0");
	}

	@Test
	public void functionDependentPoly() {
		t("s(x,y)=x+y", "x + y");
		t("s(1,2)*x=1", "x = 0.3333333333333333");
	}

	@Test
	public void piecewiseIntegration() {
		t("f(x):=x^2", "x^(2)");
		t("g(x):=1/x", "1 / x");
		t("h(x):=If(0<x<=2,x^2, x>2, 1/x)",
				"If[0 < x " + Unicode.LESS_EQUAL + " 2, x^(2), x > 2, 1 / x]");
		t("h2(x):=If(x<=2,x^2, x>2, 1/x)",
				"If[x " + Unicode.LESS_EQUAL + " 2, x^(2), x > 2, 1 / x]");
		t("h3(x):=If(0<x<=2,f(x), x>2, g(x))",
				"If[0 < x " + Unicode.LESS_EQUAL + " 2, x^(2), x > 2, 1 / x]");
		t("h4(x):=If(0<x<=2,f(x), 2<x<4, g(x))", "If[0 < x "
				+ Unicode.LESS_EQUAL + " 2, x^(2), 2 < x < 4, 1 / x]");
		t("h5(x):=If(x>=2,x^2, x<2, 1/x)",
				"If[x " + Unicode.GREATER_EQUAL + " 2, x^(2), x < 2, 1 / x]");
		for (String cmd : new String[] { "Integral", "NIntegral" }) {
			tRound(cmd + "(h(x),1,3)", eval("-ln(2) + ln(3) + 7 / 3"));
			tRound(cmd + "(h2(x),1,3)", eval("-ln(2) + ln(3) + 7 / 3"));
			tRound(cmd + "(h3(x),1,3)", eval("-ln(2) + ln(3) + 7 / 3"));
			tRound(cmd + "(h4(x),1,3)", eval("-ln(2) + ln(3) + 7 / 3"));
			tRound(cmd + "(h5(x),1,3)", "7.02648");
		}
		t("Integral(If(x^2>1,1,x>7,0,0),-1,1)", "0");
		t("Integral(If(x^2>1,1,x>7,0,0),-2,2)", "2");
		t("Integral(If(x>2,1,2),0,2.01)", "4.01");
		t("Integral(If(x^4>1,1,0),-2,2)", "2");
		tRound("Integral(If(x>2,3,x>1,2,1),0.99,3.01)", "5.04");
	}

	@Test
	public void intersectPlanesShouldUpdate() {
		t("e1:x=z", "x - z = 0");
		t("e2:y=z", "y - z = 0");
		t("SetValue(e1,?)");
		t("SetValue(e2,?)");
		t("g:Intersect(e1,e2)", "X = (NaN, NaN, NaN) + "
				+ Unicode.lambda + " (NaN, NaN, NaN)");
		t("SetValue(e1,x=z)");
		t("SetValue(e2,y=z)");
		t("g", "X = (0, 0, 0) + "
				+ Unicode.lambda + " (1, 1, 1)");
	}

	@Test
	public void angleBisectorsShouldUpdate() {
		t("e1:X = (0, 0, 0) + t (1, 0, 0)", "X = (0, 0, 0) + t (1, 0, 0)");
		t("e2:X = (0, 0, 0) + t (0, 1, 0)", "X = (0, 0, 0) + t (0, 1, 0)");
		t("SetValue(e1,?)");
		t("SetValue(e2,?)");
		t("g:AngleBisector(e1,e2)", "X = (?, ?, ?)", "X = (?, ?, ?)");
		t("SetValue(e1,X = (0, 0, 0) + t (1, 0, 0))");
		t("SetValue(e2,X = (0, 0, 0) + t (0, 1, 0))");
		t("g", "X = (0, 0, 0) + "
				+ Unicode.lambda + " (1, -1, 0)");
	}

	private static void intersect(String arg1, String arg2, boolean num,
			String... results) {
		intersect(arg1, arg2, num, num, results);
	}

	private static void intersect(String arg1, String arg2, boolean num,
			boolean closest, String... results) {
		app.getKernel().clearConstruction(true);
		app.getKernel().getConstruction().setSuppressLabelCreation(false);
		tRound("its:=Intersect(" + arg1 + "," + arg2 + ")", results);
		GeoElement geo = get("its") == null ? get("its_1") : get("its");
		boolean symmetric = geo != null
				&& !(geo.getParentAlgorithm() instanceof AlgoIntersectPolyLines
				&& geo.getParentAlgorithm().getOutput(0)
				.getGeoClassType() == geo.getParentAlgorithm()
				.getOutput(1).getGeoClassType());
		if (symmetric) {
			tRound("Intersect(" + arg2 + "," + arg1 + ")", results);
		}
		if (num) {
			tRound("Intersect(" + arg1 + "," + arg2 + ",1)", results[0]);
			if (symmetric) {
				tRound("Intersect(" + arg2 + "," + arg1 + ",1)", results[0]);
			}
		}
		if (closest) {
			tRound("Intersect(" + arg1 + "," + arg2 + "," + results[0] + ")",
					results[0]);
		}
	}

	@Test
	public void intersectConicConicSymmetric() {
		intersectSym("7x y + 3x - 9y = -820", "-7x^2 - 7y^2 - 4x + 14y = -1220",
				"{(-10, 10), (-9.21683, 10.77766)}");
		intersectSym("-9x² + 20x + 2y = -1106", "5x y + 6x - 2y = 96",
				"{(-10, -3), (0.36626, -556.05895), (12.25596, 0.37895)}");
		intersectSym("-10x y + 8x + 7y = -270", "7x^2 + 7y^2 + 6x + 4y = 564",
				"{(-9.26094, -1.96681), (-2.07704, -9.12423),"
						+ " (5.88084, 6.11961), (6, 6)}");
		intersectSym("5x y - 8x + 2y = 240", "-3 x^2 + 14x y - 6x + 6y = 643",
				"{(7, 8)}");
		intersectSym("4y² - 9x + y = 66", "-9 x² - 9y² - 4x - 2y = -920",
				"{(8, -6), (8.01745, 5.75334)}");
		intersectSym("-4x² - 4y² + 9x - 9y = -405", "x² - 2x - 12y = -108",
				"{(0, 9), (2.09298, 9.01622)}");
		intersectSym("2x y - 2x - 9y = -108", "-5x y + 5y² + 6x + 7y = 724",
				"{(-0.34925, 11.20776), (463.84925, 0.89224), (10, -8)}");
		intersectSym("-9x² + 20x + 2y = -1106", "5x y + 6x - 2y = 96",
				"{(-10, -3), (0.36626, -556.05895), (12.25596, 0.37895)}");
		intersectSym("-x² + 6x + 20y = -291", "-x y - x + 2y = -83",
				"{(-7, -10), (22, 3.05)}");
		intersectSym("-4x y + 2x - 7y = -88", "x² + y² + 2x + 8y = 3",
				"{(-5, -6)}");
	}

	private void intersectSym(String in1, String in2, String expected) {
		tRound("{Intersect(" + in1 + "," + in2 + ")}", expected);
		tRound("{Intersect(" + in2 + "," + in1 + ")}", expected);
	}

	@Test
	public void intersectConicConic() {
		tRound("{Intersect(x y + 6x -5y = 158, x y = 128)}",
				"{(-8.12623, -15.75147), (13.12623, 9.75147)}");
		tRound("{Intersect( 7y² + 2x + 14y = 3 , -6y² - 20x = 7 )}",
				"{(-2.11538, -2.42582), (-0.36704, 0.23832)}");
		tRound("{Intersect( -5x² - 9x + 9y = 0 , 2x² - 12x + 14y = 7 )}",
				"{(0.75, 1.0625), (-0.95455, -0.44835)}");
		tRound("{Intersect[-7x y - 10x - 7y = 10, 2x y - 9x + 2y = 9]}",
				"{(?, ?)}");
		tRound("{Intersect[x y - x + 3y = -15, x² + y² - 12x - 2y = 143]}",
				"{(-6, 7), (-1.3923, -10.19615), (19.3923, 0.19615)}");
		tRound("{Intersect[-7x y - 10x - 7y = 10, 2x y - 9x + 2y = 9]}", "{(?, ?)}");
		tRound("{Intersect(-4x y + 2x - 7y = -88, x² + y² + 2x + 8y = 3)}", "{(-5, -6)}");
		tRound("{Intersect(-x² + 6x + 20y = -291, -x y - x + 2y = -83)}",
				"{(-7, -10), (22, 3.05)}");
		tRound("{Intersect(-7x² - 10x + 18y = 118, -5x² - 5y² - 7x - 3y = -272)}",
				"{(-2, 7), (0.57571, 7.00428)}");
		tRound("{Intersect(4y² + 3x - 6y = 13, -7x y - 2x + 8y = -32)}",
				"{(-2.14218, -1.5779), (5.08096, 0.79219), (3, 2)}");
		tRound("{Intersect[ x² - 6x - 2y = -11, x² + y² - 6x - 4y = -12]}", "{(3, 1)}");
		// intersect very flat parabola (nearly parallel lines) with a circle
		tRound("{Intersect[ -9y² - 2x + 8y = -992, 5x² + 5y² - 9x + 6y = 566]}",
				"{(-3.99989, -10.10585), (6, -10)}");
	}

	@Test
	public void intersectConicConicInOnePoint() {
		tRound("{Intersect[(x + 3.04)^2 + (y + 0.11)^2 = 144,"
						+ "(x - 4.16)^2 + (y + 0.11)^2 = 23.04]}",
				"{(8.96, -0.11)}");
	}

	@Test
	public void cmdSetConstructionStep() {
		app.setSaved();
		assertTrue(app.clearConstruction());
		t("cs=ConstructionStep[]", "1");
		t("2", "2");
		t("7", "7");
		t("SetConstructionStep[2]");
		t("cs", "2");
		t("SetConstructionStep[1]");
		t("cs", "1");
		assertTrue(app.clearConstruction());
	}

	@Test
	public void testBoolean() {
		t("true" + Unicode.XOR + "true", "false");
		t("true" + Unicode.XOR + "false", "true");
		t("false" + Unicode.XOR + "true", "true");
		t("false" + Unicode.XOR + "false", "false");
		t("true" + Unicode.IMPLIES + "true", "true");
		t("true" + Unicode.IMPLIES + "false", "false");
		t("false" + Unicode.IMPLIES + "true", "true");
		t("false" + Unicode.IMPLIES + "false", "true");
		t("true" + Unicode.OR + "true", "true");
		t("true" + Unicode.OR + "false", "true");
		t("false" + Unicode.OR + "true", "true");
		t("false" + Unicode.OR + "false", "false");
		t("true" + Unicode.AND + "true", "true");
		t("true" + Unicode.AND + "false", "false");
		t("false" + Unicode.AND + "true", "false");
		t("false" + Unicode.AND + "false", "false");
		t(Unicode.NOT + "true", "false");
		t(Unicode.NOT + "false", "true");
	}

	@Test
	public void parametricSyntaxes() {
		t("X=(s,2s)", "X = (s, (2 * s))");
		t("Intersect[X=(s,s),x+y=2]", "(1, 1)");
	}

	private static void ti(String in, String out) {
		testSyntax(in.replace("i", Unicode.IMAGINARY + ""),
				AlgebraTestHelper.getMatchers(out.replace("i", Unicode.IMAGINARY + "")),
				app,
				ap, StringTemplate.xmlTemplate);
	}

	@Test
	public void complexArithmetic() {
		ti("(0i)^2", "0i");
		ti("(0i)^0", "?");
		ti("(0i)^-1", "?");
		ti("(2+0i)^0", "1 + 0i");
		ti("(1/0+0i)^0", "?");
	}

	@Test
	public void redefine() {
		t("la={1}", "{1}");
		t("lb={2}", "{2}");
		t("lc=la", "{1}");
		t("lc=lb", "{2}");
		t("1*lb", "{2}");
	}

	@Test
	public void parsePower() {
		t("a=4", "4");
		t("pia", "12.566370614359172");
		t("pi1", "3.141592653589793");
		t("pi1a", "12.566370614359172");
		t("pie", "8.539734222673566");
		t("pii", "3.141592653589793ί");
		t("pix", "(pi * x)");
		t("sinx", "sin(x)");
		t("sin x", "sin(x)");
		t("f(" + Unicode.theta_STRING + ")=sin " + Unicode.theta_STRING,
					"sin(" + Unicode.theta_STRING + ")");
		t("f(" + Unicode.theta_STRING + ")=sin" + Unicode.theta_STRING,
				"sin(" + Unicode.theta_STRING + ")");
		t("f(t)=sin t", "sin(t)");
		t("f(t)=sint", "sin(t)");
		t("x" + Unicode.PI_STRING, "(x * pi)");
		t("xdeg", "x" + DEGREE_STRING);
		t("sinxdeg", "sin(x" + DEGREE_STRING + ")");
	}

	private static String indices(String string) {
		return string.replace("^2", Unicode.SUPERSCRIPT_2 + "");
	}

	private static String eval(String string) {
		return ap.evaluateToGeoElement(string, true)
				.toValueString(StringTemplate.editTemplate);
	}

	private static void platonicTest(String string, int deg, String[] dodeca) {
		tRound(string + "[(1;" + deg + "deg),(0,0)]", dodeca);
		tRound(string + "[(1;" + deg + "deg),(0,0),(1,0)]", dodeca);
		String[] dodeca1 = new String[dodeca.length + 1];
		dodeca1[0] = dodeca[0];
		dodeca1[1] = "(1, 0, 0)";
		System.arraycopy(dodeca, 1, dodeca1, 2, dodeca1.length - 2);
		tRound(string + "[(1;" + deg + "deg),(0,0),Vector[(0,0,1)]]", dodeca1);
	}

	@Test
	public void testExpIntegral() {
		tRound("expIntegral(5)", "40.18528");
		tRound("expIntegral(5+0i)", "40.18528 + 0" + Unicode.IMAGINARY);
	}

	@Test
	public void testInverseTrigDegree() {
		tRound("asind(0.5)", "30\u00B0");
		tRound("acosd(0.5)", "60\u00B0");
		tRound("atand(1)", "45\u00B0");
		tRound("asind(0.317)", "18.48159\u00B0");
		tRound("acosd(0.317)", "71.51841\u00B0");
		tRound("atand(0.317)", "17.58862\u00B0");
	}

	@Test
	public void testIndexLookup() {
		t("aa_{1}=1", "1");
		t("aa_{1}+1", "2");
		t("aa_1+1", "2");
		t("ab_1=1", "1");
		t("ab_{1}+1", "2");
		t("ab_1+1", "2");
		// overwrite
		t("ab_1=3", "3");
		t("ab_{1}+1", "4");
	}

	@Test
	public void testShorthandIntersect() {
		t("x=2*y=3*z", "X = (0, 0, 0) + " + Unicode.lambda + " (6, 3, 2)");
		t("(x=2y,2y=3z)", "X = (0, 0, 0) + " + Unicode.lambda + " (6, 3, 2)");
		tRound("x-1=y+2=z-6", "X = (-2.33333, -5.33333, 2.66667) + "
				+ Unicode.lambda + " (1, 1, 1)");
		tRound("(x-1)/3=(y+2)/2=5(z-6)", "X = (-60.47239, -42.9816, 1.90184) + "
				+ Unicode.lambda + " (2.5, 1.66667, 0.16667)");
		tRound("1-x=y+2=z-6", "X = (4.33333, -5.33333, 2.66667) + "
				+ Unicode.lambda + " (1, -1, -1)");
		tRound("x+x-1=y+y+2=z-6+z", "X = (-1.16667, -2.66667, 1.33333) + "
				+ Unicode.lambda + " (4, 4, 4)");
	}

	@Test
	public void zipReloadTest() {
		t("list1=Zip[f(1),f,{x,x+1}]", "{1, 2}");
		String xml = app.getGgbApi().getXML();
		t("list2=Zip[f(1,2),f,{x+y,y+x+1}]", "{3, 4}");
		t("list3=Zip[f(1),f,{Curve(p,p+1,p,0,2)}]", "{(1, 2)}");
		app.getKernel().clearConstruction(true);
		app.getGgbApi().setXML(xml);
		t("list1", "{1, 2}");
		t("Object[\"list2\"]", "NaN");
	}

	@Test
	public void testComplexFunctions() {
		t("f(x)=x^2", "x^(2)");
		tRound("f(i)", "-1 + 0" + Unicode.IMAGINARY);
		t("f((3,4))", "9");
		t("g: x > 1", "x > 1");
		t("g((3,4))", "true");
	}

	@Test
	public void testRootsRedefine() {
		tRound("Roots[sin(x),-1,4]", "(0, 0)", "(3.14159, 0)");
		tRound("A", "(0, 0)");
		tRound("B", "(3.14159, 0)");
		tRound("A = Roots[sin(x),-1,4.2]",
				"(0, 0)", "(3.14159, 0)");
		tRound("A", "(0, 0)");
		tRound("Object[\"B\"]", "(3.14159, 0)");
	}

	private static void prob(String cmd, String params, String pdf,
			String cdf) {
		prob(cmd, params, pdf, cdf, -5);
	}

	private static void prob(String cmd, String params, String pdf, String cdf,
			int skip) {
		app.getKernel().getConstruction().setFileLoading(false);
		tRound("cdf1=" + cmd + "(" + params + ",x)", unicode(cdf));
		app.getKernel().getConstruction().setFileLoading(true);
		tRound("pdf1=" + cmd + "(" + params + ",x)", unicode(pdf));
		app.getKernel().getConstruction().setFileLoading(false);
		tRound("pdf=" + cmd + "(" + params + ",x,false)", unicode(pdf));
		tRound("cdf=" + cmd + "(" + params + ",x,true)", unicode(cdf));
		for (int i = -1; i < 5; i++) {
			t("cdf(" + i + ")==" + cmd + "(" + params + "," + i + ",true)",
					"true");
			if (i == skip) {
				t("!IsDefined(pdf(" + i + ")) && !IsDefined(" + cmd + "("
						+ params + "," + i + ",false))", "true");
			} else {
				t("pdf(" + i + ")==" + cmd + "(" + params + "," + i + ",false)",
						"true");

			}
		}
	}

	private static void intProb(String cmd, String args, String val, String pf,
			String cdf) {
		t("ZoomIn[0,0,100,100]");
		tRound(cmd + "(" + args + "," + val + ",false)", pf);
		tRound(cmd + "(" + args + "," + val + ",true)", cdf);

		tRound(cmd + "(" + args + ")", "1");
		tRound(cmd + "(" + args + ",false)", "1");

		tRound(cmd + "(" + args + ",true)>1", "true");
	}

	@Test
	public void yLHSFunctions() {
		t("f:y=sin(x)", "sin(x)");
		assertEquals(GeoClass.FUNCTION, get("f").getGeoClassType());
		t("SetValue(f, x^2)");
		assertEquals(GeoClass.FUNCTION, get("f").getGeoClassType());
		assertEquals(GeoClass.FUNCTION, get("f").getGeoClassType());
	}

	@Test
	public void yLHSImplicitCurves() {
		t("f:y^2=sin(x)", "y^(2) = sin(x)");
		assertEquals(GeoClass.IMPLICIT_POLY, get("f").getGeoClassType());
		t("SetValue(f, y = sin(x))");
		assertEquals(GeoClass.IMPLICIT_POLY, get("f").getGeoClassType());
		app.setXML(app.getXML(), true);
		assertEquals(GeoClass.IMPLICIT_POLY, get("f").getGeoClassType());
	}

	@Test
	public void yLHSLines() {
		t("l: y = 2x", "y = 2x");
		assertEquals(GeoClass.LINE, get("l").getGeoClassType());
		t("SetValue(l, y = 2x - 3)");
		assertEquals(GeoClass.LINE, get("l").getGeoClassType());
		app.setXML(app.getXML(), true);
		assertEquals(GeoClass.LINE, get("l").getGeoClassType());
	}

	@Test
	public void yLHSConics() {
		t("c: y = 2x^2", "y = 2x²");
		assertEquals(GeoClass.CONIC, get("c").getGeoClassType());
		t("SetValue(c, y = 2x - 3)");
		assertEquals(GeoClass.CONIC, get("c").getGeoClassType());
		app.setXML(app.getXML(), true);
		assertEquals(GeoClass.CONIC, get("c").getGeoClassType());
	}

	@Test
	public void yLHSPlanes() {
		t("a: y = 2x + 3z", "-2x + y - 3z = 0");
		assertEquals(GeoClass.PLANE3D, get("a").getGeoClassType());
		t("SetValue(a, y = 2x - 3z + 4)");
		assertEquals(GeoClass.PLANE3D, get("a").getGeoClassType());
		app.setXML(app.getXML(), true);
		assertEquals(GeoClass.PLANE3D, get("a").getGeoClassType());
	}

	@Test
	public void yLHSQuadrics() {
		t("q: y = 2x^2 + 3z", "-2x² + 0z² + y - 3z = 0");
		assertEquals(GeoClass.QUADRIC, get("q").getGeoClassType());
		t("SetValue(q, y = 2x² - 3z² + 4)");
		assertEquals(GeoClass.QUADRIC, get("q").getGeoClassType());
		app.setXML(app.getXML(), true);
		assertEquals(GeoClass.QUADRIC, get("q").getGeoClassType());
	}

	@Test
	public void plusMinus() {
		tpm("1pm2", "{3, -1}");
		tpm("1pm2pm4", "{7, -5}");
		tpm("pm2", "{2, -2}");
		tpm("pmx", "{x, (-x)}");
		tpm("x+(pm2)", "{x + 2, x - 2}");
		tpm("xpm2", "{x + 2, x - 2}");
		tpm("xpm(pm2)", "{x + 2, x - (-2)}");
		t("mul=4", "4");
		tpm("prod=pm mul 3", "{12, -12}");
		assertEquals("(" + Unicode.PLUSMINUS + "mul) * 3",
				get("prod").getDefinition(StringTemplate.editTemplate));
		tpm("prod2=pm sqrt 4", "{2, -2}");
		assertEquals(Unicode.PLUSMINUS + "sqrt(4)",
				get("prod2").getDefinition(StringTemplate.editTemplate));
	}

	@Test
	public void expandFunctions() {
		t("f(x,y)=x+y", "x + y");
		t("a:x + 1 / f", "x + 1 / (x + y)");
		t("a(1,3)", "1.25");
	}

	@Test
	public void testProduct() {
		t("a=1", "1");
		t("b=1", "1");
		t("ab+1", "2");
		tRound("ab" + Unicode.pi + "+1", "4.14159");
	}

	@Test
	public void expandFunctionsLine() {
		t("f(x,y)=2x+0y", "(2 * x) + (0 * y)");
		t("g(x,y)=0x+3y", "(0 * x) + (3 * y)");
		// t("2f+3g", "(2 * ((2 * x) + (0 * y))) + (3 * ((0 * x) + (3 * y)))");
		t("2f+3g=36", "4x + 9y = 36");
	}

	private static void tpm(String string, String expected) {
		t(string.replace("pm", Unicode.PLUSMINUS + ""), expected);
	}

	@Test
	public void expandedFractionIsNotUsedForEvaluation() {
		t("a=(1+1/143)^143", "2.7088378687594363");
		((GeoNumeric) get("a")).setSymbolicMode(true, true);
		t("a", "2.708837868759473");

		t("b=(1+1/400)^400", "2.714891744381287");
		((GeoNumeric) get("b")).setSymbolicMode(true, true);
		t("b", "2.7148917443812293");
	}

	@Test
	public void numIntTest() {
		t("F(t,x)=NIntegral(sin(x)+sin(t-x), x)",
				"NIntegral[sin(x) + sin(t - x), x]");
		((FunctionalNVar) get("F")).setSecret(null);
		assertEquals("-cos(x) - (-cos(t - x))",
				get("F").toValueString(StringTemplate.testTemplate));
	}

	@Test
	public void testPointsFromList() {
		t("Sequence(Segment(Point({0, n}), Point({1, n+0})), n, 0, 9, 1)",
				"{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}");
	}

	@Test
	public void xCurve() {
		t("f:(cos(t),sin(t+1))", "(cos(t), sin(t + 1))");
		t("x(f)", "x(f(t))");
	}

	private static String complex(String string) {
		return string.replaceAll("i", Unicode.IMAGINARY + "");
	}

	@Test
	public void cmdAffineRatio() {
		t("AffineRatio[ (1,1), (2,1/2), (3,1/3) ]", "NaN");
		t("AffineRatio[ (-1, 1), (1, 1), (4, 1) ]", "2.5");
	}

	@Test
	public void cmdAngle() {
		t("Angle[ x+y=17, x=4 ]", "315*" + DEGREE_STRING);
		t("Angle[ (1,1) ]", "45*" + DEGREE_STRING);
		t("Angle[ (1,1), (3,1/3), (2,1/2) ]", "8.972626614896395*" + DEGREE_STRING);
		t("Angle[ (1,1), (4,1/4), 30" + DEGREE_STRING + " ]", "30*" + DEGREE_STRING,
				"(1.026923788646684, -0.6004809471616708)");
		tRound("Angle[ Segment[(1,1),(2,1/2)], x+y=17 ]", "341.56505" + DEGREE_STRING);
		t("Angle((1, -1, 0),(0, 0, 0),(-1, -1, 0), zAxis)", "270*" + DEGREE_STRING);
	}

	@Test
	public void cmdAngularBisector() {
		t("anbisA=AngleBisector[ x+y=17, x=4 ]", "0.9238795325112867x + "
						+ "0.3826834323650897y = 8.670402750791315" ,
				"-0.3826834323650897x + 0.9238795325112867y = 10.47970019318637");
		t("anbisB=AngleBisector[ (1,1),(2,1/2),(3,1/3) ]", "-0.9509826718461247x +"
				+ " 0.3092441718907662y = -1.7473432577468664");
	}

	@Test
	public void cmdANOVA() {
		t("ANOVA[ {1,2,3,4,5}, {2,3,4}]", "{1, 0}");
	}

	@Test
	public void cmdAppend() {
		t("Append[ {1,2,3,4,5}, Polygon[(1,1),(2,1/2),4] ]", "{1, 2, 3, 4, 5, 1.25}");
		t("Append[ Polygon[(1,1),(2,1/2),4],  {1,2,3,4,5} ]", "{1.25, 1, 2, 3, 4, 5}");
	}

	@Test
	public void cmdApplyMatrix() {
		t("ApplyMatrix[ {{1,2},{3,4}}, Polygon[(1,1),(2,1/2),4] ]", "2.5");
		t("ApplyMatrix[ {{2,0,0},{0,3,0},{0,0,4}}, Sphere((0,0,1),1) ]",
				"0.25x² + 0.1111111111111111y² + 0.0625z² - 0.5z = 0");
		tRound("Coefficients[ApplyMatrix[ {{0,1},{-1,0}}, 2x+y+0z=1 ]] * sqrt(5)",
				"{-1, 2, 0, 1}");
	}

	@Test
	public void cmdArc() {
		t("Arc[ x^2+y^2=1, 0.05, 0.5 ]", "0.45");
		t("Arc[ x^2+y^2=1, (1,1),(2,1/2) ]", "5.742765806909002");
		t("Arc[ x^2+y^2/2=1, 0.05, 0.5 ]", "0.4691535414499456");
		t("Arc[ x^2+y^2/2=1, (1,1),(2,1/2) ]", "7.054542994541888");
	}

	@Test
	public void cmdArea() {
		t("Area[ x^2+y^2=1 ]", "3.141592653589793");
		t("Area[ Polygon[(1,1),(2,1/2),(3,1/3)] ]", "0.16666666666666652");
		t("Area[ (1,1), (3,1/3), (1,1) ]", "0");
	}

	@Test
	public void cmdAreCollinear() {
		t("AreCollinear[ (1,1),(2,1/2),(3,1/3) ]", "false");
	}

	@Test
	public void cmdAreConcurrent() {
		t("AreConcurrent[x+y=17,x=4, x=0]", "false");
	}

	@Test
	public void cmdAreCongruent() {
		t("AreCongruent[Segment[(0,1),(1,0)],Segment[(1,0),(0,1)]]", "true");
		t("AreCongruent[Segment[(0,1),(1,0)],Segment[(-1,0),(0,-1)]]", "true");
		t("AreCongruent[Segment[(0,1),(1,0)],Segment[(2,0),(0,2)]]", "false");
		t("AreCongruent(Polygon((0,0),(2,0),(2,3),(1,4),(-1,4)),"
				+ "Polygon((1,4),(-1,4),(0,0),(2,0),(2,3)))", "true");
		t("AreCongruent(Polygon((0,0),(2,0),(2,3),(1,4),(-1,4)),"
				+ "Polygon((2,3),(2,0),(0,0),(-1,4),(1,4)))", "true");
	}

	@Test
	public void cmdAreConcyclic() {
		t("AreConcyclic[ (1,1),(2,1/2),(3,1/3),(4,1/4) ]", "false");
	}

	@Test
	public void cmdAreEqual() {
		t("AreEqual[(1,1),(2,1/2)]", "false");
	}

	@Test
	public void cmdAreParallel() {
		t("AreParallel[ x+y=17,x=4 ]", "false");
	}

	@Test
	public void cmdArePerpendicular() {
		t("ArePerpendicular[ x+y=17,x=4 ]", "false");
	}

	@Test
	public void cmdAttachCopyToView() {
		t("AttachCopyToView[(1,1),1]", "(1, 1)");
		t("AttachCopyToView[(1,1), 2, (2,1/2), (3,1/3), (123,0), (0,123)]",
				"(0.6200000000000008, 13.680000000000005)");
	}

	@Test
	public void cmdAxes() {
		t("Axes[ x^2+y^2=1 ]", "y = 0", "x = 0");
	}

	@Test
	public void cmdAxisStepX() {
		t("AxisStepX[]", "2000");
	}

	@Test
	public void cmdAxisStepY() {
		t("AxisStepY[]", "2000");
	}

	@Test
	public void cmdBarChart() {
		t("BarChart[ {2,3,4}, {2,3,4} ]", "9");
		t("BarChart[ {2,3,4}, {2,3,4}, 4 ]", "36");
		t("BarChart[ {2,3,4}, 4 ]", "12");
		t("BarChart[ 42, 50, {1,2,3,4,5} ]", "24");
		t("BarChart[ 42, 50, t^2, t, 4, 4 ]", "128");
		t("BarChart[ 42, 50, t^2, t, 4, 4, 0.05 ]", "128");
	}

	@Test
	public void cmdBarycenter() {
		t("Barycenter[{(1,1),(2,1/2),(3,1/3)},{42,4,13}]", "(1.5084745762711864,"
				+ " 0.8192090395480226)");
	}

	@Test
	public void cmdBernoulli() {
		t("Bernoulli[ 0.7, false ]", "1");
		t("Bernoulli[ 0.7, true ]", "Infinity");
	}

	@Test
	public void cmdBeta() {
		t("beta(-1.1,-3.1)", "-88.36531346708531");
	}

	@Test
	public void cmdBinomial() {
		t("BinomialCoefficient[ 5, -1 ]", "0");
		t("BinomialCoefficient[ 5, 1 ]", "5");
	}

	@Test
	public void cmdBinomialDist() {
		intProb("BinomialDist", "11, 0.5", "5", "0.22559", "0.5");
	}

	@Test
	public void cmdBottom() {
		t("Bottom[Cone[x^2+y^2=9,4]]",
				"X = (0, 0, 0) + (3 cos(t), -3 sin(t), 0)");
	}

	@Test
	public void cmdBoxPlot() {
		t("BoxPlot[ 4, 50, {2,3,4} ]", "3");
		t("BoxPlot[ 4, 50, {2,3,4}, true ]", "3");
		t("BoxPlot[ 4, 50, {2,3,4}, {1,2,3,4,5}, false ]", "NaN");
		t("BoxPlot[ 50, 42, 42, 4, 42, 13, 50 ]", "42");
	}

	@Test
	public void cmdButton() {
		t("Button[ ]", "");
		t("Button[ \"GeoGebra\" ]", "");
	}

	@Test
	public void cmdCauchy() {
		prob("Cauchy", "2,1", "abs(1) / ((1^2 + (x - 2)^2) " + Unicode.pi + ")",
				"tan" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING
						+ "((x - 2) / abs(1)) / " + Unicode.pi + " + 0.5");
	}

	@Test
	public void cmdCell() {
		t("Cell[ 42, 4 ]", "NaN");
	}

	@Test
	public void cmdCellRange() {
		t("CellRange[ A1, A1 ]", "{}");
	}

	@Test
	public void cmdCenter() {
		t("Center[ x^2+y^2=1 ]", "(0, 0)");
	}

	@Test
	public void cmdCentroid() {
		t("Centroid[ Polygon[(1,1),(2,1/2),(3,1/3)] ]", "(2, 0.6111111111111113)");
	}

	@Test
	public void cmdCheckbox() {
		t("Checkbox[]", "true");
		t("Checkbox[ {1,2,3,4,5} ]", "true");
		t("Checkbox[ \"GeoGebra\" ]", "true");
		t("Checkbox[ \"GeoGebra\", {1,2,3,4,5} ]", "true");
	}

	@Test
	public void cmdChiSquared() {
		prob("ChiSquared", "2", "If(x < 0, 0, (" + Unicode.EULER_STRING
						+ "^((-x) / 2) x^(2 / 2 - 1)) / (2^(2 / 2) gamma(2 / 2)))",
				"If(x < 0, 0, gamma(2 / 2, x / 2) / gamma(2 / 2))");
	}

	@Test
	public void cmdChiSquaredTest() {
		t("ChiSquaredTest[{{1,2,3,4,5}}]", "?");
		t("ChiSquaredTest[{1,2,3,4,5},{1,2,3,4,5}]", "{1, 0}");
		t("ChiSquaredTest[{{1,2,3,4,5}},{{1,2,3,4,5}}]", "?");
	}

	@Test
	public void cmdCircleArc() {
		t("CircularArc[ (5,1/5), (1,1),(2,1/2) ]", "0.39864912610014436");
	}

	@Test
	public void cmdCircle() {
		t("Circle[ (1,1), 42 ]", "(x - 1)² + (y - 1)² = 1764");
		t("Circle[ (1,1),(2,1/2) ]", "(x - 1)² + (y - 1)² = 1.2500000000000002");
		t("Circle[ (1,1),(2,1/2),(3,1/3) ]", "(x - 3.083333333333333)² + (y -"
				+ " 3.916666666666666)² = 12.847222222222216");
		t("Circle[ (1,1), Segment[(1,1),(2,1/2)] ]", "(x - 1)² + (y - 1)² "
				+ "= 1.2500000000000002");
	}

	@Test
	public void cmdCircle3D() {
		// first check with 2D view active
		t("Circle[ (1,1,0), 42 ]", "X = (1, 1, 0) + (42 cos(t), 42 sin(t), 0)");
		app.setActiveView(App.VIEW_EUCLIDIAN3D);
		// first check with 2D view active
		t("Circle[ (1,1,0), 42 ]", "X = (1, 1, 0) + (42 cos(t), 42 sin(t), 0)");
	}

	@Test
	public void cmdCircleSector() {
		t("CircularSector[ (5,1/5), (1,1),(2,1/2) ]", "0.8130878692245387");
	}

	@Test
	public void cmdCircumcircleArc() {
		t("CircumcircularArc[ (1,1),(2,1/2),(3,1/3) ]", "2.139820751140848");
	}

	@Test
	public void cmdCircumcircleSector() {
		t("CircumcircularSector[ (1,1),(2,1/2),(3,1/3) ]", "3.834882107183551");
	}

	@Test
	public void cmdConic() {
		areEqual("Conic[ {42,4,13,50,5,7} ]", "42x² + 50x y + 4y² + 5x + 7y = -13");
		areEqual("Conic[ {42,4,13,50,5,7} ]", "42x² + 50x y + 4y² + 5x + 7y = -13");
		areEqual("Conic[ {2, 3, -1, 4, 2, -3} ]", "2x² + 4x y + 3y² + 2x - 3y = 1");
		areEqual("Conic[ (1,1),(2,1/2),(3,1/3),(4,1/4),(5,1/5) ]",
				"2.56x y = 2.56");
		areEqual("Conic[ (0, -4), (2, 4), (3,1), (-2,3), (-3,-1)]",
				"-151x² + 37xy - 14x - 72y² + 42y + 1320 = 0");
		areEqual("Conic[ 2, 3, -1, 4, 2, -3 ]", "2x² + 4x y + 3y² + 2x - 3y = 1");

	}

	private void areEqual(String command1, String command2) {
		t("AreEqual[" + command1 + ", " + command2 + "]", "true");
	}

	@Test
	public void testConic5() {
		GeoPoint[] points = {
				newPoint(-0.25558616768332837, 0),
				newPoint(-0.24401517687241298, 0.09869574035418058),
				newPoint(-0.31884715907669187, 0.16407854720571086),
				newPoint(-0.4410243233086352, 0.14553384365563557),
				newPoint(-0.35858986503875784, -0.06827447415125582)
		};
		AlgoConicFivePoints algo = new AlgoConicFivePoints(app.getKernel().getConstruction(),
				points);
		GeoConicND conic = algo.getConic();
		assertThat(conic, isDefined());
	}

	private GeoPoint newPoint(double x, double y) {
		return new GeoPoint(app.getKernel().getConstruction(), x, y, 0.001);
	}

	@Test
	public void cmdCircumference() {
		t("Circumference[ x^2+y^2=1 ]", "6.283185307179586");
	}

	@Test
	public void cmdComplexRoot() {
		tRound("Sort({ComplexRoot(x^6 + 7x^3 - 8)})",
				complex("{-2 + 0i, -0.5 - 0.86603i,"
						+ " -0.5 + 0.86603i, 1 - 1.73205i, 1 + 0i, 1 + 1.73205i}"));
		t("ComplexRoot( x^2 )", complex("0i"));
	}

	@Test
	public void cmdConstructionStep() {
		t("ConstructionStep[]", "1");
		t("ConstructionStep[ Polygon[(1,1),(2,1/2),4] ]", "0");
	}

	@Test
	public void cmdConvexHull() {
		t("ConvexHull[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]",
				"ConvexHull[{(1, 1), (2, 2), (3, 3), (4, 1 / 4), (5, 1 / 5)}]");
	}

	@Test
	public void cmdCone() {
		tRound("Cone[x^2+y^2=9,4]",
				eval("12*pi"), "X = (0, 0, 4)",
				eval("pi*15"));
		tRound("Cone[(0,0,0),(0,0,4),3]", eval("12*pi"),
				"X = (0, 0, 0) + (3 cos(t), -3 sin(t), 0)", eval("pi*15"));
		tRound("Cone[(0,0,0),Vector[(0,0,4)],pi/4]",
				indices("x^2 + y^2 - 1z^2 = 0"));
	}

	@Test
	public void cmdConeInfinite() {
		tRound("InfiniteCone[(1,1),(1,1,2),45deg]",
				indices("x^2 + y^2 - 1z^2 - 2x - 2y = -2"));
		tRound("InfiniteCone[(1,1),Vector[(0,0,2)],45deg]",
				indices("x^2 + y^2 - 1z^2 - 2x - 2y = -2"));
		tRound("InfiniteCone[(1,1),xAxis,45deg]",
				indices("-1x^2 + y^2 + z^2 + 2x - 2y = 0"));
	}

	@Test
	public void cmdContingencyTable() {
		String column = "\\text{\\text{Column \\%}}&\\;&\\;&\\;\\\\";
		String pct = "\\;&100&50&66.67\\\\";
		String pctM = "\\;&0&50&33.33\\\\";
		String table = "\\begin{array}{|l|r|r||r|}\\hline "
				+ "\\text{\\text{Frequency}}&\\text{L}&\\text{R}&\\text{Total}\\\\"
				+ column + "\\hline \\text{F}&1&1&2\\\\" + pct
				+ "\\hline \\text{M}&0&1&1\\\\" + pctM
				+ "\\hline \\hline \\text{Total}&1&2&3\\\\\\hline \\end{array}";
		t("ContingencyTable[ {\"M\",\"F\",\"F\"},{\"R\",\"R\",\"L\"}]",
				table.replace(column, "").replace(pct, "").replace(pctM, ""));
		t("ContingencyTable[ {\"M\",\"F\",\"F\"},{\"R\",\"R\",\"L\"} ,\"|\"]",
				table);
		t("ContingencyTable[ {\"F\",\"M\"},{\"L\",\"R\"},{{1,1},{0,1}} ]",
				table.replace(column, "").replace(pct, "").replace(pctM, ""));
		t("ContingencyTable[ {\"F\",\"M\"},{\"L\",\"R\"},{{1,1},{0,1}},\"|\"]",
				table);
	}

	@Test
	public void cmdCross() {
		t("Cross[(0,0,1),(1,0,0)]", "(0, 1, 0)");
		t("Cross[(0,0,1),(0,1,0)]", "(-1, 0, 0)");
		t("Cross[(0,0,1),(0,0,1)]", "(0, 0, 0)");
		t("Cross[(0,1),(2,0)]", "-2");
		t("Cross[(0,1),(0,2)]", "0");
	}

	@Test
	public void cmdCube() {
		t("Cube[(0,0,0),(0,0,2)]",
				"8", "(2, 0, 0)", "(0, 2, 0)", "(0, 2, 2)",
				"(2, 2, 2)", "(2, 2, 0)", "4", "4", "4", "4", "4", "4",
				"2", "2", "2", "2", "2", "2", "2", "2", "2", "2", "2",
				"2");
		t("Cube[(0,0,0),(0,2,0),(0,2,2)]",
				"8", "(0, 0, 2)", "(2, 0, 0)", "(2, 2, 0)",
				"(2, 2, 2)", "(2, 0, 2)", "4", "4", "4", "4", "4", "4",
				"2", "2", "2", "2", "2", "2", "2", "2", "2", "2", "2",
				"2");
		t("Cube[(0,0,0),(0,0,2),xAxis]",
				"8", "(0, -2, 2)", "(0, -2, 0)", "(2, 0, 0)",
				"(2, 0, 2)", "(2, -2, 2)", "(2, -2, 0)", "4", "4", "4",
				"4", "4", "4", "2", "2", "2", "2", "2", "2", "2", "2",
				"2", "2", "2", "2");
	}

	@Test
	public void cmdCylinder() {
		tRound("Cylinder[x^2+y^2=9,4]", eval("36*pi"),
				"X = (0, 0, 4) + (3 cos(t), 3 sin(t), 0)", eval("pi*24"));
		tRound("Cylinder[(0,0,0),(0,0,4),3]", eval("36*pi"),
				"X = (0, 0, 0) + (3 cos(t), -3 sin(t), 0)",
				"X = (0, 0, 4) + (3 cos(t), 3 sin(t), 0)", eval("pi*24"));
		tRound("Cylinder[(0,0,0),Vector[(0,0,4)],1]",
				indices("x^2 + y^2 + 0z^2 = 1"));
	}

	@Test
	public void cmdCylinderInfinite() {
		tRound("InfiniteCylinder[(1,1),(1,1,2),1]",
				indices("x^2 + y^2 + 0z^2 - 2x - 2y = -1"));
		tRound("InfiniteCylinder[(1,1),Vector[(0,0,2)],1]",
				indices("x^2 + y^2 + 0z^2 - 2x - 2y = -1"));
		tRound("InfiniteCylinder[xAxis,1]", indices("y^2 + z^2 = 1"));
	}

	@Test
	public void cmdClasses() {
		t("Classes[ {2,3,4}, 42 ]",
				"{2, 2.0476190476190474, 2.0952380952380953, 2.142857142857143,"
						+ " 2.1904761904761907, 2.238095238095238, 2.2857142857142856,"
						+ " 2.3333333333333335, 2.380952380952381, 2.4285714285714284,"
						+ " 2.4761904761904763, 2.5238095238095237, 2.571428571428571,"
						+ " 2.619047619047619, 2.6666666666666665, 2.7142857142857144,"
						+ " 2.761904761904762, 2.8095238095238093, 2.857142857142857,"
						+ " 2.9047619047619047, 2.9523809523809526, 3, 3.0476190476190474,"
						+ " 3.095238095238095, 3.142857142857143, 3.1904761904761907,"
						+ " 3.238095238095238, 3.2857142857142856, 3.333333333333333,"
						+ " 3.380952380952381, 3.4285714285714284, 3.4761904761904763,"
						+ " 3.5238095238095237, 3.571428571428571, 3.619047619047619,"
						+ " 3.6666666666666665, 3.7142857142857144, 3.761904761904762,"
						+ " 3.8095238095238093, 3.8571428571428568, 3.9047619047619047,"
						+ " 3.9523809523809526, 4}");
		t("Classes[ {2,3,4}, 13, 4 ]", "?");
	}

	@Test
	public void cmdClosestPoint() {
		t("ClosestPoint[ x^2+y^2=1, (1,1) ]", "(0.7071067811865476, 0.7071067811865475)");
		t("ZoomIn(-5,-5,5,5)");
		t("ClosestPoint[ x⁴ + 2x² y² - 4x² + 3.6x + y⁴ - 4y² = 0.81, (0.55708, -0.2547)]",
				"(0.5416458135538293, -0.12427455943331392)");
		t("ClosestPoint[ x⁴ + 2x² y² - 4x² + 3.6x + y⁴ - 4y² = 0.81, (0.55708, 0.20383)]",
				"(0.547622890963136, 0.12372237646614026)");
		t("ClosestPoint[ Polygon[(1,1),(2,1/2),(3,1/3)], (1,1) ]", "(1, 1)");
		t("ClosestPoint[ xAxis, yAxis ]", "(0, 0)");
		tRound("ClosestPoint[ sqrt(1/x), (-1,3) ]", "(0.1, 3.16228)");
		tRound("ClosestPoint[ sqrt(sin(x)), (-1,0) ]", "(0, 0)");
	}

	@Test
	public void cmdCoefficients() {
		t("Coefficients[ x^2+y^2=1 ]", "{1, 1, -1, 0, 0, 0}");
		t("Coefficients[ x^2 ]", "{1, 0, 0}");
		t("Coefficients[ 2x+3y+4z=5 ]", "{2, 3, 4, -5}");
		t("Coefficients[ Fit({(0,0),(1,1),(2,4)}, {x^2,sin(x),x}) ]", "{1, 0, 0}");
		t("Coefficients[7x^3+sin(x)]", "{7, 3}");
	}

	@Test
	public void cmdColumn() {
		t("Column[ A1 ]", "1");
	}

	@Test
	public void cmdColumnName() {
		t("ColumnName[ A1 ]", "A");
	}

	@Test
	public void cmdCompleteSquare() {
		t("CompleteSquare[ x^2 ]", "(x)^(2)");
		t("CompleteSquare[ x^2 + 2x + 2 ]", "(x + 1)^(2) + 1");
	}

	@Test
	public void cmdCopyFreeObject() {
		t("CopyFreeObject[ Polygon[(1,1),(2,1/2),4] ]", "1.25");
	}

	@Test
	public void cmdCorner() {
		// don't test this
	}

	@Test
	public void cmdCountIf() {
		t("CountIf[ x>3, {1,2,3,4,5} ]", "2");
		t("CountIf[ A>3,A, {1,2,3,4,5} ]", "2");
	}

	@Test
	public void cmdCovariance() {
		t("Covariance[ {1,2,3,4,5}, {1,2,3,4,5} ]", "2");
		t("Covariance[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]",
				"-0.6699999999999999");
	}

	@Test
	public void cmdCrossRatio() {
		t("CrossRatio[ (1,1), (2,1/2), (3,1/3), (4,1/4) ]", "NaN");
		t("CrossRatio[ (-1, 1), (1, 1), (3, 1), (4, 1) ]", "1.2");
	}

	@Test
	public void cmdCurvature() {
		tRound("Curvature[ (1,1), Curve[sin(t),cos(t),t,0,3] ]", "-1");
		t("Curvature[ (1,1), sin(x) ]", "-0.5730366435551724");
		tRound("Curvature[ (1,1), circle[(2, 3),2] ]", "0.5");
	}

	@Test
	public void cmdCurvatureVector() {
		tRound("CurvatureVector[ (1,1), Curve[sin(t),cos(t),t,0,3] ]", "(-0.70711, -0.70711)");
		t("CurvatureVector[ (1,1), sin(x) ]", "(NaN, NaN)");
		tRound("CurvatureVector[ (1,1), Circle[(2, 3),2] ]", "(0.22361, 0.44721)");
	}

	@Test
	public void cmdCurveCartesian() {
		t("Curve[ t^2, t^2, t, 42, 50 ]", "(t^(2), t^(2))");
	}

	@Test
	public void cmdCubic() {
		t("Cubic[ (1,1),(2,1/2),(3,1/3),42 ]",
				"-12817.721707818935x^3 - 86298.35010859629x^2 y + 122469.63661217807x^2 -"
						+ " 129048.41967687852x y^2 + 481421.6333812809x y - 371104.61727442464x "
						+ "+ 653.9626105014759y^3 + 256970.21097996994y^2 - 616987.5503638929y ="
						+ " -354741.21554768074");
	}

	@Test
	public void cmdContinuedFraction() {
		t("ContinuedFraction[(sqrt(5)-1)/2]",
				"0+\\frac{1}{1+\\frac{1}{1+\\frac{1}{1+\\frac{1}{1+\\frac{1}{1+\\frac{1}{1+"
						+ "\\frac{1}{1+\\frac{1}{1+\\frac{1}{1+\\frac{1}{1+\\frac{1}{1+\\frac{1}"
						+ "{1+\\frac{1}{1+\\frac{1}{1+\\cdots}}}}}}}}}}}}}}");
		t("ContinuedFraction[(sqrt(5)-1)/2,true]",
				"[0;1,1,1,1,1,1,1,1,1,1,1,1,1,1,\\ldots]");
		t("ContinuedFraction[(sqrt(5)-1)/2,10]",
				"0+\\frac{1}{1+\\frac{1}{1+\\frac{1}{1+\\frac{1}{1+\\frac{1}{1+\\frac{1}"
						+ "{1+\\frac{1}{1+\\frac{1}{1+\\frac{1}{1+\\cdots}}}}}}}}}");
		t("ContinuedFraction[(sqrt(5)-1)/2,10,true]", "[0;1,1,1,1,1,1,1,1,1,\\ldots]");
	}

	@Test
	public void cmdCommonDenominator() {
		t("CommonDenominator[1/2,1/3]", "6");
		t("CommonDenominator[1/(x-1),1/(x^2-1)]", "NaN");
	}

	@Test
	public void cmdCenterView() {
		t("CenterView[(1,1)]");
	}

	@Test
	public void cmdClosestPointRegion() {
		t("ClosestPointRegion[x^2+y^2=1,(1,1)]", "(0.7071067811865476, 0.7071067811865475)");
	}

	@Test
	public void cmdDataFunction() {
		t("DataFunction[]", "DataFunction[{}, {},x]");
		tRound("DataFunction[]", "DataFunction[x]");
	}

	@Test
	public void cmdDefined() {
		t("IsDefined[ Polygon[(1,1),(2,1/2),4] ]", "true");
	}

	@Test
	public void cmdDegree() {
		t("Degree[x^4 + 2 x^2]", "4");
		t("Degree[0x]", "0");
		t("Degree[x^2-x^2+x+1]", "1");
	}

	@Test
	public void cmdDelauneyTriangulation() {
		t("DelaunayTriangulation[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]",
				"DelauneyTriangulation[{(1, 1), (2, 2), (3, 3), (4, 1 / 4), (5, 1 / 5)}]");
	}

	@Test
	public void cmdDelete() {
		t("obj2=Polygon[(1,1),(2,1/2),4]", "1.25");
		t("Delete[ obj2 ]");
	}

	@Test
	public void cmdDeterminant() {
		t("Determinant[ {{1,2},{3,4}} ]", "-2");
	}

	@Test
	public void cmdDenominator() {
		t("Denominator[ (x + 2)/(x+1) ]", "x + 1");
		t("Denominator[ 3/7 ]", "7");
		t("Denominator[ 5/(-8) ]", "8");
		t("Denominator[ 2/0 ]", "0");
		t("Denominator[ 1234/5678 ]", "2839");
		t("Denominator[ 12345/67890 ]", "4526");
		t("Denominator[ 123456/789012 ]", "65751");
		t("Denominator[ 1234567/8901234 ]", "8901234");
		t("Denominator[ 12345678/90123456 ]", "15020576");
		t("Denominator[ 123456789/12345678 ]", "1371742");
		t("Denominator[ 1234567890/1234567890 ]", "1");
		t("Denominator[ 12345678901/23456789012 ]", "23456789012");
		t("Denominator[ 123456789012/3456789012 ]", "288065751");
		t("Denominator[ 1234567890123/45678901234 ]", "45678901234");
		t("Denominator(0.125/0.166666666666666666)", "4");
		t("Denominator(0.125/3)", "24");
		t("Denominator(3/0.166666666666666666)", "1");
		t("Denominator[ 1/(-3) ]", "3");
		t("Denominator[ 2/(-3) ]", "3");
		t("Denominator[ infinity ]", "0");
		t("Denominator[ -infinity ]", "0");
		t("Denominator[ 0 ]", "1");
	}

	@Test
	public void cmdDiameter() {
		t("ConjugateDiameter[ x+y=17, x^2+y^2=1 ]", "x - y = 0");
		t("ConjugateDiameter[ Vector[ (1,1) ] , x^2+y^2=1 ]", "x + y = 0");
	}

	@Test
	public void cmdDirection() {
		t("Direction[-2x + 3y + 1 = 0]", "(3, 2)");
	}

	@Test
	public void cmdDirectrix() {
		t("Directrix[ x^2 - 3x + 3y = 9 ]", "y = 4.5", "?");
	}

	@Test
	public void cmdDistance() {
		t("Distance[ (1,1), Polygon[(1,1),(2,1/2),4] ]", "0");
		t("Distance[ xAxis,yAxis ]", "0");
		t("Distance[x+y+z=1, x+y+z=2]", "0.5773502691896257");
		t("Distance[Line[(1,1),(2,1/2)],Line[(3,1/3),(4,1/4)]]", "0");
		t("Distance[Line[(5,1/5,-1),(5,2,-8)],Line[(3,1/3),(4,1/4)]]", "0.21605541979688042");
		t("Distance[Line[(1,1),(2,1/2)],Line[(-5,4,-3),(9,-2,3)]]", "0.4423258684646914");
		t("Distance[Line[(5,1/5,-1),(5,2,-8)],Line[(-5,4,-3),(9,-2,3)]]", "0.09443851715611623");
	}

	@Test
	public void cmdDiv() {
		t("Div[ 4, 4 ]", "1");
		t("Div[ x^4+4, x^2 ] ", "x^(2)");
	}

	@Test
	public void cmdDifference() {
		tRound("diff_{1}=Difference[Polygon[(0,0),(2,0),4],Polygon[(1,1),(3,1),(3,3),(1,3)]]",
				"3", "(2, 1)", "(1, 1)", "(1, 2)", "(0, 2)",
				"(0, 0)", "(2, 0)", "1", "1", "1", "2", "2", "1");
		assertNotNull(app.getKernel().lookupLabel("diff_{1}"));
		tRound("symDiff=Difference[Polygon[(0,0),(2,0),4],Polygon[(1,1),(3,1),(3,3),(1,3)], true]",
				"3", "3", "(3, 3)", "(1, 3)", "(1, 2)", "(2, 2)",
				"(2, 1)", "(3, 1)", "(2, 1)", "(1, 1)", "(1, 2)",
				"(0, 2)", "(0, 0)", "(2, 0)", "2", "1", "1", "1", "1",
				"2", "1", "1", "1", "2", "2", "1");
		assertNotNull(app.getKernel().lookupLabel("symDiff_{1}"));
	}

	@Test
	public void cmdDilate() {
		t("Dilate[ (4,5), 2,(1,1) ]", "(7, 9)");
		t("Dilate[ (4,5), 2 ]", "(8, 10)");
		t("r=Dilate(y=-3x-6,2)", "y = -3x - 12");
		((GeoLine) get("r")).setToUser();
		t("r", "3x + y = -12");
	}

	@Test
	public void cmdDivisorsSum() {
		t("DivisorsSum[37]", "38");
	}

	@Test
	public void cmdDivisors() {
		t("Divisors[37]", "2");
	}

	@Test
	public void cmdDimension() {
		t("Dimension[(3,7)]", "2");
	}

	@Test
	public void cmdDivisorsList() {
		t("DivisorsList[42]", "{1, 2, 3, 6, 7, 14, 21, 42}");
	}

	@Test
	public void cmdDivision() {
		t("Division[3,7]", "{0, 3}");
		t("Division[x^2, x+1]", "{x - 1, 1}");
	}

	@Test
	public void cmdDodecahedron() {
		String[] dodeca = new String[] { "7.66312", "(1.30902, 0.95106, 0)",
				"(0.5, 1.53884, 0)", "(-0.30902, -0.42533, 0.85065)",
				"(1.30902, -0.42533, 0.85065)", "(1.80902, 1.11352, 0.85065)",
				"(0.5, 2.06457, 0.85065)", "(-0.80902, 1.11352, 0.85065)",
				"(-0.80902, 0.26287, 1.37638)", "(0.5, -0.68819, 1.37638)",
				"(1.80902, 0.26287, 1.37638)", "(1.30902, 1.80171, 1.37638)",
				"(-0.30902, 1.80171, 1.37638)", "(-0.30902, 0.42533, 2.22703)",
				"(0.5, -0.16246, 2.22703)", "(1.30902, 0.42533, 2.22703)",
				"(1, 1.37638, 2.22703)", "(0, 1.37638, 2.22703)", "1.72048",
				"1.72048", "1.72048", "1.72048", "1.72048", "1.72048",
				"1.72048", "1.72048", "1.72048", "1.72048", "1.72048",
				"1.72048", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1",
				"1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1",
				"1", "1", "1", "1", "1", "1", "1" };
		platonicTest("Dodecahedron", 108, dodeca);
	}

	@Test
	public void cmdDot() {
		t("Dot[(0,0,1),(1,0,0)]", "0");
		t("Dot[(0,0,1),(0,0,1)]", "1");
		t("Dot[(0,3),(0,2)]", "6");
	}

	@Test
	public void cmdDotPlot() {
		t("ZoomIn(0,0,16,12)");
		t("DotPlot[ {1,1,1,2} ]", "{(1, 1), (1, 2), (1, 3), (2, 1)}");
		t("DotPlot[ {1,1,1,2},2 ]", "{(1, 2), (1, 4), (1, 6), (2, 2)}");
		t("DotPlot[ {1,1,1,2}, true]",
				"{(1, 0.1), (1, 0.3), (1, 0.5), (2, 0.1)}");
		t("DotPlot[ {1,1,1,2}, true, 5 ]",
				"{(1, 0.1), (1, 1.1), (1, 2.1), (2, 0.1)}");
	}

	@Test
	public void cmdDynamicCoordinates() {
		t("DynamicCoordinates[ (1,1), 42, 4 ]", "(42, 4)");
		t("DynamicCoordinates[ (1,1), 42, 4, 13 ]", "(42, 4, 13)");
	}

	@Test
	public void cmdEccentricity() {
		t("Eccentricity[ x^2+y^2=1 ]", "0");
	}

	@Test
	public void cmdElement() {
		t("Element[ {{{1,2,3,4,5}}}, 1, 1, 6]", "NaN");
		t("Element[ {1,2,3,4,5}, 4 ]", "4");
		t("Element[ {{1,2},{3,4}}, 2, 1 ]", "3");
	}

	@Test
	public void cmdEllipse() {
		t("Ellipse[ (1,1),(2,1/2),(3,1/3) ]",
				"34.98700805481898x² + 4x y + 37.98700805481898y² - 107.96102416445694x - "
						+ "62.98051208222847y = -21.77272535465181");
		t("Ellipse[ (3,1/3), (3,1/3), 4 ]",
				"256x² + 256y² - 1536x - 170.66666666666666y = 1763.5555555555557");
		t("Ellipse[ (3,1/3), (3,1/3), Segment[(1,1),(2,1/2)] ]",
				"20.000000000000004x² + 20.000000000000004y² - 120.00000000000003x - "
						+ "13.333333333333336y = -157.22222222222226");
	}

	@Test
	public void cmdEnds() {
		t("Ends[Cone[x^2+y^2=9,4]]", "X = (0, 0, 0) + (3 cos(t), -3 sin(t), 0)", "X = (0, 0, 4)");
	}

	@Test
	public void cmdErlang() {
		prob("Erlang", "2,1",
				"If(x < 0, 0, (" + Unicode.EULER_STRING
						+ "^(-(1x)) x^(2 - 1) * 1^2) / (2 - 1)!)",
				"If(x < 0, 0, gamma(2, 1x) / (2 - 1)!)");
	}

	@Test
	public void cmdExcentricity() {
		t("LinearEccentricity[ x^2+y^2=1 ]", "0");
	}

	@Test
	public void cmdExecute() {
		t("Execute[ {\"Midpoint[%1,%2]\"}, (1,1), (2,1/2)]");
		t("Execute[ {\"42=42-1\"}]");
	}

	@Test
	public void cmdExponential() {
		prob("Exponential", "2",
				"If(x < 0, 0, 2" + Unicode.EULER_STRING + "^(-(2x)))",
				"If(x < 0, 0, 1 - " + Unicode.EULER_STRING + "^(-(2x)))");
	}

	@Test
	public void cmdExportImage() {
		t("ZoomIn(-10,-10,10,10)");
		t("ExportImage[\"type\",\"png\"]");
		t("ExportImage[\"scale\",0.5]");
		t("ExportImage[\"scalecm\",1]");
		t("ExportImage[\"dpi\",72]");
		t("ExportImage[\"dpi\",72, \"scalecm\", 2]");
		t("ExportImage[\"height\",300]");
		t("ExportImage[\"width\",300]");
		t("ExportImage[\"transparent\",true]");
	}

	@Test
	public void cmdExpression() {
		t("(1,2)+{(2,3),(4,5)}", "{(3, 5), (5, 7)}");
	}

	@Test
	public void cmdExtremum() {
		t("ZoomIn[-5,-5,5,5]");
		tRound("Extremum[ sin(x), 1, 7 ]",
				"(1.5708, 1)", "(4.71239, -1)");
		tRound("Extremum[ x^3-3x ]", "(-1, 2)", "(1, -2)");
		tRound("Extremum[ nroot(x^(3) - 3x, 3) ]",
				"(-1, 1.25992)", "(1, -1.25992)");
		// TODO t("Extremum((x^2-4)/(x-2),-9,9)", "(NaN, NaN)");
	}

	@Test
	public void cmdFactor() {
		t("Factor[ x^2 ]", "x^(2)");
	}

	@Test
	public void cmdFactors() {
		t("Factors[ 42 ]", "{{2, 1}, {3, 1}, {7, 1}}");
		t("Factors[ x^2 ]", "?");
	}

	@Test
	public void cmdFDistribution() {
		prob("FDistribution", "2,1",
				"If(x < 0, 0, (1^(1 / 2) (2x)^(2 / 2)) / "
						+ "(beta(2 / 2, 1 / 2) x (2x + 1)^(2 / 2 + 1 / 2)))",
				"If(x < 0, 0, betaRegularized(2 / 2, 1 / 2, (2x) / (2x + 1)))",
				0);
	}

	@Test
	public void cmdFillCells() {
		t("FillCells[ A1, {1,2,3,4,5} ]");
		t("FillCells[ A1, {{1,2},{3,4}} ]");
		t("FillCells[ A1:A10, Polygon[(1,1),(2,1/2),4] ]");
	}

	@Test
	public void cmdFillColumn() {
		t("FillColumn[ 13, {1,2,3,4,5} ]", "{1, 2, 3, 4, 5}");
	}

	@Test
	public void cmdFillRow() {
		t("FillRow[ 4, {1,2,3,4,5} ]", "{1, 2, 3, 4, 5}");
	}

	@Test
	public void cmdFirst() {
		t("First[ {1,2,3,4,5} ]", "{1}");
		t("First[ {1,2,3,4,5} , 42 ]", "?");
		t("First[ Function[{1,2,1,2,1,2,1,2,1,2,1}] , 2 ]", "{(1, 1), (1.125, 2)}");
		t("First[ \"GeoGebra\" ]", "G");
		t("First[ \"GeoGebra\" , 3 ]", "Geo");
	}

	@Test
	public void cmdFirstAxis() {
		t("MajorAxis[ x^2+y^2=1 ]", "y = 0");
	}

	@Test
	public void cmdFirstAxisLength() {
		t("SemiMajorAxisLength[ x^2+y^2=1 ]", "1");
	}

	@Test
	public void cmdFit() {
		tRound("Fit[ {(0,1),(1,2),(2,5)}, {x^2,x,1} ]",
				unicode("1x^2 + 0x + 1 * 1"));
		tRound("Fit[ {(0,1,1),(1,1,2),(2,1,5),(0,2,4),(1,2,5),(2,2,8)}, {x^2,x,1,x^2*y,x*y,y} ]",
				unicode("3y + 0x y + 0x^2 y - 2 * 1 + 0x + 1x^2"));
		t("a=Slider[0,10]", "0");
		t("b=Slider[0,10]", "0");
		t("c=Slider[0,10]", "0");
		tRound("Fit[ {(0,1),(1,2),(2,5)},a*x^2+b*x+c ]",
				unicode("1x^2 + 0x + 1"));
		// for APPS-2451
		t("Translate[Fit[ {(0,0),(1,4)}, {x} ],(1,1)]",
				"(4 * (x - 1)) + 1");
	}

	@Test
	public void cmdFitExp() {
		t("FitExp[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]",
				"(3.8524166362194783 * exp((-0.5298317366548038 * x)))");
	}

	@Test
	public void cmdFitGrowth() {
		t("FitGrowth[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]",
				"(3.8524166362194783 * 0.5887040186524746^(x))");
	}

	@Test
	public void cmdFitLineX() {
		t("FitLineX[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]",
				"y = -1.7259701492537307x + 6.467910447761192");
	}

	@Test
	public void cmdFitLineY() {
		t("FitLine[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]",
				"y = -0.33500000000000013x + 2.2950000000000004");
	}

	@Test
	public void cmdFitLog() {
		// slightly different result on M2 Mac with xmlTemplate, use maxPrecision instead
		t("FitLog[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", StringTemplate.maxPrecision,
				"1.77913767533668 - 0.510849628173396ln(x)");
	}

	@Test
	public void cmdFitLogistic() {
		t("FitLogistic[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]",
				"1.0000000000001077 / (1 + (4.575485271998105E-17 * exp((7.81108 * x))))");
	}

	@Test
	public void cmdFitPow() {
		// slightly different result on M2 Mac with xmlTemplate, use maxPrecision instead
		t("FitPow[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", StringTemplate.maxPrecision,
				"2.11729141837616x^-1.03491792057186");
	}

	@Test
	public void cmdFitSin() {
		tRound("FitSin[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]",
				"1.4496 + 1.41446sin(1.46525x - 2.11343)");
	}

	@Test
	public void cmdFitPoly() {
		t("FitPoly[ {(0,0),(1,1),(2,4),(3,9),(4,16)}, 0 ]", "6");
		t("FitPoly[ {(0,0),(1,1),(2,4),(3,9),(4,16)}, 1 ]", "(4 * x) - 2");
		t("FitPoly[ {(0,0),(1,1),(2,4),(3,9),(4,16)}, 2 ]", "x^(2)");
		t("FitPoly[ {(0,0),(1,1),(2,4),(3,9),(4,16)}, 3 ]", "x^(2)");
		// this one falls back to Polynomial()
		tRound("FitPoly[ {(0,0),(1,1),(2,4),(3,9),(4,16)}, 4 ]",
				unicode("0x^3 + x^2 + 0x"));
		tRound("FitPoly[ {(0,0),(1,1),(2,4),(3,9),(4,16),(5,25)}, 4 ]",
				unicode("0x^4 + 0x^3 + x^2 + 0x + 0"));
		t("FitPoly[ Function({0,4,0,1,4,9,16}), 1 ]", "(4 * x) - 2");
	}

	@Test
	public void cmdFitImplicit() {
		t("FitImplicit[{(0,0),(0,1),(0,2),(0,3)},2]", "?");
		t("FitImplicit[{(0,0),(0,1),(0,2),(0,3),(3,0),(4,0),(5,0)},2]",
				"-x y = 0");
	}

	@Test
	public void cmdFlatten() {
		t("Flatten[ {} ]", "{}");
		t("Flatten[ {{},{{{}}}} ]", "{}");
		t("Flatten[ {{(1,1)},{{{4}}}} ]", "{(1, 1), 4}");
	}

	@Test
	public void cmdFocus() {
		t("Focus[ x^2+y^2=1 ]", "(0, 0)", "(0, 0)");
	}

	@Test
	public void cmdFractionText() {
		t("FractionText[ 4/6 ]", " \\frac{ 2 }{ 3 } ");
		t("FractionText[ -4/6 ]", " \\frac{ -2 }{ 3 } ");
		t("FractionText[ 4/6, false ]", " \\frac{ 2 }{ 3 } ");
		t("FractionText[ -4/6 , false ]", "- \\frac{ 2 }{ 3 } ");
		t("FractionText[ (1,1) ]", "{ \\left( 1,1 \\right) }");
	}

	@Test
	public void cmdFrequency() {
		t("Frequency[ false, {1,2,3,4,5}, {2,3,4}]", "{0, 1, 1, 1}");
		t("Frequency[ false, {1,2,3,4,5}, {2,3,4}, true , 4 ]", "{0, 4, 4, 4}");
		t("Frequency[ false, {2,3,4}]", "{1, 1, 1}");
		t("Frequency[ {1,2,3,4,5}, {2,3,4},false]", "{0, 1, 1, 1}");
		t("Frequency[ {1,2,3,4,5}, {2,3,4}, true , 4 ]", "{0, 4, 4, 4}");
		t("Frequency[ {2,3,4} ]", "{1, 1, 1}");
		t("Frequency[ {\"GeoGebra\",\"rocks\"},{\"X\",\"Y\"} ]", "{{1, 0}, {0, 1}}");
	}

	@Test
	public void cmdFrequencyPolygon() {
		t("FrequencyPolygon[ false, {1,2,3,4,5}, {2,3,4}, false , 4 ]", "0");
		t("FrequencyPolygon[ {1,2,3,4,5}, {1,2,3,4,5} ]", "0");
		t("FrequencyPolygon[ {1,2,3,4,5}, {2,3,4}, false , 50 ]", "0");
	}

	@Test
	public void cmdFrequencyTable() {
		t("FrequencyTable[ false, {1,2,3,4,5}, {2,3,4}]",
				"\\begin{array}{c|c}Interval&\\text{Count} \\\\\\hline 1\\text{ -- "
						+ "}2&0\\\\2\\text{ -- }3&1\\\\3\\text{ -- }4&1\\\\4\\text{ -- }5&1"
						+ "\\\\\\end{array}");
		t("FrequencyTable[ false, {1,2,3,4,5}, {2,3,4}, true , 4 ]",
				"\\begin{array}{c|c}Interval&\\text{Frequency} \\\\\\hline 1\\text{ -- "
						+ "}2&0\\\\2\\text{ -- }3&4\\\\3\\text{ -- }4&4\\\\4\\text{ -- }5&4\\\\\\"
						+ "end{array}");
		t("FrequencyTable[ false, {2,3,4}]",
				"\\begin{array}{c|c}Value&\\text{Frequency} \\\\\\hline 2&1\\\\3&1\\\\4&1"
						+ "\\\\\\end{array}");
		t("FrequencyTable[ {1,2,3,4,5}, {2,3,4} ]",
				"\\begin{array}{c|c}Interval&\\text{Count} \\\\\\hline 1\\text{ -- "
						+ "}2&0\\\\2\\text{ -- }3&1\\\\3\\text{ -- }4&1\\\\4\\text{ -- "
						+ "}5&1\\\\\\end{array}");
		t("FrequencyTable[ {1,2,3,4,5}, {2,3,4}, true , 4 ]",
				"\\begin{array}{c|c}Interval&\\text{Frequency} \\\\\\hline 1\\text{ -- "
						+ "}2&0\\\\2\\text{ -- }3&4\\\\3\\text{ -- }4&4\\\\4\\text{ -- "
						+ "}5&4\\\\\\end{array}");
		t("FrequencyTable[ {2,3,4} ]",
				"\\begin{array}{c|c}Value&\\text{Frequency} \\\\\\hline "
						+ "2&1\\\\3&1\\\\4&1\\\\\\end{array}");
	}

	@Test
	public void cmdFromBase() {
		t("FromBase[\"FFA23\",16]", "1047075");
	}

	@Test
	public void cmdFunction() {
		t("Function[ ]", "DataFunction[{}, {},x]"); // empty function for logging
		t("Function[ sin(x), 4, 13 ]", "If[4 ≤ x ≤ 13, sin(x)]");
		t("Function[ {1,2,3,4,5} ]", "freehand(x)");
	}

	@Test
	public void cmdFutureValue() {
		t("FutureValue[ 42, 4, 13]", "-1058200");
		t("FutureValue[ 42, 4, 13,1]", "-4477001");
		t("FutureValue[ 42, 4, 13,0,1]", "-45502600");
	}

	@Test
	public void cmdGamma() {
		prob("Gamma", "2,1",
				"If(x < 0, 0, (x^(2 - 1) " + Unicode.EULER_STRING
						+ "^(-(x / 1))) / (1^2 gamma(2)))",
				"If(x < 0, 0, gamma(2, x / 1) / gamma(2))");
	}

	@Test
	public void cmdBetaDist() {
		prob("BetaDist", "2,1",
				"If(0 < x < 1, (x^(2 - 1) (1 - x)^(1 - 1)) / beta(2, 1), 0)",
				"If(0 < x < 1, betaRegularized(2, 1, x), If(x "
						+ Unicode.LESS_EQUAL + " 0, 0, 1))");
	}

	@Test
	public void cmdGCD() {
		t("GCD[ {1,2,3,4,5} ]", "1");
		t("GCD[ 42, 42 ]", "42");
	}

	@Test
	public void cmdGetTime() {
		// don't test this
	}

	@Test
	public void cmdGeometricMean() {
		t("GeometricMean[ {1,2,3,4,5} ]", "2.6051710846973517");
	}

	@Test
	public void cmdHarmonicMean() {
		t("HarmonicMean[ {1,2,3,4,5} ]", "2.18978102189781");
	}

	@Test
	public void cmdHeight() {
		t("Height[Cone[x^2+y^2=9,4]]", "4");
		t("Height[Cube[(0,0,1),(0,0,0)]]", "1");
	}

	@Test
	public void cmdHideLayer() {
		t("HideLayer[ 42 ]");
	}

	@Test
	public void cmdHistogram() {
		t("Histogram[ false, {1,2,3,4,5}, {2,3,4}, true , 4 ]", "12");
		t("Histogram[ {1,2,3,4,5}, {1,2,3,4,5} ]", "5");
		t("Histogram[ {1,2,3,4,5}, {2,3,4}, true , 4  ]", "12");
	}

	@Test
	public void cmdHistogramRight() {
		t("HistogramRight[ false, {1,2,3,4,5}, {2,3,4}, true , 4 ]", "12");
		t("HistogramRight[ {1,2,3,4,5}, {1,2,3,4,5} ]", "5");
		t("HistogramRight[ {1,2,3,4,5}, {2,3,4}, true , 4 ]", "12");
	}

	@Test
	public void cmdHull() {
		t("Hull[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} , 0.05 ]",
				"ConvexHull[{(1, 1), (2, 2), (3, 3), (4, 1 / 4), (5, 1 / 5)}]");
	}

	@Test
	public void cmdHyperbola() {
		t("Hyperbola[ (1,1),(2,1/2),(3,1/3) ]",
				"0.7907697229588069x² + 4x y + 3.790769722958807y² - 5.37230916887642x -"
						+ " 11.68615458443821y = -8.474188225595094");
		t("Hyperbola[ (3,1/3), (3,1/3), 4 ]",
				"256x² + 256y² - 1536x - 170.66666666666666y = 1763.5555555555557");
		t("Hyperbola[ (3,1/3), (3,1/3), Segment[(1,1),(2,1/2)] ]",
				"20.000000000000004x² + 20.000000000000004y² - 120.00000000000003x - "
						+ "13.333333333333336y = -157.22222222222226");
	}

	@Test
	public void cmdHyperGeometric() {
		intProb("HyperGeometric", "10,3,5", "2", "0.41667", "0.91667");
	}

	@Test
	public void cmdIcosahedron() {
		String[] dodeca = new String[] { "2.18169",
				"(-0.30902, 0.75576, 0.57735)", "(0.5, -0.6455, 0.57735)",
				"(1.30902, 0.75576, 0.57735)", "(0.5, 1.22285, 0.93417)",
				"(-0.30902, -0.17841, 0.93417)", "(1.30902, -0.17841, 0.93417)",
				"(0, 0.57735, 1.51152)", "(0.5, -0.28868, 1.51152)",
				"(1, 0.57735, 1.51152)", "0.43301", "0.43301", "0.43301",
				"0.43301", "0.43301", "0.43301", "0.43301", "0.43301",
				"0.43301", "0.43301", "0.43301", "0.43301", "0.43301",
				"0.43301", "0.43301", "0.43301", "0.43301", "0.43301",
				"0.43301", "0.43301", "1", "1", "1", "1", "1", "1", "1", "1",
				"1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1",
				"1", "1", "1", "1", "1", "1", "1", "1", "1" };
		platonicTest("Icosahedron", 60, dodeca);
	}

	@Test
	public void cmdIdentity() {
		t("Identity[ 3 ]", "{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}");
	}

	@Test
	public void cmdIf() {
		t("If[ false, (1,1), (2,1/2) ]", "(2, 0.5)");
		t("If[ false,(3,1/3) ]", "(NaN, NaN)");
	}

	@Test
	public void cmdIFactor() {
		t("IFactor[ x^4-2 ]", "x^(4) - 2");
	}

	@Test
	public void cmdImplicitCurve() {
		t("ImplicitCurve[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]",
				"0.11111111111111115x^2 + 2.111111111111111x y - 1.0000000000000002x -"
						+ " 2.2222222222222223y^2 + y = 0");
		t("ImplicitCurve[ x*y ]", "x y = 0");
	}

	@Test
	public void cmdImplicitSurface() {
		if (app.has(Feature.IMPLICIT_SURFACES)) {
			t("ImplicitSurface[sin(x)+sin(y)+sin(z)]",
					"sin(x) + sin(y) + sin(z) = 0");
		}
	}

	@Test
	public void cmdIncircle() {
		t("Incircle[ (1,1),(2,1/2),(3,1/3) ]",
				"(x - 2.0245848862665516)² + (y - 0.575603044305899)² = 0.006180493369736152");
	}

	@Test
	public void cmdIndexOf() {
		t("IndexOf[ 5, {1, 3, 5, 2, 5, 4} ]", "3");
		t("IndexOf[ 5, {1, 3, 5, 2, 5, 4}, 4 ]", "5");
		t("IndexOf[ 5, {1, 3, 5, 2, 5, 4}, 6 ]", "NaN");
		t("IndexOf[ \"GeoGebra\", \"GeoGebra\"]", "1");
		t("IndexOf[ \"Ge\", \"GeoGebra\", 4 ]", "4");
	}

	@Test
	public void cmdInsert() {
		t("Insert[ {1,2,3,4,5}, {1,2,3,4,5}, 4 ]", "{1, 2, 3, 1, 2, 3, 4, 5, 4, 5}");
		t("Insert[ Polygon[(1,1),(2,1/2),4], {1,2,3,4,5}, 4 ]", "{1, 2, 3, 1.25, 4, 5}");
	}

	@Test
	public void cmdIntegralBetween() {
		t("IntegralBetween[ sin(x), sin(x), 4, 13 ]", "0");
		t("IntegralBetween[ sin(x), sin(x), 4, 13, false ]", "NaN");
	}

	@Test
	public void cmdIntersection() {
		t("Intersection[ {1,2,3,4,5}, {1,2,3,4,5} ]", "{1, 2, 3, 4, 5}");
	}

	@Test
	public void cmdIntersectRegion() {
		t("IntersectRegion[ Polygon[(1,1),(2,1/2),(3,1/3)], Polygon[(1,1),(2,1/2),(3,1/3)] ]",
				"0.16666666666666652", "(1, 1)", "(2, 0.5)", "(3, 0.3333333333333333)",
				"1.118033988749895", "1.0137937550497031", "2.1081851067789197");
	}

	@Test
	public void cmdInteriorAngles() {
		t("InteriorAngles[Polygon((0,0),(2,0),(2,1),(1,1),(1,2),(0,2))]",
				deg("90"), deg("90"), deg("90"), deg("270"),
				deg("90"), deg("90"));
		t("InteriorAngles[Polygon((0,0),(2,0),(2,1),(0,1),(0,0))]",
				"?", deg("90"), deg("90"), deg("90"),
				"?");
	}

	@Test
	public void cmdIntersect() {
		t("ZoomIn(-5,-5,5,5)");
		intersect("3x=4y", "Curve[5*sin(t),5*cos(t),t,0,6]", false, "(4, 3)",
				"(-4, -3)");
		intersect("x=y", "x+y=2", true, "(1, 1)");
		intersect("x=y", "x^2+y^2=2", true, "(1, 1)", "(-1, -1)");
		intersect("x=y", "x^4+y^4=2", false, "(1, 1)", "(-1, -1)");
		intersect("x^4+y^4=2", "(x-2)^4+y^4=2", false, "(1, -1)", "(1, 1)");
		intersect("x^2+y^2=2", "x^4+y^4=2", false, "(-1, -1)", "(-1, 1)",
				"(1, -1)", "(1, 1)");
		intersect("x", "x^4+y^4=2", false, "(-1, -1)", "(1, 1)");
		t("Intersect[x=y,x^2+y^2=2, (-5, -3)]", "(-1, -1)");
		tRound("{Intersect(x^2+y^2=25, x y=12)}", "{(-4, -3), (-3, -4), (3, 4), (4, 3)}");
		tRound("Intersect[x^2+y^2=25,(x-6)^2+ y^2=25, 1]", "(3, 4)");
		intersect("x=y", "sin(x)", false, "(0, 0)");
		intersect("x=y", "(2,2)", false, "(2, 2)");
		intersect("x", "(2,2)", false, "(2, 2)");
		intersect("x=y", "(x-1)^2+1", true, "(1, 1)", "(2, 2)");
		intersect("x^2=y^2", "(x-1)^2+1", true, false, "(1, 1)", "(2, 2)");
		intersect("x=y", "PolyLine((-1,-2),(-1,3),(5,3))", false, true,
				"(3, 3)", "(-1, -1)");
		intersect("x^2", "PolyLine((-1,-2),(-1,3),(5,3))", false, true,
				"(-1, 1)", eval("(sqrt(3), 3)"));
		intersect("x^2", "Polygon((-1,-2),(-1,3),(5,3))", false, true,
				"(-1, 1)", eval("(sqrt(3), 3)"));
		intersect("PolyLine((1,-2),(1,4),(5,3))",
				"PolyLine((-1,-2),(-1,3),(5,3))", false, "(1, 3)", "(5, 3)");
		intersect("PolyLine((1,-2),(1,4),(5,3))",
				"Polygon((-1,-2),(-1,3),(5,3))", false, "(1, 3)",
				"(1, -0.33333)", "(5, 3)", "(5, 3)");
		intersect("Polygon((1,-2),(1,4),(5,3))",
				"Polygon((-1,-2),(-1,3),(5,3))", false, "(1, 3)",
				"(1, -0.33333)", "(5, 3)", "(5, 3)", "(5, 3)", "(5, 3)");
		intersect("(x+1)^4+(y-3)^4=1", "PolyLine((-1,-2),(-1,3),(5,3))", false,
				"(-1, 2)", "(0, 3)");
		intersect("(x+1)^2+(y-3)^2=1", "PolyLine((-1,-2),(-1,3),(5,3))", false,
				"(-1, 2)", "(0, 3)");
		intersect("(x+1)^2+(y-3)^2=1", "Polygon((-1,-2),(-1,3),(5,3))", false,
				"(-1, 2)", "(0, 3)");
		intersect("x^2+1", "x^3-x+2", true, "(-1, 2)", "(1, 2)");

		// function with removable discontinuity
		intersect("(x ln(x + 1)) / (exp(2x) - 1)", "xAxis", false, "(?, ?)");
		intersect("Curve(t,-t^4+2 t^2+0.25 t+5,t,-10,10)", "0.1x - 0.96y=-3.9156",
				false, "(-1.51783, 3.92064)", "(1.57047, 4.24234)");
		intersect("-x^4+2 x^2+0.25 x+5", "0.1x - 0.96y=-3.9156",
				false, "(-1.51783, 3.92064)", "(1.57047, 4.24234)");
		intersect("Spline({(1,0),(1,1),(0,1)},3)", "x=y",
				false, "(1, 1)");
		intersect("Segment((0,0),(0,5))", "x^2+y^2+z^2=4",
				false, "(?, ?, ?)", "(0, 2, 0)");
		if (app.has(Feature.IMPLICIT_SURFACES)) {
			intersect("x^4+y^4+z^4=2", "x=y", false, "(-1, -1, 0)",
					"(1, 1, 0)");
		}
	}

	@Test
	public void testIntersectCurves3D() {
		tRound("Intersect(Curve(v,v,pi/4,v,0,4),Curve(cos(u),sin(u),u,u,0,6),1,1)",
				"(0.70711, 0.70711, 0.7854)");
	}

	@Test
	public void cmdIntersectConic() {
		t("IntersectConic[x+z=0, x^2+y^2+z^2=1]",
				"X = (0, 0, 0) + (0.7071067811865476 sin(t), cos(t), -0.7071067811865476 sin(t))");
		t("IntersectConic[x^2+y^2+(z-1)^2=0, x^2+y^2+z^2=0]", "?");
	}

	@Test
	public void cmdIntersectPath() {
		// 3D
		t("IntersectPath[x+y+z=1,x+y-z=1]",
				"X = (1, 0, 0) + " + Unicode.lambda + " (-2, 2, 0)");
		tRound("IntersectPath[x^2+y^2+z^2=4,x+y-z=1]",
				"X = (0.33333, 0.33333, -0.33333) + (-1.35401 cos(t) - 0.78174 sin(t),"
						+ " 1.35401 cos(t) - 0.78174 sin(t), -1.56347 sin(t))");
		tRound("IntersectPath[Polygon[(0,0,0),(2,0,0),(2, 2,0),(0,2,0)],Polygon[(1,1),(3,1),4]]",
				"1", "(2, 2, 0)", "(1, 2, 0)", "(1, 1, 0)",
				"(2, 1, 0)", "1", "1", "1", "1");
		tRound("IntersectPath[Polygon[(0,0),(2,0),4],x+y=3]", eval("sqrt(2)"));
		// 2D
		tRound("IntersectPath[Polygon[(0,0,0),(2,0,0),(2, 2,0),(0,2,0)],x+y=3]",
				eval("sqrt(2)"));
		tRound("IntersectPath[Polygon[(0,0),(2,0),4],Polygon[(1,1),(3,1),4]]",
				"1", "(2, 2)", "(1, 2)", "(1, 1)", "(2, 1)", "1",
				"1", "1", "1");
		tRound("IntersectPath[Polygon[(1,-2),(3,-2),(3,0)],"
						+ "Polygon[Intersect[x=1,xAxis],(1,-2),(3,-2),(3,0)]]",
				"2", "(3, 0)", "(1, -2)", "(3, -2)", "2.82843", "2", "2");
		tRound("IntersectPath[Polygon[(0,0),(4,0),4],(x-2)^2+(y-2)^2=5]",
				"2", "2", "2", "2");
		tRound("IntersectPath[Segment[(0,0),(4,4)],(x-2)^2+(y-2)^2=2]",
				eval("sqrt(8)"));
		tRound("IntersectPath[Segment[(0,0),(2,2)],(x-2)^2+(y-2)^2=2]",
				eval("sqrt(2)"));
		tRound("IntersectPath[Segment[(1.5,1.5),(2,2)],(x-2)^2+(y-2)^2=2]",
				eval("sqrt(.5)"));
		tRound("IntersectPath[Cube[(0,0),(sqrt(2),0),(sqrt(2),sqrt(2))],x+y+z=sqrt(2)]",
				"1.73205", "(1.41421, 0, 0)", "(0, 1.41421, 0)",
				"(0, 0, 1.41421)", "2", "2", "2");
	}

	@Test
	public void cmdIntersectPathHomogeneousCoords() {
		t("A=Intersect(100x = -300, 100y = 200)", "(-3, 2)");
		tRound("t1=Polygon(A, (-3,-2), (3,-2))", "12", "4", "6", "7.2111");
		tRound("IntersectPath(t1,-3x+2y=10)", "1.24808");
	}

	@Test
	public void cmdIntersectPoly() {
		t("ZoomIn(-5,-5,5,5)");
		intersect("x^2+x", "x^2+x", false, "(?, ?)");
		intersect("x^2+x", "x^2+x+1", false, "(?, ?)");
	}

	@Test
	public void cmdInverseBinomial() {
		t("InverseBinomial[ 5, 0.5, 0.5 ]", "2");
	}

	@Test
	public void cmdInverseBinomialMinimumTrials() {
		t("InverseBinomialMinimumTrials[5, 0.5, 5]", "NaN");
		t("InverseBinomialMinimumTrials[5, 0.5, 5]", "NaN");
		t("InverseBinomialMinimumTrials[-0.01, 0.5, 5]", "NaN");
		t("InverseBinomialMinimumTrials[0.5, 1.1, 5]", "NaN");
		t("InverseBinomialMinimumTrials[0.5, -1.1, 5]", "NaN");
		t("InverseBinomialMinimumTrials[0.5, 0.5, -12]", "NaN");
		t("InverseBinomialMinimumTrials[0.5, 0.5, 1.2]", "NaN");
		t("InverseBinomialMinimumTrials[0.5, 0.0, 49]", "NaN");
		t("InverseBinomialMinimumTrials[0.01, 0.7, 49]", "86");
		t("InverseBinomialMinimumTrials[0.01, 0.1, 50]", "681");
		t("InverseBinomialMinimumTrials[0.1, 0.5, 50]", "115");
		t("InverseBinomialMinimumTrials[0.7, 1.0, 100]", "101");
		t("InverseBinomialMinimumTrials[0.5, 0.5, 5]", "11");
	}

	@Test
	public void cmdInverseCauchy() {
		t("InverseCauchy[ 3, 5, 0.5 ]", "3");
	}

	@Test
	public void cmdInverseChiSquared() {
		tRound("InverseChiSquared[ 5, 0.5 ]", "4.35146");
	}

	@Test
	public void cmdInverseExponential() {
		tRound("InverseExponential[ 2, 0.5 ]", "0.34657");
	}

	@Test
	public void cmdInverseFDistribution() {
		tRound("InverseFDistribution[ 3, 5, 0.5 ]", "0.90715");
	}

	@Test
	public void cmdInverseGamma() {
		tRound("InverseGamma[ 3, 5, 0.5 ]", "13.3703");
	}

	@Test
	public void cmdInverseBeta() {
		tRound("InverseBeta[ 3, 5, 0.5 ]", "0.36412");
	}

	@Test
	public void cmdInverseHyperGeometric() {
		t("InverseHyperGeometric(60, 10, 20, 0.5)", "3");
	}

	@Test
	public void cmdInverseLogistic() {
		t("InverseLogistic[1,2,3]", "0");
	}

	@Test
	public void cmdInverseLogNormal() {
		t("InverseLogNormal[1,2,3]", "0");
	}

	@Test
	public void cmdInverseNormal() {
		t("InverseNormal[ 0, 5, 0.5 ]", "0");
	}

	@Test
	public void cmdInversePascal() {
		t("InversePascal(10, 0.3, 0.5)", "22");
	}

	@Test
	public void cmdInversePoisson() {
		t("InversePoisson(5, 0.5)", "5");
	}

	@Test
	public void cmdInverseTDistribution() {
		t("InverseTDistribution(5, 0.5)", "0");
	}

	@Test
	public void cmdInverseWeibull() {
		tRound("InverseWeibull(1, 2, 0.5)", "1.38629");
	}

	@Test
	public void cmdInverseZipf() {
		t("InverseZipf(19, 1, 0.85)", "11");
	}

	@Test
	public void cmdInvert() {
		t("Invert[ {{1,1},{0,2}} ]", "{{1, -0.5}, {0, 0.5}}");
		t("Invert[ sin(x) ]", "asin(x)");
		t("Invert[If[x>1,x^3+1] ]", "If[cbrt(x - 1) > 1, cbrt(x - 1)]");
		t("Invert[ If(x>2,x)+1 ]", "If[x - 1 > 2, x - 1]");
		t("Invert[ If(x>2,sin(x)-x) ]", "?");
		t("Invert[3+0/x]", "?");
		t("Invert[3+x/0]", "?");
		t("Invert[3+2/x]", "2 / (x - 3)");
		AlgebraTestHelper.enableCAS(app, false);
		app.getKernel().getAlgebraProcessor().reinitCommands();
		t("Invert[ sin(x) ]", "NInvert[sin(x)]");
		AlgebraTestHelper.enableCAS(app, true);
		app.getKernel().getAlgebraProcessor().reinitCommands();
	}

	@Test
	public void cmdIsFactored() {
		t("IsFactored[ x ]", "true");
		t("IsFactored[ 0.5 ]", "true");
		t("IsFactored[ 5 ]", "true");
		t("IsFactored[ x^2-1 ]", "false");
		t("IsFactored[ x(x+1) ]", "true");
		t("IsFactored[ x(2x+2) ]", "false");
		t("IsFactored[ x^3-1 ]", "false");
		t("IsFactored[ x(x/2+1/2) ]", "false");
		t("IsFactored[ sin^2(x)-cos^2(x) ]", "?");
		t("IsFactored[ (sin(x)+cos(x))(sin(x)+cos(x)) ]", "?");
		t("IsFactored[ 0.5(x+1)2 ]", "false");
		t("IsFactored[ (x+1)(x^2-1) ]", "false");
		t("IsFactored[ (x+1)^2(x+2)^2((x+1)(2x+2))^2 ]", "?");
		t("IsFactored[ (x+1)(2x+2) ]", "false");
		t("IsFactored[ -2x-2 ]", "false");
		t("IsFactored[ -x-1 ]", "false");
		t("IsFactored[ 2*(x+0.5) ]", "false");
		t("IsFactored[ 2x+2 ]", "false");
		t("IsFactored[ 5(6/5x+2/5) ]", "false");
		t("IsFactored[ (1-x)(1+x) ]", "true");
		t("IsFactored[ 3x-2x ]", "false");
		t("IsFactored[ x^2-x ]", "false");
		t("IsFactored[ (x-1)^2+2 ]", "false");
		t("IsFactored[ x^2-2 ]", "true");
		t("IsFactored[ 3(x+1)^2+1 ]", "false");
		t("IsFactored[ (x+1)x+2 ]", "false");
		t("IsFactored[ x^3-64 ]", "false");
		t("IsFactored[ x^4+4 ]", "false");
		t("IsFactored[ 5(x-16/5)(x+3) ]", "false");
		t("IsFactored[ 49(x-3/7)(x+3/7) ]", "false");
		t("IsFactored[ 45x^4+15x^3-34x^2+86x-60 ]", "false");
		t("IsFactored[ (x^2+1)^2 ]", "true");
		t("IsFactored[ x^4+2x^2+1 ]", "false");
		t("IsFactored[ x^4+3x^2+1+2x^3+2x ]", "false");
		t("IsFactored[ -4x^4+24x^3+5x^2+18x+6 ]", "false");
		t("IsFactored[ -36x^4-56x^3+69x^2+53x-18 ]", "false");
		t("IsFactored[ 26x^4+16x^3-69x^2-53x+18 ]", "true");
		t("IsFactored[ -x^4+3x^2+1 ]", "true");
	}

	@Test
	public void cmdIsFactoredUpdate() {
		t("redef1(x) = x (2x-2)", "(x * ((2 * x) - 2))");
		t("redef2(x) = x^50 (x-1)", "(x^(50) * (x - 1))");
		t("check1 = IsFactored(redef1)", "false");
		t("check2 = IsFactored(redef2)", "?");
		t("redef1(x) = x (x-1)", "(x * (x - 1))");
		t("redef2(x) = x (x-1)", "(x * (x - 1))");
		t("check1", "true");
		t("check2", "true");
	}

	@Test
	public void cmdIsInteger() {
		t("IsInteger[ 42 ]", "true");
	}

	@Test
	public void cmdIsPrime() {
		t("IsPrime[0]", "?");
	}

	@Test
	public void cmdIsInRegion() {
		t("IsInRegion[(0,0),Circle[(1,1),2]]", "true");
		t("IsInRegion[(0,0),Circle[(1,1),1]]", "false");
		t("IsInRegion[(0,0,0),x+y+z=1]", "false");
		t("IsInRegion[(0,0,0),x+y+z=0]", "true");
		t("IsInRegion[(0,0,0),Polygon[(0,0,1),(1,0,0),(0,1,0)]]", "false");
		t("IsInRegion[(1/3,1/3,1/3),Polygon[(0,0,1),(1,0,0),(0,1,0)]]", "true");
		// move the centroid a bit in z-axis, it should no longer be inside
		t("IsInRegion[(1/3,1/3,1/2),Polygon[(0,0,1),(1,0,0),(0,1,0)]]",
				"false");
		t("IsInRegion[ (1,1), x^2+y^2=1 ]", "false");
	}

	@Test
	public void cmdIsTangent() {
		t("IsTangent[ x+y=17,x^2+y^2=1 ]", "false");
	}

	@Test
	public void cmdIsVertexForm() {
		t("IsVertexForm(4(x+1)^2+3)", "true");
		t("IsVertexForm(4(-3/7+x)^2+3/7+sqrt(2))", "true");
		t("IsVertexForm(4(x-3/7)^2+3/7+sqrt(2))", "true");
		t("IsVertexForm(x^2)", "true");
		t("IsVertexForm(4(-x-3/7)^2+3/7+sqrt(2))", "false");
		t("IsVertexForm((2x+2)^2+3)", "false");
	}

	@Test
	public void cmdIteration() {
		t("Iteration[ x*2, 2, 5 ]", "64");
		t("Iteration[ t*2, t, {(2,3)}, 5 ]", "(64, 96)");
		t("Iteration[ x*y, {1,1}, 6 ]", "720");
		t("Iteration[ x*y, {1,1}, 0 ]", "1");
		t("Iteration[ x*y, {1,1}, -1 ]", "NaN");
	}

	@Test
	public void cmdIterationList() {
		t("IterationList[ x*2, 2, 5 ]", "{2, 4, 8, 16, 32, 64}");
		t("IterationList[ a+b, a, b, {1,1}, 5 ]", "{1, 1, 2, 3, 5, 8}");
		t("IterationList[ x*y, {1,1}, 6 ]", "{1, 1, 2, 6, 24, 120, 720}");
	}

	@Test
	public void cmdJoin() {
		t("Join[ {1,2,3,4,5}, {1,2,3,4,5}]", "{1, 2, 3, 4, 5, 1, 2, 3, 4, 5}");
		t("Join[ {{1,2},{3,4}} ]", "{1, 2, 3, 4}");
	}

	@Test
	public void cmdKeepIf() {
		t("KeepIf[ x > 3, {1,2,3,4,5} ]", "{4, 5}");
		t("KeepIf[ A > 3,A, {1,2,3,4,5} ]", "{4, 5}");
		syntaxes++;
		t("KeepIf[ Distance[A,(0,0)]>4,A, {(1,1)} ]", "{}");
	}

	@Test
	public void cmdLast() {
		t("Last[ {1,2,3,4,5} ]", "{5}");
		t("Last[ {1,2,3,4,5} , 2 ]", "{4, 5}");
		t("Last[ \"GeoGebra\" ]", "a");
		t("Last[ \"GeoGebra\" , 3 ]", "bra");
	}

	@Test
	public void cmdLaTeX() {
		t("FormulaText[ Polygon[(1,1),(2,1/2),4] ]", "1.25");
		t("FormulaText[ Polygon[(1,1),(2,1/2),4], false ]",
				"Polygon\\left(\\left(1,\\;1 \\right), "
						+ "\\left(2,\\;\\frac{1}{2} \\right), 4 \\right)");
		t("FormulaText[ Polygon[(1,1),(2,1/2),4], false, false ]",
				"Polygon\\left(\\left(1,\\;1 \\right), \\left(2,\\;"
						+ "\\frac{1}{2} \\right), 4 \\right)");
	}

	@Test
	public void cmdLCM() {
		t("LCM[ {1,2,3,4,5} ]", "60");
		t("LCM[ 42, 42 ]", "42");
	}

	@Test
	public void cmdLeftSide() {
		t("LeftSide[x^2=y^2]", "(((-x) + y) * (x + y))");
	}

	@Test
	public void cmdLeftSum() {
		t("LeftSum[ sin(x), 4, 13, 42 ]", "-1.681216143937807");
	}

	@Test
	public void cmdLength() {
		t("Length[ Curve(3t,4t,t,0,10), 2, 3 ]", "5");
		tRound("Length[ Curve(3t,4t,t,0,10), (3,5),(6,9) ]", "5");
		t("Length[ 3/4x, 0, 4 ]", "5");
		tRound("Length[ x^2, 1, 4 ]", "15.33969");
		tRound("Length[ sqrt(x), 1, 4 ]", "3.16784");
		tRound("Length[ 3/4x, (0,1), (4,4) ]", "5");
		t("Length[ Segment((1,0),(0,0))]", "1");
		t("Length[ Segment((3,4,12),(0,0))]", "13");
		t("Length[ CircleArc((0,0),(1/pi,0),(0,1))]", "0.5");
		t("Length[Vector((3,4))]", "5");
		t("Length[ (3,4) ]", "5");
		t("Length[ 1..10 ]", "10");
		t("freehandFunc=Function[{1,2,3,1,5,7,9}]", "freehand(x)");
		t("Length[ freehandFunc ]", "5");
		t("Length[ \"GeoGebra\" ]", "8");
	}

	@Test
	public void cmdLetterToUnicode() {
		t("LetterToUnicode[ \"X\" ]", "88");
	}

	@Test
	public void cmdLine() {
		t("Line[ (1,1), x+y=17 ]", "x + y = 2");
		t("Line[ (1,1),(2,1/2) ]", "0.5x + y = 1.5");
		t("Line[ (1,1), (1,1) ]", "?");
		t("Line[ (1,1), (5,1/5,-1) ]", "X = (1, 1, 0) + λ (4, -0.8, -1)");
		t("Line[ (5,1/5,-1), (1,1) ]", "X = (5, 0.2, -1) + λ (-4, 0.8, 1)");
		t("Line[ (5,1/5,-1), (5,1/5,-1) ]", "X = (?, ?, ?)");
		t("Line[ (1,1), (5,1/5,-1) ]", "X = (1, 1, 0) + λ (4, -0.8, -1)");
		t("Line[ (5,1/5,-1), (1,1) ]", "X = (5, 0.2, -1) + λ (-4, 0.8, 1)");
		t("Line[ (5,1/5,-1), (5,2,-8) ]", "X = (5, 0.2, -1) + λ (0, 1.8, -7)");
	}

	@Test
	public void cmdLineBisector() {
		t("PerpendicularBisector[ (1,1),(2,1/2) ]", "-x + 0.5y = -1.125");
		t("PerpendicularBisector[ Segment[(1,1),(2,1/2)] ]", "-x + 0.5y = -1.125");
	}

	@Test
	public void cmdLineGraph() {
		t("f=LineGraph({1,2},{3,4})", "DataFunction[{1, 2}, {3, 4},x]");
		t("Point(f)", "(1, 3)");
		t("Tangent(f, (1,3))", "?");
	}

	@Test
	public void cmdLineGraphInvalid() {
		t("LineGraph({3,2},{3,4})", "?");
		t("LineGraph({3,(4,5)},{3,4})", "?");
		t("LineGraph({1,2},{3,?})", "?");
		t("LineGraph({1,2},{3,Infinity})", "?");
	}

	@Test
	public void cmdLocus() {
		t("Locus[ x+y, Point[x^2+y^2=1] ]", "Locus[x + y, Point[x^(2) + y^(2) = 1]]");
		t("Locus[ SlopeField[ x/y ], Point[x^2+y^2=1] ]",
				"Locus[SlopeField[x / y], Point[x^(2) + y^(2) = 1]]");

		t("P=Point(xAxis)", "(0, 0)");
		t("Q=P+(1,0)", "(1, 0)");
		t("loc = Locus(Q,P)", "Locus[Q, P]");
		assertThat(get("loc"), isDefined());
	}

	@Test
	public void cmdLogistic() {
		prob("Logistic", "2,1",
				Unicode.EULER_STRING + "^(-((x - 2) / abs(1))) / (("
						+ Unicode.EULER_STRING
						+ "^(-((x - 2) / abs(1))) + 1)^2 abs(1))",
				"(" + Unicode.EULER_STRING + "^(-((x - 2) / abs(1))) + 1)^-1");
	}

	@Test
	public void cmdLogNormal() {
		prob("LogNormal", "2,1",
				"If(x " + Unicode.LESS_EQUAL + " 0, 0, " + Unicode.EULER_STRING
						+ "^(-((ln(x) - 2)^2 / (1^2 * 2))) / (abs(1) sqrt(2"
						+ Unicode.pi + ") x))",
				"If(x " + Unicode.LESS_EQUAL
						+ " 0, 0, erf((ln(x) - 2) / (sqrt(2) abs(1))) * 0.5 + 0.5)");
	}

	@Test
	public void cmdLowerSum() {
		t("LowerSum[ sin(x), 4, 13, 42 ]", "-2.1618997759936693");
	}

	@Test
	public void cmdMAD() {
		t("MAD( {1,2,3,4,5} )", "1.2"); // (2+1+1+2)/5 =1.2
		t("MAD( 1, 3 )", "1"); // (1+1)/2 =1
		tRound("MAD({20, 40, 41, 42, 40, 54}, {20, 6, 4, 5, 2})", "5.78524");
	}

	@Test
	public void cmdmad() {
		t("mad( {1,2,3,4,5} )", "1.2"); // (2+1+1+2)/5 =1.2
		t("mad( 1, 3 )", "1"); // (1+1)/2 =1
		tRound("mad({20, 40, 41, 42, 40, 54}, {20, 6, 4, 5, 2})", "5.78524");
	}

	@Test
	public void cmdMatrixRank() {
		t("MatrixRank[{{1}}]", "1");
	}

	@Test
	public void cmdMax() {
		tRound("Max[ x, 1, 2 ]", "(2, 2)");
		t("interval = 2 < x < 3", "2 < x < 3");
		t("intervalMax = Max[ interval ]", "3");
		t("Max[ {3,4,5,6} ]", "6");
		t("Max[ {3,4,5}, {2,1,0} ]", "4");
		t("Max[ 7, 5 ]", "7");
		t("SetValue(interval, ?)");
		t("intervalMax", "NaN");
	}

	@Test
	public void cmdMaximize() {
		t("slider:=Slider[0,5]", "0");
		t("Maximize[ 5-(3-slider)^2, slider ]", "3");
		tRound("ptPath:=Point[(x-3)^2+(y-4)^2=25]", "(0, 0)");
		tRound("Maximize[ y(ptPath), ptPath ]", "(3, 9)");
	}

	@Test
	public void cmdMean() {
		t("Mean[ {3,4,5} ]", "4");
		t("Mean[ {15,36,39}, {5,12,13} ]", "33.8");
	}

	@Test
	public void cmdmean() {
		t("mean[ {3,4,5} ]", "4");
		t("mean[ {15,36,39}, {5,12,13} ]", "33.8");
	}

	@Test
	public void cmdMeanX() {
		t("MeanX[ {(1, 1), (2, 2), (3, 3), (4, 0.25), (5, 0.2)} ]", "3");
	}

	@Test
	public void cmdMeanY() {
		t("MeanY[ {(1, 1), (2, 2), (3, 3), (4, 0.25), (5, 0.2)} ]", "1.29");
	}

	@Test
	public void cmdMedian() {
		t("Median[{1,2,3,4,5}]", "3");
		t("Median[{1,2,3,4,5},{1,2,3,4,5}]", "4");
	}

	@Test
	public void cmdMidpoint() {
		t("Midpoint[(0,0),(2,2)]", "(1, 1)");
		t("Midpoint[0<x<2]", "1");
		t("Midpoint[Segment[(0,0),(2,2)]]", "(1, 1)");
		t("Midpoint[(x-1)^2+(y-1)^2=pi]", "(1, 1)");
	}

	@Test
	public void cmdMin() {
		tRound("Min[ x, 1, 2 ]", "(1, 1)");
		t("Min[ 2 < x < 3 ]", "2");
		t("Min[ {3,4,5,6} ]", "3");
		t("Min[ {3,4,5}, {0,1,2} ]", "4");
		t("Min[ 7, 5 ]", "5");
	}

	@Test
	public void cmdMinimize() {
		t("slider:=Slider[0,5]", "0");
		t("Minimize[ 5+(3-slider)^2, slider ]", "3");
		tRound("ptPath:=Point[(x-3)^2+(y-4)^2=25]", "(0, 0)");
		tRound("Minimize[ y(ptPath), ptPath ]", "(3, -1)");
	}

	@Test
	public void cmdMinimumSpanningTree() {
		t("MinimumSpanningTree[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]",
				"MinimumSpanningTree[{(1, 1), (2, 2), (3, 3), (4, 1 / 4), (5, 1 / 5)}]");
	}

	@Test
	public void cmdMirror() {
		t("Reflect[ Polygon[(1,1),(2,1/2),4], x^2+y^2=1 ]",
				"((1 / ((1 + (0.5 * abs(t)) + (-0.25 * abs(t - 1)) + (-0.7499999999999999 *"
						+ " abs(t - 2)) + (0.24999999999999978 * abs(t - 3)) + (t *"
						+ " (-0.2500000000000001)) + 1)^(2) + (1 + (-0.25 * abs(t)) +"
						+ " (0.75 * abs(t - 1)) + (-0.25 * abs(t - 2)) + (-0.75 * abs(t - 3))"
						+ " + (t * (-0.5)) + 2)^(2)) * (1 + (0.5 * abs(t)) + (-0.25 * abs(t - 1))"
						+ " + (-0.7499999999999999 * abs(t - 2)) + (0.24999999999999978 *"
						+ " abs(t - 3)) + (t * (-0.2500000000000001)) + 1)), (1 / ((1 +"
						+ " (0.5 * abs(t)) + (-0.25 * abs(t - 1)) + (-0.7499999999999999"
						+ " * abs(t - 2)) + (0.24999999999999978 * abs(t - 3)) + (t *"
						+ " (-0.2500000000000001)) + 1)^(2) + (1 + (-0.25 * abs(t)) +"
						+ " (0.75 * abs(t - 1)) + (-0.25 * abs(t - 2)) + (-0.75 * abs(t - 3))"
						+ " + (t * (-0.5)) + 2)^(2)) * (1 + (-0.25 * abs(t)) + (0.75 * abs(t - 1))"
						+ " + (-0.25 * abs(t - 2)) + (-0.75 * abs(t - 3)) + (t * (-0.5)) + 2)))");
		t("Reflect[ Polygon[(1,1),(2,1/2),4], x+y=17 ]", "1.25");
		t("Reflect[ Polygon[(1,1),(2,1/2),4], (1,1) ]", "1.25");
	}

	@Test
	public void cmdMirrorPlane() {
		t("e:x-z=0", "x - z = 0");
		t("Reflect[Vector[(0,1)],e]", "(0, 1, 0)");
		tRound("Reflect[Curve[(t,t^3),t,0,5],e]", unicode("(0t, t^3, 1t)"));
		tRound("Reflect[y=x^2,e]",
				unicode("X = (0, 0, 0) + (0, 0.25 t^2, -0.5 t)"));
		tRound("Reflect[Polygon[(0,0),(0,1),4],e]", "1");
		t("Reflect[Polyline[(0,0),(0,1),(1,1)],e]", "2");
		t("Reflect[Line[(0,0),(0,1)],e]",
				"X = (0, 0, 0) + " + Unicode.lambda + " (0, 1, 0)");
		tRound("Reflect[x+y,e]", "(0u + 1 (u + v), v, 1u + 0 (u + v))");
		t("Reflect[(1,0),e]", "(0, 0, 1)");
		t("Reflect[x^3+y^3=0,e]", "?");
		t("picT=ToolImage[2]");
		t("Reflect[picT,e]", "picT'");
		tRound("Reflect[xAxis,e]",
				unicode("X = (0, 0, 0) + " + Unicode.lambda + " (0, 0, 1)"));
		tRound("Reflect[yAxis,e]",
				unicode("X = (0, 0, 0) + " + Unicode.lambda + " (0, 1, 0)"));
		tRound("Reflect[zAxis,e]",
				unicode("X = (0, 0, 0) + " + Unicode.lambda + " (1, 0, 0)"));
	}

	@Test
	public void cmdMode() {
		t("Mode[ {1,2,3,4,5} ]", "{}");
	}

	@Test
	public void cmdMod() {
		t("Mod[ -1, 1/3]", "0");
		t("Mod[ 4, 4 ]", "0");
		t("Mod[ x^4+4, x^2 ] ", "4");
	}

	@Test
	public void cmdName() {
		t("Name[ Polygon[(1,1),(2,1/2),4] ]", "Polygon((1, 1), (2, 1 / 2), 4)");
	}

	@Test
	public void cmdnCr() {
		t("nCr[ 5, -1 ]", "0");
		t("nCr[ 5, 1 ]", "5");
	}

	@Test
	public void cmdNDerivative() {
		tRound("NDerivative[x^2]", unicode("NDerivative(x^2)"));
		tRound("NDerivative[x^2, 5]", unicode("NDerivative(x^2, 5)"));
	}

	@Test
	public void cmdDerivativeNoCas() {
		// no CAS used; ExpressionNode manipulations should keep fractions
		tRound("Derivative[x^2/3]", unicode("2 / 3 x"));
	}

	@Test
	public void cmdNet() {
		tRound("Net[Cube[(0,0,2),(0,0,0)],1]",
				"24", "(0, 0, 2)", "(0, 0, 0)", "(2, 0, 0)",
				"(2, 0, 2)", "(0, 0, 4)", "(2, 0, 4)", "(2, 0, 6)",
				"(0, 0, 6)", "(-2, 0, 2)", "(-2, 0, 0)", "(0, 0, -2)",
				"(2, 0, -2)", "(4, 0, 0)", "(4, 0, 2)", "4", "4", "4",
				"4", "4", "4", "2", "2", "2", "2", "2", "2", "2", "2",
				"2", "2", "2", "2", "2", "2", "2", "2", "2", "2",
				"2");
		t("Net[Tetrahedron[(0,0,1),(0,1,0),(1,0,0)],Segment[(0,0,1),(0,1,0)]]",
				"NaN", "(NaN, NaN, NaN)", "(NaN, NaN, NaN)",
				"(NaN, NaN, NaN)", "(NaN, NaN, NaN)", "(NaN, NaN, NaN)",
				"(NaN, NaN, NaN)", "NaN", "NaN", "NaN", "NaN", "NaN",
				"NaN", "NaN", "NaN", "NaN", "NaN", "NaN", "NaN",
				"NaN");
	}

	@Test
	public void cmdNIntegral() {
		t("NIntegral[x^2,-1,1]", "0.6666666666666666");
		t("NIntegral[x^2]", "NIntegral[x^(2)]");
		t("G(x)=NIntegral(x/x^2, 1, 0, 10)",
				"NIntegral[x / x^(2), 1, 0, 10]");
		tRound("G(4)-ln(4)", "0");
		t("G1(x)=NIntegral(If(x==0,0,sin(x^2)/x^2), 0, 0, 10)",
				"NIntegral[If[x ≟ 0, 0, sin(x^(2)) / x^(2)], 0, 0, 10]");
		tRound("G1(4)", "1.2609");
		AlgebraTestHelper.shouldFail("Nintegral[exp(x),x,0,1]", "x", app);
	}

	@Test
	public void cmdNInvert() {
		t("ni(x)=NInvert[ sin(x) ]", "NInvert[sin(x)]");
		t("ni(sin(1))", "1");
	}

	@Test
	public void cmdNormalize() {
		t("Normalize[{1,3,2}]", "{0, 1, 0.5}");
		t("Normalize[{(1,1),(3,1),(2,1)}]", "{(0, 0), (1, 0), (0.5, 0)}");
	}

	@Test
	public void cmdNumerator() {
		t("Numerator[ (x + 2)/(x+1) ]", "x + 2");
		t("Numerator[ 3/7 ]", "3");
		t("Numerator[ 5/(-8) ]", "-5");
		t("Numerator[ 2/0 ]", "1");
		t("Numerator[ 1234/5678 ]", "617");
		t("Numerator[ 12345/67890 ]", "823");
		t("Numerator[ 123456/789012 ]", "10288");
		t("Numerator[ 1234567/8901234 ]", "1234567");
		t("Numerator[ 12345678/90123456 ]", "2057613");
		t("Numerator[ 123456789/12345678 ]", "13717421");
		t("Numerator[ 1234567890/1234567890 ]", "1");
		t("Numerator[ 12345678901/23456789012 ]", "12345678901");
		t("Numerator[ 123456789012/3456789012 ]", "10288065751");
		t("Numerator[ 1234567890123/45678901234 ]", "1234567890123");
		t("frac=10/6", "1.6666666666666667");
		t("Numerator(frac)", "5");
		t("Denominator(frac)", "3");
		t("frac2=-10/6", "-1.6666666666666667");
		t("Numerator(frac2)", "-5");
		t("Denominator(frac2)", "3");
		t("frac3=-10/-6", "1.6666666666666667");
		t("Numerator(frac3)", "5");
		t("Denominator(frac3)", "3");
		t("Numerator(0.125/0.166666666666666666)", "3");
		t("Numerator(0.125/3)", "1");
		t("Numerator(3/0.166666666666666666)", "18");
		t("Numerator[ 1/(-3) ]", "-1");
		t("Numerator[ 2/(-3) ]", "-2");
		t("Numerator[ infinity ]", "1");
		t("Numerator[ -infinity ]", "-1");
		t("Numerator[ 0 ]", "0");
	}

	@Test
	public void cmdNormal() {
		prob("Normal", "2,1",
				Unicode.EULER_STRING
						+ "^((-(x - 2)^2) / (1^2 * 2)) / (abs(1) sqrt(2"
						+ Unicode.pi + "))",
				"(erf((x - 2) / (abs(1) sqrt(2))) + 1) / 2");
	}

	@Test
	public void cmdNormalQuantilePlot() {
		t("NormalQuantilePlot[ {2,3,4}]",
				"{(2, -0.8193286198336103), (3, 0), (4, 0.8193286198336103), 2.8284271247461903}");
		t("Slope(Element(NormalQuantilePlot[ {2,3,4}],4))", "1");
	}

	@Test
	public void cmdNSolveODE() {
		// pendulum testcase from the wiki
		t("g = 9.8", "9.8");
		t("l = 2", "2");
		t("a = 5", "5");
		t("b = 3", "3");
		t("y1'(t, y1, y2) = y2", "y2");
		t("y2'(t, y1, y2) = (-g) / l sin(y1)", "(((-9.8)) / 2 * sin(y1))");
		t("nint=NSolveODE({y1', y2'}, 0, {a, b}, 20)",
				"NSolveODE[{y1', y2'}, 0, {a, b}, 20]",
				"NSolveODE[{y1', y2'}, 0, {a, b}, 20]");

		tRound("x1 = l sin(y(Point(nint_1, 0)))", "-1.91785");
		tRound("y1 = -l cos(y(Point(nint_1, 0)))", "-0.56732");
		tRound("Segment((0, 0), (x1, y1))", "2");
		// undefined testcase
		t("yu1'(t, y1, y2) = ?", "NaN");
		t("yu2'(t, y1, y2) = ?", "NaN");
		t("NSolveODE({yu1', yu2'}, 0, {a, b}, 20)",
				"NSolveODE[{yu1', yu2'}, 0, {a, b}, 20]",
				"NSolveODE[{yu1', yu2'}, 0, {a, b}, 20]");
	}

	@Test
	public void cmdnPr() {
		t("nPr[8,7]", "40320");
	}

	@Test
	public void cmdObject() {
		t("txt = \"GeoGebra\"", "GeoGebra");
		t("Name[ txt ]", "txt");
	}

	@Test
	public void cmdOctahedron() {
		String[] dodeca = new String[] { "0.4714", "(0, 0.57735, 0.8165)",
				"(0.5, -0.28868, 0.8165)", "(1, 0.57735, 0.8165)", "0.43301",
				"0.43301", "0.43301", "0.43301", "0.43301", "0.43301",
				"0.43301", "0.43301", "1", "1", "1", "1", "1", "1", "1", "1",
				"1", "1", "1", "1" };
		platonicTest("Octahedron", 60, dodeca);
	}

	@Test
	public void cmdOrdinal() {
		t("Ordinal[ 4 ]", "4th");
	}

	@Test
	public void cmdOrdinalRank() {
		t("OrdinalRank[ {1,2,3,4,5} ]", "{1, 2, 3, 4, 5}");
	}

	@Test
	public void cmdOsculatingCircle() {
		tRound("OsculatingCircle[ (-1, 0), Conic[{1, 1, 1, 2, 2, 3}] ]",
				"x² + y² + 2x + 1y = -1");
		t("OsculatingCircle[ (0, 0), x^2 ]", "x² + y² - y = 0");
		t("OsculatingCircle[ (1,1), (x - 2)² + (y - 3)² = 4 ]", "?");
	}

	@Test
	public void cmdOrthogonalLine() {
		// 2D
		t("PerpendicularLine[ (1,2), x+y=7 ]", "-x + y = 1");
		t("PerpendicularLine[ (1,2), Segment[(1,6),(6,1)] ]", "-x + y = 1");
		t("PerpendicularLine[ (1,2),Vector[(1,3)]]", "-x - 3y = -7");
		// 3D
		t("PerpendicularLine[ (1,2,0), x+y=7 ]",
				"X = (1, 2, 0) + " + Unicode.lambda + " (1, 1, 0)");
		t("PerpendicularLine[ (1,2,0), Segment[(1,6),(6,1)] ]",
				"X = (1, 2, 0) + " + Unicode.lambda + " (-5, -5, 0)");
		t("PerpendicularLine[ (1,2,0),Vector[(1,3)]]",
				"X = (1, 2, 0) + " + Unicode.lambda + " (3, -1, 0)");
		t("PerpendicularLine[(1,1,1),z=0]",
				"X = (1, 1, 1) + " + Unicode.lambda + " (0, 0, -1)");
		t("PerpendicularLine[(1,1,1),y=0,xOyPlane]",
				"X = (1, 1, 1) + " + Unicode.lambda + " (0, 1, 0)");
		tRound("PerpendicularLine[(1,1,1),y=0,space]",
				"X = (1, 1, 1) + " + Unicode.lambda + " (0, 0.70711, 0.70711)");
		t("PerpendicularLine[x=1,y=1]",
				"X = (1, 1, 0) + " + Unicode.lambda + " (0, 0, 1)");
	}

	@Test
	public void cmdOrthogonalPlane() {
		t("OrthogonalPlane[ (0,0,1), X=(p,2p,3p) ]", "x + 2y + 3z = 3");
		t("OrthogonalPlane[ (0,0,1), Vector[(1,2,3)] ]", "x + 2y + 3z = 3");
	}

	@Test
	public void cmdOrthogonalVector() {
		t("PerpendicularVector[ 3x = 4y ]", "(3, -4)");
		t("PerpendicularVector[ Segment((0,0),(3,4)) ]", "(-4, 3)");
		t("PerpendicularVector[Vector((3,4))  ]", "(-4, 3)");
		t("PerpendicularVector[(3,4)  ]", "(-4, 3)");
		t("PerpendicularVector[ 2x+2y+z=1 ]", "(2, 2, 1)");
		syntaxes -= 2;
	}

	@Test
	public void cmdPan() {
		t("Pan[ 42, 4 ]");
	}

	@Test
	public void cmdParabola() {
		t("Parabola[ (1,1), x+y=17 ]", "x² - 2x y + y² + 30x + 30y = 285");
	}

	@Test
	public void cmdParameter() {
		t("Parameter[ (x+y)^2=x-y ]", "0.35355339059327373");
	}

	@Test
	public void cmdParametricDerivative() {
		t("ParametricDerivative[Curve[t^2,sin(t),t,-7,7]]",
				"(t^(2), cos(t) / ((2 * t)))");
	}

	@Test
	public void cmdParseToFunction() {
		t("ParseToFunction[\"x^2\"]", "x^(2)");
		t("ParseToFunction[\"x+y\",{}]", "x + y");
		t("ParseToFunction[\"u+v\",{\"u\",\"v\"}]", "u + v");
		t("ParseToFunction[\"x+\"]", "?");
		t("ParseToFunction[\"y\"]", "?");
		t("ParseToFunction[\"x+\",{}]", "?");
		t("f(x) = 3x² + 2", "(3 * x^(2)) + 2");
		t("txt = \"f(x) = 3x + 1\"", "f(x) = 3x + 1");
		t("ParseToFunction[ f, txt ]", "(3 * x) + 1");
	}

	@Test
	public void cmdParseToNumber() {
		t("ParseToNumber[ \"7\"]", "7");
		t("ParseToNumber[ \"/\"]", "NaN");
		t("n1 = 5", "5");
		t("txt = \"6\"", "6");
		t("ParseToNumber[ n1, txt ]", "6"); // valid
		t("ParseToNumber[ 42, \"GeoGebra\" ]", "NaN"); // invalid, but all exceptions should be
		// caught
	}

	@Test
	public void cmdPathParameter() {
		t("PathParameter[ Point[x^2+y^2=1] ]", "0.5");
	}

	@Test
	public void cmdPascal() {
		intProb("Pascal", "3,0.5", "4", "0.11719", "0.77344");
	}

	@Test
	public void cmdPayment() {
		t("Payment[ 42, 4, 13]", "-546.0001597051597");
		t("Payment[ 42, 4, 13,1]", "-546.000171990172");
		t("Payment[ 42, 4, 13,0,1]", "-12.697678132678133");
	}

	@Test
	public void cmdPlane() {
		t("Plane[ (0,0,1),(1,0,0),(0,1,0) ]", "x + y + z = 1");
		t("Plane[ Polygon[(0,0,1),(2,0,0),(0,3,0)] ]", "3x + 2y + 6z = 6");
		t("Plane[ Ellipse[(0,0,1),(2,0,0),(0,3,0)] ]", "3x + 2y + 6z = 6");
		t("Plane[ (1,2,3),X=(s,s,s) ]", "x - 2y + z = 0");
		t("Plane[ (1,2,3),x+y+z=0 ]", "x + y + z = 6");
		t("Plane[ X=(s,s,s+1),X=(s,s,s) ]", "-x + y = 0");
		t("Plane[ (0,0,1),Vector[(1,0,0)],Vector[(0,1,0)] ]", "z = 1");
	}

	@Test
	public void cmdPlaneBisector() {
		t("PlaneBisector[(1,1),(1,1,2)]", "z = 1");
		t("PlaneBisector[Segment[(1,1),(1,1,2)]]", "z = 1");
	}

	@Test
	public void cmdPlaySound() {
		t("PlaySound[ false ]");
		t("PlaySound[ sin(x), 42, 50 ]");
		t("PlaySound[ sin(x), 42, 50, 13, 4]");
		t("PlaySound[ false ]"); // test this twice instead of playing file
		t("PlaySound[ \"CDEFGAHC\", 42 ]");
		t("PlaySound[ 13, 4, 42 ]");
	}

	@Test
	public void cmdPenStroke() {
		t("PenStroke()", "PenStroke[]");
		t("PenStroke[(1,1),(2,2)]",
				"PenStroke[1.0000E0,1.0000E0,2.0000E0,2.0000E0,NaN,NaN]");
	}

	@Test
	public void cmdPercentile() {
		t("Percentile[ {1,2,3,4,5}, 0.05 ]", "1");
	}

	@Test
	public void cmdPerimeter() {
		t("Perimeter[ x^2+y^2=1 ]", "6.283185307179586");
		t("Perimeter[ Polygon[(1,1),(2,1/2),(3,1/3)] ]", "4.240012850578518");

		t("P=Point(xAxis)", "(0, 0)");
		t("Q=P+(1,0)", "(1, 0)");
		t("loc = Locus(Q,P)", "Locus[Q, P]");
		t("round[Perimeter[loc]]", "399996");
	}

	@Test
	public void cmdPeriods() {
		t("Periods[ 10%/12, -200, -400, 10000 ]", "39.97894632819951");
		t("Periods[ 10%/12, -200, -400, 10000, 1 ]", "39.70201587954573");
	}

	@Test
	public void cmdPresentValue() {
		t("PresentValue[ 42, 4, 13]", "-0.30952371898803116");
		t("PresentValue[ 42, 4, 13, 1]", "-0.3095240114882381");
		t("PresentValue[ 42, 4, 13, 0, 1]", "-13.30951991648534");
	}

	@Test
	public void cmdPerpendicularPlane() {
		t("PerpendicularPlane[(3,2,7),Line[(1,1,1),(1,1,3)]]", "z = 7");
		t("PerpendicularPlane[(3,2,7),Vector[(1,1,0)]]", "x + y = 5");
	}

	@Test
	public void cmdPMCC() {
		t("CorrelationCoefficient[ {1,2,3,4,5}, {1,2,3,4,5} ]", "1");
		t("CorrelationCoefficient[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", "-0.44056070999419766");
	}

	@Test
	public void cmdPoisson() {
		intProb("Poisson", "2", "1", "0.27067", "0.40601");
	}

	@Test
	public void cmdPoissonWithDoubles() {
		t("Poisson[10.5, 11..16]", StringTemplate.maxPrecision, "0.439655709066945");
	}

	@Test
	public void cmdPoint() {
		t("Point[ xAxis ]", "(0, 0)");
		tRound("Point[ x^2+y^2=1, 0.25]", "(0, -1)");
		t("Point[ (1,1),Vector((3,4))]", "(4, 5)");
		t("Point[ {1,2}]", "(1, 2)");
		t("Point[ 0x < 1]", "(0, 0)");
		t("Point[ 0x > 1]", "(NaN, NaN)");
	}

	@Test
	public void cmdPointIn() {
		t("PointIn[ Polygon[(1,1),(2,1/2),(3,1/3)] ]", "(1, 1)");
	}

	@Test
	public void cmdPointList() {
		t("PointList[ {1,2,3,4,5} ]", "{}");
	}

	@Test
	public void cmdPolar() {
		t("Polar[ (1,1), x^2+y^2=1 ]", "x + y = 1");
		t("Polar[ x+y=17, x^2+y^2=1 ]", "(0.058823529411764705, 0.058823529411764705)");
	}

	@Test
	public void cmdPolygon() {
		t("Polygon[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", "3.4499999999999997");
		t("Polygon[ (1,1),(2,1/2), 42 ]", "175.14095339471965");
		t("Polygon[ (1,1), (2,1/2), (3,1/3) ]", "0.16666666666666652",
				"1.118033988749895", "1.0137937550497031", "2.1081851067789197");
	}

	@Test
	public void cmdPolyLine() {
		t("PolyLine[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", "6.755851322151136");
		t("PolyLine[ (1,1), (3,1/3), (5,1/5) ]", "4.112624623895307");
	}

	@Test
	public void cmdPolynomial() {
		t("Polynomial[ sin(x) ]", "?");
		t("Polynomial[ 1*x^2-1*x+1 ]", "x^(2) - x + 1");
		t("Polynomial[ -x*(x+1)*(x-1) ]", "(-x^(3)) + x");
		t("Polynomial[ (2x+3)^3 ]",
				"(8 * x^(3)) + (36 * x^(2)) + (54 * x) + 27");
		t("Polynomial[ {(1,1),(-1,1),(0,0) } ]", "x^(2)");
		t("Polynomial[ {(1,0),(-1,2),(0,0) } ]", "x^(2) - x");
	}

	@Test
	public void cmdPolynomialMultiVariable() {
		t("Polynomial[y^2+(x+y)^2]", "x^(2) + (2 * (x * y)) + (2 * y^(2))");
		t("Polynomial[x+y-1]", "x + y - 1");
		t("Polynomial[x-y+1]", "x - y + 1");
		t("Polynomial[-y+1+x]", "x - y + 1");
		t("Polynomial[-x+y-1]", "(-x) + y - 1");
		t("Polynomial[x+y-z]", "?");
		t("Polynomial[0x+0y-1]", "-1");
		t("Polynomial(sin(x) + y)", "?");
		t("Polynomial(x^2 y^2 + x^3 + x^2 y + y^3 + x*y + 1)",
				"(x^(2) * y^(2)) + x^(3) + (x^(2) * y) + y^(3) + (x * y) + 1");
	}

	@Test
	public void cmdPrimeFactors() {
		t("PrimeFactors[ 42 ]", "{2, 3, 7}");
	}

	@Test
	public void cmdPrism() {
		tRound("Prism[(0,0,0),(1,0,0),(0,1,0),(0,0,1)]",
				"0.5", "(1, 0, 1)", "(0, 1, 1)", "0.5", "1",
				eval("sqrt(2)"), "1", "0.5", "1", eval("sqrt(2)"), "1",
				"1", "1", "1", "1", eval("sqrt(2)"), "1");
		tRound("Prism[Polygon[(0,0,0),(1,0,0),(0,1,0)],(0,0,1)]",
				"0.5", "(1, 0, 1)", "(0, 1, 1)", "1",
				eval("sqrt(2)"), "1", "0.5", "1", "1", "1", "1",
				eval("sqrt(2)"), "1");
		tRound("Prism[Polygon[(-3,0,0),(0,-3,0),(3,0,0),(0,3,0)],4]",
				"72", "(-3, 0, 4)", "(0, -3, 4)", "(3, 0, 4)",
				"(0, 3, 4)", eval("12sqrt(2)"), eval("12sqrt(2)"),
				eval("12sqrt(2)"), eval("12sqrt(2)"), "18", "4", "4",
				"4", "4", eval("3sqrt(2)"), eval("3sqrt(2)"),
				eval("3sqrt(2)"), eval("3sqrt(2)"));
	}

	@Test
	public void cmdProduct() {
		t("Product[ {1,2,3,4} ]", "24");
		t("Product[ 1..10,  5 ]", "120");
		t("Product[ {1,2,3},  {100,1,2} ]", "18");
		t("Product[ {{1,2,3},  {100,1,2}} ]", "{100, 2, 6}");
		tRound("Product[ k/(k+1),k,1,7 ]", "0.125");
		t("Product[{x,y}]", "(x * y)");
		t("Product[ Sequence({{1,k},{0,1}},k,1,10) ]", "{{1, 55}, {0, 1}}");
		t("Product[ (k,k),k,1,5 ]", "-480 - 480" + Unicode.IMAGINARY);
	}

	@Test
	public void cmdPyramid() {
		tRound("Pyramid[(0,0,0),(1,0,0),(0,1,0),(0,0,1)]",
				eval("1/6"), "0.5", "0.5", eval("sqrt(3)/2"),
				"0.5", "1", eval("sqrt(2)"), "1", "1", eval("sqrt(2)"),
				eval("sqrt(2)"));
		tRound("Pyramid[Polygon[(0,0,0),(1,0,0),(0,1,0)],(0,0,1)]",
				eval("1/6"), "0.5", eval("sqrt(3)/2"), "0.5",
				"1", eval("sqrt(2)"), eval("sqrt(2)"));
		tRound("Pyramid[Polygon[(-3,0,0),(0,-3,0),(3,0,0),(0,3,0)],4]",
				"24", "(0, 0, 4)", "9.60469", "9.60469",
				"9.60469", "9.60469", "5", "5", "5", "5");
	}

	@Test
	public void cmdQ1() {
		t("Q1[ {1,2,3,4,5} ]", "1.5");
		t("Q1[ {1,2,3,4,5}, {1,2,3,4,5} ]", "3");
	}

	@Test
	public void cmdQ3() {
		t("Q3[ {1,2,3,4,5} ]", "4.5");
		t("Q3[ {1,2,3,4,5}, {1,2,3,4,5} ]", "5");
	}

	@Test
	public void cmdQuadricSide() {
		tRound("Side[Cone[x^2+y^2=9,4]]", eval("15pi"));
	}

	@Test
	public void cmdRadius() {
		t("Radius[ x^2+y^2=1 ]", "1");
	}

	@Test
	public void cmdRandom() {
		t("RandomBetween[ ]", "0.30871945533265976"); // since RandomBetween is alias for Random and
		// both Random() and random() should do the same
		t("RandomBetween[ 42, 50 ]", "47");
		t("RandomBetween[ 42, 50, true ]", "44");
		t("RandomBetween[ 1, 10, 3 ]", "{10, 4, 3}");
	}

	@Test
	public void cmdRandomBinomial() {
		t("RandomBinomial[ 42, 0.05 ]", "2");
	}

	@Test
	public void cmdRandomElement() {
		t("RandomElement[{-4}]", "-4");
	}

	@Test
	public void cmdRandomNormal() {
		//t("RandomNormal[ 42, 4 ]", "46.871736265019415");
	}

	@Test
	public void cmdRandomPoisson() {
		t("RandomPoisson[ 42 ]", "41");
	}

	@Test
	public void cmdRandomUniform() {
		t("RandomUniform[ 42, 50 ]", "48.61857939211996");
		t("RandomUniform[ 42, 50, 42 ]",
				"{43.20825243623007, 48.67092988355333, 45.68245098128929, 44.24457593338248,"
						+ " 43.56771365938525, 43.43418752699934, 48.92549437061882, "
						+ "45.892725294609555, 45.36777365304823, 47.062958330826234, "
						+ "47.598869160537376, 44.522627600461384, 46.57296244423981, "
						+ "44.96809831747009, 48.97451676771871, 48.44584754129598, "
						+ "46.92970948028143, 44.99487484871204, 47.57798983415783, "
						+ "49.26891664166057, 43.56917657505481, 48.472998533821915, "
						+ "47.023466203307784, 45.70719416822676, 44.44463324533959, "
						+ "46.319275474074956, 47.080888115651106, 43.01006258639012, "
						+ "45.97861513980737, 42.21851333028773, 42.291876133521995, "
						+ "45.87075083963444, 47.240427048878566, 45.166130241369935, "
						+ "49.52137972548305, 45.07688675133833, 47.169855830381145, "
						+ "48.16372510219153, 49.15234236654727, 46.79069629716014, "
						+ "49.80827577294727, 42.61668090348201}");
	}

	@Test
	public void cmdRandomPointIn() {
		t("RandomPointIn[x^2+y^2=1]", "(-0.5919702312242243, -0.6856114560964431)");
		t("RandomPointIn[Polygon[(0,0),(1,0),(0,1)]]", "(0.3691214939418974, 0.3602548753661351)");
		t("RandomPointIn[0,0,1,1]", "(0, 1)");
	}

	@Test
	public void cmdRandomDiscrete() {
		t("RandomDiscrete[{1,2,3},{4,5,6}]", "3");
		t("RandomDiscrete[{1,2,3},{}]", "NaN");
		t("RandomDiscrete[{1,2,3},{4,5,-9}]", "1");
	}

	@Test
	public void cmdRate() {
		t("Rate[ 5*12, -300, 10000 ]", "0.021750422875496778");
		t("Rate[ 42, 4, 13, 1]", "NaN");
		t("Rate[ 42, 4, 13, 0, 1]", "-0.9999999999997204");
		t("Rate[ 42, 4, 13, 0, 1, 42]", "NaN");
	}

	@Test
	public void cmdRay() {
		t("Ray[ (3,1/3), (1,1) ]", "-0.6666666666666667x - 2y = -2.6666666666666665");
		t("Ray[ (3,1/3), Vector[(1,1)] ]", "-x + y = -2.6666666666666665");
	}

	@Test
	public void cmdRandomPolynomial() {
		app.setRandomSeed(42);
		t("RandomPolynomial[5,-1,1]", "x^(5) - x^(4) + x^(3) - x^(2) - x + 1");
		t("RandomPolynomial[5,-1,1]", "(-x^(5)) + x^(4) + x^(3) + x + 1");
		t("RandomPolynomial[5,-1,1]", "x^(5) - x^(4) + x^(3) + x^(2) - x - 1");
		t("RandomPolynomial[5,-1,1]", "(-x^(5)) + x^(4) + x^(2) + x");
		t("RandomPolynomial[5,-1,1]", "(-x^(5)) - x + 1");
		t("RandomPolynomial[5,-1,1]", "(-x^(5)) + x^(4) + x^(3) + x^(2) - x");
		t("RandomPolynomial[5,-1,1]", "x^(5) + x^(4) - x^(3) - 1");
		t("RandomPolynomial[5,-2,2]",
				"(2 * x^(5)) + (2 * x^(3)) - (2 * x^(2)) + 1");
		t("RandomPolynomial[5,-3,3]",
				"(2 * x^(5)) - x^(4) - (3 * x^(3)) + (2 * x^(2)) + 3");
		t("RandomPolynomial[5,-5,4]",
				"(-5 * x^(5)) - (4 * x^(4)) + (4 * x^(3)) - (2 * x^(2)) - (5 * x) - 5");
		t("RandomPolynomial[5,-2,5]",
				"x^(5) + (5 * x^(4)) - x^(3) + (4 * x) + 1");
	}

	@Test
	public void cmdRectangleSum() {
		t("RectangleSum[ sin(x), 4, 13, 42, 0.05 ]", "-1.6703498237676007");
	}

	@Test
	public void cmdReducedRowEchelonForm() {
		t("ReducedRowEchelonForm[{{1,2},{3,4}}]", "{{1, 0}, {0, 1}}");
	}

	@Test
	public void cmdRelation() {
		// don't test; user interaction needed
	}

	@Test
	public void cmdRemoveUndefined() {
		t("RemoveUndefined[ {1,2,3,4,5} ]", "{1, 2, 3, 4, 5}");
	}

	@Test
	public void cmdResidualPlot() {
		t("ResidualPlot[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)}, sin(x) ]",
				"{(1, 0.1585290151921035), (2, 1.0907025731743183), (3, 2.8588799919401326), "
						+ "(4, 1.0068024953079282), (5, 1.1589242746631385)}");
	}

	@Test
	public void cmdReverse() {
		t("Reverse[ {1,2,3,4,5} ]", "{5, 4, 3, 2, 1}");
	}

	@Test
	public void cmdReadText() {
		ScreenReaderAdapter screenReader = Mockito.spy(ScreenReaderAdapter.class);
		((EuclidianViewNoGui) app.getActiveEuclidianView())
				.setScreenReader(screenReader);
		t("SetActiveView(1)");
		t("ReadText(\"Can anybody hear me?\")");
		verify(screenReader).readDelayed("Can anybody hear me?");
	}

	@Test
	public void cmdRemove() {
		t("Remove[{1,2,2},{2}]", "{1, 2}");
	}

	@Test
	public void cmdRename() {
		t("Rename[ 6*7, \"a\" ]");
		assertEquals(
				get("a").toValueString(StringTemplate.defaultTemplate), "42");
		t("Rename[ a, \"b\" ]");
		assertEquals(
				get("b").toValueString(StringTemplate.defaultTemplate), "42");
		Assert.assertNull(get("a"));
		t("Rename[ b, \"  cc  d  \" ]");
		assertEquals(
				get("cc").toValueString(StringTemplate.defaultTemplate), "42");
		Assert.assertNull(get("b"));
		AlgebraTestHelper.shouldFail("Rename[ cc, \"\" ]", "Illegal", app);
		assertNotNull(get("cc"));
		AlgebraTestHelper.shouldFail("Rename[ cc, \"42\" ]", "Illegal", app);
		assertNotNull(get("cc"));
		AlgebraTestHelper.shouldFail("Rename[ cc, \"A_{}\" ]", "Illegal", app);
		assertNotNull(get("cc"));
		AlgebraTestHelper.shouldFail("Rename[ cc, \"A_{\" ]", "Illegal", app);
		assertNotNull(get("cc"));
		t("Rename[ cc, \"A_\" ]");
		Assert.assertNull(get("cc"));
		assertEquals(
				get("A").toValueString(StringTemplate.defaultTemplate), "42");
	}

	@Test
	public void cmdReflect3D() {
		t("Reflect[sin(x)+sin(y), x+y+z=0]",
				"((0.33333333333333337 * u) + (-0.6666666666666666 * v) +"
						+ " (-0.6666666666666666 * (sin(u) + sin(v))), (-0.6666666666666666 * u) + "
						+ "(0.33333333333333337 * v) + (-0.6666666666666666 * (sin(u) + sin(v))), "
						+ "(-0.6666666666666666 * u) + (-0.6666666666666666 * v) + "
						+ "(0.33333333333333337 * (sin(u) + sin(v))))");
	}

	@Test
	public void cmdRepeat() {
		t("Repeat[2, UpdateConstruction[]]");
	}

	@Test
	public void cmdReplaceAll() {
		t("ReplaceAll(\"3cos(t)+cos(2y)\", \"cos\", \"sin\") ", "3sin(t)+sin(2y)");
		t("ReplaceAll(\"3cos(t)+cos(2y)\", \"(\", \"[\") ", "3cos[t)+cos[2y)");
		t("ReplaceAll(\"3cos(t)\", \"\", \"*\") ", "*3*c*o*s*(*t*)*");
	}

	@Test
	public void cmdRigidPolygon() {
		t("RigidPolygon[ (0,0), (1,0), (0,1) ]");
		t("RigidPolygon[ Polygon[(1,1),(2,1/2),(3,1/3)] ]");
		t("RigidPolygon[ Polygon[(1,1),(2,1/2),(3,1/3)], 42,4 ]");
	}

	@Test
	public void cmdRightSide() {
		t("RightSide[x^2=y^2]", "0");
	}

	@Test
	public void cmdRoot() {
		t("Root[ x^3-x ]", "(-1, 0)", "(0, 0)", "(1, 0)");
		t("Root[ x^3-2x^2+x ]", "(0, 0)", "(1, 0)");
		t("Root[ x^3-3x^2+3x-1 ]", "(1, 0)");
		tRound("Root[ sin(x*pi), 1.3 ]", "(1, 0)");
		tRound("Root[ sin(x*pi), -3,3 ]", "(0, 0)");
		t("Root[9x^4 - x^2 ]", "(-0.3333333333333333, 0)", "(0, 0)",
				"(0.3333333333333333, 0)");
		t("Root[x^4-4x^2]", "(-2, 0)", "(0, 0)", "(2, 0)");
		t("a:=4/5", "0.8");
		t("Root(a)", "(NaN, NaN)");
		t("b:=0/5", "0");
		t("Root(b)", "(NaN, NaN)");
		t("Root(x^6 - 2x^5 - 4x^4 + 8x^3)", "(-2, 0)", "(0, 0)", "(2, 0)");
		t("Root(x^8 - x^4)", "(-1, 0)", "(0, 0)", "(1, 0)");
	}

	@Test
	public void cmdRootHighDeg() {
		long time = System.currentTimeMillis();
		t("Root((x+1)^99)", "(-1, 0)");
		t("Root((x+1)^99+1)", "(NaN, NaN)");
		assertTrue(System.currentTimeMillis() - time < 1000);
	}

	@Test
	public void cmdExtremumHighDeg() {
		long time = System.currentTimeMillis();
		StringTemplate lowPrecision = StringTemplate.printDecimals(
				ExpressionNodeConstants.StringType.GEOGEBRA, 2, false);
		t("Extremum((x+1)^24)", "(-1, 0)");
		t("Extremum((x+1)^98)", lowPrecision, "(-1, 0)");
		// nearly horizontal => x coordinate random, assert on y only
		t("y(Extremum((x+1)^98+1))", lowPrecision, "1");
		assertTrue(System.currentTimeMillis() - time < 1000);
	}

	@Test
	public void cmdRootList() {
		t("RootList[ {1,2,3,4,5} ]", "{(1, 0), (2, 0), (3, 0), (4, 0), (5, 0)}");
	}

	@Test
	public void cmdRootMeanSquare() {
		t("RootMeanSquare[ {1,2,3,4,5} ]", "3.3166247903554");
	}

	@Test
	public void cmdRoots() {
		t("ZoomIn(-10,-2,20,2)"); // makes the test deterministic
		t("Roots[ sin(x), 4, 13 ]",
				"(6.283185305816606, 0)", "(9.424777959654795, 0)", "(12.566370613845491, 0)");
		t("flat(x)=2.00011sin(x/2)-x", "(2.00011 * sin(x / 2)) - x");
		tRound("Roots(flat,-0.05,0.05)", "(-0.03633, 0)", "(0, 0)", "(0.03633, 0)");
		tRound("Roots(flat,-0.005,0.005)", "(0, 0)");
	}

	@Test
	public void cmdRotate() {
		t("Rotate[ Polygon[(1,1),(2,1/2),4], 30" + Unicode.DEGREE_STRING + "]", "1.25");
		t("Rotate[ Polygon[(1,1),(2,1/2),4], 30" + Unicode.DEGREE_STRING + ", (1,1) ]", "1.25");
	}

	@Test
	public void cmdRotateText() {
		t("RotateText[ \"GeoGebra\",  30" + Unicode.DEGREE_STRING + " ]",
				"\\rotatebox{29.999999999999996}{ \\text{ GeoGebra }  }");
	}

	@Test
	public void cmdRow() {
		t("Row[ A1 ]", "1");
	}

	@Test
	public void cmdRunClickScript() {
		t("RunClickScript[(1,1)]");
	}

	@Test
	public void cmdRunUpdateScript() {
		t("RunUpdateScript[(1,1)]");
	}

	@Test
	public void cmdRSquare() {
		t("RSquare[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)}, sin(x) ]", "-1.0312547732729542");
	}

	@Test
	public void cmdSample() {
		t("Sample({3}, 1)", "{3}");
		t("Sample({3}, 1, true)", "{3}");
	}

	@Test
	public void cmdSampleSD() {
		t("SampleSD[ {1,2,3,4,5} ]", "1.5811388300841898");
		t("SampleSD[ {1,2,3,4,5}, {1,2,3,4,5} ]", "1.2909944487358058");
	}

	@Test
	public void cmdstdev() {
		t("stdev[ {1,2,3,4,5} ]", "1.5811388300841898");
		t("stdev[ {1,2,3,4,5}, {1,2,3,4,5} ]", "1.2909944487358058");
	}

	@Test
	public void cmdstdevp() {
		t("stdevp[ {1,2,3,4,5} ]", "1.4142135623730951");
		t("stdevp[ {1,2,3,4,5}, {1,2,3,4,5} ]", "1.2472191289246477");
	}

	@Test
	public void cmdSampleSDX() {
		t("SampleSDX[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", "1.5811388300841898");
	}

	@Test
	public void cmdSampleSDY() {
		t("SampleSDY[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", "1.2022894826122368");
	}

	@Test
	public void cmdSampleVariance() {
		t("SampleVariance[ {1,2,3,4,5} ]", "2.5");
		t("SampleVariance[ {1,2,3,4,5}, {1,2,3,4,5} ]", "1.6666666666666674");
	}

	@Test
	public void cmdScientificText() {
		t("ScientificText[e,5]", "2.7183 \\times 10^{0}");
		t("ScientificText[0.002]", "2 \\times 10^{-3}");
	}

	@Test
	public void cmdSD() {
		t("SD[ {1,2,3,4,5} ]", "1.4142135623730951");
		t("SD[ {1,2,3,4,5}, {1,2,3,4,5} ]", "1.2472191289246477");
	}

	@Test
	public void cmdSDX() {
		t("SDX[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", "1.4142135623730951");
	}

	@Test
	public void cmdSDY() {
		t("SDY[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", "1.0753604047016052");
	}

	@Test
	public void cmdSecondAxis() {
		t("MinorAxis[ x^2+y^2=1 ]", "x = 0");
	}

	@Test
	public void cmdSecondAxisLength() {
		t("SemiMinorAxisLength[ x^2+y^2=1 ]", "1");
	}

	@Test
	public void cmdSector() {
		t("Sector[ x^2+y^2=1, 0.05, 0.5 ]", "0.225");
		t("Sector[ x^2+y^2=1, (1,1),(2,1/2) ]", "2.871382903454501");
	}

	@Test
	public void cmdSegment() {
		t("Segment[ (1,1), 4 ]", "4", "(5, 1)");
		t("Segment[ (1,1),(2,1/2) ]", "1.118033988749895");
	}

	@Test
	public void cmdSelectedElement() {
		t("SelectedElement[ {1,2,3,4,5} ]", "1");
	}

	@Test
	public void cmdSelectedIndex() {
		t("SelectedIndex[ {1,2,3,4,5} ]", "1");
	}

	@Test
	public void cmdSelectObjects() {
		t("SelectObjects[]");
		t("SelectObjects[ Polygon[(1,1),(2,1/2),4], Polygon[(1,1),(2,1/2),4], (1,1) ]");
	}

	@Test
	public void cmdSemicircle() {
		t("Semicircle[ (1,1),(2,1/2) ]", "1.7562036827601817");
	}

	@Test
	public void cmdSetActiveView() {
		t("SetActiveView[ 1 ]");
		t("SetActiveView[ Plane[(1,0),(2,3),(0,1)] ]");
	}

	@Test
	public void cmdSetAxesRatio() {
		t("SetAxesRatio[ 42, 42 ]");
	}

	@Test
	public void cmdSetCaption() {
		t("SetCaption[ Polygon[(1,1),(2,1/2),4], \"GeoGebra\" ]");
	}

	@Test
	public void cmdSetConditionToShowObject() {
		t("SetConditionToShowObject[ Polygon[(1,1),(2,1/2),4], false ]");
	}

	@Test
	public void cmdSetViewDirection() {
		t("SetViewDirection[]");
		t("SetViewDirection[Vector[(0, 0, 1)]]");
		t("SetViewDirection[Vector[(1; α; -30°)], false]");
	}

	@Test
	public void cmdSetDynamicColor() {
		t("SetDynamicColor[ Polygon[(1,1),(2,1/2),4], 0.1, 1, 0.2 ]");
		t("SetDynamicColor[ Polygon[(1,1),(2,1/2),4], 0.1, 1, 0.2, 0.05 ]");
	}

	@Test
	public void cmdSetFilling() {
		t("SetFilling[ Polygon[(1,1),(2,1/2),4], 42 ]");
	}

	@Test
	public void cmdSetFixed() {
		t("SetFixed[ Polygon[(1,1),(2,1/2),4], false ]");
		t("SetFixed[ Polygon[(1,1),(2,1/2),4], false, true ]");
	}

	@Test
	public void cmdSetImage() {
		app.setImageManager(Mockito.mock(ImageManager.class));
		t("c:x^2+y^2=1", unicode("x^2 + y^2 = 1"));
		t("pic=ToolImage(2)");
		t("SetImage(c, pic)");
		t("SetImage(c, \"play\")");
	}

	@Test
	public void cmdSetLabelMode() {
		t("SetLabelMode[ Polygon[(1,1),(2,1/2),4], 42 ]");
	}

	@Test
	public void cmdSetLayer() {
		t("SetLayer[ Polygon[(1,1),(2,1/2),4], 42 ]");
	}

	@Test
	public void cmdSetLineStyle() {
		t("SetLineStyle[ x+y=17, 42 ]");
	}

	@Test
	public void cmdSetDecoration() {
		t("SetDecoration[ x+y=17, 4 ]");
		t("SetDecoration[ Segment[(0,1),(1,0)], 1, 2 ]");
	}

	@Test
	public void cmdSetLineThickness() {
		t("SetLineThickness[ x+y=17, 42 ]");
	}

	@Test
	public void cmdSetSeed() {
		t("SetSeed[42]");
	}

	@Test
	public void cmdSetPerspective() {
		t("SetPerspective[\"SAG/C\"]");
	}

	@Test
	public void cmdSetSpinSpeed() {
		t("SetSpinSpeed[42]");
	}

	@Test
	public void cmdSetPointSize() {
		t("SetPointSize[ (1,1), 42 ]");
	}

	@Test
	public void cmdSetPointStyle() {
		t("SetPointStyle[ (1,1), 42 ]");
	}

	@Test
	public void cmdSetTooltipMode() {
		t("SetTooltipMode[ Polygon[(1,1),(2,1/2),4], 1 ]");
	}

	@Test
	public void cmdSetTrace() {
		t("SetTrace[(1,1),true]");
		t("SetTrace[(2,1/2),false]");
	}

	@Test
	public void cmdSetValue() {
		t("SetValue[ false, 0 ]");
		t("SetValue[ {1,2,3,4,5}, 4, Polygon[(1,1),(2,1/2),4] ]");
		t("SetValue[ Polygon[(1,1),(2,1/2),4], Polygon[(1,1),(2,1/2),4] ]");
	}

	@Test
	public void cmdSetVisibleInView() {
		t("SetVisibleInView[ Polygon[(1,1),(2,1/2),4], 1, false ]");
	}

	@Test
	public void cmdSetColor() {
		t("A=(0,0,1)", "(0, 0, 1)");
		t("SetColor[ A, \"lime\" ]");
		assertEquals(GeoGebraColorConstants.LIME.toString(),
				get("A").getObjectColor().toString());
		t("SetColor[ A, \"orange\"^z(A) ]");
		assertEquals(GColor.ORANGE.toString(),
				get("A").getObjectColor().toString());
		t("SetColor[ A1, \"orange\"^z(A) ]");
		t("SetColor[ A, 1, 0, 0 ]");
		assertEquals(GColor.RED.toString(),
				get("A").getObjectColor().toString());
		t("SetColor[ A, x(A), y(A), z(A) ]");
		assertEquals(GColor.BLUE.toString(),
				get("A").getObjectColor().toString());
		assertEquals("A,A1",
				StringUtil.join(",", app.getGgbApi().getAllObjectNames()));
		assertEquals(2, app.getKernel().getConstruction().steps());
	}

	@Test
	public void cmdSetCoords() {
		t("A=(1,1)", "(1, 1)");
		t("B=(1,1,1)", "(1, 1, 1)");
		t("SetCoords[ A, x((2,1/2))+1, 3 ]");
		t("A", "(3, 3)");
		t("SetCoords[ B, 4, 5, 6 ]");
		t("B", "(4, 5, 6)");
		t("SetCoords[ B, 7, 8 ]");
		t("B", "(7, 8, 0)");

		t("A=Point(xAxis)", "(0, 0)");
		t("SetCoords(A,1/0,0)");
		t("A", "(Infinity, 0)");

		t("B=Point(x=2)", "(2, 0)");
		t("SetCoords(B,0,1/0)");
		t("B", "(2, Infinity)");
	}

	@Test
	public void cmdSetBackgroundColor() {
		t("txt=\"GeoGebra Rocks\"", "GeoGebra Rocks");
		t("A=(0,0,1)", "(0, 0, 1)");
		t("SetBackgroundColor[ \"red\" ]");
		assertEquals(
				app.getActiveEuclidianView().getBackgroundCommon().toString(),
				GColor.RED.toString());
		t("SetBackgroundColor[ 1, 1, 1 ]");
		assertEquals(
				app.getActiveEuclidianView().getBackgroundCommon().toString(),
				GColor.WHITE.toString());
		t("SetBackgroundColor[ \"orange\"^z(A) ]");
		assertEquals(
				app.getActiveEuclidianView().getBackgroundCommon().toString(),
				GColor.ORANGE.toString());
		t("SetBackgroundColor[ x(A), y(A), z(A) ]");
		assertEquals(
				app.getActiveEuclidianView().getBackgroundCommon().toString(),
				GColor.BLUE.toString());
		t("SetBackgroundColor[ txt, \"lime\" ]");
		assertEquals(GeoGebraColorConstants.LIME.toString(),
				get("txt").getBackgroundColor().toString());
		t("SetBackgroundColor[txt, 0, 1, 0 ]");
		assertEquals(GColor.GREEN.toString(),
				get("txt").getBackgroundColor().toString());
		t("SetBackgroundColor[ txt, x(A), y(A), z(A) ]");
		assertEquals(GColor.BLUE.toString(),
				get("txt").getBackgroundColor().toString());
		t("SetBackgroundColor[ A1, \"orange\"^z(A) ]");
		t("SetBackgroundColor[ A1, 0, 1, 1 ]");
		assertEquals("txt,A,A1",
				StringUtil.join(",", app.getGgbApi().getAllObjectNames()));
	}

	@Test
	public void cmdSetLevelOfDetail() {
		t("a:x+y", "x + y");
		assertEquals(((GeoFunctionNVar) get("a")).getLevelOfDetail(),
				SurfaceEvaluable.LevelOfDetail.SPEED);
		t("SetLevelOfDetail(a,0)");
		assertEquals(((GeoFunctionNVar) get("a")).getLevelOfDetail(),
				SurfaceEvaluable.LevelOfDetail.SPEED);
		t("SetLevelOfDetail(a,1)");
		assertEquals(((GeoFunctionNVar) get("a")).getLevelOfDetail(),
				SurfaceEvaluable.LevelOfDetail.QUALITY);
	}

	@Test
	public void cmdSequence() {
		t("Sequence[ 4 ]", "{1, 2, 3, 4}");
		t("Sequence[ 3.2, 7.999 ]", "{3, 4, 5, 6, 7, 8}");
		t("Sequence[ 3.2, 7.999, 1 ]", "{3.2, 4.2, 5.2, 6.2, 7.2}");
		t("Sequence[ 3.2, 7.999, -1 ]", "?");
		t("Sequence[ -3.2, 3.2 ]", "{-3, -2, -1, 0, 1, 2, 3}");
		t("Sequence[ 3.2, -2 ]", "{3, 2, 1, 0, -1, -2}");
		t("Sequence[ t^2, t, 1, 4 ]", "{1, 4, 9, 16}");
		t("Sequence[ t^2, t, 1, 4, 2 ]", "{1, 9}");
		t("Sequence[ t^2, t, 1, 4, -2 ]", "{}");
		t("Sequence[ i, i, 3, 5 ]", "{3, 4, 5}");
		t("Sequence[ i, i, 3.6, 7.9, 1 ]", "{3.6, 4.6, 5.6, 6.6, 7.6}");
		t("Sequence[ i, i, 3.2, 7.2, 1 ]", "{3.2, 4.2, 5.2, 6.2, 7.2}");
		t("Length[Unique[Sequence[ random(), t, 1, 10]]]", "10");
		t("Sequence(Angle((0,1,0),(0,0,0),(1,0,0),Vector((0,0,1))),k,1,2)",
				"{270*" + DEGREE_STRING + ", 270*" + DEGREE_STRING + "}");
	}

	@Test
	public void cmdShear() {
		t("Shear[ Polygon[(1,1),(2,1/2),4], x+y=17, 4 ]", "1.25");
	}

	@Test
	public void cmdShortestDistance() {
		t("Perimeter(ShortestDistance({1,2,3,4,5}, (3,1/3), (5,1/5), false ))",
				"NaN"); // not even segments
		t("A=(0, 0)", "(0, 0)");
		t("Perimeter(ShortestDistance({Segment(A,(0,1)), Segment(A,(2,0))}, "
				+ "(2,0), (0,1), false))", "3");
		t("Perimeter(ShortestDistance({Segment(A,(0,1)), Segment(A,(2,0))}, "
				+ "(2,0), (2,0), false))", "0"); // empty path
		t("Perimeter(ShortestDistance({Segment(A,(0,1)), Segment(A,(2,0))}, "
				+ "(2,0), (4,0), false))", "NaN"); // not connected
	}

	@Test
	public void cmdShowLabel() {
		t("ShowLabel[ Polygon[(1,1),(2,1/2),4], false ]");
	}

	@Test
	public void cmdShowLayer() {
		t("ShowLayer[ 42 ]");
	}

	@Test
	public void cmdShowAxes() {
		t("ShowAxes[]");
		assertTrue(app.getSettings().getEuclidian(1).getShowAxis(1));
		assertTrue(app.getEuclidianView1().getShowAxis(1));
		t("ShowAxes[false]");
		assertFalse(app.getSettings().getEuclidian(1).getShowAxis(1));
		assertFalse(app.getEuclidianView1().getShowAxis(1));
		t("ShowAxes[2,true]");
		assertTrue(app.getSettings().getEuclidian(2).getShowAxis(1));
		t("ShowAxes[2,false]");
		assertFalse(app.getSettings().getEuclidian(2).getShowAxis(1));
	}

	@Test
	public void cmdShowGrid() {
		t("ShowGrid[]");
		assertTrue(app.getSettings().getEuclidian(1).getShowGrid());
		assertTrue(app.getEuclidianView1().getShowGrid());
		t("ShowGrid[false]");
		assertFalse(app.getSettings().getEuclidian(1).getShowGrid());
		assertFalse(app.getEuclidianView1().getShowGrid());
		t("ShowGrid[2,true]");
		assertTrue(app.getSettings().getEuclidian(2).getShowGrid());
	}

	@Test
	public void cmdShuffle() {
		t("Shuffle[ {1,2,3,4,5} ]", "{4, 3, 1, 2, 5}");
	}

	@Test
	public void cmdSigmaXX() {
		t("SigmaXX[ {1,2,3,4,5} ]", "55");
		t("SigmaXX[ {1,2,3,4,5}, {1,2,3,4,5} ]", "225");
		t("SigmaXX[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", "55");
	}

	@Test
	public void cmdSigmaXY() {
		t("SigmaXY[ {1,2,3,4,5}, {1,2,3,4,5} ]", "55");
		t("SigmaXY[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", "16");
	}

	@Test
	public void cmdSigmaYY() {
		t("SigmaYY[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", "14.1025");
	}

	@Test
	public void cmdSlider() {
		t("Slider[ 42, 50, 13, 5, 4, false, !false, true, !false ]", "42");
	}

	@Test
	public void cmdSlope() {
		t("Slope[ x+y=17 ]", "-1");
	}

	@Test
	public void cmdSlopeField() {
		t("SlopeField[ x/y ]", "SlopeField[x / y]");
		t("SlopeField[ -y/x, 5 ]", "SlopeField[((-y)) / x, 5]");
		t("SlopeField[ -y/x, 5, 0.1 ]", "SlopeField[((-y)) / x, 5, 0.1]");
		t("SlopeField[ -y/x, 5, 0.1,0,0,1,1]", "SlopeField[((-y)) / x, 5, 0.1, 0, 0, 1, 1]");
	}

	@Test
	public void cmdSlowPlot() {
		t("SlowPlot[ sin(x) ]");
		t("SlowPlot[ sin(x), false ]");
	}

	@Test
	public void cmdSolveODE() {
		t("SolveODE[ sin(x)(x), sin(x)(x+42), x^2(x),42, 13, 50, 50, 0.05 ]",
				"SolveODE[(sin(x) * x), (sin(x) * (x + 42)), (x^(2) * x), 42, 13, 50, 50, 0.05]");
		t("SolveODE[ sin(x)(x), x^2(y), 42, 4, 13, 0.5 ]",
				"SolveODE[(sin(x) * x), (x^(2) * y), 42, 4, 13, 0.5]");
		t("SolveODE[ x*y, 42, 4, 50, 0.5 ]", "SolveODE[(x * y), 42, 4, 50, 0.5]");
		t("SolveODE[ -x]", "?");
		t("SolveODE[ -x,(1,1)]", "?");
	}

	@Test
	public void cmdSort() {
		t("Sort[ {1,2,3,4,5} ]", "{1, 2, 3, 4, 5}");
		t("Sort[ {1,2,3,4,5}, {2,3,4} ]", "?");
	}

	@Test
	public void cmdStartRecord() {
		t("StartRecord[]");
		t("StartRecord[false]");
		t("StartRecord[true]");
	}

	@Test
	public void cmdSpearman() {
		t("Spearman[ {1,2,3,4,5}, {1,2,3,4,5} ]", "1");
		t("Spearman[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", "-0.6");
	}

	@Test
	public void cmdSplit() {
		t("Split(\"kjhkjhk\", {\"p\"})", "{\"kjhkjhk\"}");
		t("Split(\"kjhkjhk\", {\"\"})", "{\"k\", \"j\", \"h\", \"k\", \"j\", \"h\", \"k\"}");
		t("Split(\"ppppp\", {\"p\"})", "{}");
		t("Split(\"\", {\"p\"})", "{}");
		t("Split(\"\", {\"\"})", "{}");
		t("Split(\"XaXaXX\", {\"X\"})", "{\"a\", \"a\"}");
		t("Split(\"aabbbcc\", {\"ab\", \"bb\"})", "{\"a\", \"cc\"}");
		t("Split(\"4(x+1)(x+2)\", {\"(\", \")\"})", "{\"4\", \"x+1\", \"x+2\"}");
		t("Split(\"4(x+1)(x+2)\", {\"(\", \")\", \"x\"})", "{\"4\", \"+1\", \"+2\"}");
		t("Split(\"4(x+1)(x+2)\", {\"(x\", \")\"})", "{\"4\", \"+1\", \"+2\"}");
		t("Split(\"kjhkjhk\", {})", "{\"kjhkjhk\"}");
		t("undefinedText=Element({\"a\"},7)", "");
		t("Split(undefinedText, {\",\"})", "?");
		t("Split(\"abcde\", {undefinedText})", "?");
	}

	@Test
	public void cmdSpline() {
		String theSpline = "(If(t < 0.38743, 0.88246t^3 + 2.44868t,"
				+ " -0.55811t^3 + 1.67434t^2 + 1.8t + 0.08377), "
				+ "If(t < 0.38743, -5.43794t^3 + 3.39737t,"
				+ " 3.43925t^3 - 10.31776t^2 + 7.39473t - 0.51623))";
		tRound("Spline[{(0,0),(1,1),(3,0)}]", unicode(theSpline));
		tRound("Spline[{(0,0),(1,1),(3,0)},3]", unicode(theSpline));
		tRound("Spline[{(0,0),(1,1),(3,0)},3,sqrt(x^2+y^2)]",
				unicode(theSpline));
		tRound("Spline[{(0,0),(1,1),(1,1),(3,0)},4]", "?");
	}

	@Test
	public void cmdStartAnimation() {
		t("StartAnimation[]");
		t("StartAnimation[ false ]");
		t("StartAnimation[ Point[x^2+y^2=1] ]");
		t("StartAnimation[ Point[x^2+y^2=1], false ]");
	}

	@Test
	public void cmdStemPlot() {
		t("StemPlot[ {1,2,3,4,5} ]",
				"{\\begin{tabular}{ll}\\begin{array}{r|ll}1&0 \\\\ 2&0 \\\\ 3&0 \\\\ 4&0"
						+ " \\\\ 5&0 \\\\ \\end{array} \\\\ \\fbox{\\text{Key: 3|1 means 3.1}} "
						+ "\\\\ \\end{tabular}}");
		t("StemPlot[ {1,2,3,4,5}, 0 ]",
				"{\\begin{tabular}{ll}\\begin{array}{r|ll}1&0 \\\\ 2&0 \\\\ 3&0 \\\\ 4&0"
						+ " \\\\ 5&0 \\\\ \\end{array} \\\\ \\fbox{\\text{Key: 3|1 means 3.1}}"
						+ " \\\\ \\end{tabular}}");
	}

	@Test
	public void cmdStickGraph() {
		t("StickGraph[{1,2,3,4,5}, {2,3,4}]", "NaN");
		t("StickGraph[{(1,1),(2,2),(3,3),(4,1/4),(5,1/5)}]", "6.45");
		t("StickGraph[{1,2,3,4,5}, {2,3,4},true]", "NaN");
		t("StickGraph[{(1,1),(2,2),(3,3),(4,1/4),(5,1/5)},true]", "6.45");
	}

	@Test
	public void cmdStepGraph() {
		t("StepGraph[{1,2,3,4,5}, {2,3,4}]", "NaN");
		t("StepGraph[{(1,1),(2,2),(3,3),(4,1/4),(5,1/5)}]", "6.45");
		t("StepGraph[{1,2,3,4,5}, {2,3,4},true]", "NaN");
		t("StepGraph[{(1,1),(2,2),(3,3),(4,1/4),(5,1/5)},true]", "6.45");
		t("StepGraph[{1,2,3,4,5}, {2,3,4},true,42]", "NaN");
		t("StepGraph[{(1,1),(2,2),(3,3),(4,1/4),(5,1/5)},true,4]", "6.45");
	}

	@Test
	public void cmdStretch() {
		t("Stretch[ Polygon[(1,1),(2,1/2),4], x+y=17, 4 ]", "5");
		t("Stretch[ Polygon[(1,1),(2,1/2),4], Vector[Point[{0,1}]] ]", "1.25");
	}

	@Test
	public void cmdSum() {
		t("listSum={1,10,1/2}", "{1, 10, 0.5}");
		t("Sum[ listSum , listSum]", "101.25");
		t("Sum[ listSum ]", "11.5");
		t("Sum[ listSum , 2 ]", "11");
		t("Sum[ listSum , 0 ]", "0");
		t("Sum[{x+y,0x+y}]", "x + y + (0 * x) + y");
		t("Sum[{x,y}]", "x + y");
		t("Sum[x+1,x+2,x+3]", "x + 1 + x + 2 + x + 3");
		t("Sum[x+1+y,x+2+y,x+3+y]", "x + 1 + y + x + 2 + y + x + 3 + y");
		t("Sum[{(1,2),(3,4)}]", "(4, 6)");
		t("Sum[{(1,2,7),(3,4),(1,1,1)}]", "(5, 7, 8)");
		t("Sum[{\"Geo\",\"Gebra\"}]", "GeoGebra");
		t("Sum[{}]", "0");
		t("Sum[{x+y,2*x}]", "x + y + (2 * x)");
		t("Sum[x^k,k,1,5]", "x^(1) + x^(2) + x^(3) + x^(4) + x^(5)");
		t("Sum[2^k,k,1,5]", "62");
		t("Sum[(k,k),k,1,5]", "(15, 15)");
		t("y=Sum[x^k,k,1,5]", "x^(1) + x^(2) + x^(3) + x^(4) + x^(5)");
	}

	@Test
	public void cmdSumSquaredErrors() {
		t("SumSquaredErrors[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)}, sin(x) ]", "11.744715099064221");
	}

	@Test
	public void cmdSurface() {
		t("Surface[u*v,u+v,u^2+v^2,u,-1,1,v,1,3]",
				"((u * v), u + v, u^(2) + v^(2))");
		t("Surface[2x,2pi]", "(u, ((2 * u) * cos(v)), ((2 * u) * sin(v)))");
		t("Surface[2x,2pi,yAxis]",
				"((u * cos(v)), (2 * u), (u * (-sin(v))))");

		t("g3=Surface[(u,v,u),u,-1,1,v,1,3]", "(u, v, u)");
		assertEquals("\\left(u,\\;v,\\;u \\right)",
				get("g3").toLaTeXString(false, StringTemplate.latexTemplate));

		t("g2=Surface[(u,v),u,-1,1,v,1,3]", "(u, v)");
		assertEquals("\\left(u,\\;v \\right)",
				get("g2").toLaTeXString(false, StringTemplate.latexTemplate));
	}

	@Test
	public void cmdSphere() {
		t("Sphere[(0,0,1),4]", indices("x^2 + y^2 + (z - 1)^2 = 16"));
		t("Sphere[(0,0,1),(0,4,1)]", indices("x^2 + y^2 + (z - 1)^2 = 16"));
	}

	@Test
	public void cmdSurdText() {
		t("SurdText((-7 * 3^(1 / 2)) / 2)", "-\\frac{7 \\; \\sqrt{3}}{2}");
		t("SurdText(-sqrt(2))", "-\\sqrt{2}");
		t("SurdText(-sqrt(4^2 + 4^2))", "-4 \\; \\sqrt{2}");
		t("SurdText[ 42 ]", "42");
		t("SurdText[ 42, {1,2,3,4,5} ]", "42");
		t("SurdText[ (1,1) ]", " \\left( 1 , 1 \\right) ");
	}

	@Test
	public void cmdSXX() {
		t("Sxx[ {1,2,3,4,5} ]", "10");
		t("Sxx[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", "10");
	}

	@Test
	public void cmdSXY() {
		t("Sxy[ {1,2,3,4,5}, {1,2,3,4,5} ]", "10");
		t("Sxy[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", "-3.3500000000000014");
	}

	@Test
	public void cmdSYY() {
		t("Syy[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]", "5.782");
	}

	@Test
	public void cmdSVD() {
		t("SVD[ {{1}} ]", "{{{1}}, {{1}}, {{1}}}");
	}

	@Test
	public void cmdTableText() {
		t("tables=TableText[1..5]", StringContains.containsString("array"));
		checkSize("tables", 5, 1);
		t("tableh=TableText[ 1..5, 1..5,\"h\" ]",
				StringContains.containsString("array"));
		checkSize("tableh", 5, 2);
		t("tablev=TableText[ {1..5, 1..5},\"v\" ]",
				StringContains.containsString("array"));
		checkSize("tablev", 2, 5);
		t("tablesplit=TableText[1..5,\"v\",3]",
				StringContains.containsString("array"));
		checkSize("tablesplit", 2, 3);
		t("tablesplit=TableText[1..5,\"h\",3]",
				StringContains.containsString("array"));
		checkSize("tablesplit", 3, 2);
		t("tables=TableText[{1,2,3}, {4,5}, \"c\", 100]",
				StringContains.containsString("array"));
		checkSize("tables", 3, 2);
		t("tables=TableText[{1,2}, {3, 4,5}, \"c\", 100, 120]",
				StringContains.containsString("array"));
		checkSize("tables", 3, 2);
		t("tables=TableText[{{1,2,3}, {4,5}}, \"c\", 100]",
				StringContains.containsString("array"));
		checkSize("tables", 3, 2);
		t("tables=TableText[{{1,2}, {3,4,5}}, \"c\", 100, 120]",
				StringContains.containsString("array"));
		checkSize("tables", 3, 2);
	}

	@Test
	public void cmdTaylorSeries() {
		t("TaylorPolynomial[ sin(x)^2, pi, 5 ]",
				"(2 * (x - pi)^(2) / 2!) - (8 * (x - pi)^(4) / 4!)"
						.replaceAll("pi", "3.141592653589793"));
	}

	@Test
	public void cmdTake() {
		t("Take[ {1,2,3,4,5} , 1, 3 ]", "{1, 2, 3}");
		t("Take[ {1,2,3,4,5} , 4 ]", "{4, 5}");
		t("Take[ \"GeoGebra\" , 1, 5 ]", "GeoGe");
		t("Take[ \"GeoGebra\" , 3 ]", "oGebra");
	}

	@Test
	public void cmdTangent() {
		t("Tangent[ x^2+y^2=1, x^2+y^2=1 ]", "?", "?", "?", "?");
		t("Tangent[ x+y=17, x^2+y^2=1 ]",
				"x + y = 1.4142135623730951", "x + y = -1.414213562373095");
		t("Tangent[ 42, sin(x) ]",
				"y = -0.39998531498835127x + 15.882861681595118");
		t("Tangent[ Point[Curve[sin(t),cos(t),t,0,3], 0.05], Curve[sin(t),cos(t),t,0,3] ]",
				"y = -0.1511352180582951x + 1.011356442673664");
		t("Tangent[ (1,1), x^2+y^2=1 ]", "y = 1", "x = 1");
		t("Tangent[ (1,1), sin(x) ]",
				"y = 0.5403023058681398x + 0.30116867893975674");
		// slightly different result on M2 Mac with xmlTemplate, use maxPrecision13 instead
		t("Tangent[ (1,1), Spline[{(2,3),(1,4),(2,5),(3,1)}]]", StringTemplate.maxPrecision13,
				"y = 22.40252712698x - 66.20758138095");
		t("Tangent[ (0, 1), Curve(cos(z), sin(z), z, 0, π)]",
				"y = 1");
	}

	@Test
	public void cmdTetrahedron() {
		String[] dodeca = new String[] { "0.11785", "(0.5, 0.28868, 0.8165)",
				"0.43301", "0.43301", "0.43301", "0.43301", "1", "1", "1", "1",
				"1", "1" };
		platonicTest("Tetrahedron", 60, dodeca);
	}

	@Test
	public void cmdTDistribution() {
		prob("TDistribution", "2",
				"((x^2 / 2 + 1)^(-((2 + 1) / 2)) gamma((2 + 1) / 2)) / (sqrt(2"
						+ Unicode.pi + ") gamma(2 / 2))",
				"0.5 + (betaRegularized(2 / 2, 0.5, 1) "
						+ "- betaRegularized(2 / 2, 0.5, 2 / (2 + x^2))) sgn(x) / 2");
	}

	@Test
	public void cmdTextfield() {
		t("InputBox[]", "");
		t("P = (2,1/2)", "(2, 0.5)");
		t("InputBox[ P ]", "(2, 1 / 2)");
	}

	@Test
	public void cmdText() {
		t("Text[ Polygon[(1,1),(2,1/2),4] ]", "1.25");
		t("Text[ Polygon[(1,1),(2,1/2),4], false ]", "Polygon((1, 1), (2, 1 / 2), 4)");
		t("Text[ Polygon[(1,1),(2,1/2),4], (1,1) ]", "1.25");
		t("Text[ Polygon[(1,1),(2,1/2),4], (1,1), false ]", "Polygon((1, 1), (2, 1 / 2), 4)");
		t("Text[ Polygon[(1,1),(2,1/2),4], (1,1), false, false ] ",
				"Polygon((1, 1), (2, 1 / 2), 4)");
		t("Text[ Polygon[(1,1),(2,1/2),4], (1,1), false, false, 1 ] ",
				"Polygon((1, 1), (2, 1 / 2), 4)");
		t("Text[ Polygon[(1,1),(2,1/2),4], (1,1), false, false, 1, 1 ] ",
				"Polygon((1, 1), (2, 1 / 2), 4)");
	}

	@Test
	public void cmdTextToUnicode() {
		t("TextToUnicode[ \"GeoGebra\" ]", "{71, 101, 111, 71, 101, 98, 114, 97}");
	}

	@Test
	public void cmdTiedRank() {
		t("TiedRank[ {1,2,3,4,5} ]", "{1, 2, 3, 4, 5}");
	}

	@Test
	public void cmdTMeanEstimate() {
		t("TMeanEstimate[ {1,2,3,4,5}, 4]", "?");
		t("TMeanEstimate[ 42, 13, 4, 0.05]", "{41.55743110260425, 42.44256889739575}");

	}

	@Test
	public void cmdTMean2Estimate() {
		t("TMean2Estimate[ {1,2,3,4,5}, {1,2,3,4,5}, 13, false ]", "?");
		t("TMean2Estimate[ 42, 4, 13, 50, 42, 4,  13, false]", "?");
	}

	@Test
	public void cmdTop() {
		t("Top[Cone[x^2+y^2=9,4]]", "X = (0, 0, 4)");
	}

	@Test
	public void cmdTriangular() {
		prob("Triangular", "1,3,2",
				"If(x < 1, 0, If(x < 2, (2 (x - 1)) / ((2 - 1) (3 - 1)),"
						+ " If(x < 3, (2 (x - 3)) / ((2 - 3) (3 - 1)), 0)))",
				"If(x < 1, 0, If(x < 2, (x - 1)^2 / ((2 - 1) (3 - 1)),"
						+ " If(x < 3, (x - 3)^2 / ((2 - 3) (3 - 1)) + 1, 1)))");
	}

	@Test
	public void cmdToolImage() {
		t("ToolImage[ 42 ]");
		t("ToolImage[ 42, (1,1) ]");
		t("ToolImage[ 42, (1,1), (2,1/2) ]");
	}

	@Test
	public void cmdToBase() {
		t("ToBase[1000000,2]", "11110100001001000000");
	}

	@Test
	public void cmdToPolar() {
		t("ToPolar[(1,2)]", "(2.23606797749979; 63.43494882292201*°)");
		t("ToPolar[4+3*i]", "(5; 36.86989764584402*°)");
	}

	@Test
	public void cmdToComplex() {
		t("ToComplex[(1,2)]", "1 + 2" + Unicode.IMAGINARY);
	}

	@Test
	public void cmdToPoint() {
		t("ToPoint[(1,1)]", "(1, 1)");
	}

	@Test
	public void cmdTranslate() {
		t("Translate[Polygon[(1,1), (2,1/2), 4], (1,1)]", "1.25");
		t("Translate[(1, 1), (3,1/3) ]", "(4, 1.3333333333333333)");
	}

	@Test
	public void cmdTranspose() {
		t("Transpose[ {{1,2},{3,4}} ]", "{{1, 3}, {2, 4}}");
	}

	@Test
	public void cmdTrapezoidalSum() {
		t("TrapezoidalSum[ sin(x), 4, 13, 42 ]", "-1.5551122654948175");
	}

	@Test
	public void cmdTravelingSalesman() {
		t("TravelingSalesman[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]",
				"TravelingSalesman[{(1, 1), (2, 2), (3, 3), (4, 1 / 4), (5, 1 / 5)}]");
	}

	@Test
	public void cmdTriangleCenter() {
		t("TriangleCenter[ (1,1),(2,1/2),(3,1/3),42 ]",
				"(2.0351425014047106, 0.5603546311318476)");
		t("TriangleCenter[ (-1.2, 2.28),(6.36, 4.77),(2.5, 0.76), 591]",
				"(3.972074828617704, 4.828273764099394)");
	}

	@Test
	public void cmdTriangleCurve() {
		t("TriangleCurve[ (1,1),(2,1/2),(3,1/3),A+B=C ]", "-3x - 5.999999999999999y = -10");
	}

	@Test
	public void cmdTrilinear() {
		t("Trilinear[(1,1),(2,1/2),(3,1/3),42,4,13]", "(1.572137531308229, 0.7878453636050818)");
	}

	@Test
	public void cmdTTest() {
		t("TTest[ {1,2,3,4,5}, 42, \">\" ]", "{0.9999996765168523, -55.154328932550705}");
		t("TTest[ 4, 13, 4, 50, \">\"]", "{0.9970990608023428, -7.076923076923077}");
	}

	@Test
	public void cmdTTestPaired() {
		t("TTestPaired[ {1, 2, 3, 4, 5}, {1, 1, 3, 5, 5}, \"<\"]", "{0.5, 0}");
	}

	@Test
	public void cmdTTest2() {
		t("TTest2[ {1,2,3,4,5}, {1,2,3,4,5}, \">\", false ]", "{0.5, 0}");
		t("TTest2[ 13, 4, 13, 50, 42, 4, \">\", false]",
				"{0.9118882772503653, -1.759451281498666}");
	}

	@Test
	public void cmdTurningPoint() {
		t("InflectionPoint[ x^3 ]", "(0, 0)");
	}

	@Test
	public void cmdType() {
		t("Type(x^2+y^2+z^2=1)", "4");
		t("Type(x^2+y^2=1)", "4");
	}

	@Test
	public void cmdPieChart() {
		// the "value" of pie chart is just the command name (no sensible way to define it)
		t("p1=PieChart({1,2,3})", "PieChart");
		t("p2=PieChart({1,2,3}, (1,1), 2)", "PieChart");
		assertThat(get("p2"), isDefined());
		t("p3=PieChart({1,2,-3})", "PieChart");
		assertThat(get("p3"), not(isDefined()));
	}

	private static void checkSize(String string, int cols, int rows) {
		AlgoTableText parentAlgorithm = (AlgoTableText) get(string).getParentAlgorithm();
		assertNotNull(parentAlgorithm);
		GDimension d = parentAlgorithm.getSize();
		if (parentAlgorithm.getAlignment() == 'h') {
			assertEquals(cols, d.getWidth());
			assertEquals(rows, d.getHeight());
		} else {
			assertEquals(rows, d.getWidth());
			assertEquals(cols, d.getHeight());
		}
	}

	@Test
	public void cmdTurtle() {
		// tested in the followings
	}

	@Test
	public void cmdTurtleForward() {
		t("TurtleForward[Turtle[], 2]");
	}

	@Test
	public void cmdTurtleBack() {
		t("TurtleBack[Turtle[], 2]");
	}

	@Test
	public void cmdTurtleLeft() {
		t("TurtleLeft[Turtle[], 3.14]");
	}

	@Test
	public void cmdTurtleRight() {
		t("TurtleRight[Turtle[], 3.14]");
	}

	@Test
	public void cmdTurtleUp() {
		t("TurtleUp[ Turtle[] ]");
	}

	@Test
	public void cmdTurtleDown() {
		t("TurtleDown[ Turtle[] ]");
	}

	@Test
	public void cmdUniform() {
		prob("Uniform", "1,2.5", "If(x < 1, 0, If(x < 2.5, (2.5 - 1)^-1, 0))",
				"If(x < 1, 0, If(x < 2.5, (x - 1) / (2.5 - 1), 1))");
	}

	@Test
	public void cmdUnitOrthogonalVector() {
		t("UnitPerpendicularVector[ 3x = 4y ]", "(0.6, -0.8)");
		t("UnitPerpendicularVector[ Segment((0,0),(3,4)) ]", "(-0.8, 0.6)");
		t("UnitPerpendicularVector[ Vector((3,4)) ]", "(-0.8, 0.6)");
		t("UnitPerpendicularVector[ (3,4) ]", "(-0.8, 0.6)");
		t("UnitPerpendicularVector[ 2x+2y+z=1 ]*3", "(2, 2, 1)");
		syntaxes -= 2;
	}

	@Test
	public void cmdUnitVector() {
		t("UnitVector[ 3x = 4y ]", "(-0.8, -0.6)");
		t("UnitVector[ Segment((0,0),(3,4)) ]", "(0.6, 0.8)");
		t("UnitVector[ Vector((3,4)) ]", "(0.6, 0.8)");
		t("UnitVector[ (3,4) ]", "(0.6, 0.8)");
		t("UnitVector[ Vector((2,2,1)) ]*3", "(2, 2, 1)");
		t("UnitVector[ (2,2,1) ]*3", "(2, 2, 1)");
		t("UnitVector[ Line((0,0,0),(2,2,1)) ]*3", "(2, 2, 1)");
	}

	@Test
	public void cmdUnion() {
		t("join=Union[Polygon[(1,1),(1,0),(0,1)],Polygon[(0,0),(1,0),(0,1)]]",
				"1", "(1, 0)", "(1, 1)", "(0, 1)", "(0, 0)", "1",
				"1", "1", "1");
		t("join=Union[Polygon[(1,1,0),(1,0,0),(0,1,0)],Polygon[(0,0,0),(1,0,0),(0,1,0)]]",
				"1", "(1, 0, 0)", "(0, 0, 0)", "(0, 1, 0)",
				"(1, 1, 0)", "1", "1", "1", "1");
		t("Union[{1,2,3}, {2,2,2,4,4,4}]", "{1, 2, 3, 4}");
		t("Union[{\"1\",\"2\",\"3\"}, {\"2\",\"2\",\"2\",\"4\",\"4\",\"4\"}]",
				"{\"1\", \"2\", \"3\", \"4\"}");
	}

	@Test
	public void cmdUpdateConstruction() {
		t("UpdateConstruction[]");
		t("UpdateConstruction[4]");
	}

	@Test
	public void cmdUnicodeToLetter() {
		t("UnicodeToLetter[ 44 ]", ",");
	}

	@Test
	public void cmdUnicodeToText() {
		t("UnicodeToText[ {41,42,43,44,45} ]", ")*+,-");
	}

	@Test
	public void cmdUnique() {
		t("Unique[ {1,2,3,4,5}]", "{1, 2, 3, 4, 5}");
	}

	@Test
	public void cmdUpperSum() {
		t("UpperSum[ sin(x), 4, 13, 42 ]", "-0.949458361978994");
	}

	@Test
	public void cmdVariance() {
		t("Variance[ {1,2,3,4,5} ]", "2");
		t("Variance[ {1,2,3,4,5}, {1,2,3,4,5} ]", "1.5555555555555571");
	}

	@Test
	public void cmdVector() {
		t("Vector[ (1,1) ]", "(1, 1)");
		t("Vector[ (3,1/3), (5,1/5) ]", "(2, -0.1333333333333333)");
	}

	@Test
	public void cmdVerticalText() {
		t("VerticalText[ \"GeoGebra\" ]",
				"\\rotatebox{90.0}{ \\text{ GeoGebra }  }");
		t("VerticalText[ \"GeoGebra\", (1,1) ]",
				"\\rotatebox{90.0}{ \\text{ GeoGebra }  }");
	}

	@Test
	public void cmdVertex() {
		t("Vertex[ x^2/9+y^2/4 =1 ]",
				"(-3, 0)", "(3, 0)", "(0, -2)", "(0, 2)");
		tRound("Unique({Vertex[ x>y && x>0 && x^2+y^2 < 2 && 4x>y^3 && 4y> x^3]})",
				"{(0, 0), (1, 1), (1.30208, 0.55189)}");
		t("Vertex[ Polygon[(0,0),(1,0),(0,1)] ]",
				"(0, 0)", "(1, 0)", "(0, 1)");
		t("Vertex[ Polygon[(0,0),(1,0),(0,1)],2 ]", "(1, 0)");
		t("Vertex[ Segment[(1,0),(0,1)], 1]", "(1, 0)");
	}

	@Test
	public void cmdVolume() {
		tRound("Volume[Cube[(0,0,1),(0,1,0)]]", eval("sqrt(8)"));
		tRound("Volume[Sphere[(0,0,1),4]]", eval("4/3*pi*4^3"));
	}

	@Test
	public void cmdVoronoi() {
		t("Voronoi[ {(1,1),(2,2),(3,3),(4,1/4),(5,1/5)} ]",
				"Voronoi[{(1, 1), (2, 2), (3, 3), (4, 1 / 4), (5, 1 / 5)}]");
	}

	@Test
	public void cmdWeibull() {
		prob("Weibull", "2,1",
				"If(x < 0, 0, 2 / 1 (x / 1)^(2 - 1) " + Unicode.EULER_STRING
						+ "^(-(x / 1)^2))",
				"If(x < 0, 0, 1 - " + Unicode.EULER_STRING + "^(-(x / 1)^2))");
	}

	@Test
	public void cmdZip() {
		t("Zip[ t^2, t, {1,2,3,4,5}]", "{1, 4, 9, 16, 25}");
		t("Zip[ i, i, {1, 2} ]", "{1, 2}");
		t("Zip[ i + j, i, {1, 2}, j, {3, 4} ]", "{4, 6}");
		t("Zip[ i + j, j, {1, 2}, i, {3, 4} ]", "{4, 6}");
		t("Zip(2*A,A,{(1,1),(2,3),(4,5,6)})", "{(2, 2, 0), (4, 6, 0), (8, 10, 12)}");
	}

	@Test
	public void testZipWithObject() {
		t("a:x=y", "y = x");
		t("RemoveUndefined(Zip(Object(xx), xx, {\"y\",\"a\",\"x\"}))", "{y = x}");
	}

	@Test
	public void testZipWithText() {
		t("Zip(Text[A,B,true,true,C,0], A, {\"t1\", \"t2\", \"t3\"}, B, {(0,1),(0,2),(0,3)} , "
				+ "C, {-1, 0, 1})", "{\"t1\", \"t2\", \"t3\"}");
	}

	@Test
	public void cmdZipf() {
		intProb("Zipf", "4,3", "3", "0.03145", "0.98673");
	}

	@Test
	public void cmdZoomIn() {
		t("ZoomIn[ ]");
		t("ZoomIn[ 42 ]");
		t("ZoomIn[ 42, (1,1) ]");
		t("ZoomIn[ -1, -1, 1, 1 ]");
		t("ZoomIn[ -1, -1, -1, 1, 1, 1 ]");
	}

	@Test
	public void cmdZoomOut() {
		t("ZoomOut[ 42 ]");
		t("ZoomOut[ 42, (1,1) ]");
	}

	@Test
	public void cmdZProportionTest() {
		t("ZProportionTest[ 42, 42, 4, \"<\" ]", "{NaN, NaN}");
	}

	@Test
	public void cmdZMeanTest() {
		t("ZMeanTest[ {1,2,3,4,5}, 42, 4, \">\" ]", "{0.5212295432641831, -0.053239713749995}");
		t("ZMeanTest[ 42, 42, 4, 13, \">\" ]", "{0.08364680132758395, 1.380952380952381}");
	}

	@Test
	public void cmdZMean2Test() {
		t("ZMean2Test[ {1,2,3,4,5}, 42, {1,2,3,4,5},13, \">\" ]", "{0.5, 0}");
		t("ZMean2Test[ 42, 42, 4, 13,42,4,\">\" ]", "{0.16441313070217656, 0.9764807930671371}");
	}

	@Test
	public void cmdZProportion2Test() {
		t("ZProportion2Test[ 13, 50, 42, 42, \">\" ]", "{NaN, NaN}");
	}

	@Test
	public void cmdZProportionEstimate() {
		t("ZproportionEstimate[42, 4, 13]", "?");
	}

	@Test
	public void cmdZProportion2Estimate() {
		t("Zproportion2Estimate[42, 4, 13, 42, 4]", "?");
	}

	@Test
	public void cmdZMeanEstimate() {
		t("ZMeanEstimate[ {1,2,3,4,5}, 42, 42 ]", "?");
		t("ZMeanEstimate[ 42, 13, 50, 42 ]", "?");
	}

	@Test
	public void cmdZMean2Estimate() {
		t("ZMean2Estimate[ {1,2,3,4,5}, {1,2,3,4,5}, 13, 50, 42 ]", "?");
		t("ZMean2Estimate[ 42, 42, 4, 13, 50, 42, 4 ]", "?");
	}

	@Test
	public void productDegree() {
		t("f(x)=x^3-Product(Sequence(x+k,k,1,-1,-1))", "x^(3) - (((x + 1) * (x)) * (x - 1))");
		t("Degree(f)", "1");
	}

	@Test
	public void cmdDirac() {
		t("Dirac(-1)", "0");
		t("Dirac(-1, 1)", "0");
		t("Dirac(0)", "Infinity");
		t("Dirac(0, 1)", "Infinity");
		t("Dirac(12)", "0");
		t("Dirac(123, 3)", "0");
	}

	@Test
	public void cmdHeaviside() {
		t("Heaviside(-10000)", "0");
		t("Heaviside(-1)", "0");
		t("Heaviside(0)", "1");
		t("Heaviside(1)", "1");
		t("Heaviside(10000)", "1");
	}
}
