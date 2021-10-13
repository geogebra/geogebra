package org.geogebra.common.kernel.interval.operands;

import static org.geogebra.common.kernel.interval.IntervalConstants.positiveInfinity;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.interval.Interval;
import org.junit.Test;

public class TanTest {
	@Test
	public void testTanAtKTimesPi() {
		assertEquals(zero(), zero().tan());
		assertEquals(zero() , piTimes(1).tan());
		assertEquals(zero() , piTimes(2).tan());
		assertEquals(zero() , piTimes(4).tan());
		assertEquals(zero() , piTimes(99).tan());
	}

	private Interval piTimes(int times) {
		return new Interval(Math.PI * times);
	}

	@Test
	public void testTanAtPiHalf() {
		assertEquals(positiveInfinity(), halfOfPiTimes(1).tan());
		assertEquals(positiveInfinity(), halfOfPiTimes(-1).tan());
	}

	private Interval halfOfPiTimes(int times) {
		return new Interval((Math.PI / 2) * times);
	}
}
