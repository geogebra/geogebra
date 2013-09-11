package geogebra.common.kernel.commands;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.kernelND.GeoPointND;
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
					arg[2] instanceof GeoNumberValue)
				return regularPolygon(c.getLabels(), (GeoPointND) arg[0], (GeoPointND) arg[1], (GeoNumberValue) arg[2]);		

		default:
			// polygon for given points
			GeoPointND[] points = new GeoPointND[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint()))
					throw argErr(app, c.getName(), arg[i]);
				points[i] = (GeoPointND) arg[i];
			}
			// everything ok
			return polygon(c.getLabels(), points);
		}	
	}
	
	/**
	 * 
	 * @param labels
	 * @param points
	 * @return polygon for points
	 */
	protected GeoElement[] polygon(String[] labels, GeoPointND[] points){
		return kernelA.Polygon(labels, points);
	}
	
	/**
	 * 
	 * @param labels
	 * @param A
	 * @param B
	 * @param n
	 * @return regular polygon
	 */
	protected GeoElement[] regularPolygon(String[] labels, GeoPointND A, GeoPointND B, GeoNumberValue n){
		return getAlgoDispatcher().RegularPolygon(labels, A, B, n);
	}
}
