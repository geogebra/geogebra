package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;

/**
 * Ellipse[ <GeoPoint>, <GeoPoint>, <NumberValue> ]
 */
public class CmdEllipse extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdEllipse(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { getAlgoDispatcher().Ellipse(c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1],
						(NumberValue) arg[2]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { getAlgoDispatcher()
						.Ellipse(c.getLabel(), (GeoPoint) arg[0],
								(GeoPoint) arg[1], (GeoPoint) arg[2]) };
				return ret;
			} else {
				throw argErr(app, c.getName(), getBadArg(ok,arg));
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
