/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.solvers.NewtonSolver;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.roots.RealRootUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Finds one real root of a function in the given interval using Brent's method.
 */
public class AlgoRootInterval extends AlgoElement {

	private GeoFunctionable f; // input
	private GeoNumberValue a;
	private GeoNumberValue b; // interval bounds
	private GeoPoint rootPoint; // output

	private BrentSolver rootFinder;
	NewtonSolver rootPolisher;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param f
	 *            function
	 * @param a
	 *            interval left bound
	 * @param b
	 *            interval right bound
	 */
	public AlgoRootInterval(Construction cons, String label, GeoFunctionable f,
			GeoNumberValue a, GeoNumberValue b) {
		super(cons);
		this.f = f;
		this.a = a;
		this.b = b;

		// output
		rootPoint = new GeoPoint(cons);
		setInputOutput(); // for AlgoElement
		compute();
		rootPoint.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Root;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = f.toGeoElement();
		input[1] = a.toGeoElement();
		input[2] = b.toGeoElement();

		setOnlyOutput(rootPoint);
		setDependencies();
	}

	public GeoPoint getRootPoint() {
		return rootPoint;
	}

	@Override
	public final void compute() {
		rootPoint.setCoords(calcRoot(), 0.0, 1.0);
	}

	final double calcRoot() {
		if (!(f.isDefined() && a.isDefined() && b.isDefined())) {
			return Double.NaN;
		}

		double root = Double.NaN;
		Function fun = f.getFunctionForRoot();

		if (rootFinder == null) {
			rootFinder = new BrentSolver();

			rootPolisher = new NewtonSolver();
		}

		double min = a.getDouble();
		double max = b.getDouble();

		double newtonRoot = Double.NaN;

		try {
			// Brent's method (Apache)
			root = rootFinder.solve(AlgoRootNewton.MAX_ITERATIONS, fun, min,
					max);

		} catch (Exception e) {
			// e.printStackTrace();
			Log.debug("problem finding root: " + e.getMessage());

			try {
				// Let's try again by searching for a valid domain first
				double[] borders = RealRootUtil.getDefinedInterval(fun, min,
						max);
				root = rootFinder.solve(AlgoRootNewton.MAX_ITERATIONS, fun,
						borders[0],
						borders[1]);
			} catch (Exception ex) {
				// ex.printStackTrace();
				Log.debug("problem finding root: " + ex.getMessage());
				return Double.NaN;
			}
		}

		// Log.debug("result from Brent: " + root);

		// ******** Polish Root ***************
		// adapted from EquationSolver
		// #4691

		try {
			newtonRoot = rootPolisher.solve(AlgoRootNewton.MAX_ITERATIONS, fun,
					min,
					max, root);

			if (Math.abs(fun.value(newtonRoot)) < Math.abs(fun.value(root))) {
				root = newtonRoot;
				// Log.debug("polished result from Newton is better: " +
				// newtonRoot);
			}

		} catch (Exception e) {
			Log.debug("problem polishing root: " + e.getMessage());
		}

		// check result
		if (Math.abs(fun.value(root)) < Kernel.MIN_PRECISION) {
			return root;
		}

		Log.debug("problem with root accuracy");
		return Double.NaN;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("RootOfAonIntervalBC",
				"Root of %0 on interval [%0, %1]", f.getLabel(tpl),
				a.getLabel(tpl), b.getLabel(tpl));
	}

}
