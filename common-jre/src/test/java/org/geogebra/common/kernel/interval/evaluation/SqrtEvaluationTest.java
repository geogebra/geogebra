package org.geogebra.common.kernel.interval.evaluation;

import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
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
}
