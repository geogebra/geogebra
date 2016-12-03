package org.geogebra.common.kernel.statistics;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.QRDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.debug.Log;

/**
 * AlgoFit A general linear curvefit: Fit[<List of Points>,<List of Functions>]
 * Example: f(x)=1, g(x)=x, h(x)=e^x L={A,B,...} c(x)=Fit[L,{f,g,h}] will give a
 * least square curvefit: c(x)= a+b*x+c*e^x
 * 
 * Simple test procedure: Make points A,B, ... L={A,B,...} f(x)=1, g(x)=x,
 * h(x)=x^2,... =x^n F={f,g,h,...} right(x)=Regpoly[L,n] fit(x)=Fit[L,F]
 * 
 * The solution is the usual: M_t*M*X=M_t*Y, the solution of overdetermined
 * linear equation systems..
 * 
 * @author Hans-Petter Ulven
 * @version 2010-02-23
 */
public class AlgoFit extends AlgoElement implements FitAlgo {

	private GeoList pointlist; // input
	private GeoList functionlist; // output
	private CasEvaluableFunction fitfunction; // output

	// variables:
	private int datasize = 0; // rows in M and Y
	private int functionsize = 0; // cols in M
	// private GeoFunctionable[] functionarray = null;
	private FunctionListND functionarray;
	private RealMatrix M = null;
	private RealMatrix Y = null;
	private RealMatrix P = null;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param pointlist
	 *            list of input points
	 * @param functionlist
	 *            lists of functions that can be used in result
	 */
	public AlgoFit(Construction cons, String label, GeoList pointlist,
			GeoList functionlist) {
		super(cons);

		this.pointlist = pointlist;
		this.functionlist = functionlist;
		if (functionlist.getElementType() == GeoClass.FUNCTION_NVAR
				|| pointlist.getElementType() == GeoClass.POINT3D) {
			functionarray = new FunctionListND.XYZ();
		} else {
			functionarray = new FunctionListND.XY();
		}
		fitfunction = functionarray.getTemplate(cons);
		setInputOutput();
		compute();
		fitfunction.setLabel(label);
	}// Constructor

	@Override
	public Commands getClassName() {
		return Commands.Fit;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = pointlist;
		input[1] = functionlist;

		setOnlyOutput(fitfunction);
		setDependencies();
	}

	/**
	 * @return resulting Fit function
	 */
	public CasEvaluableFunction getFit() {
		return fitfunction;
	}

	@Override
	public final void compute() {
		GeoElement geo2 = null;
		datasize = pointlist.size(); // rows in M and Y
		functionsize = functionlist.size(); // cols in M
		functionarray.setSize(functionsize);
		Log.debug(functionsize);
		M = new Array2DRowRealMatrix(datasize, functionsize);
		Y = new Array2DRowRealMatrix(datasize, 1);
		P = new Array2DRowRealMatrix(functionsize, 1); // Solution parameters

		if (!pointlist.isDefined()
				|| // Lot of things can go wrong...
				!functionlist.isDefined() || (functionsize > datasize)
				|| (functionsize < 1) || (datasize < 1) // Perhaps a max
														// restriction of
														// functions and data?
		) // Even if noone would try 500 datapoints and 100 functions...
		{
			fitfunction.setUndefined();
			return;
		}
		// We are in business...
		// Best to also check:
		geo2 = pointlist.get(0);
		if (!geo2.isGeoPoint()) {
			fitfunction.setUndefined();
			return;
		}// if wrong contents in lists
		try {

			// Get functions, x and y from lists
			if (!makeMatrixes()) {
				fitfunction.setUndefined();
				return;
			}

			// Solve for parametermatrix P:
			DecompositionSolver solver = new QRDecompositionImpl(M).getSolver();
			if (solver.isNonSingular()) {
				P = solver.solve(Y);

				fitfunction = functionarray.makeFunction(fitfunction,
						functionlist, P);

			} else {
				fitfunction.setUndefined();
			}
			// walk(fitfunction.getFunctionExpression()); //debug, erase
			// later

			// First solution (Borcherds):
			// fitfunction.set(kernel.getAlgebraProcessor().evaluateToFunction(buildFunction()));
			// fitfunction.setDefined(true);

		} catch (Throwable t) {
			fitfunction.setUndefined();
			t.printStackTrace();
		}

	}// compute()

	// Get info from lists into matrixes and functionarray
	private final boolean makeMatrixes() {
		GeoElement geo = null;
		GeoPointND point = null;

		// Make array of functions:
		for (int i = 0; i < functionsize; i++) {
			geo = functionlist.get(i);
			if (!functionarray.set(i, geo)) {
				return false;
			}
		}// for all functions
			// Make matrixes with the right values: M*P=Y
		M = new Array2DRowRealMatrix(datasize, functionsize);
		Y = new Array2DRowRealMatrix(datasize, 1);
		for (int r = 0; r < datasize; r++) {
			geo = pointlist.get(r);
			if (!geo.isGeoPoint()) {
				// throw (new Exception("Not points in function list..."));
				return false;
			}// if not point
			point = (GeoPointND) geo;
			Y.setEntry(r, 0, functionarray.extractValueCoord(point));
			for (int c = 0; c < functionsize; c++) {
				M.setEntry(r, c, functionarray.evaluate(c, point));
			}
		}// for rows (=datapoints)
			// mprint("M:",M);
			// mprint(Y:",Y);

		return true;

	}



	public double[] getCoeffs() {
		double[] ret = new double[functionsize];
		for (int i = 0; i < functionsize; i++) {
			// reverse order
			ret[i] = P.getEntry(functionsize - i - 1, 0);
		}

		return ret;
	}

	// --- SNIP --- ///

	

}
