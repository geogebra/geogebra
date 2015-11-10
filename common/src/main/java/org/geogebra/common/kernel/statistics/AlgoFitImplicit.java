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
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.linear.SingularValueDecomposition;
import org.apache.commons.math.linear.SingularValueDecompositionImpl;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitPoly;
import org.geogebra.common.plugin.GeoClass;

public class AlgoFitImplicit extends AlgoElement {

	private GeoList pointlist; // input
	private GeoElement orderGeo = new GeoNumeric(cons, 2);
	private GeoImplicit fitfunction; // output

	// variables:
	private int datasize = 0; // rows in M and Y
	private RealMatrix M = null, V;

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
		fitfunction = new GeoImplicitPoly(cons);
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

		if (!pointlist.isDefined() || datasize < order * (order + 3) / 2) {
			fitfunction.setUndefined();
			return;
		}

		if (!pointlist.getElementType().equals(GeoClass.POINT)) {
			fitfunction.setUndefined();
			return;
		}
		try {

			// Get functions, x and y from lists
			if (!makeMatrixes()) {
				fitfunction.setUndefined();
				return;
			}

			// App.debug("datasize = " + datasize);
			// App.debug("order = " + order);
			// App.debug("M cols = "+M.getColumnDimension());
			// App.debug("M rows = "+M.getRowDimension());
			// App.debug("M = "+M.toString());

			SingularValueDecomposition svd = new SingularValueDecompositionImpl(
					M);

			V = svd.getV();

			// App.debug("V = "+V.toString());

			// App.debug("size of M = "+M.getColumnDimension()+" "+M.getRowDimension());
			// App.debug("size of V = "+V.getColumnDimension()+" "+V.getRowDimension());

			makeFunction();

		} catch (Throwable t) {
			fitfunction.setUndefined();
			t.printStackTrace();
		}

	}// compute()

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

			x = point.getX();
			y = point.getY();
			int c = 0;

			// create powers eg x^2y^0, x^1y^1, x^0*y^2, x, y, 1
			for (int i = 0; i <= order; i++) {
				for (int xpower = 0; xpower <= i; xpower++) {

					int ypower = i - xpower;

					double val = power(x, xpower) * power(y, ypower);
					// App.debug(val + "x^"+xpower+" * y^"+ypower);

					M.setEntry(r, c++, val);

				}
			}

		}

		return true;

	}

	public static double power(double x, int power) {
		if (power == 0) {
			return 1;
		}

		if (power == 1) {
			return x;
		}

		double ret = x;

		while (power > 1) {
			ret *= x;
			power--;
		}

		return ret;
	}

	private final void makeFunction() {

		int order = (int) orderGeo.evaluateDouble();

		double[][] coeffs = new double[order + 1][order + 1];

		// App.debug("row/cols = "+V.getRowDimension() + " "+
		// V.getColumnDimension()+" "+(order * (order + 3) / 2 -1));

		// App.debug(V.toString());

		RealVector coeffsRV = V.getColumnVector(V.getColumnDimension() - 1);

		int c = 0;

		// create powers eg x^2y^0, x^1y^1, x^0*y^2, x, y, 1
		for (int i = 0; i <= order; i++) {
			for (int j = 0; j <= i; j++) {

				coeffs[j][i - j] = coeffsRV.getEntry(c++);

			}
		}

		fitfunction.setCoeff(coeffs);
		fitfunction.setDefined();
	}

}
