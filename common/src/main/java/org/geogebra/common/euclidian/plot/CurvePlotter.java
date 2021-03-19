package org.geogebra.common.euclidian.plot;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;

/**
 * Class to plot x->f(x) functions and 2D/3D parametric curves
 * 
 * @author mathieu
 *
 */
public class CurvePlotter {

	// the curve is sampled at least at this many positions to plot it
	private static final int MIN_SAMPLE_POINTS = 80;
	private static final boolean LEGACY = false;
	private final CurveSegmentPlotter curveSegmentPlotter;

	/**
	 * Draws a parametric curve (x(t), y(t)) for t in [tMin, tMax].
	 * @param tMin min value of parameter
	 * @param tMax max value of parameter
	 * @param curve curve to be drawn
	 * @param view Euclidian view to be used
	 * @param gp generalpath that can be drawn afterwards
	 * @param calcLabelPos whether label position should be calculated and returned
	 * @param moveToAllowed whether moveTo() may be used for gp
	 * @author Markus Hohenwarter, based on an algorithm by John Gillam
	 */
	public CurvePlotter(CurveEvaluable curve, double tMin,
			double tMax, EuclidianView view, PathPlotter gp, boolean calcLabelPos,
			Gap moveToAllowed) {

		// ensure MIN_PLOT_POINTS
		double minSamplePoints = Math.max(MIN_SAMPLE_POINTS, view.getWidth() / 6);
		double maxParamStep = Math.abs(tMax - tMin) / minSamplePoints;
		// plot Interval [tMin, tMax]
		curveSegmentPlotter = new CurveSegmentPlotter(curve, tMin, tMax, 0,
				maxParamStep, view,	gp, calcLabelPos, moveToAllowed);

		if (moveToAllowed == Gap.CORNER) {
			gp.corner();
		}
	}

	/**
	 * Emulates the old behaviour.
	 *
	 * Draws a parametric curve (x(t), y(t)) for t in [tMin, tMax].
	 * @param tMin min value of parameter
	 * @param tMax max value of parameter
	 * @param curve curve to be drawn
	 * @param view Euclidian view to be used
	 * @param gp generalpath that can be drawn afterwards
	 * @param calcLabelPos whether label position should be calculated and returned
	 * @param moveToAllowed whether moveTo() may be used for gp
	 * @return point of the label.
	 * @author Markus Hohenwarter, based on an algorithm by John Gillam
	 */
	public static GPoint plotCurve(CurveEvaluable curve, double tMin,
			double tMax, EuclidianView view, PathPlotter gp, boolean calcLabelPos,
			Gap moveToAllowed) {
		if (LEGACY) {
			return CurvePlotterOriginal.plotCurve(curve, tMin, tMax, view, gp,
					calcLabelPos, moveToAllowed);
		}

		CurvePlotter plotter = new CurvePlotter(curve, tMin, tMax, view,
				gp, calcLabelPos, moveToAllowed);

		return plotter.getLabelPoint();

	}

	/**
	 *
	 * @return the point of the curve label
	 */
	public GPoint getLabelPoint() {
		return curveSegmentPlotter.getLabelPoint();
	}
}