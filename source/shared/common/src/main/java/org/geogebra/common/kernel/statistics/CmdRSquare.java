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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;

/**
 * RSquare[&lt;List of Points&gt;,&lt;Function&gt;]
 * 
 * @author G.Sturr
 * @version 2010-9-13
 */
public class CmdRSquare extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRSquare(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c, info);
		boolean[] ok = new boolean[2];
		switch (n) {
		case 2:
			if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isRealValuedFunction())) {

				AlgoRSquare algo = new AlgoRSquare(cons,
						(GeoList) arg[0], (GeoFunctionable) arg[1]);
				algo.getRSquare().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getRSquare() };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}
}