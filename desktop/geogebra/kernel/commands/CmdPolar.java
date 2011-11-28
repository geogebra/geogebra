package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPoint;

/**
 * Polar[ <GeoPoint>, <GeoConic> ]
 */
class CmdPolar extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPolar(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// polar line to point relative to conic
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = { kernel.PolarLine(c.getLabel(),
						(GeoPoint) arg[0], (GeoConic) arg[1]) };
				return ret;
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, "Polar", arg[0]);
				else
					throw argErr(app, "Polar", arg[1]);
			}

		default:
			throw argNumErr(app, "Polar", n);
		}
	}
}
