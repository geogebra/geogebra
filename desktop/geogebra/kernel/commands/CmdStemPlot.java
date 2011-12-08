package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoClass;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoList;

/**
 * StemPlot
 */
class CmdStemPlot extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdStemPlot(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 1:
			if ((ok[0] = (arg[0].isGeoList()))) {
				GeoList list = (GeoList) arg[0];

				GeoElement[] ret = { kernel.StemPlot(c.getLabel(), list) };
				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		case 2:
			if (!arg[0].isGeoList()) {
				throw argErr(app, c.getName(), arg[0]);
			}
			if (!arg[1].isGeoNumeric()) {
				throw argErr(app, c.getName(), arg[1]);
			}

			GeoElement[] ret = { kernel.StemPlot(c.getLabel(),
					(GeoList) arg[0], (GeoNumeric) arg[1]) };
			return ret;

		case 0:
			throw argNumErr(app, c.getName(), n);

		default:

			GeoList list = wrapInList(kernel, arg, arg.length, GeoClass.DEFAULT);
			if (list != null) {
				GeoElement[] ret2 = { kernel.StemPlot(c.getLabel(), list) };
				return ret2;
			}

			throw argErr(app, c.getName(), arg[0]);
		}
	}
}
