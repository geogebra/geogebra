package org.geogebra.common.kernel.stepbystep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steps.SystemSteps;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquation;
import org.geogebra.common.kernel.stepbystep.steptree.StepEquationSystem;
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
public class SystemStepTest {
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
    public void linearSystems() {
        t(new String[] { "3x + 2y + z = 2", "2x + 3y + 3z = 1"}, "x y", 0,
                "x = (((3)(z) + 4))/(5), y = ((-(7)(z)-1))/(5)");
        t(new String[] { "3x + 2y + z = 2", "2x + 3y + 3z = 1", "3x + 2y + 3z = 3"}, "x y z", 0,
                "x = (11)/(10), y = -(9)/(10), z = (1)/(2)");
        t(new String[] { "3x + 2y = 1", "2x + 3y = 2"}, "x y" ,0,
                "x = -(1)/(5), y = (4)/(5)");
    }

    @Test
    public void quadraticLinear() {
        t(new String[] { "3x + 2y = 1", "x^2 + y^2 = 1"}, "x y", 0,
                "x = (((4)(nroot(3, 2)) + 3))/(13), y = ((2-(6)(nroot(3, 2))))/(13)",
                "x = ((-(4)(nroot(3, 2)) + 3))/(13), y = ((2 + (6)(nroot(3, 2))))/(13)");
        t(new String[] { "x^2 + y^2 + z^2 = 9", "x + y = 2", "y + z = 3"}, "x y z", 0,
                "x = ((-nroot(13, 2) + 1))/(3), y = ((nroot(13, 2) + 5))/(3), " +
                        "z = ((4-nroot(13, 2)))/(3)",
                "x = ((nroot(13, 2) + 1))/(3), y = ((-nroot(13, 2) + 5))/(3), " +
                        "z = ((4 + nroot(13, 2)))/(3)");
    }

    @Test
    public void simpleElimination() {
        t(new String[] { "3x + 2y = 1", "2x + 3y = 2", "3x + 3y = 4"}, "x y", 1, "fail");
        t(new String[] { "3x + 2y = 1", "2x + 3y + z = 1"}, "x y z", 1, "fail");
        t(new String[] { "3x + 2y = 1 + y - x", "3 + x + y = -x + 2y"}, "x y", 1,
                "x = -(1)/(3), y = (7)/(3)");
        t(new String[] { "3x + 2y = 2", "2x + 3y = 1"}, "x y", 1,
                "x = (4)/(5), y = -(1)/(5)");
        t(new String[] { "-3x + 2y = 2", "2x + 3y = 1"}, "x y", 1,
                "x = -(4)/(13), y = (7)/(13)");
        t(new String[] { "3x + 2y = 2", "-2x + -3y = 1"}, "x y", 1,
                "x = (8)/(5), y = -(7)/(5)");
        // TODO: simplify solution at the end
        t(new String[] { "sqrt(2) * x + 4/5 * y = 2", "1/9 * x + sqrt(5) * y = 1"}, "x y", 1,
                "x = -(((-(182106)(nroot(2, 2))-(45)((2-(9)(nroot(2, 2))))((4 + (45)(nroot(10, 2))))(nroot(10, 2))))(nroot(2, 2)))/(40468), "
                        + "y = -((5)((2-(9)(nroot(2, 2))))((4 + (45)(nroot(10, 2)))))/(20234)");
        t(new String[] { "3x + 8y = 2", "-2x + -2y = 1"}, "x y", 1,
                "x = -(6)/(5), y = (7)/(10)");
        t(new String[] { "3x + 4y = 2", "3x + 4y = 3"}, "x y", 1);
        t(new String[] { "3x + 4y = 2", "6x + 8y = 4"}, "x y", 1,
                "x in R, y = ((2-(3)(x)))/(4)");
    }

    @Test
    public void cramersRule() {
        t(new String[] {"x + y - 3 = -z", "2x + 2y + 3z = 8 - z", "-12 + 4x + 5y + 5z = x + y"},
                "x y z", 2, "x = 1, y = 1, z = 1");
    }

    @Test
    public void gaussJordanElimination() {
        t(new String[] {"x + y - 3 = -z", "2x + 2y + 3z = 8 - z", "-12 + 4x + 5y + 5z = x + y"},
                "x y z", 3, "x = 1, y = 1, z = 1");
        t(new String[] { "3x + 2y + z = 2", "2x + 3y + 3z = 1", "3x + 2y + 3z = 3"},
                "x y z", 3, "x = (11)/(10), y = -(9)/(10), z = (1)/(2)");
    }

    public void t(String[] equations, String variableString, int method,
                  String... expectedSolutions) {
        if (needsHeading) {
            Throwable t = new Throwable();
            htmlBuilder.addHeading(t.getStackTrace()[1].getMethodName(), 1);
            needsHeading = false;
        }

        htmlBuilder.addHeading("Testcase " + (caseCounter++), 2);

        List<StepEquation> stepEquations = new ArrayList<>();
        for (String eq : equations) {
            stepEquations.add((StepEquation) StepNode.getStepTree(eq, app.getKernel().getParser()));
        }

        SolutionBuilder steps = new SolutionBuilder();
        StepEquationSystem ses = new StepEquationSystem(stepEquations.toArray(new StepEquation[0]));

        List<StepVariable> variables = new ArrayList<>();
        for (String variableName : variableString.split(" ")) {
            variables.add(new StepVariable(variableName));
        }

        try {
            List<StepSolution> solutions = null;

            switch (method) {
                case 0:
                    solutions = SystemSteps.solveBySubstitution(ses, variables, steps);
                    break;
                case 1:
                    solutions = SystemSteps.solveByElimination(ses, variables, steps);
                    break;
                case 2:
                    solutions = SystemSteps.cramersRule(ses, variables, steps);
                    break;
                case 3:
                    solutions = SystemSteps.gaussJordanElimination(ses, variables, steps);
            }

            htmlBuilder.buildStepGui(steps.getSteps());

            String[] actualSolutions = new String[solutions.size()];
            for (int i = 0; i < solutions.size(); i++) {
                actualSolutions[i] = solutions.get(i).toString();
            }

            Arrays.sort(actualSolutions);
            Arrays.sort(expectedSolutions);

            Assert.assertArrayEquals(expectedSolutions, actualSolutions);
        } catch (SolveFailedException e) {
            htmlBuilder.addHeading("Failed: ", 4);
            if (e.getSteps() != null) {
                htmlBuilder.buildStepGui(steps.getSteps());
            }

            Assert.assertArrayEquals(expectedSolutions, new String[] { "fail" });
        } catch (CASConflictException e) {
            htmlBuilder.addHeading("CAS conflict: ", 4);
            htmlBuilder.buildStepGui(steps.getSteps());

            Assert.assertArrayEquals(expectedSolutions, new String[] { "CASfail" });
        } catch (CASException e) {
            Log.debug(e);
            Assert.fail();
        }
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
