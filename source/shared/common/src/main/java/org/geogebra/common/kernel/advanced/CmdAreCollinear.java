/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.prover.AlgoAreCollinear;
import org.geogebra.common.main.MyError;

/**
 * AreCollinear[&lt;Point&gt;, &lt;Point&gt;, &lt;Point&gt; ]
 */
public class CmdAreCollinear extends CommandProcessor {

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAreCollinear(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);
		if (n == 3) {
			if (!arg[0].isGeoPoint()) {
				throw argErr(c, arg[0]);
			}
			if (!arg[1].isGeoPoint()) {
				throw argErr(c, arg[1]);
			}
			if (!arg[2].isGeoPoint()) {
				throw argErr(c, arg[2]);
			}

			AlgoAreCollinear algo = new AlgoAreCollinear(cons,
					(GeoPointND) arg[0], (GeoPointND) arg[1],
					(GeoPointND) arg[2]);
			algo.getResult().setLabel(c.getLabel());
			GeoElement[] ret = { algo.getResult() };
			return ret;
		}
		throw argNumErr(c);

	}

}
