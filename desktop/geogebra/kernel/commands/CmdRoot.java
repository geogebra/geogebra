package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunctionable;
import geogebra.main.MyError;

/**
 * Root[ <GeoFunction> ] Root[ <GeoFunction> , <Number> ] Root[ <GeoFunction> ,
 * <Number> , <Number> ]
 */
class CmdRoot extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRoot(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		// roots of polynomial
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoFunctionable()))
				return kernel.Root(c.getLabels(), ((GeoFunctionable) arg[0])
						.getGeoFunction());
			else
				throw argErr(app, "Root", arg[0]);

			// root with start value
		case 2:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoFunctionable())
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernel.Root(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Root", arg[0]);
				else
					throw argErr(app, "Root", arg[1]);
			}

			// root in interval
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { kernel.Root(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Root", arg[0]);
				else if (!ok[1])
					throw argErr(app, "Root", arg[1]);
				else
					throw argErr(app, "Root", arg[2]);
			}

		default:
			throw argNumErr(app, "Root", n);
		}
	}
}
