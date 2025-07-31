package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.main.MyError;

/**
 * Slope[ &lt;GeoLine&gt; ]
 * 
 * Slope[ &lt;GeoFunction&gt; ]
 */
public class CmdSlope extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSlope(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);
			if (arg[0].isGeoLine()) {
				GeoElement[] ret = { getAlgoDispatcher().slope(c.getLabel(),
						(GeoLine) arg[0], null) };
				return ret;
			}
			if (arg[0].isGeoFunction()) {
				GeoElement[] ret = { getAlgoDispatcher().slope(c.getLabel(),
						null, (GeoFunction) arg[0]) };
				return ret;
			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
