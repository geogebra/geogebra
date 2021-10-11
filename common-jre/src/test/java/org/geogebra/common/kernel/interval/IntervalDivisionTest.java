package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalOperands.divide;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IntervalDivisionTest {

	@Test
	public void testDivisionPositiveWithZero() {
		assertEquals(undefined(), divide(interval(1, 2), interval(-1, 1)));
		assertEquals(undefined(), divide(interval(0, 2), interval(0, 1)));
		assertEquals(undefined(), divide(interval(0, 2), interval(-1, 0)));
		assertEquals(interval(1, Double.POSITIVE_INFINITY),
				divide(interval(1, 2), interval(0, 1)));
		assertEquals(interval(NEGATIVE_INFINITY, -1),
				divide(interval(1, 2), interval(-1, 0)));
	}

	@Test
	public void testDivisionNegativeWithZero() {
		assertEquals(undefined(), divide(interval(-2, -1), interval(-1, 1)));
		assertEquals(undefined(), divide(interval(-2, 0), interval(0, 1)));
		assertEquals(undefined(), divide(interval(-2, 0), interval(-1, 0)));
		assertEquals(interval(NEGATIVE_INFINITY, -1),
				divide(interval(-2, -1), interval(0, 1)));
		assertEquals(interval(NEGATIVE_INFINITY, -1),
				divide(interval(-2, -1), interval(0, 1)));
	}

	@Test
	public void testDivisionMixedWithZero() {
		assertEquals(undefined(), divide(interval(-2, 3), interval(-1, 1)));
	}

	@Test
	public void testDivisionZeroWithZero() {
		assertEquals(zero(), divide(zero(), interval(-1, 1)));
		assertEquals(zero(), divide(zero(), interval(-1, 0)));
		assertEquals(zero(), divide(zero(), interval(0, 1)));
	}

	@Test
	public void testDivisionWithoutZero() {
		assertEquals(interval(1 / 4.0, 2 / 3.0),
				divide(interval(1, 2), interval(3, 4)));

		assertEquals(interval(0, 2 / 3.0),
				divide(interval(1, 2), interval(3, POSITIVE_INFINITY)));

		assertEquals(interval(0, POSITIVE_INFINITY),
				divide(interval(1, POSITIVE_INFINITY), interval(3, POSITIVE_INFINITY)));

		assertEquals(interval(1 / 4.0, 2 / 3.0),
				divide(interval(-2, -1), interval(-4, -3)));

		assertEquals(interval(0, 2 / 3.0),
				divide(interval(-2, -1), interval(NEGATIVE_INFINITY, -3)));

		assertEquals(interval(-Double.MIN_VALUE, POSITIVE_INFINITY),
				divide(interval(NEGATIVE_INFINITY, -1), interval(NEGATIVE_INFINITY, -3)));

		assertEquals(interval(-2 / 3.0, -1 / 4.0),
				divide(interval(-2, -1), interval(3, 4)));

		assertEquals(interval(-2 / 3.0, 1 / 3.0),
				divide(interval(-2, 1), interval(3, 4)));

		assertEquals(interval(-1 / 3.0, 2 / 3.0),
				divide(interval(-2, 1), interval(-4, -3)));

		assertEquals(interval(1 / 4.0, 2 / 3.0),
				divide(interval(1, 2), interval(3, 4)));

		assertEquals(interval(-2 / 3.0, -1 / 4.0),
				divide(interval(1, 2), interval(-4, -3)));
	}
}
