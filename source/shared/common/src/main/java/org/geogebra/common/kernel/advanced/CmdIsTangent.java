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
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.prover.AlgoIsTangent;
import org.geogebra.common.main.MyError;

/**
 * IsTangent[&lt;Line&gt;, &lt;Conic&gt;]
 * 
 * @author Zoltan Kovacs
 *
 */
public class CmdIsTangent extends CommandProcessor {

	/**
	 * Create new command processor
	 *
	 * @param kernel kernel
	 */
	public CmdIsTangent(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c, info);
		if (n == 2) {

			if ((ok[0] = arg[0].isGeoLine())
					&& (ok[1] = arg[1].isGeoConic())) {

				AlgoIsTangent algo = new AlgoIsTangent(cons, c.getLabel(),
						(GeoLine) arg[0], (GeoConic) arg[1]);
				GeoElement[] ret = {algo.getResult()};

				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));
		}
		throw argNumErr(c);
	}
}

