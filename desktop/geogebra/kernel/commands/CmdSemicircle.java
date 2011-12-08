package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoPoint2;

/**
 * Semicircle[ <GeoPoint>, <GeoPoint> ]
 */
class CmdSemicircle extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSemicircle(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { kernel.Semicircle(c.getLabel(),
						(GeoPoint2) arg[0], (GeoPoint2) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Semicircle", arg[0]);
				else
					throw argErr(app, "Semicircle", arg[1]);
			}

		default:
			throw argNumErr(app, "Semicircle", n);
		}
	}
}
