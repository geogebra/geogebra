package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoTurningPointPolynomial;
import org.geogebra.common.kernel.algos.AlgoTurningPointPolynomialInterval;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;

/**
 * TurningPoint[ <GeoFunction> ]
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
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoFunctionable()) {

				GeoFunction f = ((GeoFunctionable) arg[0]).getGeoFunction();

				// special case for If
				// non-polynomial -> undefined
				ExpressionNode exp = f.getFunctionExpression();
				if (exp.getOperation().equals(Operation.IF)) {

					AlgoTurningPointPolynomialInterval algo = new AlgoTurningPointPolynomialInterval(
							cons, c.getLabels(), f);
					GeoPoint[] g = algo.getRootPoints();
					return g;

				}

				AlgoTurningPointPolynomial algo = new AlgoTurningPointPolynomial(
						cons, c.getLabels(), f);

				return algo.getRootPoints();
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
