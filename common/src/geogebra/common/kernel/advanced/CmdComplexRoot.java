package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoComplexRootsPolynomial;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.main.MyError;

/**
 * ComplexRoot[ <GeoFunction> ]
 */
public class CmdComplexRoot extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdComplexRoot(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		// roots of polynomial
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoFunctionable()) {
				
				GeoFunction f = ((GeoFunctionable) arg[0]).getGeoFunction();
				
				// allow functions that can be simplified to factors of polynomials
				if (!f.isPolynomialFunction(true))
					return null;

				AlgoComplexRootsPolynomial algo = new AlgoComplexRootsPolynomial(cons,
						c.getLabels(), f);

				return algo.getRootPoints();
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
