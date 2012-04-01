package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/**
 * CountIf[ <GeoBoolean>, <GeoList> ]
 */
public class CmdCountIf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCountIf(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:

			arg = resArgs(c);

			// DO NOT change instanceof here (see
			// GeoFunction.isGeoFunctionable())
			if ((ok[0] = arg[0] instanceof GeoFunction)&&
					(ok[1] = arg[1].isGeoList())) {
				GeoFunction booleanFun = (GeoFunction) arg[0];
				if ((ok[0] = booleanFun.isBooleanFunction())
						&& (ok[1] = arg[1].isGeoList())) {

					GeoElement[] ret = { kernelA.CountIf(c.getLabel(),
							booleanFun, ((GeoList) arg[1])) };
					return ret;
				}
			}

			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
