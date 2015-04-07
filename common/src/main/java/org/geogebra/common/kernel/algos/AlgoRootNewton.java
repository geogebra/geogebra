/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.apache.commons.math.analysis.solvers.BrentSolver;
import org.apache.commons.math.analysis.solvers.NewtonSolver;
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
import org.geogebra.common.kernel.roots.RealRootDerivFunction;
import org.geogebra.common.kernel.roots.RealRootUtil;

/**
 * Finds one real root of a function with newtons method. The first derivative
 * of this function must exist.
 */
public class AlgoRootNewton extends AlgoIntersectAbstract {

	public static final int MAX_ITERATIONS = 100;

	private GeoFunction f; // input, g for intersection of functions
	private NumberValue start; // start value for root of f
	private GeoPoint rootPoint; // output

	private GeoElement startGeo;
	private NewtonSolver rootFinderNewton;
	private BrentSolver rootFinderBrent;

	public AlgoRootNewton(Construction cons, String label, GeoFunction f,
			NumberValue start) {
		super(cons);
		this.f = f;
		this.start = start;
		startGeo = start.toGeoElement();

		// output
		rootPoint = new GeoPoint(cons);
		setInputOutput(); // for AlgoElement
		compute();

		rootPoint.setLabel(label);
	}

	AlgoRootNewton(Construction cons) {
		super(cons);
	}

	@Override
	public Commands getClassName() {
		return Commands.Root;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = f;
		input[1] = startGeo;

		super.setOutputLength(1);
		super.setOutput(0, rootPoint);
		setDependencies();
	}

	public GeoPoint getRootPoint() {
		return rootPoint;
	}

	@Override
	public void compute() {
		if (!(f.isDefined() && startGeo.isDefined())) {
			rootPoint.setUndefined();
		} else {
			double startValue = start.getDouble();
			Function fun = f.getFunction(startValue);

			// calculate root
			rootPoint.setCoords(calcRoot(fun, startValue), 0.0, 1.0);
		}
	}

	public final double calcRoot(Function fun, double start) {
		double root = Double.NaN;
		if (rootFinderBrent == null)
			rootFinderBrent = new BrentSolver(Kernel.STANDARD_PRECISION);

		// try Brent method with borders close to start value
		try {

			// arbitrary (used to depend on screen width)
			double step = 1;

			root = rootFinderBrent.solve(MAX_ITERATIONS, new RealRootAdapter(
					fun), start - step, start + step, start);
			if (checkRoot(fun, root)) {
				// System.out.println("1. Brent worked: " + root);
				return root;
			}
		} catch (Exception e) {
			root = Double.NaN;
		}

		// try Brent method on valid interval around start
		double[] borders = getDomain(fun, start);
		try {
			root = rootFinderBrent.solve(MAX_ITERATIONS, new RealRootAdapter(
					fun), borders[0], borders[1], start);
			if (checkRoot(fun, root)) {
				// System.out.println("2. Brent worked: " + root);
				return root;
			}
		} catch (Exception e) {
			root = Double.NaN;
		}

		// try Newton's method
		RealRootDerivFunction derivFun = fun.getRealRootDerivFunction();
		if (derivFun != null) {
			// check if fun(start) is defined
			double eval = fun.evaluate(start);
			if (Double.isNaN(eval) || Double.isInfinite(eval)) {
				// shift left border slightly right
				borders[0] = 0.9 * borders[0] + 0.1 * borders[1];
				start = (borders[0] + borders[1]) / 2;
			}

			if (rootFinderNewton == null) {
				rootFinderNewton = new NewtonSolver();
			}

			try {
				root = rootFinderNewton.solve(MAX_ITERATIONS,
						new RealRootDerivAdapter(derivFun), borders[0],
						borders[1], start);
				if (checkRoot(fun, root)) {
					// System.out.println("Newton worked: " + root);
					return root;
				}
			} catch (Exception e) {
				root = Double.NaN;
			}
		}

		// neither Brent nor Newton worked
		return Double.NaN;
	}

	private static boolean checkRoot(Function fun, double root) {
		// check what we got
		return !Double.isNaN(root)
				&& (Math.abs(fun.evaluate(root)) < Kernel.MIN_PRECISION);
	}

	/**
	 * Tries to find a valid domain for the given function around it's starting
	 * value.
	 */
	private double[] getDomain(Function fun, double start) {
		// arbitrary interval (used to depend on screen width)
		return RealRootUtil.getDefinedInterval(fun, start - 0.5, start + 0.5);
	}

	@Override
	public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("RootOfAWithInitialValueB", f.getLabel(tpl),
				startGeo.getLabel(tpl));

	}
}
