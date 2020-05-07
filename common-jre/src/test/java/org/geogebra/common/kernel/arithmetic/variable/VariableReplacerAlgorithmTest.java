package org.geogebra.common.kernel.arithmetic.variable;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.test.TestStringUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class VariableReplacerAlgorithmTest extends BaseUnitTest {

	private VariableReplacerAlgorithm variableReplacerAlgorithm;

	@Before
	public void setupTest() {
		variableReplacerAlgorithm = new VariableReplacerAlgorithm(getKernel());
	}

	@Test
	public void testPower() {
		shouldReplaceAs("pixxyyy",
				Unicode.PI_STRING + " * x^(2) * y^(3)");
	}

	@Test
	public void testIndexProduct() {
		add("a_{1} = 4");
		add("b = 2");
		add("b_{1} = 4");
		shouldReplaceAs("a_{1}b","a_{1} * b");
		shouldReplaceAs("ba_{1}","b * a_{1}");
		shouldReplaceAs("a_{1}b_{1}","a_{1} * b_{1}");
	}

	@Test
	public void testFunctionProducts() {
		add("s=5");
		add("i=5");
		add("n=5");
		add("a=5");
		add("b=3");
		shouldReplaceAs("sina", "sin(a)");
	}

	@Test
	public void testFunctionProductsMul() {
		shouldReplaceAs("xlnx", "x * log(x)");
		shouldReplaceAs("xln2x", "x * log(2 * x)");
		shouldReplaceAs("xsinx", "x * sin(x)");
	}

	@Test
	public void testConstantMultiplier() {
//		shouldReplaceAs("8sqrt(x)", "8 * sqrt(x)");
//		shouldReplaceAs("isqrt3", "i * sqrt(3)");
		shouldReplaceAs("18pisqrt5", "18 * " + Unicode.PI_STRING
			+ " * sqrt(5)");
	}

	@Test
	public void testAvarb() {
		add("a=1");
		add("b=1");
		add("f(var)=?");
		add("t(mul, var)=?");
		shouldReplaceAs("avarb", "a * var * b");
		shouldReplaceAs("amulvarb", "a * mul * var * b");
	}

	@Ignore
	@Test
	public void testConstantMultiplierWithBrackets() {
		shouldReplaceAs("4xsin(4x)", "4 * x * sin(4 * x)");
	}

	@Test
	public void testEmbeddedTrigs() {
		shouldReplaceAs("4coscoscosx", "4 * cos(cos(cos(x)))");
	}

	@Test
	public void testArctan() {
		shouldReplaceAs("21arctan2x", "21 * atand(2 * x)");
	}

	@Test
	public void testPiRSquare() {
		add("a = 1");
		shouldReplaceAs("ar^(2)", "a * r^(2)");
		shouldReplaceAs("2pir^(2)", "2 * " + Unicode.PI_STRING + " * r^(2)");
	}

	@Test
	public void testTrig() {
		shouldReplaceAs("sinx", "sin(x)");
		shouldReplaceAs("sinxx", "sin(x^(2))");
		shouldReplaceAs("sin2", "sin(2)");
		shouldReplaceAs("cos3x", "cos(3 * x)");
		shouldReplaceAs("asinsinpix",
				TestStringUtil.unicode("asind(sin(" + Unicode.PI_STRING + " * x))"));
	}

	@Test
	public void testLog() {
		shouldReplaceAs("lnpi", "log(" + Unicode.PI_STRING + ")");
		shouldReplaceAs("ln" + Unicode.PI_STRING, "log(" + Unicode.PI_STRING + ")");
		shouldReplaceAs("log_{2}2", "log(2, 2)");
		shouldReplaceAs("log_22", "log(2, 2)");
		shouldReplaceAs("log_{2}xx", "log(2, x^(2))");
	}

	private void shouldReplaceAs(String in, String out) {
		ExpressionValue replacement = variableReplacerAlgorithm.replace(in);
		Assert.assertEquals(out,
				replacement.toString(StringTemplate.testTemplate));
	}

	@Test
	public void testReuseInstance() {
		String expression = "x";
		variableReplacerAlgorithm.replace(expression);
		variableReplacerAlgorithm.replace(expression);
		int powerOfX = variableReplacerAlgorithm.getExponents().get("x");
		Assert.assertEquals(1, powerOfX);
	}
}
