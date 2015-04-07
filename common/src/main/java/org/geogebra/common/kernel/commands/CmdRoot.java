package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoRootInterval;
import org.geogebra.common.kernel.algos.AlgoRootNewton;
import org.geogebra.common.kernel.algos.AlgoRootsPolynomial;
import org.geogebra.common.kernel.algos.AlgoRootsPolynomialInterval;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;

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
			if ((arg[0].isGeoFunctionable())) {
				GeoFunction gf = ((GeoFunctionable) arg[0]).getGeoFunction();
				return Root(c, gf);
			}
			throw argErr(app, c.getName(), arg[0]);

			// root with start value
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoFunctionable())
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))) {

				AlgoRootNewton algo = new AlgoRootNewton(cons, c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(GeoNumberValue) arg[1]);

				GeoElement[] ret = { algo.getRootPoint() };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

			// root in interval
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))
					&& (ok[2] = (arg[2] instanceof GeoNumberValue))) {

				AlgoRootInterval algo = new AlgoRootInterval(cons,
						c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2]);

				GeoElement[] ret = { algo.getRootPoint() };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * all Roots of polynomial f (works only for polynomials and functions that
	 * can be simplified to factors of polynomials, e.g. sqrt(x) to x)
	 */
	final private GeoPoint[] Root(Command c, GeoFunction f) {

		// special case for If
		// non-polynomial -> undefined
		ExpressionNode exp = f.getFunctionExpression();
		if (exp.getOperation().equals(Operation.IF)) {

			AlgoRootsPolynomialInterval algo = new AlgoRootsPolynomialInterval(
					cons, c.getLabels(), f);
			GeoPoint[] g = algo.getRootPoints();
			return g;

		}

		// allow functions that can be simplified to factors of polynomials
		if (!f.getConstruction().isFileLoading()
				&& !f.isPolynomialFunction(true) && f.isDefined())
			throw argErr(app, c.getName(), f);

		AlgoRootsPolynomial algo = new AlgoRootsPolynomial(cons, c.getLabels(),
				f);
		GeoPoint[] g = algo.getRootPoints();
		return g;
	}

}
