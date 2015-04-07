package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * Cauchy Distribution
 */
public class CmdCauchy extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCauchy(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		arg = resArgs(c);

		BooleanValue cumulative = null; // default for n=3 (false)
		switch (n) {
		case 4:
			if (!arg[2].isGeoFunction()
					|| !((GeoFunction) arg[2]).toString(
							StringTemplate.defaultTemplate).equals("x")) {
				throw argErr(app, c.getName(), arg[2]);
			}

			if (arg[3].isGeoBoolean()) {
				cumulative = (BooleanValue) arg[3];
			} else
				throw argErr(app, c.getName(), arg[3]);

			// fall through
		case 3:
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {
				if (arg[2].isGeoFunction()
						&& ((GeoFunction) arg[2]).toString(
								StringTemplate.defaultTemplate).equals("x")) {

					AlgoCauchyDF algo = new AlgoCauchyDF(cons, c.getLabel(),
							(GeoNumberValue) arg[0], (GeoNumberValue) arg[1],
							cumulative);
					return algo.getGeoElements();

				} else if (arg[2] instanceof GeoNumberValue) {

					AlgoCauchy algo = new AlgoCauchy(cons, c.getLabel(),
							(GeoNumberValue) arg[0], (GeoNumberValue) arg[1],
							(GeoNumberValue) arg[2]);

					GeoElement[] ret = { algo.getResult() };
					return ret;
				} else
					throw argErr(app, c.getName(), arg[2]);

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
