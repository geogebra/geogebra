package geogebra.common.kernel.commands;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;


	/**
	 * Segment[ <GeoPoint>, <GeoPoint> ] Segment[ <GeoPoint>, <Number> ]
	 */
public class CmdSegment extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
		public CmdSegment(Kernel kernel) {
			super(kernel);
		}
		
	@Override
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
	                		getAlgoDispatcher().Segment(
	                            c.getLabel(),
	                            (GeoPoint) arg[0],
	                            (GeoPoint) arg[1])};
	                return ret;
	            }
	            
	            // segment from point with given length
	            else if ((ok[0] = (arg[0] .isGeoPoint()))
	                && (ok[1] = (arg[1] .isNumberValue())))
					return
							getAlgoDispatcher().Segment(
	                            c.getLabels(),
	                            (GeoPoint) arg[0],
	                            (NumberValue) arg[1]);
				else {
	                if (!ok[0])
	                    throw argErr(app, c.getName(), arg[0]);
					throw argErr(app, c.getName(), arg[1]);
	            }

	        case 3 : // special case for Segment[A,B,poly1] -> do nothing!
	            arg = resArgs(c);

	            if ((ok[0] = (arg[0] .isGeoPoint()))
	            		&& (ok[1] = (arg[1] .isGeoPoint()))
	            		&& (ok[2] = (arg[2] .isGeoPolygon()))) {
	                GeoElement[] ret = {};
	                return ret;
	            }
	            throw argNumErr(app, c.getName(), n);

	        default :
	            throw argNumErr(app, c.getName(), n);
	    }
	}
}


