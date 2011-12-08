package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 *Poisson Distribution
 */
class CmdPoisson extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPoisson(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		
		case 1:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())) {
				GeoElement[] ret = { kernel.Poisson(c.getLabel(),
						(NumberValue) arg[0]) };
				return ret;
			}
			else
				throw argErr(app, c.getName(), arg[0]);

			
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isGeoBoolean())) {
				GeoElement[] ret = { kernel.Poisson(c.getLabel(),
						(NumberValue) arg[0], (GeoBoolean)arg[1]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);
		
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = { kernel.Poisson(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(GeoBoolean) arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
