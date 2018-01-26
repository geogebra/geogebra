package org.geogebra.stepbystep;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.steps.RegroupSteps;
import org.geogebra.common.kernel.stepbystep.steps.RegroupTracker;
import org.geogebra.common.kernel.stepbystep.steps.SimplificationStepGenerator;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.main.App;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class RegroupUnitTests {
    private static App app;

    @BeforeClass
    public static void setupApp() {
        app = CommandsTest.createApp();
    }

    @Test
    public void regroupProductsTest() {
        regroupProduct("2*3*4", "24");
        regroupProduct("2*2*2", "8");
        regroupProduct("2*2^2", "(2)^((1 + 2))");
        regroupProduct("2*2*2^3", "(2)^((1 + 1 + 3))");
        regroupProduct("sqrt(2*2)", "nroot((2)^((1 + 1)), 2)");
    }

    public void regroupProduct(String a, String b) {
        test(RegroupSteps.REGROUP_PRODUCTS, a, b);
    }

    public void test(SimplificationStepGenerator ssg, String a, String b) {
        Assert.assertEquals(b, ssg.apply(convert(a), new SolutionBuilder(), new RegroupTracker()).toString());
    }

    private StepExpression convert(String s) {
        return (StepExpression) StepNode.getStepTree(s, app.getKernel().getParser());
    }
}