package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.piHalf;
import static org.geogebra.common.kernel.interval.IntervalConstants.wholeR;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TanTest {
	@Test
	public void tanAroundPiHalfShouldBeWholeInverted() {
		assertEquals(wholeR().invert(), around(piHalf()).tan().multiplicativeInverse());
	}

	private Interval around(Interval val) {
		return interval(val.getLow() - 1E-6, val.getHigh() + 1E-6);
	}

	@Test
	public void tanPiHalfShouldBeInverted() {
		assertEquals(zero(), piHalf().tan().multiplicativeInverse());
	}
}
