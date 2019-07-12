package org.geogebra.common.kernel.commands;

import static com.himamis.retex.editor.share.util.Unicode.DEGREE_STRING;
import static com.himamis.retex.editor.share.util.Unicode.PI_STRING;
import static com.himamis.retex.editor.share.util.Unicode.theta_STRING;
import static org.geogebra.test.TestStringUtil.unicode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.jre.headless.ScreenReaderAccumulator;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoIntersectConics;
import org.geogebra.common.kernel.algos.AlgoIntersectPolyLines;
import org.geogebra.common.kernel.algos.AlgoTableText;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable.LevelOfDetail;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.geogebra.test.commands.CommandSignatures;
import org.hamcrest.Matcher;
import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;;


@SuppressWarnings("javadoc")
public class CommandsTest {
	static AppCommon3D app;
	static AlgebraProcessor ap;
	static List<Integer> signature;

	public static void tRound(String s, String... expected) {
		testSyntax(s, AlgebraTestHelper.getMatchers(expected), app, ap,
				StringTemplate.editTemplate);
	}

	public static void t(String s, String... expected) {
		testSyntax(s, AlgebraTestHelper.getMatchers(expected), app, ap,
				StringTemplate.xmlTemplate);
	}

	public static void t(String s, Matcher<String> expected) {
		testSyntax(s, Arrays.asList(expected), app, ap,
				StringTemplate.xmlTemplate);
	}

	public static void testSyntax(String s, List<Matcher<String>> expected,
			App app1,
			AlgebraProcessor proc, StringTemplate tpl) {
		if (syntaxes == -1000) {
			Throwable t = new Throwable();
			String cmdName = t.getStackTrace()[2].getMethodName().substring(3);
			try {
				Commands.valueOf(cmdName);
			} catch (Exception e) {
				cmdName = t.getStackTrace()[3].getMethodName().substring(3);
			}

			signature = CommandSignatures.getSigneture(cmdName);
			if (signature != null) {
				syntaxes = signature.size();
				AlgebraTestHelper.dummySyntaxesShouldFail(cmdName, signature,
						app1);
			}
			System.out.println();
			System.out.print(cmdName);
		}
		syntaxes--;
		AlgebraTestHelper.testSyntaxSingle(s, expected, proc, tpl);
	}

	private static int syntaxes = -1000;

	@Before
	public void resetSyntaxes() {
		resetSyntaxCounter();
		app.getKernel().clearConstruction(true);
		app.setActiveView(App.VIEW_EUCLIDIAN);
	}

	public static void resetSyntaxCounter() {
		syntaxes = -1000;
	}

	@After
	public void checkSyntaxes() {
		checkSyntaxesStatic();
	}

	public static void checkSyntaxesStatic() {
		Assert.assertTrue("unchecked syntaxes: " + syntaxes + signature,
				syntaxes <= 0);
	}

	@BeforeClass
	public static void setupApp() {
		app = new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
		ap = app.getKernel().getAlgebraProcessor();
	}

	@Test
	public void testQuadricExpr() {
		t("-y^2=z-1", "-y" + Unicode.SUPERSCRIPT_2 + " + 0z"
				+ Unicode.SUPERSCRIPT_2 + " - z = -1");
		t("y^2=1-z", "y" + Unicode.SUPERSCRIPT_2 + " + 0z"
				+ Unicode.SUPERSCRIPT_2 + " + z = 1");
	}

	private static GeoElement get(String label) {
		return app.getKernel().lookupLabel(label);
	}

	@Test
	public void listPropertiesTest() {
		t("mat1={{1,2,3}}", "{{1, 2, 3}}");
		Assert.assertTrue(((GeoList) get("mat1")).isEditableMatrix());
		t("slider1=7", "7");
		t("mat2={{1,2,slider1}}", "{{1, 2, 7}}");
		Assert.assertTrue(((GeoList) get("mat2")).isEditableMatrix());
		t("mat2={{1,2,slider1},Reverse[{1,2,3}]}", "{{1, 2, 7}, {3, 2, 1}}");
		Assert.assertFalse(((GeoList) get("mat2")).isEditableMatrix());
	}

	@Test
	public void operationSequence() {
		Assert.assertEquals(StringUtil.fixVerticalBars("1..2"),
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
	public void cmdMidpoint() {
		t("Midpoint[(0,0),(2,2)]", "(1, 1)");
		t("Midpoint[0<x<2]", "1");
		t("Midpoint[Segment[(0,0),(2,2)]]", "(1, 1)");
		t("Midpoint[(x-1)^2+(y-1)^2=pi]", "(1, 1)");
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
	public void cmdCross() {
		t("Cross[(0,0,1),(1,0,0)]", "(0, 1, 0)");
		t("Cross[(0,0,1),(0,1,0)]", "(-1, 0, 0)");
		t("Cross[(0,0,1),(0,0,1)]", "(0, 0, 0)");
		t("Cross[(0,1),(2,0)]", "-2");
		t("Cross[(0,1),(0,2)]", "0");
	}

	@Test
	public void functionDependentPoly() {
		t("s(x,y)=x+y", "x + y");
		t("s(1,2)*x=1", "x = 0.3333333333333333");
	}

	@Test
	public void cmdDot() {
		t("Dot[(0,0,1),(1,0,0)]", "0");
		t("Dot[(0,0,1),(0,0,1)]", "1");
		t("Dot[(0,3),(0,2)]", "6");
	}

	@Test
	public void cmdNormalize() {
		t("Normalize[{1,3,2}]", "{0, 1, 0.5}");
		t("Normalize[{(1,1),(3,1),(2,1)}]", "{(0, 0), (1, 0), (0.5, 0)}");
	}

	@Test
	public void cmdDataFunction() {
		t("DataFunction[]", "DataFunction[{}, {},x]");
		tRound("DataFunction[]", new String[] { "DataFunction[x]" });
	}

	@Test
	public void cmdInteriorAngles() {
		t("InteriorAngles[Polygon((0,0),(2,0),(2,1),(1,1),(1,2),(0,2))]",
				new String[] { deg("90"), deg("90"), deg("90"), deg("270"),
						deg("90"), deg("90") });
	}

	private static String deg(String string) {
		return string + "*" + DEGREE_STRING;
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
			tRound(cmd + "(h(x),1,3)", eval("-log(2) + log(3) + 7 / 3"));
			tRound(cmd + "(h2(x),1,3)", eval("-log(2) + log(3) + 7 / 3"));
			tRound(cmd + "(h3(x),1,3)", eval("-log(2) + log(3) + 7 / 3"));
			tRound(cmd + "(h4(x),1,3)", eval("-log(2) + log(3) + 7 / 3"));
			tRound(cmd + "(h5(x),1,3)", "7.02648");
		}
		t("Integral(If(x^2>1,1,x>7,0,0),-1,1)", "0");
		t("Integral(If(x^2>1,1,x>7,0,0),-2,2)", "2");
		t("Integral(If(x>2,1,2),0,2.01)", "4.01");
		t("Integral(If(x^4>1,1,0),-2,2)", "2");
		tRound("Integral(If(x>2,3,x>1,2,1),0.99,3.01)", "5.04");
	}

	@Test
	public void cmdAreCongruent() {
		t("AreCongruent[Segment[(0,1),(1,0)],Segment[(1,0),(0,1)]]", "true");
		t("AreCongruent[Segment[(0,1),(1,0)],Segment[(-1,0),(0,-1)]]", "true");
		t("AreCongruent[Segment[(0,1),(1,0)],Segment[(2,0),(0,2)]]", "false");
	}

	@Test
	public void cmdIntersectPoly() {
		t("ZoomIn(-5,-5,5,5)", new String[0]);
		intersect("x^2+x", "x^2+x", false, "(?, ?)");
		intersect("x^2+x", "x^2+x+1", false, "(?, ?)");
	}

	@Test
	public void cmdIntersect() {
		t("ZoomIn(-5,-5,5,5)", new String[0]);
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
		intersect("x^2+y^2=25", "x y=12", true, "(3, 4)", "(-3, -4)",
				"(-4, -3)", "(4, 3)");
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

		if (app.has(Feature.IMPLICIT_SURFACES)) {
			intersect("x^4+y^4+z^4=2", "x=y", false, "(-1, -1, 0)",
					"(1, 1, 0)");
		}
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
				&& !(geo.getParentAlgorithm() instanceof AlgoIntersectConics)
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
	}

	@Test
	public void cmdMaximize() {
		t("slider:=Slider[0,5]", "0");
		t("Maximize[ 5-(3-slider)^2, slider ]", "3");
		tRound("ptPath:=Point[(x-3)^2+(y-4)^2=25]", "(0, 0)");
		tRound("Maximize[ y(ptPath), ptPath ]", "(3, 9)");
	}

	@Test
	public void cmdMinimize() {
		t("slider:=Slider[0,5]", "0");
		t("Minimize[ 5+(3-slider)^2, slider ]", "3");
		tRound("ptPath:=Point[(x-3)^2+(y-4)^2=25]", "(0, 0)");
		tRound("Minimize[ y(ptPath), ptPath ]", "(3, -1)");
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
	public void cmdImplicitSurface() {
		if (app.has(Feature.IMPLICIT_SURFACES)) {
			t("ImplicitSurface[sin(x)+sin(y)+sin(z)]",
					"sin(x) + sin(y) + sin(z) = 0");
		}
	}

	@Test
	public void cmdSetConstructionStep() {
		app.setSaved();
		assertTrue(app.clearConstruction());
		t("cs=ConstructionStep[]", "1");
		t("2", "2");
		t("7", "7");
		t("SetConstructionStep[2]", new String[] {});
		t("cs", "2");
		t("SetConstructionStep[1]", new String[] {});
		t("cs", "1");
		assertTrue(app.clearConstruction());
	}

	@Test
	public void cmdSVD() {
		t("SVD[ {{1}} ]", "{{{1}}, {{1}}, {{1}}}");
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
		t("Length[Unique[Sequence[ random(), t, 1, 10]]]", "10");
	}

	@Test
	public void cmdOrthogonalPlane() {
		t("OrthogonalPlane[ (0,0,1), X=(p,2p,3p) ]", "x + 2y + 3z = 3");
		t("OrthogonalPlane[ (0,0,1), Vector[(1,2,3)] ]", "x + 2y + 3z = 3");
	}

	@Test
	public void cmdDifference() {
		tRound("Difference[Polygon[(0,0),(2,0),4],Polygon[(1,1),(3,1),(3,3),(1,3)]]",
				new String[] { "3", "(2, 1)", "(1, 1)", "(1, 2)", "(0, 2)",
						"(0, 0)", "(2, 0)", "1", "1", "1", "2", "2", "1" });
		tRound("Difference[Polygon[(0,0),(2,0),4],Polygon[(1,1),(3,1),(3,3),(1,3)], true]",
				new String[] { "3", "3", "(3, 3)", "(1, 3)", "(1, 2)", "(2, 2)",
						"(2, 1)", "(3, 1)", "(2, 1)", "(1, 1)", "(1, 2)",
						"(0, 2)", "(0, 0)", "(2, 0)", "2", "1", "1", "1", "1",
						"2", "1", "1", "1", "2", "2", "1" });
	}

	@Test
	public void parametricSyntaxes() {
		t("X=(s,2s)", "X = (0, 0) + s (1, 2)");
		t("Intersect[X=(s,s),x+y=2]", "(1, 1)");
	}

	private static void ti(String in, String out) {
		testSyntax(in.replace("i", Unicode.IMAGINARY + ""),
				AlgebraTestHelper.getMatchers(new String[] {
						out.replace("i", Unicode.IMAGINARY + "") }),
				app,
				ap, StringTemplate.xmlTemplate);
	}

	@Test
	public void complexArithmetic() {
		ti("(0i)^2", "0 + 0i");
		ti("(0i)^0", "NaN - NaNi");
		ti("(0i)^-1", "NaN - NaNi");
		ti("(2+0i)^0", "1 + 0i");
		ti("(1/0+0i)^0", "NaN - NaNi");
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
		t("pix", "(" + PI_STRING + " * x)");
		t("sinx", "sin(x)");
		t("sin x", "sin(x)");
		t("f(" + theta_STRING + ")=sin " + theta_STRING, "sin(" + theta_STRING + ")");
		t("f(" + theta_STRING + ")=sin" + theta_STRING, "sin(" + theta_STRING + ")");
		t("f(t)=sin t", "sin(t)");
		t("f(t)=sint", "sin(t)");
		t("x" + PI_STRING, "(" + PI_STRING + " * x)");
		t("sinxdeg", "sin((1*" + DEGREE_STRING + " * x))");
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
	public void cmdExtremum() {
		tRound("Extremum[ sin(x), 1, 7 ]",
				new String[] { "(1.5708, 1)", "(4.71239, -1)" });
		tRound("Extremum[ x^3-3x ]", new String[] { "(-1, 2)", "(1, -2)" });
		// TODO t("Extremum((x^2-4)/(x-2),-9,9)", "(NaN, NaN)");
	}

	@Test
	public void cmdSurface() {
		t("Surface[u*v,u+v,u^2+v^2,u,-1,1,v,1,3]",
				"((u * v), u + v, u^(2) + v^(2))");
		t("Surface[2x,2pi]", "(u, ((2 * u) * cos(v)), ((2 * u) * sin(v)))");
		t("Surface[2x,2pi,yAxis]",
				"((u * cos(v)), (2 * u), (u * (-sin(v))))");

		t("g3=Surface[(u,v,u),u,-1,1,v,1,3]", "(u, v, u)");
		Assert.assertEquals("\\left(u, v, u \\right)",
				get("g3").toLaTeXString(false, StringTemplate.latexTemplate));

		t("g2=Surface[(u,v),u,-1,1,v,1,3]", "(u, v)");
		Assert.assertEquals("\\left(u, v \\right)",
				get("g2").toLaTeXString(false, StringTemplate.latexTemplate));
	}

	@Test
	public void cmdCube() {
		t("Cube[(0,0,0),(0,0,2)]",
				new String[] { "8", "(2, 0, 0)", "(0, 2, 0)", "(0, 2, 2)",
						"(2, 2, 2)", "(2, 2, 0)", "4", "4", "4", "4", "4", "4",
						"2", "2", "2", "2", "2", "2", "2", "2", "2", "2", "2",
						"2" });
		t("Cube[(0,0,0),(0,2,0),(0,2,2)]",
				new String[] { "8", "(0, 0, 2)", "(2, 0, 0)", "(2, 2, 0)",
						"(2, 2, 2)", "(2, 0, 2)", "4", "4", "4", "4", "4", "4",
						"2", "2", "2", "2", "2", "2", "2", "2", "2", "2", "2",
						"2" });
		t("Cube[(0,0,0),(0,0,2),xAxis]",
				new String[] { "8", "(0, -2, 2)", "(0, -2, 0)", "(2, 0, 0)",
						"(2, 0, 2)", "(2, -2, 2)", "(2, -2, 0)", "4", "4", "4",
						"4", "4", "4", "2", "2", "2", "2", "2", "2", "2", "2",
						"2", "2", "2", "2" });
	}

	@Test
	public void cmdVolume() {
		tRound("Volume[Cube[(0,0,1),(0,1,0)]]", eval("sqrt(8)"));
		tRound("Volume[Sphere[(0,0,1),4]]", eval("4/3*pi*4^3"));
	}

	@Test
	public void cmdSphere() {
		t("Sphere[(0,0,1),4]", indices("x^2 + y^2 + (z - 1)^2 = 16"));
		t("Sphere[(0,0,1),(0,4,1)]", indices("x^2 + y^2 + (z - 1)^2 = 16"));
	}

	@Test
	public void cmdCone() {
		tRound("Cone[x^2+y^2=9,4]",
				new String[] { eval("12*pi"), "X = (0, 0, 4)",
						eval("pi*15"), });
		tRound("Cone[(0,0,0),(0,0,4),3]", new String[] { eval("12*pi"),
				"X = (0, 0, 0) + (3 cos(t), -3 sin(t), 0)", eval("pi*15"), });
		tRound("Cone[(0,0,0),Vector[(0,0,4)],pi/4]",
				new String[] { indices("x^2 + y^2 - 1z^2 = 0") });
	}

	@Test
	public void cmdCylinder() {
		tRound("Cylinder[x^2+y^2=9,4]", new String[] { eval("36*pi"),
				"X = (0, 0, 4) + (3 cos(t), 3 sin(t), 0)", eval("pi*24"), });
		tRound("Cylinder[(0,0,0),(0,0,4),3]", new String[] { eval("36*pi"),
				"X = (0, 0, 0) + (3 cos(t), -3 sin(t), 0)",
				"X = (0, 0, 4) + (3 cos(t), 3 sin(t), 0)", eval("pi*24") });
		tRound("Cylinder[(0,0,0),Vector[(0,0,4)],1]",
				new String[] { indices("x^2 + y^2 + 0z^2 = 1") });
	}

	@Test
	public void cmdPlaneBisector() {
		t("PlaneBisector[(1,1),(1,1,2)]", "z = 1");
		t("PlaneBisector[Segment[(1,1),(1,1,2)]]", "z = 1");
	}

	@Test
	public void cmdInfiniteCylinder() {
		tRound("InfiniteCylinder[(1,1),(1,1,2),1]",
				indices("x^2 + y^2 + 0z^2 - 2x - 2y = -1"));
		tRound("InfiniteCylinder[(1,1),Vector[(0,0,2)],1]",
				indices("x^2 + y^2 + 0z^2 - 2x - 2y = -1"));
		tRound("InfiniteCylinder[xAxis,1]", indices("y^2 + z^2 = 1"));
	}

	@Test
	public void cmdInfiniteCone() {
		tRound("InfiniteCone[(1,1),(1,1,2),45deg]",
				indices("x^2 + y^2 - 1z^2 - 2x - 2y = -2"));
		tRound("InfiniteCone[(1,1),Vector[(0,0,2)],45deg]",
				indices("x^2 + y^2 - 1z^2 - 2x - 2y = -2"));
		tRound("InfiniteCone[(1,1),xAxis,45deg]",
				indices("-1x^2 + y^2 + z^2 + 2x - 2y = 0"));
	}

	@Test
	public void cmdHeight() {
		t("Height[Cone[x^2+y^2=9,4]]", "4");
		t("Height[Cube[(0,0,1),(0,0,0)]]", "1");
	}

	@Test
	public void cmdEnds() {
		t("Ends[Cone[x^2+y^2=9,4]]", new String[] {
				"X = (0, 0, 0) + (3 cos(t), -3 sin(t), 0)", "X = (0, 0, 4)" });
	}

	@Test
	public void cmdBottom() {
		t("Bottom[Cone[x^2+y^2=9,4]]",
				"X = (0, 0, 0) + (3 cos(t), -3 sin(t), 0)");
	}

	@Test
	public void cmdTop() {
		t("Top[Cone[x^2+y^2=9,4]]", "X = (0, 0, 4)");
	}

	@Test
	public void cmdQuadricSide() {
		tRound("Side[Cone[x^2+y^2=9,4]]", eval("15pi"));
	}

	@Test
	public void cmdPerpendicularPlane() {
		t("PerpendicularPlane[(3,2,7),Line[(1,1,1),(1,1,3)]]", "z = 7");
		t("PerpendicularPlane[(3,2,7),Vector[(1,1,0)]]", "x + y = 5");
	}

	@Test
	public void cmdNet() {
		tRound("Net[Cube[(0,0,2),(0,0,0)],1]",
				new String[] { "24", "(0, 0, 2)", "(0, 0, 0)", "(2, 0, 0)",
						"(2, 0, 2)", "(0, 0, 4)", "(2, 0, 4)", "(2, 0, 6)",
						"(0, 0, 6)", "(-2, 0, 2)", "(-2, 0, 0)", "(0, 0, -2)",
						"(2, 0, -2)", "(4, 0, 0)", "(4, 0, 2)", "4", "4", "4",
						"4", "4", "4", "2", "2", "2", "2", "2", "2", "2", "2",
						"2", "2", "2", "2", "2", "2", "2", "2", "2", "2",
						"2" });
		t("Net[Tetrahedron[(0,0,1),(0,1,0),(1,0,0)],Segment[(0,0,1),(0,1,0)]]",
				new String[] { "NaN", "(NaN, NaN, NaN)", "(NaN, NaN, NaN)",
						"(NaN, NaN, NaN)", "(NaN, NaN, NaN)", "(NaN, NaN, NaN)",
						"(NaN, NaN, NaN)", "NaN", "NaN", "NaN", "NaN", "NaN",
						"NaN", "NaN", "NaN", "NaN", "NaN", "NaN", "NaN",
						"NaN" });
	}

	@Test
	public void cmdIntersectPath() {
		// 3D
		t("IntersectPath[x+y+z=1,x+y-z=1]",
				"X = (1, 0, 0) + " + Unicode.lambda + " (-2, 2, 0)");
		tRound("IntersectPath[x^2+y^2+z^2=4,x+y-z=1]",
				"X = (0.33333, 0.33333, -0.33333) + (-1.35401 cos(t) - 0.78174 sin(t), 1.35401 cos(t) - 0.78174 sin(t), -1.56347 sin(t))");
		tRound("IntersectPath[Polygon[(0,0,0),(2,0,0),(2, 2,0),(0,2,0)],Polygon[(1,1),(3,1),4]]",
				new String[] { "1", "(2, 2, 0)", "(1, 2, 0)", "(1, 1, 0)",
						"(2, 1, 0)", "1", "1", "1", "1" });
		tRound("IntersectPath[Polygon[(0,0),(2,0),4],x+y=3]", eval("sqrt(2)"));
		// 2D
		tRound("IntersectPath[Polygon[(0,0,0),(2,0,0),(2, 2,0),(0,2,0)],x+y=3]",
				eval("sqrt(2)"));
		tRound("IntersectPath[Polygon[(0,0),(2,0),4],Polygon[(1,1),(3,1),4]]",
				new String[] { "1", "(2, 2)", "(1, 2)", "(1, 1)", "(2, 1)", "1",
						"1", "1", "1" });
		tRound("IntersectPath[Polygon[(0,0),(4,0),4],(x-2)^2+(y-2)^2=5]",
				new String[] { "2", "2", "2", "2" });
		tRound("IntersectPath[Segment[(0,0),(4,4)],(x-2)^2+(y-2)^2=2]",
				eval("sqrt(8)"));
		tRound("IntersectPath[Segment[(0,0),(2,2)],(x-2)^2+(y-2)^2=2]",
				eval("sqrt(2)"));
		tRound("IntersectPath[Segment[(1.5,1.5),(2,2)],(x-2)^2+(y-2)^2=2]",
				eval("sqrt(.5)"));
		tRound("IntersectPath[Cube[(0,0),(sqrt(2),0),(sqrt(2),sqrt(2))],x+y+z=sqrt(2)]",
				new String[] { "1.73205", "(1.41421, 0, 0)", "(0, 1.41421, 0)",
						"(0, 0, 1.41421)", "2", "2", "2" });
	}

	private static String indices(String string) {
		return string.replace("^2", Unicode.SUPERSCRIPT_2 + "");
	}

	private static String eval(String string) {
		return ap.evaluateToGeoElement(string, true)
				.toValueString(StringTemplate.editTemplate);
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

	private static void platonicTest(String string, int deg, String[] dodeca) {
		tRound(string + "[(1;" + deg + "deg),(0,0)]", dodeca);
		tRound(string + "[(1;" + deg + "deg),(0,0),(1,0)]", dodeca);
		String[] dodeca1 = new String[dodeca.length + 1];
		dodeca1[0] = dodeca[0];
		dodeca1[1] = "(1, 0, 0)";
		for (int i = 2; i < dodeca1.length; i++) {
			dodeca1[i] = dodeca[i - 1];
		}
		tRound(string + "[(1;" + deg + "deg),(0,0),Vector[(0,0,1)]]", dodeca1);
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
	public void cmdOctahedron() {
		String[] dodeca = new String[] { "0.4714", "(0, 0.57735, 0.8165)",
				"(0.5, -0.28868, 0.8165)", "(1, 0.57735, 0.8165)", "0.43301",
				"0.43301", "0.43301", "0.43301", "0.43301", "0.43301",
				"0.43301", "0.43301", "1", "1", "1", "1", "1", "1", "1", "1",
				"1", "1", "1", "1" };
		platonicTest("Octahedron", 60, dodeca);
	}

	@Test
	public void cmdPyramid() {

		tRound("Pyramid[(0,0,0),(1,0,0),(0,1,0),(0,0,1)]",
				new String[] { eval("1/6"), "0.5", "0.5", eval("sqrt(3)/2"),
						"0.5", "1", eval("sqrt(2)"), "1", "1", eval("sqrt(2)"),
						eval("sqrt(2)"), });
		tRound("Pyramid[Polygon[(0,0,0),(1,0,0),(0,1,0)],(0,0,1)]",
				new String[] { eval("1/6"), "0.5", eval("sqrt(3)/2"), "0.5",
						"1", eval("sqrt(2)"), eval("sqrt(2)"), });
		tRound("Pyramid[Polygon[(-3,0,0),(0,-3,0),(3,0,0),(0,3,0)],4]",
				new String[] { "24", "(0, 0, 4)", "9.60469", "9.60469",
						"9.60469", "9.60469", "5", "5", "5", "5" });
	}

	@Test
	public void cmdPrism() {
		tRound("Prism[(0,0,0),(1,0,0),(0,1,0),(0,0,1)]",
				new String[] { "0.5", "(1, 0, 1)", "(0, 1, 1)", "0.5", "1",
						eval("sqrt(2)"), "1", "0.5", "1", eval("sqrt(2)"), "1",
						"1", "1", "1", "1", eval("sqrt(2)"), "1" });
		tRound("Prism[Polygon[(0,0,0),(1,0,0),(0,1,0)],(0,0,1)]",
				new String[] { "0.5", "(1, 0, 1)", "(0, 1, 1)", "1",
						eval("sqrt(2)"), "1", "0.5", "1", "1", "1", "1",
						eval("sqrt(2)"), "1" });
		tRound("Prism[Polygon[(-3,0,0),(0,-3,0),(3,0,0),(0,3,0)],4]",
				new String[] { "72", "(-3, 0, 4)", "(0, -3, 4)", "(3, 0, 4)",
						"(0, 3, 4)", eval("12sqrt(2)"), eval("12sqrt(2)"),
						eval("12sqrt(2)"), eval("12sqrt(2)"), "18", "4", "4",
						"4", "4", eval("3sqrt(2)"), eval("3sqrt(2)"),
						eval("3sqrt(2)"), eval("3sqrt(2)") });
	}

	@Test
	public void cmdTetrahedron() {
		String[] dodeca = new String[] { "0.11785", "(0.5, 0.28868, 0.8165)",
				"0.43301", "0.43301", "0.43301", "0.43301", "1", "1", "1", "1",
				"1", "1" };
		platonicTest("Tetrahedron", 60, dodeca);
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
	public void cmdSpline() {
		String theSpline = "(If(t < 0.38743, 0.88246t^3 + 2.44868t, -0.55811t^3 + 1.67434t^2 + 1.8t + 0.08377), If(t < 0.38743, -5.43794t^3 + 3.39737t, 3.43925t^3 - 10.31776t^2 + 7.39473t - 0.51623))";
		tRound("Spline[{(0,0),(1,1),(3,0)}]", unicode(theSpline));
		tRound("Spline[{(0,0),(1,1),(3,0)},3]", unicode(theSpline));
		tRound("Spline[{(0,0),(1,1),(3,0)},3,sqrt(x^2+y^2)]",
				unicode(theSpline));
		tRound("Spline[{(0,0),(1,1),(1,1),(3,0)},4]", "?");
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
	public void cmdUnion() {
		t("join=Union[Polygon[(1,1),(1,0),(0,1)],Polygon[(0,0),(1,0),(0,1)]]",
				new String[] { "1", "(1, 0)", "(1, 1)", "(0, 1)", "(0, 0)", "1",
						"1", "1", "1" });
		t("join=Union[Polygon[(1,1,0),(1,0,0),(0,1,0)],Polygon[(0,0,0),(1,0,0),(0,1,0)]]",
				new String[] { "1", "(1, 0, 0)", "(0, 0, 0)", "(0, 1, 0)",
						"(1, 1, 0)", "1", "1", "1", "1" });
		t("Union[{1,2,3}, {2,2,2,4,4,4}]", "{1, 2, 3, 4}");
		t("Union[{\"1\",\"2\",\"3\"}, {\"2\",\"2\",\"2\",\"4\",\"4\",\"4\"}]",
				"{\"1\", \"2\", \"3\", \"4\"}");
	}

	@Test
	public void cmdInvert() {
		t("Invert[ {{1,1},{0,2}} ]", "{{1, -0.5}, {0, 0.5}}");
		t("Invert[ sin(x) ]", "asin(x)");
		t("Invert[If[x>1,x^3+1] ]", "If[cbrt(x - 1) > 1, cbrt(x - 1)]");
		t("Invert[ If(x>2,x)+1 ]", "If[x - 1 > 2, x - 1]");
		t("Invert[ If(x>2,sin(x)-x) ]", "?");
		AlgebraTestHelper.enableCAS(app, false);
		app.getKernel().getAlgebraProcessor().reinitCommands();
		t("Invert[ sin(x) ]", "NInvert[sin(x)]");
		AlgebraTestHelper.enableCAS(app, true);
		app.getKernel().getAlgebraProcessor().reinitCommands();
	}

	@Test
	public void cmdNInvert() {
		t("ni(x)=NInvert[ sin(x) ]", "NInvert[sin(x)]");
		t("ni(sin(1))", "1");
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
	public void cmdFit() {
		tRound("Fit[ {(0,1),(1,2),(2,5)}, {x^2,x,1} ]",
				unicode("1x^2 + 0x + 1 * 1"));
		tRound("Fit[ {(0,1,1),(1,1,2),(2,1,5),(0,2,4),(1,2,5),(2,2,8)}, {x^2,x,1,x^2*y,x*y,y} ]",
				unicode("3y + 0x y + 0x^2 y - 2 + 0x + 1x^2"));
		t("a=Slider[0,10]", "0");
		t("b=Slider[0,10]", "0");
		t("c=Slider[0,10]", "0");
		tRound("Fit[ {(0,1),(1,2),(2,5)},a*x^2+b*x+c ]",
				unicode("1x^2 + 0x + 1"));
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
		t("picT=ToolImage[2]", new String[0]);
		t("Reflect[picT,e]", "picT'");
		tRound("Reflect[xAxis,e]",
				unicode("X = (0, 0, 0) + " + Unicode.lambda + " (0, 0, 1)"));
		tRound("Reflect[yAxis,e]",
				unicode("X = (0, 0, 0) + " + Unicode.lambda + " (0, 1, 0)"));
		tRound("Reflect[zAxis,e]",
				unicode("X = (0, 0, 0) + " + Unicode.lambda + " (1, 0, 0)"));
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
		tRound("Roots[sin(x),-1,4]", new String[] { "(0, 0)", "(3.14159, 0)" });
		tRound("A", "(0, 0)");
		tRound("B", "(3.14159, 0)");
		tRound("A = Roots[sin(x),-1,4.2]",
				new String[] { "(0, 0)", "(3.14159, 0)" });
		tRound("A", "(0, 0)");
		tRound("Object[\"B\"]", "(3.14159, 0)");
	}

	@Test
	public void cmdRoot() {
		tRound("Root[ x^3-x ]", new String[] { "(-1, 0)", "(0, 0)", "(1, 0)" });
		tRound("Root[ sin(x*pi), 1.3 ]", "(1, 0)");
		tRound("Root[ sin(x*pi), -3,3 ]", "(0, 0)");
		t("a:=4/5", "0.8");
		t("Root(a)", "(NaN, NaN)");
		t("b:=0/5", "0");
		t("Root(b)", "(NaN, NaN)");
	}

	@Test
	public void cmdParametricDerivative() {
		t("ParametricDerivative[Curve[t^2,sin(t),t,-7,7]]",
				"(t^(2), cos(t) / ((2 * t)))");
	}

	@Test
	public void cmdNDerivative() {
		tRound("NDerivative[x^2]", unicode("NDerivative(x^2)"));
	}

	@Test
	public void cmdRename() {
		t("Rename[ 6*7, \"a\" ]", new String[0]);
		Assert.assertEquals(
				get("a").toValueString(StringTemplate.defaultTemplate), "42");
		t("Rename[ a, \"b\" ]", new String[0]);
		Assert.assertEquals(
				get("b").toValueString(StringTemplate.defaultTemplate), "42");
		Assert.assertNull(get("a"));
		t("Rename[ b, \"  cc  d  \" ]", new String[0]);
		Assert.assertEquals(
				get("cc").toValueString(StringTemplate.defaultTemplate), "42");
		Assert.assertNull(get("b"));
		AlgebraTestHelper.shouldFail("Rename[ cc, \"\" ]", "Illegal", app);
		Assert.assertNotNull(get("cc"));
		AlgebraTestHelper.shouldFail("Rename[ cc, \"42\" ]", "Illegal", app);
		Assert.assertNotNull(get("cc"));
		AlgebraTestHelper.shouldFail("Rename[ cc, \"A_{}\" ]", "Illegal", app);
		Assert.assertNotNull(get("cc"));
		AlgebraTestHelper.shouldFail("Rename[ cc, \"A_{\" ]", "Illegal", app);
		Assert.assertNotNull(get("cc"));
		t("Rename[ cc, \"A_\" ]", new String[0]);
		Assert.assertNull(get("cc"));
		Assert.assertEquals(
				get("A").toValueString(StringTemplate.defaultTemplate), "42");
	}

	@Test
	public void cmdVertex() {
		t("Vertex[ x^2/9+y^2/4 =1 ]",
				new String[] { "(-3, 0)", "(3, 0)", "(0, -2)", "(0, 2)" });
		tRound("Unique({Vertex[ x>y && x>0 && x^2+y^2 < 2 && 4x>y^3 && 4y> x^3]})",
				"{(0, 0), (-1, -1), (1, 1), (-2, -2), (2, 2), (0, -1.41421), (0, 1.41421), (-0.55189, -1.30208), (0.55189, 1.30208), (-1.30208, -0.55189), (1.30208, 0.55189)}");
		t("Vertex[ Polygon[(0,0),(1,0),(0,1)] ]",
				new String[] { "(0, 0)", "(1, 0)", "(0, 1)" });
		t("Vertex[ Polygon[(0,0),(1,0),(0,1)],2 ]", "(1, 0)");
		t("Vertex[ Segment[(1,0),(0,1)], 1]", "(1, 0)");
	}

	@Test
	public void cmdSetCoords() {
		t("Pt2=(1,1)", "(1, 1)");
		t("Pt3=(1,1,1)", "(1, 1, 1)");
		t("SetCoords[ Pt2, x(Pt2)+1, 3 ]", new String[0]);
		t("Pt2", "(2, 3)");
		t("SetCoords[ Pt3, 4, 5, 6 ]", new String[0]);
		t("Pt3", "(4, 5, 6)");
		t("SetCoords[ Pt3, 7, 8 ]", new String[0]);
		t("Pt3", "(7, 8, 0)");

		t("A=Point(xAxis)", "(0, 0)");
		t("SetCoords(A,1/0,0)", new String[0]);
		t("A", "(Infinity, 0)");

		t("B=Point(x=2)", "(2, 0)");
		t("SetCoords(B,0,1/0)", new String[0]);
		t("B", "(2, Infinity)");
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

	@Test
	public void cmdWeibull() {
		prob("Weibull", "2,1",
				"If(x < 0, 0, 2 (x)^(2 - 1) " + Unicode.EULER_STRING
						+ "^(-(x)^2))",
				"If(x < 0, 0, 1 - " + Unicode.EULER_STRING + "^(-(x)^2))");
	}

	@Test
	public void cmdTDistribution() {
		prob("TDistribution", "2",
				"((x^2 / 2 + 1)^(-((2 + 1) / 2)) gamma((2 + 1) / 2)) / (sqrt(2"
						+ Unicode.pi + ") gamma(2 / 2))",
				"0.5 + (betaRegularized(2 / 2, 0.5, 1) - betaRegularized(2 / 2, 0.5, 2 / (2 + x^2))) sgn(x) / 2");
	}

	@Test
	public void cmdUniform() {
		prob("Uniform", "1,2.5", "If(x < 1, 0, If(x < 2.5, (2.5 - 1)^-1, 0))",
				"If(x < 1, 0, If(x < 2.5, (x - 1) / (2.5 - 1), 1))");
	}

	@Test
	public void cmdChiSquared() {
		prob("ChiSquared", "2", "If(x < 0, 0, (" + Unicode.EULER_STRING
				+ "^((-x) / 2) x^(2 / 2 - 1)) / (2^(2 / 2) gamma(2 / 2)))",
				"If(x < 0, 0, gamma(2 / 2, x / 2) / gamma(2 / 2))");
	}

	@Test
	public void cmdErlang() {
		prob("Erlang", "2,1",
				"If(x < 0, 0, (" + Unicode.EULER_STRING
						+ "^(-(x 1)) x^(2 - 1) 1^2) / (2 - 1)!)",
				"If(x < 0, 0, gamma(2, 1x) / (2 - 1)!)");
	}

	@Test
	public void cmdNormal() {
		prob("Normal", "2,1",
				Unicode.EULER_STRING
						+ "^((-(x - 2)^2) / (1^2 2)) / (abs(1) sqrt("
						+ Unicode.pi + " 2))",
				"(erf((x - 2) / (abs(1) sqrt(2))) + 1) / 2");
	}

	@Test
	public void cmdGamma() {
		prob("Gamma", "2,1",
				"If(x < 0, 0, (x^(2 - 1) " + Unicode.EULER_STRING
						+ "^(-(x))) / (1^2 gamma(2)))",
				"If(x < 0, 0, gamma(2, x) / gamma(2))");
	}

	@Test
	public void cmdTriangular() {
		prob("Triangular", "1,3,2",
				"If(x < 1, 0, If(x < 2, (2 (x - 1)) / ((2 - 1) (3 - 1)), If(x < 3, (2 (x - 3)) / ((2 - 3) (3 - 1)), 0)))",
				"If(x < 1, 0, If(x < 2, (x - 1)^2 / ((2 - 1) (3 - 1)), If(x < 3, (x - 3)^2 / ((2 - 3) (3 - 1)) + 1, 1)))");
	}

	@Test
	public void cmdFDistribution() {
		prob("FDistribution", "2,1",
				"If(x < 0, 0, (1^(1 / 2) (2x)^(2 / 2)) / (beta(2 / 2, 1 / 2) x (2x + 1)^(2 / 2 + 1 / 2)))",
				"If(x < 0, 0, betaRegularized(2 / 2, 1 / 2, (2x) / (2x + 1)))",
				0);
	}

	@Test
	public void cmdExponential() {
		prob("Exponential", "2",
				"If(x < 0, 0, 2" + Unicode.EULER_STRING + "^(-(2x)))",
				"If(x < 0, 0, 1 - " + Unicode.EULER_STRING + "^(-(2x)))");
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
						+ "^(-((ln(x) - 2)^2 / (1^2 2))) / (abs(1) sqrt(2"
						+ Unicode.pi + ") x))",
				"If(x " + Unicode.LESS_EQUAL
						+ " 0, 0, erf((ln(x) - 2) / (sqrt(2) abs(1))) 0.5 + 0.5)");
	}

	@Test
	public void cmdCauchy() {
		prob("Cauchy", "2,1", "abs(1) / ((1^2 + (x - 2)^2) " + Unicode.pi + ")",
				"tan" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING
						+ "((x - 2) / abs(1)) / " + Unicode.pi + " + 0.5");
		// "atan((x - 2) / abs(1)) / " + Unicode.pi + " + 0.5");
	}

	@Test
	public void cmdZipf() {
		intProb("Zipf", "4,3", "3", "0.03145", "0.98673");
	}

	@Test
	public void cmdBernoulli() {
		t("Bernoulli[ 0.7, false ]", "1");
		t("Bernoulli[ 0.7, true ]", "Infinity");
	}

	private static void intProb(String cmd, String args, String val, String pf,
			String cdf) {
		t("ZoomIn[0,0,100,100]", new String[0]);
		tRound(cmd + "(" + args + "," + val + ",false)", pf);
		tRound(cmd + "(" + args + "," + val + ",true)", cdf);

		tRound(cmd + "(" + args + ")", "1");
		tRound(cmd + "(" + args + ",false)", "1");

		tRound(cmd + "(" + args + ",true)>1", "true");
	}

	@Test
	public void cmdPoisson() {
		intProb("Poisson", "2", "1", "0.27067", "0.40601");
	}

	@Test
	public void cmdPascal() {
		intProb("Pascal", "3,0.5", "4", "0.11719", "0.77344");
	}

	@Test
	public void cmdHyperGeometric() {
		intProb("HyperGeometric", "10,3,5", "2", "0.41667", "0.91667");
	}

	@Test
	public void cmdBinomialDist() {
		intProb("BinomialDist", "11, 0.5", "5", "0.22559", "0.5");
	}

	@Test
	public void yLHSFunctions() {
		t("f:y=sin(x)", "sin(x)");
		t("SetValue(f,x^2)", new String[0]);
		Assert.assertEquals(get("f").getGeoClassType(), GeoClass.FUNCTION);
		app.setXML(app.getXML(), true);
		Assert.assertEquals(get("f").getGeoClassType(), GeoClass.FUNCTION);
	}

	@Test
	public void plusMinus() {
		tpm("1pm2", "{3, -1}");
		tpm("1pm2pm4", "{7, -5}");
		tpm("pm2", "{2, -2}");
		tpm("pmx", "{x, (-x)}");
		tpm("x+(pm2)", "{x + 2, x - 2}");
		tpm("xpm2", "{x + 2, x - 2}");
		tpm("xpm(pm2)", "{x + 2, x + 2}");
		t("mul=4", "4");
		tpm("prod=pm mul 3", "{12, -12}");
		Assert.assertEquals("(" + Unicode.PLUSMINUS + "mul) 3",
				get("prod").getDefinition(StringTemplate.editorTemplate));
		tpm("prod2=pm sqrt 4", "{2, -2}");
		Assert.assertEquals(Unicode.PLUSMINUS + "sqrt(4)",
				get("prod2").getDefinition(StringTemplate.editorTemplate));
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
		t("a=(1+1/143)^143", "2.708837868759473");
		((GeoNumeric) get("a")).setSymbolicMode(true, true);
		t("a", "2.708837868759473");

		t("b=(1+1/400)^400", "2.7148917443812293");
		((GeoNumeric) get("b")).setSymbolicMode(true, true);
		t("b", "2.7148917443812293");
	}

	@Test
	public void numIntTest() {
		t("F(t,x)=NIntegral(sin(x)+sin(t-x), x)",
				"NIntegral[sin(x) + sin(t - x), x]");
		((FunctionalNVar) get("F")).setSecret(null);
		Assert.assertEquals("-cos(x) - (-cos(t - x))",
				get("F").toValueString(StringTemplate.testTemplate));
	}

	@Test
	public void cmdSetColor() {
		t("A=(0,0,1)", "(0, 0, 1)");
		t("SetColor[ A, \"lime\" ]", new String[0]);
		Assert.assertEquals(GeoGebraColorConstants.LIME.toString(),
				get("A").getObjectColor().toString());
		t("SetColor[ A, \"orange\"^z(A) ]", new String[0]);
		Assert.assertEquals(GColor.ORANGE.toString(),
				get("A").getObjectColor().toString());
		t("SetColor[ A1, \"orange\"^z(A) ]", new String[0]);
		t("SetColor[ A, 1, 0, 0 ]", new String[0]);
		Assert.assertEquals(GColor.RED.toString(),
				get("A").getObjectColor().toString());
		t("SetColor[ A, x(A), y(A), z(A) ]", new String[0]);
		Assert.assertEquals(GColor.BLUE.toString(),
				get("A").getObjectColor().toString());
		Assert.assertEquals("A,A1",
				StringUtil.join(",", app.getGgbApi().getAllObjectNames()));
	}

	@Test
	public void cmdSetBackgroundColor() {
		t("txt=\"GeoGebra Rocks\"", "GeoGebra Rocks");
		t("A=(0,0,1)", "(0, 0, 1)");
		t("SetBackgroundColor[ \"red\" ]", new String[0]);
		Assert.assertEquals(
				app.getActiveEuclidianView().getBackgroundCommon().toString(),
				GColor.RED.toString());
		t("SetBackgroundColor[ 1, 1, 1 ]", new String[0]);
		Assert.assertEquals(
				app.getActiveEuclidianView().getBackgroundCommon().toString(),
				GColor.WHITE.toString());
		t("SetBackgroundColor[ \"orange\"^z(A) ]", new String[0]);
		Assert.assertEquals(
				app.getActiveEuclidianView().getBackgroundCommon().toString(),
				GColor.ORANGE.toString());
		t("SetBackgroundColor[ x(A), y(A), z(A) ]", new String[0]);
		Assert.assertEquals(
				app.getActiveEuclidianView().getBackgroundCommon().toString(),
				GColor.BLUE.toString());
		t("SetBackgroundColor[ txt, \"lime\" ]", new String[0]);
		Assert.assertEquals(GeoGebraColorConstants.LIME.toString(),
				get("txt").getBackgroundColor().toString());
		t("SetBackgroundColor[ txt, 0, 1, 0 ]", new String[0]);
		Assert.assertEquals(GColor.GREEN.toString(),
				get("txt").getBackgroundColor().toString());
		t("SetBackgroundColor[ txt, x(A), y(A), z(A) ]", new String[0]);
		Assert.assertEquals(GColor.BLUE.toString(),
				get("txt").getBackgroundColor().toString());
		t("SetBackgroundColor[ A1, \"orange\"^z(A) ]", new String[0]);
		t("SetBackgroundColor[ A1, 0, 1, 1 ]", new String[0]);
		Assert.assertEquals("txt,A,A1",
				StringUtil.join(",", app.getGgbApi().getAllObjectNames()));
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
				new String[] { "NSolveODE[{y1', y2'}, 0, {a, b}, 20]",
						"NSolveODE[{y1', y2'}, 0, {a, b}, 20]" });

		tRound("x1 = l sin(y(Point(nint_1, 0)))", "-1.91785");
		tRound("y1 = -l cos(y(Point(nint_1, 0)))", "-0.56732");
		tRound("Segment((0, 0), (x1, y1))", "2");
		// undefined testcase
		t("yu1'(t, y1, y2) = ?", "NaN");
		t("yu2'(t, y1, y2) = ?", "NaN");
		t("NSolveODE({yu1', yu2'}, 0, {a, b}, 20)",
				new String[] { "NSolveODE[{yu1', yu2'}, 0, {a, b}, 20]",
						"NSolveODE[{yu1', yu2'}, 0, {a, b}, 20]" });
	}

	@Test
	public void cmdLength() {
		t("Length[ Curve(3t,4t,t,0,10), 2, 3 ]", "5");
		tRound("Length[ Curve(3t,4t,t,0,10), (3,5),(6,9) ]", "5");
		t("Length[ 3/4x, 0, 4 ]", "5");
		t("round(Length[ x^2, 1, 4 ],5)", "15.33969");
		t("round(Length[ sqrt(x), 1, 4 ],5)", "3.16784");
		tRound("Length[ 3/4x, (0,1), (4,4) ]", "5");
		t("Length[ Segment((1,0),(0,0))]", "1");
		t("Length[ Segment((3,4,12),(0,0))]", "13");
		t("Length[ CircleArc((0,0),(1/pi,0),(0,1))]", "0.5");

		t("Length[Vector((3,4))]", "5");

		t("Length[ (3,4) ]", "5");

		t("Length[ 1..10 ]", "10");
		// t("Length[ loc ]");
		t("freehandFunc=Function[{1,2,3,1,5,7,9}]", "freehand(x)");
		t("Length[ freehandFunc ]", "5");

		t("Length[ \"GeoGebra\" ]", "8");
	}

	@Test
	public void cmdTaylorSeries() {
		t("TaylorPolynomial[ sin(x)^2, pi, 5 ]",
				"(2 * (x - pi)^(2) / 2!) - (8 * (x - pi)^(4) / 4!)"
						.replaceAll("pi", "3.141592653589793"));
	}

	@Test
	public void cmdHoles() {
		if (app.has(Feature.COMMAND_HOLES)) {
			t("Holes(x/x)", "{(0, 1)}");
			t("Holes((x^2 - 4)/(x - 2))", "{(2, 4)}");
			t("Holes(x + x/x)", "{(0, 1)}");
			t("Holes(2^(x + x/x))", "{(0, 2)}");
		}
	}

	@Test
	public void cmdSetLevelOfDetail() {
		t("a:x+y", "x + y");
		assertEquals(((GeoFunctionNVar) get("a")).getLevelOfDetail(),
				LevelOfDetail.SPEED);
		t("SetLevelOfDetail(a,0)", new String[0]);
		assertEquals(((GeoFunctionNVar) get("a")).getLevelOfDetail(),
				LevelOfDetail.SPEED);
		t("SetLevelOfDetail(a,1)", new String[0]);
		assertEquals(((GeoFunctionNVar) get("a")).getLevelOfDetail(),
				LevelOfDetail.QUALITY);
	}

	@Test
	public void cmdContingencyTable() {
		String column = "\\text{\\text{Column \\%}}&\\;&\\;&\\;\\\\";
		String pct = "\\;&100&50&66.67\\\\";
		String pctM = "\\;&0&50&33.33\\\\";
		String table = "\\begin{array}{|l|r|r||r|}\\hline \\text{\\text{Frequency}}&\\text{L}&\\text{R}&\\text{Total}\\\\"
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
	public void cmdDotPlot() {
		t("ZoomIn(0,0,16,12)", new String[0]);
		t("DotPlot[ {1,1,1,2} ]", "{(1, 1), (1, 2), (1, 3), (2, 1)}");
		t("DotPlot[ {1,1,1,2},2 ]", "{(1, 2), (1, 4), (1, 6), (2, 2)}");
		t("DotPlot[ {1,1,1,2}, true]",
				"{(1, 0.1), (1, 0.3), (1, 0.5), (2, 0.1)}");
		t("DotPlot[ {1,1,1,2}, true, 5 ]",
				"{(1, 0.1), (1, 1.1), (1, 2.1), (2, 0.1)}");
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
	public void cmdDirection() {
		t("Direction[ x = y ]", "(-1, -1)");
		t("Direction[ Line((0,0,0),(2,2,1)) ]", "(2, 2, 1)");
		t("Direction[ Vector((2,2,1)) ]", "(2, 2, 1)");
		t("Direction[ Vector((3,4)) ]", "(3, 4)");
		t("Direction[ 2x+2y+z=1 ]*3", "(-2, -2, -1)");
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
	public void cmdOrthogonalVector() {
		t("PerpendicularVector[ 3x = 4y ]", "(3, -4)");
		t("PerpendicularVector[ Segment((0,0),(3,4)) ]", "(-4, 3)");
		t("PerpendicularVector[Vector((3,4))  ]", "(-4, 3)");
		t("PerpendicularVector[(3,4)  ]", "(-4, 3)");
		t("PerpendicularVector[ 2x+2y+z=1 ]", "(2, 2, 1)");
		syntaxes -= 2;
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
	public void testPointsFromList() {
		t("Sequence(Segment(Point({0, n}), Point({1, n+0})), n, 0, 9, 1)",
				"{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}");
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
	public void cmdBinomial() {
		t("BinomialCoefficient[ 5, -1 ]", "0");
		t("BinomialCoefficient[ 5, 1 ]", "5");
	}

	@Test
	public void cmdnCr() {
		t("nCr[ 5, -1 ]", "0");
		t("nCr[ 5, 1 ]", "5");
	}

	@Test
	public void xCurve() {
		t("f:(cos(t),sin(t+1))", "(cos(t), sin(t + 1))");
		t("x(f)", "x(f(t))");
	}

	@Test
	public void cmdComplexRoot() {
		tRound("Sort({ComplexRoot(x^6 + 7x^3 - 8)})",
				complex("{-2 + 0i, -0.5 - 0.86603i, -0.5 + 0.86603i, 1 - 1.73205i, 1 + 0i, 1 + 1.73205i}"));
		t("ComplexRoot( x^2 )", complex("0 + 0i"));
	}

	private static String complex(String string) {
		return string.replaceAll("i", Unicode.IMAGINARY + "");
	}

	@Test
	public void cmdReadText() {
		t("SetActiveView(1)");
		t("ReadText(\"Can anybody hear me?\")");
		assertTrue(((ScreenReaderAccumulator) app.getActiveEuclidianView()
				.getScreenReader()).hasRead("Can anybody hear me?"));
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
	}

	private static void checkSize(String string, int cols, int rows) {
		GDimension d = ((AlgoTableText) get(string).getParentAlgorithm())
				.getSize();
		if (((AlgoTableText) get(string).getParentAlgorithm())
				.getAlignment() == 'h') {
			assertEquals(cols, d.getWidth());
			assertEquals(rows, d.getHeight());
		} else {
			assertEquals(rows, d.getWidth());
			assertEquals(cols, d.getHeight());
		}
	}

}
