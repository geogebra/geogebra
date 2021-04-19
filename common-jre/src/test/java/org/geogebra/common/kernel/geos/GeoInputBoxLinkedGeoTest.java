package org.geogebra.common.kernel.geos;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.test.commands.AlgebraTestHelper;
import org.junit.Assert;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoInputBoxLinkedGeoTest extends BaseUnitTest {

	private GeoInputBox inputBox;

	@Override
	public AppCommon createAppCommon() {
		return new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
	}

	@Test
	public void shouldNotShowQuotesForText() {
		setupInput("txt", "\"GeoGebra Rocks\"");
		t("ib", "GeoGebra Rocks");
		updateInput("GeoGebra Really Rocks");
		t("txt", "GeoGebra Really Rocks");
		hasType("txt", GeoClass.TEXT);
	}

	@Test
	public void shouldShowNewlineQuotesForText() {
		setupInput("txt", "\"GeoGebra\\\\nRocks\"");
		assertEquals("GeoGebra\\\\nRocks", inputBox.getText());
		updateInput("GeoGebra\\\\nReally\\\\nRocks");
		t("txt", "GeoGebra\nReally\nRocks");
	}

	@Test
	public void enteringNewValueShouldKeepVectorType() {
		setupAndCheckInput("v", "(1, 3)");
		t("Rename(v,\"V\")");
		updateInput("(1, 5)");
		t("V", "(1, 5)");
		hasType("V", GeoClass.VECTOR);
	}

	@Test
	public void enteringNewValueShouldKeepVectorType3D() {
		setupAndCheckInput("v3", "(1, 3, 6)");
		updateInput("(1, 5)");
		t("v3", "(1, 5, 0)");
		hasType("v3", GeoClass.VECTOR3D);
	}

	@Test
	public void enteringNewValueShouldKeepPlaneType() {
		setupAndCheckInput("p", "x + y - z = 0");
		updateInput("x = y");
		t("p", "x - y = 0");
		hasType("p", GeoClass.PLANE3D);
	}

	@Test
	public void enteringNewValueShouldKeepComplexNumber() {
		setupAndCheckInput("P", "1 + i");
		updateInput("7");
		t("P", "7 + 0" + Unicode.IMAGINARY);
		assertEquals("7",
				lookup("P").getDefinition(StringTemplate.defaultTemplate));
		hasType("P", GeoClass.POINT);
	}

	@Test
	public void enteringShortLinearExprShouldKeepLineType() {
		setupAndCheckInput("l", "y = 2x + 3");
		updateInput("3x + 5");
		t("l", "y = 3x + 5");
		hasType("l", GeoClass.LINE);
	}

	@Test
	public void symbolicShouldShowDefinition() {
		setupInput("l", "1 + 1 / 5");
		((GeoNumeric) lookup("l")).setSymbolicMode(true, false);
		inputBox.setSymbolicMode(true, false);
		assertEquals("1+(1)/(5)", inputBox.getTextForEditor());
		((GeoNumeric) lookup("l")).setSymbolicMode(false, false);
		assertEquals("1+(1)/(5)", inputBox.getTextForEditor());
	}

	@Test
	public void nonsymbolicShouldShowDefinitionForFraction() {
		setupInput("l", "1 + 1 / 5");
		((GeoNumeric) lookup("l")).setSymbolicMode(true, true);
		inputBox.setSymbolicMode(false, false);
		assertEquals("6 / 5", inputBox.getText());
		((GeoNumeric) lookup("l")).setSymbolicMode(false, true);
		assertEquals("1.2", inputBox.getText());
	}

	@Test
	public void shouldShowValueForSimpleNumeric() {
		setupInput("l", "5");
		inputBox.setSymbolicMode(true, false);
		assertEquals("5", inputBox.getText());
		assertEquals("5", inputBox.getTextForEditor());
	}

	@Test
	public void shouldBeEmptyAfterSettingLineUndefined() {
		setupInput("f", "y = 5");
		t("SetValue(f, ?)");
		assertEquals("", inputBox.getText());
	}

	@Test
	public void argumentsForFunctionCopyShouldBeVisible() {
		add("f:x");
		setupInput("g", "3f");
		assertEquals("3f(x)", inputBox.getText());
		updateInput("f(x)");
		assertEquals("f(x)", inputBox.getText());
	}

	@Test
	public void symbolicShouldBeEmptyAfterSettingLineUndefined() {
		setupInput("f", "y = 5");
		t("SetValue(f, ?)");
		inputBox.setSymbolicMode(true, false);
		assertEquals("", inputBox.getText());
		assertEquals("", inputBox.getTextForEditor());
	}

	@Test
	public void shouldBeEmptyAfterSettingPlaneUndefined() {
		setupInput("eq1", "4x + 3y + 2z = 1");
		t("SetValue(eq1, ?)");
		assertEquals("", inputBox.getText());
	}

	@Test
	public void symbolicShouldBeEmptyAfterSettingPlaneUndefined() {
		setupInput("eq1", "4x + 3y + 2z = 1");
		t("SetValue(eq1, ?)");
		inputBox.setSymbolicMode(true, false);
		assertEquals("", inputBox.getText());
		assertEquals("", inputBox.getTextForEditor());
	}

	@Test
	public void symbolicShouldShowDefinitionFor3DPoints() {
		setupInput("P", "(?,?,?)");
		inputBox.setSymbolicMode(true, false);
		assertEquals("(?,?,?)", inputBox.getTextForEditor());
		updateInput("(sqrt(2), 1/3, 0)");
		assertEquals("(sqrt(2),(1)/(3),0)", inputBox.getTextForEditor());
		add("SetValue(P,?)");
		assertEquals("(?,?,?)", inputBox.getTextForEditor());
	}

	@Test
	public void shouldAcceptLinesConicsAndFunctionsForImplicitCurve() {
		setupInput("eq1", "x^3 = y^2");
		updateInput("x = y"); // line
		assertEquals("x = y", inputBox.getText());
		updateInput("y = x"); // function (linear)
		assertEquals("y = x", inputBox.getText());
		updateInput("y = x^2"); // function (quadratic)
		assertEquals(unicode("y = x^2"), inputBox.getText());
		updateInput("x^2 = y^2"); // conic
		assertEquals(unicode("x^2 = y^2"), inputBox.getText());
	}

	@Test
	public void shouldAcceptLinesAndFunctionsForConics() {
		setupInput("eq1", "x^2 = y^2");
		updateInput("x = y"); // line
		assertEquals("x = y", inputBox.getText());
		updateInput("y = x"); // function (linear)
		assertEquals("y = x", inputBox.getText());
		updateInput("y = x^2"); // function (quadratic)
		assertEquals(unicode("y = x^2"), inputBox.getText());
	}

	@Test
	public void shouldAcceptFunctionsForLines() {
		setupInput("eq1", "x = y");
		updateInput("y = x"); // function (linear)
		assertEquals("y = x", inputBox.getText());
	}

	@Test
	public void shouldBeEmptyAfterPlaneInputUndefined() {
		setupInput("eq1", "4x + 3y + 2z = 1");
		GeoElement ib2 = add("in2=InputBox(eq1)");
		updateInput("?");
		// both input boxes undefined, we prefer empty string over question mark
		// even if that's what the user typed (APPS-1246)
		assertEquals("", inputBox.getText());
		assertEquals("", ((GeoInputBox) ib2).getText());
	}

	@Test
	public void shouldBeEmptyAfterImplicitUndefined() {
		setupInput("eq1", "x^2=y^3");
		updateInput("?");
		assertEquals("", inputBox.getText());
		assertEquals("eq1: \\,?", lookup("eq1")
				.getLaTeXAlgebraDescriptionWithFallback(false,
						StringTemplate.latexTemplate, false));
	}

	@Test
	public void shouldBeEmptyAfterDependentNumberUndefined() {
		add("a=1");
		setupInput("b", "3a");
		updateInput("x=y");
		assertEquals("b\\, = \\,?", lookup("b")
				.getLaTeXAlgebraDescriptionWithFallback(false,
						StringTemplate.latexTemplate, false));
	}

	@Test
	public void shouldAllowQuestionMarkWhenLinkedToText() {
		setupInput("txt", "\"GeoGebra Rocks\"");
		updateInput("?");
		assertEquals("?", inputBox.getText());
	}

	@Test
	public void shouldBeEmptyAfterSettingComplexUndefined() {
		setupInput("z1", "3 + i");
		t("SetValue(z1, ?)");
		assertEquals("", inputBox.getText());
	}

	@Test
	public void symbolicShouldBeEmptyAfterSettingComplexUndefined() {
		setupInput("z1", "3 + i");
		t("SetValue(z1, ?)");
		inputBox.setSymbolicMode(true, false);
		assertEquals("", inputBox.getText());
		assertEquals("", inputBox.getTextForEditor());
	}

	@Test
	public void functionParameterShouldNotChangeToX() {
		add("f(c) = c / ?");
		inputBox = add("ib=InputBox(f)");
		inputBox.setSymbolicMode(false, false);
		assertEquals("c / ?", inputBox.getText());
		updateInput("?");
		assertEquals("", inputBox.getText());
		updateInput("c / 3");
		assertEquals("c / 3", inputBox.getText());
	}

	@Test
	public void independentVectorsMustBeColumnEditable() {
		setupInput("l", "(1, 2, 3)");
		assertEquals("{{1}, {2}, {3}}", inputBox.getTextForEditor());
	}

	@Test
	public void symbolicShouldSupportVectorsWithVariables() {
		add("a: 1");
		setupInput("l", "(1, 2, a)");
		assertEquals("(1, 2, a)", inputBox.getText());
		assertEquals("{{1}, {2}, {a}}", inputBox.getTextForEditor());
	}

	@Test
	public void compound2DVectorsMustBeFlatEditable() {
		add("u: (1, 2)");
		add("v: (3, 4)");
		setupInput("l", "u + v");
		assertEquals("u+v", inputBox.getTextForEditor());
	}

	@Test
	public void compound3DVectorsMustBeFlatEditable() {
		add("u: (1, 2, 3)");
		add("v: (3, 4, 5)");
		setupInput("l", "u + v");
		assertEquals("u+v", inputBox.getTextForEditor());
	}

	@Test
	public void twoVariableFunctionParameterShouldNotChangeToX() {
		add("g(p, q) = p / ?");
		inputBox = add("ib=InputBox(g)");
		inputBox.setSymbolicMode(false, false);
		assertEquals("p / ?", inputBox.getText());
		updateInput("?");
		assertEquals("", inputBox.getText());
		updateInput("p / q");
		assertEquals("p / q", inputBox.getText());
	}

	@Test
	public void testGeoNumericExtendsMinMaxInSymbolic() {
		GeoNumeric numeric = add("a = 5");
		numeric.setShowExtendedAV(true);
		numeric.initAlgebraSlider();
		assertFalse(numeric.getIntervalMax() >= 20);
		assertFalse(numeric.getIntervalMin() <= -20);

		GeoInputBox inputBox = add("ib = InputBox(a)");
		inputBox.setSymbolicMode(true);

		inputBox.updateLinkedGeo("20");
		inputBox.updateLinkedGeo("-20");

		assertTrue(numeric.getIntervalMax() >= 20);
		assertTrue(numeric.getIntervalMin() <= -20);
	}

	@Test
	public void testGeoNumericIsClampedToMinMaxInNonSymbolic() {
		GeoNumeric numeric = add("a = 0");
		numeric.setShowExtendedAV(true);
		numeric.initAlgebraSlider();

		Assert.assertEquals(-5, numeric.getIntervalMin(), Kernel.MAX_PRECISION);
		Assert.assertEquals(5, numeric.getIntervalMax(), Kernel.MAX_PRECISION);

		inputBox = add("ib = InputBox(a)");

		inputBox.updateLinkedGeo("-10");
		Assert.assertEquals(-5, numeric.getValue(), Kernel.MAX_PRECISION);

		inputBox.updateLinkedGeo("10");
		Assert.assertEquals(5, numeric.getValue(), Kernel.MAX_PRECISION);
	}

	private void t(String input, String... expected) {
		AlgebraTestHelper.testSyntaxSingle(input, expected,
				getApp().getKernel().getAlgebraProcessor(),
				StringTemplate.xmlTemplate);
	}

	private void hasType(String label, GeoClass geoClass) {
		assertEquals(lookup(label).getGeoClassType(), geoClass);
	}

	private void updateInput(String string) {
		inputBox.textObjectUpdated(new ConstantTextObject(string));
	}

	private void setupAndCheckInput(String label, String value) {
		setupInput(label, value);
		assertEquals(value,
				inputBox.toValueString(StringTemplate.testTemplate));
	}

	private void setupInput(String label, String value) {
		add(label + ":" + value);
		inputBox = add("ib=InputBox(" + label + ")");
	}

	@Test
	public void testCanBeSymbolicForPlane() {
		add("A = (0,0)");
		add("B = (2,0)");
		add("C = (2,2)");
		add("p:Plane(A,B,C)");
		GeoInputBox inputBox = add("InputBox(p)");
		assertTrue(inputBox.canBeSymbolic());
	}

	@Test
	public void testCanBeSymbolicForEquation() {
		add("eq1:x^3+y^3=1");
		GeoInputBox inputBox1 = add("InputBox(eq1)");
		add("eq2:x^2+y^2+z^2=1");
		GeoInputBox inputBox2 = add("InputBox(eq2)");
		assertTrue(inputBox1.canBeSymbolic());
		assertTrue(inputBox2.canBeSymbolic());
	}

	@Test
	public void symbolicShouldBeEmptyAfterSettingConicUndefined() {
		setupInput("eq1", "xx+yy = 1");
		inputBox.setSymbolicMode(true, false);
		updateInput("?");
		assertEquals("", inputBox.getTextForEditor());
		getApp().setXML(getApp().getXML(), true);
		assertEquals("", inputBox.getTextForEditor());
		assertEquals("eq1: \\,?", lookup("eq1")
				.getLaTeXAlgebraDescriptionWithFallback(false,
						StringTemplate.latexTemplate, false));
	}

	@Test
	public void symbolicShouldBeEmptyAfterSettingQuadricUndefined() {
		setupInput("eq1", "x^2 + y^2 + z^2 = 1");
		inputBox.setSymbolicMode(true, false);
		inputBox.updateLinkedGeo("?");
		assertEquals("", inputBox.getTextForEditor());
		getApp().setXML(getApp().getXML(), true);
		assertEquals("", inputBox.getTextForEditor());
		assertEquals("eq1: \\,?", lookup("eq1")
				.getLaTeXAlgebraDescriptionWithFallback(false,
						StringTemplate.latexTemplate, false));
	}

	@Test
	public void minusShouldStayInNumerator() {
		setupInput("f", "x");
		inputBox.setSymbolicMode(true, false);
		updateInput("(-1)/4 x");
		assertEquals("(-1)/(4) x", inputBox.getTextForEditor());
		assertEquals("\\frac{-1}{4} \\; x", inputBox.getText());
	}

	@Test
	public void minusShouldStayInFrontOfFraction() {
		setupInput("f", "x");
		inputBox.setSymbolicMode(true, false);
		updateInput("-(1/4) x");
		assertEquals("-((1)/(4)) x", inputBox.getTextForEditor());
		assertEquals("-\\frac{1}{4} \\; x", inputBox.getText());
	}

	@Test
	public void implicitMultiplicationWithParenthesis() {
		add("c = 2");
		add("a = c + 2");
		setupInput("a", "2");
		updateInput("cc(2)");
		assertEquals("c c * 2", inputBox.getText());
	}

	@Test
	public void implicitMultiplicationWithEvaluatable() {
		add("f: y = 2 * x + 3");
		setupInput("g", "x");
		updateInput("xf(x) + 4");
		assertEquals("x f(x) + 4", inputBox.getText());
	}

	@Test
	public void symbolicShouldBeEmptyAfterSettingComplexFunctionUndefined() {
		setupInput("f", "x+i");
		inputBox.setSymbolicMode(true, false);
		inputBox.updateLinkedGeo("?");
		assertEquals("", inputBox.getTextForEditor());
		getApp().setXML(getApp().getXML(), true);
		assertEquals("", inputBox.getTextForEditor());
		assertEquals("ComplexFunction", lookup("f").getTypeString());
		// \text{undefined} also acceptable but ? is consistent with real-valued functions
		assertEquals("f(x) = ?", lookup("f")
				.getLaTeXAlgebraDescriptionWithFallback(false,
						StringTemplate.defaultTemplate, false));
	}

	@Test
	public void shouldAcceptNumberForComplexFunctions() {
		setupInput("f", "x+i");
		add("pt=f(1-i)");
		inputBox.setSymbolicMode(true, false);
		inputBox.updateLinkedGeo("2");
		assertEquals("2 + 0" + Unicode.IMAGINARY,
				lookup("pt").toValueString(StringTemplate.testTemplate));
	}

	@Test
	public void vector2dKeepsInput() {
		GeoVector vec1 = addAvInput("u=(1, 2)");
		GeoInputBox inputBox = add("InputBox(u)");
		GeoVector vec2 = addAvInput("v=(sqrt(3), 3/2)");
		vec1.set(vec2);
		assertThat(inputBox.getText(), equalTo("(sqrt(3), 3 / 2)"));
		addAvInput("SetValue(u,?)");
		assertThat(inputBox.getText(), equalTo("(?, ?)"));
	}

	@Test
	public void vector3dKeepsInput() {
		GeoVector3D vec1 = addAvInput("u=(1, 2, 3)");
		GeoInputBox inputBox = add("InputBox(u)");
		GeoVector3D vec2 = addAvInput("v=(5/6, 3/2, sqrt(5))");
		vec1.set(vec2);
		assertThat(inputBox.getText(), equalTo("(5 / 6, 3 / 2, sqrt(5))"));
		addAvInput("SetValue(u,?)");
		assertThat(inputBox.getText(), equalTo("(?, ?, ?)"));
	}

	@Test
	public void shouldPreferScalarProductOverDistance() {
		add("a=1");
		GeoInputBox inputBox = add("InputBox(a)");
		add("A=(1,2)");
		add("B=(1,3)");
		inputBox.updateLinkedGeo("AB");
		assertEquals(7, lookup("a").evaluateDouble(), 0);
	}

	@Test
	public void shouldNotAutocreatePoints() {
		add("A=(1,1)");
		GeoInputBox inputBox = add("InputBox(A)");
		add("B=(1,3)");
		inputBox.updateLinkedGeo("B2");
		assertEquals(lookup("A").toValueString(StringTemplate.testTemplate),
				"(2, 6)");
		inputBox.updateLinkedGeo("O");
		assertEquals(lookup("A").toValueString(StringTemplate.testTemplate),
				"(?, ?)");
	}

	@Test
	public void shouldNotAcceptCommands() {
		add("A=(1,1)");
		GeoInputBox inputBox = add("InputBox(A)");
		inputBox.updateLinkedGeo("Midpoint((0,0),(1,2))");
		assertTrue("Command should trigger error", inputBox.hasError());
	}

	@Test
	public void shouldNotAcceptRenaming() {
		add("A=(1,1)");
		GeoInputBox inputBox = add("InputBox(A)");
		inputBox.updateLinkedGeo("B=(7,7)");
		assertTrue("Rename should trigger error", inputBox.hasError());
		inputBox.updateLinkedGeo("B:(7,7)");
		assertTrue("Rename should trigger error", inputBox.hasError());
		inputBox.updateLinkedGeo("B:=(7,7)");
		assertTrue("Rename should trigger error", inputBox.hasError());
	}

	@Test
	public void pointOnPathShouldBeRestricted() {
		GeoElement point = add("A=Point(y=2)");
		GeoInputBox inputBox = add("InputBox(A)");
		inputBox.updateLinkedGeo("(3,7)");
		assertEquals(point.toValueString(StringTemplate.editTemplate),
				"(3, 2)");
	}

	@Test
	public void pointInRegionShouldBeRestricted() {
		GeoElement point = add("A=PointIn(xx+yy=2)");
		GeoInputBox inputBox = add("InputBox(A)");
		inputBox.updateLinkedGeo("(5,-5)");
		assertEquals(point.toValueString(StringTemplate.editTemplate),
				"(1, -1)");
	}

	@Test
	public void hasSpecialEditorTest() {
		GeoElement mat1 = add("mat1={{1,2,3}}");
		assertTrue(mat1.hasSpecialEditor());

		add("slider1 = 7");
		GeoElement mat2 = add("mat2={{1,2,slider1}}");
		assertTrue(mat2.hasSpecialEditor());
		mat2 = add("mat2={{1,2,slider1},Reverse[{1,2,3}]}");
		assertFalse(mat2.hasSpecialEditor());

		GeoElement l1 = add("l1: 3x + 2y = 4");
		assertFalse(l1.hasSpecialEditor());

		GeoElement l2 = add("l2: 3x + 2y = 5z - 4");
		assertFalse(l2.hasSpecialEditor());

		GeoElement A = add("A = (1, 2)");
		assertTrue(A.hasSpecialEditor());

		GeoElement B = add("B = (1, 2, 3)");
		assertTrue(B.hasSpecialEditor());

		GeoElement C = add("C = A + B");
		assertFalse(C.hasSpecialEditor());

		GeoElement v = add("v = (1, 2, 3)");
		assertTrue(v.hasSpecialEditor());

		GeoElement z_1 = add("z_1 = 3 + i");
		assertFalse(z_1.hasSpecialEditor());
	}

	@Test
	public void imaginaryUnitShouldBeDisplayedAsI() {
		add("m1 = {{1}, {2}}");
		GeoInputBox inputBox = add("InputBox(m1)");
		inputBox.updateLinkedGeo("{{" + Unicode.IMAGINARY + "}, {3}}");
		assertEquals("{{i},{3}}", inputBox.getTextForEditor());
		assertEquals("{{i}, {3}}", inputBox.getText());
	}
}
