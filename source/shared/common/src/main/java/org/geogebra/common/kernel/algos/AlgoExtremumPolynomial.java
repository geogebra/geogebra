/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Finds all local extrema of a polynomial
 * 
 * @author Markus Hohenwarter
 */
public class AlgoExtremumPolynomial extends AlgoRootsPolynomial {

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels
	 * @param f
	 *            function
	 * @param labelEnabled
	 *            whether to add this to construction and label outputs
	 */
	public AlgoExtremumPolynomial(Construction cons, String[] labels,
			GeoFunctionable f, boolean labelEnabled) {
		super(cons, labels, f, labelEnabled);
	}

	@Override
	public Commands getClassName() {
		return Commands.Extremum;
	}

	/**
	 * @return extrema
	 */
	public GeoPoint[] getExtremumPoints() {
		return super.getRootPoints();
	}

	@Override
	public void compute() {
		if (f.isDefined()) {
			yValFunction = f.getFunction();

			// roots of first derivative
			// (roots without change of sign are removed)
			calcRoots(yValFunction, 1);
		} else {
			solution.resetRoots();
		}

		setRootPoints(solution.curRoots, solution.curRealRoots);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("ExtremumOfA", "Extremum of %0",
				f.getLabel(tpl));

	}

}
