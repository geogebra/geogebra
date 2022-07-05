package org.geogebra.common.kernel.interval.operators;

import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.geogebra.common.kernel.interval.operators.IntervalOperands.multiply;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.interval.Interval;
import org.junit.Test;

public class IntervalSubtractTest {

	@Test
	public void subtractFromOpenToNegativeInfinity() {
		// Table 5, row 1
		assertEquals(whole(), interval(Double.NEGATIVE_INFINITY, -23.45)
				.subtract(interval(Double.NEGATIVE_INFINITY, 3.45)));
		assertEquals(interval(Double.NEGATIVE_INFINITY, -33.33),
				interval(Double.NEGATIVE_INFINITY, 12.34)
				.subtract(interval(45.67, 56.78)));
		assertEquals(interval(Double.NEGATIVE_INFINITY, -33.33),
				interval(Double.NEGATIVE_INFINITY, 12.34)
				.subtract(interval(45.67, Double.POSITIVE_INFINITY)));
		assertEquals(whole(), interval(Double.NEGATIVE_INFINITY, 12.34).subtract(whole()));
	}

	@Test
	public void subtractFromFiniteInterval() {
		// Table 5, row 2
		assertEquals(interval(-6.8, Double.POSITIVE_INFINITY), interval(-1.2, 3.4)
				.subtract(interval(Double.NEGATIVE_INFINITY, 5.6)));
		assertEquals(interval(-6.8, 11.2), interval(-1.2, 3.4)
				.subtract(interval(-7.8, 5.6)));
		assertEquals(interval(Double.NEGATIVE_INFINITY, 11.2), interval(-1.2, 3.4)
				.subtract(interval(-7.8, Double.POSITIVE_INFINITY)));
		assertEquals(whole(), interval(-1.2, 3.4).subtract(whole()));
	}

	@Test
	public void subtractFromIntervalOpenToPositiveInfinity() {
		// Table 5, row 3.
		assertEquals(interval(-44.44, Double.POSITIVE_INFINITY),
				interval(12.34, Double.POSITIVE_INFINITY)
				.subtract(interval(Double.NEGATIVE_INFINITY, 56.78)));
		assertEquals(interval(-44.44, Double.POSITIVE_INFINITY),
					interval(12.34, Double.POSITIVE_INFINITY)
					.subtract(interval(-99.88, 56.78)));
		assertEquals(whole(), interval(12.34, Double.POSITIVE_INFINITY).subtract(
				interval(-55.67, Double.POSITIVE_INFINITY)));
		assertEquals(whole(), interval(12.34, Double.POSITIVE_INFINITY).subtract(
				whole()));
	}

	@Test
	public void subtractFromWhole() {
		// Table 5, row 4.
		assertEquals(whole(), whole().subtract(interval(Double.NEGATIVE_INFINITY, 12.34)));
		assertEquals(whole(), whole().subtract(interval(12.34, 56.78)));
		assertEquals(whole(), whole().subtract(interval(2.34, Double.POSITIVE_INFINITY)));
		assertEquals(whole(), whole().subtract(whole()));
	}

	@Test
	public void subtractFromInverted() {
		Interval result = invertedInterval(-10, 10).subtract(interval(50, 60));
		assertEquals(invertedInterval(-70, -40), result);
	}

	@Test
	public void compatibilityTestWithAdd() {
		assertEquals(invertedInterval(10, 20).subtract(interval(1, 2)),
				invertedInterval(10, 20)
						.add(interval(-2, -1)));
	}

	@Test
	public void compatibilityTestWithMultiplyAndAdd() {
		assertEquals(invertedInterval(10, 20).subtract(interval(1, 2)),
				invertedInterval(10, 20)
				.add(multiply(interval(-1), interval(1, 2))));
	}
}
