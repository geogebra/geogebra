package org.geogebra.common.kernel.interval.operands;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.geogebra.common.kernel.interval.IntervalHelper.invertedInterval;
import static org.geogebra.common.kernel.interval.IntervalOperands.csc;
import static org.geogebra.common.kernel.interval.IntervalOperands.sec;
import static org.geogebra.common.kernel.interval.IntervalOperands.sqrt;
import static org.geogebra.common.kernel.interval.IntervalOperands.tan;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalOperands;
import org.junit.Test;

public class SqrtOperandTest {

	@Test
	public void sqrtPositiveInfinityShouldBePositiveInfinity() {
		assertEquals(interval(Double.POSITIVE_INFINITY), sqrt(interval(Double.POSITIVE_INFINITY)));
	}

	@Test
	public void sqrtOfZeroShouldBeZero() {
		assertEquals(zero(), sqrt(interval(0)));
	}

	@Test
	public void sqrtOfNegativeShouldBeEmpty() {
		assertEquals(undefined(), sqrt(interval(-3, -2)));
		assertEquals(undefined(), sqrt(interval(-2)));
		assertEquals(undefined(), sqrt(interval(Double.NEGATIVE_INFINITY)));
		assertEquals(undefined(), sqrt(interval(Double.NEGATIVE_INFINITY, -1E-6)));
	}

	@Test
	public void sqrtOfZeroInverseShouldBePositiveInfinity() {
		assertEquals(undefined(), sqrt(zero().multiplicativeInverse()));
	}

	@Test
	public void intervalWithMinusLowShouldBeUndefined() {
		Interval x = interval(-3.224503997145689E-14, 0.019999999999967755);
		assertEquals(undefined(),
				sqrt(x.negative()).negative().multiplicativeInverse());
	}

	@Test
	public void intervalWithMinusZeroShouldBeUndefined() {
		Interval x = interval(-0.0, 0.019999999999967755);
		assertEquals(undefined(),
				sqrt(x.negative()).negative().multiplicativeInverse());
	}

	@Test
	public void minusSqrtInverseShouldBeUndefinedAtZero() {
		assertEquals(undefined(),
				IntervalOperands.multiply(sqrt(zero()).multiplicativeInverse(),
						interval(-1)));
	}

	@Test
	public void minusSqrtInverseShouldBeNegativeInfinityAroundZero() {
		Interval sqrt = sqrt(interval(-1E-4, 1E-4));
		Interval multiplicativeInverse = sqrt
				.multiplicativeInverse();
		assertEquals(Double.NEGATIVE_INFINITY,
				IntervalOperands.multiply(multiplicativeInverse,
						interval(-1)).getLow(), 0);
	}

	@Test
	public void minusSqrtInverseOfMinusXShouldBeAproxZeroAroundZero() {
		Interval x = interval(-1E-4, 1E-4);
		Interval sqrt = sqrt(x.multiplicativeInverse());
		Interval multiplicativeInverse = sqrt
				.multiplicativeInverse();
		assertEquals(interval(-0.01, 0),
				IntervalOperands.multiply(multiplicativeInverse,
						interval(-1)));
	}

	@Test
	public void sqrtTanX() {
		Interval tanPiHalf = tan(interval(1.5609788497524344, 1.5707963267949026));
		assertEquals(interval(10.092367961261552, Double.POSITIVE_INFINITY), sqrt(tanPiHalf));
	}

	@Test
	public void sqrtOfPositiveInvertedInterval() {
		assertEquals(invertedInterval(2, 3), sqrt(invertedInterval(4,  9)));
	}

	@Test
	public void sqrtOfMixedInvertedInterval() {
		assertEquals(interval(3, Double.POSITIVE_INFINITY), sqrt(invertedInterval(-4,  9)));
	}

	@Test
	public void sqrtOfNegativeInvertedInterval() {
		assertEquals(undefined(), sqrt(invertedInterval(-4,  -9)));
	}

	@Test
	public void sqrtOfSecCscX() {
		Interval interval = interval(-1E-4, 1E-4);
		Interval csc = csc(interval);
		Interval sec = sec(csc);
		assertEquals(interval(1, Double.POSITIVE_INFINITY), sqrt(sec));
	}
}
