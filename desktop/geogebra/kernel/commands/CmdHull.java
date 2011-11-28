package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoNumeric;

/**
 *Hull
 */
class CmdHull extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdHull(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:

			if (arg[0].isGeoList() && arg[1].isGeoNumeric()) {
				GeoElement[] ret = { kernel.Hull(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[arg[0].isGeoList() ? 1 : 0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
