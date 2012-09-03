package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;

/**
 * Semicircle[ <GeoPoint>, <GeoPoint> ]
 */
public class CmdSemicircle extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSemicircle(Kernel kernel) {
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
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { getAlgoDispatcher().Semicircle(c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1]) };
				return ret;
			} 
			throw argErr(app, c.getName(), getBadArg(ok,arg));
				

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
