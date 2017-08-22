package org.geogebra.stepbystep;

import java.util.List;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.kernel.stepbystep.EquationSteps;
import org.geogebra.common.kernel.stepbystep.SolutionStep;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.main.App;
import org.junit.Assert;
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

	@Test
	public void linearEquation() {
		t("(4x-3)/(2 + 3)", "1-x", "x", 17, "(8)/(9)");
		t("1-2x", "2x+8", "x", 12, "-(7)/(4)");
		t("(2x-5)/12", "(-x)/4-5/3", "x", 15, "-3");
		t("x/x", "0", "x", 5);
		t("2x+1", "x-2", "x", 9, "-3");
		t("3x+4", "-x-1", "x", 12, "-(5)/(4)");
		t("x-2", "sqrt(3)", "x", 6, "(nroot(3, 2) + 2)");
		t("x-2", "sqrt(12)-2*sqrt(3)", "x", 8, "2");
	}

	@Test
	public void rationalEquations() {
		t("1/x", "4x", "x", 17, "nroot((1)/(4), 2)", "-nroot((1)/(4), 2)");
		t("1/(1+x)-2x/(2+x)", "7", "x", 16, "(((2)(nroot(13, 2))-22))/(18)", "((-(2)(nroot(13, 2))-22))/(18)");
		t("1/x-1/(x+1)", "3", "x", 16, "((nroot(21, 2)-3))/(6)", "((-nroot(21, 2)-3))/(6)");
		t("x-1/x", "2x", "x", 9);
		t("9", "x/3-x/4", "x", 6, "108");
		t("x+1/(x-1)", "2x-1", "x", 22, "0", "2");
		t("1/(x-6)+x/(x-2)", "4/(x^2-8x+12)", "x", 25, "-1");
		t("x/(1-x)", "(3+x)/x", "x", 16, "(((2)(nroot(7, 2))-2))/(4)", "((-(2)(nroot(7, 2))-2))/(4)");
		t("((1)/(x)+1)^(2)", "((1)/(x+3)-2)^(2)", "x", 38, "(((3)(nroot(5, 2))-9))/(6)", "((-(3)(nroot(5, 2))-9))/(6)",
				"((nroot(13, 2)-1))/(2)",
				"((-nroot(13, 2)-1))/(2)");
		t("(1/x+3)^2", "6", "x", 40, "-(1)/((-nroot(6, 2) + 3))", "-(1)/((nroot(6, 2) + 3))");
	}

	@Test
	public void productEquations() {
		t("0", "(x-8)(x+9)", "x", 14, "8", "-9");
		t("0", "(x^2-3x-8)(x+5)", "x", 15, "((nroot(41, 2) + 3))/(2)", "((-nroot(41, 2) + 3))/(2)", "-5");
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
	}

	@Test
	public void irrationalEquations() {
		t("sqrt(3x+1)", "x+1", "x", 22, "0", "1");
		t("sqrt(3x+1)", "x+1+sqrt(2x+3)", "x", 22);
		t("2x+10", "x+1+sqrt(5x-4)", "x", 16);
		t("sqrt(3x-4)", "sqrt(4x-3)", "x", 14);
		t("sqrt(x)+sqrt(x+1)+sqrt(x+2)", "2", "x", 31);
		t("sqrt(x)+1", "sqrt(x+1)+sqrt(x+2)+1", "x", 25);
		t("sqrt(x-1)", "sqrt(x)", "x", 9);
		t("sqrt(1+sqrt(1+sqrt(x)))", "10", "x", 20, "96040000");
		t("sqrt(1+ sqrt(1+ sqrt(1+ sqrt(x))))", "5", "x", 26, "109312229376");
		t("sqrt(1+sqrt(x))", "10", "x", 14, "9801");
		t("1+ sqrt(1+ sqrt(1+ sqrt(x)))", "5", "x", 23, "50176");
	}

	@Test
	public void absoluteValueEquations() {
		t("4*|2x-10|-3", "7*|x+1|+|5x-4|+2+x", "x", 79, "-(38)/(3)", "(32)/(21)");
		t("|x|-5", "0", "x", 8, "5", "-5");
		t("|x|-5", "|x-2|", "x", 32);
	}

	@Test
	public void qubicEquations() {
		t("x^3+1", "4", "x", 9, "nroot(3, 3)");
		t("x^3+3x^2+3x+2", "0", "x", 14, "(nroot(-1, 3)-1)");
		t("x^3-6x^2+12x+13", "0", "x", 14, "(nroot(-21, 3) + 2)");
		t("x^3 - 6 x^2 + 11 x - 6", "0", "x", 22, "3", "2", "1");
		t("x^3 - x - 5 x^2 - x - 3", "0", "x", 4, "5.47");
		t("x^3 - (29 x^2)/12 + (37 x)/24 - 1/4", "0", "x", 27, "(3)/(2)", "(2)/(3)", "(1)/(4)");
		t("x^3 + 3 x^2 - 7 x + 3", "0", "x", 34, "1", "(nroot(7, 2)-2)", "(-nroot(7, 2)-2)");
		t("x^3", "sqrt(2) - 2", "x", 6, "nroot((nroot(2, 2)-2), 3)");
		t("x^3+2", "0", "x", 9, "nroot(-2, 3)");
	}

	@Test
	public void higherOrderEquations() {
		t("x^4+2x+3", "2x+19", "x", 14, "2", "-2");
		t("x^4+2x+3", "2x+2", "x", 9);
		t("2x^5+2x+3", "2x+67", "x", 15, "2");
		t("x^4+4x^2+2", "0", "x", 30);
		t("x^4+x^3+4x^2+2", "0", "x", 4);
		t("x^6+4x^3+2", "0", "x", 36, "nroot((nroot(2, 2)-2), 3)", "nroot((-nroot(2, 2)-2), 3)");
		t("(((1+x)^(2)+1)^(2)+1)^(2)", "10", "x", 48, "(nroot((nroot((nroot(10, 2)-1), 2)-1), 2)-1)",
				"(-nroot((nroot((nroot(10, 2)-1), 2)-1), 2)-1)");
		t("((x+1)^4+1)^2", "6", "x", 33, "(nroot((nroot(6, 2)-1), 4)-1)", "(-nroot((nroot(6, 2)-1), 4)-1)");
		t("((1+x)^(2)+1)^(2)+1", "10", "x", 36, "(nroot(2, 2)-1)", "(-nroot(2, 2)-1)");
	}

	@Test
	public void trigonometricEquations() {
		t("3+2sin(x)", "sin(x)-1", "x", 10);
		t("1/2+2sin(x)", "sin(x)+1", "x", 27, "((2)([k0])(pi) + (pi)/(6))", "-(((2)([k0])(pi) + (pi)/(6)-pi))");
		t("1/2+2sin(3x+1)", "sin(3x+1)+1", "x", 33, "(((2)([k0])(pi) + (pi)/(6)-1))/(3)", "-(((2)([k0])(pi) + (pi)/(6)-pi + 1))/(3)");
		t("(sin(2x+1))^2+1/2", "1", "x", 59, "(((2)([k0])(pi) + arcsin(nroot((1)/(2), 2))-1))/(2)",
				"-(((2)([k0])(pi) + arcsin(nroot((1)/(2), 2))-pi + 1))/(2)", "(((2)([k0])(pi) + arcsin(-nroot((1)/(2), 2))-1))/(2)",
				"-(((2)([k0])(pi) + arcsin(-nroot((1)/(2), 2))-pi + 1))/(2)");
		t("1/2+2cos(3x+1)", "cos(3x+1)+1", "x", 33, "(((2)([k0])(pi) + (pi)/(3)-1))/(3)",
				"-(((2)([k0])(pi) + (pi)/(3) + (-2)(pi) + 1))/(3)");
		t("3+2tan(x)", "tan(x)-1", "x", 11, "(arctan(-4) + ([k0])(pi))");
	}

	public void t(String LHS, String RHS, String variable, int expectedSteps, String... expectedSolutions) {
		EquationSteps es = new EquationSteps(app.getKernel(), LHS, RHS, variable);

		SolutionStep steps = es.getSteps();
		List<StepNode> solutions = es.getSolutions();

		Assert.assertEquals(expectedSteps, countSteps(steps));
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
}
