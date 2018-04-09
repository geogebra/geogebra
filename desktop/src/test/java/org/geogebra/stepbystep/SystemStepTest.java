package org.geogebra.stepbystep;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.stepbystep.CASConflictException;
import org.geogebra.common.kernel.stepbystep.SolveFailedException;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steps.SystemSteps;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquation;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquationSystem;
import org.geogebra.common.main.App;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class SystemStepTest {
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
    public void linearSystems() {
        t(new String[] { "3x + 2y + z = 2", "2x + 3y + 3z = 1", "3x + 2y + 3z = 3"}, 0);
        t(new String[] { "3x + 2y = 1", "2x + 3y = 2"}, 0);
    }

    @Test
    public void quadraticLinear() {
        t(new String[] { "3x + 2y = 1", "x^2 + y^2 = 1"}, 0);
        t(new String[] { "x^2 + y^2 + z^2 = 9", "x + y = 2", "y + z = 3"}, 0);
    }

    @Test
    public void simpleElimination() {
        t(new String[] { "3x + 2y = 1", "2x + 3y = 2", "3x + 3y = 4"}, 1, "fail");
        t(new String[] { "3x + 2y = 1", "2x + 3y + z = 1"}, 1, "fail");
        t(new String[] { "3x + 2y = 1 + y - x", "3 + x + y = -x + 2y"}, 1);
        t(new String[] { "3x + 2y = 2", "2x + 3y = 1"}, 1);
        t(new String[] { "-3x + 2y = 2", "2x + 3y = 1"}, 1);
        t(new String[] { "3x + 2y = 2", "-2x + -3y = 1"}, 1);
        t(new String[] { "sqrt(2) * x + 4/5 * y = 2", "1/9 * x + sqrt(5) * y = 1"}, 1);
        t(new String[] { "3x + 8y = 2", "-2x + -2y = 1"}, 1);
        t(new String[] { "3x + 4y = 2", "3x + 4y = 3"}, 1);
        t(new String[] { "3x + 4y = 2", "6x + 8y = 4"}, 1);
    }

    public void t(String[] equations, int method, String... expectedSolutions) {
        if (needsHeading) {
            Throwable t = new Throwable();
            htmlBuilder.addHeading(t.getStackTrace()[1].getMethodName(), 1);
            needsHeading = false;
        }

        htmlBuilder.addHeading("Testcase " + (caseCounter++), 2);

        List<StepEquation> stepEquations = new ArrayList<>();
        for (String eq : equations) {
            stepEquations.add(new StepEquation(eq, app.getKernel().getParser()));
        }

        SolutionBuilder steps = new SolutionBuilder();
        StepEquationSystem ses = new StepEquationSystem(stepEquations.toArray(new StepEquation[0]));

        try {
            switch (method) {
                case 0:
                    SystemSteps.solveBySubstitution(ses, steps);
                    break;
                case 1:
                    SystemSteps.solveByElimination(ses, steps);
            }
        } catch (SolveFailedException e) {
            htmlBuilder.addHeading("Failed: ", 4);
            if (e.getSteps() != null) {
                e.getSteps().getListOfSteps(htmlBuilder, app.getLocalization());
            }

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
    }

    @Before
    public void resetHeading() {
        needsHeading = true;
    }

    @AfterClass
    public static void printHtml() {
        htmlBuilder.printReport("system.html");
    }
}
