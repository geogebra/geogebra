package org.geogebra.common.kernel.interval.operands;

import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.positiveInfinity;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;
import org.junit.Test;

public class TanTest {
	@Test
	public void testTanAtKTimesPi() {
		assertEquals(zero(), zero().tan());
		assertEquals(zero(), piTimes(1).tan());
		assertEquals(zero(), piTimes(2).tan());
		assertEquals(zero(), piTimes(4).tan());
		assertEquals(zero(), piTimes(99).tan());
	}

	private Interval piTimes(int times) {
		return new Interval(Math.PI * times);
	}

	@Test
	public void repair() {
		assertEquals(one(), interval(-0.7539822368615448, -0.6283185307179531).tan());
	}

	@Test
	public void testTanAtPiHalf() {
		assertEquals(positiveInfinity(), halfOfPiTimes(1).tan());
		assertEquals(positiveInfinity(), halfOfPiTimes(3).tan());
		assertEquals(positiveInfinity(), halfOfPiTimes(5).tan());
	}

	@Test
	public void testTanAtAroundPiHalf() {
		assertEquals(whole(), IntervalConstants.piHalf().tan());
	}

	private Interval halfOfPiTimes(int times) {
		return new Interval((Math.PI / 2) * times);
	}
}
