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
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
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
	private final GeoElement orderGeo;
	private GeoImplicit fitfunction; // output

	// variables:
	private int datasize = 0; // rows in M and Y
	private RealMatrix M = null;
	private RealMatrix V;

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

			SingularValueDecomposition svd = new SingularValueDecomposition(
					M);

			V = svd.getV();

			makeFunction();
		} catch (Throwable t) {
			fitfunction.setUndefined();
			Log.debug(t);
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

					double val = Math.pow(x, xpower) * Math.pow(y, ypower);

					M.setEntry(r, c++, val);
				}
			}
		}

		return true;
	}

	private final void makeFunction() {
		int order = (int) orderGeo.evaluateDouble();

		double[][] coeffs = new double[order + 1][order + 1];

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
