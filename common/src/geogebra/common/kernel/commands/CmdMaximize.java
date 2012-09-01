package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoMaximize;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 * Maximize[ <dependent variable>, <independent variable> ]
 */
public class CmdMaximize extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMaximize(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoElement()))
					&& (ok[1] = (arg[1].isGeoNumeric()))) {
				
				AlgoMaximize algo = new AlgoMaximize(cons, c.getLabel(), (NumberValue)arg[0],
						(GeoNumeric) arg[1]);
				
        		GeoElement[] ret= { algo.getResult() };

				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}// process(command)

}// CmdMaximize(kernel)
