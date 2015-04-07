package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoFunctionMinMax;
import org.geogebra.common.kernel.algos.AlgoIntervalMax;
import org.geogebra.common.kernel.algos.AlgoListMax;
import org.geogebra.common.kernel.algos.AlgoMax;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoInterval;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * Max[ <Number>, <Number> ]
 */
public class CmdMax extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMax(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoList()) {

				AlgoListMax algo = new AlgoListMax(cons, c.getLabel(),
						(GeoList) arg[0]);

				GeoElement[] ret = { algo.getMax() };
				return ret;
			} else if (arg[0].isGeoInterval()) {

				AlgoIntervalMax algo = new AlgoIntervalMax(cons, c.getLabel(),
						(GeoInterval) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {
				AlgoMax algo = new AlgoMax(cons, c.getLabel(),
						(GeoNumberValue) arg[0], (GeoNumberValue) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else if (((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1]
					.isGeoList()))) {

				// value and frequency list
				AlgoListMax algo = new AlgoListMax(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]);

				GeoElement[] ret = { algo.getMax() };
				return ret;
			}

			throw argErr(app, c.getName(), getBadArg(ok, arg));

		case 3: // Max[f,a,b]
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoFunction())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {

				AlgoFunctionMinMax algo = new AlgoFunctionMinMax(cons,
						c.getLabel(), (GeoFunction) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2], false);

				GeoElement[] ret = { algo.getPoint() };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
