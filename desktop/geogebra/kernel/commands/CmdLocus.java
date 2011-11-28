package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoNumeric;
import geogebra.kernel.geos.GeoPoint;

/**
 * Locus[ <GeoPoint Q>, <GeoPoint P> ]
 * or
 * Locus[ <GeoPoint Q>, <GeoNumeric P> ]
 */
class CmdLocus extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLocus(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			// second argument has to be point on path
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				
				GeoPoint p1 = (GeoPoint) arg[0];
				GeoPoint p2 = (GeoPoint) arg[1];
				
				if (p2.isPointOnPath()) {
				
				GeoElement[] ret = { kernel.Locus(c.getLabel(),
						p1, p2) };
				return ret;
				} else {
					GeoElement[] ret = { kernel.Locus(c.getLabel(),
							p2, p1) };
					return ret;
					
				}
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoNumeric()))) {
				GeoPoint p1 = (GeoPoint) arg[0];
				GeoNumeric p2 = (GeoNumeric) arg[1];
				
				GeoElement[] ret = { kernel.Locus(c.getLabel(),
						p1, p2) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Locus", arg[0]);
				else
					throw argErr(app, "Locus", arg[1]);
			}

		default:
			throw argNumErr(app, "Locus", n);
		}
	}
}
