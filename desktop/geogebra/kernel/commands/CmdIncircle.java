package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPoint;

/*
 * Incircle[ <GeoPoint>, <GeoPoint>, <GeoPoint> ]
 * dsun [6/26/2011]
 */
class CmdIncircle extends CommandProcessor {
    public CmdIncircle(Kernel kernel) {
	super(kernel);
    }
    public GeoElement[] process(Command c) throws MyError {
	int n = c.getArgumentNumber();
	boolean[] ok = new boolean[n];
	GeoElement[] arg;
	switch (n) {
	case 3 :
	    arg = resArgs(c);
	    if ((ok[0] = (arg[0] .isGeoPoint()))
		&& (ok[1] = (arg[1] .isGeoPoint()))
		&& (ok[2] = (arg[2] .isGeoPoint()))) {
		GeoElement[] ret =
		{
		    kernel.Incircle(
			c.getLabel(),
			(GeoPoint) arg[0],
			(GeoPoint) arg[1],
			(GeoPoint) arg[2])};
		return ret;
	    } else {
		if (!ok[0])
		    throw argErr(app, "Incircle", arg[0]);
		else if (!ok[1])
		    throw argErr(app, "Incircle", arg[1]);
		else
		    throw argErr(app, "Incircle", arg[2]);
	    }
	default :
	    throw argNumErr(app, "Incircle", n);
	}
    }
} // CmdIncircle
