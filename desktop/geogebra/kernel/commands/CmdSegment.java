package geogebra.kernel.commands;


import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;


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
	                            (GeoPoint2) arg[0],
	                            (GeoPoint2) arg[1])};
	                return ret;
	            }
	            
	            // segment from point with given length
	            else if ((ok[0] = (arg[0] .isGeoPoint()))
	                && (ok[1] = (arg[1] .isNumberValue())))
					return
	                         kernel.Segment(
	                            c.getLabels(),
	                            (GeoPoint2) arg[0],
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


