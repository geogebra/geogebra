package org.geogebra.common.euclidian.plot;

import org.apache.commons.math3.util.Cloner;
import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.util.DoubleUtil;

/**
 * Class to plot x->f(x) functions and 2D/3D parametric curves
 * 
 * @author mathieu
 *
 */
public class CurveSegmentPlotter {
	public static final int MAX_PIXEL_DISTANCE = 10; // pixels

	// maximum angle between two line segments
	private static final double MAX_ANGLE = 10; // degrees
	public static final double MAX_BEND = Math.tan(MAX_ANGLE * Kernel.PI_180);

	// maximum number of bisections (max number of plot points = 2^MAX_DEPTH)
	private static final int MAX_DEFINED_BISECTIONS = 16;
	private static final int MAX_PROBLEM_BISECTIONS = 8;
	// NB: don't try to increase this to improve discontinuity check in something like
	// ln(x)+sin(x), it could lead to piecewise functions joining up.
	private static final int MAX_CONTINUITY_BISECTIONS = 8;

	private static final double MAX_JUMP = 5;
	private final CurveEvaluable curve;
	private final double tMin;
	private final double tMax;
	private final int intervalDepth;
	private final double maxParamStep;
	private final EuclidianView view;
	private final PathPlotter gp;
	private final LabelPositionCalculator labelPositionCalculator;
	private boolean needLabelPos;
	private final Gap moveToAllowed;
	private GPoint labelPoint = null;
	private double[] move;
	private boolean nextLineToNeedsMoveToFirst;
	private final double[] eval;
	private double[] evalLeft;
	private double[] evalRight;
	private CurveSegmentInfo info;
	private SegmentParams params;
	public static final int LENGTH = MAX_DEFINED_BISECTIONS + 1;
	private CurvePlotterStack stack;
	private boolean onScreen;
	private double[] divisors;

	/**
	 * Draws a parametric curve (x(t), y(t)) for t in [tMin, tMax].
	 *
	 * @param maxParamStep
	 *             largest parameter step width allowed
	 * @param gp
	 *            generalpath that can be drawn afterwards
	 * @param calcLabelPos
	 *            whether label position should be calculated and returned
	 * @param moveToAllowed
	 *            whether moveTo() may be used for gp
	 * @author Markus Hohenwarter, based on an algori5thm by John Gillam
	 */
	public CurveSegmentPlotter(CurveEvaluable curve, double tMin,
			double tMax, int intervalDepth, double maxParamStep,
			EuclidianView view, PathPlotter gp, boolean calcLabelPos,
			Gap moveToAllowed) {
		this.curve = curve;
		this.tMin = tMin;
		this.tMax = tMax;
		this.intervalDepth = intervalDepth;
		this.maxParamStep = maxParamStep;
		this.view = view;
		this.gp = gp;
		// plot interval for t in [tMin, tMax]
		// If we run into a problem, i.e. an undefined point f(t), we bisect
		// the interval and plot both intervals [left, (left + right)/2] and
		// [(left + right)/2, right]
		// see catch block

		needLabelPos = calcLabelPos;
		this.moveToAllowed = moveToAllowed;

		// The following algorithm by John Gillam avoids multiple
		// evaluations of the curve for the same parameter value t
		// see an explanation of this algorithm below.

		move = curve.newDoubleArray();
		nextLineToNeedsMoveToFirst = false;
		eval = curve.newDoubleArray();
		labelPositionCalculator = new LabelPositionCalculator(view);
		if (start()) {
			plot();
		}
	}

	private boolean start() {
		if (isCurveUndefinedAt(tMin)) {
			plotProblemInterval(tMin);
			return false;
		}

		evalLeft = Cloner.clone(eval);

		if (isCurveUndefinedAt(tMax)) {
			plotProblemInterval(tMin);
			return false;
		}

		onScreen = view.isOnView(eval);
		evalRight = Cloner.clone(eval);

		gp.firstPoint(evalLeft, moveToAllowed);

		createStack();
		createDivisors();
		createParams();
		initDiffs();
		createInfo();
		return true;
	}

	private void createStack() {
		stack = new CurvePlotterStack(LENGTH, onScreen, evalRight);
	}

	private void createDivisors() {
		divisors = createDivisors(tMin, tMax);
	}

	private void createParams() {
		params = new SegmentParams(tMin, divisors, view);
	}

	private void initDiffs() {
		params.diff = view.getOnScreenDiff(evalLeft, evalRight);
		curve.evaluateCurve(tMin + divisors[divisors.length - 1], eval);
		params.prevDiff = view.getOnScreenDiff(evalLeft, eval);
	}

	private void createInfo() {
		info = new CurveSegmentInfo(view);
	}

	private GPoint plot() {
		if (plotBisectorAlgo()) {
			return plotProblemInterval(params.left);
		}

		return labelPoint;
	}

	// Actual plotting algorithm:
	// use bisection for interval until we reach
	// a small pixel distance between two points and
	// a small angle between two segments.
	// The evaluated curve points are stored on a stack
	// to avoid multiple evaluations at the same position.
	private boolean plotBisectorAlgo() {
		do {
			info.update(evalLeft, evalRight, params.diff, params.prevDiff);
			// bisect interval as long as max bisection depth not reached & ...
			while (params.hasMaxDepthNotReached()
					&& (info.isDistanceOrAngleInvalid()
							|| params.isStepTooBig(maxParamStep))
					&& params.isDiffZerosLimitNotReached()) {

				// push stacks
				stack.push(params.dyad, params.depth, onScreen, evalRight);
				params.progress();

        		// evaluate curve for parameter t
				curve.evaluateCurve(params.t, eval);
				onScreen = view.isOnView(eval);

				if (isUndefined(eval) && hasNoSingularity(params.t, divisors[LENGTH - 1])) {
					return true;
				}

				evalRight = Cloner.clone(eval);
				params.updateDiff(evalLeft, evalRight);
				params.countDiffZeros = isDiffZero(params.diff) ? params.countDiffZeros + 1 : 0;

				info.update(evalLeft, evalRight, params.diff, params.prevDiff);

			} // end of while-loop for interval bisections

			drawSegment(params.t, params.left, info);

			// remember last point in general path
			evalLeft = Cloner.clone(evalRight);
			params.left = params.t;

			// remember first point on screen for label position
			if (onScreen && needLabelPos) {
				calculateLabelPosition();
			}

			/*
			 * Here's the real utility of the algorithm: Now pop stack and go to
			 * right; notice the corresponding dyadic value when we go to right
			 * is 2*i/(2^(d+1) = i/2^d !! So we've already calculated the
			 * corresponding x and y values when we pushed.
			 */

			CurvePlotterStackItem item = stack.pop();
			onScreen = item.onScreen;
			evalRight = item.eval;
			params.restoreFromStack(item);
			params.updateDiff(evalLeft, evalRight);
		} while (stack.hasItems()); // end of do-while loop for bisection stack
		gp.endPlot();
		return false;
	}

	private boolean hasNoSingularity(double t, double interval) {
		// check if c(t-eps) and c(t+eps) are both defined

		return !isContinuousAround(curve, t,
				interval, view, eval);
	}

	private boolean isCurveUndefinedAt(double x) {
		curve.evaluateCurve(x, eval);
		return isUndefined(eval);
	}

	protected void drawSegment(double t, double left, CurveSegmentInfo info) {
		if (isLineTo(t, left, info)) {
			// handle previous moveTo first
			if (nextLineToNeedsMoveToFirst) {
				gp.moveTo(move);
				nextLineToNeedsMoveToFirst = false;
			}

			// draw line
			gp.lineTo(evalRight);
		} else {
			// moveTo: remember moveTo position to avoid multiple moveTo
			// operations
			move = Cloner.clone(evalRight);
			nextLineToNeedsMoveToFirst = true;
		}
	}

	protected void calculateLabelPosition() {
		labelPoint = labelPositionCalculator.calculate(evalRight[0], evalRight[1]);
		needLabelPos = false;
	}

	private boolean isLineTo(double t, double left, CurveSegmentInfo info) {
		boolean lineTo = true;
		// TODO
		if (moveToAllowed == Gap.MOVE_TO) {
			if (info.isOffScreen()) {
				// don't draw segments that are off screen
				lineTo = false;
			} else if (info.isDistanceOrAngleInvalid()) {
				// check for DISCONTINUITY
				lineTo = isContinuous(curve, left, t, MAX_CONTINUITY_BISECTIONS);
			}
		} else if (moveToAllowed == Gap.CORNER) {
			gp.corner(evalRight);
		}
		return lineTo;
	}

	private static boolean isDiffZero(double[] diff) {
		return DoubleUtil.isZero(diff[0]) && DoubleUtil.isZero(diff[1]);
	}

	private static double[] createDivisors(double tMin, double tMax) {
		double[] divisors = new double[LENGTH];
		divisors[0] = tMax - tMin;
		for (int i = 1; i < LENGTH; i++) {
			divisors[i] = divisors[i - 1] / 2;
		}
		return divisors;
	}

	/**
	 * Returns true when x is either NaN or infinite.
	 */
	static boolean isUndefined(double x) {
		return Double.isNaN(x) || Double.isInfinite(x);
	}

	/**
	 * Returns true when at least one element of eval is either NaN or infinite.
	 */
	private static boolean isUndefined(double[] eval) {
		for (double value : eval) {
			if (isUndefined(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Plots an interval where f(tMin) or f(tMax) is undefined.
	 */
	private GPoint plotProblemInterval(double left) {
		boolean calcLabel = needLabelPos;
		// stop recursion for too many intervals
		if (intervalDepth > MAX_PROBLEM_BISECTIONS || left == tMax) {
			return labelPoint;
		}

		GPoint  labelPointMin, labelPointMax;

		// plot interval for t in [tMin, tMax]
		// If we run into a problem, i.e. an undefined point f(t), we bisect
		// the interval and plot both intervals [t, (t+tMax)/2] and [(t+tMax)/2],
		// tMax]
		double splitParam = (left + tMax) / 2.0;

		// make sure that we first bisect down to intervals with a max size of
		// maxParamStep
		boolean intervalsTooLarge = Math.abs(left - splitParam) > maxParamStep;
		if (intervalsTooLarge) {
			// bisect interval
			calcLabel = calcLabel && labelPoint == null;
			CurveSegmentPlotter
					plotterMin = new CurveSegmentPlotter(curve, left, splitParam, intervalDepth + 1,
					maxParamStep, view, gp, calcLabel, moveToAllowed);
			labelPointMin = plotterMin.getLabelPoint();

			// plot interval [(tMin+tMax)/2, tMax]
			calcLabel = calcLabel && labelPointMin == null;
			CurveSegmentPlotter plotterMax =
					new CurveSegmentPlotter(curve, splitParam, tMax, intervalDepth + 1,
							maxParamStep, view, gp, calcLabel, moveToAllowed);

			labelPointMax = plotterMax.getLabelPoint();
		} else {
			// look at the end points of the intervals [tMin, (tMin+tMax)/2] and
			// [(tMin+tMax)/2, tMax]
			// and try to get a defined interval. This is important if one of
			// both interval borders is defined and the other is undefined. In
			// this
			// case we want to find a smaller interval where both borders are
			// defined

			// plot interval [tMin, (tMin+tMax)/2]
			double[] borders = new double[2];
			getDefinedInterval(curve, left, splitParam, borders);
			calcLabel = calcLabel && labelPoint == null;
			CurveSegmentPlotter plotterMin = new CurveSegmentPlotter(curve, borders[0], borders[1],
					intervalDepth + 1, maxParamStep, view, gp, calcLabel,
					moveToAllowed);
			labelPointMin = plotterMin.getLabelPoint();

			// plot interval [(tMin+tMax)/2, tMax]
			getDefinedInterval(curve, splitParam, tMax, borders);
			calcLabel = calcLabel && labelPointMin == null;
			CurveSegmentPlotter plotterMax = new CurveSegmentPlotter(curve, borders[0], borders[1],
					intervalDepth + 1, maxParamStep, view, gp, calcLabel,
					moveToAllowed);
			labelPointMax = plotterMax.getLabelPoint();
		}

		if (labelPoint == null) {
			if (labelPointMin != null) {
				labelPoint = labelPointMin;
			} else if (labelPointMax != null) {
				labelPoint = labelPointMax;
			}
		}
		return labelPoint;
	}

	/**
	 * Returns whether curve is defined for c(t-eps) and c(t + eps).
	 */
	private static boolean isContinuousAround(CurveEvaluable curve, double t,
			double eps, EuclidianView view, double[] evalT) {
		// check if c(t) is undefined
		double[] eval = curve.newDoubleArray();

		// c(t + eps)
		curve.evaluateCurve(t + eps, eval);
		double oldy = eval[1];
		if (!isUndefined(eval)) {
			// c(t - eps)
			curve.evaluateCurve(t - eps, eval);
			if (!isUndefined(eval)) {
				// SINGULARITY for functions: c(t) undef, c(t-eps) and c(t+eps)
				// defined and close
				if (curve.isFunctionInX()
						&& Math.abs(oldy - eval[1]) * view.getYscale() < MAX_JUMP) {
					evalT[1] = (oldy + eval[1]) * 0.5;
					return true;
				}
				// SINGULARITY for curves: c(t) undef, c(t-eps) and c(t+eps)
				// defined, ignore distance
				return !curve.isFunctionInX();
			}
		}

		// c(t-eps) or c(t+eps) is undefined
		return false;
	}

	/**
	 * Checks if c is continuous in the interval [tMin, tMax]. We assume that c(tMin)
	 * and c(tMax) are both defined.
	 * 
	 * @param c
	 *            curve
	 * @param from
	 *            min parameter
	 * @param to
	 *            max parameter
	 * @param maxIterations
	 *            max number of bisections
	 * 
	 * @return true when tMin and tMax get closer than Kernel.MAX_DOUBLE_PRECISION
	 */
	public static boolean isContinuous(CurveEvaluable c, double from, double to,
			int maxIterations) {
		double tMin = from;
		double tMax = to;
		if (DoubleUtil.isEqual(tMin, tMax, Kernel.MAX_DOUBLE_PRECISION)) {
			return true;
		}

		// left = c(tMin)
		double[] left = c.newDoubleArray();
		c.evaluateCurve(tMin, left);
		if (isUndefined(left)) {
			// NaN or infinite: not continuous
			return false;
		}

		// right = c(tMax)
		double[] right = c.newDoubleArray();
		c.evaluateCurve(tMax, right);
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

		while (iterations++ < maxIterations && dist > eps) {
			double m = (tMin + tMax) / 2;
			c.evaluateCurve(m, middle);
			double distLeft = c.distanceMax(left, middle);
			double distRight = c.distanceMax(right, middle);

			// take the interval with the larger distance to do the bisection
			if (distLeft > distRight) {
				dist = distLeft;
				tMax = m;
			} else {
				dist = distRight;
				tMin = m;
			}

			if (DoubleUtil.isEqual(tMin, tMax, Kernel.MAX_DOUBLE_PRECISION)) {
				return true;
			}
		}

		// we managed to make the distance clearly smaller than the initial
		// distance
		return dist <= eps;
	}

	/**
	 * Sets borders to a defined interval in [a, b] if possible.
	 */
	private static void getDefinedInterval(CurveEvaluable curve, double a,
			double b, double[] borders) {
		double[] eval = curve.newDoubleArray();

		// check first and last point in interval
		curve.evaluateCurve(a, eval);
		boolean aDef = !isUndefined(eval);
		curve.evaluateCurve(b, eval);
		boolean bDef = !isUndefined(eval);

		// both end points defined
		if (aDef && bDef) {
			borders[0] = a;
			borders[1] = b;
		}
		// one end point defined
		else if (aDef || bDef) {
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
	}

	public GPoint getLabelPoint() {
		return labelPoint;
	}
}
