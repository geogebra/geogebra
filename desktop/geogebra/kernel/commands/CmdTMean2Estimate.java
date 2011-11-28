package geogebra.kernel.commands;

import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoBoolean;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoNumeric;

/**
 * TEstimate (t confidence interval estimate of the difference of means)
 */
class CmdTMean2Estimate extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTMean2Estimate(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 4:
			if ((ok[0] = arg[0].isGeoList()) 
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoBoolean())) {
				GeoElement[] ret = { kernel.TMean2Estimate(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1], (GeoNumeric) arg[2], (GeoBoolean) arg[3]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		case 8:
			if ((ok[0] = arg[0].isGeoNumeric()) 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())
					&& (ok[5] = arg[5].isGeoNumeric())
					&& (ok[6] = arg[6].isGeoNumeric())
					&& (ok[7] = arg[7].isGeoBoolean()))
			{
				GeoElement[] ret = { kernel.TMean2Estimate(c.getLabel(),
						(GeoNumeric) arg[0], 
						(GeoNumeric) arg[1],
						(GeoNumeric) arg[2],
						(GeoNumeric) arg[3], 
						(GeoNumeric) arg[4],
						(GeoNumeric) arg[5],
						(GeoNumeric) arg[6],
						(GeoBoolean) arg[7]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else if (!ok[4])
				throw argErr(app, c.getName(), arg[4]);
			else if (!ok[5])
				throw argErr(app, c.getName(), arg[5]);
			else if (!ok[6])
				throw argErr(app, c.getName(), arg[6]);
			else
				throw argErr(app, c.getName(), arg[7]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
