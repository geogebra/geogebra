package geogebra.common.kernel.commands;


import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;
import geogebra.common.kernel.AbstractKernel;


/*
 * Circle[ <GeoPoint>, <GeoNumeric> ] Circle[ <GeoPoint>, <GeoPoint> ] Circle[
 * <GeoPoint>, <GeoPoint>, <GeoPoint> ]
 */
public class CmdCircle extends CommandProcessor {
	
	public CmdCircle(AbstractKernel kernel) {
		super(kernel);
	}
	
public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isNumberValue()))) {
                GeoElement[] ret =
                    {
                         kernelA.Circle(
                            c.getLabel(),
                            (GeoPoint2) arg[0],
                            (NumberValue) arg[1])};
                return ret;
            } else if (
                (ok[0] = (arg[0] .isGeoPoint()))
                    && (ok[1] = (arg[1] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernelA.Circle(
                            c.getLabel(),
                            (GeoPoint2) arg[0],
                            (GeoPoint2) arg[1])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, c.getName(), arg[0]);
                else
                    throw argErr(app, c.getName(), arg[1]);
            }

        case 3 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))
                && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernelA.Circle(
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
}