package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 *Zipf
 */
public class CmdZipf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdZipf(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())) {
				
				AlgoZipfBarChart algo = new AlgoZipfBarChart(cons, c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]);

				GeoElement[] ret = { algo.getSum() };
				return ret;
				}
				else if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())					
					&& (ok[2] = arg[2].isGeoBoolean())) {
				
				AlgoZipfBarChart algo = new AlgoZipfBarChart(cons, c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(GeoBoolean) arg[2]);

				GeoElement[] ret = { algo.getSum() };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);
			
		case 4:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[3] = arg[3].isGeoBoolean())) {
				
				AlgoZipf algo = new AlgoZipf(cons, c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2], (GeoBoolean) arg[3]);

				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
