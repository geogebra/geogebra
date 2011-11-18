package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoText;
import geogebra.main.MyError;

/**
 *Append
 */
class CmdIndexOf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIndexOf(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:
			if (arg[1].isGeoText() && arg[0].isGeoText()) {
				GeoElement[] ret = { kernel.IndexOf(c.getLabel(),
						(GeoText) arg[0], (GeoText) arg[1]) };
				return ret;
			} else if (arg[1].isGeoList()) {
				GeoElement[] ret = { kernel.IndexOf(c.getLabel(), arg[0],
						(GeoList) arg[1]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[1]);
		case 3:
			boolean[] ok = new boolean[2];
			if ((ok[0] = arg[1].isGeoText() && arg[0].isGeoText())
					&& (ok[1] = arg[2].isNumberValue())) {
				GeoElement[] ret = { kernel.IndexOf(c.getLabel(),
						(GeoText) arg[0], (GeoText) arg[1],
						(NumberValue) arg[2]) };
				return ret;
			} else if ((ok[0] = arg[1].isGeoList())
					&& (ok[1] = arg[2].isNumberValue())) {
				GeoElement[] ret = { kernel.IndexOf(c.getLabel(), arg[0],
						(GeoList) arg[1], (NumberValue) arg[2]) };
				return ret;
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[1]);
			throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
