package org.geogebra.common.kernel.statistics;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import java.util.Iterator;
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
import org.geogebra.common.util.debug.Log;

/*******************
 * Fits c/(1+aexp(-bx)) to a list of points. Adapted from: Nonlinear regression
 * algorithms are well known, see:
 * mathworld.wolfram.com/NonlinearLeastSquaresFitting.html
 * ics.forth.gr/~lourakis/levmar Damping Parameter in Marquardt's Method, Hans
 * Bruun Nielsen, IMM-Rep 1999-05
 * 
 * The problem is to find the best initial values for the parameters, little
 * information available on this problem...
 * 
 * Bjorn Ove Thue, the norwegian translator and programmer of the norwegian
 * version of WxMaxima, was kind enough to give me his idea: Make the assumption
 * that the first and last point are close to the solution curve. Calculate c
 * and a from those points, with b as parameter, iterate to a good value for b,
 * and do the final nonlinear regression iteration with all three parameters.
 * 
 * Constraints: &lt;List of Points&gt; should have at least 3 points. The first and
 * last datapoint should not be too far from the solution curve. Negative a,b
 * and c: Asymptotes: Quality of points get even more important, should not be
 * too close to either vertical or horisontal asymptotes, should have several
 * "good" points on each branch of the curve. (Positive a, b and c is quite
 * robust though. :-) ) Problems: Non-linear regression is difficult, and the
 * choice of initial values for the parameters are highly critical. The
 * algorithm here might converge to a local minimum. The algorithm might diverge,
 * so after MAXITERATIONS the result will be unusable
 * 
 * Possible future solution: Make more commands where you give both a list and
 * suggestions for the parameters?
 * 
 * @author Hans-Petter Ulven
 * @version 22.11.08
 */

public final class AlgoFitLogistic extends AlgoElement implements FitAlgo {

	// Tuning of noisefilter, Levenberg-Marquardt iteration, debug, rounding off
	// errors
	private final static double LMFACTORDIV = 3.0d;
	private final static double LMFACTORMULT = 2.0d;
	private final static int MAXITERATIONS = 200; // 100 probably enough,
													// nothing is usable after
													// that...
	private final static double EPSILONFIND = 1E-6d;
	private final static double EPSILONREG = 1E-14d;
	private final static double EPSSING = 1E-20d;

	// Properties
	private double a;
	private double b;
	private double c; // c/(1+a*exp(-bx))
	// datapoints
	private double[] xd;
	private double[] yd;
	private int size; // of xd and yd
	private int iterations; // LM iterations
	private boolean error = false; // general error flag

	// Flags for y-values, set by getPoints();
	private boolean allplus;
	private boolean allneg;

	// GeoGebra obligatory:
	private GeoList geolist; // input
	private GeoFunction geofunction; // output
	// Variables in calcultions that tries to prevent rounding off errors:
	private double x1;
	private double y1;
	private double x2;
	private double y2;
	private double ymult;
	private double e1;
	private double e2;
	private double emult;
	private double ydiff;

	/**
	 * @param cons
	 *            construction
	 * @param geolist
	 *            input points
	 */
	public AlgoFitLogistic(Construction cons, GeoList geolist) {
		super(cons);
		this.geolist = geolist;
		geofunction = new GeoFunction(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.FitLogistic;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geolist;
		setOnlyOutput(geofunction);
		setDependencies();
	}

	/**
	 * @return resulting function
	 */
	public GeoFunction getFitLogistic() {
		return geofunction;
	}

	@Override
	public void compute() {
		size = geolist.size();
		error = false; // General flag
		if (!geolist.isDefined() || (size < 3)) { // Need three points, at the
													// very least...
			geofunction.setUndefined();
			return;
		}

		getPoints(); // sorts the points on x while getting them!
		try {
			doReg();
		} catch (Exception all) {
			error = true;
		} // try-catch
		if (!error) {
			// a=2.0d;c=3.0d;b=0.5d;
			MyDouble A = new MyDouble(kernel, a);
			MyDouble B = new MyDouble(kernel, -b);
			MyDouble C = new MyDouble(kernel, c);
			MyDouble ONE = new MyDouble(kernel, 1.0d);
			FunctionVariable X = new FunctionVariable(kernel);
			ExpressionValue expr = new ExpressionNode(kernel, B,
					Operation.MULTIPLY, X);
			expr = new ExpressionNode(kernel, expr, Operation.EXP, null);
			expr = new ExpressionNode(kernel, A, Operation.MULTIPLY, expr);
			expr = new ExpressionNode(kernel, ONE, Operation.PLUS, expr);
			ExpressionNode node = new ExpressionNode(kernel, C,
					Operation.DIVIDE, expr);
			Function f = new Function(node, X);
			geofunction.setFunction(f);
			geofunction.setDefined(true);
		} else {
			geofunction.setUndefined();
			return;
		} // if error in regression
	}

	// / ============= IMPLEMENTATION
	// =============================================================///
	private void doReg() {
		findParameters(); // Find initial parameters a,b,c,d
		logisticReg(); // Run LM nonlinear iteration
	}

	private void findParameters() {
		int sign = 1;
		double k = 0.001d; // debug("findParameters():\n================");
		// Remember some values to speed up later calculations:
		x1 = xd[0];
		y1 = yd[0];
		x2 = xd[size - 1];
		y2 = yd[size - 1];
		ymult = y1 * y2;
		e1 = Math.exp(x1);
		e2 = Math.exp(x2);
		emult = e1 * e2;
		ydiff = y1 - y2;

		// Handling negative a,b or c. (To avoid iteration across asymptotic
		// singularity.)
		boolean increasing; // allplus, allneg set by getpoints()
		if (y1 < y2) {
			increasing = true;
		} else {
			increasing = false;
		}

		if (allplus) { // a>0 and c>0
			if (!increasing) {
				sign = -1;
				k = -k;
			} // k<0 else k>0
		} else if (allneg) { // a>0 and c<0
			if (increasing) {
				sign = -1;
				k = -k;
			} // k<0 else k<0
		} else { // a<0: 4 cases to sort out: Last value closer to x-axis than
					// first value=>k<0
			if (Math.abs(y2) < Math.abs(y1)) {
				sign = -1;
				k = -k;
			} // if
		} // if
			// debug("increasing: "+increasing+" allpos: "+allplus+" allneg:
			// "+allneg);

		// / Iterate for best k: ///
		double err_old = beta2(k);
		double lambda = 0.01d;
		k = k + sign * lambda;
		double err = err_old + 1; // to start off the while:
		while (Math.abs(err - err_old) > EPSILONFIND) {
			err = beta2(k);
			// negerr=beta2(xd,yd,-k);
			// if(Math.abs(negerr)<Math.abs(err)){//change to neg k
			// k=-k;sign=-1*sign;err=negerr;
			// }
			if (err < err_old) {
				lambda = lambda * 5; // going right way:-)
				err_old = err;
				err = err + 1; // to keep going...
			} else {
				k = k - sign * lambda; // go back and try again
				lambda = lambda / 5;
			} // if progress
			k += sign * lambda;
			// debug("b, error-error_old: "+k+" , "+diff);
		} // while reduction in error
			// Set params for final iteration:
		b = k; // next routine uses c,a,b...
		a = a(x1, y1, x2, y2, k);
		c = c(x1, y1, x2, y2, k);
		// debug("\nfindParameters()finished with:\n"+a+" b= "+b+" c= "+c);
		// debug("Sum sq. errors: "+beta2(xd,yd,a,b,c)+"\n-------------");
		if (Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(c)) {
			error = true;
			Log.debug("findParameters(): a,b or c undefined");
			return;
		} // 20.11:if one is undefined, everything is undefined
	}

	private void logisticReg() {

		double lambda = 0.0d; // LM-damping coefficient
		double multfaktor = LMFACTORMULT; // later?: divfaktor=LMFACTORDIV;
		double residual, old_residual = beta2(xd, yd, a, b, c);
		// double diff = -1.0d; //negative to start it off

		double da = EPSILONREG, db = EPSILONREG, dc = EPSILONREG; // Something
																	// larger
																	// than eps,
																	// to get
																	// started...
		double b1, b2, b3; // At*beta
		double m11, m12, m13, m21, m22, m23, m31, m32, m33, // At*A
				n; // singular check
		double x, y;
		double dfa, dfb, dfc, beta, newa, newb, newc;
		iterations = 0;
		// ****checked up to here
		// LM: optimal startlambda
		b1 = b2 = b3 = 0.0d;
		m11 = m22 = m33 = 0.0d;
		for (int i = 0; i < size; i++) {
			x = xd[i];
			y = yd[i];
			beta = beta(x, y, a, b, c);
			dfa = df_a(x, a, b, c);
			dfb = df_b(x, a, b, c);
			dfc = df_c(x, a, b);
			// b=At*beta
			b1 += beta * dfa;
			b2 += beta * dfb;
			b3 += beta * dfc;
			// m=At*A
			m11 += dfa * dfa; // only need diagonal
			m22 += dfb * dfb;
			m33 += dfc * dfc;
		} // for all datapoints

		double startfaktor = Math.max(Math.max(m11, m22), m33);
		lambda = startfaktor * 0.001; // heuristic... (Set to zero if no LM)

		while (Math.abs(da) + Math.abs(db) + Math.abs(dc) > EPSILONREG) {
			// or while(Math.abs(diff)>EPSILON) ?
			iterations++;
			if ((iterations > MAXITERATIONS) || error) { // From experience:
															// >200 gives
															// nothing more...
				Log.debug("More than " + MAXITERATIONS
						+ " iterations. Solution is probably not usable.");
				break;
			}
			b1 = b2 = b3 = 0.0d;
			m11 = m12 = m13 = m21 = m22 = m23 = m31 = m32 = m33 = 0.0d;
			for (int i = 0; i < size; i++) {
				x = xd[i];
				y = yd[i];
				beta = beta(x, y, a, b, c);
				dfa = df_a(x, a, b, c);
				dfb = df_b(x, a, b, c);
				dfc = df_c(x, a, b);
				// b=At*beta
				b1 += beta * dfa;
				b2 += beta * dfb;
				b3 += beta * dfc;
				// m=At*A
				m11 += dfa * dfa + lambda;
				m12 += dfa * dfb;
				m13 += dfa * dfc;
				m22 += dfb * dfb + lambda;
				m23 += dfb * dfc;
				m33 += dfc * dfc + lambda;
			} // for all datapoints

			// Symmetry:
			m21 = m12;
			m31 = m13;
			m32 = m23;

			n = RegressionMath.det33(m11, m12, m13, m21, m22, m23, m31, m32,
					m33);

			if (Math.abs(n) < EPSSING) { // Not singular?
				error = true;
				Log.debug("Singular matrix...");
				da = db = dc = 0.0d; // to stop it all...
			} else {
				da = RegressionMath.det33(b1, m12, m13, b2, m22, m23, b3, m32,
						m33) / n;
				db = RegressionMath.det33(m11, b1, m13, m21, b2, m23, m31, b3,
						m33) / n;
				dc = RegressionMath.det33(m11, m12, b1, m21, m22, b2, m31, m32,
						b3) / n;
				newa = a + da;
				newb = b + db;
				newc = c + dc; // remember this and update later if ok
				residual = beta2(xd, yd, newa, newb, newc);
				// diff=residual-old_residual;
				// //debug("Residual difference: "+diff+" lambda: "+lambda);

				if (residual < old_residual) { // (Set to true if no LM)
					lambda = lambda / LMFACTORDIV; // going well :-) but don't
													// overdo it...
					old_residual = residual;
					multfaktor = LMFACTORMULT; // reset this!
					a = newa;
					b = newb;
					c = newc;
				} else {
					lambda = lambda * multfaktor; // not going well :-(
					multfaktor *= 2; // LM drives hard...
				} // if going the right way

			} // if(error)-else
				// debug(""+da+"\t"+db+"\t"+dc+"\n"+a+"\t"+b+"\t"+c);
		} // while(|da|+|db|+|dc|>epsilonreg)

		// 20.11: not wanted:
		// errorMsg("AlgoFitLogistic: Sum Errors Squared= "+beta2(xd,yd,a,b,c));
		// //Info

		if (Double.isNaN(a) || Double.isNaN(b) || Double.isNaN(c)) {
			error = true;
			Log.debug("findParameters(): a,b or c undefined");
			return;
		} // 20.11:if one is undefined, everything is undefined

	}

	// --- The Logistic Function and its derivates --- //

	/** Logistic function f(x)=c/(1+ae^(-bx)) */
	private static double f(double x, double a1, double b1, double c1) {
		return df_c(x, a1, b1) * c1;
	}

	// Adjusted f, used in findParameters(), when a and c are calculated from
	// first and last datapoint
	// Also tries to avoid rounding off errors
	private double f(double x, double k) { // k=b
		double e1k = Math.pow(e1, k), e2k = Math.pow(e2, k);
		double efrac = Math.pow(emult / Math.exp(x), k);
		return ymult * (e1k - e2k) / (y2 * e1k - y1 * e2k + ydiff * efrac);
	}

	// df/dc=1/(1+ae^(-bx))
	private static double df_c(double x, double a1, double b1) {
		return 1.0d / (1.0d + a1 * Math.exp(-b1 * x));
	}

	// df/da
	private static double df_a(double x, double a1, double b1,
			double c1) {
		double df_c = df_c(x, a1, b1);
		return df_c * df_c * Math.exp(-b1 * x) * (-c1);
	}

	// df/db
	private static double df_b(double x, double a1, double b1,
			double c1) {
		double df_c = df_c(x, a1, b1);
		return df_c * df_c * Math.exp(-b1 * x) * x * a1 * c1;
	}

	// / --- Error calculations --- ///
	// beta = yd-f(xd,yd,a,b,c)
	private static double beta(double x, double y, double a1, double b1,
			double c1) {
		return y - f(x, a1, b1, c1);
	}

	// beta = yd-f(x,b) for use in findParameters(). (a and c calculated)
	private double beta(double x, double y, double b1) {
		return y - f(x, b1);
	}

	// Sum of squared errors, using last a,b and c
	private double beta2(double[] x, double[] y, double a1, double b1,
			double c1) {
		double sum = 0.0d, beta;
		for (int i = 0; i < size; i++) {
			beta = beta(x[i], y[i], a1, b1, c1);
			sum += beta * beta;
		} // for all datapoints
			// debug("Sum Squared Errors: "+sum);
		return sum;
	}

	// Sum of squared errors, using b(=k). a and c are calculated from first and
	// last datapoint.
	private double beta2(double k1) {
		double beta = 0.0d, sum = 0.0d;
		for (int i = 0; i < size; i++) {
			beta = beta(xd[i], yd[i], k1);
			sum += beta * beta;
		} // for all data
		return sum;
	}

	// / --- Bjorn Ove Thue's trick --- ///
	// c as function of first and last point
	private static double c(double cx1, double cy1, double cx2,
			double cy2, double cb) {
		return cy1 * cy2 * (Math.exp(cb * cx1) - Math.exp(cb * cx2))
				/ (cy2 * Math.exp(cb * cx1) - cy1 * Math.exp(cb * cx2));
	}

	/** a as function of first and last point */
	private static double a(double ax1, double ay1, double ax2,
			double ay2, double ab) {
		return Math.exp(ab * (ax1 + ax2)) * (ay1 - ay2)
				/ (ay2 * Math.exp(ab * ax1) - ay1 * Math.exp(ab * ax2));
	}

	private void getPoints() {

		// problem bothering the gui: GeoList
		// newlist=k.Sort("tmp_{FitLogistic}",geolist);
		double[] xlist = null, ylist = null;
		double[] xy = new double[2];
		GeoPoint geoelement;
		// This is code duplication of AlgoSort, but for the time being:
		TreeSet<GeoPoint> sortedSet;
		sortedSet = new TreeSet<>(GeoPoint.getComparatorX());
		for (int i = 0; i < size; i++) {
			if (geolist.get(i) instanceof GeoPoint) {
				geoelement = (GeoPoint) geolist.get(i);
				sortedSet.add(geoelement);
			} else {
				error = true;
			} // if point
		} // for all points
		Iterator<GeoPoint> iter = sortedSet.iterator();
		int i = 0;
		allplus = true;
		allneg = true; // Need sign info in findParameters()
		xlist = new double[size];
		ylist = new double[size];
		while (iter.hasNext()) {
			geoelement = iter.next();
			geoelement.getInhomCoords(xy);
			xlist[i] = xy[0];
			ylist[i] = xy[1];
			if (ylist[i] < 0) {
				allplus = false;
			}
			if (ylist[i] > 0) {
				allneg = false;
			}
			i++;
		} // while iterating

		xd = xlist;
		yd = ylist;
		if (error) {
			Log.debug("getPoints(): Wrong list format...");
		}
	}

	@Override
	public double[] getCoeffs() {
		double[] ret = { a, b, c };
		return ret;
	}

}
