package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoAreaPoints;
import geogebra.common.kernel.algos.AlgoAreaPolygon;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.main.MyError;

/**
 * Area[ <GeoPoint>, ..., <GeoPoint> ] Area[ <GeoConic> ] Area[ <Polygon> ]
 * (returns Polygon directly)
 */
public class CmdArea extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdArea(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		if (n == 1) {
			arg = resArgs(c);

			// area of conic
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { getAlgoDispatcher().Area(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			}
			// area of polygon = polygon variable
			else if (arg[0].isGeoPolygon()) {
				
				AlgoAreaPolygon algo = new AlgoAreaPolygon(cons, c.getLabel(),
						(GeoPolygon) arg[0]);

				GeoElement[] ret = { algo.getArea() };
				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}
		}

		// area of points
		else if (n > 2) {
			arg = resArgs(c);
			GeoPoint[] points = new GeoPoint[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint())) {
					throw argErr(app, c.getName(), arg[i]);
				}
				points[i] = (GeoPoint) arg[i];
			}
			// everything ok
			
			AlgoAreaPoints algo = new AlgoAreaPoints(cons, c.getLabel(), points);

			GeoElement[] ret = { algo.getArea() };
			return ret;
		} else {
			throw argNumErr(app, c.getName(), n);
		}
	}
}
