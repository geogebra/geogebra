package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 * AngularBisector[ <GeoPoint>, <GeoPoint>, <GeoPoint> ] AngularBisector[
 * <GeoLine>, <GeoLine> ]
 */
class CmdAngularBisector extends CommandProcessorDesktop {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAngularBisector(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// angular bisector of 2 lines
			if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoLine()))) {
				return kernel.AngularBisector(c.getLabels(), (GeoLine) arg[0],
						(GeoLine) arg[1]);
			} else {
				if (!ok[0]) {
					throw argErr(app, c.getName(), arg[0]);
				} else {
					throw argErr(app, c.getName(), arg[1]);
				}
			}

		case 3:
			arg = resArgs(c);

			// angular bisector of three points
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { kernel.AngularBisector(c.getLabel(),
						(GeoPoint2) arg[0], (GeoPoint2) arg[1],
						(GeoPoint2) arg[2]) };
				return ret;
			}

			// syntax error
			else {
				if (!ok[0]) {
					throw argErr(app, c.getName(), arg[0]);
				} else if (!ok[1]) {
					throw argErr(app, c.getName(), arg[1]);
				} else {
					throw argErr(app, c.getName(), arg[2]);
				}
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
