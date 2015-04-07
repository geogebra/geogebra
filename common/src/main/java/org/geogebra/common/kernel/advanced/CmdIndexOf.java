package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoText;
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
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

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
			} else

				throw argErr(app, c.getName(), arg[1]);
		case 3:
			boolean[] ok = new boolean[2];
			if ((ok[0] = arg[1].isGeoText() && arg[0].isGeoText())
					&& (ok[1] = arg[2] instanceof GeoNumberValue)) {

				AlgoIndexOf algo = new AlgoIndexOf(cons, c.getLabel(),
						(GeoText) arg[0], (GeoText) arg[1],
						(GeoNumberValue) arg[2]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if ((ok[0] = arg[1].isGeoList())
					&& (ok[1] = arg[2] instanceof GeoNumberValue)) {

				AlgoIndexOf algo = new AlgoIndexOf(cons, c.getLabel(), arg[0],
						(GeoList) arg[1], (GeoNumberValue) arg[2]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[1]);
			throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
