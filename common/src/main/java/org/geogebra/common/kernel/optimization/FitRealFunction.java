package org.geogebra.common.kernel.optimization;

import java.util.Set;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;

/*
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by
 the Free Software Foundation.

 */

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
 *       FitRealFunction(Function)                Makes a copy of Function with gliders
 *                                                replaced by mydouble parameters
 *       value(double,double[])                    Evaluates for x and pars[]
 *       gradient(double,double[])                Evaluates a gradient for x and pars[] numerically
 *   
 *   For AlgoFitNL:
 *   
 *       getNumberOfParameters()             Get number of gliders/parameters found and changed
 *       getStartParameters()                Get array of startvalues for parameters.
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
	private GeoElement[] gliders = null; // Pointers to gliders, need for new
										// startvalues
	private Function newf = null;
	private double lastvalue = 0.0d;
	private MyDouble[] mydoubles = null;
	private boolean parametersOK = true;

	// / --- Interface --- ///

	/** Probably not needed? */
	public FitRealFunction() {
	}

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
	 * @return functionvalue
	 */
	@Override
	public final double value(double x, double... pars) {
		for (int i = 0; i < numberOfParameters; i++) {
			mydoubles[i].set(pars[i]);
			// mydoubles[i].setLabel("p_{"+i+"}");
		} // for all parameter
		lastvalue = newf.value(x);
		return lastvalue;
	}

	/**
	 * Returns array of partial derivatives with respect to parameters.
	 * 
	 * Derivatives are approximated numerically, the step for given slider is
	 * 
	 * max(1E-5, 0.01 * slider step)
	 * 
	 * @param x
	 *            double variable
	 * @param pars
	 *            double[] parameters
	 */
	@Override
	public final double[] gradient(double x, double... pars) {
		double oldf, newf1;
		double deltap = 1.0E-5; // 1E-10 and 1E-15 is far too small, keep E-5
								// until search algo is made
		double[] gradient = new double[numberOfParameters];
		for (int i = 0; i < numberOfParameters; i++) {
			oldf = value(x, pars);
			double old = pars[i];
			double deltaI = deltap;
			if (gliders[i] instanceof GeoNumeric) {
				double step = gliders[i].getAnimationStep();
				if (step > 1E-13) {
					deltaI = Math.min(step * 0.01, deltap);
				}
			}
			pars[i] += deltaI;
			newf1 = value(x, pars);
			gradient[i] = (newf1 - oldf) / deltap;
			pars[i] = old;
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
		FunctionVariable fvar = f.getFunctionVariable();

		Set<GeoElement> hash = f
				.getVariables(SymbolicMode.NONE); // Get
																		// a,b,c,...
																// to array
		if (hash.isEmpty()) {
			// throw (new Exception("No gliders/parameters in
			// fit-function..."));
			this.parametersOK = false;
		} else {
			gliders = hash.toArray(new GeoElement[0]);
		} // if no gliders

		numberOfParameters = gliders.length;

		mydoubles = new MyDouble[numberOfParameters]; // Make my own parameters
		double temp;
		for (int i = 0; i < numberOfParameters; i++) {
			temp = ((NumberValue) gliders[i]).getDouble();
			mydoubles[i] = new MyDouble(kernel);
			mydoubles[i].set(temp); // Set mydoubles to start values from a,b,c
		} // for all parameters

		ExpressionNode node = f.getExpression();

		ExpressionNode enf = node.deepCopy(kernel); // Make new
													// tree
													// for
													// new
													// function
		// ExpressionNode enf=new ExpressionNode(kernel,evf);

		for (int i = 0; i < numberOfParameters; i++) {
			enf = enf
					.replace(gliders[i],
							mydoubles[i]
									.evaluate(StringTemplate.defaultTemplate))
					.wrap();
		}
		enf.resolveVariables(new EvalInfo(false));
		// should we dispose this??? if(this.newf!=null)
		this.newf = new Function(enf, fvar);

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
		double[] startvalues = new double[numberOfParameters];
		for (int i = 0; i < numberOfParameters; i++) {
			startvalues[i] = ((NumberValue) gliders[i]).getDouble(); // Only
																		// first
																		// time:
																		// mydoubles[i].getDouble();
		} // for all parameters
		return startvalues;
	}

	/**
	 * @param x
	 *            argument value
	 * @return f(x)
	 */
	public final double value(double x) {
		return newf.value(x);
	}

	/**
	 * @return wrapped function
	 */
	public final Function getFunction() {
		return newf;
	}

	/**
	 * @return coefficients
	 */
	public MyDouble[] getCoeffs() {
		return mydoubles;
	}

	/**
	 * @return whether parameters are OK
	 */
	public boolean isParametersOK() {
		return parametersOK;
	}

}