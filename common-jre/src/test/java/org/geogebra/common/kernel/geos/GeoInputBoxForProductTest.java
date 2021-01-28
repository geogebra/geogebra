package org.geogebra.common.kernel.geos;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoInputBoxForProductTest extends BaseUnitTest {

	@Test
	public void testPiRSquare() {
		add("g = ?");
		add("r = ?");
		shouldBeUpdatedAs("g", "pir^(2)", Unicode.PI_STRING + " r" + Unicode.SUPERSCRIPT_2);
	}

	@Test
	public void testAvarb() {
		add("a=1");
		add("f(var)=?");
		add("b=2");
		shouldBeUpdatedAs("f", "avarb", "a var b");
	}

	@Test
	public void testVarVar() {
		add("f(var)=?");
		shouldBeUpdatedAs("f", "var var", "var var");
	}

	@Test
	public void testMultiVarProduct() {
		addAvInput("f(u, v)=?");
		shouldBeUpdatedAs("f", "uv", "u v");
		shouldBeUpdatedAs("f", "vu", "v u");
	}

	@Test
	public void testXPlusBs() {
		add("f(x)=?");
		add("b=1");
		shouldBeUpdatedAs("f", "x+bb", "x+b b");
		shouldBeUpdatedAs("f", "x+bbb", "x+b b b");
		shouldBeUpdatedAs("f", "x+bbbb", "x+b b b b");
		shouldBeUpdatedAs("f", "x+bbbbbx", "x+b b b b b x");
	}

	@Test
	public void testABX() {
		add("f(x)=?");
		add("a=?");
		add("b=?");
		shouldBeUpdatedAs("f", "xab", "x a b");
		shouldBeUpdatedAs("f", "x + ab", "x+a b");
		shouldBeUpdatedAs("f", "xxxxxxxxxx", "x" + Unicode.SUPERSCRIPT_1 + Unicode.SUPERSCRIPT_0);
		shouldBeUpdatedAs("f", "axxxxxxxxxx",
				"a x" + Unicode.SUPERSCRIPT_1 + Unicode.SUPERSCRIPT_0);
		shouldBeUpdatedAs("f", "axaxaxaxax", "a x a x a x a x a x");
	}

	@Test
	public void testAkka() {
		add("a=?");
		add("aa(x,y)=?");
		add("g(k)=?");
		shouldBeUpdatedAs("g", "kk", unicode("k^2"));
		shouldBeUpdatedAs("g", "kkk", unicode("k^3"));
		shouldBeUpdatedAs("g", "kkkk", unicode("k^4"));
		shouldBeUpdatedAs("g", "akakak", "a k a k a k");
		shouldBeUpdatedAs("g", "akka", unicode("a k^2 a"));
		shouldBeUpdatedAs("g", "kkaa", unicode("k^2 a a"));
	}

	@Test
	public void testArctanIntegral() {
		add("f(x)=?");
		shouldBeUpdatedAs("f", "21xarctanx", "21 x tan"
				+ Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(x)");
		shouldBeUpdatedAs("f", "22xarctan(x)", "22 x tan"
				+ Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(x)");
	}

	@Test
	public void testSinPower() {
		add("f(x)=?");
		shouldBeUpdatedAs("f", "xsin^2(x)", unicode("x sin^2(x)"));
		shouldBeUpdatedAs("f", "xsin^(-1)(x)", "x sin"
				+ Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(x)");
	}

	@Test
	public void functionPowerShouldNotBeUsedForProduct() {
		add("f(r,t)=?");
		shouldBeUpdatedAs("f", "t^2 r^(11t)", unicode("t^2 r^(11 t)"));
	}

	@Test
	public void testCost7() {
		add("g(t)=?");
		shouldBeUpdatedAs("g", "-tcos7t/7", "(-(t cos(7 t)))/(7)");
		shouldBeUpdatedAs("g", "-tcos(8t)/7", "(-(t cos(8 t)))/(7)");
	}

	@Test
	public void testNpi7() {
		add("f(x)=?");
		add("n=6");
		shouldBeUpdatedAs("f", "npi/7", "(n " + Unicode.PI_STRING + ")/(7)");
	}

	@Test
	public void testLnX() {
		add("f(x)=?");
		shouldBeUpdatedAs("f", "xlnx", "x ln(x)");
		shouldBeUpdatedAs("f", "xln2x", "x ln(2 x)");
	}

	@Test
	public void testC_2Index() {
		add("c_2=3");
		add("f(x)=?");
		shouldBeUpdatedAs("f", "c_2e^(7x)", "c_2 " + Unicode.EULER_STRING + "^(7 x)");
	}

	@Test
	public void testsina() {
		add("f(x)=?");
		add("a=4");
		shouldBeUpdatedAs("f", "sinax", "sin(a x)");
	}

	@Test
	public void testx4() {
		add("f(x)=?");
		shouldBeUpdatedAs("f", "x4", "x*4");
	}

	@Test
	public void testk4() {
		add("g(k)=?");
		shouldBeUpdatedAs("g", "k4", "k*4");
	}

	@Test
	public void testAkakakaaa() {
		add("a=7");
		add("g(k)=?");
		shouldBeUpdatedAs("g", "akakakaaa", "a k a k a k a a a");
	}

	@Test
	public void testImaginaryProduct() {
		add("a=4+i");
		shouldBeUpdatedAs("a", "i1", "i");
	}

	@Test
	public void testPiSqrt() {
		add("f(x)=?");
		shouldBeUpdatedAs("f", "18pisqrt5", "18 " + Unicode.PI_STRING + " sqrt(5)");
	}

	@Test
	public void testIndex() {
		add("f(x)=?");
		add("B_{0}=7");
		shouldBeUpdatedAs("f", "B_{0}e^(2)",
				"B_{0} " + Unicode.EULER_STRING + Unicode.SUPERSCRIPT_2);
	}

	@Test
	public void testTangent() {
		add("f(x)=?");
		shouldBeUpdatedAs("f", "2xtan8x", "2 x tan(8 x)");
	}

	@Test
	public void testFcosThetaSum() {
		add("θ=45");
		add("F=5");
		add("f(x, y)=?");
		shouldBeUpdatedAs("f", "Fcosθx+Fsinθy", "F cos(θ x)+F sin(θ y)");
	}

	private void numberBeUpdatedAs(String updatedText, String expected) {
		addAvInput("a = 1");
		shouldBeUpdatedAs("a", updatedText, expected);
	}

	private void shouldBeUpdatedAs(String linkedGeo, String updatedText, String expected) {
		GeoInputBox inputBox = addAvInput("ib = InputBox(" + linkedGeo + ")");
		inputBox.setSymbolicMode(true);
		inputBox.updateLinkedGeo(updatedText);
		assertEquals(expected, inputBox.getTextForEditor());
	}

	@Test
	public void minusPiShouldStayAsItIs() {
		String minusPi = "-" + Unicode.PI_STRING;
		numberBeUpdatedAs(minusPi,	minusPi);
	}

	@Test
	public void minusEShouldStayAsItIs() {
		String minusE = "-" + Unicode.EULER_STRING;
		numberBeUpdatedAs(minusE, minusE);
	}

	@Test
	public void expressionWithMinusPiShouldStayAsItIs() {
		String piExpression = "-" + Unicode.PI_STRING + "+1";
		numberBeUpdatedAs(piExpression, piExpression);
	}

	@Test
	public void expressionWithMinusEShouldStayAsItIs() {
		addAvInput("a = 0.32");
		GeoInputBox inputBox = addAvInput("b = InputBox(a)");
		inputBox.setSymbolicMode(true);
		inputBox.updateLinkedGeo("-" + Unicode.EULER_STRING + " + 1");
		assertEquals("-" + Unicode.EULER_STRING + " + 1", inputBox.getText());
	}

	@Test
	public void testMultiLetterVariable() {
		add("abc=7");
		addAvInput("f(x)=x");
		GeoInputBox inputBox = addAvInput("b = InputBox(f)");
		inputBox.setSymbolicMode(true);
		inputBox.updateLinkedGeo("x+abc");
		assertTrue(inputBox.hasError());
	}

	@Test
	public void testLetterApostrophesVariable() {
		add("a''''=7");
		addAvInput("f(x)=x");
		GeoInputBox inputBox = addAvInput("b = InputBox(f)");
		inputBox.setSymbolicMode(true);
		inputBox.updateLinkedGeo("x+a''''");
		assertEquals("x + a''''", inputBox.getText());
	}

	@Test
	public void testLetterSubscriptVariable() {
		add("F_{max}=7");
		addAvInput("f(x)=x");
		GeoInputBox inputBox = addAvInput("b = InputBox(f)");
		inputBox.setSymbolicMode(true);
		inputBox.updateLinkedGeo("x+F_{max}");
		assertEquals("x + F_{max}", inputBox.getText());

		add("var_{max}=7");
		inputBox.updateLinkedGeo("x+var_{max}");
		assertTrue(inputBox.hasError());
	}
}
