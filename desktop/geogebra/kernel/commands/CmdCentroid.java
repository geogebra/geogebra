package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoPolygon;

/**
 * Centroid[ <Polygon> ]
 */
class CmdCentroid extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCentroid(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoPolygon())) {
				GeoElement[] ret = { kernel.Centroid(c.getLabel(),
						(GeoPolygon) arg[0]) };
				return ret;
			} else
				throw argErr(app, "Centroid", arg[0]);

		default:
			throw argNumErr(app, "Centroid", n);
		}
	}
}
