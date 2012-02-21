package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;

/**
 * BarChart[ <Number>, <Number>, <List> ]
 */
public class CmdBoxPlot extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdBoxPlot(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isGeoList()))) {
				GeoElement[] ret = { kernelA.BoxPlot(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(GeoList) arg[2]) };
				return ret;
			} else
				throw argErr(app, c.getName(), null);

		case 7:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isNumberValue())
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isNumberValue()))
					&& (ok[4] = (arg[4].isNumberValue()))
					&& (ok[5] = (arg[5].isNumberValue()))
					&& (ok[6] = (arg[6].isNumberValue())))) {

				GeoElement[] ret = { kernelA.BoxPlot(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2], (NumberValue) arg[3],
						(NumberValue) arg[4], (NumberValue) arg[5],
						(NumberValue) arg[6]) };
				return ret;

			}
			// else continue:

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
