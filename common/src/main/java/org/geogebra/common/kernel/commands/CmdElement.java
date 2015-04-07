package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoListElement;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;

/**
 * Element[ <list>, <n> ] Element[ <point>, <n> ]
 */
public class CmdElement extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdElement(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 0:
		case 1:
			throw argNumErr(app, c.getName(), n);
		case 2:
			arg = resArgs(c);
			// list
			if ((ok[0] = arg[0].isGeoList() || arg[0] instanceof GeoList)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {

				AlgoListElement algo = new AlgoListElement(cons, c.getLabel(),
						(GeoList) arg[0], (GeoNumberValue) arg[1]);

				GeoElement[] ret = { algo.getElement() };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			arg = resArgs(c);
			// list
			GeoNumberValue[] nvs = new GeoNumberValue[n - 1];
			if (!arg[0].isGeoList()) {
				throw argErr(app, c.getName(), arg[0]);
			}
			for (int i = 1; i < n; i++) {
				if (arg[i] instanceof GeoNumberValue)
					nvs[i - 1] = (GeoNumberValue) arg[i];
				else {
					App.printStacktrace(arg[0]);
					throw argErr(app, c.getName(), arg[i]);
				}
			}

			AlgoListElement algo = new AlgoListElement(cons, c.getLabel(),
					(GeoList) arg[0], nvs);

			GeoElement[] ret = { algo.getElement() };
			return ret;
		}

	}
}
