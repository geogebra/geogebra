package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 * PathParameter[Point on path]
 * 
 */
class CmdPathParameter extends CommandProcessor {

	/**
	 * @param kernel
	 */
	public CmdPathParameter(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if ((arg[0].isGeoPoint())) {

				GeoElement[] ret = { kernel.PathParameter(c.getLabel(),
						(GeoPoint2) arg[0]) };
				return ret;

			} else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
