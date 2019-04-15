package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.main.App;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
		AlgebraTest.testSyntaxSingle(input, expected, ap,
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
		Assert.assertEquals(expectedInput, app.getKernel().lookupLabel(label)
				.toString(StringTemplate.defaultTemplate));
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
		Assert.assertEquals("a \\, = \\,2 \\; \\sqrt{2}", text);
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
		Assert.assertEquals("f\\left(x, y \\right) \\, = \\,x + y",
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
	}

	@Test
	public void sequence() {
		t("2*Sequence(Mod(n,3),n,1,5)", "{2, 4, 0, 2, 4}");
		t("Sequence(Mod(n,3),n,1,5)", "{1, 2, 0, 1, 2}");
	}

	@Test
	public void testSolveCommand() {
		t("Solve(x*a^2=4*a, a)", "{a = 4 / x, a = 0}");

		t("f(x)=x^3-k*x^2+4*k*x", "-k * x^(2) + x^(3) + 4 * k * x");
		t("Solve(f(x) = 0)", "{x = (k - sqrt(k^(2) - 16 * k)) / 2, x =" +
				" (k + sqrt(k^(2) - 16 * k)) / 2, x = 0}");

		t("Solve(k(k-16)>0,k)", "{k < 0, k > 16}");
		t("Solve(x^2=4x)", "{x = 0, x = 4}");
		t("Solve({x=4x+y,y+x=2},{x, y})", "{{x = -1, y = 3}}");
		t("Solve(sin(x)=cos(x))", "{x = k_1 * \u03c0 + 1 / 4 * \u03c0}");
	}

	@Test
	public void testReplacingAssignments() {
		t("eq1:x+y=3", "x + y = 3");
		t("eq2:x-y=1", "x - y = 1");
		t("Solve({eq1, eq2})", "{{x = 2, y = 1}}");
	}

	@Test
	public void testCalculations() {
		t("eq1: 9=a*3^3+b*3^2+c*3+d", "9 = 27 * a + 9 * b + 3 * c + d");
		t("eq2: 4=a*2^3+b*2^2+c*2+d", "4 = 8 * a + 4 * b + 2 * c + d");
		t("eq3: 7=a*4^3+b*4^2+c*4+d", "7 = 64 * a + 16 * b + 4 * c + d");
		t("eq4: 1=a*1^3+b*1^2+c*1+d", "1 = a + b + c + d");
		t("Solve({eq1,eq2,eq3,eq4}, {a,b,c,d})", "{{a = (-3) / 2, b = 10, c = (-33) / 2, d = 9}}");
	}

	@Test
	public void testCurveSketching() {
		t("f(x)=x^3-2x^2+1", "x^(3) - 2 * x^(2) + 1");
		t("Derivative(f)", "3 * x^(2) - 4 * x");
		t("f''(x)", "6 * x - 4");
		t("Derivative(f, x, 3)", "6");
		t("Solve(f(x) = 0)", "{x = (-sqrt(5) + 1) / 2, x = 1, x = (sqrt(5) + 1) / 2}");
		t("Solve(f'(x) = 0)", "{x = 0, x = 4 / 3}");
		t("Solve(f''(x) = 0)", "{x = 2 / 3}");
	}
}
