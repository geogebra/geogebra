package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Minimize[ &lt;dependent variable&gt;, &lt;independent variable&gt; ]
 */
public class CmdMinimize extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMinimize(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isGeoNumeric())) {

				AlgoMinimize algo = new AlgoMinimize(cons, c.getLabel(),
						(GeoNumberValue) arg[0], (GeoNumeric) arg[1]);

				GeoElement[] ret = { algo.getResult() };

				return ret;
			}
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isGeoPoint())) {

				AlgoMinimize algo = new AlgoMinimize(cons, c.getLabel(),
						(GeoNumberValue) arg[0], (GeoPointND) arg[1]);

				GeoElement[] ret = { algo.getResult() };

				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));
		default:
			throw argNumErr(c);
		}
	}

}
