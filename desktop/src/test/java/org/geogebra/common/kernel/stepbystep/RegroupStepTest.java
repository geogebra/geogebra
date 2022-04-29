package org.geogebra.common.kernel.stepbystep;

import org.geogebra.common.kernel.commands.AlgebraTest;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStep;
import org.geogebra.common.kernel.stepbystep.steptree.StepExpression;
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
		app = AlgebraTest.createApp();
		htmlBuilder = new HtmlStepBuilder(app.getLocalization());
	}

	private static HtmlStepBuilder htmlBuilder;
	private boolean needsHeading;
	private static int caseCounter = 0;

	@Test
	public void trigonometricSimplifications() {
		r("cos(-(pi/2))", "0");
		r("cos(-(pi))", "-1");
		r("sin((2 * pi)/3)", "(nroot(3, 2))/(2)");
		r("sin((128 * pi)/3)", "(nroot(3, 2))/(2)");
		r("sin((131 * pi)/3)", "-(nroot(3, 2))/(2)");
		r("sin(2*pi)", "0");
		r("sin((5*pi)/2 + x)", "cos(x)");
		r("sin(2*pi*arbconst(1))", "0");
		r("sin(pi*arbconst(1))", "sin((pi)(k1))");
		r("sin(2*pi*y)", "sin((2)(pi)(y))");
		r("sin(9 * pi / 4)", "(nroot(2, 2))/(2)");
		r("sin(5 * pi / 4)", "-(nroot(2, 2))/(2)");
		r("sin(13 * pi / 4)", "-(nroot(2, 2))/(2)");
		r("sin(51 * pi)", "0");
		r("sin(2 * pi * x + 2 * pi * arbconst(1) + 4 * pi * arbconst(2))",
				"sin((2)(pi)(x))");
		r("tan(2 * pi * x + 2 * pi * arbconst(1) + 4 * pi * arbconst(2))",
				"tan((2)(pi)(x))");
	}

	@Test
	public void trickyTests() {
		r("nroot(2^9, 6)/4", "(nroot(2, 2))/(2)");
		r("3^(-2)", "(1)/(9)");
		r("x/(2y-y-y) + 3", "undefined");
		r("nroot(-2, 2)", "undefined");
		r("nroot(-4, 2)", "undefined");
		r("nroot(-x, 2)", "nroot(-x, 2)");
		r("3x*5x", "(15)((x)^(2))");
		r("3x^2*3^(-1)x^(-1)y", "(x)(y)");
		r("9^2*3*(3^(-1 - 2))", "9");
		f("x*z+y*z", "(z)((x + y))");
		f("y*z-x*z", "(z)((y-x))");
		r("(x z + y z) / (y z - x z)", "((x + y))/((y-x))");
		r("(-24x^3) / (-16x^2 y)", "((3)(x))/((2)(y))");
		r("(0.6x^2 y z^3) / (0.3x^5 y^2 z)", "((2)((z)^(2)))/(((x)^(3))(y))");
		r("(0.6*x^2*y*z^3) / (0.31*x^5*y^2*z*8.21)", "((0.235747122)((z)^(2)))/(((x)^(3))(y))");
		r("(5x (x^2 - y^2)) / (15 (y - x))", "-((x)((x + y)))/(3)");
		r("(5x (x - y)) / (15 (y - x))", "-(x)/(3)");
	}

	@Test
	public void factorTest() {
		f("(27)((x)^(5)) + (81)((x)^(4)) + (90)((x)^(3)) + (46)((x)^(2)) + (11)(x) + 1",
				"(((x + 1))^(2))((((3)(x) + 1))^(3))");
		f("((3)(x) + 1-(2)(nroot(((27)((x)^(5)) + (81)((x)^(4)) + (90)((x)^(3)) + (46)((x)^(2)) + (11)(x) + 1), 6)) + (nroot((x + 1), 3))^(2))",
				"((3)(x) + 1-(2)(nroot((((x + 1))^(2))((((3)(x) + 1))^(3)), 6)) + (nroot((x + 1), 3))^(2))");
		f("x^5-x^6", "((x)^(5))((1-x))");
		f("y^2*x + y*x", "(y)(x)((y + 1))");
		f("x^2+x", "(x)((x + 1))");
		f("2^(k+1)+2^k", "((2)^(k))(3)");
		f("x^3 + 6 x^2 + 11 x + 6", "((x + 3))((x + 2))((x + 1))");
		f("12x+6y+9z", "(3)(((4)(x) + (2)(y) + (3)(z)))");
		f("3*x^2*(x+2) + 3*x*(x+2)^2", "(6)(x)((x + 2))((x + 1))");
		f("a^3 + b^3", "((a + b))(((a)^(2)-(a)(b) + (b)^(2)))");
		f("a^3 - b^3", "((a-b))(((a)^(2) + (a)(b) + (b)^(2)))");
		f("-a^3 + b^3", "((b-a))(((b)^(2) + (b)(a) + (a)^(2)))");
		f("-a^3 - b^3", "-((a + b))(((a)^(2)-(a)(b) + (b)^(2)))");
		f("24x^3 - 58x^2 + 37x - 6", "(((4)(x)-1))(((3)(x)-2))(((2)(x)-3))");
		f("x^3 + 3 x^2 - 7 x + 3", "((x-1))((x + 2 + nroot(7, 2)))((x + 2-nroot(7, 2)))");
		f("4(x+1)^2+(x+1)^4+1", "((((x + 1))^(2) + 2 + nroot(3, 2)))((((x + 1))^(2) + 2-nroot(3, 2)))");
		f("x^2+4x+1", "((x + 2 + nroot(3, 2)))((x + 2-nroot(3, 2)))");
		f("(x^2-3)^2+4(x^2-3)+4", "(((x + 1))((x-1)))^(2)");
		f("(x^2-3)^2-4(x^2-3)+4", "(((x + nroot(5, 2)))((x-nroot(5, 2))))^(2)");
		f("a^3+3*a^2*b+3*a*b^2+b^3", "((a + b))^(3)");
		f("a^3-3*a^2*b+3*a*b^2-+b^3", "((a-b))^(3)");
	}

	@Test
	public void fractionTest() {
		r("x^2+x-(1)/(3)", "((x)^(2) + x-(1)/(3))");
		r("((2)(pi)-((5)(pi))/(3))", "(pi)/(3)");
		r("(((5)(pi))/(4)-pi)", "(pi)/(4)");
		r("(5*pi)/2 + x", "(((5)(pi))/(2) + x)"); // DON'T expand!
		r("(5*pi)/2 + pi", "((7)(pi))/(2)"); // DO expand!
		r("(5*pi)/2 + pi + 3", "(3 + ((7)(pi))/(2))"); // DO expand pi, but not 3!!
		r("x+1/2", "(x + (1)/(2))"); // DON'T expand!
		r("x+x/2", "((3)(x))/(2)"); // DO expand!
		r("(2^(2k)+2^(k+1))/((2)^(k))", "((2)^(k) + 2)");
		e("((1)/((1-((11 + nroot(13, 2)))/(9))) " +
				"+ ((22 + (2)(nroot(13, 2))))/((18-((9)((11 + nroot(13, 2))))/(9))))", "7");
		r("3/2+4/2", "(7)/(2)");
		r("1/3+1/2", "(5)/(6)");
		r("1/2-1/3", "(1)/(6)");
		r("1/3-1/2", "-(1)/(6)");
		r("(x^2-3x+2)/(x-1)", "(x-2)");
		r("(x-1)/(x^2-3x+2)", "(1)/((x-2))");
		// r("(x^2-3x+2)/(x-1) + (x-1)/(x^2-3x+2)", "((((x-2))(x)-((x-2))(2) + 1))/((x-2))");
		r("1/(sqrt(2)-1)+1/(sqrt(2)+1)-2sqrt(2)", "0");
		r("1/(sqrt(2)-1)", "(nroot(2, 2) + 1)");
		r("1/(2+sqrt(5))-1/(2-sqrt(5))-2sqrt(5)", "0");
		r("x*x*y/2*z*x/3*5*4", "((10)((x)^(3))(y)(z))/(3)");
		r("(x+1)/x*10*(x+2)/(x+1)^2*1/5", "(((2)(x) + 4))/((x)((x + 1)))");
	}

	@Test
	public void regroupTest() {
		r("nroot((4)((x)^(2))(y), 2)", "(2)(|x|)(nroot(y, 2))");
		r("nroot((4)((x)^(2)), 2)", "(2)(|x|)");
		r("2 - 2", "0");
		r("(-(nroot(7, 2))^(3) + (7)(nroot(7, 2)))", "0");
		r("((nroot(2, 2))^(3))/(8)", "(nroot(2, 2))/(4)");
		r("nroot(81x^(3)y^(4), 3)", "(3)(x)(y)(nroot((3)(y), 3))");
		r("sqrt(x^3)", "(|x|)(nroot(x, 2))");
		r("-((-3)*(-5)*(-6))", "90");
		r("(-3)*(5)*(6)", "-90");
		r("(3)*(-5)*(6)", "-90");
		r("-(-x)", "x");
		r("nroot(5, 2)*nroot(2, 2)", "nroot(10, 2)");
		r("nroot(2, 2)*nroot(3, 3)", "nroot(72, 6)");
		r("(x-y)/(sqrt(x)-sqrt(y))", "(nroot(x, 2) + nroot(y, 2))");
		r("nroot(1, 2)", "1");
		r("nroot(-1, 3)", "-1");
		r("-(-x)-3-(-(-x))+1", "-2");
		r("2/nroot(3, 2)", "((2)(nroot(3, 2)))/(3)");
		r("2/nroot(3, 3)", "((2)(nroot(9, 3)))/(3)");
		r("-(3+x)", "(-3-x)");
		r("(-x)^2+1", "((x)^(2) + 1)");
		r("x*sqrt(8)+sqrt(27)", "((x)(2)(nroot(2, 2)) + (3)(nroot(3, 2)))");
		r("sqrt(8)-sqrt(2)", "nroot(2, 2)");
		r("sqrt(x^2)+(sqrt(x))^2+nroot(x^3, 3)+nroot(x^2, 4)", "(|x| + (2)(x) + nroot(|x|, 2))");
		r("2^3+x^0+x^1+nroot(x, 1)+sqrt(16)", "((2)(x) + 13)");
		r("arcsin(1/2)", "(pi)/(6)");
		r("nroot(1/sqrt(8), 3)", "(nroot(2, 2))/(2)");
	}

	@Test
	public void expandTest() {
		e("(3x+1)(4y+2)(5z+3)",
				"((60)(x)(y)(z) + (36)(x)(y) + (30)(x)(z) + (18)(x) + (20)(y)(z) + (12)(y) + (10)(z) + 6)");
		e("5(4y+2)", "((20)(y) + 10)");
		e("(x+y)*t*v", "((x)(t)(v) + (y)(t)(v))");
		e("3(x+y)4(z+t)", "((12)(x)(z) + (12)(x)(t) + (12)(y)(z) + (12)(y)(t))");
		e("3*(x+y)*v*(z+t)", "((3)(x)(v)(z) + (3)(x)(v)(t) + (3)(y)(v)(z) + (3)(y)(v)(t))");
		e("(4y+2)^2", "((16)((y)^(2)) + (16)(y) + 4)");
		e("(4y+2+z)^2", "((16)((y)^(2)) + 4 + (z)^(2) + (16)(y) + (4)(z) + (8)(y)(z))");
		e("(4y+2)^3", "((64)((y)^(3)) + (96)((y)^(2)) + (48)(y) + 8)");
	}

	@Test
	public void exponentialOfLogarithm() {
		r("2^log(2,x)", "x");
		r("a^log(a,x)", "x");
		r("e^ln(x)", "x");
	}

	@Test
	public void decimalFractionTest() {
		r("0.125 + 0.25", "0.375");
		r("1/2 + 0.25", "(3)/(4)");
		r("1/3 + 0.5 + 0.25", "(13)/(12)");
		r("sqrt(2) + 1/3 + 0.2445", "(nroot(2, 2) + (3467)/(6000))");
		r("sqrt(2) + 1/3 + 0.24454", "1.992086896");
		r("sqrt(2) + 0.2445", "1.658713562");
		r("sqrt(2) + 0.24454", "1.658753562");
		r("sqrt(2*x) + 0.1241 + sqrt(3*x) + nroot(5, 3)", "((3.14626437)(nroot(x, 2)) + 1.834075947)");
		r("(3*x)/(0.24) + (9*x)/(0.3325) + 0.4324*x", "(40.000069173)(x)");
		r("(3.14)/(2.1*x) + (x)/(3.14)", "((1.495238095)/(x) + (0.318471338)(x))");
		r("(2*x)/3 + 0.15", "(((40)(x) + 9))/(60)");
		r("(2*x)/3 + 0.15161", "((0.666666667)(x) + 0.15161)");
		r("(2*x)/3.14 + 0.15", "((0.636942675)(x) + 0.15)");
		r("pi + 1/3 + 1.41", "(pi + (523)/(300))");
		r("pi + 1.41", "4.551592654");
	}

	public void r(String toRegroup, String expectedResult) {
		if (needsHeading) {
			Throwable t = new Throwable();
			htmlBuilder.addHeading(t.getStackTrace()[1].getMethodName(), 1);
			needsHeading = false;
		}
		htmlBuilder.addHeading("Testcase " + (caseCounter++), 2);

		SolutionBuilder sb = new SolutionBuilder();
		StepExpression sn = (StepExpression) StepNode.getStepTree(toRegroup, app.getKernel().getParser());
		String result = sn.regroupOutput(sb).toString();

		SolutionStep steps = sb.getSteps();
		htmlBuilder.buildStepGui(steps);

		Assert.assertEquals(expectedResult, result);
	}

	public void e(String toExpand, String expectedResult) {
		if (needsHeading) {
			Throwable t = new Throwable();
			htmlBuilder.addHeading(t.getStackTrace()[1].getMethodName(), 1);
			needsHeading = false;
		}
		htmlBuilder.addHeading("Testcase " + (caseCounter++), 2);

		SolutionBuilder sb = new SolutionBuilder();
		StepExpression sn = (StepExpression) StepNode.getStepTree(toExpand, app.getKernel().getParser());
		String result = sn.expandOutput(sb).toString();

		SolutionStep steps = sb.getSteps();
		htmlBuilder.buildStepGui(steps);

		Assert.assertEquals(expectedResult, result);
	}

	public void f(String toFactor, String expectedResult) {
		if (needsHeading) {
			Throwable t = new Throwable();
			htmlBuilder.addHeading(t.getStackTrace()[1].getMethodName(), 1);
			needsHeading = false;
		}
		htmlBuilder.addHeading("Testcase " + (caseCounter++), 2);

		SolutionBuilder sb = new SolutionBuilder();
		StepExpression sn = (StepExpression) StepNode.getStepTree(toFactor, app.getKernel().getParser());
		String result = sn.factorOutput(sb).toString();

		SolutionStep steps = sb.getSteps();
		htmlBuilder.buildStepGui(steps);

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
