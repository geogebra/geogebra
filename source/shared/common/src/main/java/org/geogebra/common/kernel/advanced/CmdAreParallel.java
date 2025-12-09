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

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.prover.AlgoAreParallel;
import org.geogebra.common.main.MyError;

/**
 * AreParallel[&lt;Line&gt;, &lt;Line&gt;]
 * 
 * @author Simon Weitzhofer 3rd of may 2012
 *
 */
public class CmdAreParallel extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAreParallel(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);
		if (n == 2) {
			if (!(arg[0] instanceof GeoLine)) {
				throw argErr(c, arg[0]);
			}
			if (!(arg[1] instanceof GeoLine)) {
				throw argErr(c, arg[1]);
			}

			AlgoAreParallel algo = new AlgoAreParallel(cons, c.getLabel(),
					arg[0], arg[1]);

			GeoElement[] ret = { algo.getResult() };
			return ret;
		}
		throw argNumErr(c);

	}

}
