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
import org.geogebra.common.kernel.prover.AlgoAreConcurrent;
import org.geogebra.common.main.MyError;

/**
 * AreConcurrent[ &lt;Line&gt;, &lt;Line&gt;, &lt;Line&gt; ]
 */
public class CmdAreConcurrent extends CommandProcessor {

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAreConcurrent(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);
		if (n == 3) {
			if (!(arg[0] instanceof GeoLine)) {
				throw argErr(c, arg[0]);
			}
			if (!(arg[1] instanceof GeoLine)) {
				throw argErr(c, arg[1]);
			}
			if (!(arg[2] instanceof GeoLine)) {
				throw argErr(c, arg[2]);
			}

			AlgoAreConcurrent algo = new AlgoAreConcurrent(cons, c.getLabel(),
					(GeoLine) arg[0], (GeoLine) arg[1], (GeoLine) arg[2]);

			GeoElement[] ret = { algo.getResult() };
			return ret;
		}
		throw argNumErr(c);

	}

}
