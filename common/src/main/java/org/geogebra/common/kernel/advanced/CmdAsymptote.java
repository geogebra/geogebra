package org.geogebra.common.kernel.advanced;

import org.geogebra.common.cas.AlgoAsymptoteFunction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.implicit.AlgoAsymptoteImplicitPoly;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.main.MyError;

/**
 * Asymptote[ <GeoConic> ]
 */
public class CmdAsymptote extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAsymptote(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {

				AlgoAsymptote algo = new AlgoAsymptote(cons, c.getLabels(),
						(GeoConic) arg[0]);
				return algo.getAsymptotes();

			} else if (arg[0].isGeoFunction()) {

				AlgoAsymptoteFunction algo = new AlgoAsymptoteFunction(cons,
						c.getLabel(), (GeoFunction) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if (arg[0].isGeoImplicitCurve()) {

				AlgoAsymptoteImplicitPoly algo = new AlgoAsymptoteImplicitPoly(
						cons, c.getLabel(), (GeoImplicit) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
