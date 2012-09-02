package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoFunctionInvert;
import geogebra.common.kernel.algos.AlgoInvert;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/**
 * Invert[ <Function> ]
 * Invert[ <Matrix> ]
 */
public class CmdInvert extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInvert(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			if (arg[0].isGeoFunction()) {
				
				AlgoFunctionInvert algo = new AlgoFunctionInvert(cons, c.getLabel(),
						(GeoFunction) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else if (arg[0].isGeoList()) {
				
				AlgoInvert algo = new AlgoInvert(cons, c.getLabel(),
						(GeoList) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;

			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
