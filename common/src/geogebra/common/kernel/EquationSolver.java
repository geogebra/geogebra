/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel;

import geogebra.common.kernel.arithmetic.PolyFunction;
import geogebra.common.kernel.roots.RealRootAdapter;
import geogebra.common.kernel.roots.RealRootDerivAdapter;
import geogebra.common.main.App;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.math.analysis.solvers.LaguerreSolver;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolver;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactory;
import org.apache.commons.math.complex.Complex;
/**
 * Class for solving polynomial equations
 */
public class EquationSolver implements EquationSolverInterface {

	private static final double LAGUERRE_EPS = 1E-5;
	private LaguerreSolver laguerreSolver;
	private UnivariateRealSolver rootFinderBrent, rootFinderNewton;
	/**
	 * Createsnew equation solver
	 * @param kernel kernel
	 */
	public EquationSolver(Kernel kernel) {
		// we need someone to polish our roots
		// rootPolisher = new RealRoot();
		// extrFinder = kernel.getExtremumFinder();
	}

	final public int polynomialRoots(double[] roots, boolean multiple) {
		int realRoots;

		/*
		 * update: if roots[n], roots[n-1], ..., we need to determine "real"
		 * degree Darko Drakulic, 28.6.2011.
		 */

		int degree = roots.length - 1;
		for (int i = degree; i >= 0 && roots[i] == 0; i--)
			degree--;

		switch (degree) { // degree of polynomial
		case 0:
			realRoots = 0;
			break;

		case 1:
			roots[0] = -roots[0] / roots[1];
			realRoots = 1;
			break;

		case 2:
			realRoots = solveQuadratic(roots, roots, Kernel.STANDARD_PRECISION);
			if (multiple && realRoots == 1) {
				realRoots = 2;
				roots[1] = roots[0];
			}
			break;

		case 3:
			realRoots = solveCubic(roots, roots, Kernel.STANDARD_PRECISION);
			break;

		default:
			realRoots = laguerreAll(roots);
		}

		// solveQuadratic and solveCubic may return -1
		return Math.max(0, realRoots);
	}

	
	final public int polynomialComplexRoots(double[] real, double complex[]) {
		int ret = -1;
		switch (real.length - 1) { // degree of polynomial
		case 0:
			ret = 0;
			break;

		case 1:
			real[0] = -real[0] / real[1];
			complex[0] = 0;
			ret = 1;
			break;

		case 2:
			ret = solveQuadraticComplex(real, complex);
			break;

		default:
			ret = laguerreAllComplex(real, complex);
		}

		return ret;
	}

	/**
	 * Solves the quadratic whose coefficients are in the <code>eqn</code> array
	 * and places the non-complex roots back into the same array, returning the
	 * number of roots. The quadratic solved is represented by the equation:
	 * 
	 * <pre>
	 *     eqn = {C, B, A};
	 *     ax^2 + bx + c = 0
	 * </pre>
	 * 
	 * A return value of <code>-1</code> is used to distinguish a constant
	 * equation, which might be always 0 or never 0, from an equation that has
	 * no zeroes.
	 * 
	 * @param eqn
	 *            the array that contains the quadratic coefficients
	 * @return the number of roots, or <code>-1</code> if the equation is a
	 *         constant
	 */
	final public int solveQuadratic(double eqn[]) {
		return solveQuadratic(eqn, eqn, Kernel.STANDARD_PRECISION);
	}

	
	final public int solveQuadratic(double eqn[], double res[],double eps) {
		double a = eqn[2];
		double b = eqn[1];
		double c = eqn[0];
		int roots = 0;
		if (Math.abs(a) < eps) {
			// The quadratic parabola has degenerated to a line.
			if (Math.abs(b) < eps)
				// The line has degenerated to a constant.
				return -1;
			res[roots++] = -c / b;
		} else {
			// From Numerical Recipes, 5.6, Quadratic and Cubic Equations
			double d = b * b - 4.0 * a * c;
			if (Math.abs(d) < eps)
				res[roots++] = -b / (2.0 * a);
			else {
				if (d < 0.0)
					// If d < 0.0, then there are no roots
					return 0;
				d = Math.sqrt(d);
				// For accuracy, calculate one root using:
				// (-b +/- d) / 2a
				// and the other using:
				// 2c / (-b +/- d)
				// Choose the sign of the +/- so that b+d gets larger in
				// magnitude
				if (b < 0.0) {
					d = -d;
				}
				double q = (b + d) / -2.0;
				// We already tested a for being 0 above
				res[roots++] = q / a;
				res[roots++] = c / q;
			}
		}
		return roots;
	}

	/**
	 * @param real put coeffivients here on input, receive real parts on output
	 * @param complex imaginary parts on output
	 * @return number of roots
	 */
	final public static int solveQuadraticComplex(double real[],
			double complex[]) {
		double a = real[2];
		double b = real[1];
		double c = real[0];
		int roots = 0;

		if (Math.abs(a) < Kernel.EPSILON) {
			// The quadratic parabola has degenerated to a line.
			if (Math.abs(b) < Kernel.EPSILON)
				// The line has degenerated to a constant.
				return -1;
			complex[roots] = 0;
			real[roots++] = -c / b;
		} else {
			// From Numerical Recipes, 5.6, Quadratic and Cubic Equations
			double d = b * b - 4.0 * a * c;
			if (Math.abs(d) < Kernel.EPSILON) {
				complex[roots] = 0;
				real[roots++] = -b / (2.0 * a);
			} else {
				if (d < 0.0) {
					// If d < 0.0, then there are 2 complex roots
					d = Math.sqrt(-d);
					complex[0] = d / (2.0 * a);
					real[0] = -b / (2.0 * a);
					complex[1] = -complex[0];
					real[1] = real[0];
					roots = 2;
				} else {
					d = Math.sqrt(d);
					// For accuracy, calculate one root using:
					// (-b +/- d) / 2a
					// and the other using:
					// 2c / (-b +/- d)
					// Choose the sign of the +/- so that b+d gets larger in
					// magnitude
					if (b < 0.0) {
						d = -d;
					}
					double q = (b + d) / -2.0;
					// We already tested a for being 0 above
					complex[roots] = 0;
					real[roots++] = q / a;
					complex[roots] = 0;
					real[roots++] = c / q;
				}
			}
		}
		return roots;
	}

	/**
	 * Solves the cubic whose coefficients are in the <code>eqn</code> array and
	 * places the non-complex roots back into the same array, returning the
	 * number of roots. The solved cubic is represented by the equation:
	 * 
	 * <pre>
	 *     eqn = {c, b, a, d}
	 *     dx^3 + ax^2 + bx + c = 0
	 * </pre>
	 * 
	 * A return value of -1 is used to distinguish a constant equation that
	 * might be always 0 or never 0 from an equation that has no zeroes.
	 * 
	 * @param eqn
	 *            an array containing coefficients for a cubic
	 * @return the number of roots, or -1 if the equation is a constant.
	 */
	final public int solveCubic(double eqn[]) {
		return solveCubic(eqn, eqn, Kernel.STANDARD_PRECISION);
	}

	/*
	 * adapted from gsl/poly/solve_cubic.c
	 * 
	 * added solveQuadratic() added fixRoots() removed sorting of roots
	 * 
	 * solve_cubic.c - finds the real roots of x^3 + a x^2 + b x + c = 0
	 */
	final public int solveCubic(double eqn[], double res[],double eps) {

		int roots = 0;
		double d = eqn[3];
		if (Math.abs(d) < eps) {
			// The cubic has degenerated to quadratic (or line or ...).
			return solveQuadratic(eqn, res, eps);
		}
		double a = eqn[2] / d;
		double b = eqn[1] / d;
		double c = eqn[0] / d;
		double q = (a * a - 3 * b); // D(q) = 2aD(a) + 3D(b)
		double r = (2 * a * a * a - 9 * a * b + 27 * c); // D(r) = (3aa-9b)D(a)
															// - 9aD(b) + 27D(c)

		double Q = q / 9; // D(Q) = D(q)/9
		double R = r / 54;

		double Q3 = Q * Q * Q;
		double R2 = R * R;

		double CR2 = 729 * r * r; // D(CR2) = 729*2rD(r) ( |1458r(3aa-9b) -
									// 8748q*2a| + |-9a*1458r -8748q*3| +
									// |27*1458r| )*kernel.EPSILON
		double CQ3 = 2916 * q * q * q; // D(CQ3) = 2916*3qD(q) (D(root) ~
										// D(2sqrt(Q))= -1/sqrt(Q) D(Q),
										// |D(root)| |2a+3|/sqrt(9q)
										// *kernel.EPSILON

		// Application.debug("Q = "+Q+" R = "+R+" Q3 = "+Q3+" R2 = "+R2+" CR2 = "+CR2+" CQ3 = "+CQ3);

		// Application.debug(Math.abs(CR2 - CQ3)+"");

		if (Math.abs(R) < Kernel.EPSILON && Math.abs(Q) < Kernel.EPSILON) // if
																			// (R
																			// ==
																			// 0
																			// &&
																			// Q
																			// ==
																			// 0)
		{
			res[roots++] = -a / 3;
			res[roots++] = -a / 3;
			res[roots++] = -a / 3;
			return 3;
		}
		// Michael Borcherds changed to check CR2 equal to CQ3 to first 8
		// significant digits
		// important for eg y=(ax-b)^2(cx-d)
		// |D(CR2-CQ3)|< (|r(aa-3b) - 4qa| + |-3ar -6q| + |9r|)*13122*sqrt(q) /
		// |2a+3| *kernel.EPSILON
		// for simplicity, it (may be) about 10* max(CR2,CR3)/|2a+3| *
		// kernel.EPSILON
		// else if (Math.abs(CR2 - CQ3) < Math.max(CR2, CQ3) * kernel.EPSILON)
		// // else if (CR2 == CQ3)
		else if (Math.abs(CR2 - CQ3) < Math.max(CR2, CQ3) * 10
				/ Math.max(1, Math.abs(2 * a + 3)) * Kernel.EPSILON) // else if
																		// (CR2
																		// ==
																		// CQ3)
		{
			// this test is actually R2 == Q3, written in a form suitable
			// for exact computation with integers

			// Due to finite precision some double roots may be missed, and
			// considered to be a pair of complex roots z = x +/- kernel.EPSILON
			// i
			// close to the real axis.

			double sqrtQ = Math.sqrt(Q);

			if (R > 0) {
				res[roots++] = -2 * sqrtQ - a / 3;
				res[roots++] = sqrtQ - a / 3;
				res[roots++] = sqrtQ - a / 3;
			} else {
				res[roots++] = -sqrtQ - a / 3;
				res[roots++] = -sqrtQ - a / 3;
				res[roots++] = 2 * sqrtQ - a / 3;
			}
			return 3;
		} else if (CR2 < CQ3) // equivalent to R2 < Q3
		{
			double sqrtQ = Math.sqrt(Q);
			double sqrtQ3 = sqrtQ * sqrtQ * sqrtQ;
			double theta = Math.acos(R / sqrtQ3);
			double norm = -2 * sqrtQ;
			res[roots++] = norm * Math.cos(theta / 3) - a / 3;
			res[roots++] = norm * Math.cos((theta + 2.0 * Math.PI) / 3) - a / 3;
			res[roots++] = norm * Math.cos((theta - 2.0 * Math.PI) / 3) - a / 3;

			// GeoGebra addition
			// TODO: find a better way to deal with this
			if (res != eqn) // if res has the same reference as eqn, it's not
							// possible to fix roots
				fixRoots(res, eqn);

			return 3;
		} else {
			double sgnR = (R >= 0 ? 1 : -1);
			double A = -sgnR
					* Math.pow(Math.abs(R) + Math.sqrt(R2 - Q3), 1.0 / 3.0);
			double B = Q / A;
			res[roots++] = A + B - a / 3;
			return 1;
		}
	}// */

	
	/*
	 * This pruning step is necessary since solveCubic uses the cosine function
	 * to calculate the roots when there are 3 of them. Since the cosine method
	 * can have an error of +/- 1E-14 we need to make sure that we don't make
	 * any bad decisions due to an error.
	 * 
	 * If the root is not near one of the endpoints, then we will only have a
	 * slight inaccuracy in calculating the x intercept which will only cause a
	 * slightly wrong answer for some points very close to the curve. While the
	 * results in that case are not as accurate as they could be, they are not
	 * disastrously inaccurate either.
	 * 
	 * On the other hand, if the error happens near one end of the curve, then
	 * our processing to reject values outside of the t=[0,1] range will fail
	 * and the results of that failure will be disastrous since for an entire
	 * horizontal range of test points, we will either overcount or undercount
	 * the crossings and get a wrong answer for all of them, even when they are
	 * clearly and obviously inside or outside the curve.
	 * 
	 * To work around this problem, we try a couple of Newton-Raphson iterations
	 * to see if the true root is closer to the endpoint or further away. If it
	 * is further away, then we can stop since we know we are on the right side
	 * of the endpoint. If we change direction, then either we are now being
	 * dragged away from the endpoint in which case the first condition will
	 * cause us to stop, or we have passed the endpoint and are headed back. In
	 * the second case, we simply evaluate the slope at the endpoint itself and
	 * place ourselves on the appropriate side of it or on it depending on that
	 * result.
	 */
	private static void fixRoots(double res[], double eqn[]) {
		double myepsilon = 1E-5;

		for (int i = 0; i < 3; i++) {
			double t = res[i];
			if (Math.abs(t) < myepsilon) {
				res[i] = findZero(t, 0, eqn);
			} else if (Math.abs(t - 1) < myepsilon) {
				res[i] = findZero(t, 1, eqn);
			}
		}
	}

	private static double solveEqn(double eqn[], int order, double t) {
		double v = eqn[order];
		int counter = order;
		while (--counter >= 0) {
			v = v * t + eqn[counter];
		}
		return v;
	}

	private static double findZero(double init, double target, double eqn[]) {
		double t = init;
		double slopeqn[] = { eqn[1], 2 * eqn[2], 3 * eqn[3] };
		double slope;
		double origdelta = 0;
		double origt = t;
		while (true) {
			slope = solveEqn(slopeqn, 2, t);
			if (slope == 0.0)
				// At a local minima - must return
				return t;
			double y = solveEqn(eqn, 3, t);
			if (y == 0.0)
				// Found it! - return it
				return t;
			// assert(slope != 0 && y != 0);
			double delta = -(y / slope);
			// assert(delta != 0);
			if (origdelta == 0.0) {
				origdelta = delta;
			}
			if (t < target) {
				if (delta < 0)
					return t;
			} else if (t > target) {
				if (delta > 0)
					return t;
			} else
				return (delta > 0 ? (target + java.lang.Double.MIN_VALUE)
						: (target - java.lang.Double.MIN_VALUE));
			double newt = t + delta;
			if (t == newt)
				// The deltas are so small that we aren't moving...
				return t;
			if (delta * origdelta < 0) {
				// We have reversed our path.
				int tag = (origt < t ? getTag(target, origt, t) : getTag(
						target, t, origt));
				if (tag != INSIDE)
					// Local minima found away from target - return the middle
					return (origt + t) / 2;
				// Local minima somewhere near target - move to target
				// and let the slope determine the resulting t.
				t = target;
			} else {
				t = newt;
			}
		}
	}

	private static final int BELOW = -2;
	private static final int LOWEDGE = -1;
	private static final int INSIDE = 0;
	private static final int HIGHEDGE = 1;
	private static final int ABOVE = 2;

	/*
	 * Determine where coord lies with respect to the range from low to high. It
	 * is assumed that low <= high. The return value is one of the 5 values
	 * BELOW, LOWEDGE, INSIDE, HIGHEDGE, or ABOVE.
	 */
	private static int getTag(double coord, double low, double high) {
		if (coord <= low)
			return (coord < low ? BELOW : LOWEDGE);
		if (coord >= high)
			return (coord > high ? ABOVE : HIGHEDGE);
		return INSIDE;
	}

	/* ************************************************* */

	private static final double LAGUERRE_START = -1;

	/**
	 * Calculates all roots of a polynomial given by eqn using Laguerres method.
	 * Polishes roots found. The roots are stored in eqn again.
	 * 
	 * @param eqn
	 *            coefficients of polynomial
	 */
	@SuppressWarnings("deprecation")
	private int laguerreAll(double[] eqn) {
		// for fast evaluation of polynomial (used for root polishing)
		PolyFunction polyFunc = new PolyFunction(eqn);
		PolyFunction derivFunc = polyFunc.getDerivative();

		/*
		 * double estx = 0; try { estx = rootPolisher.newtonRaphson(polyFunc,
		 * LAGUERRE_START); Application.debug("newton estx: " + estx); if
		 * (Double.isNaN(estx)) { estx = LAGUERRE_START;
		 * Application.debug("corrected estx: " + estx); } } catch (Exception e)
		 * {}
		 */

		// calc roots with Laguerre method

		/*
		 * old code using Flanagan library //ComplexPoly poly = new
		 * ComplexPoly(eqn); //Complex [] complexRoots = poly.roots(false, new
		 * Complex(LAGUERRE_START, 0)); // don't polish here
		 */

		Complex[] complexRoots = null;
		try {
			if (laguerreSolver == null) {
				laguerreSolver = new LaguerreSolver();
			}
			complexRoots = laguerreSolver.solveAll(eqn, LAGUERRE_START);
		} catch (Exception e) {
			System.err.println("EquationSolver.LaguerreSolver: "
					+ e.getLocalizedMessage());
		}

		if (complexRoots == null)
			complexRoots = new Complex[0];

		// sort complexRoots by real part into laguerreRoots
		double[] laguerreRoots = new double[complexRoots.length];
		for (int i = 0; i < laguerreRoots.length; i++) {
			laguerreRoots[i] = complexRoots[i].getReal();
		}
		Arrays.sort(laguerreRoots);

		// get the roots from Laguerre method
		int realRoots = 0;
		double root;

		for (int i = 0; i < laguerreRoots.length; i++) {
			// System.out.println("laguerreRoots[" + i + "] = " +
			// laguerreRoots[i]);

			// let's polish all complex roots to get all real roots
			root = laguerreRoots[i];

			// check if root is bounded in intervall [root-eps, root+eps]
			double left = i == 0 ? root - 1 : (root + laguerreRoots[i - 1]) / 2;
			double right = i == laguerreRoots.length - 1 ? root + 1
					: (root + laguerreRoots[i + 1]) / 2;
			double f_left = polyFunc.evaluate(left);
			double f_right = polyFunc.evaluate(right);
			boolean bounded = f_left * f_right < 0.0;

			try {
				if (bounded) {
					if (rootFinderBrent == null) {
						UnivariateRealSolverFactory fact = UnivariateRealSolverFactory
								.newInstance();
						rootFinderBrent = fact.newBrentSolver();
					}

					// small f'(root): don't go too far from our laguerre root !
					double brentRoot = rootFinderBrent.solve(
							new RealRootAdapter(polyFunc), left, right, root);
					if (Math.abs(polyFunc.evaluate(brentRoot)) < Math
							.abs(polyFunc.evaluate(root))) {
						root = brentRoot;
					}
					// System.out.println("Polish bisectNewtonRaphson: " +
					// root);
				} else {
					if (rootFinderNewton == null) {
						UnivariateRealSolverFactory fact = UnivariateRealSolverFactory
								.newInstance();
						rootFinderNewton = fact.newNewtonSolver();
					}

					// the root is not bounded: give Mr. Newton a chance
					double newtonRoot = rootFinderNewton.solve(
							new RealRootDerivAdapter(polyFunc), left, right,
							root);
					if (Math.abs(polyFunc.evaluate(newtonRoot)) < Math
							.abs(polyFunc.evaluate(root))) {
						root = newtonRoot;
					}
					// System.out.println("Polish newtonRaphson: " + root);
				}
			} catch (Exception e) {
				// Application.debug("Polish FAILED: ");
				// polishing failed: maybe we have an extremum here
				// try to find a local extremum
				try {
					if (rootFinderBrent == null) {
						UnivariateRealSolverFactory fact = UnivariateRealSolverFactory
								.newInstance();
						rootFinderBrent = fact.newBrentSolver();
					}

					double brentRoot = rootFinderBrent.solve(
							new RealRootAdapter(derivFunc), left, right);
					if (Math.abs(polyFunc.evaluate(brentRoot)) < Math
							.abs(polyFunc.evaluate(root))) {
						root = brentRoot;
					}
					// System.out.println("    find extremum successfull: " +
					// root);
				} catch (Exception ex) {
					App.debug(ex.getMessage());
				}
			}

			// check if the found root is really ok
			double[] val = polyFunc.evaluateDerivFunc(root); // get f(root) and
																// f'(root)
			double error = Math.abs(val[0]); // | f(root) |
			double slope = Math.abs(val[1]);

			boolean success;
			if (slope < 1)
				success = error < LAGUERRE_EPS;
			else
				success = error < LAGUERRE_EPS * slope;

			if (success) {
				// Application.debug("FOUND ROOT: " + root);
				eqn[realRoots] = root;
				realRoots++;
			} else {
				// Application.debug("no root: " + root + ", error " + error);
			}
		}
		return realRoots;
	}

	/**
	 * Calculates all roots of a polynomial given by eqn using Laguerres method.
	 * Polishes roots found. The roots are stored in eqn again.
	 */
	@SuppressWarnings("deprecation")
	private int laguerreAllComplex(double[] real, double[] complex) {

		Complex[] complexRoots = null;
		try {
			if (laguerreSolver == null) {
				laguerreSolver = new LaguerreSolver();
			}
			complexRoots = laguerreSolver.solveAll(real, LAGUERRE_START);
		} catch (Exception e) {
			App.debug("Problem solving with LaguerreSolver"
					+ e.getLocalizedMessage());
			return 0;
		}

		// sort by real part & remove duplicates

		TreeSet<Complex> sortedSet = new TreeSet<Complex>(getComparatorReal());

		for (int i = 0; i < complexRoots.length; i++) {
			sortedSet.add(complexRoots[i]);
		}

		int roots = 0;
		Complex temp;
		Iterator<Complex> iterator = sortedSet.iterator();
		while (iterator.hasNext()) {
			temp = iterator.next();
			real[roots] = temp.getReal();
			complex[roots] = temp.getImaginary();
			roots++;
		}

		return roots;
	}

	// avoid too big polynomial coefficients in laguerreAll
	// private static final int MAX_POLYNOMIAL_COEFFICIENT = 1000;
	//
	// // avoid huge coefficients
	// private void checkCoefficients(double [] eqn) {
	// double max = Math.abs(eqn[0]);
	// double temp;
	// for (int i=1; i < eqn.length; i++) {
	// temp = Math.abs(eqn[i]);
	// if (temp > max) max = temp;
	// }
	// if (max > MAX_POLYNOMIAL_COEFFICIENT) {
	// Application.debug("changed coefficients");
	// double factor = MAX_POLYNOMIAL_COEFFICIENT/max;
	// for (int i=0; i < eqn.length; i++) {
	// eqn[i] *= factor;
	// }
	// }
	// }

	/*
	 * solve_quartic.c - finds the real roots of x^4 + a x^3 + b x^2 + c x + d =
	 * 0
	 * 
	 * modified from GSL Quartic extension
	 * http://www.network-theory.co.uk/download/gslextras/Quartic/
	 */

	public int solveQuartic(double eqn[], double res[], double eps) {

		if (Math.abs(eqn[4]) < 0)
			return solveCubic(eqn, res,  Kernel.STANDARD_PRECISION);

		double a = eqn[3] / eqn[4], b = eqn[2] / eqn[4], c = eqn[1] / eqn[4], d = eqn[0]
				/ eqn[4];

		/*
		 * This code is based on a simplification of the algorithm from
		 * zsolve_quartic.c for real roots
		 */
		double u[] = new double[3], v[] = new double[3], zarr[] = new double[4];
		double aa, pp, qq, rr, rc, sc, tc, mt;
		double w1r, w1i, w2r, w2i, w3r;
		double v1, v2, arg, theta;
		double disc, h;
		int k1 = 0, k2 = 0;

		int roots = 0;

		/*
		 * Deal easily with the cases where the quartic is degenerate. The
		 * ordering of solutions is done explicitly.
		 */
		if (0 == b && 0 == c) {
			if (0 == d) {
				if (a > 0) {
					res[roots++] = -a;
					res[roots++] = 0.0;
					res[roots++] = 0.0;
					res[roots++] = 0.0;
				} else {
					res[roots++] = 0.0;
					res[roots++] = 0.0;
					res[roots++] = 0.0;
					res[roots++] = -a;
				}
				return 4;
			} else if (0 == a) {
				if (d > 0) {
					return 0;
				}

				res[roots++] = Math.sqrt(Math.sqrt(-d));
				res[roots] = -res[roots - 1];
				roots++;
				return 2;

			}
		}

		if (0.0 == c && 0.0 == d) {
			res[roots++] = 0.0;
			res[roots++] = 0.0;
			double[] res2 = new double[3];
			res2[2] = 1.0;
			res2[1] = a;
			res2[0] = b;
			int n = solveQuadratic(res2, res2, eps);
			res[roots++] = res2[0];
			res[roots++] = res2[1];
			// if (gsl_poly_solve_quadratic(1.0,a,b,x2,x3)==0) {
			if (n == 0) {
				mt = 3;
			} else {
				mt = 1;
			}
		} else {
			/*
			 * For non-degenerate solutions, proceed by constructing and solving
			 * the resolvent cubic
			 */
			aa = a * a;
			pp = b - (3.0 / 8.0) * aa;
			qq = c - (1.0 / 2.0) * a * (b - (1.0 / 4.0) * aa);
			rr = d - (1.0 / 4.0)
					* (a * c - (1.0 / 4.0) * aa * (b - (3.0 / 16.0) * aa));
			rc = (1.0 / 2.0) * pp;
			sc = (1.0 / 4.0) * ((1.0 / 4.0) * pp * pp - rr);
			tc = -((1.0 / 8.0) * qq * (1.0 / 8.0) * qq);

			/*
			 * This code solves the resolvent cubic in a convenient fashion for
			 * this implementation of the quartic. If there are three real
			 * roots, then they are placed directly into u[]. If two are
			 * complex, then the real root is put into u[0] and the real and
			 * imaginary part of the complex roots are placed into u[1] and
			 * u[2], respectively. Additionally, this calculates the
			 * discriminant of the cubic and puts it into the variable disc.
			 */
			{
				double qcub = (rc * rc - 3 * sc);
				double rcub = (2 * rc * rc * rc - 9 * rc * sc + 27 * tc);

				double Q = qcub / 9;
				double R = rcub / 54;

				double Q3 = Q * Q * Q;
				double R2 = R * R;

				double CR2 = 729 * rcub * rcub;
				double CQ3 = 2916 * qcub * qcub * qcub;

				disc = (CR2 - CQ3) / 2125764.0;

				if (0 == R && 0 == Q) {
					u[0] = -rc / 3;
					u[1] = -rc / 3;
					u[2] = -rc / 3;
				} else if (CR2 == CQ3) {
					double sqrtQ = Math.sqrt(Q);
					if (R > 0) {
						u[0] = -2 * sqrtQ - rc / 3;
						u[1] = sqrtQ - rc / 3;
						u[2] = sqrtQ - rc / 3;
					} else {
						u[0] = -sqrtQ - rc / 3;
						u[1] = -sqrtQ - rc / 3;
						u[2] = 2 * sqrtQ - rc / 3;
					}
				} else if (CR2 < CQ3) {
					double sqrtQ = Math.sqrt(Q);
					double sqrtQ3 = sqrtQ * sqrtQ * sqrtQ;
					theta = Math.acos(R / sqrtQ3);
					if (R / sqrtQ3 >= 1.0)
						theta = 0.0;
					{
						double norm = -2 * sqrtQ;

						u[0] = norm * Math.cos(theta / 3) - rc / 3;
						u[1] = norm * Math.cos((theta + 2.0 * Math.PI) / 3)
								- rc / 3;
						u[2] = norm * Math.cos((theta - 2.0 * Math.PI) / 3)
								- rc / 3;
					}
				} else {
					double sgnR = (R >= 0 ? 1 : -1);
					double modR = Math.abs(R);
					double sqrt_disc = Math.sqrt(R2 - Q3);
					double A = -sgnR * Math.pow(modR + sqrt_disc, 1.0 / 3.0);
					double B = Q / A;
					double mod_diffAB = Math.abs(A - B);

					u[0] = A + B - rc / 3;
					u[1] = -0.5 * (A + B) - rc / 3;
					u[2] = -(Math.sqrt(3.0) / 2.0) * mod_diffAB;
				}
			}
			/* End of solution to resolvent cubic */

			/*
			 * Combine the square roots of the roots of the cubic resolvent
			 * appropriately. Also, calculate 'mt' which designates the nature
			 * of the roots: mt=1 : 4 real roots (disc == 0) mt=2 : 0 real roots
			 * (disc < 0) mt=3 : 2 real roots (disc > 0)
			 */

			if (0.0 == disc)
				u[2] = u[1];

			if (0 >= disc) {
				mt = 2;

				/*
				 * One would think that we could return 0 here and exit, since
				 * mt=2. However, this assignment is temporary and changes to
				 * mt=1 under certain conditions below.
				 */

				v[0] = Math.abs(u[0]);
				v[1] = Math.abs(u[1]);
				v[2] = Math.abs(u[2]);

				v1 = Math.max(Math.max(v[0], v[1]), v[2]);
				/* Work out which two roots have the largest moduli */
				k1 = 0;
				k2 = 0;
				if (v1 == v[0]) {
					k1 = 0;
					v2 = Math.max(v[1], v[2]);
				} else if (v1 == v[1]) {
					k1 = 1;
					v2 = Math.max(v[0], v[2]);
				} else {
					k1 = 2;
					v2 = Math.max(v[0], v[1]);
				}

				if (v2 == v[0]) {
					k2 = 0;
				} else if (v2 == v[1]) {
					k2 = 1;
				} else {
					k2 = 2;
				}

				if (0.0 <= u[k1]) {
					w1r = Math.sqrt(u[k1]);
					w1i = 0.0;
				} else {
					w1r = 0.0;
					w1i = Math.sqrt(-u[k1]);
				}
				if (0.0 <= u[k2]) {
					w2r = Math.sqrt(u[k2]);
					w2i = 0.0;
				} else {
					w2r = 0.0;
					w2i = Math.sqrt(-u[k2]);
				}
			} else {
				mt = 3;

				if (0.0 == u[1] && 0.0 == u[2]) {
					arg = 0.0;
				} else {
					arg = Math.sqrt(Math.sqrt(u[1] * u[1] + u[2] * u[2]));
				}
				theta = Math.atan2(u[2], u[1]);

				w1r = arg * Math.cos(theta / 2.0);
				w1i = arg * Math.sin(theta / 2.0);
				w2r = w1r;
				w2i = -w1i;
			}

			/* Solve the quadratic to obtain the roots to the quartic */
			w3r = qq / 8.0 * (w1i * w2i - w1r * w2r) / (w1i * w1i + w1r * w1r)
					/ (w2i * w2i + w2r * w2r);
			h = a / 4.0;

			zarr[0] = w1r + w2r + w3r - h;
			zarr[1] = -w1r - w2r + w3r - h;
			zarr[2] = -w1r + w2r - w3r - h;
			zarr[3] = w1r - w2r - w3r - h;

			/* Arrange the roots into the variables z0, z1, z2, z3 */
			if (2 == mt) {
				if (u[k1] >= 0 && u[k2] >= 0) {
					mt = 1;
					res[roots++] = zarr[0];
					res[roots++] = zarr[1];
					res[roots++] = zarr[2];
					res[roots++] = zarr[3];
				} else {
					return 0;
				}
			} else {
				res[roots++] = zarr[0];
				res[roots++] = zarr[1];
			}
		}

		/* Sort the roots as usual */
		if (1 == mt) {
			/*
			 * Roots are all real, sort them by the real part if (*x0 > *x1)
			 * SWAPD (*x0, *x1); if (*x0 > *x2) SWAPD (*x0, *x2); if (*x0 > *x3)
			 * SWAPD (*x0, *x3);
			 * 
			 * if (*x1 > *x2) SWAPD (*x1, *x2); if (*x2 > *x3) { SWAPD (*x2,
			 * *x3); if (*x1 > *x2) SWAPD (*x1, *x2); }
			 */
			return 4;
		}
		/*
		 * 2 real roots if (*x0 > *x1) SWAPD (*x0, *x1);
		 */

		return 2;
	}

	/**
	 * Returns a comparator for Complex objects. (sorts on real part coordinate)
	 * If equal does return zero (deletes duplicates!)
	 * @return comparator for complex numbers
	 */
	public static Comparator<Complex> getComparatorReal() {
		if (comparatorReal == null) {
			comparatorReal = new Comparator<Complex>() {
				public int compare(Complex itemA, Complex itemB) {

					double compReal = itemA.getReal() - itemB.getReal();

					if (Kernel.isZero(compReal)) {
						double compImaginary = itemA.getImaginary()
								- itemB.getImaginary();

						// if real parts equal, sort on imaginary
						if (!Kernel.isZero(compImaginary))
							return compImaginary < 0 ? -1 : +1;

						// return 0 -> remove duplicates!
						return 0;
					}
					return compReal < 0 ? -1 : +1;
				}
			};

		}

		return comparatorReal;
	}

	private static Comparator<Complex> comparatorReal;
}
