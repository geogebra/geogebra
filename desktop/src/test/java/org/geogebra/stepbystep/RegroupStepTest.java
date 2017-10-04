package org.geogebra.stepbystep;

import org.geogebra.commands.CommandsTest;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.steptree.SimplificationSteps;
import org.geogebra.common.kernel.stepbystep.steptree.StepNode;
import org.geogebra.common.main.App;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class RegroupStepTest {
	private static App app;

	@BeforeClass
	public static void setupApp() {
		app = CommandsTest.createApp();
	}

	private static HtmlStepBuilder htmlBuilder = new HtmlStepBuilder();
	private boolean needsHeading;
	private static int caseCounter = 0;

	@Test
	public void factorTest() {
		f("x^3 + 6 x^2 + 11 x + 6", "((x + 3))((x + 2))((x + 1))");
		f("3*x^2*(x+2) + 3*x*(x+2)^2", "(6)(x)((x + 2))((x + 1))");
		f("4(x+1)^2+(x+1)^4+1", "((((x + 1))^(2) + 2 + nroot(3, 2)))((((x + 1))^(2) + 2-nroot(3, 2)))");
		f("x^2+4x+1", "((x + 2 + nroot(3, 2)))((x + 2-nroot(3, 2)))");
		f("(x^2-3)^2+4(x^2-3)+4", "(((x + 1))^(2))(((x-1))^(2))");
		f("(x^2-3)^2-4(x^2-3)+4", "(((x + nroot(5, 2)))^(2))(((x-nroot(5, 2)))^(2))");
	}

	@Test
	public void regroupTest() {
		r("1/(sqrt(2)-1)+1/(sqrt(2)+1)-2sqrt(2)", "0");
		r("1/(sqrt(2)-1)", "(nroot(2, 2) + 1)");
		r("1/(2+sqrt(5))-1/(2-sqrt(5))-2sqrt(5)", "0");
		r("-(-x)", "x");
		r("(x)/((1-x))", "(x)/((1-x))");
		r("(x^2-3x+2)/(x-1)", "(x-2)");
		r("(x-1)/(x^2-3x+2)", "(1)/((x-2))");
		r("nroot(5, 2)*nroot(2, 2)", "nroot(10, 2)");
		r("nroot(2, 2)*nroot(3, 3)", "nroot(72, 6)");
		r("(x-y)/(sqrt(x)-sqrt(y))", "(nroot(x, 2) + nroot(y, 2))");
		r("2x+1+3x+2 = x+x+1+1", "((5)(x) + 3) = ((2)(x) + 2)");
		r("1/3+1/2", "(5)/(6)");
		r("nroot(1, 2)", "1");
		r("nroot(-1, 3)", "-1");
		r("-(-x)-3-(-(-x))+1", "-2");
		r("2/nroot(3, 2)", "((2)(nroot(3, 2)))/(3)");
		r("2/nroot(3, 3)", "((2)(nroot(9, 3)))/(3)");
		r("-(3+x)", "(-3-x)");
		r("(-x)^2+1", "((x)^(2) + 1)");
		r("x*sqrt(8)+sqrt(27)", "((x)(2)(nroot(2, 2)) + (3)(nroot(3, 2)))");
		r("sqrt(8)-sqrt(2)", "nroot(2, 2)");
		r("x*x*y/2*z*x/3*5*4", "((10)((x)^(3))(y)(z))/(3)");
		r("sqrt(x^2)+(sqrt(x))^2+nroot(x^3, 3)+nroot(x^2, 4)", "(|x| + (2)(x) + nroot(|x|, 2))");
		r("2^3+x^0+x^1+nroot(x, 1)+sqrt(16)", "((2)(x) + 13)");
		r("arcsin(1/2)", "(pi)/(6)");
		r("nroot(1/sqrt(8), 3)", "(nroot(2, 2))/(2)");
		r("(x+1)/x*10*(x+2)/(x+1)^2*1/5", "((2)((x + 2)))/((x)((x + 1)))");
	}

	@Test
	public void expandTest() {
		e("(3x+1)(4y+2)(5z+3)",
				"((60)(z)(y)(x) + (20)(z)(y) + (30)(z)(x) + (10)(z) + (36)(y)(x) + (12)(y) + (18)(x) + 6)");
		e("5(4y+2)", "((20)(y) + 10)");
		e("(4y+2)^2", "((16)((y)^(2)) + (16)(y) + 4)");
		e("(4y+2+z)^2", "((16)((y)^(2)) + 4 + (z)^(2) + (16)(y) + (4)(z) + (8)(y)(z))");
		e("(4y+2)^3", "((64)((y)^(3)) + (96)((y)^(2)) + (48)(y) + 8)");
	}

	public void r(String toRegroup, String expectedResult) {
		if (needsHeading) {
			Throwable t = new Throwable();
			htmlBuilder.addHeading(t.getStackTrace()[1].getMethodName(), 1);
			needsHeading = false;
		}
		htmlBuilder.addHeading("Testcase " + (caseCounter++), 2);

		SolutionBuilder sb = new SolutionBuilder(app.getLocalization());
		StepNode sn = StepNode.getStepTree(toRegroup, app.getKernel().getParser());
		String result = sn.regroup(sb).toString();

		SolutionStep steps = sb.getSteps();
		steps.getListOfSteps(htmlBuilder);

		Assert.assertEquals(expectedResult, result);
	}

	public void e(String toExpand, String expectedResult) {
		if (needsHeading) {
			Throwable t = new Throwable();
			htmlBuilder.addHeading(t.getStackTrace()[1].getMethodName(), 1);
			needsHeading = false;
		}
		htmlBuilder.addHeading("Testcase " + (caseCounter++), 2);

		SolutionBuilder sb = new SolutionBuilder(app.getLocalization());
		StepNode sn = StepNode.getStepTree(toExpand, app.getKernel().getParser());
		String result = sn.expand(sb).toString();

		SolutionStep steps = sb.getSteps();
		steps.getListOfSteps(htmlBuilder);

		Assert.assertEquals(expectedResult, result);
	}

	public void f(String toFactor, String expectedResult) {
		if (needsHeading) {
			Throwable t = new Throwable();
			htmlBuilder.addHeading(t.getStackTrace()[1].getMethodName(), 1);
			needsHeading = false;
		}
		htmlBuilder.addHeading("Testcase " + (caseCounter++), 2);

		SolutionBuilder sb = new SolutionBuilder(app.getLocalization());
		StepNode sn = StepNode.getStepTree(toFactor, app.getKernel().getParser());
		String result = SimplificationSteps.DEFAULT_FACTOR.apply(sn, sb, new int[] { 1 }).toString();

		SolutionStep steps = sb.getSteps();
		steps.getListOfSteps(htmlBuilder);

		Assert.assertEquals(expectedResult, result);
	}

	@Before
	public void resetHeading() {
		needsHeading = true;
	}

	@AfterClass
	public static void printHtml() {
		htmlBuilder.printReport("regroup.html");
	}
}
