package geogebra.common.kernel.statistics;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.optimization.FitRealFunction;
import geogebra.common.main.App;

import org.apache.commons.math.optimization.fitting.CurveFitter;
import org.apache.commons.math.optimization.general.LevenbergMarquardtOptimizer;

/**
 * <pre>
 * AlgoFitNL:			(NL=NonLinear Curvefit)
 * A general curvefit:
 * 		Fit[<List of Points>,<Function>]
 * Example:
 *     f(x)=a+b/(x-c)
 *     L={A,B,...}
 *     g(x)=Fit[L,f]
 *     will give a function
 *     g(x)=p1+p2/(x-p3)
 *     where p1, p2 and p3 are calculated to give the least sum of squared errors.
 *     
 * The nonlinear curve-fitting is done with an iteration algortithm, which is not
 * guaranteed to work.
 * The values of a, b and c are taken as starting points for the iteration algorithm.
 * If the iteration does not converge or the number of iterations is getting to large,
 * the result is undefined, a signal for the user to try to adjust the starting
 * point with the gliders a, b and c.
 * 
 * Uses Levenberg-Marquardt algorithm in org.apache.commons library
 * 
 * ToDo:		The gradient in FitRealFunction could be more sophisticated, but the Apache lib is quite robust :-)
 *   				Some tuning of numerical precision both here and in the setup of LM-optimizer
 * </pre>
 * 
 * @author Hans-Petter Ulven
 * @version 2011-03-15
 */
public class AlgoFitNL extends AlgoElement {

	private static final boolean DEBUG = false; // false in distribution

	private GeoList pointlist; // input
	private GeoFunction inputfunction; // input
	private GeoFunction outputfunction; // output

	// variables:
	private int datasize = 0; // rows in M and Y
	private double[] xdata = null;
	private double[] ydata = null;
	private FitRealFunction prfunction = null; // function for Apache lib
	private LevenbergMarquardtOptimizer LMO = new LevenbergMarquardtOptimizer();
	private CurveFitter curvefitter = new CurveFitter(LMO);

	public AlgoFitNL(Construction cons, String label, GeoList pointlist,
			GeoFunction inputfunction) {
		super(cons);

		this.pointlist = pointlist;
		this.inputfunction = inputfunction;
		outputfunction = new GeoFunction(cons);
		setInputOutput();
		compute();
		outputfunction.setLabel(label);
	}// Constructor

	@Override
	public Algos getClassName() {
		return Algos.AlgoFitNL;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = pointlist;
		input[1] = inputfunction;
		setOnlyOutput(outputfunction);
		setDependencies();
	}// setInputOutput()

	public GeoFunction getFitNL() {
		return outputfunction;
	}// getFitNL()

	@Override
	public final void compute() {
		GeoElement geo1 = null;
		GeoElement geo2 = null;
		this.datasize = pointlist.size(); // Points in dataset

		if (!pointlist.isDefined() || !inputfunction.isDefined()
				|| (datasize < 1) // Perhaps a max restriction of functions and
									// data?
		) // Even if noone would try 500 datapoints and 100 functions...
		{
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
		}// if wrong contents in lists
		try {
			makeDataArrays(); // Get points as x[] and y[] from lists

			// / --- Solve :-) --- ///

			// prfunction makes itself a copy of inputfunction with
			// parameters instead of GeoNumerics
			prfunction = new FitRealFunction(inputfunction.getFunction());

			// very important:
			curvefitter.clearObservations();

			for (int i = 0; i < datasize; i++) {
				curvefitter.addObservedPoint(1.0, xdata[i], ydata[i]);
			}// for all datapoints

			double[] result = curvefitter.fit(prfunction,
					prfunction.getStartValues());

			// DEBUG - to be removed:
			int iter = LMO.getIterations();
			if (iter > 200) {
				errorMsg("More than 200 iterations...");
			}

			outputfunction.setFunction(prfunction.getFunction());
			outputfunction.setDefined(true);

		} catch (Throwable t) {
			outputfunction.setUndefined();
			errorMsg(t.getMessage());
			if (DEBUG) {
				t.printStackTrace();
			}
		}// try-catch

	}// compute()

	// Get info from lists into matrixes and functionarray
	private final void makeDataArrays() throws Exception {
		GeoElement geo = null;
		GeoPoint point = null;
		datasize = pointlist.size();
		xdata = new double[datasize];
		ydata = new double[datasize];

		// Make array of datapoints
		for (int i = 0; i < datasize; i++) {
			geo = pointlist.get(i);
			if (!geo.isGeoPoint()) {
				throw (new Exception("Not points in function list..."));
			}// if not point
			point = (GeoPoint) geo;
			xdata[i] = point.getX();
			ydata[i] = point.getY();
		}// for rows (=datapoints)
	}// makeDataArrays()

	// / --- Debug --- ///
	private final static void errorMsg(String s) {
		App.debug(s);
	}// errorMsg(String)

	// TODO Consider locusequability

}// class AlgoFitNL

