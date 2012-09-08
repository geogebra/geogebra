package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoBarGraph;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 * StickGraph[ <List>, <FreqList> ]
 */
public class CmdBarGraph extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdBarGraph(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoNumeric()))) {
				
				AlgoBarGraph algo = new AlgoBarGraph(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1], (GeoNumeric) arg[2]);

				GeoElement[] ret = { algo.getSum() };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
