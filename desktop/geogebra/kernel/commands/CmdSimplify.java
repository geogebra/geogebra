package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.CasEvaluableFunction;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoText;
import geogebra.main.MyError;

/**
 *Simplify
 */
class CmdSimplify extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSimplify(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if ((arg[0].isCasEvaluableObject())) {
				GeoElement[] ret = { kernel.Simplify(c.getLabel(),
						(CasEvaluableFunction) arg[0]) };
				return ret;
			} else if ((arg[0].isGeoText())) {
				GeoElement[] ret = { kernel.Simplify(c.getLabel(),
						(GeoText) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
