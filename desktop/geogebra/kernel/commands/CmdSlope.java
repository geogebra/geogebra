package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoLine;
import geogebra.main.MyError;

/**
 * Slope[ <GeoLine> ] Slope[ <GeoFunction> ]
 */
class CmdSlope extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSlope(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoLine()) {
				GeoElement[] ret = { kernel.Slope(c.getLabel(),
						(GeoLine) arg[0]) };
				return ret;
			} else
				throw argErr(app, "Slope", arg[0]);

		default:
			throw argNumErr(app, "Slope", n);
		}
	}
}
