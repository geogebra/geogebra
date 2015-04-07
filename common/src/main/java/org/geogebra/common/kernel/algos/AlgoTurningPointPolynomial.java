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
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Finds all inflection points of a polynomial
 * 
 * @author Markus Hohenwarter
 */
public class AlgoTurningPointPolynomial extends AlgoRootsPolynomial {

	public AlgoTurningPointPolynomial(Construction cons, String[] labels,
			GeoFunction f) {
		super(cons, labels, f);
	}

	@Override
	public Commands getClassName() {
		return Commands.TurningPoint;
	}

	public GeoPoint[] getInflectionPoints() {
		return super.getRootPoints();
	}

	@Override
	public void compute() {
		if (!f.isPolynomialFunction(true)) {
			initRootPoints(1);
			getOutput(0).setUndefined();
			return;
		}
		if (f.isDefined()) {
			yValFunction = f.getFunction();

			// roots of second derivative
			// (roots without change of sign are removed)
			calcRoots(yValFunction, 2);
		} else {
			curRealRoots = 0;
		}

		setRootPoints(curRoots, curRealRoots);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("InflectionPointofA", f.getLabel(tpl));
	}

}
