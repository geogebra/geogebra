package geogebra.common.kernel.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;

/*
 * Incircle[ <GeoPoint>, <GeoPoint>, <GeoPoint> ]
 * dsun [6/26/2011]
 */
public class CmdIncircle extends CommandProcessor {
    public CmdIncircle(AbstractKernel kernel) {
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
		    kernelA.Incircle(
			c.getLabel(),
			(GeoPoint2) arg[0],
			(GeoPoint2) arg[1],
			(GeoPoint2) arg[2])};
		return ret;
	    } else {
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
} // CmdIncircle
