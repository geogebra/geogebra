package org.geogebra.common.euclidian.plot.interval;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import java.util.function.Predicate;
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

	void valuesShouldBeBetween(double low, double high) {
		List<IntervalPathMockEntry> result =
				gp.getLog().stream().filter(entry -> entry.y < low - 1E-6
						|| entry.y > high + 1E-6).collect(Collectors.toList());
		assertEquals(Collections.emptyList(), result);
	}

	void valuesShouldNotBe(Predicate<? super IntervalPathMockEntry> predicate) {
		List<IntervalPathMockEntry> result =
				gp.getLog().stream().filter(predicate).collect(Collectors.toList());
		assertEquals(Collections.emptyList(), result);
	}

	public void logShouldBeAt(int index, IntervalPathMockEntry.PathOperation op, double x,
			double y) {
		assertEquals(gp.getLog().get(index), new IntervalPathMockEntry(op, x, y));
	}
}