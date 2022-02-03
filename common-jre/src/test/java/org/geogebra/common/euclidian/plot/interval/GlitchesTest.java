package org.geogebra.common.euclidian.plot.interval;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GlitchesTest extends IntervalPlotterCommon {

	public static final int RESET_LINE_ONLY = 1;

	@Test
	public void testZeroDividedBySinXShouldNotContainInfinity() {
		withDefaultScreen();
		withFunction("0/sin(x)");
		assertEquals(0, gp.getLog().stream().filter(e -> Double.isInfinite(e.y)).count());
	}

	@Test
	public void oneDividedByZeroTimeXShouldBeEmpty() {
		withHiResFunction("1/(0x)");
		assertEquals(1, gp.getLog().size());
	}

	@Test
	public void testXInverseMultipliedByZero() {
		withBounds(-1, 1, -8, -8);
		withScreenSize(50, 50);
		withFunction("0(1/x)");
		assertEquals(0, gp.getLog().stream().filter(e -> e.y != 0).count());
	}

	@Test
	public void testZeroDividedByZeroDividedByTanXShouldBeEmpty() {
		withBounds(-1, 1, -8, -8);
		withScreenSize(50, 50);
		withFunction("0/(0/tan(x))");
		assertEquals(RESET_LINE_ONLY, gp.getLog().size());
	}

	@Test
	public void testTanXAtHighZoomIsWhole() {
		withBounds(-1E15, 1E15, -1E15, -1E15);
		withScreenSize(50, 50);
		withFunction("tan(x)");
		assertEquals(101, gp.getLog().size());
	}

	@Test
	public void oneDividedByXDoesNotProduceNans() {
		withDefaultScreen();
		withFunction("1/x");
		assertEquals(0, gp.getLog().stream().filter(e -> Double.isInfinite(e.y)).count());
	}
}
