package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;

/**
 * Flatten[ <GeoList> ]
 */
public class CmdFlatten extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFlatten(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		if (n != 1) {
			throw argNumErr(app, c.getName(), n);
		}

		GeoElement arg;
		arg = resArgs(c)[0];

		if (arg.isGeoList()) {

			AlgoFlatten algo = new AlgoFlatten(cons, c.getLabel(),
					(GeoList) arg);

			GeoElement[] ret = { algo.getResult() };
			return ret;
		}
		throw argErr(app, c.getName(), arg);
	}
}
