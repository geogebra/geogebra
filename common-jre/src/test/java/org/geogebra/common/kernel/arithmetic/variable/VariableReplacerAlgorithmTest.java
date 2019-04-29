package org.geogebra.common.kernel.arithmetic.variable;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.variable.power.Base;
import org.geogebra.test.TestStringUtil;
import org.junit.Assert;
import org.junit.Before;
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
				Unicode.PI_STRING + TestStringUtil.unicode(" x^2 y^3"));
	}

	@Test
	public void testLog() {
		shouldReplaceAs("log_{2}2", "log(2, 2)");
		shouldReplaceAs("log_22", "log(2, 2)");
		testLogWithXSquare();
	}

	private void testLogWithXSquare() {
		String xSquare = TestStringUtil.unicode("x^2");
		String expected = "log(2, " + xSquare + ")";
		shouldReplaceAs("log_{2}xx", expected);
	}

	private void shouldReplaceAs(String in, String out) {
		ExpressionValue replacement = variableReplacerAlgorithm.replace(in);
		Assert.assertEquals(out,
				replacement.toString(StringTemplate.defaultTemplate));
	}

	@Test
	public void testReuseInstance() {
		String expression = "x";
		variableReplacerAlgorithm.replace(expression);
		variableReplacerAlgorithm.replace(expression);
		int powerOfX = variableReplacerAlgorithm.getExponents().get(Base.x);
		Assert.assertEquals(1, powerOfX);
	}
}
