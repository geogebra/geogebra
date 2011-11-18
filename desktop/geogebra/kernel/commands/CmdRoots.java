package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunctionable;
import geogebra.main.MyError;

/**
 * Roots[ <GeoFunction>, <Number> , <Number> ]
 * (Numerical version, more than one root.)
 */
class CmdRoots extends CommandProcessor {

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
					throw argErr(app, "Root", arg[0]);
				else if (!ok[1])
					throw argErr(app, "Root", arg[1]);
				else
					throw argErr(app, "Root", arg[2]);
			}//if

		default:
			throw argNumErr(app, "Roots", n);
		}//switch
	}//process(command)
}//class CmdRoots
