package org.geogebra.common.kernel.arithmetic.variable;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.variable.power.Base;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class VariableReplacerAlgorithmTest extends BaseUnitTest {

	private VariableReplacerAlgorithm variableReplacerAlgorithm;

	@Before
	public void setup() {
		super.setup();
		variableReplacerAlgorithm = new VariableReplacerAlgorithm(getKernel());
	}

	@Test
	public void testLog() {
		ExpressionValue replacement = variableReplacerAlgorithm.replace("log_{2}2");
		Assert.assertEquals("log(2, 2)", replacement.toString());
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
