package org.geogebra.common.euclidian.plot.interval;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.util.DoubleUtil;
import org.junit.Test;

public class GlitchesTest extends IntervalPlotterCommon {

	@Test
	public void testZeroDividedBySinXShouldNotContainInfinity() {
		withDefaultScreen();
		withFunction("0/sin(x)");
		assertEquals(0, gp.getLog().stream().filter(e -> Double.isInfinite(e.y)).count());
	}

	@Test
	public void oneDividedByZeroTimeXShouldBeEmpty() {
		withHiResFunction("1/(0x)");
		assertEquals(0, gp.getLog().size());
	}

	@Test
	public void testXInverseMultipliedByZero() {
		withBounds(-1, 1, -8, -8);
		withScreenSize(50, 50);
		withFunction("0(1/x)");
		assertEquals(101, gp.getLog().stream().filter(e -> DoubleUtil.isEqual(e.y, 0))
				.count());
	}

	@Test
	public void testZeroDividedByZeroDividedByTanXShouldBeEmpty() {
		withBounds(-1, 1, -8, -8);
		withScreenSize(50, 50);
		withFunction("0/(0/tan(x))");
		assertEquals(0, gp.getLog().size());
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
