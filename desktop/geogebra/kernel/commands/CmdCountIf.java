package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoFunction;
import geogebra.kernel.geos.GeoList;

/**
 * CountIf[ <GeoBoolean>, <GeoList> ]
 */
class CmdCountIf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCountIf(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:

			arg = resArgs(c);

			// DO NOT change instanceof here (see
			// GeoFunction.isGeoFunctionable())
			if (ok[0] = (arg[0] instanceof GeoFunction)) {
				GeoFunction booleanFun = (GeoFunction) arg[0];
				if ((ok[0] = booleanFun.isBooleanFunction())
						&& (ok[1] = arg[1].isGeoList())) {

					GeoElement[] ret = { kernel.CountIf(c.getLabel(),
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
