package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoAreaPoints;
import org.geogebra.common.kernel.algos.AlgoAreaPolygon;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

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
						(GeoConicND) arg[0]) };
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
			GeoPointND[] points = new GeoPointND[n];
			boolean is3D = false;
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint())) {
					throw argErr(app, c.getName(), arg[i]);
				}
				points[i] = (GeoPointND) arg[i];
				if (!is3D && arg[i].isGeoElement3D()) {
					is3D = true;
				}
			}
			// everything ok

			AlgoAreaPoints algo = getAlgoAreaPoints(cons, c.getLabel(), points,
					is3D);

			GeoElement[] ret = { algo.getArea() };
			return ret;
		} else {
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param points
	 *            points
	 * @param is3D
	 *            if there is a 3D point
	 * @return algo
	 */
	protected AlgoAreaPoints getAlgoAreaPoints(Construction cons, String label,
			GeoPointND[] points, boolean is3D) {
		return new AlgoAreaPoints(cons, label, points);
	}
}
