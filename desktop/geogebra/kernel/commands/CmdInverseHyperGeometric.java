package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;

/**
 *InverseHyperGeometric
 */
class CmdInverseHyperGeometric extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInverseHyperGeometric(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[3] = arg[3].isNumberValue())) {
				GeoElement[] ret = { kernel.InverseHyperGeometric(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2], (NumberValue) arg[3]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
