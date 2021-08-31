package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class TanEvaluateTest extends BaseUnitTest {

	@Test
	public void testCscTanXInverse() {
		Interval result = interval(-4.600000000000001, -4.500000000000002)
				.tan().csc().multiplicativeInverse();

		assertTrue(result.getLow() >= -1 && result.getHigh() <= 1);
	}

	@Test
	public void testTanXInverse() {
		Interval tan = interval(IntervalConstants.PI_HALF_LOW,
				IntervalConstants.PI_HALF_HIGH).tan();
		Interval result = tan.multiplicativeInverse();
		assertEquals(IntervalConstants.whole().invert().uninvert(), result);
	}

	@Test
	public void testMinusTanXInverse() {
		Interval tan = interval(IntervalConstants.PI_HALF_LOW,
				IntervalConstants.PI_HALF_HIGH).tan();
		Interval minusTan = tan.multiply(interval(-1));
		Interval result = minusTan.multiplicativeInverse();
		assertEquals(IntervalConstants.whole().invert().uninvert(), result);
	}
}
