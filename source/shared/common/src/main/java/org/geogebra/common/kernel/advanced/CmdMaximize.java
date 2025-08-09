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
 * Maximize[ &lt;dependent variable&gt;, &lt;independent variable&gt; ]
 */
public class CmdMaximize extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMaximize(Kernel kernel) {
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

				AlgoMaximize algo = new AlgoMaximize(cons, c.getLabel(),
						(GeoNumberValue) arg[0], (GeoNumeric) arg[1]);

				GeoElement[] ret = { algo.getResult() };

				return ret;
			}
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isGeoPoint())) {

				AlgoMaximize algo = new AlgoMaximize(cons, c.getLabel(),
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
