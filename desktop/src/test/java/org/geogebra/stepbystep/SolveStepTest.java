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
			app.getKernel().evaluateGeoGebraCAS("regroup(1)", null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void linearEquation() {
		t("(4x-3)/(2 + 3)", "1-x", "x", 16, "(8)/(9)");
		t("1-2x", "2x+8", "x", 11, "(-7)/(4)");
		t("(2x-5)/12", "(-x)/4-5/3", "x", 16, "-3");
		t("x/x", "0", "x", 4);
		t("2x+1", "x-2", "x", 8, "-3");
		t("3x+4", "-x-1", "x", 13, "(5)/(-4)");
		t("x-2", "sqrt(3)", "x", 5, "(nroot(3, 2) + 2)");
		t("x-2", "sqrt(12)-2*sqrt(3)", "x", 7, "2");
	}

	@Test
	public void rationalEquations() {
		t("1/x", "4x", "x", 14, "nroot((1)/(4), 2)", "-nroot((1)/(4), 2)");
		t("1/(1+x)-2x/(2+x)", "7", "x", 15, "(((2)(nroot(13, 2))-22))/(18)", "(((-2)(nroot(13, 2))-22))/(18)");
		t("1/x-1/(x+1)", "3", "x", 15, "((nroot(21, 2)-3))/(6)", "((-nroot(21, 2)-3))/(6)");
		t("x-1/x", "2x", "x", 8);
		t("9", "x/3-x/4", "x", 5, "108");
		t("x+1/(x-1)", "2x-1", "x", 21, "0", "2");
		t("1/(x-6)+x/(x-2)", "4/(x^2-8x+12)", "x", 24, "-1");
		t("x/(1-x)", "(3+x)/x", "x", 15, "(((2)(nroot(7, 2))-2))/(4)", "(((-2)(nroot(7, 2))-2))/(4)");
		t("((1)/(x)+1)^(2)", "((1)/(x+3)-2)^(2)", "x", 35, "(((3)(nroot(5, 2))-9))/(6)", "(((-3)(nroot(5, 2))-9))/(6)",
				"((nroot(13, 2)-1))/(2)",
				"((-nroot(13, 2)-1))/(2)");
		t("(1/x+3)^2", "6", "x", 37, "(-1)/((-nroot(6, 2) + 3))", "(-1)/((nroot(6, 2) + 3))");
	}

	@Test
	public void productEquations() {
		t("0", "(x-8)(x+9)", "x", 13, "8", "-9");
		t("0", "(x^2-3x-8)(x+5)", "x", 14, "((nroot(41, 2) + 3))/(2)", "((-nroot(41, 2) + 3))/(2)", "-5");
	}

	@Test
	public void quadraticEquations() {
		t("x^2+4", "4x", "x", 12, "2");
		t("x^2+3x+1", "x-1", "x", 10);
		t("x-1", "x^2+3x+1", "x", 10);
		t("x^2+4x+1", "0", "x", 20, "(nroot(3, 2)-2)", "(-nroot(3, 2)-2)");
		t("x^2-6x+9", "(x-3)^2", "x", 7, "NaN");
		t("(x-5)^2", "x^2", "x", 21, "(5)/(2)");
		t("3x^2+3x+3", "x^2-x-2", "x", 9);
		t("(x-2)^2-x^2", "-x^2", "x", 12, "2");
	}

	@Test
	public void irrationalEquations() {
		t("sqrt(3x+1)", "x+1", "x", 21, "0", "1");
		t("sqrt(3x+1)", "x+1+sqrt(2x+3)", "x", 23);
		t("2x+10", "x+1+sqrt(5x-4)", "x", 15);
		t("sqrt(3x-4)", "sqrt(4x-3)", "x", 13);
		t("sqrt(x)+sqrt(x+1)+sqrt(x+2)", "2", "x", 20);
		t("sqrt(x)+1", "sqrt(x+1)+sqrt(x+2)+1", "x", 26);
		t("sqrt(x-1)", "sqrt(x)", "x", 8);
		t("sqrt(1+sqrt(1+sqrt(x)))", "10", "x", 19, "96040000");
		t("sqrt(1+ sqrt(1+ sqrt(1+ sqrt(x))))", "5", "x", 25, "109312229376");
		t("sqrt(1+sqrt(x))", "10", "x", 13, "9801");
		t("1+ sqrt(1+ sqrt(1+ sqrt(x)))", "5", "x", 22, "50176");
	}

	@Test
	public void absoluteValueEquations() {
		t("4*|2x-10|-3", "7*|x+1|+|5x-4|+2+x", "x", 80, "(-38)/(3)", "(32)/(21)");
		t("|x|-5", "0", "x", 21, "-5", "5");
		t("|x|-5", "|x-2|", "x", 32);
	}

	@Test
	public void qubicEquations() {
		t("x^3+1", "4", "x", 7, "nroot(3, 3)");
		t("x^3+3x^2+3x+2", "0", "x", 12, "(nroot(-1, 3)-1)");
		t("x^3-6x^2+12x+13", "0", "x", 12, "(nroot(-21, 3) + 2)");
		t("x^3 - 6 x^2 + 11 x - 6", "0", "x", 21, "1", "2", "3");
		t("x^3 - x - 5 x^2 - x - 3", "0", "x", 3, "5.47");
		t("x^3 - (29 x^2)/12 + (37 x)/24 - 1/4", "0", "x", 26, "(3)/(2)", "(2)/(3)", "(1)/(4)");
		t("x^3 + 3 x^2 - 7 x + 3", "0", "x", 31, "1", "(nroot(7, 2)-2)", "(-nroot(7, 2)-2)");
		t("x^3", "sqrt(2) - 2", "x", 4, "nroot((nroot(2, 2)-2), 3)");
		t("x^3+2", "0", "x", 7, "nroot(-2, 3)");
	}

	@Test
	public void higherOrderEquations() {
		t("x^4+2x+3", "2x+19", "x", 11, "nroot(16, 4)", "-nroot(16, 4)");
		t("x^4+2x+3", "2x+2", "x", 8);
		t("2x^5+2x+3", "2x+67", "x", 13, "nroot(32, 5)");
		t("x^4+4x^2+2", "0", "x", 29);
		t("x^4+x^3+4x^2+2", "0", "x", 3);
		t("x^6+4x^3+2", "0", "x", 35, "nroot((nroot(2, 2)-2), 3)", "nroot((-nroot(2, 2)-2), 3)");
		t("(((1+x)^(2)+1)^(2)+1)^(2)", "10", "x", 41, "(nroot((nroot((nroot(10, 2)-1), 2)-1), 2)-1)",
				"(-nroot((nroot((nroot(10, 2)-1), 2)-1), 2)-1)");
		t("((x+1)^4+1)^2", "6", "x", 28, "(nroot((nroot(6, 2)-1), 4)-1)", "(-nroot((nroot(6, 2)-1), 4)-1)");
		t("((1+x)^(2)+1)^(2)+1", "10", "x", 31, "(nroot(2, 2)-1)", "(-nroot(2, 2)-1)");
	}

	public void t(String LHS, String RHS, String variable, int expectedSteps, String... expectedSolutions) {
		EquationSteps es = new EquationSteps(app.getKernel(), LHS, RHS, variable);

		SolutionStep steps = es.getSteps();
		List<StepNode> solutions = es.getSolutions();

		Assert.assertTrue(Math.abs(expectedSteps - countSteps(steps)) <= 2);
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
