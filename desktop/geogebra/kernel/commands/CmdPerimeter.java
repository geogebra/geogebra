package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoConic;

/*
 * Perimeter[ <GeoPolygon> ]
 * Perimeter[ <Conic> ]
 */
public class CmdPerimeter extends CommandProcessor {

	public CmdPerimeter(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			// Perimeter[ <GeoPolygon> ]
			arg = resArgs(c);
			if ( (arg[0].isGeoPolygon())) {

				GeoElement[] ret = { kernel.Perimeter(c.getLabel(),
						(GeoPolygon) arg[0]) };
				return ret;

				// Perimeter[ <Conic> ]
			} else if ( (arg[0].isGeoConic())) {

				GeoElement[] ret = { kernel.Circumference(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;

			} else if ( (arg[0].isGeoLocus())) {
				//Perimeter[locus]
				GeoElement[] ret = { kernel.Perimeter(c.getLabel(),
						(GeoLocus) arg[0]) };
				return ret;

			} else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
