package org.geogebra.common.kernel.stepbystep;

import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.kernel.stepbystep.steptree.StepConstant;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.kernel.stepbystep.steptree.StepVariable;
import org.geogebra.common.main.App;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class GeneralUnitTests {
    private static App app;

    @BeforeClass
    public static void setupApp() {
		app = AlgebraTest.createApp();
    }

    @Test
    public void equalsTest() {
        equals("3+2+1", "1+2+3", true);
        equals("3*(x+1)", "(1+x)*3", true);
        equals("x+x", "2*x", false);
        equals("((1+2)*3+4)*5+6", "6+5*(4+3*(2+1))", true);
        equals("1+1", "2", false);
        equals("arcsin(x) + arccos(x) + arctan(x)", "arcsin(x) + arccos(x) + arctan(x)", true);
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
		remainder("2 * x", "2 * pi", "(2)(x)");
		quotient("pi", "pi", "1");
		remainder("pi", "pi", "0");
		quotient("x", "x", "1");
		quotient("51 * pi", "2 * pi", "25");
		remainder("51 * pi", "2 * pi", "pi");
        quotient("13", "5", "2");
        remainder("13", "5", "3");
        quotient("4(x-3)+5", "x-3", "4");
        remainder("4(x-3)+5", "x-3", "5");
        quotient("4(x-3)+5", "2(x-3)", "2");
        remainder("4(x-3)+5", "2(x-3)", "5");
        quotient("3k+1", "3", "k");
        remainder("3k+1", "3", "1");
		quotient("3*2", "3", "2");
    }

    @Test
    public void getCommonTest() {
        getCommon("3", "5", "3");
        getCommon("3+k", "3", "3");
        getCommon("z+y+3", "z+5", "(z + 3)");
        getCommon("l*k", "l", "l");
        getCommon("l*k+1", "l+k+1", "(l + 1)");
    }

    @Test
    public void GCDTest() {
        GCD("2^(2k+1)", "2^(k+2)", "(2)^((k + 1))");
        GCD("(x^2-3x+2)", "(x-1)", "(x-1)");
    }

    @Test
    public void largestNthPowerTest() {
        nthpower(8, 2, 4);
        nthpower(8, 3, 8);
        nthpower(108, 2, 36);
        nthpower(12, 2, 4);
    }

    @Test
    public void signTest() {
        sign("exp(x)", 1);
        sign("x^x", 0);
        sign("x^2", 1);
        sign("(x^2)^x", 1);
        sign("2+3", 1);
        sign("2+x", 0);
        sign("|x| + x^2", 1);
        sign("|x| - x^2", 0);
        sign("x^(2k)", 0);
        sign("x^(2k+1)", 0);
        sign("x^2*y^4", 1);
        sign("-3-|x|", -1);
    }

    @Test
    public void weakGCDTest() {
        weakGCD("x*x", "x*1", "x");
        weakGCD("((4)(x)-1)(6)((x)^(2))", "-(((4)(x)-1))(13)(x)", "(((4)(x)-1))(x)");
    }

    @Test
    public void getVariableTest() {
        findVariable("x+3", "x", "x");
        findVariable("(x+3)/2", "x", "(x)/(2)");
        findVariable("7*x+4*x+5", "x", "((7)(x) + (4)(x))");
    }

    public void weakGCD(String a, String b, String c) {
        Assert.assertEquals(c, StepHelper.weakGCD(convert(a), convert(b)).toString());
    }

    public void nthpower(int a, int b, int c) {
        Assert.assertEquals(c, StepNode.largestNthPower(StepConstant.create(a), b));
    }

    public void equals(String a, String b, boolean eq) {
        Assert.assertEquals(convert(a).equals(convert(b)), eq);
        Assert.assertEquals(convert(b).equals(convert(a)), eq);
    }

    public void sign(String a, int sign) {
        Assert.assertEquals(convert(a).sign(), sign);
    }

    public void contains(String a, String b, boolean cont) {
        Assert.assertEquals(convert(a).containsExpression(convert(b)), cont);
    }

    public void quotient(String a, String b, String c) {
        Assert.assertEquals(c, convert(a).quotient(convert(b)).toString());
    }

    public void remainder(String a, String b, String c) {
        Assert.assertEquals(c, convert(a).remainder(convert(b)).toString());
    }

    public void getCommon(String a, String b, String c) {
        Assert.assertEquals(c, convert(a).getCommon(convert(b)).toString());
    }

    public void GCD(String a, String b, String c) {
        Assert.assertEquals(c, StepHelper.gcd(convert(a), convert(b)).toString());
    }

    public void findVariable(String a, String b, String c) {
        Assert.assertEquals(c, convert(a).findVariableIn(new StepVariable(b)).toString());
    }

    private StepExpression convert(String s) {
        return (StepExpression) StepNode.getStepTree(s, app.getKernel().getParser());
    }
}
