package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/**
 * GCD[ <Number>, <Number> ]
 * GCD[list]
 * adapted from CmdMax by Michael Borcherds 2008-01-03
 */
public class CmdGCD extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdGCD(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {
				GeoElement[] ret = { 
						kernelA.GCD(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);
		
		case 2:			
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue()) &&
				(ok[1] = arg[1].isNumberValue())) 
			{
				GeoElement[] ret = { 
						kernelA.GCD(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;
				
			}
			throw argErr(app, c.getName(), getBadArg(ok,arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
