package org.geogebra.common.euclidian.plot;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;

/**
 * Class to plot x->f(x) functions and 2D/3D parametric curves
 * 
 * @author mathieu
 *
 */
public class CurvePlotter {

	public static final int MAX_PIXEL_DISTANCE = 10; // pixels

	// maximum angle between two line segments
	private static final double MAX_ANGLE = 10; // degrees
	public static final double MAX_BEND = Math.tan(MAX_ANGLE * Kernel.PI_180);

	// the curve is sampled at least at this many positions to plot it
	private static final int MIN_SAMPLE_POINTS = 80;

	/**
	 * Draws a parametric curve (x(t), y(t)) for t in [tMin, tMax].
	 * 
	 * @param tMin
	 *            min value of parameter
	 * @param tMax
	 *            max value of parameter
	 * @param curve
	 *            curve to be drawn
	 * @param view
	 *            Euclidian view to be used
	 * @param gp
	 *            generalpath that can be drawn afterwards
	 * @param calcLabelPos
	 *            whether label position should be calculated and returned
	 * @param moveToAllowed
	 *            whether moveTo() may be used for gp
	 * @return label position as Point
	 * @author Markus Hohenwarter, based on an algorithm by John Gillam
	 */
	public static GPoint plotCurve(CurveEvaluable curve, double tMin,
			double tMax, EuclidianView view, PathPlotter gp, boolean calcLabelPos,
			Gap moveToAllowed) {

		// ensure MIN_PLOT_POINTS
		double minSamplePoints = Math.max(MIN_SAMPLE_POINTS, view.getWidth() / 6);
		double maxParamStep = Math.abs(tMax - tMin) / minSamplePoints;
		// plot Interval [tMin, tMax]
		IntervalPlotter intervalPlotter =
				new IntervalPlotter(curve, tMin, tMax, 0, maxParamStep, view,
						gp, calcLabelPos, moveToAllowed);
		if (moveToAllowed == Gap.CORNER) {
			gp.corner();
		}

		return intervalPlotter.plot();
	}
}
