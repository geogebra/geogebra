package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IntervalDivisionTest {

	@Test
	public void testDivisionPositiveWithZero() {
		assertEquals(undefined(), interval(1, 2).divide(interval(-1, 1)));
		assertEquals(undefined(), interval(0, 2).divide(interval(0, 1)));
		assertEquals(undefined(), interval(0, 2).divide(interval(-1, 0)));
		assertEquals(interval(1, Double.POSITIVE_INFINITY),
				interval(1, 2).divide(interval(0, 1)));
		assertEquals(interval(NEGATIVE_INFINITY, -1),
				interval(1, 2).divide(interval(-1, 0)));
	}

	@Test
	public void testDivisionNegativeWithZero() {
		assertEquals(undefined(), interval(-2, -1).divide(interval(-1, 1)));
		assertEquals(undefined(), interval(-2, 0).divide(interval(0, 1)));
		assertEquals(undefined(), interval(-2, 0).divide(interval(-1, 0)));
		assertEquals(interval(NEGATIVE_INFINITY, -1),
				interval(-2, -1).divide(interval(0, 1)));
		assertEquals(interval(NEGATIVE_INFINITY, -1),
				interval(-2, -1).divide(interval(0, 1)));
	}

	@Test
	public void testDivisionMixedWithZero() {
		assertEquals(undefined(), interval(-2, 3).divide(interval(-1, 1)));
	}

	@Test
	public void testDivisionZeroWithZero() {
		assertEquals(zero(), zero().divide(interval(-1, 1)));
		assertEquals(zero(), zero().divide(interval(-1, 0)));
		assertEquals(zero(), zero().divide(interval(0, 1)));
	}

	@Test
	public void testDivisionWithoutZero() {
		assertEquals(interval(1 / 4.0, 2 / 3.0),
				interval(1, 2).divide(interval(3, 4)));

		assertEquals(interval(0, 2 / 3.0),
				interval(1, 2).divide(interval(3, POSITIVE_INFINITY)));

		assertEquals(interval(0, POSITIVE_INFINITY),
				interval(1, POSITIVE_INFINITY).divide(interval(3, POSITIVE_INFINITY)));

		assertEquals(interval(1 / 4.0, 2 / 3.0),
				interval(-2, -1).divide(interval(-4, -3)));

		assertEquals(interval(0, 2 / 3.0),
				interval(-2, -1).divide(interval(NEGATIVE_INFINITY, -3)));

		assertEquals(interval(-Double.MIN_VALUE, POSITIVE_INFINITY),
				interval(NEGATIVE_INFINITY, -1).divide(interval(NEGATIVE_INFINITY, -3)));

		assertEquals(interval(-2 / 3.0, -1 / 4.0),
				interval(-2, -1).divide(interval(3, 4)));

		assertEquals(interval(-2 / 3.0, 1 / 3.0),
				interval(-2, 1).divide(interval(3, 4)));

		assertEquals(interval(-1 / 3.0, 2 / 3.0),
				interval(-2, 1).divide(interval(-4, -3)));

		assertEquals(interval(1 / 4.0, 2 / 3.0),
				interval(1, 2).divide(interval(3, 4)));

		assertEquals(interval(-2 / 3.0, -1 / 4.0),
				interval(1, 2).divide(interval(-4, -3)));
	}
}
