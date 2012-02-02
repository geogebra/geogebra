package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/*
 * Defined[ Object ]
 * Michael Borcherds
 * 2008-03-06
 */
public class CmdDefined extends CommandProcessor {

	public CmdDefined(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 1:

				GeoElement[] ret = { 
						kernelA.Defined(c.getLabel(),
						 arg[0] ) };
				return ret;
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
