package org.geogebra.common.kernel.interval;

import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.piHalf;
import static org.geogebra.common.kernel.interval.IntervalConstants.positiveInfinity;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SqrtTest {

	@Test
	public void sqrtOfSqrtXAtZeroShouldBeZero() {
		// sqrt(sqrt([0])) == 0
		Interval inner = zero().sqrt();
		Interval outer = inner.sqrt();
		assertEquals(zero(), outer);
	}

	@Test
	public void sqrtOfSqrtXAroundZeroShouldBeZero() {
		// sqrt(sqrt([low..0..high])) == 0
		Interval inner = interval(-1E-6, 1E-6).sqrt();
		Interval outer = inner.sqrt();
		assertEquals(zero(), outer);
	}

	@Test
	public void sqrtOfXInverseShouldBePositiveInfinityAtZero() {
		// 1/sqrt([0]) == ∞
		assertEquals(positiveInfinity(), zero().multiplicativeInverse());
	}

	@Test
	public void testSqrtSecCotX() {
		Interval cot = piHalf().cot();
		Interval sec = cot.sec();
		Interval result = sec.sqrt();
		assertEquals(IntervalConstants.one(), result);
	}

	@Test
	public void testSqrtSqrtOfXInverse() {
		Interval result =
				interval(0).sqrt().sqrt();
		Interval inverse = result.multiplicativeInverse();
		assertEquals(interval(POSITIVE_INFINITY), inverse);
	}
	@Test
	public void testSqrtInveredWhole() {
		assertEquals(undefined(), whole().invert().sqrt());
	}

	@Test
	public void testSqrtTanX() {
		assertEquals(undefined(), piHalf().tan().sqrt());
	}

}
