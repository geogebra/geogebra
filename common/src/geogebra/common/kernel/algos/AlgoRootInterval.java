/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.roots.RealRootAdapter;
import geogebra.common.kernel.roots.RealRootUtil;

import org.apache.commons.math.analysis.solvers.UnivariateRealSolver;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactory;

/**
 * Finds one real root of a function in the given interval using Brent's method.
 */
public class AlgoRootInterval extends AlgoElement {

	private GeoFunction f; // input
	private NumberValue a, b; // interval bounds
	private GeoPoint rootPoint; // output

	private GeoElement aGeo, bGeo;
	private UnivariateRealSolver rootFinder;

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

	final double calcRoot() {
		if (!(f.isDefined() && aGeo.isDefined() && bGeo.isDefined()))
			return Double.NaN;

		double root = Double.NaN;
		Function fun = f.getFunction();

		if (rootFinder == null) {
			UnivariateRealSolverFactory fact = UnivariateRealSolverFactory
					.newInstance();
			rootFinder = fact.newBrentSolver();
		}

		try {
			// Brent's method
			root = rootFinder.solve(new RealRootAdapter(fun), a.getDouble(),
					b.getDouble());
		} catch (Exception e) {
			try {
				// Let's try again by searchin for a valid domain first
				double[] borders = RealRootUtil.getDefinedInterval(fun,
						a.getDouble(), b.getDouble());
				root = rootFinder.solve(new RealRootAdapter(fun), borders[0],
						borders[1]);
			} catch (Exception ex) {
				root = Double.NaN;
			}
		}

		// check result
		if (Math.abs(fun.evaluate(root)) < Kernel.MIN_PRECISION) {
			return root;
		}
		return Double.NaN;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return loc.getPlain("RootOfAonIntervalBC", f.getLabel(tpl),
				aGeo.getLabel(tpl), bGeo.getLabel(tpl));
	}

	// TODO Consider locusequability
}
