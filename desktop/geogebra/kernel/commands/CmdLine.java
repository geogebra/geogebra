package geogebra.kernel.commands;


import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;


/*
 * Line[ <GeoPoint>, <GeoPoint> ] Line[ <GeoPoint>, <GeoVector> ] Line[
 * <GeoPoint>, <GeoLine> ]
 */
public class CmdLine extends CommandProcessor {
	
	public CmdLine(Kernel kernel) {
		super(kernel);
	}
	
public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    boolean[] ok = new boolean[n];
    GeoElement[] arg;

    switch (n) {
        case 2 :
            arg = resArgs(c);

            // line through two points
            if ((ok[0] = (arg[0] .isGeoPoint()))
                && (ok[1] = (arg[1] .isGeoPoint()))) {
                GeoElement[] ret =
                    {
                         kernel.Line(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoPoint) arg[1])};
                return ret;
            }

            // line through point with direction vector
            else if (
                (ok[0] = (arg[0] .isGeoPoint()))
                    && (ok[1] = (arg[1] .isGeoVector()))) {
                GeoElement[] ret =
                    {
                         kernel.Line(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoVector) arg[1])};
                return ret;
            }

            // line through point parallel to another line
            else if (
                (ok[0] = (arg[0] .isGeoPoint()))
                    && (ok[1] = (arg[1] .isGeoLine()))) {
                GeoElement[] ret =
                    {
                         kernel.Line(
                            c.getLabel(),
                            (GeoPoint) arg[0],
                            (GeoLine) arg[1])};
                return ret;
            }

            // syntax error
            else {
                if (!ok[0])
                    throw argErr(app, "Line", arg[0]);
                else
                    throw argErr(app, "Line", arg[1]);
            }

        default :
            throw argNumErr(app, "Line", n);
    }
}
}
