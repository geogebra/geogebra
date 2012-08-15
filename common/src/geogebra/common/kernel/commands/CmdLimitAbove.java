package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.cas.AlgoLimitAbove;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;

/**
 *LimitAbove
 */
public class CmdLimitAbove extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLimitAbove(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:
			if ((ok = arg[0].isGeoFunction()) && (arg[1].isNumberValue())) {
				AlgoLimitAbove algo = new AlgoLimitAbove(cons, c.getLabel(),
						(GeoFunction) arg[0], (NumberValue) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
