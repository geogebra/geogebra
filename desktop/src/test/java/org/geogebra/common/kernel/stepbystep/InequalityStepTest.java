package org.geogebra.common.kernel.stepbystep;

import java.util.Arrays;

import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.StepInequality;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepSolution;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class InequalityStepTest {
    private static App app;

    @BeforeClass
    public static void setupApp() {
		app = AlgebraTest.createApp();
        htmlBuilder = new HtmlStepBuilder(app.getLocalization());
        // just to load CAS
        try {
            app.getKernel().evaluateGeoGebraCAS("Regroup(1)", null);
        } catch (Throwable e) {
            Log.debug(e);
        }
    }

    private static HtmlStepBuilder htmlBuilder;
    private boolean needsHeading;
    private static int caseCounter = 0;

    @Test
    public void linearInequality() {
        i("3x + 2", "<=", "5", "x", "x in (-inf, 1]");
        i("3x + 2", "<", "5", "x", "x in (-inf, 1)");
        i("2x + 1", ">", "9x+7", "x", "x in (-inf, -(6)/(7))");
        i("2x + 1", ">=", "9x+7", "x", "x in (-inf, -(6)/(7)]");
        i("x", ">", "|k|", "x", "x in (|k|, inf)");
    }

    @Test
    public void positiveVsZeroInequality() {
    	i("(x+2)^2", ">", "0", "x", "x in R \\ {-2}");
        i("x^2+2x+1", ">=", "0", "x", "x in R");
        i("x^2+2x+1", ">", "0", "x", "x in R \\ {-1}");
        i("x^2+2x+1", "<", "0", "x");
        i("x^2+2x+1", "<=", "0", "x", "x = -1");
        i("0", ">=", "x^2+2x+1", "x", "x = -1");
        i("0", ">", "x^2+2x+1", "x");
        i("0", "<=", "x^2+2x+1", "x", "x in R");
        i("0", "<", "x^2+2x+1", "x", "x in R \\ {-1}");
		i("(x-k)^2", ">", "0", "x", "x in R \\ {k}");
		i("(x-k)^2", ">", "0", "k", "k in R \\ {x}");
    }

    @Test
    public void tableBasedInequality() {
        i("x^3 + 3 x^2 - 7 x + 3", ">=", "0", "x",
                "x in [(-nroot(7, 2)-2), (nroot(7, 2)-2)]", "x in [1, inf)");
        i("(x^2-3x+2)/(x-3)", ">=", "0", "x",
                "x in [1, 2]", "x in (3, inf)");
        i("x^3 - 5*x^2 + 8*x - 4", ">=", "0", "x", "x in [1, inf)");
        i("x^3 - 5*x^2 + 8*x - 4", ">", "0", "x", "x in (1, 2)",
                "x in (2, inf)");
    }

    @Test
	public void rationalInequalities() {
		i("1/x", ">", "1", "x", "x in (0, 1)");
		i("1/x", "<=", "1", "x", "x in (-inf, 0)", "x in [1, inf)");
		i("(x+2)/(x+2)", ">", "1", "x");
		i("(x+2)/(x+2)", ">", "0", "x", "x in R \\ {-2}");
	}

    @Test
    public void oneOverXtest() {
        i("1/x", "<", "x", "x", "x in (-1, 0)", "x in (1, inf)");
        i("1/x", ">", "x", "x", "x in (-inf, -1)", "x in (0, 1)");
    }

    @Test
    public void quadraticInequality() {
        i("x^2+4x+5", ">", "0", "x", "x in R");
        i("2*x^2+5x+6", ">", "0", "x", "x in R");
        i("3x^2+5x-2", ">", "0", "x", "x in (-inf, -2)", "x in ((1)/(3), inf)");
        i("3x^2+5x-3", "<=", "0", "x", "x in [((-5-nroot(61, 2)))/(6), ((-5 + nroot(61, 2)))/(6)]");
    }

    @Test
	public void notSupportedTest() {
		i("x^2", ">", "|k|", "x", "fail");
		i("x^2", ">", "k^2", "x", "fail");
	}

    public void i(String LHS, String op, String RHS, String variable, String... expectedSolutions) {
        if (needsHeading) {
            Throwable t = new Throwable();
            htmlBuilder.addHeading(t.getStackTrace()[1].getMethodName(), 1);
            needsHeading = false;
        }
        htmlBuilder.addHeading("Testcase " + (caseCounter++), 2);

		StepInequality ineq = StepInequality.from(LHS, op, RHS,
				app.getKernel().getParser());
		StepVariable var = new StepVariable(variable);
        SolutionBuilder steps = new SolutionBuilder();

        StepNode[] solutions = new StepNode[0];

        try {
			solutions = ineq.solveAndCompareToCAS(app.getKernel(), var, steps)
                    .toArray(new StepSolution[0]);
        } catch (SolveFailedException e) {
            htmlBuilder.addHeading("Failed: ", 4);
            htmlBuilder.buildStepGui(steps.getSteps());

            Assert.assertArrayEquals(expectedSolutions, new String[] { "fail" });
            return;
        } catch (CASConflictException e) {
            htmlBuilder.addHeading("CAS conflict: ", 4);
            htmlBuilder.buildStepGui(steps.getSteps());

            Assert.assertArrayEquals(expectedSolutions, new String[] { "CASfail" });
            return;
        } catch (CASException e) {
            Log.debug(e);
            Assert.fail();
        }

        htmlBuilder.buildStepGui(steps.getSteps());

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
		htmlBuilder.printReport("inequalities.html");
    }
}
