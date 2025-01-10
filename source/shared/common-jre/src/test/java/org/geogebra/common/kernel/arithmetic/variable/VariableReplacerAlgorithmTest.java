package org.geogebra.common.kernel.arithmetic.variable;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.test.TestStringUtil;
import org.geogebra.test.annotation.Issue;
import org.junit.Assert;
import org.junit.Before;
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
		// transformation to x^2 y^3 done on higher level, see ParserTest
		shouldReplaceAs("pixxyyy",
				Unicode.PI_STRING + " * x * x * y * y * y");
	}

	@Test
	@Issue({"WLY-122", "APPS-5781"})
	public void testDecimal() {
		shouldReplaceAs("pi8.1",
				Unicode.PI_STRING + " * 8.1");
		add("C_{0}=3");
		shouldReplaceAs("C_{0}8.1", "C_{0} * 8.1");
	}

	@Test
	public void testIndexProduct() {
		allowMultipleUnassigned();
		add("a_{1} = 4");
		add("b = 2");
		add("b_{1} = 4");
		add("i_{1} = 7");
		shouldReplaceAs("a_{1}b", "a_{1} * b");
		shouldReplaceAs("ba_{1}", "b * a_{1}");
		shouldReplaceAs("a_{1}b_{1}", "a_{1} * b_{1}");
		shouldReplaceAs("c_{1}'a''", "c_{1}' * a''");
		shouldReplaceAs("bi_{1}", "b * i_{1}");
		shouldReplaceAs("2i_{1}", "2 * i_{1}");
	}

	@Test
	public void testIndexProductInputBar() {
		add("b=3");
		add("i_{1} = 7");
		shouldReplaceAs("2i_{1}", "2 * i_{1}");
		shouldReplaceAs("bi_{1}", "b * i_{1}");
		shouldReplaceAs("2i_{2}", "2 * i_{2}");
		shouldReplaceAs("bi_{2}", "b * i_{2}");
		shouldReplaceAs("2pi_{2}", "2 * pi_{2}");
		shouldReplaceAs("bpi_{2}", "b * pi_{2}");
		shouldReplaceAs("2deg_{2}", "2 * deg_{2}");
		shouldReplaceAs("bdeg_{2}", "b * deg_{2}");
	}

	@Test
	public void testIndexProductGreek() {
		allowMultipleUnassigned();
		shouldReplaceAs("E_{m}" + Unicode.omega + "C",
				"E_{m} * " + Unicode.omega + " * C");
	}

	@Test
	public void testFunctionProductsMul() {
		allowMultipleUnassigned();
		shouldReplaceAs("xlnx", "x * ln(x)");
		shouldReplaceAs("xln2x", "x * ln(2 * x)");
		shouldReplaceAs("xsinx", "x * sin(x)");
	}

	static void allowMultipleUnassigned() {
		variableReplacerAlgorithm.setMultipleUnassignedAllowed(true);
	}

	@Test
	public void testConstantMultiplier() {
		shouldReplaceAs("18pisqrt5", "18 * " + Unicode.PI_STRING
			+ " * sqrt(5)");
	}

	@Test
	public void testEmbeddedTrigs() {
		allowMultipleUnassigned();
		shouldReplaceAs("4coscoscosx", "4 * cos(cos(cos(x)))");
	}

	@Test
	public void testTrig() {
		shouldReplaceAs("sinx", "sin(x)");
		shouldReplaceAs("sinxx", "sin(x * x)");
		shouldReplaceAs("sin2", "sin(2)");
		shouldReplaceAs("cos3x", "cos(3 * x)");
		shouldReplaceAs("asinsinpix",
				TestStringUtil.unicode("asind(sin(" + Unicode.PI_STRING + " * x))"));
	}

	@Test
	public void testImaginary() {
		allowMultipleUnassigned();
		shouldReplaceAs("isqrt3", Unicode.IMAGINARY + " * sqrt(3)");
	}

	@Test
	public void testLog() {
		shouldReplaceAs("lnpi", "ln(" + Unicode.PI_STRING + ")");
		shouldReplaceAs("ln" + Unicode.PI_STRING, "ln(" + Unicode.PI_STRING + ")");
		shouldReplaceAs("log_{2}2", "log(2, 2)");
		shouldReplaceAs("log_22", "log(2, 2)");
		shouldReplaceAs("log_{2}xx", "log(2, x * x)");
		shouldReplaceAs("xlog_{2}x", "x * log(2, x)");
	}

	@Test
	public void testParseReverse() {
		shouldReplaceAs("ax", "a * x");
	}

	@Test
	@Issue("APPS-5781")
	public void shouldNotSimplify() {
		add("c=3");
		shouldReplaceAs("c0deg", "(c * 0)" + Unicode.DEGREE_STRING);
		shouldReplaceAs("c1deg", "(c * 1)" + Unicode.DEGREE_STRING);
		shouldReplaceAs("c0c", "c * 0 * c");
		shouldReplaceAs("c1c", "c * 1 * c");
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
		ExpressionValue secondRun = variableReplacerAlgorithm.replace(expression);
		Assert.assertEquals("x", secondRun.toString(StringTemplate.defaultTemplate));
	}
}
