package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoList;

/**
 * Unique
 */
class CmdUnique extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUnique(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 1:
			if (ok[0] = arg[0].isGeoList()) {
				GeoElement[] ret = { kernel.Unique(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;

			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
