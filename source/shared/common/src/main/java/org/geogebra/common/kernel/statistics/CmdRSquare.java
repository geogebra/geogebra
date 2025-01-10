package org.geogebra.common.kernel.statistics;

/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

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
		GeoElement[] arg = resArgs(c);
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