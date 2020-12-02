package org.geogebra.common.kernel.geos;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.io.XmlTestUtil;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.TextObject;
import org.geogebra.test.UndoRedoTester;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoInputBoxTest extends BaseUnitTest {

	private static final String POINT_2D = "(1,2)";
	private static final String POINT_3D = "(1,2,3)";
	private static final String FUNCTION = "x^2";
	private static final String CONIC = "x^2+y^2=1";
	private static final String LINE = "x+3y=1";
	private static final String PLANE = "x+y+z=1";
	private static final String NUMBER = "4";

	private TextObject textObject;

	@Override
	public AppCommon createAppCommon() {
		return AppCommonFactory.create3D();
	}

	@Before
	public void setUp() {
		textObject = Mockito.mock(TextObject.class);
	}

	@Test
	public void symbolicInputBoxUseDefinitionForFunctions() {
		add("f = x+1");
		add("g = 2f(x+2)+1");
		GeoInputBox inputBox1 = add("InputBox(f)");
		GeoInputBox inputBox2 = add("InputBox(g)");
		inputBox2.setSymbolicMode(true, false);
		assertEquals("x + 1", inputBox1.getText());
		assertEquals("2 f(x+2)+1", inputBox2.getTextForEditor());
	}

	@Test
	public void symbolicInputBoxUseDefinitionForFunctionsNVar() {
		add("f = x*y+1");
		add("g = 2f(x+2, y)+1");
		GeoInputBox inputBox1 = add("InputBox(f)");
		GeoInputBox inputBox2 = add("InputBox(g)");
		inputBox2.setSymbolicMode(true, false);
		assertEquals("x y + 1", inputBox1.getText());
		assertEquals("2 f(x+2,y)+1", inputBox2.getTextForEditor());
		assertEquals("2 \\; f\\left(x + 2, y \\right) + 1",
				inputBox2.getText());
	}

    @Test
    public void symbolicInputBoxTextShouldBeInLaTeX() {
        add("f = x + 12");
        add("g = 2f(x + 1) + 2");
        GeoInputBox inputBox2 = add("InputBox(g)");
        inputBox2.setSymbolicMode(true, false);
        assertEquals("2 \\; f\\left(x + 1 \\right) + 2", inputBox2.getText());
    }

    @Test
    public void testMatrixShouldBeInLaTeX() {
        add("m1 = {{1, 2, 3}, {4, 5, 6}}");
        GeoInputBox inputBox = add("InputBox(m1)");
        inputBox.setSymbolicMode(true, false);
        assertEquals("\\left(\\begin{array}{rrr}1&2&3\\\\4&5&6\\\\ \\end{array}\\right)",
				inputBox.getText());
    }

    @Test
    public void inputBoxTextAlignmentIsInXMLTest() {
        App app = getApp();
        add("A = (1,1)");
        GeoInputBox inputBox = add("B = Inputbox(A)");
        assertEquals(HorizontalAlignment.LEFT, inputBox.getAlignment());
        inputBox.setAlignment(HorizontalAlignment.CENTER);
        assertEquals(HorizontalAlignment.CENTER, inputBox.getAlignment());
        String appXML = app.getXML();
        app.setXML(appXML, true);
        inputBox = (GeoInputBox) lookup("B");
        assertEquals(HorizontalAlignment.CENTER, inputBox.getAlignment());
    }

	@Test
	public void testTempUserInputNotInXml() {
		add("A = (1,1)");
		GeoInputBox inputBox = add("B = InputBox(A)");
		inputBox.updateLinkedGeo("(1,2)");

		App app = getApp();
		String appXML = app.getXML();
		app.setXML(appXML, true);
		inputBox = (GeoInputBox) lookup("B");
		assertNull(inputBox.getTempUserDisplayInput());
		assertNull(inputBox.getTempUserEvalInput());
	}

	@Test
	public void testTempUserInputInXml() {
		add("A = (1,1)");
		GeoInputBox inputBox = add("B = Inputbox(A)");

		String wrongSyntax = "(1,1)+";
		inputBox.updateLinkedGeo(wrongSyntax);

		App app = getApp();
		String appXML = app.getXML();
		app.setXML(appXML, true);
		inputBox = (GeoInputBox) lookup("B");
		assertEquals(wrongSyntax, inputBox.getTempUserEvalInput());
		assertEquals(wrongSyntax, inputBox.getTempUserDisplayInput());
	}

	@Test
	public void inputBoxCorrectlySavedAndLoaded() {
		GeoText text = add("FormulaText(\\sqrt{n})");
		GeoInputBox savedInputBox = add("inputbox1=InputBox()");
		savedInputBox.setSymbolicMode(true);
		savedInputBox.setAuxiliaryObject(true);
		savedInputBox.setLength(50);
		savedInputBox.setAlignment(HorizontalAlignment.CENTER);
		savedInputBox.setTempUserDisplayInput("abcde");
		savedInputBox.setTempUserEvalInput("?");
		savedInputBox.setDynamicCaption(text);
		String appXML = getApp().getXML();
		XmlTestUtil.testCurrentXML(getApp());
		getApp().setXML(appXML, true);
		GeoInputBox loadedInputBox = (GeoInputBox) lookup("inputbox1");

		assertEquals(50, loadedInputBox.getLength());
		assertEquals(HorizontalAlignment.CENTER, loadedInputBox.getAlignment());
		assertEquals("abcde", loadedInputBox.getTempUserDisplayInput());
		assertEquals("?", loadedInputBox.getTempUserEvalInput());
		assertEquals(text, loadedInputBox.getDynamicCaption());
	}

	@Test
	public void testSymbolicUserInput() {
		add("a = 5");
		GeoInputBox inputBox =  add("Inputbox(a)");
		String tempDisplayInput = "\\frac{5}{\\nbsp}";
		inputBox.setTempUserDisplayInput(tempDisplayInput);
		inputBox.updateLinkedGeo("5/");
		inputBox.setSymbolicMode(true);
		assertEquals(tempDisplayInput, inputBox.getDisplayText());
		assertEquals("5/", inputBox.getText());
	}

	@Test
	public void testInputBoxGetTextWithError() {
		add("A = Point({1, 2})");
		GeoInputBox box = add("InputBox(A)");
		assertEquals("Point({1, 2})", box.getText());
		box.updateLinkedGeo("Point({1, 2})+");
		assertEquals("Point({1, 2})+", box.getText());
		box.updateLinkedGeo("Point(1)");
		assertEquals("Point(1)", box.getText());
	}

	@Test
	public void testSymbolicInputBoxGetTextWithError() {
		add("a = 1");
		GeoInputBox box = add("InputBox(a)");
		box.setSymbolicMode(true, true);
		assertEquals("1", box.getTextForEditor());
		box.updateLinkedGeo("1+/");
		assertEquals("1+/", box.getTextForEditor());
	}

	@Test
	public void testForSimpleUndefinedGeo() {
		add("a=?");
		GeoInputBox inputBox = add("InputBox(a)");
		inputBox.setSymbolicMode(true, false);
		assertEquals("", inputBox.getText());
		assertEquals("", inputBox.getTextForEditor());

		inputBox.setSymbolicMode(false, false);
		assertEquals("", inputBox.getText());

	}

	@Test
	public void testForDependentUndefinedGeo() {
		add("a=1");
		add("b=?a");

		GeoInputBox inputBox = add("InputBox(b)");
		inputBox.setSymbolicMode(true, false);
		assertEquals("? \\; a", inputBox.getText());
		assertEquals("? a", inputBox.getTextForEditor());

		inputBox.setSymbolicMode(false, false);
		assertEquals("?a", inputBox.getText());
	}

	@Test
	public void testForEmptyInput() {
		add("a=1");

		GeoInputBox inputBox = add("InputBox(a)");
		inputBox.setSymbolicMode(true, false);

		Mockito.when(textObject.getText()).thenReturn("");
		inputBox.textObjectUpdated(textObject);

		assertEquals("", inputBox.getText());
		assertEquals("", inputBox.getTextForEditor());

		inputBox.setSymbolicMode(false, false);

		assertEquals("", inputBox.getText());
	}

	@Test
	public void testForUndefinedInputInput() {
		add("a=1");

		GeoInputBox inputBox = add("InputBox(a)");
		inputBox.setSymbolicMode(true, false);

		Mockito.when(textObject.getText()).thenReturn("?");
		inputBox.textObjectUpdated(textObject);

		assertEquals("", inputBox.getText());
		assertEquals("", inputBox.getTextForEditor());

		inputBox.setSymbolicMode(false, false);

		assertEquals("", inputBox.getText());
	}

	@Test
	public void testInputForGeoText() {
		add("text = \"?\" ");
		GeoInputBox inputBox = add("InputBox(text)");

		inputBox.setSymbolicMode(true, false);
		assertEquals("?", inputBox.getText());
		assertEquals("?", inputBox.getTextForEditor());

		inputBox.setSymbolicMode(false, false);
		assertEquals("?", inputBox.getText());
	}

	@Test
	public void testCanBeSymbolicForNVarFunction() {
		add("f(x, y) = x + y");
		GeoInputBox inputBox = add("InputBox(f)");
		assertTrue(inputBox.canBeSymbolic());
	}

	@Test
	public void testCanBeSymbolicForBooleanFunction() {
		add("f(x, y) = x == y");
		GeoInputBox inputBox = add("InputBox(f)");
		assertTrue(inputBox.canBeSymbolic());
	}

	@Test
	public void testCanBeSymbolicForLine() {
		add("A = (0,0)");
		add("B = (2,2)");
		add("f:Line(A,B)");
		GeoInputBox inputBox = add("InputBox(f)");
		assertTrue(inputBox.canBeSymbolic());
	}

	@Test
	public void testErrorWorksWithString() {
		add("a = 5");
		GeoInputBox inputBox = add("ib = InputBox(a)");
		GeoText text = add("ib + \"\"");

		inputBox.updateLinkedGeo("1+");
		assertEquals("1+", text.getTextString());
	}

	@Test
	public void testCanRedefineSameClassDependent() {
		add("a = 1");
		add("b = 2");
		add("c = a + b");
		GeoInputBox inputBox = add("box = InputBox(c)");
		inputBox.updateLinkedGeo("7");

		assertEquals("7", inputBox.getText());
		assertTrue(inputBox.getLinkedGeo().isIndependent());

		inputBox.updateLinkedGeo("a + b");

		assertEquals("a + b", inputBox.getText());
		assertFalse(inputBox.getLinkedGeo().isIndependent());
	}

	@Test
	public void testGeoPointDoesNotChangeDisplayMode() {
		testDoesNotChangeDisplayMode("(1, 2)", Kernel.COORD_CARTESIAN);
		testDoesNotChangeDisplayMode("1 + 2" + Unicode.IMAGINARY, Kernel.COORD_COMPLEX);
		testDoesNotChangeDisplayMode("(1; 2)", Kernel.COORD_POLAR);
	}

	private void testDoesNotChangeDisplayMode(String original, int type) {
		GeoPoint point = add("A = " + original);
		GeoInputBox inputBox = add("InputBox(A)");
		assertEquals(point.getToStringMode(), type);

		String[] inputs = {"(1, 2)", "1 + i", "(2; 3)"};
		for (String input: inputs) {
			inputBox.updateLinkedGeo(input);
			assertEquals(point.getToStringMode(), type);
		}
		point.remove();
	}

	@Test
	public void test2DVectorRedefinition() {
		testRedefinition("v", "=", POINT_2D,
				GeoClass.VECTOR,
				new String[] {FUNCTION, CONIC, LINE, PLANE, NUMBER, POINT_3D},
				new String[] {POINT_2D});
	}

	@Test
	public void test3DVectorRedefinition() {
		testRedefinition("v", "=", POINT_3D,
				GeoClass.VECTOR3D,
				new String[] {FUNCTION, CONIC, LINE, PLANE, NUMBER},
				new String[] {POINT_3D, POINT_2D});
	}

	@Test
	public void testPlaneRedefinition() {
		testRedefinition("a", ":", PLANE,
				GeoClass.PLANE3D,
				new String[] {FUNCTION, CONIC, NUMBER, POINT_3D, POINT_2D},
				new String[] {LINE, PLANE});
	}

	@Test
	public void test2DPointRedefinition() {
		testRedefinition("A", "=", POINT_2D,
				GeoClass.POINT,
				new String[] {FUNCTION, CONIC, LINE, PLANE, POINT_3D},
				new String[] {POINT_2D, NUMBER});
	}

	@Test
	public void test3DPointRedefinition() {
		testRedefinition("A", "=", POINT_3D,
				GeoClass.POINT3D,
				new String[] {FUNCTION, CONIC, LINE, PLANE},
				new String[] {POINT_3D, POINT_2D, NUMBER});
	}

	@Test
	public void testConicRedefinition() {
		testRedefinition("eq1", ":", CONIC,
				GeoClass.CONIC,
				new String[] {FUNCTION, PLANE, NUMBER, POINT_3D, POINT_2D},
				new String[] {CONIC, LINE});
	}

	@Test
	public void testFunctionRedefinition() {
		testRedefinition("f", "=", FUNCTION,
				GeoClass.FUNCTION,
				new String[] {CONIC, LINE, PLANE, POINT_3D, POINT_2D},
				new String[] {FUNCTION, NUMBER});
	}

	@Test
	public void testLineRedefinition() {
		testRedefinition("f", ":", LINE,
				GeoClass.LINE,
				new String[] {CONIC, FUNCTION, PLANE, POINT_3D, POINT_2D},
				new String[] {LINE, NUMBER});
	}

	@Test
	public void testNumberRedefinition() {
		testRedefinition("a", "=", NUMBER,
				GeoClass.NUMERIC,
				new String[] {CONIC, FUNCTION, PLANE, POINT_3D, POINT_2D, LINE},
				new String[] {NUMBER});
	}

	@Test
	public void valueStringShouldBePlainText() {
		add("num=1/2");
		GeoInputBox box = add("b=InputBox(num)");
		box.setSymbolicMode(true, true);
		GeoText plain = add("b+\"\"");
		assertEquals("1 / 2", plain.getTextString());
	}

	@Test
	public void valueStringShouldBePlainTextForNumericBox() {
		add("num=1/2");
		add("b=InputBox(num)");
		GeoText plain = add("b+\"\"");
		assertEquals("0.5", plain.getTextString());
	}

	@Test
	public void latexStringShouldBeLaTeX() {
		add("num=1/2");
		GeoInputBox box = add("b=InputBox(num)");
		box.setSymbolicMode(true, true);
		GeoText plain = add("LaTeX(b)");
		assertEquals("\\frac{1}{2}", plain.getTextString());
	}

	private void testRedefinition(String label, String sign, String expression, GeoClass keepType,
								  String[] refusedRedefinitions, String[] acceptedRedefinitions) {
		for (String refused: refusedRedefinitions) {
			assertRedefinition(label, sign, expression, refused, keepType, false);
		}
		for (String refused: acceptedRedefinitions) {
			assertRedefinition(label, sign, expression, refused, keepType, true);
		}
	}

	private void assertRedefinition(String label, String sign, String expression,
									String redefinition, GeoClass keepType,
									boolean assertDefined) {
		String input = label + sign + expression;
		add(input);
		GeoInputBox inputBox = add("InputBox(" + label + ")");
		inputBox.updateLinkedGeo(redefinition);

		GeoElementND element = inputBox.getLinkedGeo();
		String message = (assertDefined ? "should keep " : "should not keep ")
				+ keepType + " " + redefinition;
		assertEquals(message, assertDefined, element.isDefined());
		assertEquals(keepType, element.getGeoClassType());
		element.remove();
	}

	@Test
	public void testUserInputNullAfterUpdatingLinkedGeoToValidInput() {
		addAvInput("f(x) = x");
		GeoInputBox inputBox = addAvInput("a = InputBox(f)");
		inputBox.updateLinkedGeo("xx");
		assertThat(inputBox.getTempUserEvalInput(), is(nullValue()));
	}

	@Test
	public void testUserInputAakkaa() {
		add("aa(x) = ?");
		addAvInput("a = 2");
		addAvInput("g(k) = ?");
		GeoInputBox inputBox = addAvInput("ib = InputBox(g)");
		inputBox.updateLinkedGeo("aakkaa");
		assertEquals(unicode("a a k^2 a a"), inputBox.getText());
	}

	@Test
	public void testUserInputSinx() {
		addAvInput("a=7");
		addAvInput("g(x) = ?");
		GeoInputBox inputBox = addAvInput("ib = InputBox(g)");
		inputBox.updateLinkedGeo("a sinx");
		assertEquals("a sin(x)", inputBox.getText());
	}

	@Test
	public void testUserInputNonNullAfterUpdatingLinkedGeoToInvalidInput() {
		addAvInput("f(x) = x");
		GeoInputBox inputBox = addAvInput("a = InputBox(f)");
		inputBox.updateLinkedGeo("x+()");
		assertThat(inputBox.getTempUserEvalInput(), is(notNullValue()));
	}

	@Test
	public void testUndoRedoWithInvalidInput() {
		App app = getApp();
		UndoRedoTester undoRedo = new UndoRedoTester(app);
		undoRedo.setupUndoRedo();

		addAvInput("f(x) = x");
		GeoInputBox inputBox = addAvInput("a = InputBox(f)");
		app.storeUndoInfo();
		inputBox.updateLinkedGeo("x+()");

		inputBox = undoRedo.getAfterUndo("a");
		assertThat(inputBox.getText(), equalTo("x"));

		inputBox = undoRedo.getAfterRedo("a");
		assertThat(inputBox.getText(), equalTo("x+()"));
	}

	@Test
	public void testUndoRedoWithNonSimpleNumeric() {
		App app = getApp();
		UndoRedoTester undoRedo = new UndoRedoTester(app);
		undoRedo.setupUndoRedo();

		addAvInput("n = 1");
		GeoInputBox inputBox = addAvInput("a = InputBox(n)");
		app.storeUndoInfo();
		inputBox.setSymbolicMode(true);
		app.storeUndoInfo();
		inputBox.updateLinkedGeo("1+sqrt(2)");
		addAvInput("P = (1, 1)");
		app.storeUndoInfo();

		inputBox = undoRedo.getAfterUndo("a");
		assertThat(inputBox.getText(), equalTo("1 + \\sqrt{2}"));

		inputBox = undoRedo.getAfterUndo("a");
		assertThat(inputBox.getText(), equalTo("1"));

		inputBox = undoRedo.getAfterRedo("a");
		assertThat(inputBox.getText(), equalTo("1 + \\sqrt{2}"));
	}

	@Test
	public void testDependentGeosUpdate() {
		add("g(x) = ?");
		GeoInputBox inputBox = addAvInput("ib = InputBox(g)");
		add("correct = Text(ib) == \"x\"");
		inputBox.updateLinkedGeo("x+");
		inputBox.updateLinkedGeo("x");
		assertTrue(((GeoBoolean) lookup("correct")).getBoolean());
	}

	@Test
	public void testSingleIneqRedefinedToDoubleIneq() {
		add("a:x<6");
		GeoInputBox inputBox = addAvInput("ib = InputBox(a)");
		inputBox.updateLinkedGeo("3<x<7");
		assertThat(lookup("a").isDefined(), equalTo(true));

		inputBox.updateLinkedGeo("3<x");
		assertThat(lookup("a").isDefined(), equalTo(true));
	}

	@Test
	public void testInequalityCannotRedefineAsFunction() {
		add("a:x<6");
		GeoInputBox inputBox = addAvInput("ib = InputBox(a)");
		inputBox.updateLinkedGeo("xx");
		assertThat(lookup("a").isDefined(), equalTo(false));
		assertThat(inputBox.getText(), equalTo("xx")); // still preserves user input

		add("b:2<x<9");
		GeoInputBox inputBox2 = addAvInput("ib2 = InputBox(b)");
		inputBox2.updateLinkedGeo("x+5");
		assertThat(lookup("b").isDefined(), equalTo(false));
		assertThat(inputBox2.getText(), equalTo("x+5")); // still preserves user input
	}

	@Test
	public void testFunctionCannotRedefineAsInequality() {
		add("f(x)=xx");
		GeoInputBox inputBox = addAvInput("ib = InputBox(f)");
		inputBox.updateLinkedGeo("x<5");
		assertThat(lookup("f").isDefined(), equalTo(false));
		assertThat(inputBox.getText(), equalTo("x<5")); // still preserves user input

		add("g(x)=x+8");
		GeoInputBox inputBox2 = addAvInput("ib2 = InputBox(g)");
		inputBox2.updateLinkedGeo("2<x<10");
		assertThat(lookup("g").isDefined(), equalTo(false));
		assertThat(inputBox2.getText(), equalTo("2<x<10")); // still preserves user input
	}

	@Test
	public void testInequalitySetUndefinedInputboxShouldBeEmpty() {
		add("a:x<6");
		GeoInputBox inputBox = addAvInput("ib = InputBox(a)");
		add("SetValue(a,?)");
		assertThat(lookup("a").isDefined(), equalTo(false));
		assertThat(inputBox.getText(), equalTo("")); // still preserves user input

		add("b:2<x<9");
		GeoInputBox inputBox2 = addAvInput("ib2 = InputBox(b)");
		add("SetValue(b,?)");
		assertThat(lookup("b").isDefined(), equalTo(false));
		assertThat(inputBox2.getText(), equalTo("")); // still preserves user input
	}

	@Test
	public void testCommandLikeImplicitMultiplicationParsesCorrectly() {
		add("f(g, L) = ?");
		GeoInputBox inputBox = addAvInput("ib = InputBox(f)");
		inputBox.updateLinkedGeo("gL(L+1)");
		assertEquals("g L (L + 1)", inputBox.getText());

		inputBox.updateLinkedGeo("gL(L+1)^3");
		assertEquals("g L (L + 1)³", inputBox.getText());
	}

	@Test
	public void testDefaultInputBoxSerif() {
		add("f(x) = xsinx");
		GeoInputBox inputBox = addAvInput("ib = InputBox(f)");
		assertTrue(inputBox.isSerifContent());
	}

	@Test
	public void testConstantNumberDontGetSimplified() {
		add("f(θ) = 2 + 1*1*1");
		GeoInputBox inputBox = addAvInput("ib = InputBox(f)");
		assertEquals("2 + 1 * 1 * 1", inputBox.getText());

		inputBox.updateLinkedGeo("sqrt(4)");
		assertEquals("sqrt(4)", inputBox.getText());

		inputBox.updateLinkedGeo("1+2/2");
		assertEquals("1 + 2 / 2", inputBox.getText());

		inputBox.updateLinkedGeo("5/5+1");
		assertEquals("5 / 5 + 1", inputBox.getText());

		inputBox.updateLinkedGeo("1/(10/1)");
		assertEquals("1 / (10 / 1)", inputBox.getText());

		inputBox.updateLinkedGeo("floor((1+2))");
		assertEquals("floor(1 + 2)", inputBox.getText());

		inputBox.updateLinkedGeo("floor((1+2))+3+4");
		assertEquals("floor(1 + 2) + 3 + 4", inputBox.getText());

		inputBox.updateLinkedGeo("e^36-1");
		assertEquals("ℯ³⁶ - 1", inputBox.getText());
	}

	@Test
	public void testConstantsDontGetSimplifiedInFunctions() {
		add("f(t) = t+10^2");
		GeoInputBox inputBox = addAvInput("ib = InputBox(f)");
		assertEquals("t + 10²", inputBox.getText());

		inputBox.updateLinkedGeo("1+1+t");
		assertEquals("1 + 1 + t", inputBox.getText());

		inputBox.updateLinkedGeo("10^10 + t");
		assertEquals("10¹⁰ + t", inputBox.getText());

		inputBox.updateLinkedGeo("-3/4t + 2*3/2");
		assertEquals("-3 / 4 t + 2 * 3 / 2", inputBox.getText());
	}

	@Test
	public void testSanSerifInputBoxLoadsSanSerif() {
		getApp().getGgbApi().evalXML("<element type=\"textfield\" label=\"InputBox1\">\n"
				+ "\t<show object=\"true\" label=\"true\"/>\n"
				+ "\t<objColor r=\"0\" g=\"0\" b=\"0\" alpha=\"0\"/>\n"
				+ "\t<layer val=\"0\"/>\n"
				+ "\t<labelOffset x=\"65\" y=\"65\"/>\n"
				+ "\t<labelMode val=\"3\"/>\n"
				+ "\t<fixed val=\"true\"/>\n"
				+ "\t<auxiliary val=\"true\"/>\n"
				+ "\t<symbolic val=\"true\" />\n"
				+ "\t<contentSerif val=\"false\" />\n"
				+ "\t<caption val=\"Serif\"/>\n"
				+ "</element>");
		GeoInputBox inputBox = (GeoInputBox) getConstruction().lookupLabel("InputBox1");
		assertFalse(inputBox.isSerifContent());
	}

	@Test
	public void testOldInputBoxLoadsSerif() {
		getApp().getGgbApi().evalXML("<element type=\"textfield\" label=\"InputBox1\">\n"
				+ "\t<show object=\"true\" label=\"true\"/>\n"
				+ "\t<objColor r=\"0\" g=\"0\" b=\"0\" alpha=\"0\"/>\n"
				+ "\t<layer val=\"0\"/>\n"
				+ "\t<labelOffset x=\"65\" y=\"65\"/>\n"
				+ "\t<labelMode val=\"3\"/>\n"
				+ "\t<fixed val=\"true\"/>\n"
				+ "\t<auxiliary val=\"true\"/>\n"
				+ "\t<symbolic val=\"true\" />\n"
				+ "\t<caption val=\"Serif\"/>\n"
				+ "</element>");
		GeoInputBox inputBox = (GeoInputBox) getConstruction().lookupLabel("InputBox1");
		assertTrue(inputBox.isSerifContent());
	}

	@Test
	public void voidReplaceForLinesYWithFOfX() {
		add("g: y=x");
		GeoInputBox inputBox = addAvInput("ib = InputBox(g)");
		inputBox.updateLinkedGeo("f(x)=x+5");
		assertEquals("y = x + 5", inputBox.getText());
	}

	@Test
	public void commaParsingShouldWorkInEnglish() {
		shouldReparseAs("3,141", "3141");
		shouldReparseAs("3,5>x", "If(5 > x, 3)");
		shouldReparseAs("(1,2) + 1,423", "(1, 2) + 1423");
		// merely testing that we don't throw a *wrong* exception
		shouldReparseAs("3,", "3");
	}

	@Test
	public void commaParsingShouldWorkInGerman() {
		getApp().getLocalization().setLocale(Locale.GERMAN);
		shouldReparseAs("3,141", "3.141");
		// more cases in ParserTest, no duplication here
	}

	private void shouldReparseAs(String s, String s1) {
		GeoElement linked = add(s);
		GeoInputBox input = add("InputBox(" + linked.getLabelSimple() + ")");
		input.updateLinkedGeo(s1);
		assertEquals(s1, linked.getRedefineString(false, false,
				StringTemplate.testTemplate));
	}
}