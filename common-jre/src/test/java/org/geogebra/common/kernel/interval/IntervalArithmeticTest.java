package org.geogebra.common.kernel.interval;

import static java.lang.Double.POSITIVE_INFINITY;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IntervalArithmeticTest {

	@Test
	public void testAddition() {
		assertEquals(interval(-2, 2),
				interval(-1, 1).add(interval(-1, 1)));
		assertEquals(interval(-1, POSITIVE_INFINITY),
				interval(-1, POSITIVE_INFINITY).add(interval(0, 1)));
	}

	@Test
	public void testSubtraction() {
		assertEquals(interval(-2, 2),
				interval(-1, 1).subtract(interval(-1, 1)));

		assertEquals(interval(2, 5),
				interval(5, 7).subtract(interval(2, 3)));

		assertEquals(interval(-2, POSITIVE_INFINITY),
				interval(-1, POSITIVE_INFINITY).subtract(interval(0, 1)));
	}
}