package org.geogebra.common.kernel.interval.evaluation;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.around;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.interval.IntervalFunction;
import org.junit.Test;

public class SqrtEvaluationTest extends BaseUnitTest {

	@Test
	public void testSqrtX() {
		IntervalFunction function = new IntervalFunction(add("sqrt(x)"));
		assertEquals(zero(), function.evaluate(zero()));
	}

	@Test
	public void testNoWholeInInverseOfMinusSqrtMinusX() {
		IntervalFunction function = new IntervalFunction(add("1/-sqrt(-x)"));
		for (double t = -5; t < 1; t += 1E-4) {
			assertFalse(function.evaluate(around(t)).isWhole());
		}
	}

	@Test
	public void testSqrtXInverseEmptyOnNegative() {
		IntervalFunction function = new IntervalFunction(add("1/sqrt(x)"));
		assertEquals(undefined(), function.evaluate(interval(-2, -1)));
	}

	@Test
	public void testNegativeOfSqrtXInverse() {
		IntervalFunction function = new IntervalFunction(add("-(1/sqrt(x))"));
		assertEquals(interval(Double.NEGATIVE_INFINITY, -100.0),
				function.evaluate(interval(-1E-4, 1E-4)));
	}
}
