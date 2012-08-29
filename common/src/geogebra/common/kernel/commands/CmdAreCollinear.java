/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.prover.AlgoAreCollinear;
import geogebra.common.main.MyError;

/**
 * AreCollinear[<Point>, <Point>, <Point> ]
 */
public class CmdAreCollinear extends CommandProcessor {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdAreCollinear(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		int n=c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		if (n==3) {
			if (!(arg[0] instanceof GeoPoint )){
				throw argErr(app, c.getName(), arg[0]);
			}
			if (!(arg[1] instanceof GeoPoint )){
				throw argErr(app, c.getName(), arg[1]);
			}
			if (!(arg[2] instanceof GeoPoint )){
				throw argErr(app, c.getName(), arg[2]);
			}
			
			AlgoAreCollinear algo = new AlgoAreCollinear(cons, c.getLabel(), (GeoPoint) arg[0],(GeoPoint) arg[1],(GeoPoint) arg[2]);

			GeoElement[] ret = { algo.getResult() };
			return ret;
		}
		throw argNumErr(app, c.getName(), n);
		
	}

}
