package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunctionable;
import geogebra.main.MyError;

/**
 * RectangleSum[ <GeoFunction>, <Number>, <Number>, <Number> ]
 */
class CmdRectangleSum extends CommandProcessor {

	/**
	* Create new command processor
	* @param kernel kernel
	*/
	public CmdRectangleSum (Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 5 :
			arg = resArgs(c);
			if ((ok[0] = (arg[0] .isGeoFunctionable()))
					&& (ok[1] = (arg[1] .isNumberValue()))
					&& (ok[2] = (arg[2] .isNumberValue()))
					&& (ok[3] = (arg[3] .isNumberValue()))					
					&& (ok[4] = (arg[4] .isNumberValue()))) {
				GeoElement[] ret =
				{
						kernel.RectangleSum(
								c.getLabel(),
								((GeoFunctionable) arg[0]).getGeoFunction(),
								(NumberValue) arg[1],
								(NumberValue) arg[2],
								(NumberValue) arg[3],								
								(NumberValue) arg[4])};
				return ret;
			} else
				throw argErr(app, "RectangleSum", null);

		default :
			throw argNumErr(app, "RectangleSum", n);
		}
	}
}//CmdRectangleSum
