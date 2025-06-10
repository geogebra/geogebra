package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.jupiter.api.Test;

public class ExpandAndFactorOutGCDTest extends BaseSimplifyTestSetup {

	@Test
	public void testSimplify() {
		shouldSimplify("(1 + sqrt(2))(1 + sqrt(3))", "1+sqrt(2)+sqrt(3)+sqrt(6)");
		shouldSimplify("(1 - sqrt(2))(1 + sqrt(3))", "1-sqrt(2)+sqrt(3)-sqrt(6)");
		shouldSimplify("(1 - sqrt(2))(1 - sqrt(3))", "1-sqrt(2)-sqrt(3)+sqrt(6)");
		shouldSimplify("(1 + sqrt(2))(1 - sqrt(3))", "1+sqrt(2)-sqrt(3)-sqrt(6)");
		shouldSimplify("(2 + sqrt(5)) (-10 + sqrt(5))", "-8sqrt(5)-15");
	}

	@Test
	public void withCompleteSquare() {
		shouldSimplify("(-8 - sqrt(10))(sqrt(4) + 7)", "-9sqrt(10) - 72");
	}

	@Test
	public void withMinusSigns() {
		shouldSimplify("((-5 + sqrt(2)) (-1 - sqrt(3))) / -2",
				"(5 - sqrt(2) + 5sqrt(3) - sqrt(6))/ - 2");
		shouldSimplify("(-8 + 2sqrt(2))(-2 - sqrt(6))", "-4 (sqrt(2) + sqrt(3) -  2sqrt(6) - 4)");
	}

	@Test
	public void withSameSqrt() {
		shouldSimplify("(-1 + sqrt(3)) * (1 - sqrt(3))", "2sqrt(3) - 4");
	}

	@Test
	void testExpandWithGeos() {
		evaluate("a = -5");
		evaluate("b = 2");
		evaluate("c = -1");
		evaluate("d = 3");
		shouldSimplify("((a + sqrt(b)) (c - sqrt(d))) / -2",
				"(5 - sqrt(2) + 5sqrt(3) - sqrt(6))/ -2");
	}

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return ExpandAndFactorOutGCD.class;
	}
}
