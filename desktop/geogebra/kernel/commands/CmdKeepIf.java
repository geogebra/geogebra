package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.geos.GeoList;
import geogebra.main.Application;

/**
 *KeepIf
 */
class CmdKeepIf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdKeepIf(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);
Application.debug(n);
		switch (n) {
		case 2:

			if (ok[0] = (arg[0] instanceof GeoFunction)) {
				GeoFunction booleanFun = (GeoFunction) arg[0];
				if ((ok[0] = booleanFun.isBooleanFunction())
						&& (ok[1] = arg[1].isGeoList())) {

					GeoElement[] ret = { kernel.KeepIf(c.getLabel(),
							(GeoFunction) booleanFun, ((GeoList) arg[1])) };
					return ret;
				}
			}

			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);


		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
