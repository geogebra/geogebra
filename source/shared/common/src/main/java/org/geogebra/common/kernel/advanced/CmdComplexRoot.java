package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.main.MyError;

/**
 * ComplexRoot[ &lt;GeoFunction&gt; ]
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
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		// roots of polynomial
		case 1:
			arg = resArgs(c, info);
			if (arg[0].isRealValuedFunction()) {

				GeoFunctionable f = (GeoFunctionable) arg[0];

				// allow functions that can be simplified to factors of
				// polynomials
				if (!f.getConstruction().isFileLoading()
						&& !f.isPolynomialFunction(true)) {
					return null;
				}

				AlgoComplexRootsPolynomial algo = new AlgoComplexRootsPolynomial(
						cons, c.getLabels(), f);

				return algo.getRootPoints();
			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
