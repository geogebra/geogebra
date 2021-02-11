package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoFunctionMinMax;
import org.geogebra.common.kernel.algos.AlgoIntervalAbstract;
import org.geogebra.common.kernel.algos.AlgoIntervalMax;
import org.geogebra.common.kernel.algos.AlgoIntervalMin;
import org.geogebra.common.kernel.algos.AlgoListMinMax;
import org.geogebra.common.kernel.algos.AlgoMax;
import org.geogebra.common.kernel.algos.AlgoMin;
import org.geogebra.common.kernel.algos.AlgoTwoNumFunction;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * Min[ &lt;Number>, &lt;Number> ]
 */
public class CmdMinMax extends CommandProcessor {
	private final boolean isMin;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 * @param minOrMax
	 *            used command
	 */
	public CmdMinMax(Kernel kernel, Commands minOrMax) {
		super(kernel);
		this.isMin =  minOrMax == Commands.Min;
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

				AlgoElement algo = new AlgoListMinMax(cons, (GeoList) arg[0], isMin);
				algo.getOutput(0).setLabel(c.getLabel());
				GeoElement[] ret = { algo.getOutput(0) };
				return ret;
			} else if (arg[0].isGeoFunctionBoolean()) {
				AlgoIntervalAbstract algo = isMin ? new AlgoIntervalMin(cons,
						(GeoFunction) arg[0]) : new AlgoIntervalMax(cons, (GeoFunction) arg[0]);
				algo.getOutput(0).setLabel(c.getLabel());
				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else {
				throw argErr(c, arg[0]);
			}

		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {

				AlgoTwoNumFunction algo = isMin ? new AlgoMin(cons,
						(GeoNumberValue) arg[0], (GeoNumberValue) arg[1])
						: new AlgoMax(cons,
								(GeoNumberValue) arg[0], (GeoNumberValue) arg[1]);
				algo.getResult().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else if (((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoList()))) {

				// value and frequency list
				AlgoListMinMax algo = new AlgoListMinMax(cons, (GeoList) arg[0],
						(GeoList) arg[1], isMin);
				algo.getMin().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getMin() };
				return ret;
			}

			throw argErr(c, arg[0]);

		case 3: // Min[f,a,b]
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoFunction())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {

				AlgoFunctionMinMax algo = new AlgoFunctionMinMax(cons,
						c.getLabel(), (GeoFunction) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2],
						isMin);

				GeoElement[] ret = { algo.getPoint() };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

}
