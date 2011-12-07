package geogebra.kernel.commands;


import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoFunctionable;
import geogebra.kernel.geos.GeoList;


/*
 * Function[ <GeoFunction>, <NumberValue>, <NumberValue> ]
 */
public class CmdFunction extends CommandProcessor {

	public CmdFunction (Kernel kernel) {
		super(kernel);
	}

	public  GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg = resArgs(c);

		switch (n) {
		case 1 :            	                
			if (arg[0].isGeoList()) {
				GeoElement[] ret =
				{
						kernel.Function(
								c.getLabel(),
								(GeoList)arg[0])
				};
				return ret;
			}                                
			else {
				throw argErr(app, "Function", arg[0]);
			}

		case 3 :            	                
			if ((ok[0] = (arg[0] .isGeoFunctionable()))
					&& (ok[1] = (arg[1] .isNumberValue()))
					&& (ok[2] = (arg[2] .isNumberValue()))) {
				GeoElement[] ret =
				{
						kernel.Function(
								c.getLabel(),
								((GeoFunctionable) arg[0]).getGeoFunction(),
								(NumberValue) arg[1],
								(NumberValue) arg[2])
				};
				return ret;
			}                                
			else {
				if (!ok[0])
					throw argErr(app, "Function", arg[0]);
				else if (!ok[1])
					throw argErr(app, "Function", arg[1]);
				else
					throw argErr(app, "Function", arg[2]);
			}

		default :
			throw argNumErr(app, "Function", n);
		}
	}
}