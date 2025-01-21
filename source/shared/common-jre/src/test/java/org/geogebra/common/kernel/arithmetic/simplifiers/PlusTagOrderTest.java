package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.Test;

public class PlusTagOrderTest extends BaseSimplifyTest {

	@Test
	public void name() {
		shouldSimplify("-1 + sqrt(2)", "sqrt(2) - 1");
		shouldSimplify("(-1 + sqrt(2)) / 2", "(sqrt(2) - 1) / 2");
	}

	@Override
	protected Class<? extends SimplifyNode> getSimplifierClass() {
		return PlusTagOrder.class;
	}
}
