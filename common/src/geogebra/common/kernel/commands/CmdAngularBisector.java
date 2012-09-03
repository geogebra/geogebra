package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;

/**
 * AngularBisector[ <GeoPoint>, <GeoPoint>, <GeoPoint> ] AngularBisector[
 * <GeoLine>, <GeoLine> ]
 */
public class CmdAngularBisector extends CommandProcessor {

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
				return getAlgoDispatcher().AngularBisector(c.getLabels(), (GeoLine) arg[0],
						(GeoLine) arg[1]);
			}
			if (!ok[0]) {
				throw argErr(app, c.getName(), arg[0]);
			}
			throw argErr(app, c.getName(), arg[1]);

		case 3:
			arg = resArgs(c);

			// angular bisector of three points
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { getAlgoDispatcher().AngularBisector(c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1],
						(GeoPoint) arg[2]) };
				return ret;
			}
			if (!ok[0]) {
				throw argErr(app, c.getName(), arg[0]);
			} else if (!ok[1]) {
				throw argErr(app, c.getName(), arg[1]);
			} else {
				throw argErr(app, c.getName(), arg[2]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
