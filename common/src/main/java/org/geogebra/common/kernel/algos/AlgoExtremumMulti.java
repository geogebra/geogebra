/*
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.optimization.ExtremumFinderI;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Command: Extremum[ &lt;function&gt;, left-x, right-x]
 *
 * Numerically calculates Extremum points for &lt;function&gt; in interval
 * &lt;left-x,right-x&gt; without being dependent on being able to find the
 * derivate of &lt;function&gt;.
 *
 * Restrictions for use: &lt;function&gt; should be continuous and be reasonably
 * well-behaved in the interval. (For instance the function should not be wildly
 * oscillating in small x-intervals, and not have several hundred extremums in
 * the interval.) (The interval should be an open interval, extremums should not
 * be on leftx or rightx.)(?) Breaking restrictions could go well but could also
 * give unpredictable results.
 *
 * This routine tries to find all extremums visible by eyesight in the graphic
 * screen, but might oversee more extremums not being visible. (Those might
 * become visible by zooming howeveer.)
 *
 * Algorithm is: -Sample every 5 pixel -Find intervals with possible extremums
 * -Use Brent's algorithm (see geogebra.kernel.optimization.ExtremumFinder) on
 * the intervals
 *
 * 01.03.2011: -Got rid of false extremums near asymptotes (even if the user
 * should not have them in the interval...) by testing gradient 07.03.2011:
 * Rewrote to extend abstract AlgoGeoPointsFunction which has all the label
 * code.
 *
 * @author Hans-Petter Ulven
 * @version 2011-03.07
 */

public class AlgoExtremumMulti extends AlgoGeoPointsFunction {

	// Input-Output
	private final GeoFunctionable f1;

	/**
	 * Computes "all" Extremums of f in &lt;l,r&gt;
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param function
	 *            function
	 * @param left
	 *            left bound
	 * @param right
	 *            right bound
	 * @param labelEnabled
	 *            whether to label outputs
	 */
	public AlgoExtremumMulti(Construction cons, String[] labels,
			GeoFunctionable function, GeoNumberValue left, GeoNumberValue right,
			boolean labelEnabled) {
		super(cons, labels, labelEnabled && !cons.isSuppressLabelsActive());
		this.f1 = function;
		this.left = left;
		this.right = right;

		setInputOutput();

		compute();

		// Show at least one root point in algebra view
		// Copied from AlgoRootsPolynomial...
		GeoPoint[] gpt = getPoints(); // Ancestor
		if (!gpt[0].isDefined()) {
			gpt[0].setCoords(0, 0, 1);
			gpt[0].update();
			gpt[0].setUndefined();
			gpt[0].update();
		} // if list not defined
	}

	/**
	 * Computes extrema visible in given view.
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param function
	 *            function
	 * @param view
	 *            view
	 */
	public AlgoExtremumMulti(Construction cons, String[] labels,
			GeoFunctionable function, EuclidianViewInterfaceCommon view) {
		this(cons, labels, function, view.getXminObject(),
				view.getXmaxObject(), true);

		// updates the area that is visible
		cons.registerEuclidianViewCE(this);
		intervalDefinedByEV = true;
	}

	@Override
	public Commands getClassName() {
		return Commands.Extremum;
	}

	/**
	 * @return extrema
	 */
	public GeoPoint[] getExtremumPoints() {
		return getPoints();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = f1.toGeoElement();
		input[1] = left.toGeoElement();
		input[2] = right.toGeoElement();

		// setOutputLength(1);
		// setOutput(0, E);

		super.setOutput(getPoints());

		noUndefinedPointsInAlgebraView(getPoints());

		setDependencies(); // done by AlgoElement
	}

	@Override
	public final void compute() {

		if (intervalDefinedByEV) {
			updateInterval();
		}

		double[] extremums = new double[0];
		int numberOfExtremums = 0;

		double l = left.getDouble();
		double r = right.getDouble();

		if (!f1.toGeoElement().isDefined() || !left.isDefined() || !right.isDefined()) {
			setPoints(f1, new double[1], 0); // 0 flags it
		} else {

			if (l > r) {
				double tmp = l;
				l = r;
				r = tmp;
			} // correct user input

			UnivariateFunction rrfunc = f1;

			// / --- Algorithm --- ///

			int n = findNumberOfSamples(l, r);
			int m = n;
			try { // To catch eventual wrong indexes in arrays...
				do { // debug("doing samples: "+m);
					extremums = findExtremums(rrfunc, l, r, m,
							kernel.getExtremumFinder());
					numberOfExtremums = extremums.length;

					if (numberOfExtremums < m / 2) {
						break;
					}
					m = m * 2;
				} while (m < MAX_SAMPLES);
				if (m > MAX_SAMPLES) {
					Log.debug("We have probably lost some extremums...");
				}
			} catch (Exception e) {
				Log.debug("Exception in compute() " + e.toString());
			} // try-catch
			if (numberOfExtremums == 0) {
				setPoints(f1, new double[1], 0);
			} else {
				setPoints(f1, extremums, numberOfExtremums);
			} // if null
		} // if input is ok?
	}

	/**
	 * Main algorithm, public for eventual use by other commands Finds a
	 * samplesize depending on screen coordinates Samples n intervals and
	 * collects extremums in intervals
	 * 
	 * @param rrfunc
	 *            function
	 * @param l
	 *            left bound
	 * @param r
	 *            right bound
	 * @param samples
	 *            number of samples
	 * @param extrfinder
	 *            extremum finder
	 * @return extrema
	 */
	public static double[] findExtremums(UnivariateFunction rrfunc,
			double l, double r, int samples, ExtremumFinderI extrfinder) {
		double[] y = new double[samples + 1]; // n+1 y-values
		boolean[] grad = new boolean[samples]; // n gradients, true: f'>=0,
												// false: f'<0
		ArrayList<Double> xlist = new ArrayList<>();

		double deltax = (r - l) / samples; // x[i]=l+i*deltax, don't need
											// x-array

		for (int i = 0; i <= samples; i++) { // debug("iteration: "+i);

			y[i] = rrfunc.value(l + i * deltax);
			if (i > 0) { // grad only from 1 to n-1
				if (y[i] >= y[i - 1]) { // grad positive or zero
					grad[i - 1] = true;
				} else { // grad negative
					grad[i - 1] = false;
				} // if gradient >=0 or <0
					// debug("grad "+(i-1)+": "+grad[i-1]);
			} // if gradients can be calculated

			if (i > 1) {
				double xval = 0.0;
				double curleft = l + (i - 2) * deltax;
				double curright = curleft + 2 * deltax;
				if ((grad[i - 2]) && (!grad[i - 1])) { // max
					// if( ((y[i-1]-y[i-2])/deltax)<MAX_GRADIENT) {
					xval = extrfinder.findMaximum(curleft, curright, rrfunc,
							3.0E-8);
					if (gradientChangesSign(rrfunc, xval, curleft, curright)) {
						xlist.add(DoubleUtil.checkMax(xval, rrfunc));
					} // If not too large gradient
				} else if ((!grad[i - 2]) && (grad[i - 1])) { // min
					// if( ((y[i-2]-y[i-1])/deltax) < MAX_GRADIENT ) {
					xval = extrfinder.findMinimum(curleft, curright, rrfunc,
							3.0E-8);
					if (gradientChangesSign(rrfunc, xval, curleft, curright)) {
						xlist.add(DoubleUtil.checkMin(xval, rrfunc));

					} // if not too large gradient
				} // if possible extremum between x[i-2] and x[i]
			} // if grad analysis possible
		} // for all n sample points

		double[] result = new double[xlist.size()];
		for (int i = 0; i < xlist.size(); i++) {
			result[i] = xlist.get(i);
		} // for all x
		return result;
	}

	// / --- Private methods --- ///
	// Make all private after testing...

	private static boolean gradientChangesSign(UnivariateFunction rrf,
			double x, double l, double r) {
		double dx = (r - l) / 1E8;
		double vx = rrf.value(x);
		double vxRight = rrf.value(x + dx);
		double vxLeft = rrf.value(x - dx);
		if (vxRight >= vx && vxLeft >= vx) {
			return true;
		}
		if (vxRight <= vx && vxLeft <= vx) {
			return true;
		}
		// to stay compatible with old versions check the gradient; the 1E-4
		// constant is arbitrary and not good enough for fast growing functions
		return Math.abs((vxRight - vx) / dx) < 1E-4;
	}

	@Override
	public boolean euclidianViewUpdate() {
		compute();
		return true;
	}

	@Override
	protected void initPoints(int number) {
		super.initPoints(number);
		for (int i = 0; i < points.length; i++) {
			points[i].setUseVisualDefaults(false);
		}
		if (points.length > number) {
			// count points with dependent elements
			boolean foundDependency = false;

			for (int i = Math.max(number, 1); i < points.length; i++) {
				if (!points[i].getAlgoUpdateSet().isEmpty()) {
					points[i].setCoords(0, 0, 1); // init as defined
					foundDependency = true;
				} else {
					points[i].setParentAlgorithm(null);
					points[i].remove();
				}
			}

			// at least one point is kept for its dependent elements -> no need
			// to keep the first point
			if (number == 0 && foundDependency) {
				if (!points[0].getAlgoUpdateSet().isEmpty()) {
					points[0].setCoords(0, 0, 1); // init as defined
				} else {
					points[0].setParentAlgorithm(null);
					points[0].remove();
				}
			}
			super.setOutput(points);
		}
	}
}
