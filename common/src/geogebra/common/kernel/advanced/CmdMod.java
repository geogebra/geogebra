package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoMod;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.cas.AlgoPolynomialMod;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.MyError;

/**
 * Mod[a, b]
 */
public class CmdMod extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMod(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				
				AlgoMod algo = new AlgoMod(cons, c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoFunction()))
					&& (ok[1] = (arg[1].isGeoFunction()))) {
				
				AlgoPolynomialMod algo = new AlgoPolynomialMod(cons, c.getLabel(),
						(GeoFunction) arg[0], (GeoFunction) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
