package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 * Roots[ <GeoFunction>, <Number> , <Number> ]
 * (Numerical version, more than one root.)
 */
class CmdRoots extends CommandProcessorDesktop {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRoots(Kernel kernel) {
		super(kernel);
	}//Constructor

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = kernel.Roots(c.getLabels(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2]);
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				else
					throw argErr(app, c.getName(), arg[2]);
			}//if

		default:
			throw argNumErr(app, c.getName(), n);
		}//switch
	}//process(command)
}//class CmdRoots
