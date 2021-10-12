package org.geogebra.common.kernel.interval.evaluation;

import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.around;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.geogebra.common.kernel.interval.IntervalFunction;
import org.junit.Test;

public class SqrtEvaluationTest extends BaseUnitTest {

	@Test
	public void testSqrtX() {
		IntervalFunction function = new IntervalFunction(add("sqrt(x)"));
		try {
			assertEquals(zero(), function.evaluate(zero()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNoWholeInInverseOfMinusSqrtMinusX() {
		IntervalFunction function = new IntervalFunction(add("1/-sqrt(-x)"));
		for (double t = -5; t < 1; t += IntervalConstants.PRECISION) {
			try {
				assertFalse(function.evaluate(around(t)).isWhole());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
