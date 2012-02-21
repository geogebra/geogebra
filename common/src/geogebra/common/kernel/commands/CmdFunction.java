package geogebra.common.kernel.commands;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;


/*
 * Function[ <GeoFunction>, <NumberValue>, <NumberValue> ]
 */
public class CmdFunction extends CommandProcessor {

	public CmdFunction (Kernel kernel) {
		super(kernel);
	}

	@Override
	public  GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg = resArgs(c);

		switch (n) {
		case 1 :            	                
			if (arg[0].isGeoList()) {
				GeoElement[] ret =
				{
						kernelA.Function(
								c.getLabel(),
								(GeoList)arg[0])
				};
				return ret;
			}                                
			else {
				throw argErr(app, c.getName(), arg[0]);
			}

		case 3 :            	                
			if ((ok[0] = (arg[0] .isGeoFunctionable()))
					&& (ok[1] = (arg[1] .isNumberValue()))
					&& (ok[2] = (arg[2] .isNumberValue()))) {
				GeoElement[] ret =
				{
						kernelA.Function(
								c.getLabel(),
								((GeoFunctionable) arg[0]).getGeoFunction(),
								(NumberValue) arg[1],
								(NumberValue) arg[2])
				};
				return ret;
			}                                
			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				else
					throw argErr(app, c.getName(), arg[2]);
			}

		default :
			throw argNumErr(app, c.getName(), n);
		}
	}
}