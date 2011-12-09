package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 * LinearEccentricity[ <GeoConic> ] Excentricity[ <GeoConic> ]
 */
class CmdExcentricity extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdExcentricity(Kernel kernel) {
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
				GeoElement[] ret = { kernel.Excentricity(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			} else
				throw argErr(app, "Excentricity", arg[0]);

		default:
			throw argNumErr(app, "Excentricity", n);
		}
	}
}
