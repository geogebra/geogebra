package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.jupiter.api.Test;

public class PlusTagOrderTest extends BaseSimplifyTestSetup {

	@Test
	public void testApply() {
		shouldSimplify("-1 + sqrt(2)", "sqrt(2) - 1");
		shouldSimplify("(-1 + sqrt(2)) / 2", "(sqrt(2) - 1) / 2");
	}

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return PlusTagOrder.class;
	}
}
