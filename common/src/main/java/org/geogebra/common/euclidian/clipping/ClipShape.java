/*
 * @(#)Clipper.java
 *
 * $Date: 2010-03-19 18:53:03 -0500 (Fri, 19 Mar 2010) $
 *
 * Copyright (c) 2009 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms:
 * BSD License
 * 
 * This software is probably, but not necessarily, discussed here:
 * http://javagraphics.blogspot.com/
 * 
 * And the latest version should be available here:
 * https://javagraphics.dev.java.net/
 */

package org.geogebra.common.euclidian.clipping;

import java.util.Arrays;
import java.util.Stack;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.EquationSolver;

/**
 * This class lets you clip/intersect an arbitrary shape to a Rectangle2D.
 *
 */
public class ClipShape {

	// private static final DoubleArrayFactoryImpl doubleFactory = new
	// DoubleArrayFactoryImpl();
	/** Factory for double arrays */
	static final DoubleArrayFactory doubleFactory = new DoubleArrayFactory();

	/**
	 * This is the tolerance with which 2 numbers must be similar to be
	 * considered "equal".
	 * <P>
	 * This is necessary because as we much around with numbers and equations,
	 * machine rounding will inevitably cause .5's to become .49999's and other
	 * harmless changes.
	 */
	private static final double TOLERANCE = 1E-10;

	/**
	 * This does 2 things: 1. It collapses redundant line segments that fall on
	 * the same horizontal or vertical line. This is very important, given how
	 * clipToRect() works. And not only does it vastly simplify your shape (lots
	 * of redundant lineTo's will be called), but if a shape is properly
	 * collapsed it has a much better chance of return a truly accurate result
	 * when you call getBounds() on it.
	 * <P>
	 * Note that there are still some far fetched examples (involving
	 * discontinuous shapes) where getBounds() may be inaccurate, though. 2. This
	 * can take a Function (either quadratic or cubic) and split it over a
	 * smaller interval from an arbitary [t0,t1].
	 */
	static class ClippedPath {
		/** path */
		final GGeneralPath g;
		private Stack<double[]> uncommittedPoints = new Stack<>();
		private double initialX;
		private double initialY;

		/**
		 * @param windingRule
		 *            winding rule
		 */
		public ClippedPath(int windingRule) {
			g = AwtFactory.getPrototype().newGeneralPath(windingRule);
		}

		/**
		 * @param x
		 *            x coordinate
		 * @param y
		 *            y coordinate
		 */
		public void moveTo(double x, double y) {
			flush();
			g.moveTo(x, y);
			initialX = x;
			initialY = y;
		}

		/**
		 * This makes a cubic curve to based on xf and yf that ranges from
		 * [t0,t1]. So this takes a little subset of the curves if [t0,t1] is
		 * smaller than [0,1].
		 * 
		 * @param xf
		 *            x-function
		 * @param yf
		 *            y-function
		 * @param t0
		 *            start parameter
		 * @param t1
		 *            end parameter
		 */
		public void curveTo(Function xf, Function yf, double t0, double t1) {
			flush(); // flush out lines

			double dt = t1 - t0;
			// I know I'm not explaining the math here, but you can derive
			// it with a little time and a few sheets of paper. The API for
			// the PathIterator shows the equations relating to bezier
			// parametric
			// curves. From there you can calculate whatever you need:
			// it just might take a few pages of pen & paper.
			double dx0 = xf.getDerivative(t0) * dt;
			double dx1 = xf.getDerivative(t1) * dt;
			double dy0 = yf.getDerivative(t0) * dt;
			double dy1 = yf.getDerivative(t1) * dt;
			double x0 = xf.evaluate(t0);
			double x1 = xf.evaluate(t1);
			double y0 = yf.evaluate(t0);
			double y1 = yf.evaluate(t1);

			g.curveTo(x0 + dx0 / 3, y0 + dy0 / 3, x1 - dx1 / 3,
					y1 - dy1 / 3, x1, y1);
		}

		/**
		 * Adds a line to (x,y)
		 * <P>
		 * This method doesn't actually commit a line until it's sure that it
		 * isn't writing heavily redundant lines. That is the points (0,0),
		 * (5,0) and (2,0) would be consolidated so only the first and last
		 * point remained.
		 * <P>
		 * However only horizontal/vertical lines are consolidated, because this
		 * method is aimed at clipping to (non-rotated) rectangles.
		 * 
		 * @param x
		 *            x coordinate
		 * @param y
		 *            y coordinate
		 * 
		 */
		public void lineTo(double x, double y) {
			if (uncommittedPoints.size() > 0) {
				double[] last = uncommittedPoints.peek();
				// are we adding the same point?
				if (Math.abs(last[0] - x) < TOLERANCE
						&& Math.abs(last[1] - y) < TOLERANCE) {
					return;
				}
			}

			double[] f = doubleFactory.getArray(2);
			f[0] = x;
			f[1] = y;
			uncommittedPoints.push(f);
		}

		/**
		 * Close the path
		 */
		public void closePath() {
			lineTo(initialX, initialY);
			flush();
			g.closePath();
		}

		/** Flush out the stack of uncommitted points. */
		public void flush() {
			while (uncommittedPoints.size() > 0) {
				identifyLines: while (uncommittedPoints.size() >= 3) {
					double[] first = uncommittedPoints.get(0);
					double[] middle = uncommittedPoints.get(1);
					double[] last = uncommittedPoints.get(2);

					if (Math.abs(first[0] - middle[0]) < TOLERANCE
							&& Math.abs(first[0] - last[0]) < TOLERANCE) {
						// everything has the same x, so we have a vertical line
						double[] array = uncommittedPoints.remove(1);
						doubleFactory.putArray(array);
					} else if (Math.abs(first[1] - middle[1]) < TOLERANCE
							&& Math.abs(first[1] - last[1]) < TOLERANCE) {
						// everything has the same y, so we have a horizontal
						// line
						double[] array = uncommittedPoints.remove(1);
						doubleFactory.putArray(array);
					} else {
						break identifyLines;
					}
				}

				double[] point = uncommittedPoints.remove(0);
				g.lineTo(point[0], point[1]);
				doubleFactory.putArray(point);
			}
		}
	}

	/**
	 * A function used to describe one of the 2 parametric equations for a
	 * segment of a path. This can be thought of is f(t).
	 */
	static interface Function {
		/**
		 * evaluates this function at a given value
		 * 
		 * @param t
		 *            parameter
		 * @return function value
		 */
		public double evaluate(double t);

		/**
		 * Calculates all the t-values which will yield the result "f" in this
		 * function.
		 * 
		 * @param f
		 *            the function result you're searching for
		 * @param dest
		 *            the array the results will be stored in
		 * @param destOffset
		 *            the offset at which data will be added to the array
		 * @return the number of solutions found.
		 */
		public int evaluateInverse(double f, double[] dest, int destOffset);

		/**
		 * Return the derivative (df/dt) for a given value of t
		 * 
		 * @param t
		 *            parameter value
		 * @return derivative
		 */
		public double getDerivative(double t);
	}

	/** A linear function */
	static class LFunction implements Function {
		private double slope;
		private double intercept;

		/**
		 * Defines this linear function.
		 * 
		 * @param x1
		 *            at t = 0, x1 is the output of this function
		 * @param x2
		 *            at t = 1, x2 is the output of this function
		 */
		public void define(double x1, double x2) {
			slope = (x2 - x1);
			intercept = x1;
		}

		@Override
		public String toString() {
			return slope + "*t+" + intercept;
		}

		@Override
		public double evaluate(double t) {
			return slope * t + intercept;
		}

		@Override
		public int evaluateInverse(double x, double[] dest, int offset) {
			dest[offset] = (x - intercept) / slope;
			return 1;
		}

		@Override
		public double getDerivative(double t) {
			return slope;
		}
	}

	/** A quadratic function */
	static class QFunction implements Function {
		private double a;
		private double b;
		private double c;

		@Override
		public String toString() {
			return a + "*t*t+" + b + "*t+" + c;
		}

		/**
		 * Use the 3 control points of a bezier quadratic
		 * 
		 * @param x0
		 *            f(0)
		 * @param x1
		 *            f(0.5)
		 * @param x2
		 *            f(1)
		 */
		public void define(double x0, double x1, double x2) {
			a = x0 - 2 * x1 + x2;
			b = -2 * x0 + 2 * x1;
			c = x0;
		}

		@Override
		public double evaluate(double t) {
			return a * t * t + b * t + c;
		}

		@Override
		public double getDerivative(double t) {
			return 2 * a * t + b;
		}

		@Override
		public int evaluateInverse(double x, double[] dest, int offset) {
			double C = c - x;
			double det = b * b - 4 * a * C;
			if (det < 0) {
				return 0;
			}
			if (det == 0) {
				dest[offset] = (-b) / (2 * a);
				return 1;
			}
			det = Math.sqrt(det);
			dest[offset] = (-b + det) / (2 * a);
			dest[offset + 1] = (-b - det) / (2 * a);
			return 2;
		}
	}

	/** A cubic function */
	static class CFunction implements Function {
		private double a;
		private double b;
		private double c;
		private double d;
		/**
		 * Recycle arrays here. Remember this is possibly going to be 1 object
		 * called hundreds of times, so reusing the same arrays here will save
		 * us time & memory allocation. In current setup there is only 1 thread
		 * that will be using these values.
		 */
		private double[] t2;
		private double[] eqn;

		@Override
		public String toString() {
			return a + "*t*t*t+" + b + "*t*t+" + c + "*t+" + d;
		}

		/**
		 * @param x0
		 *            f(0)
		 * @param x1
		 *            f(1/3)
		 * @param x2
		 *            f(2/3)
		 * @param x3
		 *            f(1)
		 */
		public void define(double x0, double x1, double x2, double x3) {
			a = -x0 + 3 * x1 - 3 * x2 + x3;
			b = 3 * x0 - 6 * x1 + 3 * x2;
			c = -3 * x0 + 3 * x1;
			d = x0;
		}

		@Override
		public double evaluate(double t) {
			return a * t * t * t + b * t * t + c * t + d;
		}

		@Override
		public double getDerivative(double t) {
			return 3 * a * t * t + 2 * b * t + c;
		}

		@Override
		public int evaluateInverse(double x, double[] dest, int offset) {
			if (eqn == null) {
				eqn = new double[4];
			}

			eqn[0] = d - x;
			eqn[1] = c;
			eqn[2] = b;
			eqn[3] = a;
			if (offset == 0) {
				// int k = java.awt.geom.CubicCurve2D.solveCubic(eqn,dest);
				int k = EquationSolver.solveCubicS(eqn, dest, 1E-8);
				if (k < 0) {
					return 0;
				}
				return k;
			}
			if (t2 == null) {
				t2 = new double[3];
			}
			// int k = java.awt.geom.CubicCurve2D.solveCubic(eqn,t2);
			int k = EquationSolver.solveCubicS(eqn, t2, 1E-8);
			if (k < 0) {
				return 0;
			}
			for (int i = 0; i < k; i++) {
				dest[offset + i] = t2[i];
			}
			return k;
		}
	}

	/**
	 * This creates a <code>GeneralPath</code> representing <code>s</code> when
	 * clipped to <code>r</code>
	 * 
	 * @param s
	 *            a shape that you want clipped
	 * @param t
	 *            the transform to transform <code>s</code> by.
	 *            <P>
	 *            This may be <code>null</code>, indicating that <code>s</code>
	 *            should not be transformed.
	 * @param x0
	 *            x0, y0, w, h define the rectangle to clip to
	 * @param y0
	 *            x0, y0, w, h define the rectangle to clip to
	 * @param w
	 *            x0, y0, w, h define the rectangle to clip to
	 * @param h
	 *            x0, y0, w, h define the rectangle to clip to
	 * @return a <code>GeneralPath</code> enclosing the new shape.
	 */
	public static GGeneralPath clipToRect(GShape s, GAffineTransform t, int x0,
			int y0, int w, int h) {
		GPathIterator i = s.getPathIterator(t);
		ClippedPath p = new ClippedPath(i.getWindingRule());
		double initialX = 0;
		double initialY = 0;
		int k;
		double[] f = doubleFactory.getArray(6);
		double rTop = y0;
		double rLeft = x0;
		double rRight = x0 + w;
		double rBottom = y0 + h;
		boolean shouldClose = false;
		double lastX = 0;
		double lastY = 0;
		boolean lastValueWasCapped, thisValueIsCapped, midValueInvalid;
		double cappedX, cappedY, x, y, x2, y2;

		// create 1 copy of all our possible functions,
		// and recycle these objects constantly
		// this way we avoid memory allocation:
		LFunction lxf = new LFunction();
		LFunction lyf = new LFunction();
		QFunction qxf = new QFunction();
		QFunction qyf = new QFunction();
		CFunction cxf = new CFunction();
		CFunction cyf = new CFunction();
		Function xf = null;
		Function yf = null;
		double[] interestingTimes = new double[16];
		int tCtr;

		while (!i.isDone()) {
			k = i.currentSegment(f);
			if (k == GPathIterator.SEG_MOVETO) {
				initialX = f[0];
				initialY = f[1];
				cappedX = f[0];
				cappedY = f[1];
				if (cappedX < rLeft) {
					cappedX = rLeft;
				}
				if (cappedX > rRight) {
					cappedX = rRight;
				}
				if (cappedY < rTop) {
					cappedY = rTop;
				}
				if (cappedY > rBottom) {
					cappedY = rBottom;
				}
				p.moveTo(cappedX, cappedY);
				lastX = f[0];
				lastY = f[1];
			} else if (k == GPathIterator.SEG_CLOSE) {
				f[0] = initialX;
				f[1] = initialY;
				k = GPathIterator.SEG_LINETO;
				shouldClose = true;
			}
			xf = null;
			if (k == GPathIterator.SEG_LINETO) {
				lxf.define(lastX, f[0]);
				lyf.define(lastY, f[1]);

				xf = lxf;
				yf = lyf;
			} else if (k == GPathIterator.SEG_QUADTO) {
				qxf.define(lastX, f[0], f[2]);
				qyf.define(lastY, f[1], f[3]);

				xf = qxf;
				yf = qyf;
			} else if (k == GPathIterator.SEG_CUBICTO) {
				cxf.define(lastX, f[0], f[2], f[4]);
				cyf.define(lastY, f[1], f[3], f[5]);

				xf = cxf;
				yf = cyf;
			}
			if (xf != null) {
				// gather all the t values at which we might be
				// crossing the bounds of our rectangle:

				tCtr = 0;

				tCtr += xf.evaluateInverse(rLeft, interestingTimes, tCtr);
				tCtr += xf.evaluateInverse(rRight, interestingTimes, tCtr);
				tCtr += yf.evaluateInverse(rTop, interestingTimes, tCtr);
				tCtr += yf.evaluateInverse(rBottom, interestingTimes, tCtr);
				interestingTimes[tCtr++] = 1;
				// we never actually calculate with 0, but we need to know it's
				// in the list
				interestingTimes[tCtr++] = 0;

				// put them in ascending order:
				Arrays.sort(interestingTimes, 0, tCtr);

				lastValueWasCapped = !(lastX >= rLeft && lastX <= rRight
						&& lastY >= rTop && lastY <= rBottom);

				for (int a = 0; a < tCtr; a++) {
					if (a > 0
							&& interestingTimes[a] == interestingTimes[a - 1]) {
						// do nothing
					} else if (interestingTimes[a] > 0
							&& interestingTimes[a] <= 1) {
						// this is the magic: take 2 t values and see what we
						// need to
						// do with them.
						// Remember we can make redundant horizontal/vertical
						// lines
						// all we want to because the ClippedPath will clean up
						// the mess.
						x = xf.evaluate(interestingTimes[a]);
						y = yf.evaluate(interestingTimes[a]);
						cappedX = x;
						cappedY = y;

						if (cappedX < rLeft) {
							cappedX = rLeft;
						} else if (cappedX > rRight) {
							cappedX = rRight;
						}
						if (cappedY < rTop) {
							cappedY = rTop;
						} else if (cappedY > rBottom) {
							cappedY = rBottom;
						}

						thisValueIsCapped = !(Math.abs(x - cappedX) < TOLERANCE
								&& Math.abs(y - cappedY) < TOLERANCE);

						x2 = xf.evaluate(
								(interestingTimes[a] + interestingTimes[a - 1])
										/ 2);
						y2 = yf.evaluate(
								(interestingTimes[a] + interestingTimes[a - 1])
										/ 2);
						midValueInvalid = !(rLeft <= x2 && x2 <= rRight
								&& rTop <= y2 && y2 <= rBottom);

						if ((xf instanceof LFunction) || thisValueIsCapped
								|| lastValueWasCapped || midValueInvalid) {
							p.lineTo(cappedX, cappedY);
						} else if ((xf instanceof QFunction)
								|| (xf instanceof CFunction)) {
							p.curveTo(xf, yf, interestingTimes[a - 1],
									interestingTimes[a]);
						} else {
							throw new RuntimeException("Unexpected condition.");
						}

						lastValueWasCapped = thisValueIsCapped;
					}
				}
				lastX = xf.evaluate(1);
				lastY = yf.evaluate(1);
			}
			if (shouldClose) {
				p.closePath();
				shouldClose = false;
			}
			i.next();
		}
		p.flush();
		doubleFactory.putArray(f);
		return p.g;
	}
}
