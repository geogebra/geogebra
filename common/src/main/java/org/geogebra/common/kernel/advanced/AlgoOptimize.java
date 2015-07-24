/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.optimization.ExtremumFinder;
import org.geogebra.common.kernel.roots.RealRootFunction;

/**
 * AlgoOptimize: Abstract class for AlgoMaximize and AlgoMinimize Command
 * Minimize[ <dependent variable>, <independent variable> ] (and Minimize[] )
 * which searches for the independent variable which gives the smallest/largest
 * result for the dependent variable.
 * 
 * Packages the relationship as a RealRootFunction for the ExtremumFinder.
 * 
 * @author Hans-Petter Ulven
 * @version 20.02.2011
 * 
 *          ToDo: -Bug: Intermediate steps in searching produces traces in
 *          Graphic view -Find a better way to avoid all the recursive calls,
 *          even if they are not executed all the way
 * 
 */

public abstract class AlgoOptimize extends AlgoElement {
	/** optimization types */
	public enum OptimizationType {
		/** minimize */
		MINIMIZE,
		/** maximize */
		MAXIMIZE
	}

	private Construction optCons = null;
	private ExtremumFinder extrFinder = null; // Uses ExtremumFinder for the
												// dirty work
	private RealRootFunction i_am_not_a_real_function = null;
	private GeoElement dep = null;
	private Optimizer indep = null;
	private GeoElement result = null;
	private OptimizationType type = OptimizationType.MINIMIZE;
	private boolean isrunning = false; // To stop recursive calls. Both Maximize
										// and Minimize.

	/**
	 * Constructor for optimization algos
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param dep
	 *            dependent value
	 * @param indep
	 *            independent number
	 * @param type
	 *            maximize or minimize
	 * */
	public AlgoOptimize(Construction cons, String label, NumberValue dep,
			Optimizer indep, OptimizationType type) {
		super(cons);
		this.optCons = cons;
		this.dep = dep.toGeoElement();
		this.indep = indep;
		this.type = type;
		extrFinder = new ExtremumFinder();
		i_am_not_a_real_function = indep;
		result = indep.getGeo().copy();
		setInputOutput();
		compute();
		result.setLabel(label);
	}// Constructor for Maximize

	/** Implementing AlgoElement */
	@Override
	protected void setInputOutput() {
		/*
		 * input = new GeoElement[1]; input[0] = geoList;
		 * 
		 * output = new GeoElement[1]; output[0] = max;
		 */
		input = new GeoElement[2];
		input[0] = dep;
		input[1] = indep.getGeo();

		super.setOutputLength(1);
		super.setOutput(0, result);

		setDependencies(); // done by AlgoElement
	}// setInputOutput()

	/** Implementing AlgoElement */
	@Override
	public final void compute() {
		double old = 0.0d, res = 0.0;

		if (isrunning) {
			return;
		} // do nothing return as fast as possible

		old = indep.getValue();
		isrunning = true;
		if (!indep.hasBounds()) {
			result.setUndefined();
			return;
		}
		if (type == OptimizationType.MINIMIZE) {
			res = extrFinder.findMinimum(indep.getIntervalMin(),
					indep.getIntervalMax(), i_am_not_a_real_function, 5.0E-8); // debug("Minimize ("+counter+") found "+res);
		} else {
			res = extrFinder.findMaximum(indep.getIntervalMin(),
					indep.getIntervalMax(), i_am_not_a_real_function, 5.0E-8); // debug("Maximize ("+counter+") found "+res);
		}
		indep.setValue(res);
		result.set(indep.getGeo());
		indep.setValue(old);

		// indep.updateCascade();
		optCons.updateConstruction();
		isrunning = false;
		return;

	}// compute()

	/**
	 * @return optimal value of independent number
	 */
	public GeoElement getResult() {
		return result;

	}// getMinimized()


}// abstract class AlgoOptimize
