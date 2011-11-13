package geogebra.kernel.commands;


import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;


/*
 * Polygon[ <GeoPoint>, ..., <GeoPoint> ]
 * Polygon[ <GeoPoint>, <GeoPoint>, <Number>] for regular polygon
 */
public class CmdPolyLine extends CommandProcessor {
	
	public CmdPolyLine(Kernel kernel) {
		super(kernel);
	}
	
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
			return kernel.PolyLine(c.getLabels(), (GeoList) arg[0]);
		//END G.Sturr
		
       default:
			// polygon for given points
	        GeoPoint[] points = new GeoPoint[n];
	        // check arguments
	        for (int i = 0; i < n; i++) {
	            if (!(arg[i].isGeoPoint()))
					throw argErr(app, c.getName(), arg[i]);
				else {
	                points[i] = (GeoPoint) arg[i];
	            }
	        }
	        // everything ok
	        return kernel.PolyLine(c.getLabels(), points);
		}	
}
}
