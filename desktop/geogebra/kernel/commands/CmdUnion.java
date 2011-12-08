package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoPolygon;

/**
 *Union
 */
class CmdUnion extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUnion(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:

			if (arg[0].isGeoList() && arg[1].isGeoList()) {
				GeoElement[] ret = { kernel.Union(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]) };
				return ret;
			} else
				if (arg[0].isGeoPolygon() && arg[1].isGeoPolygon()) {
					GeoElement[] ret = kernel.Union(c.getLabels(), (GeoPolygon) arg[0],
							(GeoPolygon) arg[1]);
					return ret;
				} else
					throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
