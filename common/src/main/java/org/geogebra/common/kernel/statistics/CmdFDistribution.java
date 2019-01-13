package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * FDistribution
 */
public class CmdFDistribution extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFDistribution(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		arg = resArgs(c);

		GeoBoolean cumulative = null; // default for n=3 (false)
		switch (n) {
		case 4:
			if (arg[3].isGeoBoolean()) {
				cumulative = (GeoBoolean) arg[3];
			} else {
				throw argErr(c, arg[3]);
			}

			// fall through
		case 3:
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {
				if (arg[2].isGeoFunction() && ((GeoFunction) arg[2])
						.toString(StringTemplate.defaultTemplate).equals("x")) {

					AlgoFDistributionDF algo = new AlgoFDistributionDF(cons,
							(GeoNumberValue) arg[0],
							(GeoNumberValue) arg[1],
							forceBoolean(cumulative, true));
					algo.getResult().setLabel(c.getLabel());
					return algo.getResult().asArray();

				} else if (arg[2] instanceof GeoNumberValue) {

					AlgoFDistribution algo = new AlgoFDistribution(cons,
							(GeoNumberValue) arg[0], (GeoNumberValue) arg[1],
							(GeoNumberValue) arg[2], cumulative);
					algo.getResult().setLabel(c.getLabel());
					GeoElement[] ret = { algo.getResult() };
					return ret;
				} else {
					throw argErr(c, arg[2]);
				}

			} else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			}

		default:
			throw argNumErr(c);
		}
	}
}
