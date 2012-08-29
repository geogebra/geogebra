package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;

/**
 * ZProportionTest
 */
public class CmdZMeanTest extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdZMeanTest(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 4:
			if ((ok[0] = arg[0].isGeoList()) 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoText())
			) {
				
				AlgoZMeanTest algo = new AlgoZMeanTest(cons, c.getLabel(),
						(GeoList) arg[0], 
						(GeoNumeric) arg[1],
						(GeoNumeric) arg[2],
						(GeoText) arg[3]);

				GeoElement[] ret = { algo.getResult() };
				return ret;

			} 
			
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		case 5:
			if ((ok[0] = arg[0].isGeoNumeric()) 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoText())
			) {
				
				AlgoZMeanTest algo = new AlgoZMeanTest(cons, c.getLabel(),
						(GeoNumeric) arg[0], 
						(GeoNumeric) arg[1],
						(GeoNumeric) arg[2],
						(GeoNumeric) arg[3],
						(GeoText) arg[4]);

				GeoElement[] ret = { algo.getResult() };
				return ret;

			} 
			
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
