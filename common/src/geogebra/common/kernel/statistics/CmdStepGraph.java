package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoStepGraph;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/**
 * Stick Graph
 * 
 * @author G. Sturr
 * 
 */
public class CmdStepGraph extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdStepGraph(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:

			// StepGraph[ <x List>, <y list> ]
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))) {

				AlgoStepGraph algo = new AlgoStepGraph(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]);
				GeoElement[] ret = { algo.getSum() };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		case 3:

			// StickGraph[ <x List>, <y list>, <showPoints> ]
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))
				&& (ok[2] = (arg[2].isGeoBoolean()))){
				
				AlgoStepGraph algo = new AlgoStepGraph(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1], (GeoBoolean)arg[2]);
				GeoElement[] ret = { algo.getSum() };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}