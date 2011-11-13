package geogebra.kernel.commands;


import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.MyError;


/*
 * Circle[ <GeoPoint>, <GeoNumeric> ] Circle[ <GeoPoint>, <GeoPoint> ] Circle[
 * <GeoPoint>, <GeoPoint>, <GeoPoint> ]
 */
public class CmdCircle extends CommandProcessor {
	
	public CmdCircle(Kernel kernel) {
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
                         kernel.Circle(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (NumberValue) arg[1])};
                return ret;
            } else if (
                (ok[0] = (arg[0] .isGeoPoint()))
                    && (ok[1] = (arg[1] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.Circle(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Circle", arg[0]);
                else
                    throw argErr(app, "Circle", arg[1]);
            }

        case 3 :
            arg = resArgs(c);
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))
                && (ok[2] = (arg[2] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.Circle(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1],
                            (GeoPoint) arg[2])};
                return ret;
            } else {
                if (!ok[0])
                    throw argErr(app, "Circle", arg[0]);
                else if (!ok[1])
                    throw argErr(app, "Circle", arg[1]);
                else
                    throw argErr(app, "Circle", arg[2]);
            }

        default :
            throw argNumErr(app, "Circle", n);
    }
}
}