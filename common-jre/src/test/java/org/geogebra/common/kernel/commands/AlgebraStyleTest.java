package org.geogebra.common.kernel.commands;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.SuggestionRootExtremum;
import org.geogebra.common.gui.view.algebra.SuggestionSolve;
import org.geogebra.common.gui.view.algebra.SuggestionStatistics;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.common.util.StringUtil;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.TestStringUtil;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class AlgebraStyleTest extends Assert {
	static AppCommon app;
	static AlgebraProcessor ap;

	private static class ExpressionChecker {
		private final String def;

		private ExpressionChecker(String def) {
			this.def = def;
		}

		private ExpressionChecker checkEditAndVal(String expectDef) {
			checkVal(expectDef);
			return checkEdit(expectDef);
		}

		private ExpressionChecker checkVal(String expectVal) {
			return check(def, expectVal, StringTemplate.editTemplate, true);
		}

		private ExpressionChecker checkEdit(String expectVal) {
			return checkEdit(expectVal, expectVal);
		}

		private ExpressionChecker checkEdit(String expectVal, String expectEditor) {
			check(def, expectEditor.replace(" * ", "*"),
					StringTemplate.editorTemplate, false);
			return check(def, expectVal, StringTemplate.editTemplate, false);
		}

		private void checkGiac(String s) {
			check(def, s, StringTemplate.giacTemplate, true);
		}

		private ExpressionChecker check(String def, String expect, StringTemplate tpl,
										boolean val) {
			GeoElementND[] geo = ap.processAlgebraCommandNoExceptionHandling(def,
					false, TestErrorHandler.INSTANCE, new EvalInfo(true, true),
					null);
			String res = val ? geo[0].toValueString(tpl)
					: geo[0].toGeoElement().getLaTeXDescriptionRHS(false, tpl);
			Assert.assertEquals(expect.replace("%p", Unicode.PI_STRING), res);
			return this;
		}
	}

	private static void checkRows(String def, int rows) {
		checkRows(def, rows, new EvalInfo(true));
	}

	private static void checkRows(String def, int rows, EvalInfo info) {
		GeoElementND[] el = ap.processAlgebraCommandNoExceptionHandling(def,
				false, TestErrorHandler.INSTANCE, info, null);
		assertEquals(DescriptionMode.values()[rows],
				el[0].getDescriptionMode());
		el[0].toString(StringTemplate.defaultTemplate);
		assertEquals(DescriptionMode.values()[rows],
				el[0].getDescriptionMode());
	}

	private static String checkEquation(String def, int mode, String check) {
		GeoElementND[] el = ap.processAlgebraCommandNoExceptionHandling(def,
				false, TestErrorHandler.INSTANCE, false, null);
		((GeoConicND) el[0]).setToStringMode(mode);
		assertEquals(TestStringUtil.unicode(check),
				el[0].toValueString(StringTemplate.defaultTemplate));
		return el[0].getLabelSimple();
	}

	private static void checkEquationReload(String def, int mode,
			String check) {
		String label = checkEquation(def, mode, check);
		app.setXML(app.getXML(), true);
		GeoElement reloaded = app.getKernel().lookupLabel(label);
		assertEquals(TestStringUtil.unicode(check),
				reloaded.toValueString(StringTemplate.defaultTemplate));
	}

	/**
	 * Clear construction and reset settings.
	 */
	@Before
	public void resetSyntaxes() {
		app.getKernel().clearConstruction(true);
		AlgebraTestHelper.enableCAS(app, true);
		app.getKernel()
				.setAlgebraStyle(Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE);
	}

	/**
	 * Initialize app.
	 */
	@BeforeClass
	public static void setupApp() {
		app = new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
		app.setLanguage("en_US");
		ap = app.getKernel().getAlgebraProcessor();
		// make sure x=y is a line, not plane
		app.getGgbApi().setPerspective("1");
		// Setting the general timeout to 11 seconds. Feel free to change this.
		app.getKernel().getApplication().getSettings().getCasSettings()
				.setTimeoutMilliseconds(11000);
	}

	@Test
	public void twoRowsAlgebra() {
		checkRows("a=1", 1);
		checkRows("a+a", 2);
		checkRows("sqrt(x+a)", 2);
		checkRows("{a}", 2);
		checkRows("{x}", 1);
		checkRows("{x+a}", 2);
		checkRows("{{1}}", 1);
		checkRows("{{a}}", 2);
		checkRows("{{a}}+{{1}}", 2);
		checkRows("{x=y}", 1);
		checkRows("x=y", 2);
		EvalInfo graphingFlags = new EvalInfo(true).withUserEquation(true);
		checkRows("x=y", 1, graphingFlags);
		checkRows("{y=x}", 1);
		checkRows("Sequence[100]", 2);
		checkRows("Line((0,0),(0,1))", 2);
		checkRows("Circle((0,0),(0,1))", 2);
	}

	@Test
	public void twoRowsAlgebraGraphing() {
		AlgebraTestHelper.enableCAS(app, false);
		checkRows("Line((0,0),(0,1))", 2);
		checkRows("Circle((0,0),(0,1))", 2);
		checkRows("x=y", 1);
	}

	@Test
	public void twoRowsAlgebraGraphingDerivative() {
		AlgebraTestHelper.enableCAS(app, false);
		checkRows("f(x)=x^2", 1);
		checkRows("f'", 1);
	}

	@Test
	public void twoRowsAlgebraGraphingDerivativeArg() {
		AlgebraTestHelper.enableCAS(app, false);
		checkRows("f(x)=x^2", 1);
		checkRows("f'(x)", 1);
	}

	@Test
	public void checkEquationExplicit() {
		checkEquation("x^2+4*y^2=1", GeoConicND.EQUATION_EXPLICIT,
				"x^2 + 4y^2 = 1");
		checkEquation("x^2+4*y^2-y+x*y=x +x -1", GeoConicND.EQUATION_EXPLICIT,
				"x^2 + x y + 4y^2 - 2x - y = -1");
		checkEquation("-x^2=x +x -1", GeoConicND.EQUATION_EXPLICIT,
				"-x^2 - 2x = -1");
	}

	@Test
	public void checkEquationVertex() {
		// ellipse: fallback to explicit
		checkNonParabolaFallback(GeoConicND.EQUATION_VERTEX);
		// three actual parabolas
		checkEquation("-x^2=x +x -1+y", GeoConicND.EQUATION_VERTEX,
				"y = -(x + 1)^2 +2");
		checkEquation("x^2=x +x -1+y", GeoConicND.EQUATION_VERTEX,
				"y = (x - 1)^2");
		checkEquation("y^2=y +y -1+x", GeoConicND.EQUATION_VERTEX,
				"(x - 0) = (y - 1)^2");
	}

	@Test
	public void checkEquationSpecific() {
		// ellipse
		checkEquation("x^2+4*y^2=1", GeoConicND.EQUATION_SPECIFIC,
				"x^2 / 1 + y^2 / 0.25 = 1");
		// hyperbola
		checkEquation("x^2-4*y^2=2x+2y+1", GeoConicND.EQUATION_SPECIFIC,
				"(x - 1)^2 / 1.75 - (y + 0.25)^2 / 0.44 = 1");
		// double line
		checkEquation("-x^2=x +x -1", GeoConicND.EQUATION_SPECIFIC,
				"(-x - 2.41) (-x + 0.41) = 0");
		// parabolas
		checkEquation("-x^2-x=x -1+y", GeoConicND.EQUATION_SPECIFIC,
				"x^2 = -2x - y + 1");
		checkEquation("y^2=x +x -1+y", GeoConicND.EQUATION_SPECIFIC,
				"y^2 = 2x + y - 1");
		checkEquation("(x+y)^2=x +x -1+y", GeoConicND.EQUATION_SPECIFIC,
				"x^2 + 2x y + y^2 - 2x - y = -1");
	}

	@Test
	public void checkEquationConicform() {
		checkNonParabolaFallback(GeoConicND.EQUATION_CONICFORM);
		// parabolas
		checkEquation("-x^2-x=x -1+y", GeoConicND.EQUATION_CONICFORM,
				"-(y - 2) = (x + 1)^2");
		checkEquation("y^2=x +x -1+y", GeoConicND.EQUATION_CONICFORM,
				"2(x - 0.38) = (y - 0.5)^2");
		checkEquation("(x+y)^2=x +x -1+y", GeoConicND.EQUATION_CONICFORM,
				"x^2 + 2x y + y^2 - 2x - y = -1");
	}

	@Test
	public void checkEquationParametric() {
		// ellipse
		checkEquation("x^2+4*y^2=1", GeoConicND.EQUATION_PARAMETRIC,
				"X = (0, 0) + (cos(t), 0.5 sin(t))");
		// hyperbola
		checkEquation("x^2-4*y^2=2x+2y+1", GeoConicND.EQUATION_PARAMETRIC,
				"X = (1, -0.25) + (" + Unicode.PLUSMINUS
						+ " 1.32 cosh(t), 0.66 sinh(t))");
		// parallel lines
		checkEquation("-x^2=x +x -1", GeoConicND.EQUATION_PARAMETRIC,
				"X = (-1 " + Unicode.PLUSMINUS + " 1.41, 0, 0) + "
						+ Unicode.lambda + " (0, 1, 0)");
		// double line
		checkEquation("-x^2=x +x +1", GeoConicND.EQUATION_PARAMETRIC,
				"X = (-1, 0, 0) + " + Unicode.lambda + " (0, 1, 0)");
		// parabolas
		checkEquation("-x^2-x=x -1+y", GeoConicND.EQUATION_PARAMETRIC,
				"X = (-1, 2) + (-0.5 t, -0.25 t^2)");
		checkEquation("y^2=x +x -1+y", GeoConicND.EQUATION_PARAMETRIC,
				"X = (0.38, 0.5) + (0.5 t^2, t)");
		checkEquation("(x+y)^2=x +x -1+y", GeoConicND.EQUATION_PARAMETRIC,
				"X = (0.81, -0.06) + (0.06 t^2 + 0.13 t, -0.06 t^2 + 0.13 t)");
	}

	@Test
	public void checkEquationImplicit() {
		// ellipse
		checkEquation("x^2+4*y^2=1", GeoConicND.EQUATION_IMPLICIT,
				"x^2 + 4y^2 = 1");
		// hyperbola
		checkEquation("x^2-4*y^2=2x+2y+1", GeoConicND.EQUATION_IMPLICIT,
				"x^2 - 4y^2 - 2x - 2y = 1");
		// parallel lines
		checkEquation("-x^2=x +x -1", GeoConicND.EQUATION_IMPLICIT,
				"-x^2 - 2x = -1");
		// double line
		checkEquation("-x^2=x +x +1", GeoConicND.EQUATION_IMPLICIT,
				"-x^2 - 2x = 1");
		// parabolas
		checkEquation("-x^2-x=x -1+y", GeoConicND.EQUATION_IMPLICIT,
				"-x^2 - 2x - y = -1");
		checkEquation("y^2=x +x -1+y", GeoConicND.EQUATION_IMPLICIT,
				"y^2 - 2x - y = -1");
		checkEquation("(x+y)^2=x +x -1+y", GeoConicND.EQUATION_IMPLICIT,
				"x^2 + 2x y + y^2 - 2x - y = -1");
	}

	@Test
	public void checkEquationReload() {
		checkEquationReload("x^2+4*y^2=1", GeoConicND.EQUATION_EXPLICIT,
				"x^2 + 4y^2 = 1");
		checkEquationReload("-x^2=x +x -1+y", GeoConicND.EQUATION_VERTEX,
				"y = -(x + 1)^2 +2");
		checkEquationReload("x^2+4*y^2=1", GeoConicND.EQUATION_SPECIFIC,
				"x^2 / 1 + y^2 / 0.25 = 1");
		checkEquationReload("-x^2-x=x -1+y", GeoConicND.EQUATION_CONICFORM,
				"-(y - 2) = (x + 1)^2");
		checkEquation("x^2+4*y^2=1", GeoConicND.EQUATION_PARAMETRIC,
				"X = (0, 0) + (cos(t), 0.5 sin(t))");
		checkEquation("x^2+4*y^2=1", GeoConicND.EQUATION_IMPLICIT,
				"x^2 + 4y^2 = 1");
	}

	private static void checkNonParabolaFallback(int mode) {
		// ellipse
		checkEquation("x^2+4*y^2=1", mode, "x^2 + 4y^2 = 1");
		// hyperbola
		checkEquation("x^2-4*y^2=2x+2y+1", mode, "x^2 - 4y^2 - 2x - 2y = 1");
		// double line
		checkEquation("-x^2=x +x -1", mode, "-x^2 - 2x = -1");
	}

	@Test
	public void undefinedNumbersShouldBeQuestionMark() {
		t("b=1");
		t("SetValue[b,?]");
		assertEquals("b = ?",
				getGeo("b").toString(StringTemplate.editTemplate));
		assertEquals("b=?", app.getKernel().lookupLabel("b")
				.toString(StringTemplate.editorTemplate));
		assertEquals("b=?",
				app.getKernel().lookupLabel("b").getDefinitionForEditor());
	}

	private GeoElement getGeo(String string) {
		return app.getKernel().lookupLabel(string);
	}

	@Test
	public void shortLHSshouldBeDisplayedInLaTeX() {
		t("a = 7");
		t("f: y = x^3");
		t("g: y = x^3 + a");
		assertEquals(TestStringUtil.unicode("f: \\,y = x^3"),
				getGeo("f").getLaTeXAlgebraDescription(false,
						StringTemplate.defaultTemplate));
		assertEquals(TestStringUtil.unicode("f: \\,y = x^3"),
				getGeo("f").getLaTeXAlgebraDescription(true,
						StringTemplate.defaultTemplate));
		assertEquals(TestStringUtil.unicode("f: y = x^3"),
				getGeo("f").getDefinitionForInputBar());
		assertEquals(TestStringUtil.unicode("g: \\,y = x^3 + a"),
				getGeo("g").getLaTeXAlgebraDescription(false,
						StringTemplate.defaultTemplate));
		// TODO missing y =
		assertEquals(TestStringUtil.unicode("g: x^3 + a"),
				getGeo("g").getDefinitionForInputBar());

		t("in:x>a");
		assertEquals(TestStringUtil.unicode("in: x > a"),
				getGeo("in").getDefinitionForInputBar());

		t("ff: z = y + x^3");
		t("gg: z = y +x^3 + a");
		t("hh(x,y) = y +x^3 + a");
		assertEquals("ff: \\,z = y + x^{3}",
				getGeo("ff").getLaTeXAlgebraDescription(false,
						StringTemplate.latexTemplate));
		assertEquals("gg: \\,z = y + x^{3} + a",
				getGeo("gg").getLaTeXAlgebraDescription(false,
						StringTemplate.latexTemplate));
		assertEquals("hh\\left(x, y \\right)\\, = \\,y + x^{3} + a",
				getGeo("hh").getLaTeXAlgebraDescription(false,
						StringTemplate.latexTemplate));
	}

	@Test
	public void oneLHSshouldBeDisplayedInLaTeX() {
		t("a = 7");
		t("h(x) = a*x");
		assertEquals("h\\left(x \\right)\\, = \\,a \\; x",
				getGeo("h").getLaTeXAlgebraDescriptionWithFallback(false,
						StringTemplate.latexTemplate, false));
		t("hh(x,y) = a*x*y");
		assertEquals("hh\\left(x, y \\right)\\, = \\,a \\; x \\; y",
				getGeo("hh").getLaTeXAlgebraDescriptionWithFallback(false,
						StringTemplate.latexTemplate, false));

	}

	@Test
	public void operatorsShouldHaveOneSpace() {
		t("f(x)=If[3 < x <= 5,x^(2)]");
		assertEquals(
				TestStringUtil.unicode(
						"f(x) = If(3 < x " + Unicode.LESS_EQUAL + " 5, x^2)"),

				getGeo("f").getDefinitionForInputBar());
	}

	@Test
	public void listShouldKeepDefinition() {
		t("list1 = {x+x=y}");
		assertEquals("list1 = {x + x = y}",
				getGeo("list1").getDefinitionForInputBar());
		assertEquals("x + x = y", ((GeoList) getGeo("list1")).get(0)
				.getDefinition(StringTemplate.editTemplate));
		t("list2 = Flatten[{x=y}]");
		assertEquals("list2 = Flatten({x = y})",
				((GeoList) getGeo("list2")).getDefinitionForInputBar());

	}

	@Test
	public void singleVarEquationShouldHaveSuggestion() {
		t("p: z=0");
		assertEquals("z", StringUtil.join(",",
				((EquationValue) getGeo("p")).getEquationVariables()));
		t("p: x^2+z^2=0");
		assertEquals("x,z", StringUtil.join(",",
				((EquationValue) getGeo("p")).getEquationVariables()));
	}

	/**
	 * GGB-2021, TRAC-1642
	 */
	@Test
	public void substitutedFunctionsShouldBeExpanded() {
		t("ff(x)=x");

		t("gg(x)=2*ff(x)");

		t("hh(x)=gg(x-1)");
		assertEquals("2 (x - 1)",
				getGeo("hh").toValueString(StringTemplate.defaultTemplate));

		t("a(x, y) = -y^2 - x y + 2y");

		t("f(x) = x/2");

		t("g(x) = 1 -x/2");

		t("h(x) = a(x, f) - a(x, g)");

		assertEquals(
				TestStringUtil.unicode(
				"-(x / 2)^2 - x x / 2 + 2x / 2 - (-(1 - x / 2)^2 - x (1 - x / 2) + 2 (1 - x / 2))"),
				getGeo("h").toValueString(StringTemplate.defaultTemplate));

	}

	@Test
	public void tooltipsShouldHaveDefaultPrecision() {
		t("P=(0,1/3)");
		assertEquals("Point P(0, 0.33)",
				getGeo("P").getTooltipText(false, true));
	}

	@Test
	public void definitionShouldContainCommand() {
		t("text1=TableText[{{1}}]");
		assertEquals("text1 = TableText({{1}})",
				getGeo("text1").getDefinitionForInputBar());
		t("text2=FormulaText[sqrt(x)]");
		assertEquals("text2 = FormulaText(sqrt(x))",
				getGeo("text2").getDefinitionForInputBar());
	}

	private void t(String def) {
		ap.processAlgebraCommandNoExceptionHandling(def, false,
				TestErrorHandler.INSTANCE, false, null);
	}

	@Test
	public void pointDescriptionShouldNotHaveCoords() {

		app.getKernel().setAlgebraStyle(Kernel.ALGEBRA_STYLE_DESCRIPTION);
		GeoPoint gp = new GeoPoint(app.getKernel().getConstruction());
		gp.setCoords(1, 2, 1);
		gp.setLabel("P");
		IndexHTMLBuilder builder = new IndexHTMLBuilder(false);
		AlgebraItem.buildPlainTextItemSimple(getGeo("P"), builder);
		Assert.assertEquals("Point P", builder.toString());
		t("P=(1,0)");
		AlgebraItem.buildPlainTextItemSimple(getGeo("P"), builder);
		Assert.assertEquals("Point P", builder.toString());
		t("Q=Dilate[P,2]");
		AlgebraItem.buildPlainTextItemSimple(getGeo("Q"), builder);
		Assert.assertEquals("Q = P dilated by factor 2 from (0, 0)",
				builder.toString());
		t("R=2*P");
		AlgebraItem.buildPlainTextItemSimple(getGeo("R"), builder);
		Assert.assertEquals("R = 2P", builder.toString());

	}

	@Test
	public void dependentPointsShouldHaveTextDescriptions() {
		app.getKernel().setAlgebraStyle(Kernel.ALGEBRA_STYLE_DESCRIPTION);
		IndexHTMLBuilder builder = new IndexHTMLBuilder(false);
		t("P=(1,0)");
		AlgebraItem.buildPlainTextItemSimple(getGeo("P"), builder);
		Assert.assertEquals("Point P", builder.toString());
		t("Q=Dilate[P,2]");
		AlgebraItem.buildPlainTextItemSimple(getGeo("Q"), builder);
		Assert.assertEquals("Q = P dilated by factor 2 from (0, 0)",
				builder.toString());
		t("R=2*P");
		AlgebraItem.buildPlainTextItemSimple(getGeo("R"), builder);
		Assert.assertEquals("R = 2P", builder.toString());
	}

	@Test
	public void numericPreviewFormulaTestValueStyle() {
		app.getKernel().setAlgebraStyle(Kernel.ALGEBRA_STYLE_VALUE);
		t("1+1");
		GeoElement geo = getGeo("a");
		String previewFormula = AlgebraItem.getPreviewFormula(geo, StringTemplate.defaultTemplate);
		Assert.assertEquals("\\text{a = 2}", previewFormula);
	}

	@Test
	public void equationsShouldHaveSuggestion() {
		t("A=(1,1)");
		t("B=(0,1)");
		t("C=(1,0)");
		t("p=Polygon(A,B,C)");
		t("r:x=y");
		Assert.assertNull(SuggestionSolve.get(getGeo("r")));
		t("r:x=2x+1");
		Assert.assertNotNull(SuggestionSolve.get(getGeo("r")));
	}

	@Test
	public void systemsShouldHaveSuggestion() {
		t("A=(1,1)");
		t("B=(0,1)");
		t("C=(1,0)");
		t("p=Polygon(A,B,C)");
		t("r:x=y");
		Assert.assertNull(SuggestionSolve.get(getGeo("r")));
		t("q:x=y+1");
		Assert.assertNotNull(SuggestionSolve.get(getGeo("q")));
	}

	@Test
	public void previewEquationsShouldHaveSuggestion() {
		t("A=(1,1)");
		t("B=(0,1)");
		t("C=(1,0)");
		t("p=Polygon(A,B,C)");
		GeoLine line = new GeoLine(app.getKernel().getConstruction());
		line.setCoords(1, 1, 1);
		Assert.assertNull(SuggestionSolve.get(line));
		line.setCoords(1, 0, 1);
		Assert.assertNotNull(SuggestionSolve.get(line));
	}

	@Test
	public void previewSystemsShouldHaveSuggestion() {
		t("A=(1,1)");
		t("B=(0,1)");
		t("C=(1,0)");
		t("p=Polygon(A,B,C)");
		t("r:x=y");
		GeoLine line = new GeoLine(app.getKernel().getConstruction());
		line.setCoords(1, 1, 1);
		Assert.assertNotNull(SuggestionSolve.get(line));
	}

	@Test
	public void systemSuggestionShouldVanish() {
		t("p:x+y=2");
		t("r:x=y");
		GeoElement line = getGeo("r");
		Assert.assertNotNull(SuggestionSolve.get(line));
		SuggestionSolve.get(line).execute(line);
		Assert.assertNull(SuggestionSolve.get(line));
	}

	@Test
	public void rootSuggestionShouldVanish() {
		t("f:x");
		GeoElement line = getGeo("f");
		Assert.assertNotNull(SuggestionRootExtremum.get(line));
		SuggestionRootExtremum.get(line).execute(line);
		Assert.assertNull(SuggestionRootExtremum.get(line));
		getGeo("B").remove();
		Assert.assertNotNull(SuggestionRootExtremum.get(line));
	}

	@Test
	public void rootSuggestionForParabolaShouldVanish() {
		t("f:y=x^2-6x+8");
		GeoElement parabola = getGeo("f");
		Assert.assertNotNull(SuggestionRootExtremum.get(parabola));
		SuggestionRootExtremum.get(parabola).execute(parabola);
		Assert.assertNull(SuggestionRootExtremum.get(parabola));
		getGeo("B").remove();
		Assert.assertNotNull(SuggestionRootExtremum.get(parabola));
	}

	@Test
	public void rootSuggestionForParabolaShouldCreatePoints() {
		t("f:y=x^2-6x+8");
		GeoElement parabola = getGeo("f");
		Assert.assertNotNull(SuggestionRootExtremum.get(parabola));
		SuggestionRootExtremum.get(parabola).execute(parabola);
		Assert.assertEquals(4,
				app.getGgbApi().getAllObjectNames("point").length);
	}

	@Test
	public void rootSuggestionForHyperbola() {
		t("f:xx-yy=1");
		GeoElement hyperbola = getGeo("f");
		Assert.assertNull(SuggestionRootExtremum.get(hyperbola));
	}

	@Test
	public void suggestionShouldNotCreateTwice() {
		t("f:x");
		GeoElement line = getGeo("f");
		SuggestionRootExtremum.get(line).execute(line);
		Assert.assertEquals(3, app.getGgbApi().getObjectNumber());
		getGeo("B").remove();
		Assert.assertEquals(2, app.getGgbApi().getObjectNumber());
		SuggestionRootExtremum.get(line).execute(line);
		Assert.assertEquals(3, app.getGgbApi().getObjectNumber());
	}

	@Test
	public void suggestionShouldNotCreateTwiceNonPolynomial() {
		t("f:1/x");
		GeoElement line = getGeo("f");
		SuggestionRootExtremum.get(line).execute(line);
		Assert.assertEquals(4, app.getGgbApi().getObjectNumber());
		getGeo("B").remove();
		Assert.assertEquals(3, app.getGgbApi().getObjectNumber());
		SuggestionRootExtremum.get(line).execute(line);
		Assert.assertEquals(4, app.getGgbApi().getObjectNumber());
	}

	@Test
	public void packedGeosShouldHaveJustRHSInEditor() {
		t("c=Cone[(0,0,0),(0,0,1),5]");
		String rhs = getGeo("c").getLaTeXDescriptionRHS(false,
				StringTemplate.editorTemplate);
		assertEquals("Cone((0,0,0),(0,0,1),5)", rhs);
	}

	@Test
	public void statisticsSuggestionForEmptyList() {
		t("l1={}");
		GeoElement list = getGeo("l1");
		Assert.assertNull(SuggestionStatistics.get(list));
	}

	@Test
	public void statisticsSuggestionForOneElementList() {
		t("l1={1}");
		GeoElement list = getGeo("l1");
		Assert.assertNotNull(SuggestionStatistics.get(list));

		SuggestionStatistics.get(list).execute(list);
		Assert.assertEquals(4, app.getGgbApi().getObjectNumber());
	}

	@Test
	public void statisticsSuggestionForMoreElementList() {
		t("l1={1,2,3}");
		GeoElement list = getGeo("l1");
		Assert.assertNotNull(SuggestionStatistics.get(list));

		SuggestionStatistics.get(list).execute(list);
		Assert.assertEquals(6, app.getGgbApi().getObjectNumber());
	}

	@Test
	public void statisticsSuggestionShouldCreateOneUndoPoint() {
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		app.getKernel().getConstruction().initUndoInfo();

		t("l1={1,2,3}");
		GeoElement list = getGeo("l1");
		Assert.assertNotNull(SuggestionStatistics.get(list));

		app.storeUndoInfo();
		assertEquals(1, app.getKernel().getConstruction()
				.getUndoManager().getHistorySize());

		SuggestionStatistics.get(list).execute(list);
		Assert.assertEquals(6, app.getGgbApi().getObjectNumber());

		assertEquals(2, app.getKernel().getConstruction()
				.getUndoManager().getHistorySize());
	}

	private static void deg(String def, String expect) {
		GeoElementND[] geo = ap.processAlgebraCommandNoExceptionHandling(def,
				false, TestErrorHandler.INSTANCE,
				new EvalInfo(true, true).addDegree(true), null);
		String res = geo[0].toValueString(StringTemplate.editTemplate);
		Assert.assertEquals(expect, res);
	}

	private static void rad(String def, String expect) {
		GeoElementND[] geo = ap.processAlgebraCommandNoExceptionHandling(def,
				false, TestErrorHandler.INSTANCE, new EvalInfo(true, true),
				null);
		String res = geo[0].toValueString(StringTemplate.maxPrecision13);
		Assert.assertEquals(expect, res);
	}

	/** GGB-2183 */
	@Test
	public void sinCosTanZero() {
		rad("sin(1E-1)", "0.09983341664683");
		rad("sin(1E-2)", "0.009999833334167");
		rad("sin(1E-3)", "0.0009999998333333");
		rad("sin(1E-4)", "0.00009999999983333");
		rad("sin(1E-5)", "0.000009999999999833");
		rad("sin(1E-6)", "0.0000009999999999998");
		rad("sin(1E-7)", "0.0000001000000000000");
		rad("sin(1E-8)", "1.000000000000E-8");
		rad("sin(1E-9)", "1.000000000000E-9");
		rad("sin(1E-10)", "1.000000000000E-10");
		rad("sin(1E-11)", "1.000000000000E-11");
		rad("sin(1E-12)", "1.000000000000E-12");
		rad("tan(1E-1)", "0.1003346720855");
		rad("tan(1E-2)", "0.01000033334667");
		rad("tan(1E-3)", "0.001000000333333");
		rad("tan(1E-4)", "0.0001000000003333");
		rad("tan(1E-5)", "0.00001000000000033");
		rad("tan(1E-6)", "0.000001000000000000");
		rad("tan(1E-7)", "0.0000001000000000000");
		rad("tan(1E-8)", "1.000000000000E-8");
		rad("tan(1E-9)", "1.000000000000E-9");
		rad("tan(1E-10)", "1.000000000000E-10");
		rad("tan(1E-11)", "1.000000000000E-11");
		rad("tan(1E-12)", "1.000000000000E-12");
		rad("cos(pi/2)", "0.000000000000");
		rad("cos(3pi/2)", "0.000000000000");
		rad("cos(5pi/2)", "0.000000000000");
		rad("cos(7pi/2)", "0.000000000000");
		rad("sin(pi)", "0.000000000000");
		rad("sin(10pi)", "0.000000000000");
		rad("sin(100pi)", "0.000000000000");
		rad("sin(1000pi)", "0.000000000000");

	}

	/** GGB-2183 */
	@Test
	public void autoFixDegree() {
		deg("tan(45)", "1");
		deg("named45d:=45deg", "45" + Unicode.DEGREE_CHAR);
		deg("tan(named45d)", "1");
		deg("named45:=45", "45");
		deg("tan(named45)", "1.61978");
		deg("tan(30+15)", "1.61978");
		deg("sin(22.5)-(1 / 2 * sqrt((-sqrt(2)) + 2))", "0");
		deg("sin(22.5deg)-(1 / 2 * sqrt((-sqrt(2)) + 2))", "0");
		deg("sin(22.5" + Unicode.DEGREE_STRING
				+ ")-(1 / 2 * sqrt((-sqrt(2)) + 2))", "0");
		deg("(tan(30)+tan(15))/(1-tan(30)*tan(15))", "1");
		deg("sin(x)", "sin(x)");
		deg("sin(pi)", "0");
		deg("sin(deg)", "0.01745");
		deg("sin(1deg)", "0.01745");
		deg("sin(pi/180)", "0.01745");
		deg("sin(0.001)", "0.00002");
		deg("cos(45)", "0.70711");
		deg("cos(45deg)", "0.70711");
		deg("tan(45)", "1");
		deg("tan(45deg)", "1");
		deg("sin(45)", "0.70711");
		deg("sin(45deg)", "0.70711");
		deg("sec(45)", "1.41421");
		deg("sec(45deg)", "1.41421");
		deg("csc(45)", "1.41421");
		deg("cot(45deg)", "1");
		deg("cot(45)", "1");
		deg("sin(45+pi)", "-0.8509");

		// not degrees
		deg("sin(12+13)", "-0.13235");
		deg("sin(12deg+13deg)", "0.42262");

		deg("sin(4named45d)", "0");
		deg("sin(40deg)", "0.64279");
		deg("sin(40deg deg)", "0.01218");
		deg("sin(40deg deg deg)", "0.00021");
		deg("sin(40/deg)", "-0.99923");
		deg("sin(1/deg)", "0.67952");
		deg("sin(deg)", "0.01745");
		deg("sin(40deg/deg)", "0.74511");
		deg("sin(1+deg)", "0.85077");
		deg("sin(1)+cos(2)+tan(3)", "1.06925");
		deg("sin(1deg)+cos(2deg)+tan(3deg)", "1.06925");
		deg("sin(1)+sin(pi)", "0.01745");
		deg("sin(1deg)+sin(pi)", "0.01745");
		deg("sin(pi deg)", "0.0548");
		deg("sin(40)/cos(50)", "1");
		deg("sin(40deg)/cos(50)", "1");
		deg("sin(40)/cos(50deg)", "1");
		deg("sin(40deg)/cos(50deg)", "1");
		deg("sin(x)=1/2", "sin(x) = 1 / 2");
		deg("sin(x deg)=1/2", "sin(x" + Unicode.DEGREE_CHAR + ") = 1 / 2");
		deg("sin(30)=x/2", "x = 1");
		deg("sin(37)=x/2", "x = 1.20363");
		deg("sin(30)=2/x", "sin(30" + Unicode.DEGREE_CHAR + ") = 2 / x");
		deg("sin(asin(0.5))", "0.5");
		deg("sin(asin(0.5deg))", "0.00873");
		deg("sin(45.00001)", "0.70711");
		deg("sin(45.00001deg)", "0.70711");
		deg("sin(45.1)", "0.70834");
		deg("sin(45.1deg)", "0.70834");
		deg("sin(22.5)", "0.38268");
		deg("sin(22.5deg)", "0.38268");
		deg("sin((1,2))", "0.01746 + 0.03491" + Unicode.IMAGINARY);
		deg("sin((1deg,2))", "0.0003 + 0.03491" + Unicode.IMAGINARY);
		// minutes, seconds
		deg("a:sin(8')", "0.00233");
		deg("b:sin(8'')", "0.00004");
	}

	@Test
	public void multiplicationShouldNotHaveExtraBrackets() {
		new ExpressionChecker("3x*5x").checkEdit("3x * 5x", "3 x * 5 x")
				.checkVal("3x * 5x").checkGiac("(((3)*(x))*(5))*(x)");
		new ExpressionChecker("pi*x").checkEditAndVal(Unicode.pi + " x")
				.checkGiac("(pi)*(x)");
		new ExpressionChecker("3*4*x").checkEdit("3 * 4x", "3*4 x")
				.checkVal("3 * 4x").checkGiac("((3)*(4))*(x)");
		new ExpressionChecker("3*(4*x)").checkEdit("3 * 4x", "3 * 4 x")
				.checkVal("3 * 4x").checkGiac("(3)*((4)*(x))");
		new ExpressionChecker("3*4").checkEdit("3 * 4").checkVal("12").checkGiac("(3)*(4)");
		t("a1=7");
		new ExpressionChecker("3a1*x").checkEdit("3a1 x", "3 a1 x")
				.checkVal("3 * 7 x").checkGiac("((3)*(7))*(x)");
		new ExpressionChecker("a1*a1*a1*x").checkEdit("a1 a1 a1 x")
				.checkVal("7 * 7 * 7 x").checkGiac("(((7)*(7))*(7))*(x)");
		t("a1=pi");
		new ExpressionChecker("3a1*x").checkEdit("3a1 x", "3 a1 x")
				.checkVal("3%p x").checkGiac("((3)*(pi))*(x)");
		new ExpressionChecker("a1*a1*a1*x").checkEdit("a1 a1 a1 x")
				.checkVal("%p %p %p x").checkGiac("(((pi)*(pi))*(pi))*(x)");
	}

	@Test
	public void labelOrderingTest() {
		String[] ordered = new String[] { "A", "A1", "A2", "A10", "A1X", "B",
				"B1", "B2" };
		for (int i = 0; i < ordered.length; i++) {
			for (int j = 0; j < ordered.length; j++) {
				Assert.assertEquals(
						Math.signum(GeoElement.compareLabels(ordered[i],
								ordered[j])),
						Math.signum(i - j), .1);
			}
		}
	}

}
