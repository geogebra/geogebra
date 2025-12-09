/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.advanced;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.optimization.ExtremumFinderI;

/**
 * AlgoOptimize: Abstract class for AlgoMaximize and AlgoMinimize Command
 * Minimize[ &lt;dependent variable&gt;, &lt;independent variable&gt; ] (and
 * Maximize[] ) which searches for the independent variable which gives the
 * smallest/largest result for the dependent variable.
 * 
 * Packages the relationship as a UnivariateFunction for the ExtremumFinder.
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
	private ExtremumFinderI extrFinder = null; // Uses ExtremumFinder for the
												// dirty work
	private UnivariateFunction i_am_not_a_real_function = null;
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
	 */
	public AlgoOptimize(Construction cons, String label, GeoNumberValue dep,
			Optimizer indep, OptimizationType type) {
		super(cons);
		this.optCons = cons;
		this.dep = dep.toGeoElement();
		this.indep = indep;
		this.type = type;
		extrFinder = kernel.getExtremumFinder();
		i_am_not_a_real_function = indep;
		result = indep.getGeo().copy();
		setInputOutput();
		compute();
		result.setLabel(label);
	}

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

		setOnlyOutput(result);

		setDependencies(); // done by AlgoElement
	}

	/** Implementing AlgoElement */
	@Override
	public final void compute() {
		if (isrunning) {
			return;
		} // do nothing return as fast as possible

		double old = indep.getValue();
		double res;
		isrunning = true;
		if (!indep.hasBounds()) {
			result.setUndefined();
			return;
		}
		if (type == OptimizationType.MINIMIZE) {
			res = extrFinder.findMinimum(indep.getIntervalMin(),
					indep.getIntervalMax(), i_am_not_a_real_function, 5.0E-8); // debug("Minimize
																				// ("+counter+")
																				// found
																				// "+res);
		} else {
			res = extrFinder.findMaximum(indep.getIntervalMin(),
					indep.getIntervalMax(), i_am_not_a_real_function, 5.0E-8); // debug("Maximize
																				// ("+counter+")
																				// found
																				// "+res);
		}
		indep.setValue(res);
		result.set(indep.getGeo());
		indep.setValue(old);

		// indep.updateCascade();
		optCons.updateConstruction(false);
		isrunning = false;
	}

	/**
	 * @return optimal value of independent number
	 */
	public GeoElement getResult() {
		return result;
	}

}
