package org.geogebra.test.euclidian.plot;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.junit.Ignore;
import org.junit.Test;

public class CurvePlotterTest extends BaseUnitTest {

	@Test
	public void testPlotSinX() {
		resultShouldBeTheSame(add("sin(x)"), -1,1);
	}

	@Test
	public void testPlotSinX4() {
		resultShouldBeTheSame(add("sin(x^4)"), -5, 0);
	}

	/**
	 * Original algorithm has two FIRSTPOINT on the beginning which is hardly
	 * the good case.
	 */
	@Ignore
	@Test
	public void testPlotReciprocal() {
		resultShouldBeTheSame(add("1/x"), -5, 5);
	}

	@Test
	public void testSingularity() {
		resultShouldBeTheSame(add("If(x==0, ?, sin(x))"), -5, 5);
	}

	@Test
	public void testSinX() {
		resultShouldBeTheSame(add("sin(x)"), -5, 5);
	}

	@Test
	public void testSinXSquared() {
		resultShouldBeTheSame(add("sin(x^2)"), -50, 50);
	}

	@Test
	public void testSinXPowerOf4() {
		resultShouldBeTheSame(add("sin(x^4)"), -50, 50);
	}

	@Test
	public void testCurve() {
		resultShouldBeTheSame(add("Curve( t+abs(t), t+abs(t), t, -5, 0)"), -5, 5);
	}

	protected void resultShouldBeTheSame(CurveEvaluable f, int tMin, int tMax) {
		PathPlotterMock gp = new PathPlotterMock();
		PathPlotterMock gpExpected = new PathPlotterMock();
		EuclidianView view = getApp().getActiveEuclidianView();

		GPoint pointExpected = CurvePlotterOriginal.plotCurve(f, tMin, tMax, view,
				gpExpected, true, Gap.MOVE_TO);

		CurvePlotter plotter = new CurvePlotter(f, tMin, tMax, view,
				gp, true, Gap.MOVE_TO);
		while (!plotter.isReady()) {
			plotter.plot();
		}

		assertEquals(gpExpected, gp);
		assertEquals(pointExpected, plotter.getLabelPoint());
	}
}
