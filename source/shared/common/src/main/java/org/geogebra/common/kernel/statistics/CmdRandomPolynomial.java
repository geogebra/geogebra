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

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * RandomPolynomial[degree, min, max]
 */
public class CmdRandomPolynomial extends CommandProcessor {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdRandomPolynomial(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		GeoElement[] args = resArgs(c, info);
		if (args.length != 3) {
			throw argNumErr(c);
		}
		for (int i = 1; i < 3; i++) {
			if (!(args[i] instanceof GeoNumberValue)) {
				throw argErr(c, args[i]);
			}
		}
		AlgoRandomPolynomial algo = new AlgoRandomPolynomial(cons, c.getLabel(),
				(GeoNumberValue) args[0], (GeoNumberValue) args[1],
				(GeoNumberValue) args[2]);
		return new GeoElement[] { algo.getResult() };
	}

}
