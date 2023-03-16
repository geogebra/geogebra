package org.geogebra.test.euclidian.plot;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.plot.CurvePlotter;
import org.geogebra.common.euclidian.plot.CurvePlotterOriginal;
import org.geogebra.common.euclidian.plot.Gap;
import org.geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import org.geogebra.common.jre.util.NumberFormat;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.test.OrderingComparison;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class CurvePlotterTest extends BaseUnitTest {

	@Test
	public void testPlotSinX() {
		CurveEvaluable curve = add("sin(x)");
		resultShouldBeTheSame(curve, -1, 1);
	}

	@Test
	public void testPlotSinX4() {
		resultShouldBeTheSame(add("sin(x^4)"), -5, 0);
	}

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
		resultShouldBeTheSame(add("sin(x)"), -1, -0.243311111);
	}

	@Test
	public void testSinXSquared() {
		resultShouldBeTheSame(add("sin(x^2)"), -50, 50);
	}

	@Test
	public void testSinXPowerOf4() {
		resultShouldBeTheSame(add("sin(x^4)"), -50, 50, 7000);
	}

	@Test
	public void testCurve() {
		resultShouldBeTheSame(add("Curve( t+abs(t), t+abs(t), t, -5, 0)"), -5, 5);
	}

	@Test
	public void testSqrt() {
		resultShouldBeTheSame(add("sqrt(x)"), -10, 10);
	}

	@Test
	public void testPiecewise() {
		resultShouldBeTheSame(add("If(x < 1, x + 1,"
				+ " If(x > 1, -x + 1, 1))"), -3.22724, 3.83963);
	}

	@Test
	public void testRational() {
		VerticalsCollectingClippedPath gp = new VerticalsCollectingClippedPath();
		GeoFunction f = add("(x^4+1)/x");
		EuclidianView view = getApp().getActiveEuclidianView();
		add("ZoomIn(-7,-5,5,5)");
		CurvePlotter.plotCurve(f, -7, 5, view,
				gp, true, Gap.MOVE_TO);
		assertThat(gp.getVerticals(), CoreMatchers.is(""));
	}

	@Test
	public void testSteepLinearFunction() {
		VerticalsCollectingClippedPath gp = new VerticalsCollectingClippedPath();
		GeoFunction f = add("10000x");
		EuclidianView view = getApp().getActiveEuclidianView();
		add("ZoomIn(-7,-5,5,5)");
		CurvePlotter.plotCurve(f, -7, 5, view,
				gp, true, Gap.MOVE_TO);
		assertThat(gp.getVerticals(), CoreMatchers.is("0.00049,0.00195"));
	}

	protected void resultShouldBeTheSame(CurveEvaluable f, double tMin, double tMax) {
		resultShouldBeTheSame(f, tMin, tMax, 1500);
	}

	protected void resultShouldBeTheSame(CurveEvaluable f, double tMin, double tMax,
			int maxEvaluations) {
		PathPlotterMock gp = new PathPlotterMock();
		PathPlotterMock gpExpected = new PathPlotterMock();

		EuclidianView view = getApp().getActiveEuclidianView();
		GPoint pointExpected = CurvePlotterOriginal.plotCurve(f, tMin, tMax, view,
				gpExpected, true, Gap.MOVE_TO);

		GPoint pointActual = CurvePlotter.plotCurve(f, tMin, tMax, view,
				gp, true, Gap.MOVE_TO);
		assertThat(gp.size(), OrderingComparison.lessThan(maxEvaluations));
		assertEquals(gpExpected, gp);
		assertEquals(pointExpected, pointActual);
	}

	private class VerticalsCollectingClippedPath extends GeneralPathClippedForCurvePlotter {
		private final ArrayList<String> verticals = new ArrayList<>();
		private double lastY;
		private final NumberFormat nf = new NumberFormat("#.####", 5);

		public VerticalsCollectingClippedPath() {
			super(CurvePlotterTest.this.getApp().getActiveEuclidianView());
		}

		protected void addPoint(double x, double y, SegmentType segmentType) {
			super.addPoint(x, y, segmentType);
			if (segmentType == SegmentType.LINE_TO && Math.abs(y - lastY) > 100) {
				verticals.add(nf.format(view.toRealWorldCoordX(x)));
			}
			lastY = y;
		}

		public String getVerticals() {
			return String.join(",", verticals);
		}
	}
}