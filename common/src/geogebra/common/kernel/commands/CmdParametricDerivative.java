package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.cas.AlgoParametricDerivative;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**Derivative[ <GeoFunction> ] Derivative[ <GeoFunctionNVar>, <var> ]
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
			if (arg[0].isGeoCurveCartesian()) {
				GeoCurveCartesian f = (GeoCurveCartesian) arg[0];
				
				AlgoParametricDerivative algo = new AlgoParametricDerivative(cons, label, f);

				GeoElement[] ret = { algo.getParametricDerivative() };
				return ret;
			}
			throw argErr(app, c.getName(), arg[0]);


		default:
			throw argNumErr(app, c.getName(), n);
		}

	}

}
