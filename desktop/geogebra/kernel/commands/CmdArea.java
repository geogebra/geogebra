package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoPolygon;

/**
 * Area[ <GeoPoint>, ..., <GeoPoint> ] Area[ <GeoConic> ] Area[ <Polygon> ]
 * (returns Polygon directly)
 */
class CmdArea extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdArea(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		if (n == 1) {
			arg = resArgs(c);

			// area of conic
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { kernel.Area(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			}
			// area of polygon = polygon variable
			else if (arg[0].isGeoPolygon()) {
				GeoElement[] ret = { kernel.Area(c.getLabel(),
						(GeoPolygon) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		}

		// area of points
		else if (n > 2) {
			arg = resArgs(c);
			GeoPoint[] points = new GeoPoint[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint()))
					throw argErr(app, "Area", arg[i]);
				else {
					points[i] = (GeoPoint) arg[i];
				}
			}
			// everything ok
			GeoElement[] ret = { kernel.Area(c.getLabel(), points) };
			return ret;
		} else
			throw argNumErr(app, "Area", n);
	}
}
