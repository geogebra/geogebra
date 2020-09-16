package org.geogebra.test.euclidian.plot;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.euclidian.plot.IncrementalPlotter;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.junit.Before;
import org.junit.Test;

public class IncrementalPlotterTest extends BaseUnitTest {
	private static IncrementalPlotter incrementalPlotter;
	private PathPlotterMock gp;

	@Before
	public void setUp() {
		gp = new PathPlotterMock();
		incrementalPlotter = new IncrementalPlotter(null, getApp().getActiveEuclidianView(),
				gp, true, false);
	}

	@Test
	public void testSinX() {
		resultShouldBeTheSame(add("sin(x)"), -5, 5);
	}

	protected void resultShouldBeTheSame(CurveEvaluable f, int tMin, int tMax) {
		PathPlotterMock gpExpected = new PathPlotterMock();
		EuclidianView view = getApp().getActiveEuclidianView();

		GPoint pointExpected = CurvePlotterOriginal.plotCurve(f, tMin, tMax, view,
				gpExpected, true, Gap.MOVE_TO);

		incrementalPlotter.start(tMin, tMax, f);
		incrementalPlotter.run();

		assertEquals(gpExpected, gp);
		assertEquals(pointExpected, incrementalPlotter.getLabelPoint());
	}
}
