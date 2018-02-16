package org.geogebra.stepbystep;

import java.util.Arrays;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.stepbystep.CASConflictException;
import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steptree.StepInequality;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.main.App;
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
    public void linearInequality() {
        i("3x + 2", "<=", "5", "x", "(-inf, 1]");
        i("3x + 2", "<", "5", "x", "(-inf, 1)");
        i("2x + 1", ">", "9x+7", "x", "(-(6)/(7), inf)");
        i("2x + 1", ">=", "9x+7", "x", "[-(6)/(7), inf)");
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
                    .getElements();
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
		htmlBuilder.printReport("inequalities.html");
    }
}
