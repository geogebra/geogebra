package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoPolynomialFromCoordinates;
import org.geogebra.common.kernel.algos.AlgoPolynomialFromFunction;
import org.geogebra.common.kernel.algos.AlgoPolynomialFromFunctionNVar;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * Polynomial[ &lt;GeoFunction&gt; ]
 */
public class CmdPolynomial extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPolynomial(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 1:
			if (arg[0].isRealValuedFunction()) {

				AlgoPolynomialFromFunction algo = new AlgoPolynomialFromFunction(
						cons, c.getLabel(),
						(GeoFunctionable) arg[0]);

				GeoElement[] ret = { algo.getPolynomial() };
				return ret;
			} else if (arg[0].isGeoFunctionNVar()) {
				AlgoPolynomialFromFunctionNVar algo = new AlgoPolynomialFromFunctionNVar(
						cons, c.getLabel(),
						(GeoFunctionNVar) arg[0]);
				GeoElement[] ret = { algo.getPolynomial() };
				return ret;
			}
			// PolynomialFromCoordinates
			else if (arg[0].isGeoList()) {
				GeoElement[] ret = {
						polynomialFunction(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			} else {
				throw argErr(c, arg[0]);
			}

			// more than one argument
		default:
			// try to create list of points
			GeoList list = wrapInList(kernel, arg, arg.length, GeoClass.POINT);
			if (list != null) {
				GeoElement[] ret = { polynomialFunction(c.getLabel(), list) };
				return ret;
			}
			throw argNumErr(c);
		}
	}

	/**
	 * Fits a polynomial exactly to a list of coordinates Michael Borcherds
	 * 2008-01-22
	 */
	final private GeoFunction polynomialFunction(String label, GeoList list) {
		AlgoPolynomialFromCoordinates algo = new AlgoPolynomialFromCoordinates(
				cons, label, list);
		return algo.getPolynomial();
	}
}
