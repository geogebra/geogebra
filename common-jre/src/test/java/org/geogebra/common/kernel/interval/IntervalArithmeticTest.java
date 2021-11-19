package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalOperands.multiply;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntervalArithmeticTest {

	@Test
	public void testMultiplicationEmpty() {
		assertTrue(multiply(new Interval(), interval(1, 1)).isUndefined());
	}

	@Test
	public void testMultiplicationPositiveWithPositive() {
		assertEquals(interval(2, 6),
				multiply(interval(1, 2), interval(2, 3)));

		assertEquals(interval(4, POSITIVE_INFINITY),
				multiply(interval(1, POSITIVE_INFINITY), interval(4, 6)));

		assertEquals(interval(POSITIVE_INFINITY, POSITIVE_INFINITY),
				multiply(interval(1, POSITIVE_INFINITY),
						interval(POSITIVE_INFINITY, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationPositiveWithNegative() {
		assertEquals(interval(-6, -2),
				multiply(interval(1, 2), interval(-3, -2)));

		assertEquals(interval(NEGATIVE_INFINITY, -2),
				multiply(interval(1, POSITIVE_INFINITY), interval(-3, -2)));
	}

	@Test
	public void testMultiplicationPositiveWithMixed() {
		assertEquals(interval(-4, 6),
				multiply(interval(1, 2), interval(-2, 3)));

		assertEquals(interval(NEGATIVE_INFINITY, POSITIVE_INFINITY),
				multiply(interval(1, POSITIVE_INFINITY), interval(-2, 3)));
	}

	@Test
	public void testMultiplicationPositiveWithZero() {
		assertEquals(interval(0, 0),
				multiply(interval(1, 2), interval(0, 0)));

		assertEquals(interval(0, 0),
				multiply(interval(1, POSITIVE_INFINITY), interval(0, 0)));
	}

	@Test
	public void testMultiplicationNegativeWithPositive() {
		assertEquals(interval(-6, -2),
				multiply(interval(-3, -2), interval(1, 2)));

		assertEquals(interval(NEGATIVE_INFINITY, -2),
				multiply(interval(-3, -2), interval(1, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationNegativeWithNegative() {
		assertEquals(interval(2, 6),
				multiply(interval(-2, -1), interval(-3, -2)));

		assertEquals(interval(4, POSITIVE_INFINITY),
				multiply(interval(NEGATIVE_INFINITY, -1), interval(-6, -4)));

		assertEquals(interval(POSITIVE_INFINITY, POSITIVE_INFINITY),
				multiply(interval(NEGATIVE_INFINITY, -1),
						interval(NEGATIVE_INFINITY, NEGATIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationNegativeWithMixed() {
		assertEquals(interval(-6, 4),
				multiply(interval(-2, -1), interval(-2, 3)));

		assertEquals(IntervalConstants.whole(),
				multiply(interval(NEGATIVE_INFINITY, -1), interval(-2, 3)));
	}

	@Test
	public void testMultiplicationNegativeWithZero() {
		assertEquals(zero(), multiply(interval(-2, -1), zero()));
		assertEquals(zero(), multiply(interval(NEGATIVE_INFINITY, -1), zero()));
	}

	@Test
	public void testMultiplicationMixedWithPositive() {
		assertEquals(interval(-4, 6),
				multiply(interval(-2, 3), interval(1, 2)));

		assertEquals(whole(),
				multiply(interval(-2, 3), interval(1, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationMixedWithNegative() {
		assertEquals(interval(-6, 4),
				multiply(interval(-2, 3), interval(-2, -1)));

		assertEquals(whole(),
				multiply(interval(-2, 3), interval(NEGATIVE_INFINITY, -1)));
	}

	@Test
	public void testMultiplicationMixedWithMixed() {
		assertEquals(interval(-8, 12),
				multiply(interval(-2, 3), interval(-1, 4)));

		assertEquals(whole(),
				multiply(interval(NEGATIVE_INFINITY, 3), interval(-1, POSITIVE_INFINITY)));
	}

	@Test
	public void testMultiplicationMixedWithZero() {
		assertEquals(zero(), multiply(interval(-2, 1), zero()));
		assertEquals(zero(), multiply(interval(NEGATIVE_INFINITY, 1), zero()));
	}

	@Test
	public void testMultiplicationZeroWithAny() {
		assertEquals(zero(), multiply(zero(), interval(-2, 1)));
		assertEquals(zero(), multiply(zero(), interval(-2, -1)));
		assertEquals(zero(), multiply(zero(), interval(1, 2)));
		assertEquals(zero(), multiply(zero(), interval(0, 0)));
	}
	}