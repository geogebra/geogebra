package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.cas.AlgoDerivative;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * NDerivative[ &lt;GeoFunction> ]
 * 
 * NDerivative[ &lt;GeoFunctionNVar>, &lt;var> ]
 * 
 * NDerivative[ &lt;GeoCurveCartesian> ]
 */
public class CmdNDerivative extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdNDerivative(Kernel kernel) {
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
			if (arg[0] instanceof CasEvaluableFunction) {
				CasEvaluableFunction f = (CasEvaluableFunction) arg[0];
				if (label == null) {
					label = CmdDerivative.getDerivLabel(f.toGeoElement(), 1);
				}
				GeoElement[] ret = { nDerivative(label, f, null, null) };
				return ret;
			}
			throw argErr(app, c, arg[0]);

		default:
			throw argNumErr(c);
		}

	}

	/**
	 * Computes n-th derivative of f
	 * 
	 * @param label
	 *            label for output
	 * @param f
	 *            function
	 * @param var
	 *            variable
	 * @param n
	 *            derivative degree
	 * @return derivaive
	 */
	public GeoElement nDerivative(String label, CasEvaluableFunction f,
			GeoNumeric var, GeoNumberValue n) {
		AlgoDerivative algo = new AlgoDerivative(cons, label, f, var, n, true,
				new EvalInfo(false));
		return algo.getResult();
	}

}
