package org.geogebra.stepbystep;

import java.util.Arrays;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.stepbystep.CASConflictException;
import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquation;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
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
	public void multipleVariables() {
		t("x^2 + y^2", "1", "x", "nroot((1-(y)^(2)), 2)", "-nroot((1-(y)^(2)), 2)");
		t("x^2 + y^2", "1", "y", "nroot((1-(x)^(2)), 2)", "-nroot((1-(x)^(2)), 2)");
		t("3x + 2y", "1", "x", "((1-(2)(y)))/(3)");
		t("3x + 2y", "1", "y", "((1-(3)(x)))/(2)");
	}

	@Test
	public void linearEquation() {
		t("x", "1/(sqrt(2)-1) + 1/(sqrt(2)+1)", "x", "(2)(nroot(2, 2))");
		t("(4x-3)/(2 + 3)", "1-x", "x", "(8)/(9)");
		t("1-2x", "2x+8", "x", "-(7)/(4)");
		t("(2x-5)/12", "(-x)/4-5/3", "x", "-3");
		t("x/x", "0", "x");
		t("2x+1", "x-2", "x", "-3");
		t("3x+4", "-x-1", "x", "-(5)/(4)");
		t("3x+4", "3x+4", "x", "R");
		t("3x+4", "3x+3", "x");
		t("x-2", "sqrt(3)", "x", "(nroot(3, 2) + 2)");
		t("x-2", "sqrt(12)-2*sqrt(3)", "x", "2");
	}

	@Test
	public void rationalEquations() {
		t("1/x", "2/3", "x", "(3)/(2)");
		t("1/x", "2/(3x-1)", "x", "1");
		t("1/x", "4x", "x", "(1)/(2)", "-(1)/(2)");
		t("1/(1+x)-2x/(2+x)", "7", "x", "((-11 + nroot(13, 2)))/(9)", "-((11 + nroot(13, 2)))/(9)");
		t("1/x-1/(x+1)", "3", "x", "((-3 + nroot(21, 2)))/(6)", "-((3 + nroot(21, 2)))/(6)");
		t("x-1/x", "2x", "x");
		t("9", "x/3-x/4", "x", "108");
		t("x+1/(x-1)", "2x-1", "x", "0", "2");
		t("1/(x-6)+x/(x-2)", "4/(x^2-8x+12)", "x", "-1");
		t("x/(1-x)", "(3+x)/x", "x", "((-1 + nroot(7, 2)))/(2)", "-((1 + nroot(7, 2)))/(2)");
		t("((1)/(x)+1)^(2)", "((1)/(x+3)-2)^(2)", "x", "((-3 + nroot(5, 2)))/(2)",
				"-((3 + nroot(5, 2)))/(2)", "((-1 + nroot(13, 2)))/(2)", "-((1 + nroot(13, 2)))/(2)");
		t("(1/x+3)^2", "6", "x", "-((nroot(6, 2) + 3))/(3)", "((nroot(6, 2)-3))/(3)");
	}

	@Test
	public void productEquations() {
		t("0", "(x-6)(x+1)", "x", "6", "-1");
		t("0", "(x^2-3x-8)(x+5)", "x", "((3 + nroot(41, 2)))/(2)", "((3-nroot(41, 2)))/(2)", "-5");
	}

	@Test
	public void quadraticEquations() {
		t("((1-2y)/3)^2 + y^2", "1", "y", "((2 + (6)(nroot(3, 2))))/(13)", "((2-(6)(nroot(3, 2))))/(13)");
		t("x^2+4", "4x", "x", "2");
		t("x^2+3x+1", "x-1", "x");
		t("x-1", "x^2+3x+1", "x");
		t("x^2+4x+1", "0", "x", "(nroot(3, 2)-2)", "(-nroot(3, 2)-2)");
		t("x^2-6x+9", "(x-3)^2", "x", "R");
		t("(x-5)^2", "x^2", "x", "(5)/(2)");
		t("3x^2+3x+3", "x^2-x-2", "x");
		t("(x-2)^2-x^2", "-x^2", "x", "2");
		t("(x+1)(x+2)", "(2x+3)(x+4)", "x", "(nroot(6, 2)-4)", "(-nroot(6, 2)-4)");
		t("-x^2-x+1", "0", "x", "-((1 + nroot(5, 2)))/(2)", "((-1 + nroot(5, 2)))/(2)");
		// TODO: Conditional solutions
		// t("-2*a*x^2-2*a*x+2*a", "0", "x", "((-1-nroot(5, 2)))/(2) if a != 0", "((-1 + nroot(5, 2)))/(2) if a !=
		// 0", "R, if a == 0");
	}

	@Test
	public void irrationalEquations() {
		t("sqrt(3x+1)", "x+1", "x", "0", "1");
		t("sqrt(3x+1)", "x+1+sqrt(2x+3)", "x", "fail");
		t("2x+10", "x+1+sqrt(5x-4)", "x");
		t("sqrt(3x-4)", "sqrt(4x-3)", "x", "-1");
		t("sqrt(x)+sqrt(x+1)+sqrt(x+2)", "2", "x", "fail");
		t("sqrt(x)+1", "sqrt(x+1)+sqrt(x+2)+1", "x");
		t("sqrt(x-1)", "sqrt(x)", "x");
		t("sqrt(1+sqrt(1+sqrt(x)))", "10", "x", "96040000");
		t("sqrt(1+ sqrt(1+ sqrt(1+ sqrt(x))))", "5", "x", "109312229376");
		t("sqrt(1+sqrt(x))", "10", "x", "9801");
		t("1+ sqrt(1+ sqrt(1+ sqrt(x)))", "5", "x", "50176");

	}

	@Test
	public void absoluteValueEquations() {
		t("4*|2x-10|-3", "7*|x+1|+|5x-4|+2+x", "x", "-(38)/(3)", "(32)/(21)");
		t("|x|-5", "0", "x", "5", "-5");
		t("|x|-5", "|x-2|", "x");
		t("|3-x|", "3-x", "x", "(-inf, 3]");
	}

	@Test
	public void cubicEquations() {
		t("x^2 * x", "x", "x", "-1", "0", "1");;
		t("x^3", "x", "x", "-1", "0", "1");
		t("x^3 + 3 * a * x^2 + 3 * a^2 * x + 1", "0", "x", "(nroot(((a)^(3)-1), 3)-a)");
		t("x^3+1", "4", "x", "nroot(3, 3)");
		t("x^3+3x^2+3x+2", "0", "x", "-2");
		t("x^3-6x^2+12x+13", "0", "x", "(-nroot(21, 3) + 2)");
		t("x^3 - 6 x^2 + 11 x - 6", "0", "x", "1", "2", "3");
		t("x^3 - (29 x^2)/12 + (37 x)/24 - 1/4", "0", "x", "(1)/(4)", "(2)/(3)", "(3)/(2)");
		t("x^3 + 3 x^2 - 7 x + 3", "0", "x", "1", "(-nroot(7, 2)-2)", "(nroot(7, 2)-2)");
		t("x^3", "sqrt(2) - 2", "x", "nroot((nroot(2, 2)-2), 3)");
		t("x^3+2", "0", "x", "-nroot(2, 3)");
		t("x^3", "sqrt(1/8)", "x", "(nroot(2, 2))/(2)");
		t("x^3 - 6 x^2 + 11 x - 6", "0", "x", "1", "2", "3");
	}

	@Test
	public void higherOrderEquations() {
		t("x^4+2x+3", "2x+19", "x", "2", "-2");
		t("x^4+2x+3", "2x+2", "x");
		t("2x^5+2x+3", "2x+67", "x", "2");
		t("x^4+4x^2+2", "0", "x");
		t("x^4+x^3+4x^2+2", "0", "x", "fail");
		t("x^6+4x^3+2", "0", "x", "nroot((nroot(2, 2)-2), 3)", "nroot((-nroot(2, 2)-2), 3)");
		t("((x+1)^4+1)^2", "6", "x", "(nroot((nroot(6, 2)-1), 4)-1)", "(-nroot((nroot(6, 2)-1), 4)-1)");
		t("((1+x)^(2)+1)^(2)+1", "10", "x", "(nroot(2, 2)-1)", "(-nroot(2, 2)-1)");
	}

	@Test
	public void trigonometricEquations() {
		t("3+2sin(x)", "sin(x)-1", "x");
		t("1/2+2sin(x)", "sin(x)+1", "x", "((pi + (12)(k1)(pi)))/(6)", "(((5)(pi)-(12)(k2)(pi)))/(6)");
		t("1/2+2sin(3x+1)", "sin(3x+1)+1", "x", "((pi + (12)(k1)(pi)-6))/(18)",
				"(((5)(pi)-(12)(k2)(pi)-6))/(18)");
		t("(sin(2x+1))^2+1/2", "1", "x", "((pi + (8)(k1)(pi)-4))/(8)",
				"(((3)(pi)-(8)(k2)(pi)-4))/(8)", "((-pi + (8)(k3)(pi)-4))/(8)",
				"(((5)(pi)-(8)(k4)(pi)-4))/(8)");
		t("1/2+2cos(3x+1)", "cos(3x+1)+1", "x", "((pi + (6)(k1)(pi)-3))/(9)",
				"(((5)(pi)-(6)(k2)(pi)-3))/(9)");
		t("3+2tan(x)", "tan(x)-1", "x", "(arctan(-4) + (k1)(pi))");
		t("2(sin(x))^2+(cos(x))^2+cos(x)", "1", "x", "(arccos(((1-nroot(5, 2)))/(2)) + (2)(k1)(pi))",
				"((2)(pi)-arccos(((1-nroot(5, 2)))/(2))-(2)(k2)(pi))");
		t("2(cos(x))^2+(sin(x))^2+sin(x)", "1", "x", "(arcsin(((1-nroot(5, 2)))/(2)) + (2)(k1)(pi))",
				"(pi-arcsin(((1-nroot(5, 2)))/(2))-(2)(k2)(pi))");
		t("2(cos(x))^2+2(sin(x))^2", "2", "x", "R");
		t("sin(x)+cos(x)", "1", "x", "((pi + (4)(k1)(pi)))/(2)", "(2)(k1)(pi)");
	}

	@Test
	public void extremeNested() {
		t("(((x+1)^2+1)^2+1)^2", "10", "x", "(nroot((nroot((nroot(10, 2)-1), 2)-1), 2)-1)",
				"(-nroot((nroot((nroot(10, 2)-1), 2)-1), 2)-1)");

		t("sqrt(1+sqrt(1+sqrt(1+x)))", "10", "x", "96039999");
		t("sqrt(1+sqrt(1+sqrt(1+sqrt(1+x))))", "10", "x", "9223681407920000");

		t("sqrt(x+sqrt(x+sqrt(x+1)))", "10", "x", "fail");

		// TODO: problem with accuracy
		// t("x^2", "12345678987654321", "x", "-111111111", "111111111");

		// 85076298714809070000000000000000
		// doesn't agree with CAS answer of
		// 85076298714809073438726399999999
		// t("sqrt(1+sqrt(1+sqrt(1+sqrt(1+sqrt(1+x)))))", "10", "x", 22,
		// "85076298714809073438726399999999");

	}

	@Test
	public void decimalSolve() {
		t("3.14x + 2.81", "1.41", "x", "-0.445859873");
		t("sqrt(2) + 1/3 + 0.24454*x", "(3.14)/(2.1*x)", "x", "-7.918445442", "0.772183499");
	}

	public void t(String LHS, String RHS, String variable, String... expectedSolutions) {
		if (needsHeading) {
			Throwable t = new Throwable();
			htmlBuilder.addHeading(t.getStackTrace()[1].getMethodName(), 1);
			needsHeading = false;
		}
		htmlBuilder.addHeading("Testcase " + (caseCounter++), 2);

		StepExpression _LHS = (StepExpression) StepNode.getStepTree(LHS, app.getKernel().getParser());
		StepExpression _RHS = (StepExpression) StepNode.getStepTree(RHS, app.getKernel().getParser());
		StepVariable var = new StepVariable(variable);

		SolutionBuilder steps = new SolutionBuilder();

		StepNode[] solutions = new StepNode[0];

		try {
			solutions = new StepEquation(_LHS, _RHS).solveAndCompareToCAS(app.getKernel(), var, steps).getElements();
		} catch (SolveFailedException e) {
			htmlBuilder.addHeading("Failed: ", 4);
			e.getSteps().getListOfSteps(htmlBuilder, app.getLocalization());

			Assert.assertArrayEquals(expectedSolutions, new String[] { "fail" });
			return;
		} catch (CASConflictException e) {
			htmlBuilder.addHeading("CAS conflict: ", 4);
			e.getSteps().getListOfSteps(htmlBuilder, app.getLocalization());

			Assert.assertArrayEquals(expectedSolutions, new String[] { "CASfail" });
			return;
		} catch (CASException e) {
			e.printStackTrace();
			Assert.fail();
		}

		steps.getSteps().getListOfSteps(htmlBuilder, app.getLocalization());

		Assert.assertEquals(expectedSolutions.length, solutions.length);

		String[] actualSolutions = new String[solutions.length];
		for (int i = 0; i < expectedSolutions.length; i++) {
			actualSolutions[i] = solutions[i].toString();
		}

		Arrays.sort(expectedSolutions);
		Arrays.sort(actualSolutions);

		Assert.assertArrayEquals(expectedSolutions, actualSolutions);
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
