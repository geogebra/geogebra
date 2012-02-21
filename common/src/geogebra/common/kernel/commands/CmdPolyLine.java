package geogebra.common.kernel.commands;


import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;


/*
 * Polygon[ <GeoPoint>, ..., <GeoPoint> ]
 * Polygon[ <GeoPoint>, <GeoPoint>, <Number>] for regular polygon
 */
public class CmdPolyLine extends CommandProcessor {
	
	public CmdPolyLine(Kernel kernel) {
		super(kernel);
	}
	
@Override
public GeoElement[] process(Command c) throws MyError {
    int n = c.getArgumentNumber();
    GeoElement[] arg;

    arg = resArgs(c);
    switch (n) {
	    case 0 :
	    	throw argNumErr(app, c.getName(), n);
    	//G.Sturr 2010-3-14
		case 1:
		if (arg[0].isGeoList())
			return kernelA.PolyLine(c.getLabels(), (GeoList) arg[0]);
		//END G.Sturr
		
       default:
			// polygon for given points
	        GeoPoint2[] points = new GeoPoint2[n];
	        // check arguments
	        for (int i = 0; i < n; i++) {
	            if (!(arg[i].isGeoPoint()))
					throw argErr(app, c.getName(), arg[i]);
				else {
	                points[i] = (GeoPoint2) arg[i];
	            }
	        }
	        // everything ok
	        return kernelA.PolyLine(c.getLabels(), points);
		}	
}
}
