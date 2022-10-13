package org.geogebra.common.kernel.interval.operators;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.around;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.geogebra.common.kernel.interval.IntervalHelper.invertedInterval;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.divide;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.inverse;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.multiply;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.sqrt;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.tan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.junit.Test;

public class TanOperandTest {

	@Test
	public void testTanAtKTimesPi() {
		assertEquals(zero(), tan(zero()));
		assertTrue(tan(piTimes(1)).almostEqual(zero(), 1E-11));
		assertTrue(tan(piTimes(2)).almostEqual(zero(), 1E-11));
		assertTrue(tan(piTimes(4)).almostEqual(zero(), 1E-11));
		assertTrue(tan(piTimes(99)).almostEqual(zero(), 1E-11));
	}

	private Interval piTimes(int times) {
		return around(Math.PI * times);
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

	@Test
	public void tanDivOneShouldEqualTanTimesOne() {
		Interval multiplyOne = multiply(tan(around(Math.PI / 2)), one());
		Interval divideOne = divide(tan(around(Math.PI / 2)), one());
		assertEquals(divideOne, multiplyOne);
	}
}