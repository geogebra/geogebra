package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoText;

/**
 * TTestPaired (paired t test)
 */
class CmdTTestPaired extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTTestPaired(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 3:
			if ((ok[0] = arg[0].isGeoList()) 
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoText())) {
				GeoElement[] ret = { kernel.TTestPaired(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1], (GeoText) arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else 
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
