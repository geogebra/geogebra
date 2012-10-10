package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoRootInterval;
import geogebra.common.kernel.algos.AlgoRootNewton;
import geogebra.common.kernel.algos.AlgoRootsPolynomial;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;

/**
 * Root[ <GeoFunction> ] Root[ <GeoFunction> , <Number> ] Root[ <GeoFunction> ,
 * <Number> , <Number> ]
 */
public class CmdRoot extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRoot(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		// roots of polynomial
		case 1:
			arg = resArgs(c);
			if ((arg[0].isGeoFunctionable())){				
				GeoFunction gf = ((GeoFunctionable) arg[0])
						.getGeoFunction();
				// allow functions that can be simplified to factors of polynomials
				if (!gf.isPolynomialFunction(true))
					throw argErr(app, c.getName(), arg[0]);
				return Root(c.getLabels(), gf);
			}
			throw argErr(app, c.getName(), arg[0]);

			// root with start value
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoFunctionable())
					&& (ok[1] = (arg[1].isNumberValue()))) {
				
				AlgoRootNewton algo = new AlgoRootNewton(cons, c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1]);

				GeoElement[] ret = { algo.getRootPoint() };
				return ret;
			} 
			throw argErr(app, c.getName(), getBadArg(ok,arg));
			

			// root in interval
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				
				AlgoRootInterval algo = new AlgoRootInterval(cons, c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2]);

				GeoElement[] ret = { algo.getRootPoint() };
				return ret;
			} 
				throw argErr(app, c.getName(), getBadArg(ok,arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * all Roots of polynomial f (works only for polynomials and functions that
	 * can be simplified to factors of polynomials, e.g. sqrt(x) to x)
	 */
	final private GeoPoint[] Root(String[] labels, GeoFunction f) {
		AlgoRootsPolynomial algo = new AlgoRootsPolynomial(cons, labels, f);
		GeoPoint[] g = algo.getRootPoints();
		return g;
	}

}
