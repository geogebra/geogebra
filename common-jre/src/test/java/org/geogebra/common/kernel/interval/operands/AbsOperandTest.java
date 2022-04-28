package org.geogebra.common.kernel.interval.operands;

import static org.geogebra.common.kernel.interval.IntervalConstants.negativeInfinity;
import static org.geogebra.common.kernel.interval.IntervalConstants.positiveInfinity;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.whole;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.geogebra.common.kernel.interval.IntervalHelper.invertedInterval;
import static org.geogebra.common.kernel.interval.IntervalOperands.abs;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AbsOperandTest {

	@Test
	public void testPositiveIntervals() {
		assertEquals(interval(0, 1E12), abs(interval(0, 1E12)));
	}

	@Test
	public void testNegativeIntervals() {
		assertEquals(interval(1E-234, 1E234), abs(interval(-1E234, -1E-234)));
	}

	@Test
	public void testZero() {
		assertEquals(zero(), abs(zero()));
	}

	@Test
	public void testUndefined() {
		assertEquals(undefined(), abs(undefined()));
	}

	@Test
	public void testWhole() {
		assertEquals(interval(0, Double.POSITIVE_INFINITY), abs(whole()));
	}

	@Test
	public void testNegativeInfinity() {
		assertEquals(positiveInfinity(), abs(negativeInfinity()));
	}

	@Test
	public void testMixedInvertedIntervals() {
		assertEquals(interval(100, Double.POSITIVE_INFINITY), abs(invertedInterval(-100, 100)));
	}

	@Test
	public void testNegativeInvertedIntervals() {
		assertEquals(interval(0, Double.POSITIVE_INFINITY),
				abs(invertedInterval(-200, -100)));
	}
}
