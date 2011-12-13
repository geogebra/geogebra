package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 * Direction[ <GeoLine> ]
 */
class CmdDirection extends CommandProcessorDesktop {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDirection(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoLine())) {
				GeoElement[] ret = { kernel.Direction(c.getLabel(),
						(GeoLine) arg[0]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
