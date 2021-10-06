package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.empty;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.wholeR;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalConstants.zeroWithNegativeSign;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.geogebra.common.kernel.interval.IntervalTest.invertedInterval;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IntervalMultiplicativeInverseTest {

	@Test
	public void inverseOfEmptyIntervalShouldBeEmpty() {
		assertEquals(empty(), empty().multiplicativeInverse());
	}

	@Test
	public void inverseOfInverseOfEmptyIntervalShouldBeEmpty() {
		assertEquals(empty(), empty().multiplicativeInverse().multiplicativeInverse());
	}

	@Test
	public void inverseOfUndefined() {
		assertEquals(undefined(), undefined().multiplicativeInverse());
	}

	@Test
	public void testInverseWithBoundsZeroAndPositive() {
		assertEquals(interval(RMath.divLow(1, 4), Double.POSITIVE_INFINITY),
				interval(0, 4).multiplicativeInverse());
	}

	@Test
	public void inverseOfWholeShouldBeZero() {
		assertEquals(zero(), wholeR().multiplicativeInverse());
	}

		@Test
	public void inverseOfInfinityShouldBeZero() {
		assertEquals(zero(), interval(Double.POSITIVE_INFINITY).multiplicativeInverse());
		assertEquals(zeroWithNegativeSign(),
				interval(Double.NEGATIVE_INFINITY).multiplicativeInverse());
	}

	@Test
	public void inverseOfInverseOfInfinityShouldBeInfinity() {
		assertEquals(interval(Double.POSITIVE_INFINITY),
				interval(Double.POSITIVE_INFINITY).multiplicativeInverse().multiplicativeInverse());
	}

	@Test
	public void inverseOfInverseOfNegativeInfinityShouldBeNegativeInfinity() {
		assertEquals(interval(Double.NEGATIVE_INFINITY),
 				interval(Double.NEGATIVE_INFINITY).multiplicativeInverse()
						.multiplicativeInverse());
	}

	@Test
	public void testInverseWithBoundsNegativeAndZero() {
		assertEquals(interval(Double.NEGATIVE_INFINITY, RMath.divHigh(1, -5)),
				interval(-5, 0).multiplicativeInverse());
	}

	@Test
	public void testInverseWithZeroWithinBounds() {
		assertEquals(invertedInterval(RMath.divLow(1, -4), RMath.divHigh(1, 3)),
				interval(-4, 3).multiplicativeInverse());
	}

	@Test
	public void testInverseWithPositiveBounds() {
		assertEquals(interval(RMath.divLow(1, 4), RMath.divHigh(1, 3)),
				interval(3, 4).multiplicativeInverse());
	}
}