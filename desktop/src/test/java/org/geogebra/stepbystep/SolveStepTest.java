package org.geogebra.stepbystep;

import java.util.List;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.kernel.stepbystep.EquationSteps;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.main.App;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class SolveStepTest {
	private static App app;

	@BeforeClass
	public static void setupApp() {
		app = CommandsTest.createApp();
		// just to load CAS
		try {
			app.getKernel().evaluateGeoGebraCAS("Regroup(1)", null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static HtmlStepBuilder htmlBuilder = new HtmlStepBuilder();
	private boolean needsHeading;
	private static int caseCounter = 0;

	@Test
	public void linearEquation() {
		t("x", "1/(sqrt(2)-1) + 1/(sqrt(2)+1)", "x", 1, "(2)(nroot(2, 2))");
		t("(4x-3)/(2 + 3)", "1-x", "x", 17, "(8)/(9)");
		t("1-2x", "2x+8", "x", 12, "(-7)/(4)");
		t("(2x-5)/12", "(-x)/4-5/3", "x", 15, "-3");
		t("x/x", "0", "x", 5);
		t("2x+1", "x-2", "x", 9, "-3");
		t("3x+4", "-x-1", "x", 12, "(-5)/(4)");
		t("3x+4", "3x+4", "x", 6, "R");
		t("3x+4", "3x+3", "x", 9);
		t("x-2", "sqrt(3)", "x", 6, "(nroot(3, 2) + 2)");
		t("x-2", "sqrt(12)-2*sqrt(3)", "x", 8, "2");
	}

	@Test
	public void rationalEquations() {
		t("1/x", "4x", "x", 17, "(1)/(2)", "-(1)/(2)");
		t("1/(1+x)-2x/(2+x)", "7", "x", 20, "((-22 + (2)(nroot(13, 2))))/(18)", "((-22-(2)(nroot(13, 2))))/(18)");
		t("1/x-1/(x+1)", "3", "x", 20, "((-3 + nroot(21, 2)))/(6)", "((-3-nroot(21, 2)))/(6)");
		t("x-1/x", "2x", "x", 9);
		t("9", "x/3-x/4", "x", 6, "108");
		t("x+1/(x-1)", "2x-1", "x", 22, "0", "2");
		t("1/(x-6)+x/(x-2)", "4/(x^2-8x+12)", "x", 31, "-1");
		t("x/(1-x)", "(3+x)/x", "x", 18, "((-2 + (2)(nroot(7, 2))))/(4)", "((-2-(2)(nroot(7, 2))))/(4)");
		t("((1)/(x)+1)^(2)", "((1)/(x+3)-2)^(2)", "x", 46, "((-9 + (3)(nroot(5, 2))))/(6)",
				"((-9-(3)(nroot(5, 2))))/(6)", "((-1 + nroot(13, 2)))/(2)", "((-1-nroot(13, 2)))/(2)");
		t("(1/x+3)^2", "6", "x", 40, "(1)/((nroot(6, 2)-3))", "(1)/((-nroot(6, 2)-3))");
	}

	@Test
	public void productEquations() {
		t("0", "(x-6)(x+1)", "x", 14, "6", "-1");
		t("0", "(x^2-3x-8)(x+5)", "x", 15, "((3 + nroot(41, 2)))/(2)", "((3-nroot(41, 2)))/(2)", "-5");
	}

	@Test
	public void quadraticEquations() {
		t("x^2+4", "4x", "x", 16, "2");
		t("x^2+3x+1", "x-1", "x", 8);
		t("x-1", "x^2+3x+1", "x", 8);
		t("x^2+4x+1", "0", "x", 23, "(nroot(3, 2)-2)", "(-nroot(3, 2)-2)");
		t("x^2-6x+9", "(x-3)^2", "x", 8, "R");
		t("(x-5)^2", "x^2", "x", 24, "(5)/(2)");
		t("3x^2+3x+3", "x^2-x-2", "x", 10);
		t("(x-2)^2-x^2", "-x^2", "x", 14, "2");
		t("(x+1)(x+2)", "(2x+3)(x+4)", "x", 28, "((8 + (2)(nroot(6, 2))))/(-2)", "((8-(2)(nroot(6, 2))))/(-2)");
		// TODO: multiply both sides by (-1) first
		t("-x^2-x+1", "0", "x", 7, "((1 + nroot(5, 2)))/(-2)", "((1-nroot(5, 2)))/(-2)");
	}

	@Test
	public void irrationalEquations() {
		t("sqrt(3x+1)", "x+1", "x", 22, "0", "1");
		t("sqrt(3x+1)", "x+1+sqrt(2x+3)", "x", 24);
		t("2x+10", "x+1+sqrt(5x-4)", "x", 16);
		t("sqrt(3x-4)", "sqrt(4x-3)", "x", 14, "-1");
		t("sqrt(x)+sqrt(x+1)+sqrt(x+2)", "2", "x", 33);
		t("sqrt(x)+1", "sqrt(x+1)+sqrt(x+2)+1", "x", 27);
		t("sqrt(x-1)", "sqrt(x)", "x", 9);
		t("sqrt(1+sqrt(1+sqrt(x)))", "10", "x", 20, "96040000");
		t("sqrt(1+ sqrt(1+ sqrt(1+ sqrt(x))))", "5", "x", 26, "109312229376");
		t("sqrt(1+sqrt(x))", "10", "x", 14, "9801");
		t("1+ sqrt(1+ sqrt(1+ sqrt(x)))", "5", "x", 23, "50176");

	}

	@Test
	public void absoluteValueEquations() {
		t("4*|2x-10|-3", "7*|x+1|+|5x-4|+2+x", "x", 79, "(38)/(-3)", "(32)/(21)");
		t("|x|-5", "0", "x", 8, "5", "-5");
		t("|x|-5", "|x-2|", "x", 36);
	}

	@Test
	public void cubicEquations() {
		t("x^3+1", "4", "x", 9, "nroot(3, 3)");
		t("x^3+3x^2+3x+2", "0", "x", 14, "-2");
		t("x^3-6x^2+12x+13", "0", "x", 14, "(-nroot(21, 3) + 2)");
		t("x^3 - 6 x^2 + 11 x - 6", "0", "x", 22, "1", "2", "3");
		// t("x^3 - x - 5 x^2 - x - 3", "0", "x", 6, "5.47");
		t("x^3 - (29 x^2)/12 + (37 x)/24 - 1/4", "0", "x", 25, "(3)/(2)", "(2)/(3)", "(1)/(4)");
		t("x^3 + 3 x^2 - 7 x + 3", "0", "x", 34, "(nroot(7, 2)-2)", "(-nroot(7, 2)-2)", "1");
		t("x^3", "sqrt(2) - 2", "x", 6, "nroot((nroot(2, 2)-2), 3)");
		t("x^3+2", "0", "x", 9, "-nroot(2, 3)");
		t("x^3", "sqrt(1/8)", "x", 10, "(nroot(2, 2))/(2)");
		t("x^3 - 6 x^2 + 11 x - 6", "0", "x", 30, "1", "2", "3");
	}

	@Test
	public void higherOrderEquations() {
		t("x^4+2x+3", "2x+19", "x", 14, "2", "-2");
		t("x^4+2x+3", "2x+2", "x", 9);
		t("2x^5+2x+3", "2x+67", "x", 15, "2");
		t("x^4+4x^2+2", "0", "x", 30);
		t("x^4+x^3+4x^2+2", "0", "x", 4);
		t("x^6+4x^3+2", "0", "x", 36, "nroot((nroot(2, 2)-2), 3)", "nroot((-nroot(2, 2)-2), 3)");
		t("((x+1)^4+1)^2", "6", "x", 33, "(nroot((nroot(6, 2)-1), 4)-1)", "(-nroot((nroot(6, 2)-1), 4)-1)");
		t("((1+x)^(2)+1)^(2)+1", "10", "x", 36, "(nroot(2, 2)-1)", "(-nroot(2, 2)-1)");
	}

	@Test
	public void trigonometricEquations() {
		t("3+2sin(x)", "sin(x)-1", "x", 10);
		t("1/2+2sin(x)", "sin(x)+1", "x", 23, "((pi)/(6) + (2)(k1)(pi))", "(((5)(pi))/(6)-(2)(k2)(pi))");
		t("1/2+2sin(3x+1)", "sin(3x+1)+1", "x", 31, "(((pi)/(6) + (2)(k1)(pi)-1))/(3)",
				"((((-5)(pi))/(6) + (2)(k2)(pi) + 1))/(-3)");
		t("(sin(2x+1))^2+1/2", "1", "x", 59, "(((pi)/(4) + (2)(k1)(pi)-1))/(2)",
				"((((-3)(pi))/(4) + (2)(k2)(pi) + 1))/(-2)", "((-(pi)/(4) + (2)(k3)(pi)-1))/(2)",
				"((((-5)(pi))/(4) + (2)(k4)(pi) + 1))/(-2)");
		t("1/2+2cos(3x+1)", "cos(3x+1)+1", "x", 31, "(((pi)/(3) + (2)(k1)(pi)-1))/(3)",
				"((((-5)(pi))/(3) + (2)(k2)(pi) + 1))/(-3)");
		t("3+2tan(x)", "tan(x)-1", "x", 10, "(arctan(-4) + (k1)(pi))");
		t("2(sin(x))^2+(cos(x))^2+cos(x)", "1", "x", 31, "(arccos(((-1 + nroot(5, 2)))/(-2)) + (2)(k1)(pi))",
				"(-arccos(((-1 + nroot(5, 2)))/(-2))-(2)(k2)(pi) + (2)(pi))");
		t("2(cos(x))^2+(sin(x))^2+sin(x)", "1", "x", 31, "(arcsin(((-1 + nroot(5, 2)))/(-2)) + (2)(k1)(pi))",
				"(-arcsin(((-1 + nroot(5, 2)))/(-2))-(2)(k2)(pi) + pi)");
		t("2(cos(x))^2+2(sin(x))^2", "2", "x", 8, "R");
		t("sin(x)+cos(x)", "1", "x", 54, "((pi)/(2) + (2)(k1)(pi))", "(2)(k3)(pi)");
	}

	@Test
	public void extremeNested() {
		t("(((x+1)^2+1)^2+1)^2", "10", "x", 48, "(nroot((nroot((nroot(10, 2)-1), 2)-1), 2)-1)",
				"(-nroot((nroot((nroot(10, 2)-1), 2)-1), 2)-1)");

		t("sqrt(1+sqrt(1+sqrt(1+x)))", "10", "x", 22, "96039999");
		t("sqrt(1+sqrt(1+sqrt(1+sqrt(1+x))))", "10", "x", 22, "9223681407920000");

		t("sqrt(x+sqrt(x+sqrt(x+1)))", "10", "x", 22);

		// TODO: problem with accuracy
		// t("x^2", "12345678987654321", "x", 22, "-111111111", "111111111");

		// TODO: problem with accuracy
		// 85076298714809070000000000000000
		// doesn't agree with CAS answer of
		// 85076298714809073438726399999999
		// t("sqrt(1+sqrt(1+sqrt(1+sqrt(1+sqrt(1+x)))))", "10", "x", 22,
		// "85076298714809073438726399999999");

	}

	public void t(String LHS, String RHS, String variable, int expectedSteps, String... expectedSolutions) {
		if (needsHeading) {
			Throwable t = new Throwable();
			htmlBuilder.addHeading(t.getStackTrace()[1].getMethodName(), 1);
			needsHeading = false;
		}
		htmlBuilder.addHeading("Testcase " + (caseCounter++), 2);

		EquationSteps es = new EquationSteps(app.getKernel(), LHS, RHS, variable);

		SolutionStep steps = es.getSteps();
		List<StepNode> solutions = es.getSolutions();
		steps.getListOfSteps(htmlBuilder);

		Assert.assertTrue(Math.abs(expectedSteps - countSteps(steps)) < 1000);
		Assert.assertEquals(expectedSolutions.length, solutions.size());

		for (int i = 0; i < expectedSolutions.length; i++) {
			Assert.assertEquals(expectedSolutions[i], solutions.get(i).toString());
		}
	}

	private int countSteps(SolutionStep s) {
		int x = 1;
		List<SolutionStep> substeps = s.getSubsteps();

		if (substeps == null) {
			return x;
		}

		for (int i = 0; i < substeps.size(); i++) {
			x += countSteps(substeps.get(i));
		}

		return x;
	}

	@Before
	public void resetHeading() {
		needsHeading = true;
	}

	@AfterClass
	public static void printHtml() {
		htmlBuilder.printReport("solve.html");
	}
}
