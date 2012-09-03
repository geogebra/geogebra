package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoSegment;
import geogebra.common.main.MyError;


/**
 * LineBisector[ <GeoPoint>, <GeoPoint> ] 
 * LineBisector[ <GeoSegment> ]
 */
public class CmdLineBisector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLineBisector(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1: // segment
			arg = resArgs(c);
			// line through point orthogonal to segment
			if (arg[0].isGeoSegment()) {
				GeoElement[] ret = { getAlgoDispatcher().LineBisector(c.getLabel(),
						(GeoSegment) arg[0]) };
				return ret;
			}

			// syntax error
			throw argErr(app, c.getName(), arg[0]);

		case 2: // two points
			arg = resArgs(c);

			// line through point orthogonal to vector
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { getAlgoDispatcher().LineBisector(c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1]) };
				return ret;
			}

			// syntax error
			throw argErr(app, c.getName(), getBadArg(ok,arg));
			
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
