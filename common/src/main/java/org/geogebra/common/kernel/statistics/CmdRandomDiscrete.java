package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;

/**
 * RandomDiscrete[]
 * 
 * @author Rrubaa Panchendrarajan (GSoC 2015)
 */
public class CmdRandomDiscrete extends CommandProcessor {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdRandomDiscrete(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);

		if (n != 2) {
			throw argNumErr(c);
		}

		if (!arg[0].isGeoList()) {
			throw argErr(arg[0], c);
		}
		if (!arg[1].isGeoList()) {
			throw argErr(arg[1], c);
		}
		AlgoRandomDiscrete algo = new AlgoRandomDiscrete(cons, c.getLabel(),
				(GeoList) arg[0], (GeoList) arg[1]);

		GeoElement[] ret = { algo.getResult() };
		return ret;

	}

}
