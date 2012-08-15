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
import geogebra.common.kernel.algos.AlgoAreConcurrent;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.main.MyError;

/**
 * AreConcurrent[ <Line>, <Line>, <Line> ]
 */
public class CmdAreConcurrent extends CommandProcessor {

	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdAreConcurrent(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		int n=c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		if (n==3) {
			if (!(arg[0] instanceof GeoLine )){
				throw argErr(app, c.getName(), arg[0]);
			}
			if (!(arg[1] instanceof GeoLine )){
				throw argErr(app, c.getName(), arg[1]);
			}
			if (!(arg[2] instanceof GeoLine )){
				throw argErr(app, c.getName(), arg[2]);
			}
			
			AlgoAreConcurrent algo = new AlgoAreConcurrent(cons, c.getLabel(), (GeoLine) arg[0],(GeoLine) arg[1],(GeoLine) arg[2]);

			GeoElement[] ret = { algo.getResult() };
			return ret;
		}
		throw argNumErr(app, c.getName(), n);
		
	}

}
