package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoFunction;

/**
 * Div[ a, b ]
 */
class CmdDiv extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDiv(Kernel kernel) {
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
				GeoElement[] ret = { kernel.Div(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoFunction()))
					&& (ok[1] = (arg[1].isGeoFunction()))) {
				GeoElement[] ret = { kernel.Div(c.getLabel(),
						(GeoFunction) arg[0], (GeoFunction) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Div", arg[0]);
				else
					throw argErr(app, "Div", arg[1]);
			}

		default:
			throw argNumErr(app, "Div", n);
		}
	}
}
