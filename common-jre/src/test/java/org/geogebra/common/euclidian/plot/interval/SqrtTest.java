package org.geogebra.common.euclidian.plot.interval;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SqrtTest extends IntervalPlotterCommon {

	@Test
	public void testSqrtSinInverseInverse() {
		withBounds(-20, 20, -18, 18);
		withScreenSize(300, 100);
		withFunction("sqrt(1/sin(1/x))");
		assertTrue(false);
	}

	@Test
	public void testSqrtSinInverseInverseHiRes() {
		withBounds(-20, 20, -18, 18);
		withHiResFunction("sqrt(1/sin(1/x))");
		assertTrue(false);
	}

	@Test
	public void tesMinusSqrtOfXInverse() {
		withBounds(-1, 12, -10, 10);
		withScreenSize(100, 100);
		withFunction("-sqrt(1/x)");
	}
}
