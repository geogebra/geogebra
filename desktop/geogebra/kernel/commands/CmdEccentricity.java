package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoConic;

/**
 * Eccentricity[ <GeoConic> ]
 */
class CmdEccentricity extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdEccentricity(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { kernel.Eccentricity(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			} else
				throw argErr(app, "Eccentricity", arg[0]);

		default:
			throw argNumErr(app, "Eccentricity", n);
		}
	}
}
