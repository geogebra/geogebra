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

package org.geogebra.common.kernel.optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * <h3>FitRealFunction</h3>
 * 
 * <pre>
 *   Class with FitRealFunction which will be used when Fit[&lt;List&gt;,&lt;Function&gt;] does
 *   nonlinear curve-fitting on a copy of &lt;Function&gt; where gliders a,b,c,...
 *   are used as parameters.
 *   
 *   Implements:
 * 
 *         org.apache.commons.math.optimization.fitting.ParametricUnivariateFunction
 *         which can be given to org.apache....fitting.CurveFitter which
 *         does the rest of the job.
 *   
 *   Interface:
 *   
 *       FitRealFunction(Function)                Makes a copy of Function with sliders
 *                                                replaced by MyDouble parameters
 *       value(double,double[])                    Evaluates for x and pars[]
 *       gradient(double,double[])                Evaluates a gradient for x and pars[] numerically
 *   
 *   For AlgoFitNL:
 *   
 *       getNumberOfParameters()             Get number of gliders/parameters found and changed
 *       getStartParameters()                Get array of start values for parameters.
 *       getGeoFunction(double[])            Get FitFunction as GeoFunction with parameters replaced
 *   
 *   For later extensions and external use:
 *   
 *       evaluate(double,double[])                As value(...), perhaps implementing
 *                                               other interfaces later?
 *       evaluate(double)                        As an ordinary function
 *       evaluate()                                Last value
 *       getFunction(double[])                    Get as Function with parameters replaced
 *
 *       ToDo:    The gradient could be more sophisticated, but the Apache lib is quite robust :-)
 *                Some tuning of numerical precision both here and in the setup of LM-optimizer
 *
 *                Should probably make an abstract, and make this a subclass,
 *                will do if the need arises.
 * </pre>
 * 
 * @author Hans-Petter Ulven
 * @version 15.03.2011
 */
public class FitRealFunction implements ParametricUnivariateFunction {

	// / --- Properties --- ///
	private Kernel kernel = null;
	private int numberOfParameters = 0;
	private GeoElement[] sliders = null; // Pointers to sliders, need for new
										// start values
	private Function bestFitFunction = null;
	private final List<MyDouble> parameterValues = new ArrayList<>();
	private boolean parametersOK = true;

	// / --- Interface --- ///

	/**
	 * Main constructor
	 * 
	 * @param f
	 *            Function to be copied and manipulated
	 */
	public FitRealFunction(Function f) {
		super();
		setFunction(f);
	}

	/**
	 * Implementing org.apache...fitting.ParametricUnivariateFunction
	 * 
	 * @param x
	 *            double variable
	 * @param pars
	 *            double[] parameters
	 * @return function value
	 */
	@Override
	public final double value(double x, double... pars) {
		for (int i = 0; i < numberOfParameters; i++) {
			parameterValues.get(i).set(pars[i]);
		}
		return bestFitFunction.value(x);
	}

	/**
	 * Returns array of partial derivatives with respect to parameters.
	 * <p>
	 * Derivatives are approximated numerically, the step for given slider is
	 * max(1E-5, 0.01 * slider step)
	 * </p>
	 * @param x
	 *            double variable
	 * @param parameters
	 *            double[] parameters
	 */
	@Override
	public final double[] gradient(double x, double... parameters) {
		double oldValue, newValue;
		double deltaP = 1.0E-5; // 1E-10 and 1E-15 is far too small, keep E-5
								// until search algo is made
		double[] gradient = new double[numberOfParameters];
		for (int i = 0; i < numberOfParameters; i++) {
			oldValue = value(x, parameters);
			double old = parameters[i];
			double deltaI = deltaP;
			if (sliders[i] instanceof GeoNumeric) {
				double step = sliders[i].getAnimationStep();
				if (step > 1E-13) {
					deltaI = Math.min(step * 0.01, deltaP);
				}
			}
			parameters[i] += deltaI;
			newValue = value(x, parameters);
			gradient[i] = (newValue - oldValue) / deltaP;
			parameters[i] = old;
		}
		return gradient;
	}

	/**
	 * Converts function to FitRealFunction
	 * 
	 * @param f
	 *            function depending on GeoNumeric parameters
	 */
	public void setFunction(Function f) {
		kernel = f.getKernel();

		ExpressionNode node = f.getExpression();
		ExpressionNode expressionWithConstants = node.deepCopy(kernel);
		Set<GeoNumeric> parameters = new LinkedHashSet<>();
		parameterValues.clear();
		HashMap<GeoNumeric, MyDouble> paramToValue = new HashMap<>();
		// traversing done in 2 steps here to maintain backward compatibility,
		// order of coefficients depends on HashMap sorting
		expressionWithConstants.traverse(val -> {
			if (val instanceof MyDouble && Double.isNaN(val.evaluateDouble())) {
				GeoNumeric adHocParam = new GeoNumeric(kernel.getConstruction());
				// value = 1 has a lower chance of degenerate function compared to value = 0
				adHocParam.setValue(1);
				MyDouble paramValue = new MyDouble(kernel);
				parameters.add(adHocParam);
				paramToValue.put(adHocParam, paramValue);
				return paramValue;
			} else if (val instanceof GeoNumeric
					&& ((GeoNumeric) val).isLabelSet()) {
				GeoNumeric numericVal = (GeoNumeric) val;
				if (parameters.add(numericVal)) {
					MyDouble paramValue = new MyDouble(kernel);
					parameters.add(numericVal);
					paramToValue.put(numericVal, paramValue);
					return paramValue;
				}
				return paramToValue.get(val);
			}
			return val;
		});
		if (parameters.isEmpty()) {
			this.parametersOK = false;
		} else {
			sliders = parameters.toArray(new GeoElement[0]);
		}
		for (GeoNumeric param: parameters) {
			parameterValues.add(paramToValue.get(param));
		}

		numberOfParameters = sliders.length;
		expressionWithConstants.resolveVariables(new EvalInfo(false));
		FunctionVariable functionVariable = f.getFunctionVariable();
		this.bestFitFunction = new Function(expressionWithConstants, functionVariable);
	}

	/**
	 * @return number of parameters
	 */
	public final int getNumberOfParameters() {
		return numberOfParameters;
	}

	/**
	 * @return initial value of all parameters
	 */
	public final double[] getStartValues() {
		double[] startValues = new double[numberOfParameters];
		for (int i = 0; i < numberOfParameters; i++) {
			startValues[i] = ((NumberValue) sliders[i]).getDouble();
		}
		return startValues;
	}

	/**
	 * @param x
	 *            argument value
	 * @return f(x)
	 */
	public final double value(double x) {
		return bestFitFunction.value(x);
	}

	/**
	 * @return wrapped function
	 */
	public final Function getFunction() {
		return bestFitFunction;
	}

	/**
	 * @return coefficients
	 */
	public List<MyDouble> getCoeffs() {
		return parameterValues;
	}

	/**
	 * @return whether parameters are OK
	 */
	public boolean isParametersOK() {
		return parametersOK;
	}

}