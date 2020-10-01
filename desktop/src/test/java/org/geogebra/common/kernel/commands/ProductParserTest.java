package org.geogebra.common.kernel.commands;

import static org.geogebra.test.TestStringUtil.unicode;

import org.geogebra.common.kernel.arithmetic.variable.TokenizerBaseTest;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class ProductParserTest extends TokenizerBaseTest {

	@Test
	public void testPiRSquare() {
		add("r = 2");
		shouldReparseAs("pir^(2)", Unicode.PI_STRING + " r" + Unicode.SUPERSCRIPT_2);
	}

	@Test
	public void testABCD() {
		add("a=1");
		add("b=2");
		add("c=2");
		add("d=2");
		shouldReparseAs("abcd", "a b c d");
	}

	@Test
	public void testAvarb() {
		withGeos("a", "f(var)", "b");
		shouldReparseAs("avarb", "a var b");
	}

	@Test
	public void testFunctionalVarVar() {
		withGeos("f(var)");
		shouldReparseAs("varvar", unicode("var^2"));
	}

	@Test
	public void testNFunctionalUV() {
		withGeos("f(u, v)");
		shouldReparseAs("uv", "u v");
		shouldReparseAs("vu", "v u");
	}

	@Test
	public void testPir() {
		withGeos("r");
		shouldReparseAs("pir^(2)", Unicode.PI_STRING + " r" + Unicode.SUPERSCRIPT_2);
	}

	@Test
	public void testXPlusBs() {
		withGeos("b");
		shouldReparseAs("x+bb", "x + b b");
		shouldReparseAs("x+bbb", "x + b b b");
		shouldReparseAs("x+bbbb", "x + b b b b");
		shouldReparseAs("x+bbbbbx", "x + b b b b b x");
	}

	@Test
	public void testABX() {
		shouldReparseAs("xab", "x a b");
		shouldReparseAs("x + ab", "x + a b");
		shouldReparseAs("xxxxxxxxxx", "x" + Unicode.SUPERSCRIPT_1 + Unicode.SUPERSCRIPT_0);
		shouldReparseAs("axxxxxxxxxx", "a x" + Unicode.SUPERSCRIPT_1 + Unicode.SUPERSCRIPT_0);
		shouldReparseAs("axaxaxaxax", "a x a x a x a x a x");
	}

	@Test
	public void testAkka() {
		withGeos("a", "k", "aa(x, y)");
		shouldReparseAs("kk", "k k");
		shouldReparseAs("kkk", "k k k");
		shouldReparseAs("kkkk", "k k k k");
		shouldReparseAs("akakak", "a k a k a k");
		shouldReparseAs("akka", "a k k a");
		shouldReparseAs("kkaa", "k k a a");
	}

	@Test
	public void testArctanIntegral() {
		shouldReparseAs("21xarctanx", "21x atand(x)");
	}

	@Test
	public void testCost7() {
		shouldReparseAs("-tcos7t/7", "(-(t cos(7t))) / 7");
	}

	@Test
	public void testNpi7() {
		shouldReparseAs("npi/7", "n " + Unicode.PI_STRING + " / 7");
	}

	@Test
	public void testLnX() {
		shouldReparseAs("xlnx", "x ln(x)");
		shouldReparseAs("xln2x", "x ln(2x)");
	}

	@Test
	public void testLnAbsX() {
		shouldReparseAs("xlnabsx", "x ln(abs(x))");
		shouldReparseAs("x ln abs(x)", "x ln(abs(x))");
		shouldReparseAs("xln abs(x)", "x ln(abs(x))");
		shouldReparseAs("sin(3tln abs(t))", "sin(3t ln(abs(t)))");
	}

	@Test
	public void testC_2Index() {
		shouldReparseAs("c_2e^(7x)", "c_2 " + Unicode.EULER_STRING + "^(7x)");
	}

	@Test
	public void testx4() {
		shouldReparseAs("x4", "x * 4");
	}

	@Test
	public void testk4() {
		shouldReparseAs("k4", "k * 4");
	}

	@Test
	public void testAkakakaaa() {
		withGeos("a", "k", "aa(x)");
		shouldReparseAs("akakakaaa", "a k a k a k a a a");
	}

	@Test
	public void testImaginaryProduct() {
 		shouldReparseAs("i1", String.valueOf(Unicode.IMAGINARY));
	}

	@Test
	public void testPiSqrt() {
		shouldReparseAs("18pisqrt5", "18" + Unicode.PI_STRING + " sqrt(5)");
	}

	@Test
	public void testiSqrt() {
		shouldReparseAs("isqrt5", Unicode.IMAGINARY + " sqrt(5)");
	}

	@Test
	public void testIndex() {
		shouldReparseAs("B_{0}e^(2)", "B_{0} " + Unicode.EULER_STRING + Unicode.SUPERSCRIPT_2);
	}

	@Test
	public void testTangent() {
		shouldReparseAs("2xtan8x", "2x tan(8x)");
	}

	@Test
	public void testFcosThetaSum() {
		shouldReparseAs("Fcosθx+Fsinθy", "F cos(θ x) + F sin(θ y)");
	}

	@Test
	public void testIndexProduct() {
		shouldReparseAs("F_{1}F_{2}", "F_{1} F_{2}");
		shouldReparseAs("F_{1}F_{2}sin" + Unicode.theta_STRING,
				unicode("F_{1} F_{2} sin(@theta)"));
		shouldReparseAs("Gm_{1}m_{2}", "G m_{1} m_{2}");
		shouldReparseAs("Gm_{1}m_{2}d", "G m_{1} m_{2} d");
	}

	@Test
	public void testIndexGreek() {
		shouldReparseAs("f(h,r_{w},r)=hr_{w}", "h r_{w}");
		// prefer undefined over invalid
		shouldReparseAs("f(r_{w}g, g_{w})=hr_{w}g_{w}", "h r_{w} g_{w}");
	}

	private void shouldReparseAs(String original, String parsed) {
		ParserTest.shouldReparseAs(getApp(), original, parsed);
	}
}
