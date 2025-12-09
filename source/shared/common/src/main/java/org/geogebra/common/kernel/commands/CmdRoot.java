/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.commands;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoRootInterval;
import org.geogebra.common.kernel.algos.AlgoRootNewton;
import org.geogebra.common.kernel.algos.AlgoRoots;
import org.geogebra.common.kernel.algos.AlgoRootsPolynomial;
import org.geogebra.common.kernel.algos.AlgoRootsPolynomialInterval;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;

/**
 * Root[ &lt;GeoFunction&gt; ] Root[ &lt;GeoFunction&gt; , &lt;Number&gt; ] Root[
 * &lt;GeoFunction&gt; , &lt;Number&gt; , &lt;Number&gt; ]
 */
public class CmdRoot extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRoot(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		// roots of polynomial
		case 1:
			arg = resArgs(c, info);
			if (arg[0].isRealValuedFunction()) {
				GeoFunctionable gf = (GeoFunctionable) arg[0];
				return root(c, gf);
			}
			throw argErr(c, arg[0]);

			// root with start value
		case 2:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isRealValuedFunction())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {

				AlgoRootNewton algo = new AlgoRootNewton(cons, c.getLabel(),
						(GeoFunctionable) arg[0],
						(GeoNumberValue) arg[1]);

				GeoElement[] ret = { algo.getRootPoint() };
				return ret;
			}
			if (arg[0].isGeoFunction() && arg[1].isGeoFunction()) {
				c.setName("Intersect"); // bug in some GGB versions saving
										// Intersect(f,g) as Root(f,g)
				return kernel.getAlgebraProcessor().processCommand(c, info);
			}
			throw argErr(c, getBadArg(ok, arg));

			// root in interval
		case 3:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isRealValuedFunction())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {

				AlgoRootInterval algo = new AlgoRootInterval(cons, c.getLabel(),
						(GeoFunctionable) arg[0],
						(GeoNumberValue) arg[1], (GeoNumberValue) arg[2]);

				GeoElement[] ret = { algo.getRootPoint() };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * all Roots of polynomial f (works only for polynomials and functions that
	 * can be simplified to factors of polynomials, e.g. sqrt(x) to x)
	 */
	final private GeoPoint[] root(Command c, GeoFunctionable f) {

		// special case for If
		// non-polynomial -> undefined
		Function fun = f.getFunction();
		ExpressionNode exp = fun.getFunctionExpression();
		if (exp.getOperation().isIf()) {

			AlgoRootsPolynomialInterval algo = new AlgoRootsPolynomialInterval(
					cons, c.getLabels(), f);
			GeoPoint[] g = algo.getRootPoints();
			return g;

		}

		// allow functions that can be simplified to factors of polynomials
		if (!f.getConstruction().isFileLoading()
				&& !f.isPolynomialFunction(true) && f.isDefined()) {
			return nonPolyRoots(c, kernel, f);
		}

		AlgoRootsPolynomial algo = new AlgoRootsPolynomial(cons, c.getLabels(),
				f, true);
		GeoPoint[] g = algo.getRootPoints();
		return g;
	}

	/**
	 * @param c
	 *            command
	 * @param kernel
	 *            kernel
	 * @param geoElement
	 *            function
	 * @return root points
	 */
	public static GeoPoint[] nonPolyRoots(Command c, Kernel kernel,
			GeoFunctionable geoElement) {
		EuclidianViewInterfaceCommon view = kernel.getApplication()
				.getActiveEuclidianView();

		AlgoRoots algo = new AlgoRoots(kernel.getConstruction(), c.getLabels(),
				geoElement, view);
		return algo.getRootPoints();

	}

}
