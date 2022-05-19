package org.geogebra.common.kernel.interval.evaluation;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.interval.IntervalFunction;
import org.junit.Test;

public class IfEvaluationTest extends BaseUnitTest {

	@Test
	public void testSignum() {
		IntervalFunction function = new IntervalFunction(add("If(x < 0, 0, 1)"));
		assertEquals(zero(), function.evaluate(interval(Double.NEGATIVE_INFINITY,
				0 - Kernel.MAX_PRECISION)));
		assertEquals(one(), function.evaluate(interval(0, Double.POSITIVE_INFINITY)));
	}

	@Test
	public void testIfElse() {
		IntervalFunction function = new IntervalFunction(add("If(x < 0, 2x + 3, 3x)"));
		assertEquals(interval(-1, 1), function.evaluate(interval(-2, -1)));
		assertEquals(interval(3, 6), function.evaluate(interval(1, 2)));
	}
}
