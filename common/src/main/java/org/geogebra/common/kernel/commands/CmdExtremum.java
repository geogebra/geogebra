package org.geogebra.common.kernel.commands;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoExtremumMulti;
import org.geogebra.common.kernel.algos.AlgoExtremumPolynomial;
import org.geogebra.common.kernel.algos.AlgoExtremumPolynomialInterval;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;

/**
 * Extremum[ &lt;GeoFunction> ]
 */
public class CmdExtremum extends CommandProcessor {

	/**
	 * Create new command processor
	 *
	 * @param kernel
	 *            kernel
	 */
	public CmdExtremum(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			ok[0] = arg[0].isRealValuedFunction();
			if (ok[0]) {
				return extremum(c, (GeoFunctionable) arg[0]);
			}
			throw argErr(c, arg[0]);
		case 3: // Extremum[f,start-x,end-x]
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isRealValuedFunction()))
					&& (ok[1] = (arg[1] instanceof GeoNumberValue))
					&& (ok[2] = (arg[2] instanceof GeoNumberValue))

			) {

				AlgoExtremumMulti algo = new AlgoExtremumMulti(cons,
						c.getLabels(),
						(GeoFunctionable) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2], true);
				return algo.getExtremumPoints();
			}

			throw argErr(c, getBadArg(ok, arg));
		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @return - all Extrema of function f (for polynomial functions) - all
	 *         Extrema currently visible (for non-polynomial functions)
	 */
	final private GeoPoint[] extremum(Command c, GeoFunctionable gf) {
		Function f = gf.getFunction();
		// special case for If
		// non-polynomial -> undefined
		ExpressionNode exp = f.getFunctionExpression();
		if (exp.getOperation().isIf()) {

			AlgoExtremumPolynomialInterval algo = new AlgoExtremumPolynomialInterval(
					cons, c.getLabels(), gf);
			GeoPoint[] g = algo.getRootPoints();
			return g;

		}

		// check if this is a polynomial at the moment
		// uninitialized CAS algo may return non-polynomial
		// if (!kernelA.getConstruction().isFileLoading() && f.isDefined()
		// && !f.isPolynomialFunction(true))
		// return null;
		PolyFunction poly = f.expandToPolyFunction(
				f.getFunctionExpression(), false,
				true);
		if (!gf.isPolynomialFunction(true)
				|| (poly != null && poly.isMaxDegreeReached())) {
			EuclidianViewInterfaceCommon view = this.kernel.getApplication()
					.getActiveEuclidianView();
			AlgoExtremumMulti algo = new AlgoExtremumMulti(cons, c.getLabels(),
					gf, view);
			return algo.getExtremumPoints();
		}

		AlgoExtremumPolynomial algo = new AlgoExtremumPolynomial(cons,
				c.getLabels(), gf, true);
		GeoPoint[] g = algo.getRootPoints();
		return g;
	}
}
