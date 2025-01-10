package org.geogebra.common.kernel.statistics;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import java.util.TreeSet;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

/**************
 * 
 * Fits a+b*sin(c*x+d) to a list of points. Adapted from: Nonlinear regression
 * algorithms are well known, see:
 * mathworld.wolfram.com/NonlinearLeastSquaresFitting.html
 * ics.forth.gr/~lourakis/levmar Damping Parameter in Marquardt's Method, Hans
 * Bruun Nielsen, IMM-Rep 1999-05 The problem is more to find best initial
 * values for parameters, and here I was on my own, little information available
 * on this problem... Experiments showed me: c and d are most critical If a,b
 * and c are good, d is not that critical If c and d are good, a and b are not
 * that critical The Levenberg-Marquardt method makes c and d significantly less
 * critical. This led me to this algorithm: I a=average of y-data
 * b=(maxy-miny)/2 II period=2*|x_first_max - x_first_min| c=2pi/period The
 * first two extremums are found by my "direction-changing-algorithm" (Ulven
 * nov-08), using a flank of three points as an indicator of monotonous
 * increasing or decreasing function. (Same effort as finding a local max as
 * y1&lt;y2&gt;y3, but effectively equivalent to using 5 points:
 * y1&lt;y2&lt;y3&gt;y4&gt;y5) III simple iteration of d in &lt;-pi,pi&gt; to find a good
 * d (Critical if c is a bit off, so better than pi/2-c*xmax) IV Simplified
 * Levenberg-Marquardt method. (Could be optimized if/when I am able to
 * understand the mathematics behind it and be able to check if this is of any
 * value.) (Perhaps Donald Knuth could have done all this in 50 lines, but this
 * is the best I can do...) Constraints: &lt;List of Points&gt; should have at
 * least 5 points. There should also be three points on the row steadily
 * increasing or decreasing(y1&lt;=y2&lt;=y3 or y1&gt;=y2&gt;=y3)on each side/flank of
 * the first extremums. The points should cover at least two extremums of the
 * function. The two first extremums should not be too far from the extremums of
 * the solution curve. If more than one period, there should, at the very least,
 * be more than 6 points in each period. If any of these demands are not
 * satisfied, the solution curve might be unusable. Problems: Non-linear
 * regression is difficult, and the choice of initial values for the parameters
 * are highly critical. The algorithm here might converge to a local minimum. It
 * might also diverge, so MAX_ITERATIONS is set to 200. (Experience suggests
 * that more than 100 iterations give useless results.) ToDo: Use Discrete
 * Fourier Transform instead of step II and III. Experiments show that this adds
 * surprisingly little to the robustness of my simple algorithm, so this will
 * not be done unless user feedback indicates a need for more sophistication.
 * 
 * @author Hans-Petter Ulven
 * @version 22.11.08 (november)
 */

public class AlgoFitSin extends AlgoElement implements FitAlgo {

	// Tuning of noisefilter, Levenberg-Marquardt iteration, debug, rounding off
	// errors
	private final static double NOISEKILLER = 0.2D; // Kill local extremums
													// inside a+/-noisekiller*b;
													// 0.2 seems to be a good
													// value?
	private final static double LMFACTORDIV = 3.0d;
	private final static double LMFACTORMULT = 2.0d;
	private final static int MAXITERATIONS = 200;
	private final static double EPSILON = 1E-14d;
	private final static double EPSSING = 1E-20d;
	private final static double PI = Math.PI;
	private final static double TWO_PI = PI * 2;

	// a+bsin(cx+d)
	private double a;
	private double b;
	private double c;
	private double d;
	// datapoints
	private double[] xd;
	private double[] yd;
	private int size;

	private int iterations; // LM iterations
	private boolean error = false; // General catch-all

	// / --- GeoGebra obligatory: --- ///

	private GeoList geolist; // input
	private GeoFunction geofunction; // output

	/**
	 * Implements AlgoElement
	 * 
	 * @param cons
	 *            construction
	 * @param geolist
	 *            list of points
	 */
	public AlgoFitSin(Construction cons, GeoList geolist) {
		super(cons);
		this.geolist = geolist;
		geofunction = new GeoFunction(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.FitSin;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geolist;
		setOnlyOutput(geofunction);
		setDependencies();
	}

	/** @return resulting function */
	public GeoFunction getFitSin() {
		return geofunction;
	}

	@Override
	public final void compute() {
		size = geolist.size();
		error = false; // General flag
		if (!geolist.isDefined() || size < 2 || size == 3) { // Direction-algo needs two
													// flanks, 3 in each.
			geofunction.setUndefined();
			Log.debug(
					"List not properly defined or too small (4 points needed).");
			return;
		}
		getPoints();
		if (size == 2) {
			c = Math.PI / (xd[1] - xd[0]);
			a = (yd[1] + yd[0]) / 2;
			b = (yd[1] - yd[0]) / 2;
			if (b == 0) {
				d = 0;
			} else {
				// y=a+b*sin(cx+d), clamp needed to avoid asin(1.00000001)
				double sin = MyMath.clamp((yd[0] - a) / b, -1, 1);
				d = Math.asin(sin) - c * xd[0];
			}
		} else {
			try {
				doReg();
			} catch (Exception all) {
				error = true;
			}
		}
		if (!error) {
			buildFunction();
		} else {
			geofunction.setUndefined();
		}
	}

	private void buildFunction() {
		MyDouble A = new MyDouble(kernel, a);
		MyDouble B = new MyDouble(kernel, b);
		MyDouble C = new MyDouble(kernel, c);
		MyDouble D = new MyDouble(kernel, d);
		FunctionVariable X = new FunctionVariable(kernel);
		ExpressionValue expr = new ExpressionNode(kernel, C,
				Operation.MULTIPLY, X);
		expr = new ExpressionNode(kernel, expr, Operation.PLUS, D);
		expr = new ExpressionNode(kernel, expr, Operation.SIN, null);
		expr = new ExpressionNode(kernel, B, Operation.MULTIPLY, expr);

		ExpressionNode node = new ExpressionNode(kernel, A, Operation.PLUS,
				expr);
		Function f = new Function(node, X);
		geofunction.setFunction(f);
		geofunction.setDefined(Double.isFinite(a) && Double.isFinite(b)
				&& Double.isFinite(c) && Double.isFinite(d));
	}

	// / ============= IMPLEMENTATION
	// =============================================================///
	/** Does the math part of the regression */
	public final void doReg() {
		findParameters(); // Find initial parameters a,b,c,d
		sinus_Reg(); // Run LM nonlinear iteration
	}

	/** Tries to find good initial values for a,b,c,d */
	public final void findParameters() {
		double y;
		double min_max_distance; // Distance between x-values of found(?)
									// extrema
		int numberofhalfperiods = 1; // Between the extrema
		int xmax_abs = 0, xmin_abs = 0; // Update in case changes=0 later
										// (few-data-case)
		size = xd.length;
		double sum = 0.0d, max = -Double.MAX_VALUE, min = Double.MAX_VALUE;
		// Find a and b:
		for (int i = 0; i < size; i++) {
			y = yd[i];
			sum += y;
			if (y > max) {
				max = y;
				xmax_abs = i;
			}
			if (y < min) {
				min = y;
				xmin_abs = i;
			}
		} // for
		a = sum / size;
		b = (max - min) / 2.0d;

		// Find c:
		// This time first and second local max/min, between rise and fall and
		// vv
		// Last y in a rise or decrease *is* the local extremum!
		int xmax = xmax_abs, xmin = xmin_abs; // Keep absolute xmax/xmin in case
												// changes=0 or 1 later
		int state = 0; // undecided so far...
		int current = 0;
		int changes = 0; // undecided so far...

		for (int i = 2; i < size; i++) {
			y = yd[i];
			current = direction(yd[i - 2], yd[i - 1], y);
			// current=direction5(yd[i-4],yd[i-3],yd[i-2],yd[i-1],yd[i]);
			if ((current == 1) || (current == -1)) { // Steady up or steady down
				// do state bookkeeping
				if (state == 0) { // just started:
					state = current; // set first state
				} else { // we are on our way...
					if (current != state) { // Update
											// eventual
											// change
						if (nearmaxmin(a, b, state, current, max, min)) { // Kill
																			// noise
							changes++;
							state = current;
						} // if near
					} // if change
				} // if steady up or down

				// Two changes enough. (Must check before updating extremums.)
				if (changes >= 2) { // debug("Two changes on "+i);
					// go on counting changes!break;
				} else { // if changes>=2

					// Update extremums so far
					if (current == 1) { // Steady up
						max = y;
						xmax = i; // Last is max so far
					} else if (current == -1) { // Steady down
						min = y;
						xmin = i; // Last is min so far
					} // update extremums
				} // if changes<2
			} // else: Not steady, nothing to do...
		}

		// Checking half-period:
		min_max_distance = Math.abs(xd[xmax] - xd[xmin]);
		if (changes <= 1) { // Did not succeed, abs extrema probably best
			xmin = xmin_abs;
			xmax = xmax_abs;
			min_max_distance = Math.abs(xd[xmin] - xd[xmax]); // Update for
																// final c
																// further down
			// min_max_distance might be 1,3,5,... halfperiods...find the one
			// that gives the least sse
			numberofhalfperiods = findNumberOfHalfPeriods(size / 4, xmin, xmax); // At
																					// least
																					// 6
																					// (14.02.09:4)
																					// points
																					// in
																					// a
																					// period,
																					// hopefully
		} // if too few extrema
		c = PI * numberofhalfperiods / min_max_distance;
		double c2 = 2 * Math.PI / ((xd[size - 1] - xd[0]) * 2 / changes);
		if (changes > 2) {
			c = (c + c2) / 2; // compromise?
		}

		// Find d
		// (d=0 might go well, but to be on the safe side...100 iterations
		// should be enough?
		// Could also use pi/2=c*xmax+d, but iteration is more robust in bad
		// cases.
		// If a,b and c are a bit off, d should be good!
		d = -Math.PI;
		double deltad = Math.PI * 2 * 0.01;
		double err = 0.0d;
		double bestd = 0.0d;
		double old_err = beta(xd, yd, a, b, c, d);
		for (int i = 0; i < 100; i++) {
			d += deltad;
			err = beta(xd, yd, a, b, c, d); // Without squaring is ok...
			if (err < old_err) {
				old_err = err;
				bestd = d;
			}
		}
		d = bestd;
	}

	/** Doing LM iteration */
	public final void sinus_Reg() {
		double lambda = 0.0d; // LM-damping coefficient
		double multfaktor = LMFACTORMULT; // later?: divfaktor=LMFACTORDIV;
		double residual, old_residual = beta2(xd, yd, a, b, c, d);

		double da = EPSILON, db = EPSILON, dc = EPSILON, dd = EPSILON; // Something
																		// larger
																		// than
																		// eps,
																		// to
																		// get
																		// started...
		double b1, b2, b3, b4; // At*beta
		double m11, m12, m13, m14, m21, m22, m23, m24, m31, m32, m33, m34, m41,
				m42, m43, m44, // At*A
				n; // singular check
		double x, y;
		double dfa, dfb, dfc, dfd, beta, newa, newb, newc, newd;

		iterations = 0;
		// LM: Optimal startlambda
		b1 = b2 = b3 = b4 = 0.0d;
		m11 = m22 = m33 = m44 = 0.0d;
		for (int i = 0; i < size; i++) {
			x = xd[i];
			y = yd[i];
			beta = beta(x, y, a, b, c, d);
			dfa = df_a();
			dfb = df_b(x, c, d);
			dfc = df_c(x, b, c, d);
			dfd = df_d(x, b, c, d);
			// b=At*beta
			b1 += beta * dfa;
			b2 += beta * dfb;
			b3 += beta * dfc;
			b4 += beta * dfd;
			// m=At*A
			m11 += dfa * dfa; // only need diagonal
			m22 += dfb * dfb;
			m33 += dfc * dfc;
			m44 += dfd * dfd;
		} // for all datapoints
		double startfaktor = Math.max(Math.max(Math.max(m11, m22), m33), m44);
		lambda = startfaktor * 0.001; // Heuristic, suggested by several
										// articles
		while (Math.abs(da) + Math.abs(db) + Math.abs(dc)
				+ Math.abs(dd) > EPSILON) {

			iterations++; // debug(""+iterations+" : ");
			if ((iterations > MAXITERATIONS) || error) { // From experience:
															// >100 gives
															// unusable result
				Log.debug("More than " + MAXITERATIONS + " iterations...");
				error = true; // 14.02.09: No use=>undefined!
				break;
			} // if diverging
			b1 = b2 = b3 = b4 = 0.0d;
			m11 = m12 = m13 = m14 = m21 = m22 = m23 = m24 = 0.0d;
			m31 = m32 = m33 = m34 = m41 = m42 = m43 = m44 = 0.0d;
			for (int i = 0; i < size; i++) { // for all datapoints
				x = xd[i];
				y = yd[i];
				beta = beta(x, y, a, b, c, d);
				dfa = df_a();
				dfb = df_b(x, c, d);
				dfc = df_c(x, b, c, d);
				dfd = df_d(x, b, c, d);
				// b=At*beta
				b1 += beta * dfa;
				b2 += beta * dfb;
				b3 += beta * dfc;
				b4 += beta * dfd;
				// m=At*A
				m11 += dfa * dfa + lambda;
				m12 += dfa * dfb;
				m13 += dfa * dfc;
				m14 += dfa * dfd;
				m22 += dfb * dfb + lambda;
				m23 += dfb * dfc;
				m24 += dfb * dfd;
				m33 += dfc * dfc + lambda;
				m34 += dfc * dfd;
				m44 += dfd * dfd + lambda;
			} // for all datapoints

			// Symmetry:
			m21 = m12;
			m31 = m13;
			m32 = m23;
			m41 = m14;
			m42 = m24;
			m43 = m34;

			n = RegressionMath.det44(m11, m12, m13, m14, m21, m22, m23, m24,
					m31, m32, m33, m34, m41, m42, m43, m44);

			if (Math.abs(n) < EPSSING) { // Sinular matrix?
				error = true;
				Log.debug("Singular matrix...");
				da = db = dc = dd = 0; // To stop it all...
			} else {
				da = RegressionMath.det44(b1, m12, m13, m14, b2, m22, m23, m24,
						b3, m32, m33, m34, b4, m42, m43, m44) / n;
				db = RegressionMath.det44(m11, b1, m13, m14, m21, b2, m23, m24,
						m31, b3, m33, m34, m41, b4, m43, m44) / n;
				dc = RegressionMath.det44(m11, m12, b1, m14, m21, m22, b2, m24,
						m31, m32, b3, m34, m41, m42, b4, m44) / n;
				dd = RegressionMath.det44(m11, m12, m13, b1, m21, m22, m23, b2,
						m31, m32, m33, b3, m41, m42, m43, b4) / n;

				newa = a + da;
				newb = b + db;
				newc = c + dc;
				newd = d + dd; // Remember this, in case we have to go back...
				residual = beta2(xd, yd, newa, newb, newc, newd); // debug("ChiSqError:
																	// +"+residual);
				// diff=residual-old_residual;
				// //debug("Residual difference: "+diff+" lambda: "+lambda);
				if (residual < old_residual) {
					lambda = lambda / LMFACTORDIV; // going well :-) But don't
													// overdo it...
					old_residual = residual;
					multfaktor = LMFACTORMULT; // Reset this!
					a = newa;
					b = newb;
					c = newc;
					d = newd;
				} else {
					lambda = lambda * multfaktor; // Not going well :-(
					multfaktor *= 2; // LM drives hard...
				} // if going the right way
			}
		}

		// Reduce d to interval <-pi,pi>
		// d=Rdft.reduce(d);//put here not in rdft!

		double reduction = Math.PI * 2;
		while (Math.abs(d) > Math.PI) {
			if (d > Math.PI) {
				d -= reduction;
			}
			if (d < -Math.PI) {
				d += reduction;
			}
		}

		if (Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(c)
				|| Double.isNaN(d) || error) {
			a = b = c = d = Double.NaN;
			error = true;
		}
	}

	/* sin(Cx+D) */
	private final static double sin(double x, double c, double d) {
		return Math.sin(c * x + d);
	}

	/* cos(Cx+D) */
	private final static double cos(double x, double c, double d) {
		return Math.cos(c * x + d);
	}

	/* f(x)=A+Bsin(Cx+D) */
	private final static double f(double x, double a, double b, double c,
			double d) {
		return a + b * sin(x, c, d);
	}

	/* Partial derivative of f to a */
	private final static double df_a() {
		return 1.0d;
	}

	/* Partial derivative of f to b: sin(cx+d) */
	private final static double df_b(double x, double c, double d) {
		return sin(x, c, d);
	}

	/* Partial derivative of f to c: cos(cx+d)*B*x */
	private final static double df_c(double x, double b, double c, double d) {
		return cos(x, c, d) * b * x;
	}

	/* Partial derivative of f to d: Bcos(cx+d) */
	private final static double df_d(double x, double b, double c, double d) {
		return cos(x, c, d) * b;
	}

	/* Difference to be reduced */
	private final static double beta(double x, double y, double a, double b,
			double c, double d) {
		return y - f(x, a, b, c, d);
	}

	/* Sum of quadratic errors */
	private final static double beta2(double[] x, double[] y, double a,
			double b, double c, double d) {
		double sum = 0.0d, beta;
		int n = x.length;
		for (int i = 0; i < n; i++) {
			beta = beta(x[i], y[i], a, b, c, d);
			sum += beta * beta;
		} // for all datapoints
		return sum;
	}

	// Sum of errors (absolute values)
	private final static double beta(double[] x, double[] y, double a, double b,
			double c, double d) {
		double sum = 0.0d;
		int n = x.length;
		for (int i = 0; i < n; i++) {
			sum += Math.abs(beta(x[i], y[i], a, b, c, d));
		} // for all datapoints
		return sum;
	}

	// 3 yd's on the row: up=1, down=-1, uncertain=0
	private static int direction(double y1, double y2, double y3) {
		if ((y3 > y2) && (y2 > y1)) { // Rising!
			return 1;
		} else if ((y1 > y2) && (y2 > y3)) { // All under a
			return -1;
		} else { // Some over, some under...
			return 0;
		} // if
	}

	// Get Points and sort them. (Could find abs max and min as well,
	// that is done in findParameters() which is better for testing only
	// mathematical functionality.)
	private final void getPoints() {
		double[] xlist = null, ylist = null;
		double[] xy = new double[2];
		GeoElement geoelement;
		// GeoList newlist;
		// This is code duplication of AlgoSort, but for the time being:
		TreeSet<GeoPoint> sortedSet;
		sortedSet = new TreeSet<>(GeoPoint.getComparatorX());
		for (int i = 0; i < size; i++) {
			geoelement = geolist.get(i);
			if (geoelement instanceof GeoPoint) {
				sortedSet.add((GeoPoint) geoelement);
			} else {
				error = true;
			}
		}

		int i = 0;
		xlist = new double[size];
		ylist = new double[size];
		for (GeoPoint gp: sortedSet) {
			gp.getInhomCoords(xy);
			xlist[i] = xy[0];
			ylist[i] = xy[1];
			i++;
		}
		xd = xlist;
		yd = ylist;
		if (error) {
			Log.debug("getPoints(): Wrong list format, must be points.");
		}
	}

	// Noisekiller
	private final static boolean nearmaxmin(double a, double b, int state,
			int current, double max, double min) {
		if ((state == 1) && (current == -1)) { // A real max-change?
			return max > a + NOISEKILLER * b;
		} else if ((state == -1) && (current == 1)) { // A real min-change?
			return min < a - NOISEKILLER * b;
		} else {
			return false; // Should not happen...
		}
	}

	// Is distance between abs max and abx min 1,3,5,... halfperiodes?
	// To be used if DFT is used in finding good initial values
	// Finds the number of halfperiods between abs max and abs min that gives
	// the least SSE
	private final int findNumberOfHalfPeriods(int k, int xmin, int xmax) {
		double min_error = Double.MAX_VALUE;
		double error1;
		double period, c1;
		int n = 0, best = 0;
		for (int i = 1; i <= k; i++) { // for all actual frequencies
			n = 2 * i - 1; // number of halfperiods
			period = Math.abs(xd[xmax] - xd[xmin]) * 2.0 / n;
			c1 = TWO_PI / period;
			error1 = beta2(xd, yd, a, b, c1, PI / 2.0d - c1 * xd[xmax]);
			if (error1 < min_error) {
				min_error = error1;
				best = n;
			}
		}
		return best;
	}

	@Override
	public double[] getCoeffs() {
		return new double[]{ a, b, c, d };
	}

}
