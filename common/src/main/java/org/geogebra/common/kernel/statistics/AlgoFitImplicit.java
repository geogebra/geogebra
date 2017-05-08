package org.geogebra.common.kernel.statistics;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.debug.Log;

/**
 * Fits implicit curve of given degree through points
 *
 */
public class AlgoFitImplicit extends AlgoElement {

	private GeoList pointlist; // input
	private GeoElement orderGeo = new GeoNumeric(cons, 2);
	private GeoImplicit fitfunction; // output

	// variables:
	private int datasize = 0; // rows in M and Y
	private Array2DRowRealMatrix M = null, V;

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param pointlist
	 *            list of input points
	 * @param arg
	 *            order of implicit polynomial to fit
	 */
	public AlgoFitImplicit(Construction cons, String label, GeoList pointlist,
			GeoNumberValue arg) {
		super(cons);

		this.pointlist = pointlist;
		this.orderGeo = (GeoElement) arg;
		fitfunction = kernel.newImplicitPoly(cons);
		setInputOutput();
		compute();
		fitfunction.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.FitImplicit;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = pointlist;
		input[1] = orderGeo;

		setOnlyOutput(fitfunction);
		setDependencies();
	}

	/**
	 * @return resulting Fit function
	 */
	public GeoImplicit getFit() {
		return fitfunction;
	}

	@Override
	public final void compute() {

		int order = (int) orderGeo.evaluateDouble();

		datasize = pointlist.size(); // rows in M and Y

		if (!pointlist.isDefined() || datasize < (order * (order + 3)) / 2) {
			fitfunction.setUndefined();
			return;
		}

		if (!pointlist.getElementType().equals(GeoClass.POINT)) {
			fitfunction.setUndefined();
			return;
		}
		// no additional degrees of freedom => use algo for ImplicitPoly[points]
		if (datasize == (order * (order + 3)) / 2) {
			fitfunction.throughPoints(pointlist);
			return;
		}
		try {

			// Get functions, x and y from lists
			if (!makeMatrixes()) {
				fitfunction.setUndefined();
				return;
			}

			 Log.debug("datasize = " + datasize);
			 Log.debug("order = " + order);
			 Log.debug("M cols = "+M.getColumnDimension());
			 Log.debug("M rows = "+M.getRowDimension());
			 Log.debug("M = "+M.toString());

			log(M.getData());

			SingularValueDecomposition svd = new SingularValueDecomposition(
					M);

			V = (Array2DRowRealMatrix) svd.getV();


			Log.debug("size of M = " + M.getColumnDimension() + " "
					+ M.getRowDimension());
			Log.debug("size of V = " + V.getColumnDimension() + " "
					+ V.getRowDimension());
			log(V.getData());

			makeFunction();

		} catch (Throwable t) {
			fitfunction.setUndefined();
			t.printStackTrace();
		}

	}// compute()

	private void log(double[][] data) {
		for (int i = 0; i < data.length; i++) {
			double[] data2 = data[i];
			for (int j = 0; j < data2.length; j++) {
				System.out.print("" + data2[j]);
				System.out.print(", ");
			}
			System.out.print("\n");

		}

	}

	// Get info from lists into matrixes and functionarray
	private final boolean makeMatrixes() {
		GeoElement geo = null;
		GeoPoint point = null;
		double x, y;

		int order = (int) orderGeo.evaluateDouble();

		// Make matrixes with the right values: M*P=Y
		M = new Array2DRowRealMatrix(datasize, (order + 1) * (order + 2) / 2);

		for (int r = 0; r < datasize; r++) {
			geo = pointlist.get(r);
			if (!geo.isGeoPoint()) {
				return false;
			}
			point = (GeoPoint) geo;

			x = point.getX() / point.getZ();
			y = point.getY() / point.getZ();
			int c = 0;

			// create powers eg x^2y^0, x^1y^1, x^0*y^2, x, y, 1
			for (int i = 0; i <= order; i++) {
				for (int xpower = 0; xpower <= i; xpower++) {

					int ypower = i - xpower;

					double val = power(x, xpower) * power(y, ypower);
					// System.err.print(
					// val + "x^" + xpower + " * y^" + ypower + "+");

					M.setEntry(r, c++, val);

				}
			}

		}

		return true;

	}

	/**
	 * @param x
	 *            base
	 * @param power
	 *            exponent
	 * @return base ^ exponent
	 */
	public static double power(double x, int power) {
		if (power == 0) {
			return 1;
		}

		if (power == 1) {
			return x;
		}

		double ret = x;
		int pow = power;

		while (pow > 1) {
			ret *= x;
			pow--;
		}

		return ret;
	}

	private final void makeFunction() {

		int order = (int) orderGeo.evaluateDouble();

		double[][] coeffs = new double[order + 1][order + 1];

		// Log.debug("row/cols = "+V.getRowDimension() + " "+
		// V.getColumnDimension()+" "+(order * (order + 3) / 2 -1));

		// Log.debug(V.toString());

		RealVector coeffsRV = V.getColumnVector(V.getColumnDimension() - 1);

		int c = 0;

		// create powers eg x^2y^0, x^1y^1, x^0*y^2, x, y, 1
		for (int i = 0; i <= order; i++) {
			for (int j = 0; j <= i; j++) {

				coeffs[j][i - j] = coeffsRV.getEntry(c++);

			}
		}

		Log.debug("coeffs=");
		log(coeffs);

		fitfunction.setCoeff(coeffs);
		fitfunction.setDefined();
	}

}
