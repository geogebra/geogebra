package geogebra.common.kernel.commands;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;


/**
 * Polygon[ <GeoPoint>, ..., <GeoPoint> ]
 * Polygon[ <GeoPoint>, <GeoPoint>, <Number>] for regular polygon
 */
public class CmdPolygon extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdPolygon(Kernel kernel) {
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
			return getAlgoDispatcher().Polygon(c.getLabels(), (GeoList) arg[0]);
		//END G.Sturr
		
    	case 3:        
        // regular polygon
        if (arg[0].isGeoPoint() && 
	        arg[1].isGeoPoint() &&
	        arg[2].isNumberValue())
				return getAlgoDispatcher().RegularPolygon(c.getLabels(), (GeoPoint) arg[0], (GeoPoint) arg[1], (NumberValue) arg[2]);		
        
        default:
			// polygon for given points
	        GeoPoint[] points = new GeoPoint[n];
	        // check arguments
	        for (int i = 0; i < n; i++) {
	            if (!(arg[i].isGeoPoint()))
					throw argErr(app, c.getName(), arg[i]);
				points[i] = (GeoPoint) arg[i];
	        }
	        // everything ok
	        return kernelA.Polygon(c.getLabels(), points);
		}	
}
}
