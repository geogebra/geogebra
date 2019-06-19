package org.geogebra.common.kernel.geos;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.common.gui.dialog.options.model.ObjectSettingsModel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoSymbolicTest {
	private static App app;
	private static AlgebraProcessor ap;

	@BeforeClass
	public static void setup() {
		app = AlgebraTest.createApp();
		app.getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		ap = app.getKernel().getAlgebraProcessor();
		app.getKernel().getGeoGebraCAS().evaluateGeoGebraCAS("1+1", null,
				StringTemplate.defaultTemplate, app.getKernel());
	}

	public static void t(String input, String... expected) {
		AlgebraTestHelper.testSyntaxSingle(input, expected, ap,
				StringTemplate.testTemplate);
	}

	public static void t(String input, Matcher<String> expected) {
		AlgebraTestHelper.testSyntaxSingle(input, Arrays.asList(expected), ap,
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
				app.getKernel().lookupLabel(label).getDefinitionForEditor());
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
		GeoElement geo1 = app.getKernel().lookupLabel(string);
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
	}

	@Test
	public void testSolutionsCommand() {
		t("Solutions(x*a^2=4*a, a)", "{4 / x, 0}");
		t("Solutions(x^2=4x)", "{0, 4}");
		t("Solutions({x=4x+y,y+x=2},{x, y})", "{{-1, 3}}");
		t("Solutions(sin(x)=cos(x))",
				"{(-3) / 4 * " + Unicode.pi + ", 1 / 4 * " + Unicode.pi + "}");
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
		t("Integral(exp(-x^2),-inf,inf)", "sqrt(" + Unicode.pi + ")");
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
		GeoElement a = app.getKernel().lookupLabel("a");
		ap.changeGeoElement(a, "p-q", true, false, TestErrorHandler.INSTANCE,
				null);
		checkInput("a", "a = p - q");
	}

	@Test
	public void constantShouldBeOneRow() {
		t("1", "1");
		GeoElement a = app.getKernel().lookupLabel("a");
		assertTrue(a instanceof GeoSymbolic);
		assertEquals(DescriptionMode.VALUE, a.needToShowBothRowsInAV());
	}

	@Test
	public void labeledConstantShouldBeOneRow() {
		t("a=7", "7");
		GeoElement a = app.getKernel().lookupLabel("a");
		assertTrue(a instanceof GeoSymbolic);
		assertEquals(DescriptionMode.VALUE, a.needToShowBothRowsInAV());
	}

	@Test
	public void simpleEquationShouldBeOneRow() {
		t("eq1:x+y=1", "x + y = 1");
		GeoElement a = app.getKernel().lookupLabel("eq1");
		assertTrue(a instanceof GeoSymbolic);
		assertEquals(DescriptionMode.VALUE, a.needToShowBothRowsInAV());
	}

	@Test
	public void simpleFracShouldBeOneRow() {
		t("1/2", "1 / 2");
		GeoElement a = app.getKernel().lookupLabel("a");
		assertTrue(a instanceof GeoSymbolic);
		assertEquals(DescriptionMode.VALUE, a.needToShowBothRowsInAV());
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
	public void testDerivativeShorthand() {
		t("f(x)=exp(x)", Unicode.EULER_STRING + "^(x)");
		t("f'(x)=f'(x)", Unicode.EULER_STRING + "^(x)");
		checkInput("f'", "f'(x) = f'(x)");
	}

	private GeoSymbolic getSymbolic(String label) {
		return (GeoSymbolic) app.getKernel().lookupLabel(label);
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
		GeoElement a = app.getKernel().lookupLabel("a");
		assertTrue(a instanceof GeoSymbolic);
		assertEquals(DescriptionMode.VALUE, a.needToShowBothRowsInAV());
	}

	private static void shouldFail(String string, String errorMsg) {
		AlgebraTestHelper.shouldFail(string, errorMsg, app);
	}

	private static String getObjectLHS(String label) {
		GeoElement geo = app.getKernel().lookupLabel(label);
		try {
			return geo
					.getAssignmentLHS(StringTemplate.defaultTemplate);
		} catch (Exception e) {
			return "";
		}
	}

}
