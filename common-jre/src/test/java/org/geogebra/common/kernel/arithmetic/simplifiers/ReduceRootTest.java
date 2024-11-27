package org.geogebra.common.kernel.arithmetic.simplifiers;

import org.junit.Test;

public class ReduceRootTest extends BaseSimplifyTest {
	@Override
	protected SimplifyNode getSimplifier() {
		return new ReduceRoot(getKernel());
	}

	@Test
	public void testReducedRoots() {
		shouldSimplify("sqrt(3 + 4)", "sqrt(7)");
		shouldSimplify("sqrt(72)", "6 sqrt(2)");
		shouldSimplify("sqrt(40 + 4*8)", "6 sqrt(2)");
		shouldSimplify("2 + sqrt(3 + 4)", "2 + sqrt(7)");
		shouldSimplify("2 * sqrt(3 + 4)", "2 * sqrt(7)");
		shouldSimplify("14 + 2 * sqrt(3 + 4)", "14 + 2 * sqrt(7)");
		shouldSimplify("3  * sqrt(4)", "6");
		shouldSimplify("1 + 12 + 3  * sqrt(4)", "19");
	}

	@Test
	public void name() {
		shouldSimplify("(-8 + sqrt(4)) / (-2 + sqrt(8))", "(-8 + 2) / (-2 + 2sqrt(2))");
	}
}
