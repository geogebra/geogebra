package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoSegment;
import geogebra.main.MyError;


/**
 * LineBisector[ <GeoPoint>, <GeoPoint> ] LineBisector[ <GeoSegment> ]
 */
class CmdLineBisector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLineBisector(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1: // segment
			arg = resArgs(c);
			// line through point orthogonal to segment
			if (ok[0] = (arg[0].isGeoSegment())) {
				GeoElement[] ret = { kernel.LineBisector(c.getLabel(),
						(GeoSegment) arg[0]) };
				return ret;
			}

			// syntax error
			else
				throw argErr(app, "LineBisector", arg[0]);

		case 2: // two points
			arg = resArgs(c);

			// line through point orthogonal to vector
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { kernel.LineBisector(c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1]) };
				return ret;
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, "LineBisector", arg[0]);
				else
					throw argErr(app, "LineBisector", arg[1]);
			}

		default:
			throw argNumErr(app, "LineBisector", n);
		}
	}
}
