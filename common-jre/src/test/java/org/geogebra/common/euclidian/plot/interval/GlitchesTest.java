package org.geogebra.common.euclidian.plot.interval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.util.DoubleUtil;
import org.junit.Test;

public class GlitchesTest extends BaseUnitTest {

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

	@Test
	public void testSignum() {
		withDefaultScreen();
		withFunction("If(x < 0, -1, 1)");
		assertNotEquals(0, gp.getLog().size());

	}

	IntervalPathPlotterMock gp;
	EuclidianViewBoundsMock bounds;
	IntervalPlotter plotter;

	void withDefaultScreen() {
		withBounds(-15, 15, -8, -8);
		withScreenSize(1920, 1250);
	}

	void withBounds(double xmin, double xmax, double ymin, double ymax) {
		bounds = new EuclidianViewBoundsMock(xmin, xmax, ymin, ymax);
	}

	void withScreenSize(int width, int height) {
		bounds.setSize(width, height);
	}

	void withFunction(String functionString) {
		gp = new IntervalPathPlotterMock(bounds);
		plotter = new IntervalPlotter(bounds, gp);
		GeoFunction function = add(functionString);
		plotter.enableFor(function);
	}

	protected void withHiResFunction(String description) {
		withBounds(-5000, 5000, 6000, -4000);
		withScreenSize(1920, 1280);
		withFunction(description);
	}

	@Test
	public void ifCommandShouldNotBeEmpty() {
		withHiResFunction("If(x< 0, 1)");
		assertNotEquals(0, gp.getLog().size());
	}

}
