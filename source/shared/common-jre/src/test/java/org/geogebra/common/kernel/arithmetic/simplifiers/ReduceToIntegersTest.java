package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.Test;

public class ReduceToIntegersTest extends BaseSimplifyTest {

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return ReduceToIntegers.class;
	}

	@Test
	public void testReduceToIntegers() {
		shouldSimplify("sqrt(4)sqrt(2)", "2sqrt(2)");
		shouldSimplify("(-(-2) + sqrt(5)) (-10 + sqrt(5))", "(2 + sqrt(5)) (-10 + sqrt(5))");
		shouldSimplify("((-2 - sqrt(8)) (-6)) / -4", "((-2 - sqrt(8)) (-6)) / -4");
	}

	@Test
	public void testIgnoreZeros() {
		shouldSimplify("(0 + sqrt(3) ) ( 4 - sqrt(5) ) / 11", "sqrt(3) (4 - sqrt(5)) / 11");
		shouldSimplify("(2 + sqrt(0) ) ( 4 - sqrt(5) ) / 11", "2 (4 - sqrt(5)) / 11");
		shouldSimplify("(2 + sqrt(3) ) ( 0 - sqrt(5) ) / 11", "(2 + sqrt(3))(-sqrt(5)) / 11");
		shouldSimplify("(2 + sqrt(3) ) ( 4 - sqrt(0) ) / 11", "(2 + sqrt(3))(4) / 11");
		shouldSimplify("0/sqrt(2)", "0");
		shouldSimplify("sqrt(2)/0", "\u221e");
		shouldSimplify("0/0", "-\u221e");
		shouldSimplify("(2 + sqrt(3) ) ( 0 - sqrt(5) ) / 11", "(2 + sqrt(3))(-sqrt(5)) / 11");
	}

	@Test
	public void testShouldNotChange() {
		shouldSimplify("(-(2sqrt(2)) - 2) / 4", "(-(2sqrt(2)) - 2) / 4");
		shouldSimplify("(-1 + sqrt(2)) sqrt(5)", "(-1 + sqrt(2)) sqrt(5)");
	}
}
