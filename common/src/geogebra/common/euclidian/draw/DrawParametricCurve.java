/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GPoint;
import geogebra.common.awt.GPoint2D;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GShape;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.VarString;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.ParametricCurve;
import geogebra.common.kernel.roots.RealRootUtil;
import geogebra.common.main.App;


/**
 * Draws graphs of parametric curves and functions
 * 
 * @author Markus Hohenwarter, with ideas from John Gillam (see below)
 */
public class DrawParametricCurve extends Drawable {

	// maximum and minimum distance between two plot points in pixels
	private static final int MAX_PIXEL_DISTANCE = 10; // pixels
	private static final double MIN_PIXEL_DISTANCE = 0.5; // pixels

	// maximum angle between two line segments
	private static final double MAX_ANGLE = 10; // degrees
	private static final double MAX_ANGLE_OFF_SCREEN = 45; // degrees
	private static final double MAX_BEND = Math.tan(MAX_ANGLE * Kernel.PI_180);
	private static final double MAX_BEND_OFF_SCREEN = Math
			.tan(MAX_ANGLE_OFF_SCREEN * Kernel.PI_180);

	// maximum number of bisections (max number of plot points = 2^MAX_DEPTH)
	private static final int MAX_DEFINED_BISECTIONS = 16;
	private static final int MAX_PROBLEM_BISECTIONS = 8;

	// maximum number of times to loop when xDiff, yDiff are both zero
	// eg Curve[0sin(t), 0t, t, 0, 6]
	private static final int MAX_ZERO_COUNT = 1000;

	// the curve is sampled at least at this many positions to plot it
	private static final int MIN_SAMPLE_POINTS = 80;
	/** ways to overcome discontinuity */
	public enum Gap{
	/** draw a line */
	 LINE_TO ,
	 /** skip it*/
	 MOVE_TO ,
	 /** follow along bottom of screen*/
	 RESET_XMIN ,
	 /** follow along left side of screen*/
	 RESET_YMIN ,
	 /** follow along top of screen*/
	 RESET_XMAX ,
	 /** follow along right side of screen*/
	 RESET_YMAX, CORNER ,
	}
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

	private ParametricCurve curve;
	private GeneralPathClipped gp;
	private boolean isVisible, labelVisible, fillCurve;
	private static int countPoints = 0;
	private static long countEvaluations = 0;
	
	/**
	 * Prints debug message
	 */
	public void  debug(){
		App.debug("Pts: "+countPoints+"\nEvaluations: "+countEvaluations);
	}

	/**
	 * Creates graphical representation of the curve
	 * 
	 * @param view
	 *            Euclidian view in which it should be drawn
	 * @param curve
	 *            Curve to be drawn
	 */
	public DrawParametricCurve(EuclidianView view, ParametricCurve curve) {
		this.view = view;
		hitThreshold = view.getCapturingThreshold();
		this.curve = curve;
		geo = curve.toGeoElement();
		update();
	}

	private StringBuilder labelSB = new StringBuilder();

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		labelVisible = geo.isLabelVisible();
		updateStrokes(geo);
		if (gp == null)
			gp = new GeneralPathClipped(view);
		gp.reset();

		fillCurve = filling(curve);

		double min = curve.getMinParameter();
		double max = curve.getMaxParameter();
		if (curve instanceof GeoFunction) {
			double minView = view.getXmin();
			double maxView = view.getXmax();
			if (min < minView)
				min = minView;
			if (max > maxView)
				max = maxView;
		}
		GPoint labelPoint;
		if (Kernel.isEqual(min, max)) {
			double[] eval = new double[2];
			curve.evaluateCurve(min, eval);
			view.toScreenCoords(eval);
			labelPoint = new GPoint((int) eval[0], (int) eval[1]);
		} else {
			labelPoint = plotCurve(curve, min, max, view, gp, labelVisible,
					fillCurve ? Gap.CORNER : Gap.MOVE_TO);
		}

		// gp on screen?
		if (!gp.intersects(0, 0, view.getWidth(), view.getHeight())) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}
		
		if (labelPoint != null) {
			xLabel = labelPoint.x;
			yLabel = labelPoint.y;
			switch (geo.labelMode) {
			case GeoElement.LABEL_NAME_VALUE:
				StringTemplate tpl = StringTemplate.latexTemplate;
				labelSB.setLength(0);
				labelSB.append('$');
				labelSB.append(geo.getLabel(tpl));
				labelSB.append('(');
				labelSB.append(((VarString) geo).getVarString(tpl));
				labelSB.append(")\\;=\\;");
				labelSB.append(geo.getLaTeXdescription());
				labelSB.append('$');

				labelDesc = labelSB.toString();
				break;

			case GeoElement.LABEL_VALUE:
				labelSB.setLength(0);
				labelSB.append('$');
				labelSB.append(geo.getLaTeXdescription());
				labelSB.append('$');

				labelDesc = labelSB.toString();
				break;

			case GeoElement.LABEL_CAPTION:
			default: // case LABEL_NAME:
				labelDesc = geo.getLabelDescription();
			}
			addLabelOffset(true);
		}
		// shape for filling

		if (geo.isInverseFill()) {
			setShape(AwtFactory.prototype.newArea(view.getBoundingPath()));
			getShape().subtract(AwtFactory.prototype.newArea(gp));
		}
		// draw trace
		if (curve.getTrace()) {
			isTracing = true;
			geogebra.common.awt.GGraphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null)
				drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				//view.updateBackground();
			}
		}
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
	final public static GPoint plotCurve(ParametricCurve curve, double t1,
			double t2, EuclidianView view, GeneralPathClipped gp,
			boolean calcLabelPos, Gap moveToAllowed) {

		countPoints = 0;
		countEvaluations = 0;

		// System.out.println("*** START plot: " +
		// curve.toGeoElement().getLabel() + " in "+ t1 + ", " + t2);

		// ensure MIN_PLOT_POINTS
		double max_param_step = Math.abs(t2 - t1) / MIN_SAMPLE_POINTS;
		// plot Interval [t1, t2]
		GPoint labelPoint = plotInterval(curve, t1, t2, 0, max_param_step, view,
				gp, calcLabelPos, moveToAllowed);
		if(moveToAllowed==Gap.CORNER && gp.firstPoint()!=null){
			corner(gp,gp.firstPoint().x,gp.firstPoint().y,view);
			gp.closePath();
		}
		// System.out.println(" plot points: " + countPoints + ", evaluations: "
		// + countEvaluations );
		// System.out.println("*** END plot");

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
	private static GPoint plotInterval(ParametricCurve curve, double t1,
			double t2, int intervalDepth, double max_param_step,
			EuclidianView view, GeneralPathClipped gp, boolean calcLabelPos,
			Gap moveToAllowed) {
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
		double x0, y0, t, x, y, moveX = 0, moveY = 0;
		boolean onScreen = false;
		boolean nextLineToNeedsMoveToFirst = false;
		double[] eval = new double[2];

		// evaluate for t1
		curve.evaluateCurve(t1, eval);
		countEvaluations++;
		onScreen = view.toScreenCoords(eval);
		x0 = eval[0]; // xEval(t1);
		y0 = eval[1]; // yEval(t1);
		if (isUndefined(eval)) {
			// Application.debug("Curve undefined at t = " + t1);
			return plotProblemInterval(curve, t1, t2, intervalDepth,
					max_param_step, view, gp, calcLabelPos, moveToAllowed,
					labelPoint);
		}

		// evaluate for t2
		curve.evaluateCurve(t2, eval);
		countEvaluations++;
		onScreen = view.toScreenCoords(eval);
		x = eval[0]; // xEval(t2);
		y = eval[1]; // yEval(t2);
		if (isUndefined(eval)) {
			// Application.debug("Curve undefined at t = " + t2);
			return plotProblemInterval(curve, t1, t2, intervalDepth,
					max_param_step, view, gp, calcLabelPos, moveToAllowed,
					labelPoint);
		}

		// FIRST POINT
		// c(t1) and c(t2) are defined, lets go ahead and move to our first
		// point (x0, y0)
		// note: lineTo will automatically do a moveTo if this is the first gp
		// point
		if (moveToAllowed == Gap.MOVE_TO) {
			moveTo(gp, x0, y0);
		} else if (moveToAllowed == Gap.LINE_TO || moveToAllowed == Gap.CORNER) {
			lineTo(gp, x0, y0);
		} else if (moveToAllowed == Gap.RESET_XMIN) {
			double d = gp.getCurrentPoint().getY();
			if (!Kernel.isEqual(d, y0)) {
				lineTo(gp, -10, d);
				lineTo(gp, -10, y0);
			}
			lineTo(gp, x0, y0);

		} else if (moveToAllowed == Gap.RESET_XMAX) {
			double d = gp.getCurrentPoint().getY();
			if (!Kernel.isEqual(d, y0)) {
				lineTo(gp, view.getWidth() + 10, d);
				lineTo(gp, view.getWidth() + 10, y0);
			}
			lineTo(gp, x0, y0);

		} else if (moveToAllowed == Gap.RESET_YMIN) {
			double d = gp.getCurrentPoint().getX();
			if (!Kernel.isEqual(d, x0)) {
				lineTo(gp, d, -10);
				lineTo(gp, x0, -10);
			}
			lineTo(gp, x0, y0);
		} else if (moveToAllowed == Gap.RESET_YMAX) {
			double d = gp.getCurrentPoint().getX();
			if (!Kernel.isEqual(d, x0)) {
				lineTo(gp, gp.getCurrentPoint().getX(), view.getHeight() + 10);
				lineTo(gp, x0, view.getHeight() + 10);
			}
			lineTo(gp, x0, y0);
		}

		// TODO
		// INIT plotting algorithm
		int LENGTH = MAX_DEFINED_BISECTIONS + 1;
		int dyadicStack[] = new int[LENGTH];
		int depthStack[] = new int[LENGTH];
		double xStack[] = new double[LENGTH];
		double yStack[] = new double[LENGTH];
		boolean onScreenStack[] = new boolean[LENGTH];
		double divisors[] = new double[LENGTH];
		divisors[0] = t2 - t1;
		for (int i = 1; i < LENGTH; i++)
			divisors[i] = divisors[i - 1] / 2;
		int i = 1;
		dyadicStack[0] = 1;
		depthStack[0] = 0;

		onScreenStack[0] = onScreen;
		xStack[0] = x; // xEval(t2);
		yStack[0] = y; // yEval(t2);

		// slope between (t1, t2)
		double ydiff = y - y0;
		double xdiff = x - x0;
		int countDiffZeros = 0;

		// init previous slope using (t1, t1 + min_step)
		curve.evaluateCurve(t1 + divisors[LENGTH - 1], eval);
		view.toScreenCoords(eval);
		countEvaluations++;
		double prevXdiff = eval[0] - x0;
		double prevYdiff = eval[1] - y0;

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
			segOffScreen = isSegmentOffScreen(view, x0, y0, x, y);
			// pixel distance from last point OK?
			distanceOK = segOffScreen || isDistanceOK(xdiff, ydiff);
			// angle from last segment OK?
			angleOK = isAngleOK(prevXdiff, prevYdiff, xdiff, ydiff,
					segOffScreen ? MAX_BEND_OFF_SCREEN : MAX_BEND);

			// bisect interval as long as ...
			while ( // max bisection depth not reached
			depth < MAX_DEFINED_BISECTIONS &&
			// distance not ok or angle not ok or step too big
					(!distanceOK || !angleOK || divisors[depth] > max_param_step)
					// make sure we don't get stuck on eg Curve[0sin(t), 0t, t, 0, 6]
					&& countDiffZeros < MAX_ZERO_COUNT
					) {
				// push stacks
				dyadicStack[top] = i;
				depthStack[top] = depth;
				onScreenStack[top] = onScreen;
				xStack[top] = x;
				yStack[top] = y;
				i = 2 * i - 1;
				top++;
				depth++;
				t = t1 + i * divisors[depth]; // t=t1+(t2-t1)*(i/2^depth)

				// evaluate curve for parameter t
				curve.evaluateCurve(t, eval);
				onScreen = view.toScreenCoords(eval);
				countEvaluations++;

				// check for singularity:
				// c(t) undefined; c(t-eps) and c(t+eps) both defined
				if (isUndefined(eval)) {
					// check if c(t-eps) and c(t+eps) are both defined
					boolean singularity = isDefinedAround(curve, t,
							divisors[LENGTH - 1]);

					// split interval: f(t+eps) or f(t-eps) not defined
					if (!singularity) {
						// Application.debug("Curve undefined at t = " + t);
						return plotProblemInterval(curve, left, t2,
								intervalDepth, max_param_step, view, gp,
								calcLabelPos, moveToAllowed, labelPoint);
					}
				}

				x = eval[0]; // xEval(t);
				y = eval[1]; // yEval(t);
				xdiff = x - x0;
				ydiff = y - y0;
				
				if (Kernel.isZero(xdiff) && Kernel.isZero(ydiff)) {
					countDiffZeros++;
				} else {
					countDiffZeros = 0;
				}

				// segment from last point off screen?
				segOffScreen = isSegmentOffScreen(view, x0, y0, x, y);
				// pixel distance from last point OK?
				distanceOK = segOffScreen || isDistanceOK(xdiff, ydiff);
				// angle from last segment OK?
				angleOK = isAngleOK(prevXdiff, prevYdiff, xdiff, ydiff,
						segOffScreen ? MAX_BEND_OFF_SCREEN : MAX_BEND);

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
							MAX_PROBLEM_BISECTIONS);
				}
			}else
				if (moveToAllowed == Gap.CORNER) {
					corner(gp,x,y, view);
				}

			// do lineTo or moveTo
			if (lineTo) {
				// handle previous moveTo first
				if (nextLineToNeedsMoveToFirst) {
					moveTo(gp, moveX, moveY);
					nextLineToNeedsMoveToFirst = false;
				}

				// draw line
				lineTo(gp, x, y);
			} else {
				// moveTo: remember moveTo position to avoid multiple moveTo
				// operations
				moveX = x;
				moveY = y;
				nextLineToNeedsMoveToFirst = true;
			}

			// remember last point in general path
			x0 = x;
			y0 = y;
			left = t;

			// remember first point on screen for label position
			if (needLabelPos && onScreen) {
				double xLabel = x + 10;
				if (xLabel < 20)
					xLabel = 5;
				if (xLabel > view.getWidth() - 30)
					xLabel = view.getWidth() - 15;
				double yLabel = y + 15;
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
			y = yStack[top];
			x = xStack[top];
			onScreen = onScreenStack[top];
			depth = depthStack[top] + 1; // pop stack and go to right
			i = dyadicStack[top] * 2;
			prevXdiff = xdiff;
			prevYdiff = ydiff;
			xdiff = x - x0;
			ydiff = y - y0;
			t = t1 + i * divisors[depth];
		} while (top != 0); // end of do-while loop for bisection stack

		return labelPoint;
	}

	private static void corner(GeneralPathClipped gp2,
			double x0, double y0,EuclidianView view) {
		int w = view.getWidth();
		int h = view.getHeight();
		GPoint2D pt = gp2.getCurrentPoint();
		if(pt==null){						
			return;
		}
		double x = pt.getX();
		double y = pt.getY();

		if((x<0 && x0>w) || (x>w && x0<0)){
			lineTo(gp2,x,-10);
			lineTo(gp2,x0,-10);
			return;
		}
				
		if((y<0 && y0>h)||(y>h && y0<0)){
			lineTo(gp2,-10,y);
			lineTo(gp2,-10,y0);
			return;
		}
		
		if((x>w || x<0) && (y0<0 || y0>h)){
			lineTo(gp2,x,y0);
			return;
		}
		
		if((x0>w || x0<0) && (y<0 || y>h)){
			lineTo(gp2,x0,y);
			return;
		}
		
		
	}

	/**
	 * Checks if c is continuous in the interval [t1, t2]. We assume that c(t1)
	 * and c(t2) are both defined.
	 * 
	 * @return true when t1 and t2 get closer than Kernel.MAX_DOUBLE_PRECISION
	 */
	private static boolean isContinuous(ParametricCurve c, double from,
			double to, int MAX_ITERATIONS) {
		double t1 = from;
		double t2 = to;
		if (Kernel.isEqual(t1, t2, Kernel.MAX_DOUBLE_PRECISION))
			return true;

		// left = c(t1)
		double[] left = new double[2];
		c.evaluateCurve(t1, left);
		countEvaluations++;
		if (isUndefined(left)) {
			// NaN or infinite: not continuous
			return false;
		}

		// right = c(t2)
		double[] right = new double[2];
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
		double[] middle = new double[2];

		while (iterations++ < MAX_ITERATIONS && dist > eps) {
			double m = (t1 + t2) / 2;
			c.evaluateCurve(m, middle);
			countEvaluations++;
			double distLeft = Math.max(Math.abs(left[0] - middle[0]),
					Math.abs(left[1] - middle[1]));
			double distRight = Math.max(Math.abs(right[0] - middle[0]),
					Math.abs(right[1] - middle[1]));

			// take the interval with the larger distance to do the bisection
			if (distLeft > distRight) {
				dist = distLeft;
				t2 = m;
			} else {
				dist = distRight;
				t1 = m;
			}

			if (Kernel.isEqual(t1, t2,
					Kernel.MAX_DOUBLE_PRECISION))
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
	 * Returns whether curve is defined for c(t-eps) and c(t + eps).
	 */
	private static boolean isDefinedAround(ParametricCurve curve, double t,
			double eps) {
		// check if c(t) is undefined
		double[] eval = new double[2];

		// c(t + eps)
		curve.evaluateCurve(t + eps, eval);
		countEvaluations++;
		if (!isUndefined(eval)) {
			// c(t - eps)
			curve.evaluateCurve(t - eps, eval);
			countEvaluations++;
			if (!isUndefined(eval)) {
				// SINGULARITY: c(t) undef, c(t-eps) and c(t+eps) defined
				return true;
			}
		}

		// c(t-eps) or c(t+eps) is undefined
		return false;
	}

	/**
	 * Returns whether the pixel distance from the last point is smaller than
	 * MAX_PIXEL_DISTANCE in both directions direction.
	 */
	private static boolean isDistanceOK(double xdiff, double ydiff) {
		return // distance from last point too large
		(Math.abs(xdiff) <= MAX_PIXEL_DISTANCE && Math.abs(ydiff) <= MAX_PIXEL_DISTANCE);
	}

	/**
	 * Performs a quick test whether the segment (x1, y1) to (x2, y2) is off
	 * screen.
	 */
	private static boolean isSegmentOffScreen(EuclidianView view, double x1,
			double y1, double x2, double y2) {
		// top;
		if (y1 < -EuclidianStatic.CLIP_DISTANCE
				&& y2 < -EuclidianStatic.CLIP_DISTANCE)
			return true;

		// left
		if (x1 < -EuclidianStatic.CLIP_DISTANCE
				&& x2 < -EuclidianStatic.CLIP_DISTANCE)
			return true;

		// bottom
		if (y1 > view.getHeight() + EuclidianStatic.CLIP_DISTANCE
				&& y2 > view.getHeight() + EuclidianStatic.CLIP_DISTANCE)
			return true;

		// right
		if (x1 > view.getWidth() + EuclidianStatic.CLIP_DISTANCE
				&& x2 > view.getWidth() + EuclidianStatic.CLIP_DISTANCE)
			return true;

		// close to screen
		return false;
	}

	/**
	 * Returns whether the angle between the vectors (vx, vy) and (wx, wy) is
	 * smaller than MAX_BEND, where MAX_BEND = tan(MAX_ANGLE).
	 */
	private static boolean isAngleOK(double vx, double vy, double wx,
			double wy, double bend) {
		// |v| * |w| * sin(alpha) = |det(v, w)|
		// cos(alpha) = v . w / (|v| * |w|)
		// tan(alpha) = sin(alpha) / cos(alpha)
		// tan(alpha) = |det(v, w)| / v . w

		// small angle: tan(alpha) < MAX_BEND
		// |det(v, w)| / v . w < MAX_BEND
		// |det(v, w)| < MAX_BEND * (v . w)

		double innerProduct = vx * wx + vy * wy;
		if (isUndefined(innerProduct)) {
			return true;
		} else if (innerProduct <= 0) {
			// angle >= 90 degrees
			return false;
		} else {
			// angle < 90 degrees
			// small angle: |det(v, w)| < MAX_BEND * (v . w)
			double det = Math.abs(vx * wy - vy * wx);
			return det < bend * innerProduct;
		}
	}

	/**
	 * Plots an interval where f(t1) or f(t2) is undefined.
	 */
	private static GPoint plotProblemInterval(ParametricCurve curve, double t1,
			double t2, int intervalDepth, double max_param_step,
			EuclidianView view, GeneralPathClipped gp, boolean calcLabelPos,
			Gap moveToAllowed, GPoint labelPoint) {
		boolean calcLabel = calcLabelPos;
		// stop recursion for too many intervals
		if (intervalDepth > MAX_PROBLEM_BISECTIONS || t1 == t2) {
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
			double[] eval = new double[2];
			getDefinedInterval(curve, t1, splitParam, eval);
			calcLabel = calcLabel && labelPoint == null;
			labelPoint1 = plotInterval(curve, eval[0], eval[1],
					intervalDepth + 1, max_param_step, view, gp, calcLabel,
					moveToAllowed);

			// plot interval [(t1+t2)/2, t2]
			getDefinedInterval(curve, splitParam, t2, eval);
			calcLabel = calcLabel && labelPoint1 == null;
			labelPoint2 = plotInterval(curve, eval[0], eval[1],
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
	 * Sets borders to a defined interval in [a, b] if possible.
	 * 
	 * @return whether two defined borders could be found.
	 */
	private static boolean getDefinedInterval(ParametricCurve curve, double a,
			double b, double[] borders) {
		// check first and last point in interval
		curve.evaluateCurve(a, borders);
		countEvaluations++;
		boolean aDef = !isUndefined(borders[0]) && !isUndefined(borders[1]);
		curve.evaluateCurve(b, borders);
		countEvaluations++;
		boolean bDef = !isUndefined(borders[0]) && !isUndefined(borders[1]);

		// both end points defined
		if (aDef && bDef) {
			borders[0] = a;
			borders[1] = b;
		}
		// one end point defined
		else if (aDef && !bDef || !aDef && bDef) {
			// check whether the curve is defined at the interval borders
			// if not, we try to find a valid domain
			double[] intervalX = RealRootUtil.getDefinedInterval(
					curve.getRealRootFunctionX(), a, b);
			double[] intervalY = RealRootUtil.getDefinedInterval(
					curve.getRealRootFunctionY(), a, b);
			double lowerBound = Math.max(intervalX[0], intervalY[0]);
			double upperBound = Math.min(intervalX[1], intervalY[1]);
			borders[0] = isUndefined(lowerBound) ? a : lowerBound;
			borders[1] = isUndefined(upperBound) ? b : upperBound;
		}
		// no end point defined
		else {
			borders[0] = a;
			borders[1] = b;
		}

		return !isUndefined(borders);
	}

	/**
	 * Calls gp.moveTo(x, y) only if the current point is not already at this
	 * position.
	 * 
	 * @param gp
	 *            path of the curve
	 * @param x
	 *            x-coord of start point
	 * @param y
	 *            y-coord of start point
	 */
	public static void moveTo(GeneralPathClipped gp, double x, double y) {
		drawTo(gp, x, y, false);
	}

	/**
	 * Calls gp.lineTo(x, y) only if the current point is not already at this
	 * position.
	 * 
	 * @param gp
	 *            path of the curve
	 * @param x
	 *            x-coord of target point
	 * @param y
	 *            y-coord of target point
	 */
	public static void lineTo(GeneralPathClipped gp, double x, double y) {
		drawTo(gp, x, y, true);
	}

	/**
	 * Calls gp.lineTo(x, y) resp. gp.moveTo(x, y) only if the current point is
	 * not already at this position.
	 */
	private static void drawTo(GeneralPathClipped gp, double x, double y,
			boolean lineTo) {
		GPoint2D point = gp.getCurrentPoint();

		// no points in path yet
		if (point == null) {
			gp.moveTo(x, y);
			countPoints++;
		}

		// only add points that are more than MIN_PIXEL_DISTANCE
		// from current location
		else if (!Kernel.isEqual(x, point.getX(), MIN_PIXEL_DISTANCE)
				|| !Kernel.isEqual(y, point.getY(), MIN_PIXEL_DISTANCE)) {
			if (lineTo) {
				gp.lineTo(x, y);
			} else {
				gp.moveTo(x, y);
			}

			countPoints++;
		}
	}

	/*
	 * The algorithm in plotInterval() is based on an algorithm by John Gillam.
	 * Below you find his explanation.
	 * 
	 * 
	 * Let X(t)=(x(t),y(t)) be a function defined on [a,b]. Make a uniform
	 * partition t0=a<t1<t2...<tn=b of [a,b]. Assume we've already plotted
	 * X(ti). Choose j>i and see if it's acceptable for the next point to be
	 * X(tj). If not, "back up" and try another j. Unfortunately, this probably
	 * leads to multiple evaluations of the function X at the same point. The
	 * following discussion presents an algorithm which basically does this
	 * "back up" but multiple function evaluations at the same point are
	 * avoided.
	 * 
	 * The actual algorithm always has n a power of 2, namely 2 to the maxDepth.
	 * In Math Okay, the user sets maxDepth in the drawing quality under Max
	 * lines.
	 * 
	 * The following first creates a complete binary tree of height h(3) Then
	 * the following algorithm visits all the leaves of the tree (2^h leaves):
	 * (easy induction on height h). Furthermore, the maximum stack size is
	 * clearly h+1:
	 * 
	 * Node root=create(1,3),p; p=root; Node[] stack=new Node[20]; int top=0;
	 * stack[top++]=p; do { while (p.left!=null) { stack[top++]=p;
	 * p=(Node)p.left; } System.out.print(" "+p.n); // visit p p=stack[--top];
	 * p=(Node)p.right; } while (top!=0);
	 * 
	 * Now the following code "clearly" generates as t values all the dyadic
	 * rationals with denominator 2^n, with n==maxDepth
	 * 
	 * int dyadicStack[]=new int[20]; int depthStack[]=new int[20]; double
	 * divisors[]=new double[20]; divisors[0]=1; for (int
	 * i=1;i<20;divisors[i]=divisors[i-1]/2,i++) ; int top=0,maxDepth=5; // of
	 * course 5 is just a test double t; int i=1; dyadicStack[0]=1;
	 * depthStack[0]=0; top=1; int depth=0; do { while (depth<maxDepth) {
	 * dyadicStack[top]=i; depthStack[top++]=depth; i<<=1; i--; depth++; }
	 * t=i*divisors[maxDepth]; // a visit of dyadic rational t
	 * depth=depthStack[--top]+1; // pop stack and go to right
	 * i=dyadicStack[top]<<1; } while (top !=0);
	 * 
	 * Finally, here is code which draws a curve (continuous) x(t),y(t) for
	 * t1<=t<=t2; xEval and yEval evaluate x and y at t; maxXDistance and
	 * maxYDistance are set somewhere. Let P0=(x0,y0) be "previous point" and
	 * P=(x,y) the "current point". The while loop that goes down the tree has
	 * condition of form: depth<maxDepth && !acceptable(P0,P); i.e., go on down
	 * the tree when it is unacceptable to draw the line from P0 to P.
	 * 
	 * double x0,y0,x,y,t; int dyadicStack[]=new int[20]; int depthStack[]=new
	 * int[20]; double xStack[]=new double[20]; double yStack[]=new double[20];
	 * double divisors[]=new double[20]; divisors[0]=t2-t1; for (int
	 * i=1;i<20;divisors[i]=divisors[i-1]/2,i++) ; int i=1; dyadicStack[0]=1;
	 * depthStack[0]=0; x0=xEval(t1); y0=yEval(t1); xStack[0]=x=xEval(t2);
	 * yStack[0]=y=yEval(t2); top=1; int depth=0; // with a GeneralPathClipped
	 * moveTo(sx0,sy0) , the "screen" point do { while (depth<maxDepth &&
	 * (abs(x-x0)>=maxXDistance || abs(y-y0)>=maxYDistance)) {
	 * dyadicStack[top]=i; depthStack[top]=depth; xStack[top]=x;
	 * yStack[top++]=y; i<<=1; i--; depth++; t=t1+i*divisors[depth]; //
	 * t=t1+(t2-t1)*(i/2^depth) x=xEval(t); y=yEval(t); } drawLine(x0,y0,x,y);
	 * // or with a GeneralPathClipped lineTo(sx,sy) // above is call to user
	 * written function x0=x; y0=y; // Here's the real utility of the algorithm:
	 * //Now pop stack and go to right; notice the corresponding dyadic value
	 * when we go to right is 2*i/(2^(d+1) = i/2^d !! So we've already
	 * //calculated the corresponding x and y values when we pushed.
	 * y=yStack[--top]; x=xStack[top] depth=depthStack[top]+1; // pop stack and
	 * go to right i=dyadicStack[top]<<1; } while (top !=0);
	 * 
	 * Notice the lines drawn from (x0,y0) to (x,y) always satisfy
	 * |x-x0|<maxXDistance and |y-y0|<maxYDistance or the minimum mesh
	 * 1/2^maxDepth has been reached. Also the maximum number of evaluations of
	 * functions x and y is 2^maxDepth. All this pushing and popping looks
	 * expensive, but compared to function evaluation and rendering, it's
	 * trivial.
	 * 
	 * For the special case of y=f(x), of course x(t)==t and y(t)=f(t). In this
	 * special case, you still have to worry about discontinuities of f, and in
	 * particular vertical asymptopes. So you need to adjust the Boolean
	 * acceptable.
	 */

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				EuclidianStatic.drawWithValueStrokePure(gp, g2);
			}

			g2.setPaint(geo.getObjectColor());
			g2.setStroke(objStroke);
			EuclidianStatic.drawWithValueStrokePure(gp, g2);

			if (fillCurve) {
				try {

					fill(g2, (geo.isInverseFill() ? getShape() : gp), false); // fill
																			// using
																			// default/hatching/image
																			// as
																			// appropriate

				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}

			if (labelVisible) {
				g2.setFont(view.getFontConic());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}


	@Override
	protected
	final void drawTrace(geogebra.common.awt.GGraphics2D g2) {
		g2.setPaint(geo
				.getObjectColor());
		g2.setStroke(objStroke);
		EuclidianStatic.drawWithValueStrokePure(gp, g2);
	}

	@Override
	final public boolean hit(int x, int y) {
		if (isVisible) {
			GShape t = geo.isInverseFill() ? getShape() : gp;
			if (strokedShape == null) {
				//strokedShape = new geogebra.awt.GenericShape(geogebra.awt.BasicStroke.getAwtStroke(objStroke).createStrokedShape(geogebra.awt.GenericShape.getAwtShape(gp)));
				strokedShape = objStroke.createStrokedShape(gp);
			}
			if (geo.getAlphaValue() > 0.0f || geo.isHatchingEnabled()) {
				return t.intersects(x - hitThreshold, y - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold);
			}
			
			// workaround for #2364
			if (geo.isGeoFunction()) {
				return gp.intersects(x - hitThreshold, y - hitThreshold,
					2 * hitThreshold, 2 * hitThreshold) && !gp.contains(x - hitThreshold, y - hitThreshold,
							2 * hitThreshold, 2 * hitThreshold);
			}
			
			// not GeoFunction, eg parametric
			return strokedShape.intersects(x - hitThreshold, y - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold);			
			
		}
		return false;
		/*
		 * return gp.intersects(x-3,y-3,6,6) && !gp.contains(x-3,y-3,6,6);
		 */
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		if (isVisible) {
			GShape t = geo.isInverseFill() ? getShape() : gp;
			if (strokedShape == null) {
				//strokedShape = new geogebra.awt.GenericShape(geogebra.awt.BasicStroke.getAwtStroke(objStroke).createStrokedShape(geogebra.awt.GenericShape.getAwtShape(gp)));
				strokedShape = objStroke.createStrokedShape(gp);
			}
			if (geo.getAlphaValue() > 0.0f || geo.isHatchingEnabled()) {
				return t.intersects(rect);
			}
			
			return strokedShape.intersects(rect);			
			
		}
		return false;
	}

	@Override
	final public boolean isInside(geogebra.common.awt.GRectangle rect) {
		return gp != null && rect.contains(gp.getBounds());
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public geogebra.common.awt.GRectangle getBounds() {
		if (!geo.isDefined() || !curve.isClosedPath()
				|| !geo.isEuclidianVisible()) {
			return null;
		}
		return AwtFactory.prototype.newRectangle(gp.getBounds());
	}

	final private static boolean filling(ParametricCurve curve) {
		return !curve.isFunctionInX()
				&& (curve.toGeoElement().getAlphaValue() > 0 || curve
						.toGeoElement().isHatchingEnabled());
	}

}
