package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 * BarChart[ <Number>, <Number>, <List> ]
 */
public class CmdBarChart extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdBarChart(Kernel kernel) {
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
					&& (ok[1] = (arg[1].isGeoNumeric()))) {
				GeoElement[] ret = { kernelA.BarChart(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))) {
				GeoElement[] ret = { kernelA.BarChart(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]) };
				return ret;
			} else
				throw argErr(app, c.getName(), null);
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isGeoList()))) {
				GeoElement[] ret = { kernelA.BarChart(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(GeoList) arg[2]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { kernelA.BarChart(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1],
						(NumberValue) arg[2]) };
				return ret;
			} else
				throw argErr(app, c.getName(), null);
		case 6:
			// create local variable at position 3 and resolve arguments
			arg = resArgsLocalNumVar(c, 3, 4);
			if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& ((ok[2] = arg[2].isGeoElement()))
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isNumberValue())
					&& (ok[5] = arg[5].isNumberValue())) {
				GeoElement[] ret = { kernelA.BarChart(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1], arg[2],
						(GeoNumeric) arg[3], (NumberValue) arg[4],
						(NumberValue) arg[5], null) };
				return ret;
			}
			throw argErr(app, c.getName(), null);

		case 7:
			// create local variable at position 3 and resolve arguments
			arg = resArgsLocalNumVar(c, 3, 4);
			if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& ((ok[2] = arg[2].isGeoElement()))
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isNumberValue())
					&& (ok[5] = arg[5].isNumberValue())
					&& (ok[6] = arg[6].isNumberValue())) {
				GeoElement[] ret = { kernelA.BarChart(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1], arg[2],
						(GeoNumeric) arg[3], (NumberValue) arg[4],
						(NumberValue) arg[5], (NumberValue) arg[6]) };
				return ret;
			}
			throw argErr(app, c.getName(), null);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
