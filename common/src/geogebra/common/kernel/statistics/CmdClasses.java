package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 * Classes
 */
public class CmdClasses extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdClasses(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 2:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoNumeric())) {
				AlgoClasses algo = new AlgoClasses(cons, c.getLabel(),
						(GeoList) arg[0], null, null,
						(GeoNumeric) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;

			}
			throw argErr(app, c.getName(), arg[0]);

		case 3:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())) {
				AlgoClasses algo = new AlgoClasses(cons, c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1],
						(GeoNumeric) arg[2],
						null);
				
				GeoElement[] ret = { algo.getResult() };
				return ret;

			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
