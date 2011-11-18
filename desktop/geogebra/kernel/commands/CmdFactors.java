package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunction;
import geogebra.kernel.geos.GeoNumeric;
import geogebra.main.MyError;

/**
 *Factors
 */
class CmdFactors extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFactors(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if (ok[0] = (arg[0].isGeoFunction())) {
				GeoElement[] ret = { kernel.Factors(c.getLabel(),
						(GeoFunction) arg[0]) };
				return ret;
			} else	if (ok[0] = (arg[0].isGeoNumeric())) {
					GeoElement[] ret = { kernel.PrimeFactorisation(c.getLabel(),
							(GeoNumeric) arg[0]) };
					return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
