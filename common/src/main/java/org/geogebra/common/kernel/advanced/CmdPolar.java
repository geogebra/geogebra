package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Polar[ &lt;GeoPoint&gt;, &lt;GeoConic&gt; ]
 */
public class CmdPolar extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPolar(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			AlgoDispatcher algoDispatcher = kernel.getAlgoDispatcher();
			// polar line to point relative to conic
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = { algoDispatcher.polarLine(c.getLabel(),
						(GeoPointND) arg[0], (GeoConicND) arg[1]) };
				return ret;
			}
			// pole of a line relative to conic
			if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = { algoDispatcher.polarPoint(c.getLabel(),
						(GeoLineND) arg[0], (GeoConicND) arg[1]) };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

}
