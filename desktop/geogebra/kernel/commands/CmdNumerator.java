package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunction;

/**
 * Numerator[ <Function> ]
 */
class CmdNumerator extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdNumerator(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			if (arg[0].isGeoFunction()) {
				GeoElement[] ret = { kernel.Numerator(c.getLabel(),
						(GeoFunction) arg[0]) };
				return ret;

			}
			throw argErr(app, "Numerator", arg[0]);

		default:
			throw argNumErr(app, "Numerator", n);
		}
	}
}
