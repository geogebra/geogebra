package org.geogebra.stepbystep;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class GeneralUnitTests {
    private static App app;

    @BeforeClass
    public static void setupApp() {
        app = CommandsTest.createApp();
    }

    @Test
    public void equalsTest() {
        equals("3+2+1", "1+2+3", true);
        equals("3*(x+1)", "(1+x)*3", true);
        equals("x+x", "2*x", false);
        equals("((1+2)*3+4)*5+6", "6+5*(4+3*(2+1))", true);
        equals("1+1", "2", false);
    }

    @Test
    public void containsTest() {
        contains("3+2+1", "1+3", true);
        contains("x*2+5", "2*x", true);
        contains("x*2+5", "2*x+2*x", false);
        contains("4(x-3)", "2(x-3)", true);
        contains("sqrt(x+1)", "x+1", false);
        contains("0", "12", false);
    }

    @Test
    public void quotientRemainderTest() {
        quotient("13", "5", "2");
        remainder("13", "5", "3");
        quotient("4(x-3)+5", "x-3", "4");
        remainder("4(x-3)+5", "x-3", "5");
        quotient("4(x-3)+5", "2(x-3)", "2");
        remainder("4(x-3)+5", "2(x-3)", "5");
    }

    @Test
    public void largestNthPowerTest() {
        nthpower(8, 2, 4);
        nthpower(8, 3, 8);
        nthpower(108, 2, 36);
        nthpower(12, 2, 4);
    }

    public void nthpower(int a, int b, int c) {
        Assert.assertEquals(c, StepNode.largestNthPower(StepConstant.create(a), b));
    }

    public void equals(String a, String b, boolean eq) {
        Assert.assertEquals(convert(a).equals(convert(b)), eq);
        Assert.assertEquals(convert(b).equals(convert(a)), eq);
    }

    public void contains(String a, String b, boolean cont) {
        Assert.assertEquals(convert(a).containsExpression(convert(b)), cont);
    }

    public void quotient(String a, String b, String c) {
        Assert.assertTrue(convert(a).quotient(convert(b)).equals(convert(c)));
    }

    public void remainder(String a, String b, String c) {
        Assert.assertTrue(convert(a).remainder(convert(b)).equals(convert(c)));
    }

    private StepExpression convert(String s) {
        return (StepExpression) StepNode.getStepTree(s, app.getKernel().getParser());
    }
}
