package org.geogebra.common.kernel.interval.operators;

import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.geogebra.common.kernel.interval.IntervalHelper.invertedInterval;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.interval.Interval;
import org.junit.Test;

public class SqrtOperandTest {

	private IntervalNodeEvaluator evaluator = new IntervalNodeEvaluator();

	@Test
	public void sqrtPositiveInfinityShouldBePositiveInfinity() {
		assertEquals(interval(Double.POSITIVE_INFINITY),
				evaluator.sqrt(interval(Double.POSITIVE_INFINITY)));
	}

	@Test
	public void sqrtOfZeroShouldBeZero() {
		assertEquals(zero(), evaluator.sqrt(interval(0)));
	}

	@Test
	public void sqrtOfNegativeShouldBeEmpty() {
		assertEquals(undefined(), evaluator.sqrt(interval(-3, -2)));
		assertEquals(undefined(), evaluator.sqrt(interval(-2)));
		assertEquals(undefined(), evaluator.sqrt(interval(Double.NEGATIVE_INFINITY)));
		assertEquals(undefined(), evaluator.sqrt(interval(Double.NEGATIVE_INFINITY, -1E-6)));
	}

	@Test
	public void sqrtOfZeroInverseShouldBePositiveInfinity() {
		assertEquals(undefined(), evaluator.sqrt(evaluator.inverse(zero())));
	}

	@Test
	public void inverseOfNegativeSqrtNegativeXShouldConvergeToNegativeInfinity() {
		Interval x = interval(-3.224503997145689E-14, 0.019999999999967755);
		assertEquals(Double.POSITIVE_INFINITY,
				evaluator.inverse(evaluator.sqrt(x.negative())).getLow(), 0);
	}

	@Test
	public void intervalWithMinusZeroShouldBeUndefined() {
		Interval x = interval(-0.0, 0.019999999999967755);
		assertEquals(undefined(),
				evaluator.inverse(evaluator.sqrt(x.negative()).negative()));
	}

	@Test
	public void minusSqrtInverseShouldBeUndefinedAtZero() {
		assertEquals(undefined(),
				evaluator.multiply(evaluator.inverse(evaluator.sqrt(zero())),
						interval(-1)));
	}

	@Test
	public void minusSqrtInverseShouldBeNegativeInfinityAroundZero() {
		Interval sqrt = evaluator.sqrt(interval(-1E-4, 1E-4));
		Interval inverse = evaluator.inverse(sqrt);
		assertEquals(Double.NEGATIVE_INFINITY,
				evaluator.multiply(inverse,
						interval(-1)).getLow(), 0);
	}

	@Test
	public void minusSqrtInverseOfMinusXShouldBeApproxZeroAroundZero() {
		Interval x = interval(-1E-4, 1E-4);
		Interval sqrt = evaluator.sqrt(evaluator.inverse(x));
		Interval inverse = evaluator.inverse(sqrt);
		assertEquals(interval(-0.01, 0),
				evaluator.multiply(inverse,
						interval(-1)));
	}

	@Test
	public void sqrtTanX() {
		Interval tanPiHalf = evaluator.tan(interval(1.5609788497524344, 1.5707963267949026));
		assertEquals(interval(10.092367961261552, Double.POSITIVE_INFINITY),
				evaluator.sqrt(tanPiHalf));
	}

	@Test
	public void sqrtOfPositiveInvertedInterval() {
		assertEquals(invertedInterval(2, 3), evaluator.sqrt(invertedInterval(4, 9)));
	}

	@Test
	public void sqrtOfMixedInvertedInterval() {
		assertEquals(interval(3, Double.POSITIVE_INFINITY),
				evaluator.sqrt(invertedInterval(-4, 9)));
	}

	@Test
	public void sqrtOfNegativeInvertedInterval() {
		assertEquals(undefined(), evaluator.sqrt(invertedInterval(-4, -9)));
	}

	@Test
	public void sqrtOfSecCscX() {
		Interval interval = interval(-1E-4, 1E-4);
		Interval csc = evaluator.csc(interval);
		Interval sec = evaluator.sec(csc);
		assertEquals(interval(1, Double.POSITIVE_INFINITY), evaluator.sqrt(sec));
	}
}
