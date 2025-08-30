package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * Append
 */
public class CmdIndexOf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIndexOf(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 2:
			if (arg[1].isGeoText() && arg[0].isGeoText()) {

				AlgoIndexOf algo = new AlgoIndexOf(cons, c.getLabel(), arg[0],
						arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if (arg[1].isGeoList()) {

				AlgoIndexOf algo = new AlgoIndexOf(cons, c.getLabel(), arg[0],
						arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else {
				throw argErr(c, arg[1]);
			}
		case 3:
			boolean[] ok = new boolean[2];
			if ((ok[0] = arg[1].isGeoText() && arg[0].isGeoText())
					&& (ok[1] = arg[2] instanceof GeoNumberValue)) {

				AlgoIndexOf algo = new AlgoIndexOf(cons, c.getLabel(),
						arg[0], arg[1],
						(GeoNumberValue) arg[2]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if ((ok[0] = arg[1].isGeoList())
					&& (ok[1] = arg[2] instanceof GeoNumberValue)) {

				AlgoIndexOf algo = new AlgoIndexOf(cons, c.getLabel(), arg[0],
						arg[1], (GeoNumberValue) arg[2]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if (!ok[0]) {
				throw argErr(c, arg[1]);
			}
			throw argErr(c, arg[2]);

		default:
			throw argNumErr(c);
		}
	}
}
