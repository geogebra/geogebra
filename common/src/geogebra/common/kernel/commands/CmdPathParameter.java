package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/**
 * PathParameter[Point on path]
 * 
 */
public class CmdPathParameter extends CommandProcessor {

	/**
	 * @param kernel
	 */
	public CmdPathParameter(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if ((arg[0].isGeoPoint())) {

				GeoElement[] ret = { kernelA.PathParameter(c.getLabel(),
						(GeoPoint2) arg[0]) };
				return ret;

			} else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
