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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.main.MyError;

/**
 * ComplexRoot[ &lt;GeoFunction&gt; ]
 */
public class CmdComplexRoot extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdComplexRoot(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		// roots of polynomial
		case 1:
			arg = resArgs(c, info);
			if (arg[0].isRealValuedFunction()) {

				GeoFunctionable f = (GeoFunctionable) arg[0];

				// allow functions that can be simplified to factors of
				// polynomials
				if (!f.getConstruction().isFileLoading()
						&& !f.isPolynomialFunction(true)) {
					return null;
				}

				AlgoComplexRootsPolynomial algo = new AlgoComplexRootsPolynomial(
						cons, c.getLabels(), f);

				return algo.getRootPoints();
			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
