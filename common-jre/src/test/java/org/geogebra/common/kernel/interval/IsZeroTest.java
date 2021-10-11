package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.PRECISION;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IsZeroTest {

	@Test
	public void zeroSingletonShouldReturnTrue() {
		assertTrue(interval(0).isZero());
	}

	@Test
	public void zeroWithinPrecisionShouldReturnTrue() {
		assertTrue(interval(RMath.next(-PRECISION), RMath.prev(PRECISION)).isZero());
	}
}