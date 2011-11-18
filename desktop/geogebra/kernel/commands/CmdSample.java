package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoBoolean;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;
import geogebra.main.MyError;

/**
 *Sample
 */
class CmdSample extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSample(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.Sample(c.getLabel(),
						(GeoList) arg[0], (NumberValue) arg[1], null) };
				return ret;

			} else
				throw argErr(app, c.getName(), getBadArg(ok, arg));

		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = { kernel.Sample(c.getLabel(),
						(GeoList) arg[0], (NumberValue) arg[1],
						(GeoBoolean) arg[2]) };
				return ret;

			} else
				throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
