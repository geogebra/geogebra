package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 * Integral[ <GeoFunction> ] Integral[ <GeoFunction>, <Number a>, <Number b> ]
 * Integral[ <GeoFunction f>, <GeoFunction g>, <Number a>, <Number b> ]
 */
class CmdIntegral extends CommandProcessorDesktop {

	
	// from GeoGebra 4.0, Integral has been split into Integral and IntegralBetween
	// old syntax and files will still work
	private String internalCommandName;

	/**
	 * Create new command processor
	 * @param between if true IntegralBetween instead of Integral is created
	 * @param kernel
	 *            kernel
	 */
	public CmdIntegral(Kernel kernel, boolean between) {
		super(kernel);
		internalCommandName = between ? "IntegralBetween" : "Integral";
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoFunctionable())) {
				GeoElement[] ret = { kernel.Integral(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(), null) };
				return ret;
			} else
				throw argErr(app, internalCommandName, arg[0]);

		case 2:
			// Integral[ f(x,y), x]
			arg = resArgsLocalNumVar(c, 1, 1);
			if (arg[0] instanceof CasEvaluableFunction && arg[1].isGeoNumeric()) {
				GeoElement[] ret = { kernel.Integral(c.getLabel(),
						(CasEvaluableFunction) arg[0], // function
						(GeoNumeric) arg[1]) }; // var
				return ret;
			} else
				throw argErr(app, internalCommandName, arg[0]);

		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { kernel.Integral(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, internalCommandName, arg[0]);
				else if (!ok[1])
					throw argErr(app, internalCommandName, arg[1]);
				else
					throw argErr(app, internalCommandName, arg[2]);
			}

		case 4:
			arg = resArgs(c);
			// difference of two functions
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isNumberValue() && !arg[3]
							.isBooleanValue()))) {
				GeoElement[] ret = { kernel.Integral(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						((GeoFunctionable) arg[1]).getGeoFunction(),
						(NumberValue) arg[2], (NumberValue) arg[3]) };
				return ret;

			}
			// single function integral with evaluate option
			else if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isGeoBoolean()))) {
				GeoElement[] ret = { kernel.Integral(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2],
						(GeoBoolean) arg[3]) };
				return ret;

			} else {
				if (!ok[0])
					throw argErr(app, internalCommandName, arg[0]);
				else if (!ok[1])
					throw argErr(app, internalCommandName, arg[1]);
				else if (!ok[2])
					throw argErr(app, internalCommandName, arg[2]);
				else
					throw argErr(app, internalCommandName, arg[3]);
			}

		case 5:
			arg = resArgs(c);
			// difference of two functions with evaluate option
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isNumberValue())
							&& (ok[4] = (arg[4].isGeoBoolean())))) {
				GeoElement[] ret = { kernel.Integral(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						((GeoFunctionable) arg[1]).getGeoFunction(),
						(NumberValue) arg[2], (NumberValue) arg[3],
						(GeoBoolean) arg[4]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, internalCommandName, arg[0]);
				else if (!ok[1])
					throw argErr(app, internalCommandName, arg[1]);
				else if (!ok[2])
					throw argErr(app, internalCommandName, arg[2]);
				else if (!ok[3])
					throw argErr(app, internalCommandName, arg[3]);
				else
					throw argErr(app, internalCommandName, arg[4]);
			}

		default:
			throw argNumErr(app, internalCommandName, n);
		}
	}
}
