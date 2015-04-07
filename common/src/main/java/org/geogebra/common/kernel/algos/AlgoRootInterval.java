/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.apache.commons.math.analysis.solvers.UnivariateRealSolver;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.roots.RealRootAdapter;
import org.geogebra.common.kernel.roots.RealRootDerivAdapter;
import org.geogebra.common.kernel.roots.RealRootUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Finds one real root of a function in the given interval using Brent's method.
 */
public class AlgoRootInterval extends AlgoElement {

	private GeoFunction f; // input
	private NumberValue a, b; // interval bounds
	private GeoPoint rootPoint; // output

	private GeoElement aGeo, bGeo;
	private UnivariateRealSolver rootFinder;
	UnivariateRealSolver rootPolisher;

	public AlgoRootInterval(Construction cons, String label, GeoFunction f,
			NumberValue a, NumberValue b) {
		super(cons);
		this.f = f;
		this.a = a;
		this.b = b;
		aGeo = a.toGeoElement();
		bGeo = b.toGeoElement();

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
		input[0] = f;
		input[1] = aGeo;
		input[2] = bGeo;

		super.setOutputLength(1);
		super.setOutput(0, rootPoint);
		setDependencies();
	}

	public GeoPoint getRootPoint() {
		return rootPoint;
	}

	@Override
	public final void compute() {
		rootPoint.setCoords(calcRoot(), 0.0, 1.0);
	}

	@SuppressWarnings("deprecation")
	final double calcRoot() {
		if (!(f.isDefined() && aGeo.isDefined() && bGeo.isDefined())) {
			return Double.NaN;
		}

		double root = Double.NaN;
		Function fun = f.getFunction();

		if (rootFinder == null) {
			UnivariateRealSolverFactory fact = UnivariateRealSolverFactory
					.newInstance();
			rootFinder = fact.newBrentSolver();

			rootPolisher = fact.newNewtonSolver();
		}

		double min = a.getDouble();
		double max = b.getDouble();

		double newtonRoot = Double.NaN;

		try {
			// Brent's method (Apache 2.2)
			root = rootFinder.solve(new RealRootAdapter(fun), min, max);

			// Apache 3.3 - solver seems more accurate
			// #4691
			// BrentSolver brent3 = new BrentSolver();
			// root = brent3.solve(100, new RealRootAdapter3(fun), min, max);

		} catch (Exception e) {
			// e.printStackTrace();
			Log.debug("problem finding root: " + e.getMessage());

			try {
				// Let's try again by searching for a valid domain first
				double[] borders = RealRootUtil.getDefinedInterval(fun, min,
						max);
				root = rootFinder.solve(new RealRootAdapter(fun), borders[0],
						borders[1]);
			} catch (Exception ex) {
				// ex.printStackTrace();
				Log.debug("problem finding root: " + ex.getMessage());
				return Double.NaN;
			}
		}

		// Log.debug("result from Brent: " + root);

		// ******** Polish Root ***************
		// adpated from EquationSolver
		// #4691

		try {
			newtonRoot = rootPolisher.solve(new RealRootDerivAdapter(fun), min,
					max, root);

			if (Math.abs(fun.evaluate(newtonRoot)) < Math.abs(fun
					.evaluate(root))) {
				root = newtonRoot;
				// Log.debug("polished result from Newton is better: " +
				// newtonRoot);
			}

		} catch (Exception e) {
			Log.debug("problem polishing root: " + e.getMessage());
		}

		// check result
		if (Math.abs(fun.evaluate(root)) < Kernel.MIN_PRECISION) {
			return root;
		}

		Log.debug("problem with root accuracy");
		return Double.NaN;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("RootOfAonIntervalBC", f.getLabel(tpl),
				aGeo.getLabel(tpl), bGeo.getLabel(tpl));
	}

	// TODO Consider locusequability
}
