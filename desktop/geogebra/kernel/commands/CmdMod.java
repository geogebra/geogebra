package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunction;
import geogebra.main.MyError;

/**
 * Mod[a, b]
 */
class CmdMod extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMod(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernel.Mod(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoFunction()))
					&& (ok[1] = (arg[1].isGeoFunction()))) {
				GeoElement[] ret = { kernel.Mod(c.getLabel(),
						(GeoFunction) arg[0], (GeoFunction) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Mod", arg[0]);
				else
					throw argErr(app, "Mod", arg[1]);
			}

		default:
			throw argNumErr(app, "Mod", n);
		}
	}
}
