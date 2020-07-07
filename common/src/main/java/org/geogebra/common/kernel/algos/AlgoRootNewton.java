/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.solvers.NewtonSolver;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.advanced.AlgoFunctionInvert;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.roots.RealRootUtil;

/**
 * Finds one real root of a function with newtons method. The first derivative
 * of this function must exist.
 */
public class AlgoRootNewton extends AlgoIntersectAbstract {
	/** max iterations for newton method */
	public static final int MAX_ITERATIONS = 100;

	private GeoFunctionable f; // input, g for intersection of functions
	private NumberValue start; // start value for root of f
	private GeoPoint rootPoint; // output

	private GeoElement startGeo;
	private NewtonSolver rootFinderNewton;
	private BrentSolver rootFinderBrent;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param f
	 *            function
	 * @param start
	 *            start value
	 */
	public AlgoRootNewton(Construction cons, String label, GeoFunctionable f,
			GeoNumberValue start) {
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

	/**
	 * Constructor for extending algos
	 * 
	 * @param cons
	 *            construction
	 */
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
		input[0] = f.toGeoElement();
		input[1] = startGeo;

		super.setOutputLength(1);
		super.setOutput(0, rootPoint);
		setDependencies();
	}

	/**
	 * @return root
	 */
	public GeoPoint getRootPoint() {
		return rootPoint;
	}

	@Override
	public void compute() {
		if (!(f.isDefined() && startGeo.isDefined())) {
			rootPoint.setUndefined();
		} else {
			double startValue = start.getDouble();
			Function fun = f.getFunctionForRoot();

			// calculate root
			rootPoint.setCoords(calcRoot(fun, startValue), 0.0, 1.0);
		}
	}

	/**
	 * @param fun
	 *            function
	 * @param startX
	 *            start x-value
	 * @return root
	 */
	public final double calcRoot(Function fun, double startX) {
		double root = Double.NaN;
		if (rootFinderBrent == null) {
			rootFinderBrent = new BrentSolver(Kernel.STANDARD_PRECISION);
		}

		// try Brent method with borders close to start value
		try {

			// arbitrary (used to depend on screen width)
			double step = 1;

			root = rootFinderBrent.solve(MAX_ITERATIONS, fun, startX - step,
					startX + step,
					startX);
			if (checkRoot(fun, root)) {
				// System.out.println("1. Brent worked: " + root);
				return root;
			}
		} catch (RuntimeException e) {
			root = Double.NaN;
		}

		// try Brent method on valid interval around start
		double[] borders = getDomain(fun, startX);
		try {
			root = rootFinderBrent.solve(MAX_ITERATIONS, fun, borders[0],
					borders[1], startX);
			if (checkRoot(fun, root)) {
				// System.out.println("2. Brent worked: " + root);
				return root;
			}
		} catch (RuntimeException e) {
			root = Double.NaN;
		}

		// try Newton's method
		DifferentiableUnivariateFunction derivFun = fun;
		// check if fun(start) is defined
		double eval = fun.value(startX);
		double start1 = startX;
		if (Double.isNaN(eval) || Double.isInfinite(eval)) {
			// shift left border slightly right
			borders[0] = 0.9 * borders[0] + 0.1 * borders[1];
			start1 = (borders[0] + borders[1]) / 2;
		}

		if (rootFinderNewton == null) {
			rootFinderNewton = new NewtonSolver();
		}

		try {
			root = rootFinderNewton.solve(MAX_ITERATIONS, derivFun, borders[0],
					borders[1], start1);
			if (checkRoot(fun, root)) {
				// System.out.println("Newton worked: " + root);
				return root;
			}
		} catch (RuntimeException e) {
			//
		}
		FunctionVariable x = new FunctionVariable(kernel);
		ExpressionNode inv = AlgoFunctionInvert.invert(fun.getExpression(),
				fun.getFunctionVariable(), x, kernel);
		x.set(0);
		if (inv != null) {
			root = inv.evaluateDouble();
			if (checkRoot(fun, root)) {
				return root;
			}
		}
		// neither Brent nor Newton worked
		return Double.NaN;
	}

	private static boolean checkRoot(Function fun, double root) {
		// check what we got
		return !Double.isNaN(root)
				&& (Math.abs(fun.value(root)) < Kernel.MIN_PRECISION);
	}

	/**
	 * Tries to find a valid domain for the given function around it's starting
	 * value.
	 */
	private static double[] getDomain(Function fun, double start) {
		// arbitrary interval (used to depend on screen width)
		return RealRootUtil.getDefinedInterval(fun, start - 0.5, start + 0.5);
	}

	@Override
	public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("RootOfAWithInitialValueB",
				"Root of %0 with initial value %1", f.getLabel(tpl),
				startGeo.getLabel(tpl));

	}
}
