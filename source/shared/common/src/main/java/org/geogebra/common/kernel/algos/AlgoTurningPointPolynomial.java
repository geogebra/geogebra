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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Finds all inflection points of a polynomial
 * 
 * @author Markus Hohenwarter
 */
public class AlgoTurningPointPolynomial extends AlgoRootsPolynomial {

	public AlgoTurningPointPolynomial(Construction cons, String[] labels,
			GeoFunctionable f) {
		super(cons, labels, f, true);
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
			solution.resetRoots();
		}

		setRootPoints(solution.curRoots, solution.curRealRoots);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("InflectionPointofA",
				"Inflection point of %0", f.getLabel(tpl));
	}

}
