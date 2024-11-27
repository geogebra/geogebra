package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.Test;

public class SimplifyMultiplicationTest extends BaseSimplifyTest {
	@Override
	protected SimplifyNode getSimplifier() {
		return new SimplifyMultiplication(getKernel());
	}

	@Test
	public void testSimplify() {
		shouldSimplify("(1 + sqrt(2))(1 + sqrt(3))", "1 + sqrt(3) + sqrt(6) + sqrt(2)");
		shouldSimplify("(1 + sqrt(2))(1 - sqrt(3))", "1 - sqrt(3) - sqrt(6) + sqrt(2)");
		shouldSimplify("(1 - sqrt(2))(1 + sqrt(3))", "1 + sqrt(3) - sqrt(6) - sqrt(2)");
		shouldSimplify("(1 - sqrt(2))(1 - sqrt(3))", "1 - sqrt(3) + sqrt(6) - sqrt(2)");
		shouldSimplify("(-1 - sqrt(2))(1 - sqrt(3))", "-1 + sqrt(3) + sqrt(6) - sqrt(2)");
		shouldSimplify("(-1 - sqrt(2))(-1 - sqrt(3))", "1 + sqrt(3) + sqrt(6) + sqrt(2)");
		shouldSimplify("(-1 + sqrt(2))(-1 - sqrt(3))",  "1 + sqrt(3) - sqrt(6) - sqrt(2)");
		shouldSimplify("(-1 + sqrt(2))(-1 + sqrt(3))",  "1 - sqrt(3) + sqrt(6) - sqrt(2)");
	}

	@Test
	public void name() {

		shouldSimplify("(-8 - sqrt(10))(sqrt(4) + 7)", "-72 - (2sqrt(10)) - 7sqrt(10)");
		shouldSimplify("(-8 - sqrt(10))(sqrt(4) + 7)", "9(-sqrt(10) - 8)");
	}
}
