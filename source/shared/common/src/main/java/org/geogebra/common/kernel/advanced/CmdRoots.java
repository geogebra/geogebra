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
import org.geogebra.common.kernel.algos.AlgoRoots;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdRoot;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * Roots[ &lt;GeoFunction&gt;, &lt;Number&gt; , &lt;Number&gt; ] (Numerical version, more
 * than one root.)
 */
public class CmdRoots extends CommandProcessor {

	/**
	 * Create new command processor
	 *
	 * @param kernel
	 *            kernel
	 */
	public CmdRoots(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);
			if (arg[0].isRealValuedFunction()) {
				return CmdRoot.nonPolyRoots(c, kernel,
						(GeoFunctionable) arg[0]);
			}
			throw argErr(c, arg[0]);

		case 3:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isRealValuedFunction())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {

				AlgoRoots algo = new AlgoRoots(cons, c.getLabels(),
						(GeoFunctionable) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2], true);

				GeoElement[] ret = algo.getRootPoints();
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}
}
