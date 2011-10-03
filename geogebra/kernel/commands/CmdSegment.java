package geogebra.kernel.commands;


import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.MyError;


	/*
	 * Segment[ <GeoPoint>, <GeoPoint> ] Segment[ <GeoPoint>, <Number> ]
	 */
public class CmdSegment extends CommandProcessor {
		
		public CmdSegment(Kernel kernel) {
			super(kernel);
		}
		
	public GeoElement[] process(Command c) throws MyError {	
	    int n = c.getArgumentNumber();
	    boolean[] ok = new boolean[n];
	    GeoElement[] arg;

	    switch (n) {
	        case 2 :
	            arg = resArgs(c);

	            // segment between two points
	            if ((ok[0] = (arg[0] .isGeoPoint()))
	                && (ok[1] = (arg[1] .isGeoPoint()))) {
	                GeoElement[] ret =
	                    {
	                         kernel.Segment(
	                            c.getLabel(),
	                            (GeoPoint) arg[0],
	                            (GeoPoint) arg[1])};
	                return ret;
	            }
	            
	            // segment from point with given length
	            else if ((ok[0] = (arg[0] .isGeoPoint()))
	                && (ok[1] = (arg[1] .isNumberValue())))
					return
	                         kernel.Segment(
	                            c.getLabels(),
	                            (GeoPoint) arg[0],
	                            (NumberValue) arg[1]);
				else {
	                if (!ok[0])
	                    throw argErr(app, "Segment", arg[0]);
	                else
	                    throw argErr(app, "Segment", arg[1]);
	            }

	        case 3 : // special case for Segment[A,B,poly1] -> do nothing!
	            arg = resArgs(c);

	            if ((ok[0] = (arg[0] .isGeoPoint()))
	            		&& (ok[1] = (arg[1] .isGeoPoint()))
	            		&& (ok[2] = (arg[2] .isGeoPolygon()))) {
	                GeoElement[] ret = {};
	                return ret;
	            }
	            throw argNumErr(app, "Segment", n);

	        default :
	            throw argNumErr(app, "Segment", n);
	    }
	}
}


