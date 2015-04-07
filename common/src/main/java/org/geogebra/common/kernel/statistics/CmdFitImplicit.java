/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * FitImplicit.Syntax=[ <List of Points>, <Order> ]
 * 
 */
public class CmdFitImplicit extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFitImplicit(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);
		switch (n) {
		case 2:
			if ((arg[0].isGeoList()) && (arg[1] instanceof GeoNumberValue)) {

				AlgoFitImplicit algo = new AlgoFitImplicit(cons, c.getLabel(),
						(GeoList) arg[0], (GeoNumberValue) arg[1]);

				GeoElement[] ret = { algo.getFit() };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

		default:

			throw argNumErr(app, c.getName(), n);
		}
	}
}
