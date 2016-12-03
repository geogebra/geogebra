package org.geogebra.common.euclidian.plot;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.util.Cloner;
import org.geogebra.common.util.debug.Log;

/**
 * Class to plot x->f(x) functions and 2D/3D parametric curves
 * 
 * @author mathieu
 *
 */
public class CurvePlotter {

	// low quality settings
	// // maximum and minimum distance between two plot points in pixels
	// private static final int MAX_PIXEL_DISTANCE = 16; // pixels
	// private static final double MIN_PIXEL_DISTANCE = 0.5; // pixels
	//
	// // maximum angle between two line segments
	// private static final double MAX_ANGLE = 32; // degrees
	// private static final double MAX_ANGLE_OFF_SCREEN = 70; // degrees
	// private static final double MAX_BEND = Math.tan(MAX_ANGLE *
	// Kernel.PI_180);
	// private static final double MAX_BEND_OFF_SCREEN =
	// Math.tan(MAX_ANGLE_OFF_SCREEN * Kernel.PI_180);
	//
	// // maximum number of bisections (max number of plot points = 2^MAX_DEPTH)
	// private static final int MAX_DEFINED_BISECTIONS = 8;
	// private static final int MAX_PROBLEM_BISECTIONS = 4;
	//
	// // the curve is sampled at least at this many positions to plot it
	// private static final int MIN_SAMPLE_POINTS = 5;

	@SuppressWarnings("unused")
	private static int countPoints = 0;
	@SuppressWarnings("unused")
	private static long countEvaluations = 0;

	/** ways to overcome discontinuity */
	public enum Gap {
		/** draw a line */
		LINE_TO,
		/** skip it */
		MOVE_TO,
		/** follow along bottom of screen */
		RESET_XMIN,
		/** follow along left side of screen */
		RESET_YMIN,
		/** follow along top of screen */
		RESET_XMAX,
		/** follow along right side of screen */
		RESET_YMAX,
		/** go to corner (for cartesian curves) */
		CORNER,
	}

	/**
	 * Draws a parametric curve (x(t), y(t)) for t in [t1, t2].
	 * 
	 * @param t1
	 *            min value of parameter
	 * @param t2
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
	final public static GPoint plotCurve(CurveEvaluable curve, double t1,
			double t2, EuclidianView view, PathPlotter gp,
			boolean calcLabelPos, Gap moveToAllowed) {
		countPoints = 0;
		countEvaluations = 0;

		// System.out.println("*** START plot: " +
		// curve.toGeoElement().getLabel() + " in "+ t1 + ", " + t2);

		// ensure MIN_PLOT_POINTS
		double max_param_step = Math.abs(t2 - t1) / view.getMinSamplePoints();
		// plot Interval [t1, t2]
		GPoint labelPoint = plotInterval(curve, t1, t2, 0, max_param_step,
				view, gp, calcLabelPos, moveToAllowed);
		if (moveToAllowed == Gap.CORNER) {
			gp.corner();
		}
		// System.out.println(" plot points: " + countPoints + ", evaluations: "
		// + countEvaluations );
		// System.out.println("*** END plot");

		return labelPoint;
	}

	//private static int plotIntervals = 0;

	/**
	 * Draws a parametric curve (x(t), y(t)) for t in [t1, t2].
	 * 
	 * @param: max_param_step: largest parameter step width allowed
	 * @param gp
	 *            generalpath that can be drawn afterwards
	 * @param calcLabelPos
	 *            whether label position should be calculated and returned
	 * @param moveToAllowed
	 *            whether moveTo() may be used for gp
	 * @return label position as Point
	 * @author Markus Hohenwarter, based on an algori5thm by John Gillam
	 */
	private static GPoint plotInterval(CurveEvaluable curve, double t1,
			double t2, int intervalDepth, double max_param_step,
			EuclidianView view, PathPlotter gp, boolean calcLabelPos,
			Gap moveToAllowed) {
		// Log.debug(++plotIntervals);
		// plot interval for t in [t1, t2]
		// If we run into a problem, i.e. an undefined point f(t), we bisect
		// the interval and plot both intervals [left, (left + right)/2] and
		// [(left + right)/2, right]
		// see catch block

		boolean needLabelPos = calcLabelPos;
		GPoint labelPoint = null;

		// The following algorithm by John Gillam avoids multiple
		// evaluations of the curve for the same parameter value t
		// see an explanation of this algorithm below.
		// double x0, y0;
		double t;
		// double x, y;
		// double moveX = 0, moveY = 0;
		double[] move = curve.newDoubleArray();
		for (int i = 0; i < move.length; i++) {
			move[i] = 0;
		}
		boolean onScreen = false;
		boolean nextLineToNeedsMoveToFirst = false;
		double[] eval = curve.newDoubleArray();
		double[] eval0, eval1;

		// evaluate for t1
		curve.evaluateCurve(t1, eval);
		countEvaluations++;
		if (isUndefined(eval)) {
			// Application.debug("Curve undefined at t = " + t1);
			return plotProblemInterval(curve, t1, t2, intervalDepth,
					max_param_step, view, gp, calcLabelPos, moveToAllowed,
					labelPoint);
		}
		eval0 = Cloner.clone(eval);

		// evaluate for t2
		curve.evaluateCurve(t2, eval);
		countEvaluations++;
		if (isUndefined(eval)) {
			// Application.debug("Curve undefined at t = " + t2);
			return plotProblemInterval(curve, t1, t2, intervalDepth,
					max_param_step, view, gp, calcLabelPos, moveToAllowed,
					labelPoint);
		}
		onScreen = view.isOnView(eval);
		eval1 = Cloner.clone(eval);

		// first point
		gp.firstPoint(eval0, moveToAllowed);

		// TODO
		// INIT plotting algorithm
		int LENGTH = view.getMaxDefinedBisections() + 1;
		int dyadicStack[] = new int[LENGTH];
		int depthStack[] = new int[LENGTH];
		double[][] posStack = new double[LENGTH][];
		// double xStack[] = new double[LENGTH];
		// double yStack[] = new double[LENGTH];
		boolean onScreenStack[] = new boolean[LENGTH];
		double divisors[] = new double[LENGTH];
		divisors[0] = t2 - t1;
		for (int i = 1; i < LENGTH; i++)
			divisors[i] = divisors[i - 1] / 2;
		int i = 1;
		dyadicStack[0] = 1;
		depthStack[0] = 0;

		onScreenStack[0] = onScreen;
		posStack[0] = Cloner.clone(eval1);

		// slope between (t1, t2)
		double[] diff = view.getOnScreenDiff(eval0, eval1);
		int countDiffZeros = 0;

		// init previous slope using (t1, t1 + min_step)
		curve.evaluateCurve(t1 + divisors[LENGTH - 1], eval);
		countEvaluations++;
		double[] prevDiff = view.getOnScreenDiff(eval0, eval);

		int top = 1;
		int depth = 0;
		t = t1;
		double left = t1;
		boolean distanceOK, angleOK, segOffScreen;

		// Actual plotting algorithm:
		// use bisection for interval until we reach
		// a small pixel distance between two points and
		// a small angle between two segments.
		// The evaluated curve points are stored on a stack
		// to avoid multiple evaluations at the same position.
		do {
			// segment from last point off screen?
			segOffScreen = view.isSegmentOffView(eval0, eval1);
			// pixel distance from last point OK?
			distanceOK = segOffScreen || isDistanceOK(diff, view);
			// angle from last segment OK?
			angleOK = isAngleOK(
					prevDiff,
					diff,
					segOffScreen ? view.getMaxBendOfScreen() : view
							.getMaxBend());

			// bisect interval as long as ...
			while ( // max bisection depth not reached
			depth < view.getMaxDefinedBisections() &&
			// distance not ok or angle not ok or step too big
					(!distanceOK || !angleOK || divisors[depth] > max_param_step)
					// make sure we don't get stuck on eg Curve[0sin(t), 0t, t,
					// 0, 6]
					&& countDiffZeros < view.getMaxZeroCount()) {
				// push stacks
				dyadicStack[top] = i;
				depthStack[top] = depth;
				onScreenStack[top] = onScreen;
				posStack[top] = Cloner.clone(eval1);
				i = 2 * i - 1;
				top++;
				depth++;
				t = t1 + i * divisors[depth]; // t=t1+(t2-t1)*(i/2^depth)

				// evaluate curve for parameter t
				curve.evaluateCurve(t, eval);
				onScreen = view.isOnView(eval);
				countEvaluations++;

				// check for singularity:
				// c(t) undefined; c(t-eps) and c(t+eps) both defined
				if (isUndefined(eval)) {
					// check if c(t-eps) and c(t+eps) are both defined
					boolean singularity = isContinuousAround(curve, t,
							divisors[LENGTH - 1]);

					// split interval: f(t+eps) or f(t-eps) not defined
					if (!singularity) {
						// Application.debug("Curve undefined at t = " + t);
						return plotProblemInterval(curve, left, t2,
								intervalDepth, max_param_step, view, gp,
								calcLabelPos, moveToAllowed, labelPoint);
					}
					Log.debug("SINGULARITY AT" + t);
				}

				eval1 = Cloner.clone(eval);
				diff = view.getOnScreenDiff(eval0, eval1);

				if (Kernel.isZero(diff[0]) && Kernel.isZero(diff[1])) {
					countDiffZeros++;
				} else {
					countDiffZeros = 0;
				}

				// segment from last point off screen?
				segOffScreen = view.isSegmentOffView(eval0, eval1);
				// pixel distance from last point OK?
				distanceOK = segOffScreen || isDistanceOK(diff, view);
				// angle from last segment OK?
				angleOK = isAngleOK(
						prevDiff,
						diff,
						segOffScreen ? view.getMaxBendOfScreen() : view
								.getMaxBend());

			} // end of while-loop for interval bisections

			// add point to general path: lineTo or moveTo?
			boolean lineTo = true;
			// TODO
			if (moveToAllowed == Gap.MOVE_TO) {
				if (segOffScreen) {
					// don't draw segments that are off screen
					lineTo = false;
				} else if (!angleOK || !distanceOK) {
					// check for DISCONTINUITY
					lineTo = isContinuous(curve, left, t,
							view.getMaxProblemBisections());
				}
			} else if (moveToAllowed == Gap.CORNER) {
				gp.corner(eval1);
			}

			// do lineTo or moveTo
			if (lineTo) {
				// handle previous moveTo first
				if (nextLineToNeedsMoveToFirst) {
					gp.moveTo(move);
					nextLineToNeedsMoveToFirst = false;
				}

				// draw line
				gp.lineTo(eval1);
			} else {
				// moveTo: remember moveTo position to avoid multiple moveTo
				// operations
				move = Cloner.clone(eval1);
				nextLineToNeedsMoveToFirst = true;
			}

			// remember last point in general path
			eval0 = Cloner.clone(eval1);
			left = t;

			// remember first point on screen for label position
			if (needLabelPos && onScreen) {
				double xLabel = view.toScreenCoordXd(eval1[0]) + 10;
				if (xLabel < 20)
					xLabel = 5;
				if (xLabel > view.getWidth() - 30)
					xLabel = view.getWidth() - 15;
				double yLabel = view.toScreenCoordYd(eval1[1]) + 15;
				if (yLabel < 40)
					yLabel = 15;
				else if (yLabel > view.getHeight() - 30)
					yLabel = view.getHeight() - 5;

				labelPoint = new GPoint((int) xLabel, (int) yLabel);
				needLabelPos = false;
			}

			/*
			 * Here's the real utility of the algorithm: Now pop stack and go to
			 * right; notice the corresponding dyadic value when we go to right
			 * is 2*i/(2^(d+1) = i/2^d !! So we've already calculated the
			 * corresponding x and y values when we pushed.
			 */
			--top;
			eval1 = posStack[top];
			onScreen = onScreenStack[top];
			depth = depthStack[top] + 1; // pop stack and go to right
			i = dyadicStack[top] * 2;
			prevDiff = Cloner.clone(diff);
			diff = view.getOnScreenDiff(eval0, eval1);
			t = t1 + i * divisors[depth];
		} while (top != 0); // end of do-while loop for bisection stack

		gp.endPlot();

		return labelPoint;
	}

	/**
	 * Returns true when x is either NaN or infinite.
	 */
	private static boolean isUndefined(double x) {
		return Double.isNaN(x) || Double.isInfinite(x);
	}

	/**
	 * Returns true when at least one element of eval is either NaN or infinite.
	 */
	private static boolean isUndefined(double[] eval) {
		for (int i = 0; i < eval.length; i++) {
			if (isUndefined(eval[i]))
				return true;
		}
		return false;
	}

	/**
	 * Plots an interval where f(t1) or f(t2) is undefined.
	 */
	private static GPoint plotProblemInterval(CurveEvaluable curve, double t1,
			double t2, int intervalDepth, double max_param_step,
			EuclidianView view, PathPlotter gp, boolean calcLabelPos,
			Gap moveToAllowed, GPoint labelPoint) {
		boolean calcLabel = calcLabelPos;
		// stop recursion for too many intervals
		if (intervalDepth > view.getMaxProblemBisections() || t1 == t2) {
			return labelPoint;
		}

		GPoint labelPoint1, labelPoint2;

		// plot interval for t in [t1, t2]
		// If we run into a problem, i.e. an undefined point f(t), we bisect
		// the interval and plot both intervals [t, (t+t2)/2] and [(t+t2)/2],
		// t2]
		double splitParam = (t1 + t2) / 2.0;

		// make sure that we first bisect down to intervals with a max size of
		// max_param_step
		boolean intervalsTooLarge = Math.abs(t1 - splitParam) > max_param_step;
		if (intervalsTooLarge) {
			// bisect interval
			calcLabel = calcLabel && labelPoint == null;
			labelPoint1 = plotInterval(curve, t1, splitParam,
					intervalDepth + 1, max_param_step, view, gp, calcLabel,
					moveToAllowed);

			// plot interval [(t1+t2)/2, t2]
			calcLabel = calcLabel && labelPoint1 == null;
			labelPoint2 = plotInterval(curve, splitParam, t2,
					intervalDepth + 1, max_param_step, view, gp, calcLabel,
					moveToAllowed);
		} else {
			// look at the end points of the intervals [t1, (t1+t2)/2] and
			// [(t1+t2)/2, t2]
			// and try to get a defined interval. This is important if one of
			// both interval borders is defined and the other is undefined. In
			// this
			// case we want to find a smaller interval where both borders are
			// defined

			// plot interval [t1, (t1+t2)/2]
			double[] borders = new double[2];
			getDefinedInterval(curve, t1, splitParam, borders);
			calcLabel = calcLabel && labelPoint == null;
			labelPoint1 = plotInterval(curve, borders[0], borders[1],
					intervalDepth + 1, max_param_step, view, gp, calcLabel,
					moveToAllowed);

			// plot interval [(t1+t2)/2, t2]
			getDefinedInterval(curve, splitParam, t2, borders);
			calcLabel = calcLabel && labelPoint1 == null;
			labelPoint2 = plotInterval(curve, borders[0], borders[1],
					intervalDepth + 1, max_param_step, view, gp, calcLabel,
					moveToAllowed);
		}

		if (labelPoint != null)
			return labelPoint;
		else if (labelPoint1 != null)
			return labelPoint1;
		else
			return labelPoint2;
	}

	/**
	 * Returns whether curve is defined for c(t-eps) and c(t + eps).
	 */
	private static boolean isContinuousAround(CurveEvaluable curve, double t,
			double eps) {
		// check if c(t) is undefined
		double[] eval = curve.newDoubleArray();

		// c(t + eps)
		curve.evaluateCurve(t + eps, eval);
		countEvaluations++;
		if (!isUndefined(eval)) {
			// c(t - eps)
			curve.evaluateCurve(t - eps, eval);
			countEvaluations++;
			if (!isUndefined(eval)) {
				// SINGULARITY: c(t) undef, c(t-eps) and c(t+eps) defined
				return true;// Math.abs(oldy - eval[1]) < 20 * eps;
			}
		}

		// c(t-eps) or c(t+eps) is undefined
		return false;
	}

	/**
	 * Returns whether the pixel distance from the last point is smaller than
	 * MAX_PIXEL_DISTANCE in all directions.
	 */
	private static boolean isDistanceOK(double[] diff, EuclidianView view) {
		for (double d : diff) {
			if (Math.abs(d) > view.getMaxPixelDistance()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns whether the angle between the vectors (vx, vy) and (wx, wy) is
	 * smaller than MAX_BEND, where MAX_BEND = tan(MAX_ANGLE).
	 */
	private static boolean isAngleOK(double[] v, double[] w, double bend) {
		// |v| * |w| * sin(alpha) = |det(v, w)|
		// cos(alpha) = v . w / (|v| * |w|)
		// tan(alpha) = sin(alpha) / cos(alpha)
		// tan(alpha) = |det(v, w)| / v . w

		// small angle: tan(alpha) < MAX_BEND
		// |det(v, w)| / v . w < MAX_BEND
		// |det(v, w)| < MAX_BEND * (v . w)

		double innerProduct = 0;
		for (int i = 0; i < v.length; i++) {
			innerProduct += v[i] * w[i];
		}
		if (isUndefined(innerProduct)) {
			return true;
		} else if (innerProduct <= 0) {
			// angle >= 90 degrees
			return false;
		} else {
			// angle < 90 degrees
			// small angle: |det(v, w)| < MAX_BEND * (v . w)
			double det;
			if (v.length < 3) {
				det = Math.abs(v[0] * w[1] - v[1] * w[0]);
			} else {
				double d1 = v[0] * w[1] - v[1] * w[0];
				double d2 = v[1] * w[2] - v[2] * w[1];
				double d3 = v[2] * w[0] - v[0] * w[2];
				det = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
			}
			return det < bend * innerProduct;
		}
	}

	/**
	 * Checks if c is continuous in the interval [t1, t2]. We assume that c(t1)
	 * and c(t2) are both defined.
	 * 
	 * @param c
	 *            curve
	 * @param from
	 *            min parameter
	 * @param to
	 *            max parameter
	 * @param mnaxIterations
	 *            max number of bisections
	 * 
	 * @return true when t1 and t2 get closer than Kernel.MAX_DOUBLE_PRECISION
	 */
	public static boolean isContinuous(CurveEvaluable c, double from,
			double to, int mnaxIterations) {
		double t1 = from;
		double t2 = to;
		if (Kernel.isEqual(t1, t2, Kernel.MAX_DOUBLE_PRECISION))
			return true;

		// left = c(t1)
		double[] left = c.newDoubleArray();
		c.evaluateCurve(t1, left);
		countEvaluations++;
		if (isUndefined(left)) {
			// NaN or infinite: not continuous
			return false;
		}

		// right = c(t2)
		double[] right = c.newDoubleArray();
		c.evaluateCurve(t2, right);
		countEvaluations++;
		if (isUndefined(right)) {
			// NaN or infinite: not continuous
			return false;
		}

		// Start with distance between left and right points.
		// Bisect until the maximum distance of middle to right resp. left
		// is clearly smaller than the initial distance.
		double initialDistance = Math.max(Math.abs(left[0] - right[0]),
				Math.abs(left[1] - right[1]));
		double eps = initialDistance * 0.9;
		double dist = Double.POSITIVE_INFINITY;
		int iterations = 0;
		double[] middle = c.newDoubleArray();

		while (iterations++ < mnaxIterations && dist > eps) {
			double m = (t1 + t2) / 2;
			c.evaluateCurve(m, middle);
			countEvaluations++;
			double distLeft = c.distanceMax(left, middle);
			double distRight = c.distanceMax(right, middle);

			// take the interval with the larger distance to do the bisection
			if (distLeft > distRight) {
				dist = distLeft;
				t2 = m;
			} else {
				dist = distRight;
				t1 = m;
			}

			if (Kernel.isEqual(t1, t2, Kernel.MAX_DOUBLE_PRECISION))
				return true;
			// System.out.println("  largest dist: " + dist + ", [" + t1 + ", "
			// + t2 +"]");
		}

		// we managed to make the distance clearly smaller than the initial
		// distance
		boolean ret = dist <= eps;

		// System.out.println("END isContinuous " + ret + ", eps: " + eps +
		// ", dist: " + dist);
		return ret;
	}

	/**
	 * Sets borders to a defined interval in [a, b] if possible.
	 * 
	 * @return whether two defined borders could be found.
	 */
	private static boolean getDefinedInterval(CurveEvaluable curve, double a,
			double b, double[] borders) {

		double[] eval = curve.newDoubleArray();

		// check first and last point in interval
		curve.evaluateCurve(a, eval);
		countEvaluations++;
		boolean aDef = !isUndefined(eval);
		curve.evaluateCurve(b, eval);
		countEvaluations++;
		boolean bDef = !isUndefined(eval);

		// both end points defined
		if (aDef && bDef) {
			borders[0] = a;
			borders[1] = b;
		}
		// one end point defined
		else if (aDef && !bDef || !aDef && bDef) {
			// check whether the curve is defined at the interval borders
			// if not, we try to find a valid domain
			double[] interval = curve.getDefinedInterval(a, b);
			borders[0] = isUndefined(interval[0]) ? a : interval[0];
			borders[1] = isUndefined(interval[1]) ? b : interval[1];
		}
		// no end point defined
		else {
			borders[0] = a;
			borders[1] = b;
		}

		return !isUndefined(borders);
	}

	/**
	 * draw list of points
	 * 
	 * @param gp
	 *            path plotter that actually draws the points list
	 * @param pointList
	 *            list of points
	 * @param transformSys
	 *            coordinte system to be applied on 2D points
	 * @return last point drawn
	 */
	static public double[] draw(PathPlotter gp,
			ArrayList<? extends MyPoint> pointList, CoordSys transformSys) {
		double[] coords = gp.newDoubleArray();
		int size = pointList.size();
		if (!gp.supports(transformSys)
				|| size == 0) {
			return coords;
		}
		// this is for making sure that there is no lineto from nothing
		// and there is no lineto if there is an infinite point between the
		// points
		boolean linetofirst = true;


		for (int i = 0; i < size; i++) {
			MyPoint p = pointList.get(i);

			// don't add infinite points
			// otherwise hit-testing doesn't work
			if (p.isFinite()) {
				if (gp.copyCoords(p, coords, transformSys)) {

					if (p.lineTo && !linetofirst) {
						gp.lineTo(coords);
					} else {
						gp.moveTo(coords);
					}
					linetofirst = false;
				} else {
					linetofirst = true;
				}
			} else {
				linetofirst = true;
			}
		}

		gp.endPlot();

		return coords;
	}
}
