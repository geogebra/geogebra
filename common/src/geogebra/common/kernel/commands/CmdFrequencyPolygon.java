package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 * Histogram[ <List>, <List> ]
 */
public class CmdFrequencyPolygon extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFrequencyPolygon(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))) {
				GeoElement[] ret = { kernelA.FrequencyPolygon(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]) };
				return ret;
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);


		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoBoolean()))) {
				GeoElement[] ret = { kernelA.FrequencyPolygon(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1],
						(GeoBoolean) arg[2], null) };
				return ret;
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);


		case 4:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoBoolean()))
					&& (ok[3] = (arg[3].isGeoNumeric()))) {
				GeoElement[] ret = { kernelA.FrequencyPolygon(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1],
						(GeoBoolean) arg[2], (GeoNumeric) arg[3]) };
				return ret;
			}
			
			else if ((ok[0] = (arg[0].isGeoBoolean()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoList()))
					&& (ok[3] = (arg[3].isGeoBoolean()))) {
				GeoElement[] ret = { kernelA.FrequencyPolygon(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1],
						(GeoList) arg[2], (GeoBoolean) arg[3]) };
				return ret;
			}
			
			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		case 5:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoBoolean()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoList()))
					&& (ok[3] = (arg[3].isGeoBoolean()))
					&& (ok[4] = (arg[4].isGeoNumeric()))) {
				GeoElement[] ret = { kernelA.FrequencyPolygon(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1],
						(GeoList) arg[2], (GeoBoolean) arg[3], (GeoNumeric) arg[4]) };
				return ret;
			}
			
			else if ((ok[0] = (arg[0].isGeoBoolean()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoList()))
					&& (ok[3] = (arg[3].isGeoList()))
					&& (ok[4] = (arg[4].isGeoBoolean()))) {
				GeoElement[] ret = { kernelA.FrequencyPolygon(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1],
						(GeoList) arg[2], (GeoList) arg[3], (GeoBoolean) arg[4]) };
				return ret;
				
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else
				throw argErr(app, c.getName(), arg[4]);

			
		default:
			throw argNumErr(app, c.getName(), n);
		}

	}
}
