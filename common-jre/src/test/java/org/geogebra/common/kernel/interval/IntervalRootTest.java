package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.empty;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntervalRootTest {

	@Test
	public void testSqrtPositive() {
		assertEquals(interval(2, 3), interval(4, 9).sqrt());
	}

	@Test
	public void testSqrtMixed() {
		assertEquals(interval(0, 3), interval(-4, 9).sqrt());
	}

	@Test
	public void testSqrtNegative() {
		assertEquals(empty(), interval(-9, -4).sqrt());
		assertEquals(zero(), interval(0, 0).sqrt());
		assertEquals(interval(0, 1), interval(0, 1).sqrt());
	}

	@Test
	public void testNthRootInNegativeInterval() {
		assertEquals(interval(-2, 2), interval(-8, 8).nRoot(3));
		assertEquals(interval(0.5, Double.POSITIVE_INFINITY),
				interval(-8, 8).nRoot(-3));
	}

	@Test
	public void testNthRoot() {
		assertEquals(empty(), interval(-27, -8).nRoot(-3));
		assertEquals(empty(), interval(-27, -8).nRoot(2));
		assertEquals(interval(0, 3), (interval(-4, 9).nRoot(2)));
		assertEquals(interval(-3, 2), interval(-27, 8).nRoot(3));
		assertEquals(interval(2, 3), interval(4, 9).nRoot(2));
		assertEquals(interval(2, 3), interval(8, 27).nRoot(3));
		assertEquals(interval(2, 2), interval(8, 8).nRoot(3));
	}

	@Test
	public void testNthRootWithInterval() {
		assertEquals(interval(-3, -2), interval(-27, -8).nRoot(interval(3, 3)));
		assertEquals(empty(), interval(-27, -8).nRoot(interval(4, 3)));

	}

	@Test
	public void testSqrtSinUndef() {
		assertTrue(interval(4, 5).sin().sqrt().isEmpty());
	}

	@Test
	public void testNegativeDividedZero() {
		assertEquals(undefined(),  interval(-7, -3).divide(zero()));
	}

	@Test
	public void testPowerOnPositiveFraction() {
		assertEquals(interval(1, 2).sqrt(), interval(1, 2).pow(0.5));
	}
}