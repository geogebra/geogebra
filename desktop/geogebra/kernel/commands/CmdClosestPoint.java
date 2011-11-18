package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.Path;
import geogebra.main.MyError;

/**
 * ClosestPoint[Point,Path] ClosestPoint[Path,Point]
 */
class CmdClosestPoint extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdClosestPoint(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// distance between two points
			if ((ok[0] = (arg[0] instanceof Path))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { kernel.ClosestPoint(c.getLabel(),
						(Path) arg[0], (GeoPoint) arg[1]) };
				return ret;
			}

			// distance between point and line
			else if ((ok[1] = (arg[1] instanceof Path))
					&& (ok[0] = (arg[0].isGeoPoint()))) {
				GeoElement[] ret = { kernel.ClosestPoint(c.getLabel(),
						(Path) arg[1], (GeoPoint) arg[0]) };
				return ret;
			}

			// syntax error
			else {
				if (ok[0] && !ok[1])
					throw argErr(app, c.getName(), arg[1]);
				else
					throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
