package org.geogebra.commands;

import java.util.Locale;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.desktop.main.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RedefineTest extends Assert {
	static AppDNoGui app;
	static AlgebraProcessor ap;

	@Before
	public void resetSyntaxes() {
		app.getKernel().clearConstruction(true);
	}

	@BeforeClass
	public static void setupApp() {
		app = new AppDNoGui(new LocalizationD(3), false);
		app.setLanguage(Locale.US);
		ap = app.getKernel().getAlgebraProcessor();
		// make sure x=y is a line, not plane
		app.getGgbApi().setPerspective("1");
		// Setting the general timeout to 11 seconds. Feel free to change this.
		app.getKernel().getApplication().getSettings().getCasSettings()
				.setTimeoutMilliseconds(11000);
	}
	private static void t(String input, String expected) {
		CommandsTest.testSyntaxSingle(input, new String[] { expected }, app, ap,
				StringTemplate.xmlTemplate);
	}

	public static void t(String s, String[] expected) {
		CommandsTest.testSyntaxSingle(s, expected, app, ap,
				StringTemplate.xmlTemplate);
	}

	public void checkError(String s, String msg) {
		ErrorAccumulator errorStore = new ErrorAccumulator();

		app.getKernel().getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(s, false, errorStore,
						false, null);

		assertEquals(msg, errorStore.getErrors());

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
		t("A", "(1, 1)");
		t("poly1", "1");
		t("a", "1");
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
		assertEquals("a_1: Segment(A, B, poly1)",
				kernel.lookupLabel("a_1").getDefinitionForEditor());
		t("a_{1}: Segment(A, B, poly1)", new String[0]);
		kernel.getAlgebraProcessor().changeGeoElement(kernel.lookupLabel("a_1"),
				"a_{1}: Segment(A, B, poly1)", true, true,
				new TestErrorHandler(), null);
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
	public void randomizeUpdateConstruction() {
		app.setRandomSeed(42);
		app.setUndoRedoEnabled(true);
		app.setUndoActive(true);
		t("b=100", "100");
		t("a=randomUniform(0,b)", "72.75636800328681");
		((GeoNumeric) app.getKernel().lookupLabel("b")).setValue(10);
		((GeoNumeric) app.getKernel().lookupLabel("b")).resetDefinition();
		app.getKernel().updateConstruction(false);
		t("a", "10");
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
	public void setValueShouldChangeRandomElement() {
		app.setRandomSeed(42);
		t("P=RandomElement((1..10,1..10))",
				"(8, 8)");
		t("SetValue(P, (7, 7))", new String[0]);
		t("P", "(7, 7)");
	}

	@Test
	public void cmdRename() {
		checkError("Rename[ 6*7, \"$7\" ]",
				"Command Rename:\nIllegal argument: Text \"$7\"\n\nSyntax:\nRename( <Object>, <Name> )");
	}

	@Test
	public void functionLHSShouldRemainConic() {
		t("f(x,y)=xx+y", "x^(2) + y");
		t("a:f(x,y)=0", CommandsTest.unicode("x^2 + y = 0"));
		Assert.assertEquals(app.getKernel().lookupLabel("a").getGeoClassType(),
				GeoClass.CONIC);
		app.setXML(app.getXML(), true);
		Assert.assertEquals(app.getKernel().lookupLabel("a").getGeoClassType(),
				GeoClass.CONIC);
	}

	@Test
	public void copyOfConicShouldNotBeCellRange() {

		t("B20:x^2+y=0", CommandsTest.unicode("x^2 + y = 0"));
		t("D20=B20", CommandsTest.unicode("x^2 + y = 0"));
		Assert.assertEquals(
				app.getKernel().lookupLabel("D20").getGeoClassType(),
				GeoClass.CONIC);
		app.setXML(app.getXML(), true);
		Assert.assertEquals(
				app.getKernel().lookupLabel("D20").getGeoClassType(),
				GeoClass.CONIC);
	}
}

