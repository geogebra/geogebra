package org.geogebra.common.kernel.commands;

import static org.geogebra.test.TestStringUtil.unicode;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageManagerD;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class CommandsUsingCASTest extends AlgebraTest {

	@Before
	public void resetSyntaxes() {
		CommandsTest.resetSyntaxCounter();
		app.getKernel().clearConstruction(true);
		app.setActiveView(App.VIEW_EUCLIDIAN);
	}

	@After
	public void checkSyntaxes() {
		CommandsTest.checkSyntaxesStatic();
	}

	@Test
	public void conditionalDerivativeTest() {
		t("f(x)=If[x>0,x^2]", "If[x > 0, x^(2)]");
		t("f'(x)=Derivative[f]", "If[x > 0, (2 * x)]");
		t("f'(3)", "6");
		t("g(x,y)=If[x+y>0,x^2+x*y]", "If[x + y > 0, x^(2) + (x * y)]");
		t("h(x,y)=Derivative[g, x]", "If[x + y > 0, (2 * x) + y]");
		t("h(1,3)", "5");
	}

	@Test
	public void cmdSimplify() {
		t("Simplify[ sin^2(x)+cos^2(x) ]", "1");
		t("Simplify[ 2/sqrt(2) ]", "sqrt(2)");
		t("Simplify[\"x+-x--x\"]", "x " + Unicode.MINUS + " x + x");
		t("sa=1", "1");
		t("sb=2", "2");
		t("sc=sa+sb", "3");
		t("Simplify(sc*x^2)", "(3 * x^(2))");
	}

	@Test
	public void cmdIntegral() {
		t("Integral[ sin(x) ]", "(-cos(x))");
		t("Integral[ x^2, x ]", "(1 / 3 * x^(3))");
		t("Integral[ sin(x),0,pi ]", "2");
		t("Integral[ abs(x),-2,2 ]", "4");
		t("Integral[ sin(x), 0, 100, false ]", "NaN");

		// https://help.geogebra.org/topic/integralbetween-yields-undefined-result
		t("intFunf:=x - floor(2x) / 2", "x - floor((2 * x)) / 2");
		t("intFunq:=2floor(2x - 2floor(x)) - 1",
				"(2 * floor((2 * x) - (2 * floor(x)))) - 1");
		t("intFung:=sqrt(-x^2 + 1)", "sqrt((-x^(2)) + 1)");
		t("intFung1:=3 / 2 floor(2x - 2floor(x)) - 1",
				"(3 / 2 * floor((2 * x) - (2 * floor(x)))) - 1");
		t("intFunr:=-sqrt(3) floor(x + 1 / 2)",
				"((-sqrt(3)) * floor(x + 1 / 2))");
		t("intFunh:=intFunf(x) + intFung1(x)",
				"x - floor((2 * x)) / 2 + (3 / 2 * floor((2 * x) - (2 * floor(x)))) - 1");
		t("intFunp:=intFunq(x) intFung(intFunh(x)) + intFunr(x) + sqrt(3)",
				"(((2 * floor((2 * x) - (2 * floor(x)))) - 1) * sqrt((-(x - floor((2 * x)) / 2 + (3 / 2 * floor((2 * x) - (2 * floor(x)))) - 1)^(2)) + 1)) + ((-sqrt(3)) * floor(x + 1 / 2)) + sqrt(3)");
		// can work if native Giac is forced in AlgoIntegralDefinite
		// but causes problems for other cases
		// t("NIntegral(intFunp, 1 / 2, 2)", "-0.5589329791322");
		// t("Integral(intFunp, 1 / 2, 2)", "-0.5589329791322");
		t("Integral(1/(x^2+1),-inf,inf)", "3.14159265359");
		t("Integral(1/(x^2+1),0,inf)", "1.570796326795");
		t("Integral(1/(x^2+1),-inf,0)", "1.570796326795");
	}

	@Test
	public void cmdIntegralInfinite() {
		t("f=Normal(50,3,x,false)",
				"exp(((-(x - 50)^(2))) / ((3^(2) * 2))) / ((abs(3) * sqrt((3.141592653589793 * 2))))");
		tRound("norm:=Integral[f,-inf,50 ]", "0.5");
		tRound("nnorm:=Integral[f,50,inf ]", "0.5");
	}

	@Test
	public void cmdNSolve() {
		tRound("NSolve[ x^2=3 ]", "{x = -1.73205, x = 1.73205}");
		t("NSolve[ x^2=-1 ]", "{}");
		tRound("NSolve[ erf(x)=0.5 ]", "{x = 0.47694}");
		tRound("NSolve[ sin(x)=0 ]", "{x = 0" + Unicode.DEGREE_CHAR
				+ ", x = 180" + Unicode.DEGREE_CHAR + "}");
		t("NSolve[ {sin(x)=0, x=y} ]", "{{x = 0*" + Unicode.DEGREE_CHAR
				+ ", y = 0*" + Unicode.DEGREE_CHAR + "}}");

		tRound("NSolve( -0.083333333333333x^2 + 1.333333333333333x - 1.25=3.5)",
				"{x = 5.35425, x = 10.64575}");
	}

	@Test
	public void cmdDerivative() {
		t("Derivative[ Curve[sin(t),cos(t),t,0,1] ]",
				"(cos(t), (sin(t) * (-1)))");
		t("Derivative[ Curve[sin(t),cos(t),t,0,1],2 ]",
				"((sin(t) * (-1)), (-cos(t)))");
		t("Derivative[ sin(x) ]", "cos(x)");
		t("Derivative[ cos(x), 3 ]", "sin(x)");
		t("Derivative[ cos(x), x ]", "(-sin(x))");
		t("Derivative[ cos(x), x, 3 ]", "sin(x)");
		t("Derivative[ x^4/3 ]", "(4 / 3 * x^(3))");
		t("Derivative[exp(x)]", "\u212F^(x)");
		t("Derivative[(x+1)exp(-x)]", "((-x) * \u212F^((-x)))");
		t("fderiv:y=exp(x)", "exp(x)");
		t("fderiv'(x)", "\u212F^(x)");
		t("fderiv2:y=(x+1)exp(-x)", "((x + 1) * exp((-x)))");
		t("fderiv2'(x)", "((-x) * \u212F^((-x)))");
	}

	@Test
	public void cmdAsymptote() {
		t("Asymptote[ x*y=1 ]", new String[] { "x = 0", "y = 0" });
		t("Asymptote[ 1/x ]", "{y = 0, x = 0}");
		t("Asymptote[ 1/x^3 ]", "{y = 0, x = 0}");
		t("Asymptote[ 1/x^4 ]", "{y = 0, x = 0}");
		t("Asymptote[ x^2*y^2=1 ]", "{x = 0, y = 0}");
		t("Asymptote[ 2^x/(3^x-2^x) ]", "{y = 0, y = -1, x = 0}");
		t("Asymptote[ (x-1)/(x-1) ]", "{y = 1}");
		t("Asymptote[ (x-1)^3/(x-1) ]", "{}");
		t("Asymptote[ x+sin(x) ]", "{}");
		t("Asymptote[ (9 - 3^x) / (4 + 6^x) ]", "{y = 0, y = 2.25}");
		t("Asymptote[ 2x^2/(x^1-16)+1/(1+x^2)+1/(2+x^2) ]",
				"{y = 2x + 32, x = 16}");
		t("Asymptote[ 3 (x - 1) (x + 1) (x - 4) (x + 4) / (4 (x + 4) (2 + x) (4 - x) (x + 1)) ]",
				"{y = -0.75, x = -2}");
		t("Asymptote[ (2 - x) / ((x - 2) (x - 4)) ]", "{y = 0, x = 4}");

		t("Asymptote[ 5+exp(-x^2) ]", "{y = 5}");
		t("Asymptote[ (x^3-9+4)/(2x^3+6x+7) ]", "{y = 0.5}");
		tRound("Asymptote[ 3 atan(2x) ]", "{y = 4.71239, y = -4.71239}");
		tRound("Asymptote[ (9-4x^2)/(1+5x+5x^2)]",
				"{y = -0.8, x = -0.72361, x = -0.27639}");
		t("Asymptote[ exp(-3x)+4exp(-x) ]", "{y = 0}");
		t("Asymptote[ (7+x)/(x^2-9) ]", "{y = 0, x = -3, x = 3}");
		t("Asymptote[ sin(1/x)^2 ]", "{y = 0}");
		t("Asymptote[ atan(1/(8x)) ]", "{y = 0}");
		tRound("Asymptote[ -atan(8x) ]", "{y = -1.5708, y = 1.5708}");
		t("Asymptote[ 4exp(2/x) ]", "{y = 4}");
		t("Asymptote[ 1/ln(2x+4)]", "{y = 0, x = -1.5}");
		tRound("Asymptote[ sqrt(2x^2+1)/(3x-5)]",
				"{y = 0.4714, y = -0.4714, x = 1.66667}");
		t("Asymptote[ sqrt(x^2+6x) - x ]", "{y = 3, y = -2x - 3}");

		tRound("Asymptote[ x(x-3)(x-8)(x-3)(x+4)/(7(x+1)(1+x)(3-x)(x-8)) ]",
				"{y = -0.14286x + 0.14286, x = -1}");
		tRound("Asymptote[ x+atan(x) ]", "{y = x + 1.5708, y = x - 1.5708}");
		tRound("Asymptote[ (3x - 2) / sqrt(2x^2 + 1) ]",
				"{y = 2.12132, y = -2.12132}");
		// for this one we don't get the right vertical asymptote, at least
		// ignore the fake one
		t("IndexOf(x=7,Asymptote[ (-1+(x-7)*2^x/(3^x-2^x)+1)/(x-7) ])", "NaN");
		t("Asymptote[ ln(x^2) ]", "{x = 0}");
		t("Asymptote[ ln(abs(x^2-4)) ]", "{x = -2, x = 2}");
		t("Asymptote[ log(3,x-2) ]", "{x = 2}");
		t("Asymptote[ log(3,x-2)/(x-4) ]", "{y = 0, x = 2, x = 4}");
		t("Asymptote[ log(3,x-2)/(x-2) ]", "{y = 0, x = 2}");
		// OK
		tRound("Asymptote[ sqrt((2x - 3) / (2x^2 - 3)) ]",
				"{y = 0, x = -1.22474, x = 1.22474}");

		// these ones are tricky (problems with domain)
		// https://help.geogebra.org/topic/asymptotes-incorrectly-computed
		// tRound("Asymptote[ sqrt(2x - 3) / sqrt(2x^2 - 3) ]",
		// "{y = 0}");
		// tRound("Asymptote[ sqrt(3x^2 - 2) / sqrt(2x + 1) ]", "{}");
		// tRound("Asymptote[ sqrt((3x^2 - 2) / (2x + 1)) ]", "{x = -0.5}");

	}

	/** Test for MOB-1667 */
	@Test
	public void cmdSolveSystem() {
		t("a:abs(x)/9+abs(y)/4=1", "abs(x) / 9 + abs(y) / 4 = 1");
		t("f:y=2x", "y = 2x");
		t("Solve[ {a,f} ]",
				"{{x = 18 / 11, y = 36 / 11}, {x = -18 / 11, y = -36 / 11}}");
	}

	@Test
	public void cmdSolutions() {
		tRound("Solutions[ x^2=3 ]", "{-1.73205, 1.73205}");
		t("Solutions[ 5x=4 ]", "{4 / 5}");
		tRound("Solutions[ sin(x)=1/2 ]", "{30" + Unicode.DEGREE_CHAR + ", 150"
				+ Unicode.DEGREE_CHAR + "}");
	}

	@Test
	public void cmdNSolutions() {
		tRound("NSolutions[ x^2=3 ]", "{-1.73205, 1.73205}");
		t("NSolutions[ 5x=4 ]", "{0.8}");
		tRound("NSolutions[ sin(x)=1/2 ]", "{30" + Unicode.DEGREE_CHAR + ", 150"
				+ Unicode.DEGREE_CHAR + "}");
	}

	@Test
	public void cmdImplicitDerivative() {
		t("ImplicitDerivative[x^2+y^2]", "((-x)) / y");
	}

	@Test
	public void testIntersectCurves() {
		t("Intersect[Curve[t, t^3 - t, t, -2, 2], Curve[t, t, t, -4, 4]]",
				new String[] { "(0, 0)",
						"(1.4142135623730951, 1.4142135623730951)",
						"(-1.4142135623730951, -1.4142135623730951)" });
		t("Intersect[Curve[t, t^3 - t, t, -2, 2], Curve[t, t, t, -4, 4], 1, 1]",
				"(1.4142135623730951, 1.4142135623730956)");
		tRound("Intersect[sin(x), cos(x), 0, 2pi]",
				new String[] { "(0.7854, 0.70711)", "(3.92699, -0.70711)" });
	}

	@Test
	public void cmdSolve() {
		runSolveTests();
		app.getKernel().clearConstruction(true);
		app.setActiveView(App.VIEW_EUCLIDIAN3D);
		app.getEuclidianView3D();
		t("eq: x^2=6", unicode("x^2 + 0z^2 = 6"));
		t("Solve[ eq ]", "{x = (-sqrt(6)), x = sqrt(6)}");
		t("Solve({84.36=x*y^3,126.56=x*y^4})",
				"{{x = 19783645390161 / 791861873600, y = 3164 / 2109}}");
		runSolveTests();
	}

	@Test
	public void cmdCASLoaded() {
		t("CASLoaded[]", "true");
	}

	private static void runSolveTests() {
		t("ss=Solve[ x^2=3 ]", "{x = (-sqrt(3)), x = sqrt(3)}");
		Assert.assertTrue(AlgebraItem.isSymbolicDiffers(get("ss")));
		t("sm=Solve[ {x+y=1,x-y=0} ]", "{{x = 1 / 2, y = 1 / 2}}");
		Assert.assertTrue(AlgebraItem.isSymbolicDiffers(get("sm")));
		t("Solve[ x^2=-1 ]", "{}");
		t("Solve[ x=x ]", "{x = x}");
		t("Solve[ erf(x)=0.5 ]", "?");
		tdeg("r=Solve[ sin(x)=0 ]", "{x = 0*deg}");
		tdeg("r2=Solve[ {sin(x)=0, x=y} ]", "{{x = 0*deg, y = 0*deg}}");
		tdeg("r=Solve[ cos(x)=1/sqrt(2) ]", "{x = (-45*deg), x = 45*deg}");
		tdeg("r2=Solve[ {cos(x)=1/2, x=y} ]",
				"{{x = 60*deg, y = 60*deg}, {x = (-60*deg), y = (-60*deg)}}");
	}

	@Test
	public void imgCorner() {
		// TODO not really CAS
		GeoImage img = new GeoImage(app.getKernel().getConstruction());
		String fn = ((ImageManagerD) app.getImageManager())
				.createImage(GuiResourcesD.BAR_GRAPH, app);
		img.setImageFileName(fn);
		app.getImageManager().setCornersFromSelection(img, app);
		img.setLabel("picT");
		img.getCorner(0).setCoords(0, 0, 1);
		img.getCorner(1).setCoords(10, 0, 1);
		img.getCorner(1).updateCascade();
		tRound("Corner(picT,1)", "(0, 0)");
		tRound("Corner(picT,2)", "(10, 0)");
		tRound("Corner(picT,3)", "(10, 10)");
		tRound("Corner(picT,4)", "(0, 10)");
		EuclidianView view = app.getActiveEuclidianView();
		view.setCoordSystem(view.getXZero(), view.getYZero(), view.getXscale(),
				view.getYscale() * 2);
		tRound("Corner(picT,1)", "(0, 0)");
		tRound("Corner(picT,2)", "(10, 0)");
		tRound("Corner(picT,3)", "(10, 10)");
		tRound("Corner(picT,4)", "(0, 10)");
	}

	@Test
	public void cmdShowSteps() {
		AlgebraTestHelper.shouldFail("ShowSteps(ConstructionStep())",
				"Illegal argument: ConstructionStep", app);
		t("First(ShowSteps(Solve(x^2=-1/4)))", "{\"x^{2} = \\frac{-1}{4}\"}");
		t("First(ShowSteps(Solve(x^2=1/4)))",
				"{\"x = \\pm \\sqrt{\\frac{1}{4}}\"}");
		t("IndexOf(Text(\"x = \\pm \\frac{1}{2}\"),ShowSteps(Solve(x^2=1/4)))>0",
				"true");
		t("eq:x*x=1/4", unicode("(-x - 0.5) (-x + 0.5) = 0"));
		t("IndexOf(Text(\"x = \\pm \\frac{1}{2}\"),ShowSteps(Solve(eq)))>0",
				"true");
	}

	@Test
	public void testDerivativeDegrees() {
		deg("Derivative(sin(30)*x+sin(x))", "1 / 2 (2cos(x) + 1)");
	}

	@Test
	public void symbolicFractionsCAS() {
		frac("a=2/3-1/3", "1 / 3");
		frac("Simplify(x/3/a)", "x");
		frac("Simplify(x^a)", "cbrt(x)");
		frac("Simplify(a!)", "gamma(1 / 3) / 3");
	}

	private void frac(String def, String expect) {
		EvalInfo evalInfo = new EvalInfo(true, true).withFractions(true);
		checkWithEvalInfo(def, expect, evalInfo);
	}

	private static void deg(String def, String expect) {
		EvalInfo evalInfo = new EvalInfo(true, true).addDegree(true);
		checkWithEvalInfo(def, expect, evalInfo);
	}

	private static void checkWithEvalInfo(String def, String expect,
			EvalInfo evalInfo) {
		GeoElementND[] geo = ap.processAlgebraCommandNoExceptionHandling(def,
				false, TestErrorHandler.INSTANCE,
				evalInfo, null);
		String res = geo[0].toValueString(StringTemplate.editTemplate);
		Assert.assertEquals(expect, res);
	}

	private static void tdeg(String string, String string2) {
		t(string, string2.replace("deg", Unicode.DEGREE_STRING));
	}

	private static GeoElement get(String label) {
		return app.getKernel().lookupLabel(label);
	}
}
