package org.geogebra.stepbystep;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.*;
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
        app = CommandsTest.createApp();
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
    public void positiveTest() {
        isPositive("exp(x)", true);
        isPositive("e^x", true);
        isPositive("x^x", false);
        isPositive("x^2", true);
        isPositive("(x^2)^x", true);
        isPositive("2+3", true);
        isPositive("2+x", false);
        isPositive("e^x", true);
        isPositive("|x| + x^2", true);
        isPositive("|x| - x^2", false);
        isPositive("x^(2k)", false);
        isPositive("x^(2k+1)", false);
        isPositive("x^2*y^4", true);
    }

    @Test
    public void simpleTableTest() {
        SolutionTable table = new SolutionTable(
                new StepVariable("x"),
                StepConstant.NEG_INF,
                StepConstant.create(-2),
                StepConstant.create(3),
                StepConstant.POS_INF
        );

        table.addRow(
                StepNode.add(new StepVariable("x"), StepConstant.create(2)),
                TableElementType.NEGATIVE,
                TableElementType.NEGATIVE,
                TableElementType.ZERO,
                TableElementType.POSITIVE,
                TableElementType.POSITIVE,
                TableElementType.POSITIVE,
                TableElementType.POSITIVE
        );

        table.addRow(
                StepNode.subtract(new StepVariable("x"), StepConstant.create(3)),
                TableElementType.NEGATIVE,
                TableElementType.NEGATIVE,
                TableElementType.NEGATIVE,
                TableElementType.NEGATIVE,
                TableElementType.ZERO,
                TableElementType.POSITIVE,
                TableElementType.POSITIVE
        );

        HtmlStepBuilder htmlBuilder = new HtmlStepBuilder();
        htmlBuilder.addLatexRow(table.getDefault(app.getLocalization()));
        htmlBuilder.printReport("table.html");
    }

    @Test
    public void complexTableTest() {
        StepVariable var = new StepVariable("x");

        SolutionTable table = new SolutionTable(
                new StepVariable("x"),
                StepConstant.NEG_INF,
                StepConstant.create(-2),
                StepConstant.create(-1),
                StepConstant.POS_INF
        );

        table.addRow(
                StepNode.multiply(var, StepNode.power(StepConstant.E, var)),
                TableElementType.VSPACE,
                TableElementType.CONCAVE_DECREASING,
                TableElementType.VSPACE,
                TableElementType.CONVEX_DECREASING,
                TableElementType.VSPACE,
                TableElementType.CONVEX_INCREASING,
                TableElementType.VSPACE
        );

        table.addRow(
                StepNode.multiply(StepNode.add(var, StepConstant.create(1)), StepNode.power(StepConstant.E, var)),
                TableElementType.NEGATIVE,
                TableElementType.NEGATIVE,
                TableElementType.NEGATIVE,
                TableElementType.NEGATIVE,
                TableElementType.ZERO,
                TableElementType.POSITIVE,
                TableElementType.POSITIVE
        );

        table.addRow(
                StepNode.multiply(StepNode.add(var, StepConstant.create(2)), StepNode.power(StepConstant.E, var)),
                TableElementType.NEGATIVE,
                TableElementType.NEGATIVE,
                TableElementType.ZERO,
                TableElementType.POSITIVE,
                TableElementType.POSITIVE,
                TableElementType.POSITIVE,
                TableElementType.POSITIVE
        );

        HtmlStepBuilder htmlBuilder = new HtmlStepBuilder();
        htmlBuilder.addLatexRow(table.getDefault(app.getLocalization()));
        htmlBuilder.printReport("table.html");
    }

    @Test
    public void weakGCDTest() {
        weakGCD("x*x", "x*1", "x");
        weakGCD("((4)(x)-1)(6)((x)^(2))", "-(((4)(x)-1))(13)(x)", "(((4)(x)-1))(x)");
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

    public void isPositive(String a, boolean pos) {
        Assert.assertEquals(convert(a).isPositive(), pos);
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
        Assert.assertEquals(c, StepHelper.GCD(convert(a), convert(b)).toString());
    }

    private StepExpression convert(String s) {
        return (StepExpression) StepNode.getStepTree(s, app.getKernel().getParser());
    }
}
