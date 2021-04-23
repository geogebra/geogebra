package org.geogebra.common.euclidian.plot.interval;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class IntervalPlotterTest extends BaseUnitTest {

	public static final String EMPTY_PATH = "R";

	@Test
	public void testDiscontinuity() {
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setRealWorldCoordSystem(Math.PI - 0.0001, 2 * Math.PI + 0.0001, -2, 2);
		IntervalPathPlotterMock gp = new IntervalPathPlotterMock();
		IntervalPlotter plotter = new IntervalPlotter(view, gp);
		GeoFunction function = add("sqrt(sin(x))");
		plotter.enableFor(function);
		assertEquals(EMPTY_PATH, gp.getLog());
	}

	@Test
	public void testDiscontinoutyOfArgument() {
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setRealWorldCoordSystem(-1, -1E-7, -9, 9);
		IntervalPathPlotterMock gp = new IntervalPathPlotterMock();
		IntervalPlotter plotter = new IntervalPlotter(view, gp);
		GeoFunction function = add("-1sqrt(-1/x)");
		plotter.enableFor(function);
		assertEquals(EMPTY_PATH, gp.getLog());
	}

	@Test
	public void testSinLnx() {
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setRealWorldCoordSystem(-10, 0, -9, 9);
		IntervalPathPlotterMock gp = new IntervalPathPlotterMock();
		IntervalPlotter plotter = new IntervalPlotter(view, gp);
		GeoFunction function = add("sin(ln(x))");
		plotter.enableFor(function);
		assertEquals(EMPTY_PATH, gp.getLog());
	}

	@Test
	public void testSqrtReciprocalX() {
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setRealWorldCoordSystem(-10, 0, -9, 9);
		IntervalPathPlotterMock gp = new IntervalPathPlotterMock();
		IntervalPlotter plotter = new IntervalPlotter(view, gp);
		GeoFunction function = add("sqrt(1/x)");
		plotter.enableFor(function);
		assertEquals(EMPTY_PATH, gp.getLog());
	}

	@Test
	public void testSecLnXInverse() {
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setRealWorldCoordSystem(0, 1, -7, 7);
		IntervalPathPlotterMock gp = new IntervalPathPlotterMock();
		IntervalPlotter plotter = new IntervalPlotter(view, gp);
		GeoFunction function = add("(-8)/sec(ln(x))");
		plotter.enableFor(function);
		assertEquals(EMPTY_PATH, gp.getLog());
	}
}
