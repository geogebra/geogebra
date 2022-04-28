package org.geogebra.common.kernel.interval.operands;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.geogebra.common.kernel.interval.IntervalHelper.invertedInterval;
import static org.geogebra.common.kernel.interval.IntervalOperands.inverse;
import static org.geogebra.common.kernel.interval.IntervalOperands.multiply;
import static org.geogebra.common.kernel.interval.IntervalOperands.sqrt;
import static org.geogebra.common.kernel.interval.IntervalOperands.tan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.junit.Test;

public class TanOperandTest {
	@Test
	public void testTanAtKTimesPi() {
		assertEquals(zero(), tan(zero()));
		assertEquals(zero(), tan(piTimes(1)));
		assertEquals(zero(), tan(piTimes(2)));
		assertEquals(zero(), tan(piTimes(4)));
		assertEquals(zero(), tan(piTimes(99)));
	}

	private Interval piTimes(int times) {
		return new Interval(Math.PI * times);
	}

	@Test
	public void testTanAtAroundPiHalf() {
		assertEquals(undefined(), tan(IntervalConstants.piHalf()));
	}

	@Test
	public void testTanAtAroundPiHalfCase2() {
		assertEquals(invertedInterval(-15.894544843864, 15.894544843866603), tan(pi2()));
	}

	@Test
	public void inverseOsSqrtTanXShouldBePositive() {
		Interval x = pi2();
		Interval tan = tan(x);
		Interval sqrt = sqrt(tan);
		Interval result = sqrt.multiplicativeInverse();
		assertTrue(result.toString(), result.isPositiveWithZero());
	}

	@Test
	public void sqrtOfMinusTanXShouldGoInfinite() {
		Interval x = pi2();
		Interval tan = tan(x);
		Interval multiply = multiply(interval(-1), tan);
		Interval result = sqrt(multiply);
		assertEquals(interval(3.9867963133152404, Double.POSITIVE_INFINITY), result);
	}

	private Interval pi2() {
		return interval(1.507964473723106, 1.6336281798666976);
	}

	@Test
	public void inverseOfTanInverseXShouldBeTanX() {
		Interval x = pi2();
		Interval tan = tan(x);
		Interval inverse = inverse(tan);
		Interval result = inverse(inverse);
		assertEquals(tan(pi2()), result);
	}
}