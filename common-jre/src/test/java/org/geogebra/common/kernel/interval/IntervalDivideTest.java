package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IntervalDivideTest {

	@Test
	public void dividePositiveSingletonByPositiveSingleton() {
		assertEquals(interval(1), interval(2).divide(interval(2)));
	}

	@Test
	public void dividePositiveIntervalByPositiveSingleton() {
		assertEquals(interval(1), interval(2, 4).divide(interval(2)));
	}


	@Test
	public void divideNegativeSingletonByPositiveSingleton() {
		assertEquals(interval(-1), interval(-2).divide(interval(2)));
	}

	@Test
	public void divideNegativeSingletonByNegativeSingleton() {
		assertEquals(interval(1), interval(-2).divide(interval(-2)));
	}

	@Test
	public void divideNegativeIntervalByPositiveSingleton() {
		assertEquals(interval(1), interval(-3, -2).divide(interval(2)));
	}

	@Test
	public void divideNegativeIntervalByPositiveIntervalLessThanOne() {
		assertEquals(interval(1), interval(-4, -2).divide(interval(2, 4)));
	}

	@Test
	public void testDivisionZeroWithZero() {
		assertEquals(zero(), zero().divide(interval(-1, 1)));
		assertEquals(zero(), zero().divide(interval(-1, 0)));
		assertEquals(zero(), zero().divide(interval(0, 1)));
	}
}
