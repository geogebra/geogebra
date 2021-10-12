package org.geogebra.common.kernel.interval.operands;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.interval.IntervalConstants;
import org.junit.Test;

public class CotTest {
	@Test
	public void testCotAtPiHalf() {
		assertEquals(IntervalConstants.empty(), IntervalConstants.piHalf().cot());
	}
}
