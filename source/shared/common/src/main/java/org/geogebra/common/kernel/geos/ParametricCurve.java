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

package org.geogebra.common.kernel.geos;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;

/**
 * Curve in parametric form (f(t),g(t))
 */
public interface ParametricCurve extends Traceable, Path, CurveEvaluable, VarString {
	@Override
	double getMinParameter();

	@Override
	double getMaxParameter();

	/**
	 * @return x-coord as function of parameter
	 */
	UnivariateFunction getUnivariateFunctionX();

	/**
	 * @return y-coord as function of parameter
	 */
	UnivariateFunction getUnivariateFunctionY();

	/**
	 * Evaluates the curve for given parameter value
	 * 
	 * @param t
	 *            parameter value
	 * @param out
	 *            array to store the result
	 */
	@Override
	void evaluateCurve(double t, double[] out);

	/**
	 * Evaluates the curve for given parameter value
	 * 
	 * @param t
	 *            parameter value
	 * @return result as GeoVec2D
	 */
	ExpressionValue evaluateCurve(double t);

	/**
	 * @param t
	 *            parameter value
	 * @return curvature at given parameter
	 */
	double evaluateCurvature(double t);

	/**
	 * @return true when this is function of x
	 */
	@Override
	boolean isFunctionInX();

	/**
	 * @param i
	 *            coordinate
	 * @return function
	 */
	Function getFun(int i);

	/**
	 * @return array of function variables
	 */
	@Override
	FunctionVariable[] getFunctionVariables();

}
