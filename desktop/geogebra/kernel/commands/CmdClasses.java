package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoNumeric;

/**
 * Classes
 */
class CmdClasses extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdClasses(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 2:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoNumeric())) {
				GeoElement[] ret = { kernel.Classes(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1]) };
				return ret;

			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		case 3:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())) {
				GeoElement[] ret = { kernel.Classes(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1],
						(GeoNumeric) arg[2]) };
				return ret;

			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
