package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoPolynomialFromCoordinates;
import org.geogebra.common.kernel.algos.AlgoPolynomialFromFunction;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * Polynomial[ <GeoFunction> ]
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
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if ((arg[0].isGeoFunctionable())) {

				AlgoPolynomialFromFunction algo = new AlgoPolynomialFromFunction(
						cons, c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction());

				GeoElement[] ret = { algo.getPolynomial() };
				return ret;
			}
			// Michael Borcherds 2008-01-22 BEGIN
			// PolynomialFromCoordinates
			else if ((arg[0].isGeoList())) {
				GeoElement[] ret = { PolynomialFunction(c.getLabel(),
						((GeoList) arg[0])) };
				return ret;
			}
			// Michael Borcherds 2008-01-22 END
			else
				throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			// Markus Hohenwarter 2008-01-26 BEGIN
			// try to create list of points
			GeoList list = wrapInList(kernelA, arg, arg.length, GeoClass.POINT);
			if (list != null) {
				GeoElement[] ret = { PolynomialFunction(c.getLabel(), list) };
				return ret;
			}
			// Markus Hohenwarter 2008-01-26 END
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * Fits a polynomial exactly to a list of coordinates Michael Borcherds
	 * 2008-01-22
	 */
	final private GeoFunction PolynomialFunction(String label, GeoList list) {
		AlgoPolynomialFromCoordinates algo = new AlgoPolynomialFromCoordinates(
				cons, label, list);
		return algo.getPolynomial();
	}
}
