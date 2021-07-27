package org.geogebra.common.kernel.stepbystep.steps;

import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
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
		app = AlgebraTest.createApp();
    }

    @Test
    public void regroupProductsTest() {
        regroupProduct("2*3*4", "24");
        regroupProduct("2*2*2", "8");
        regroupProduct("2*2^2", "(2)^((1 + 2))");
        regroupProduct("2*2*2^3", "(2)^((1 + 1 + 3))");
        regroupProduct("sqrt(2*2)", "nroot((2)^((1 + 1)), 2)");
    }

    @Test
    public void simplifyRootTest() {
        simplifyRoot("nroot(((x)^(3))(y)(27)((z)^(2)), 2)", "(|x|)(3)(|z|)(nroot((x)(y)(3), 2))");
        simplifyRoot("nroot(x^(3k+1), 3)", "((x)^(k))(nroot(x, 3))");
        simplifyRoot("nroot(x^(2k+1), 2)", "((|x|)^(k))(nroot(x, 2))");
    }

    @Test
    public void simplifyPowerOfRootTest() {
        simplifyPowerOfRoot("(nroot(x, 2))^((2)(k))", "(x)^(k)");
    }

    @Test
    public void factorCommonTest() {
        factorCommon("2^k + 2^(k+1)", "((2)^(k))((1 + 2))");
        factorCommon("2^(2k)+2^(k+1)", "((2)^(k))(((2)^(k) + 2))");
    }

    @Test
    public void simplifyFractionTest() {
        simplifyFraction("(2^(2k)+2^(k+1))/((2)^(k))", "((2)^(k) + 2)");
    }

    public void regroupProduct(String a, String b) {
        test(RegroupSteps.REGROUP_PRODUCTS, a, b);
    }

    public void simplifyRoot(String a, String b) {
        test(RegroupSteps.SIMPLIFY_ROOTS, a, b);
    }

    public void simplifyPowerOfRoot(String a, String b) {
        test(RegroupSteps.SIMPLIFY_POWER_OF_ROOT, a, b);
    }

    public void factorCommon(String a, String b) {
        test(FactorSteps.FACTOR_COMMON_SUBSTEP, a, b);
    }

    public void simplifyFraction(String a, String b) {
        test(FractionSteps.SIMPLIFY_FRACTIONS, a, b);
    }

    public void test(SimplificationStepGenerator ssg, String a, String b) {
        Assert.assertEquals(b, ssg.apply(convert(a), new SolutionBuilder(), new RegroupTracker()).toString());
    }

    private StepExpression convert(String s) {
        return (StepExpression) StepNode.getStepTree(s, app.getKernel().getParser());
    }
}