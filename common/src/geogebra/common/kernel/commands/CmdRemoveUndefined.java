package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.common.kernel.AbstractKernel;

/*
 * Clean[ <List> ]
 * Michael Borcherds
 * 2008-03-06
 */
public class CmdRemoveUndefined extends CommandProcessor {

	public CmdRemoveUndefined(AbstractKernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 1:

			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						kernelA.RemoveUndefined(c.getLabel(),
						(GeoList) arg[0] ) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
