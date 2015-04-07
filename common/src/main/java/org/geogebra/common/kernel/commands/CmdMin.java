package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoFunctionMinMax;
import org.geogebra.common.kernel.algos.AlgoIntervalMin;
import org.geogebra.common.kernel.algos.AlgoListMin;
import org.geogebra.common.kernel.algos.AlgoMin;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoInterval;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * Min[ <Number>, <Number> ]
 */
public class CmdMin extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMin(Kernel kernel) {
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

				AlgoListMin algo = new AlgoListMin(cons, c.getLabel(),
						(GeoList) arg[0]);

				GeoElement[] ret = { algo.getMin() };
				return ret;
			} else if (arg[0].isGeoInterval()) {
				AlgoIntervalMin algo = new AlgoIntervalMin(cons, c.getLabel(),
						(GeoInterval) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {

				AlgoMin algo = new AlgoMin(cons, c.getLabel(),
						(GeoNumberValue) arg[0], (GeoNumberValue) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else if (((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1]
					.isGeoList()))) {

				// value and frequency list
				AlgoListMin algo = new AlgoListMin(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]);

				GeoElement[] ret = { algo.getMin() };
				return ret;
			}

			throw argErr(app, c.getName(), arg[0]);

		case 3: // Min[f,a,b]
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoFunction())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {

				AlgoFunctionMinMax algo = new AlgoFunctionMinMax(cons,
						c.getLabel(), (GeoFunction) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2], true);

				GeoElement[] ret = { algo.getPoint() };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
