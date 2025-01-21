package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.Test;

public class PositiveDenominatorTests extends BaseSimplifyTest {

	@Test
	public void testApply() {
		shouldSimplify("(2 (sqrt(2) - 1)) / -5", "(-2 (sqrt(2) - 1)) / 5");
		shouldSimplify("(3+sqrt(2)) / -5", "-(3+sqrt(2)) / 5");
		shouldSimplify("-(3+sqrt(2)) / -5", "(3+sqrt(2)) / 5");
		shouldSimplify("-7 (3+sqrt(2)) / -5", "7 (3+sqrt(2)) / 5");
		shouldSimplify("7 (3+sqrt(2)) / -5", "-7 (3+sqrt(2)) / 5");
	}

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return PositiveDenominator.class;
	}
}
