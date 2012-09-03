package geogebra.common.kernel.algos;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/**
 *Append
 */
public class CmdAppend extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAppend(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:

			if (arg[0].isGeoList()) {
				
				AlgoAppend algo = new AlgoAppend(cons, c.getLabel(),
						(GeoList) arg[0], arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if (arg[1].isGeoList()) {
				
				AlgoAppend algo = new AlgoAppend(cons, c.getLabel(), arg[0],
						(GeoList) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
