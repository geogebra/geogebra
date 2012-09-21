package geogebra.common.kernel.cas;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoIntegralFunctions;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 * Integral[ <GeoFunction> ] Integral[ <GeoFunction>, <Number a>, <Number b> ]
 * Integral[ <GeoFunction f>, <GeoFunction g>, <Number a>, <Number b> ]
 */
public class CmdIntegral extends CommandProcessor {

	
	// from GeoGebra 4.0, Integral has been split into Integral and IntegralBetween
	// old syntax and files will still work
	private String internalCommandName;

	/**
	 * Create new command processor
	 * @param command IntegralBetween, Integral or NIntegral
	 * @param kernel
	 *            kernel
	 */
	public CmdIntegral(Kernel kernel, Commands command) {
		super(kernel);
		internalCommandName = command.name();
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoFunctionable()) {
				GeoElement[] ret = { Integral(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(), null) };
				return ret;
			} 
			throw argErr(app, internalCommandName, arg[0]);

		case 2:
			// Integral[ f(x,y), x]
			arg = resArgsLocalNumVar(c, 1, 1);
			if ((ok[0]=arg[0] instanceof CasEvaluableFunction) && (ok[1]=arg[1].isGeoNumeric())) {
				GeoElement[] ret = { Integral(c.getLabel(),
						(CasEvaluableFunction) arg[0], // function
						(GeoNumeric) arg[1]) }; // var
				return ret;
			} 
			throw argErr(app, internalCommandName, getBadArg(ok,arg));

		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				
				AlgoIntegralDefinite algo = new AlgoIntegralDefinite(cons, c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2],!"NIntegral".equals(internalCommandName));

				GeoElement[] ret = { algo.getIntegral() };
				return ret;
			}
			throw argErr(app, internalCommandName, getBadArg(ok,arg));
			
		case 4:
			arg = resArgs(c);
			// difference of two functions
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isNumberValue() && !arg[3]
							.isBooleanValue()))) {
				
				AlgoIntegralFunctions algo = new AlgoIntegralFunctions(cons, c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						((GeoFunctionable) arg[1]).getGeoFunction(),
						(NumberValue) arg[2], (NumberValue) arg[3]);

				GeoElement[] ret = { algo.getIntegral() };
				return ret;

			}
			// single function integral with evaluate option
			else if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isGeoBoolean()))) {
				
				AlgoIntegralDefinite algo = new AlgoIntegralDefinite(cons, c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2],
						(GeoBoolean) arg[3]);

				GeoElement[] ret = { algo.getIntegral() };
				return ret;

			} else {
				throw argErr(app, internalCommandName, getBadArg(ok,arg));
			}

		case 5:
			arg = resArgs(c);
			// difference of two functions with evaluate option
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isNumberValue())
							&& (ok[4] = (arg[4].isGeoBoolean())))) {
				
				AlgoIntegralFunctions algo = new AlgoIntegralFunctions(cons, c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						((GeoFunctionable) arg[1]).getGeoFunction(),
						(NumberValue) arg[2], (NumberValue) arg[3],
						(GeoBoolean) arg[4]);
				
				GeoElement[] ret = { algo.getIntegral() };
				return ret;
			}
			throw argErr(app, internalCommandName, getBadArg(ok,arg));
		default:
			throw argNumErr(app, internalCommandName, n);
		}
	}
	

	/**
	 * Integral of function f
	 */
	final public GeoElement Integral(String label, CasEvaluableFunction f,
			GeoNumeric var) {
		AlgoIntegral algo = new AlgoIntegral(cons, label, f, var);
		return algo.getResult();
	}
}
