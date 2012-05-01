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
import geogebra.common.kernel.geos.GeoPoint2;
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
			if (arg[0] instanceof GeoPoint2 && arg[1] instanceof GeoPoint2 && arg[2] instanceof GeoPoint2){
			GeoElement[] ret = {kernelA.AreCollinear(c.getLabel(), (GeoPoint2) arg[0],(GeoPoint2) arg[1],(GeoPoint2) arg[2])};
			return ret;
			}
		}
		throw argNumErr(app, c.getName(), n);
		
	}

}
