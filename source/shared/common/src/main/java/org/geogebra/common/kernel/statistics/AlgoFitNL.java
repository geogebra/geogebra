package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.CurveFitter;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.optimization.FitRealFunction;
import org.geogebra.common.util.debug.Log;

/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

*/

/**
 * 
 * AlgoFitNL: (NL=NonLinear Curvefit) A general curvefit: Fit[&lt;List of
 * Points&gt;,&lt;Function&gt;] Example: f(x)=a+b/(x-c) L={A,B,...} g(x)=Fit[L,f] will
 * give a function g(x)=p1+p2/(x-p3) where p1, p2 and p3 are calculated to give
 * the least sum of squared errors.
 * 
 * The nonlinear curve-fitting is done with an iteration algorithm, which is
 * not guaranteed to work. The values of a, b and c are taken as starting points
 * for the iteration algorithm. If the iteration does not converge or the number
 * of iterations is getting to large, the result is undefined, a signal for the
 * user to try to adjust the starting point with the gliders a, b and c.
 * 
 * Uses Levenberg-Marquardt algorithm in org.apache.commons library
 * 
 * ToDo: The gradient in FitRealFunction could be more sophisticated, but the
 * Apache lib is quite robust :-) Some tuning of numerical precision both here
 * and in the setup of LM-optimizer
 * 
 * @author Hans-Petter Ulven
 * @version 2011-03-15
 */
@SuppressWarnings("deprecation")
public class AlgoFitNL extends AlgoElement implements FitAlgo {

	private GeoList pointlist; // input
	private GeoFunction inputfunction; // input
	private GeoFunction outputfunction; // output

	// variables:
	private int datasize = 0; // rows in M and Y
	private double[] xdata = null;
	private double[] ydata = null;
	private FitRealFunction prfunction = null; // function for Apache lib
	private LevenbergMarquardtOptimizer LMO = new LevenbergMarquardtOptimizer();
	private CurveFitter<ParametricUnivariateFunction> curvefitter = new CurveFitter<>(
			LMO);

	/**
	 * @param cons
	 *            construction
	 * @param pointlist
	 *            points
	 * @param inputfunction
	 *            function with parameters
	 */
	public AlgoFitNL(Construction cons, GeoList pointlist,
			GeoFunction inputfunction) {
		super(cons);

		this.pointlist = pointlist;
		this.inputfunction = inputfunction;
		outputfunction = new GeoFunction(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Fit;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = pointlist;
		input[1] = inputfunction;
		setOnlyOutput(outputfunction);
		setDependencies();
	}

	/**
	 * @return output function
	 */
	public GeoFunction getFitNL() {
		return outputfunction;
	}

	@Override
	public final void compute() {
		GeoElement geo1 = null;
		GeoElement geo2 = null;
		this.datasize = pointlist.size(); // Points in dataset

		if (!pointlist.isDefined() || !inputfunction.isDefined()
				|| (datasize < 1)) {
			// Perhaps a max restriction of functions and data?
			// Even if noone would try 500 datapoints and 100 functions...
			outputfunction.setUndefined();
			return;
		}
		// We are in business...
		// Best to also check:
		geo1 = pointlist.get(0);
		geo2 = inputfunction;
		if (!geo2.isGeoFunction() || !geo1.isGeoPoint()) {
			outputfunction.setUndefined();
			return;
		}

		try {
			// Get points as x[] and y[] from lists
			if (!makeDataArrays()) {
				outputfunction.setUndefined();
				return;
			}

			// / --- Solve :-) --- ///

			// prfunction makes itself a copy of inputfunction with
			// parameters instead of GeoNumerics
			prfunction = new FitRealFunction(inputfunction.getFunction());

			if (!prfunction.isParametersOK()) {
				outputfunction.setUndefined();
				return;
			}

			// very important:
			curvefitter.clearObservations();

			for (int i = 0; i < datasize; i++) {
				curvefitter.addObservedPoint(1.0, xdata[i], ydata[i]);
			} // for all datapoints

			curvefitter.fit(prfunction, prfunction.getStartValues());

			outputfunction.setFunction(prfunction.getFunction());
			outputfunction.setDefined(true);

		} catch (Throwable t) {
			outputfunction.setUndefined();
			Log.debug(t.getMessage());
		}

	}

	// Get info from lists into matrixes and functionarray
	private final boolean makeDataArrays() {
		GeoElement geo = null;
		GeoPoint point = null;
		datasize = pointlist.size();
		xdata = new double[datasize];
		ydata = new double[datasize];

		// Make array of datapoints
		for (int i = 0; i < datasize; i++) {
			geo = pointlist.get(i);
			if (!geo.isGeoPoint()) {
				// throw (new Exception("Not points in function list..."));
				return false;
			} // if not point
			point = (GeoPoint) geo;
			xdata[i] = point.getX();
			ydata[i] = point.getY();
		}

		return true;
	}

	@Override
	public double[] getCoeffs() {
		MyDouble[] coeffs = prfunction.getCoeffs();

		double[] ret = new double[coeffs.length];

		for (int i = 0; i < coeffs.length; i++) {
			ret[i] = coeffs[i].getDouble();
		}

		return ret;
	}

}
