package org.geogebra.test.euclidian.plot;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class CurvePlotterTest extends BaseUnitTest {

	@Test
	public void testPlotSinX() {
		resultShouldBeTheSame(add("sin(x)"), -5, 5);
	}

	@Test
	public void testPlotSinX4() {
		resultShouldBeTheSame(add("sin(x^4)"), -5, 5);
	}

	protected void resultShouldBeTheSame(GeoFunction f, int tMin, int tMax) {
		PathPlotterMock gp1 = new PathPlotterMock();
		PathPlotterMock gp2 = new PathPlotterMock();
		EuclidianView view = getApp().getActiveEuclidianView();
		CurvePlotter.plotCurve(f, tMin, tMax, view,
				gp1, false, CurvePlotter.Gap.MOVE_TO);
		CurvePlotterOriginal.plotCurve(f, tMin, tMax, view,
				gp2, false, CurvePlotter.Gap.MOVE_TO);
		assertEquals(gp1.result(), gp2.result());
	}
}
