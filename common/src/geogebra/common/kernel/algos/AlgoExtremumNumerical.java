/*
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.main.App;

/**
 * Command: Extremum[<function>,leftx,rightx]
 * 
 * Numerically calculates Extremum point for <function> in open interval
 * <leftx,rightx> without being dependent on being able to find the derivate of
 * <function>.
 * 
 * Restrictions for use: <function> should be continuous and only have one
 * extremum in the interval <leftx,rightx>. (The interval should be an open
 * interval, extremum should not be on leftx or rightx.) Breaking restrictions
 * will give unpredictable results.
 * 
 * My algorithm is based on the fact that <function> vil be monotonous on either
 * side of the extremum in the interval. The interval is sliced with a
 * divider=4, and parts of the interval is eliminated when the extremum is
 * passed in every iteration. With a uniform probability for the placement of
 * the extremum in the interval, the optimum divider is 3, but in reality the
 * extremum is moree likely to be nearer the center of the interval, so the
 * optimum divider is somewhat larger than 3. Adivider=4 is probably a good
 * compromise, in the hope that the compiler takes advantage of shift operations
 * in dividing with a power of 2, and the difference is small from 3 to 5.
 * Expected number of iterations is less than 35. (Worst case is about twice as
 * large; ca. 70.)
 * 
 * ToDo: -If this is to slow, an adaptive divider is the first candidate for
 * optimization. -Finetune numerical accuracy. (After I have read Volume 2 of
 * The Art of Computer Programming, Donald Knuth...)
 * 
 * 2011-02-06: Got rid of some consequences (fake extremum point) of wrong
 * userinput. (extremums>1, no extremums, discontinuous,...)
 * 
 * @author Hans-Petter Ulven
 * @version 2011-02.05
 */

public class AlgoExtremumNumerical extends AlgoElement {

	private GeoFunctionable function; // input
	private GeoFunction f;
	private NumberValue left; // input
	private GeoElement geoleft;
	private NumberValue right; // input
	private GeoElement georight;
	private GeoPoint E; // output
	private static double xres; // static x for test interface

	public AlgoExtremumNumerical(Construction cons, String label,
			GeoFunctionable function, NumberValue left, NumberValue right) {
		super(cons);
		this.function = function;
		this.f = function.getGeoFunction();
		this.left = left;
		this.geoleft = left.toGeoElement();
		this.right = right;
		this.georight = right.toGeoElement();

		E = new GeoPoint(cons); // Put an extremum point in the user interface
									// from the very start
		E.setCoords(0.0, 0.0, 1.0);

		setInputOutput();

		compute();

		E.setLabel(label);

	}// constru//if error in parametersctor

	@Override
	public Algos getClassName() {
		return Algos.AlgoExtremumNumerical;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = function.toGeoElement();
		input[1] = geoleft;
		input[2] = georight;

		setOutputLength(1);
		setOutput(0, E);

		setDependencies(); // done by AlgoElement
	}

	public GeoPoint getNumericalExtremum() {
		return E;
	}

	@Override
	public final void compute() {
		final int MAXITERATIONS = 100; // Safety net: Stop at 100 if something
										// diverges, no useful solution if >70
		final int DIVIDER = 4; // Size of slice
		boolean isgoingup = true; // Max or Min...
		double l = left.getDouble();
		double r = right.getDouble();
		double epsilon = Math.abs((r - l)) / 1.0E15; // About 15 digits accuracy
		double diff = epsilon * 2.0; // To start iteration...

		boolean didslice = false;
		double newleft, newright;
		int iterations = 0; // Count iterations

		if (!function.toGeoElement().isDefined() || !geoleft.isDefined()
				|| !georight.isDefined()
				|| (right.getDouble() <= left.getDouble())) {
			E.setUndefined();
			return;
		}// if input is ok?

		// / --- Algorithm --- ///

		double max = (l + r) / 2; // Tentative max (or min) so far

		// Max or min? Check if "derivative" is positive (negative) at left:
		if (f.evaluate(l) < f.evaluate(l + Math.abs((max - l) / 1E12))) { // 1E16
																			// might
																			// not
																			// give
																			// any
																			// change...
			isgoingup = true;
		} else {
			isgoingup = false;
		}// if

		// if(isgoingup){debug("Finding maximum...");}else{debug("Finding minimum...");}

		while ((diff > epsilon) && (iterations < MAXITERATIONS)) {
			iterations++; // debug(info(iterations,l,max,r));
			didslice = false;
			if (isgoingup) {
				newleft = max - (max - l) / DIVIDER;
				if (f.evaluate(newleft) < f.evaluate(l)) {
					r = newleft;
					didslice = true;
				}
				if (f.evaluate(newleft) > f.evaluate(max)) {
					r = max;
					didslice = true;
				}
				newright = max + (r - max) / DIVIDER;
				if (f.evaluate(newright) < f.evaluate(r)) {
					l = newright;
					didslice = true;
				}
				if (f.evaluate(newright) > f.evaluate(max)) {
					l = max;
					didslice = true;
				}
			} else {
				newleft = max - (max - l) / DIVIDER;
				if (f.evaluate(newleft) > f.evaluate(l)) {
					r = newleft;
					didslice = true;
				}
				if (f.evaluate(newleft) < f.evaluate(max)) {
					r = max;
					didslice = true;
				}
				newright = max + (r - max) / DIVIDER;
				if (f.evaluate(newright) > f.evaluate(r)) {
					l = newright;
					didslice = true;
				}
				if (f.evaluate(newright) < f.evaluate(max)) {
					l = max;
					didslice = true;
				}
			}// if direction

			if (!didslice) {
				l = newleft;
				r = newright;
			}

			max = (l + r) / 2;
			diff = Math.abs(r - l);
		}// while not finished
		xres = max; // use middle value in last interval

		// Save some repetitive calculations:
		double y = f.evaluate(max);
		double ld = left.getDouble();
		double rd = right.getDouble();
		double fl = f.evaluate(ld);
		double fr = f.evaluate(rd);

		E.setCoords(max, y, 1.0);

		// Final checks:
		if (Double.isNaN(y)) {
			E.setUndefined();
		} // Check infinity, discontinuity
		if (max <= (ld/* +epsilon */)) {
			E.setUndefined();
		} // Check not on left endpoint of interval
		if ((rd/*-epsilon*/) <= max) {
			E.setUndefined();
		} // Check not on right endpoint of interval

		// Check if extremum is not extremum after all...(No extremums in
		// interval? Asymptotes=>algorithm went astray?)
		// ToDo: Problem: Also covers more than one extremum if
		// f(l)<f(extremum)<f(r),
		// but not if extremum is larger/smaller than l and r...
		// Problem: Endpoints might be extremums, if no extremum in open
		// interall, because of rounding error...
		if (isgoingup) {
			if ((y < fl) || (y < fr)) {
				E.setUndefined();
			}// if not really a maximum
		} else {
			if ((y > fl) || (y > fr)) {
				E.setUndefined();
			}// if not really a minimum
		}// if not extremum

		// debug("iterations: "+iterations+"point: ("+max+","+y+")"+"in intervall: <"+left.getDouble()+","+right.getDouble()+">");
	}// compute()

	// * //--- SNIP (after debugging and testing) -------------------------
	// / --- Test interface --- ///
	// Running testcases from external testscript Test_Extremum.bsh from plugin
	// scriptrunner.
	public final static double getX() {
		return xres;
	}// getX()

	private static final boolean DEBUG = true;

	private final static void debug(String s) {
		if (DEBUG) {
			App.debug(s);
		}// if()
	}// debug()

	private String info(int i, double l, double m, double r) {
		return "Iteration " + i + ":\n" + l + "     " + m + "     " + r
				+ "     " + f.evaluate(l) + "     " + f.evaluate(m) + "     "
				+ f.evaluate(r);
	}// info()

	// TODO Consider locusequability

	// */ //--- SNIP end ---------------------------------------

}// class AlgoExtremumNumerical

