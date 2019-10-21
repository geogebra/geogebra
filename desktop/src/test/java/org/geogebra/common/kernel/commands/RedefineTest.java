package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.test.TestErrorHandler;
import org.geogebra.test.TestStringUtil;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.geogebra.test.commands.ErrorAccumulator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class RedefineTest extends Assert {
	static AppDNoGui app;
	static AlgebraProcessor ap;

	@Before
	public void resetSyntaxes() {
		app.getKernel().clearConstruction(true);
		if (app.getExam() != null) {
			app.getExam().closeExam();
			app.setExam(null);
		}
	}

	/**
	 * Initialize app.
	 */
	@BeforeClass
	public static void setupApp() {
		app = AlgebraTest.createApp();
		ap = app.getKernel().getAlgebraProcessor();
	}

	private static void t(String input, String expected) {
		AlgebraTestHelper.testSyntaxSingle(input, new String[] { expected }, ap,
				StringTemplate.xmlTemplate);
	}

	private static void t(String input, String expected, StringTemplate tpl) {
		AlgebraTestHelper.testSyntaxSingle(input, new String[] { expected }, ap,
				tpl);
	}

	public static void t(String s, String[] expected) {
		AlgebraTestHelper.testSyntaxSingle(s, expected, ap,
				StringTemplate.xmlTemplate);
	}

	public void checkError(String s, String msg) {
		ErrorAccumulator errorStore = new ErrorAccumulator();
		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(s, false, errorStore,
						false, null);
		assertEquals(msg, errorStore.getErrors());
	}

	public void add(String s) {
		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(s, true,
						app.getDefaultErrorHandler(), false, null);
	}

	@Test
	public void breakingTypeChangeShouldRaiseException() {
		t("A=(1,1)", "(1, 1)");
		t("B=(1,0)", "(1, 0)");
		t("C=(0,0)", "(0, 0)");
		t("D=(0,1)", "(0, 1)");
		t("poly1=Polygon[A,B,C,D]", new String[] { "1", "1", "1", "1", "1" });
		t("a", "1"); // polygon side
		app.getKernel().setUndoActive(true);
		app.getKernel().initUndoInfo();
		app.storeUndoInfo();
		checkError("A(x)=x", "Redefinition failed");
		checkError("A(x)=(", "Unbalanced brackets \nA(x)=( ");
		checkError("A(x)=1+", "Please check your input");
		t("A", "(1, 1)");
		t("poly1", "1");
		t("a", "1");
	}

	@Test
	public void testErrors() {
		t("f(x)=x", "x");
		checkError("f(x)=f(x)+x", "Circular definition");
		checkError("f(x)=y", "Invalid function:\n" + "Please enter an explicit function in x");
		checkError("f(t)=y", "Invalid function:\n" + "Please enter an explicit function in t");
		checkError("f(t)=x", "Invalid function:\n" + "Please enter an explicit function in t");
		checkError("f(x)=3/(x^2+y^2=1)", "Illegal division \n" + "3 /  x\u00B2 + y\u00B2 = 1 ");
		checkError("f(x)=3*(x^2+y^2=1)",
				"Illegal multiplication \n" + "3 *  x\u00B2 + y\u00B2 = 1 ");
		checkError("f(x)=3+(x^2+y^2=1)", "Illegal addition \n" + "3 +  x\u00B2 + y\u00B2 = 1 ");
		checkError("f(x)=3-(x^2+y^2=1)", "Illegal subtraction \n" + "3 -  x\u00B2 + y\u00B2 = 1 ");
		checkError("f(x)=3^(x^2+y^2=1)", "Illegal exponent \n" + "3 ^  x\u00B2 + y\u00B2 = 1 ");
		checkError("f(x)=sin(x^2+y^2=1)", "Illegal argument \n" + "sin(  x\u00B2 + y\u00B2 = 1 ) ");
		// error could be improved
		checkError("f(x)=sin(1,2,3)", "Unknown command : sin");
		checkError("{1,2,3}\\(1,2)", "Illegal list operation \n" + "{1, 2, 3} \\ (1, 2) ");
		checkError("Rename(ff,\"fff\")",
				"Please check your input :\n" + "Undefined variable \n" + "ff ");
	}

	@Test
	public void curlyBracketsShouldNotAffectRedefine() {
		t("r=1", "1");
		t("r_2=2*r", "2");
		t("r_3=3*r_2", "6");
		t("r_{2}=3*r", "3");

		t("a=7", "7");
		t("A=(1,1)", "(1, 1)");
		t("B=(1,0)", "(1, 0)");
		t("C=(0,0)", "(0, 0)");
		t("D=(0,1)", "(0, 1)");
		t("poly1=Polygon[A,B,C,D]", new String[] { "1", "1", "1", "1", "1" });
		Kernel kernel = app.getKernel();
		assertEquals("a_1 = Segment(A, B, poly1)",
				kernel.lookupLabel("a_1").getDefinitionForEditor());
		t("a_{1} = Segment(A, B, poly1)", new String[0]);
		kernel.getAlgebraProcessor().changeGeoElement(kernel.lookupLabel("a_1"),
				"a_{1} = Segment(A, B, poly1)", true, true,
				TestErrorHandler.INSTANCE, null);
	}

	@Test
	public void undoShouldNotRandomize() {
		app.setRandomSeed(42);
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		t("a=random()", "0.7275636800328681");

		app.storeUndoInfo();
		t("1", "1");
		app.storeUndoInfo();
		app.getKernel().undo();

		t("a", "0.7275636800328681");
	}

	@Test
	public void undoShouldNotRandomizeBinomial() {
		app.setRandomSeed(42);
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		t("a=RandomBinomial(100, 0.6)", "61");

		app.storeUndoInfo();
		t("1", "1");
		app.storeUndoInfo();
		app.getKernel().undo();

		t("a", "61");
	}

	@Test
	public void randomizeUpdateConstruction() {
		app.setRandomSeed(42);
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		t("b=100", "100");
		t("a=randomUniform(0,b)", "72.75636800328681");
		((GeoNumeric) get("b")).setValue(10);
		((GeoNumeric) get("b")).resetDefinition();
		app.getKernel().updateConstruction(false);
		t("a", "10");
	}

	private static GeoElement get(String string) {
		return app.getKernel().lookupLabel(string);
	}

	@Test
	public void setValueShouldChangeRandom() {
		app.setRandomSeed(42);
		t("a=random()", "0.7275636800328681");
		t("SetValue(a,0.5)", new String[0]);
		t("a", "0.5");
	}

	@Test
	public void setValueShouldChangeShuffle() {
		app.setRandomSeed(42);
		t("L_1=Shuffle(1..10)", "{8, 7, 3, 2, 6, 10, 4, 1, 5, 9}");
		t("SetValue(L_1, {1, 2, 3, 4, 5, 6, 7, 11, 9, 10})", new String[0]);
		t("L_1", "{1, 2, 3, 4, 5, 6, 7, 9, 10, 8}");
	}

	@Test
	public void undoShouldNotRandomizeShufle() {
		app.setRandomSeed(42);
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		t("L_1=Shuffle(1..10)", "{8, 7, 3, 2, 6, 10, 4, 1, 5, 9}");

		app.storeUndoInfo();
		t("1", "1");
		app.storeUndoInfo();
		app.getKernel().undo();

		t("L_1", "{8, 7, 3, 2, 6, 10, 4, 1, 5, 9}");
	}

	@Test
	public void setValueShouldChangeRandomElement() {
		app.setRandomSeed(42);
		t("P=RandomElement((1..10,1..10))", "(8, 8)");
		t("SetValue(P, (7, 7))", new String[0]);
		t("P", "(7, 7)");
	}

	@Test
	public void undoShouldNotRandomizeRandomElement() {
		app.setRandomSeed(42);
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		t("P=RandomElement((1..10,1..10))", "(8, 8)");

		app.storeUndoInfo();
		t("1", "1");
		app.storeUndoInfo();
		app.getKernel().undo();

		t("P", "(8, 8)");
	}

	@Test
	public void undoShouldNotRandomizeRandomElementWithListOfLists() {
		app.setRandomSeed(42);
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		t("P=RandomElement(Identity(10))", "{0, 0, 0, 0, 0, 0, 0, 1, 0, 0}");

		app.storeUndoInfo();
		t("1", "1");
		app.storeUndoInfo();
		app.getKernel().undo();

		t("P", "{0, 0, 0, 0, 0, 0, 0, 1, 0, 0}");
	}

	@Test
	public void cmdRename() {
		checkError("Rename[ 6*7, \"$7\" ]",
				"Command Rename:\nIllegal argument: Text \"$7\"\n\nSyntax:\nRename( <Object>, <Name> )");
	}

	@Test
	public void functionLHSShouldRemainConic() {
		t("f(x,y)=xx+y", "x^(2) + y");
		t("a:f(x,y)=0", TestStringUtil.unicode("x^2 + y = 0"));
		Assert.assertEquals(get("a").getGeoClassType(), GeoClass.CONIC);
		app.setXML(app.getXML(), true);
		hasType("a", GeoClass.CONIC);
	}

	private static void hasType(String label, GeoClass geoClass) {
		Assert.assertEquals(get(label).getGeoClassType(), geoClass);
	}

	@Test
	public void copyOfConicShouldNotBeCellRange() {

		t("B20:x^2+y=0", TestStringUtil.unicode("x^2 + y = 0"));
		t("D20=B20", TestStringUtil.unicode("x^2 + y = 0"));
		Assert.assertEquals(
				app.getKernel().lookupLabel("D20").getGeoClassType(),
				GeoClass.CONIC);
		app.setXML(app.getXML(), true);
		Assert.assertEquals(
				app.getKernel().lookupLabel("D20").getGeoClassType(),
				GeoClass.CONIC);
	}

	@Test
	public void pointOnSplineShouldMove() {
		t("A=(1, 1)", "(1, 1)");
		t("b:Spline({(0, 1),A,(1, 0)})", TestStringUtil.unicode(
				"(If(t < 0.5, -2t^3 + 2.5t, 2t^3 - 6t^2 + 5.5t - 0.5), If(t < 0.5, -2t^3 + 0.5t + 1, 2t^3 - 6t^2 + 3.5t + 0.5))"),
				StringTemplate.editTemplate);
		t("B:ClosestPoint(A, b)", "(1, 1)");
		t("A=(0, 0)", "(0, 0)");
		t("B", "(0, 0)");
	}

	@Test
	public void pointOnFnShouldNotStayUndefined() {
		t("a=1", "1");
		t("f=axx", "x^(2)");
		t("A=Point[f]", "(0, 0)");
		t("a=?", "NaN");
		t("a=1", "1");
		t("A", "(0, 0)");
	}

	@Test
	public void pointOnPartialFunctionShouldStayUndefined() {
		t("ZoomIn[0,0,100,100]", new String[0]);
		t("a=.9", "0.9");
		// undefined for most onscreen points
		t("f=If(x==0, 1, ?)",
				"If[x " + Unicode.QUESTEQ + " 0, 1, NaN]");
		t("A=Point[f, a]", "(NaN, NaN)");
		t("a=.8", "0.8");
		t("A", "(NaN, NaN)");
	}

	@Test
	public void anonymousLineShouldStayLine() {
		app.getEuclidianView3D();
		app.setActiveView(App.VIEW_EUCLIDIAN3D);
		t("c=Circle((0,0,0),1,x=0)", "X = (0, 0, 0) + (0, - cos(t), sin(t))",
				StringTemplate.editTemplate);
		app.setActiveView(App.VIEW_EUCLIDIAN);
		t("d=Circle((0,0,0),1,x=0)", "X = (0, 0, 0) + (0, - cos(t), sin(t))",
				StringTemplate.editTemplate);

		app.setXML(app.getXML(), true);
		t("d", "X = (0, 0, 0) + (0, - cos(t), sin(t))",
				StringTemplate.editTemplate);
		t("c", "X = (0, 0, 0) + (0, - cos(t), sin(t))",
				StringTemplate.editTemplate);
	}

	@Test
	public void randomDerivatives() {
		add("f(x)=1/(x+RandomBetween(1,100))");
		add("g=Derivative(2*f)");
		add("h=Derivative(2*f)");
		add("f1(x)=RandomElement({x^2})");
		add("g1=Derivative(f1)");

		String xml = "<expression label=\"f2\" exp=\"f2(x) = x\" />"
				+ "<element type=\"function\" label=\"f2\">" + "<casMap>"
				+ "<entry key=\"Derivative[((Random[-5, 5] x^Random[3, 4]) + Random[1, 4]) / ((Random[1, 3] x) + Random[1, 5]),x,1]\" val=\"((8 * x^(3)) + (6 * x^(2)) - 4) / ((4 * x^(2)) + (4 * x) + 1)\"/>"
				+ "</casMap></element>";
		app.getGgbApi().evalXML(xml);
		Assert.assertFalse(app.getXML().contains("<entry"));
		add("UpdateConstruction()");
		t("g(7)-h(7)", "0");
	}

	@Test
	public void updateImplicitCurve() {
		add("a=2");
		t("c:y^2 = (x^2-a^2)/x^2", "y^(2) = (x^(2) - 2^(2)) / x^(2)");
		Assert.assertFalse("Implicit curve with var should be dependent.",
				get("c").isIndependent());
		t("c1:y^2 = (x^2-2^2)/x^2", "y^(2) = (x^(2) - 4) / x^(2)");
		Assert.assertTrue("Implicit curve without vars should be independent.",
				get("c1").isIndependent());
		Assert.assertEquals(
				TestStringUtil.unicode("c: y^2 = (x^2 - 2^2) / x^2"),
				get("c").getAlgebraDescriptionTextOrHTMLDefault(
						new IndexHTMLBuilder(true)));
		t("a=3", "3");
		Assert.assertEquals(
				TestStringUtil.unicode("c: y^2 = (x^2 - 3^2) / x^2"),
				get("c").getAlgebraDescriptionTextOrHTMLDefault(
						new IndexHTMLBuilder(true)));
	}

	@Test
	public void derivativeShouldNotThrowCircularException() {
		t("f(x)=x^2", "x^(2)");
		t("f'(x)=f'", "(2 * x)");
		ap.changeGeoElement(get("f'"), "f'(x)", true, true,
				TestErrorHandler.INSTANCE, new AsyncOperation<GeoElementND>() {
					@Override
					public void callback(GeoElementND obj) {
						// no callback
					}
				});
		t("f'(x)", "(2 * x)");
	}

	@Test
	public void redefinitionShouldNotMakeUnfixed() {
		add("b:Circle(O,1)");
		add("c:xx+yy=2");
		add("d:xx+yy");
		app.setNewExam();
		app.startExam();
		Assert.assertFalse(get("b").isLocked());
		Assert.assertTrue(get("c").isLocked());
		Assert.assertFalse(get("d").isLocked());
		add("d:xx+yy=2");
		Assert.assertTrue(get("d").isLocked());
	}

	@Test
	public void cubeShouldNotVanish() {
		add("A=O");
		add("a=1");
		add("Segment(A,a)");
		add("cb=Cube(A,B)");
		Assert.assertTrue(get("cb").isDefined());
		add("SetValue(a,-1)");
		Assert.assertFalse(get("cb").isDefined());
		add("SetValue(a,1)");
		Assert.assertTrue(get("cb").isDefined());
	}

}
