package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.App;
import geogebra.common.main.MyError;

/**
 *KeepIf
 */
public class CmdKeepIf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdKeepIf(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);
App.debug(n);
		switch (n) {
		case 2:

			if ((ok[0] = arg[0] instanceof GeoFunction)&&(ok[1] = arg[1].isGeoList())) {
				GeoFunction booleanFun = (GeoFunction) arg[0];
				if ((ok[0] = booleanFun.isBooleanFunction())
						&& (ok[1] = arg[1].isGeoList())) {

					GeoElement[] ret = { kernelA.KeepIf(c.getLabel(),
							booleanFun, ((GeoList) arg[1])) };
					return ret;
				}
			}

			throw argErr(app, c.getName(), getBadArg(ok,arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
