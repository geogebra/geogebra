package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * Derivative[ <GeoFunction> ] Derivative[ <GeoFunctionNVar>, <var> ]
 * ParametricDerivative[ <GeoCurveCartesian> ]
 */
public class CmdParametricDerivative extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdParametricDerivative(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		String label = c.getLabel();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			// use instanceof (2D only)
			if (arg[0] instanceof GeoCurveCartesian) {
				GeoCurveCartesian f = (GeoCurveCartesian) arg[0];

				AlgoParametricDerivative algo = new AlgoParametricDerivative(
						cons, label, f);

				GeoElement[] ret = { algo.getParametricDerivative() };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}

	}

}
