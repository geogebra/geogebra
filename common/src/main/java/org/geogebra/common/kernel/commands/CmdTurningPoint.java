package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoTurningPointPolyInterval;
import org.geogebra.common.kernel.algos.AlgoTurningPointPolynomial;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;

/**
 * InflectionPoint[ &lt;GeoFunction&gt; ]
 */
public class CmdTurningPoint extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTurningPoint(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isRealValuedFunction()) {

				GeoFunctionable f = (GeoFunctionable) arg[0];

				// special case for If
				// non-polynomial -> undefined
				ExpressionNode exp = f.getFunction()
						.getFunctionExpression();
				if (exp.getOperation().isIf()) {

					AlgoTurningPointPolyInterval algo = new AlgoTurningPointPolyInterval(
							cons, c.getLabels(), f);
					GeoPoint[] g = algo.getRootPoints();
					return g;
				}

				AlgoTurningPointPolynomial algo = new AlgoTurningPointPolynomial(
						cons, c.getLabels(), f);

				return algo.getRootPoints();
			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
