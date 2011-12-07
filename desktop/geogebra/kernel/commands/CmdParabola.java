package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoPoint2;

/**
 * Parabola[ <GeoPoint>, <GeoLine> ]
 */
class CmdParabola extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdParabola(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoLine()))) {
				GeoElement[] ret = { kernel.Parabola(c.getLabel(),
						(GeoPoint2) arg[0], (GeoLine) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Parabola", arg[0]);
				else
					throw argErr(app, "Parabola", arg[1]);
			}

		default:
			throw argNumErr(app, "Parabola", n);
		}
	}
}
