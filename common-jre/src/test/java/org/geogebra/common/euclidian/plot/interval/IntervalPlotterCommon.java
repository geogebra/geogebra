package org.geogebra.common.euclidian.plot.interval;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;

public class IntervalPlotterCommon extends BaseUnitTest {
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

	public void logShouldBeAt(int index, IntervalPathMockEntry.PathOperation op, double x,
			double y) {
		assertEquals(gp.getLog().get(index), new IntervalPathMockEntry(op, x, y));
	}

	protected void withHiResFunction(String description) {
		withBounds(-5000, 5000, 6000, -4000);
		withScreenSize(1920, 1280);
		withFunction(description);
	}
}