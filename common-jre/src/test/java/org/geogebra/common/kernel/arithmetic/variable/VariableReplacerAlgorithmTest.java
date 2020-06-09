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

	private static VariableReplacerAlgorithm variableReplacerAlgorithm;

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
	public void testDecimal() {
		// opposite ordering would be better, part of WLY-98
		shouldReplaceAs("pi8.1",
				  "8.1 * " + Unicode.PI_STRING);
	}

	@Test
	public void testIndexProduct() {
		allowTokenizer();
		add("a_{1} = 4");
		add("b = 2");
		add("b_{1} = 4");
		shouldReplaceAs("a_{1}b", "a_{1} * b");
		shouldReplaceAs("ba_{1}", "b * a_{1}");
		shouldReplaceAs("a_{1}b_{1}", "a_{1} * b_{1}");
	}

	@Ignore
	@Test
	public void testFunctionProducts() {
		shouldReplaceAs("sina", "sin(a)");
	}

	@Test
	public void testFunctionProductsMul() {
		allowTokenizer();
		shouldReplaceAs("xlnx", "x * log(x)");
		shouldReplaceAs("xln2x", "x * log(2 * x)");
		shouldReplaceAs("xsinx", "x * sin(x)");
	}

	static void allowTokenizer() {
		variableReplacerAlgorithm.setTokenizerAllowed(true);
	}

	@Test
	public void testConstantMultiplier() {
	allowTokenizer();
		shouldReplaceAs("18pisqrt5", "18 * " + Unicode.PI_STRING
			+ " * sqrt(5)");
	}

	@Test
	public void testEmbeddedTrigs() {
		allowTokenizer();
		shouldReplaceAs("4coscoscosx", "4 * cos(cos(cos(x)))");
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
	public void testImaginary() {
		allowTokenizer();
		shouldReplaceAs("isqrt3", String.valueOf(Unicode.IMAGINARY) + " * sqrt(3)");
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
