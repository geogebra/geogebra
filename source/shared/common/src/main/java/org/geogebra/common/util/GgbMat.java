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

package org.geogebra.common.util;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Matrix format allowing conversion from/to MyList and GeoList, supporting
 * matrix operations (inverse, determinant etc.)
 * 
 * @author Michael Borcherds
 *
 */
public class GgbMat extends Array2DRowRealMatrix {

	private static final long serialVersionUID = 1L;

	private boolean isUndefined = false;

	/**
	 * Creates matrix from GeoList
	 * 
	 * @param inputList
	 *            list
	 */
	public GgbMat(GeoList inputList) {
		int rows = inputList.size();
		if (!inputList.isDefined() || rows == 0) {
			setIsUndefined(true);
			return;
		}

		GeoElement geo = inputList.get(0);

		if (!geo.isGeoList()) {
			setIsUndefined(true);
			return;
		}

		int cols = ((GeoList) geo).size();

		if (cols == 0) {
			setIsUndefined(true);
			return;
		}

		data = new double[rows][cols];
		// m = rows;
		// n = cols;

		GeoList rowList;

		for (int r = 0; r < rows; r++) {
			geo = inputList.get(r);
			if (!geo.isGeoList()) {
				setIsUndefined(true);
				return;
			}
			rowList = (GeoList) geo;
			if (rowList.size() != cols) {
				setIsUndefined(true);
				return;
			}
			for (int c = 0; c < cols; c++) {
				geo = rowList.get(c);
				if (!geo.isGeoNumeric()) {
					setIsUndefined(true);
					return;
				}

				setEntry(r, c, ((GeoNumeric) geo).getValue());
			}
		}
	}

	/**
	 * Creates matrix from MyList
	 * 
	 * @param inputList
	 *            list
	 */
	public GgbMat(MyList inputList) {

		if (!inputList.isMatrix()) {
			setIsUndefined(true);
			return;
		}
		int rows = inputList.getMatrixRows();
		int cols = inputList.getMatrixCols();
		if (rows < 1 || cols < 1) {
			setIsUndefined(true);
			return;
		}

		data = new double[rows][cols];

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				ExpressionValue geo = MyList.getCell(inputList, c, r);
				if (!(geo instanceof NumberValue)) {
					setIsUndefined(true);
					return;
				}
				setEntry(r, c, geo.evaluateDouble());
			}
		}
	}

	/**
	 * @param rows
	 *            number of rows
	 * @param cols
	 *            number of columns
	 */
	public GgbMat(int rows, int cols) {
		data = new double[rows][cols];
		setIsUndefined(false);
	}

	/**
	 * Inverts this matrix. If singular, sets the undefined flag to true.
	 */
	public void inverseImmediate() {

		try {
			DecompositionSolver d = new LUDecomposition(this,
					Kernel.STANDARD_PRECISION).getSolver();
			RealMatrix ret = d.getInverse();
			data = ret.getData();
			// m = ret.m;
			// n = ret.n;
		} catch (Exception e) { // can't invert
			setIsUndefined(true);
		}
	}

	/**
	 * Returns determinant of this matrix
	 * 
	 * @return determinant
	 */
	public double determinant() {
		return new LUDecomposition(this, Kernel.STANDARD_PRECISION)
				.getDeterminant();
	}

	/**
	 * Computes the reduced row echelon form.
	 * 
	 * code from http://rosettacode.org/wiki/Reduced_row_echelon_form
	 */
	public void reducedRowEchelonFormImmediate() {
		int rowCount = data.length;
		if (rowCount == 0) {
			return;
		}

		int columnCount = data[0].length;

		int lead = 0;
		for (int r = 0; r < rowCount; r++) {
			if (lead >= columnCount) {
				break;
			}
			{
				int i = r;
				// make sure we don't use a leader which is almost zero
				// https://help.geogebra.org/topic/bug-in-treppennormalform-
				while (DoubleUtil.isZero(data[i][lead])) {
					data[i][lead] = 0;
					i++;
					if (i == rowCount) {
						i = r;
						lead++;
						if (lead == columnCount) {
							return;
						}
					}
				}
				double[] temp = data[r];
				data[r] = data[i];
				data[i] = temp;
			}

			{
				double lv = data[r][lead];
				for (int j = 0; j < columnCount; j++) {
					data[r][j] /= lv;
				}
			}

			for (int i = 0; i < rowCount; i++) {
				if (i != r) {
					double lv = data[i][lead];
					for (int j = 0; j < columnCount; j++) {
						data[i][j] -= lv * data[r][j];
					}
				}
			}
			lead++;
		}
	}

	/**
	 * Transposes this matrix
	 */
	public void transposeImmediate() {

		int m = getRowDimension();
		int n = getColumnDimension();

		double[][] C = new double[n][m];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				C[j][i] = data[i][j];
			}
		}
		data = C;
	}

	/**
	 * returns GgbMatrix as a GeoList eg { {1,2}, {3,4} }
	 * 
	 * @param outputList
	 *            list for the copy
	 * @param cons
	 *            construction
	 */
	public void getGeoList(GeoList outputList, Construction cons) {

		if (isUndefined) {
			outputList.setDefined(false);
			return;
		}

		outputList.clear();
		outputList.setDefined(true);

		for (int r = 0; r < getRowDimension(); r++) {
			GeoList columnList = new GeoList(cons);
			for (int c = 0; c < getColumnDimension(); c++) {
				columnList.add(new GeoNumeric(cons, getEntry(r, c)));
			}
			outputList.add(columnList);
		}
	}

	/**
	 * returns GgbMatrix as a MyList eg { {1,2}, {3,4} }
	 * 
	 * @param outputList
	 *            list for the copy
	 * @param kernel
	 *            kernel
	 */
	public void getMyList(MyList outputList, Kernel kernel) {
		if (isUndefined) {
			return;
		}

		outputList.clear();

		for (int r = 0; r < getRowDimension(); r++) {
			MyList columnList = new MyList(kernel);
			for (int c = 0; c < getColumnDimension(); c++) {
				columnList.addListElement(new GeoNumeric(
						kernel.getConstruction(), getEntry(r, c)));
			}
			outputList.addListElement(columnList);
		}
	}

	/**
	 * @return true if the matrix is undefined eg after being inverted
	 */
	public boolean isUndefined() {
		return isUndefined;
	}

	/**
	 * Sets the undefined flag to false (e.g. when inverting singular matrix)
	 * 
	 * @param undefined
	 *            new undefined flag
	 */
	public void setIsUndefined(boolean undefined) {
		isUndefined = undefined;
	}

	/**
	 * True for matrix formed by integers
	 * 
	 * @return true if all entries are integers
	 */
	public boolean hasOnlyIntegers() {
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				if (!DoubleUtil.isInteger(data[i][j])) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param matrix
	 *            flat matrix of a GeoConic
	 */
	public void set3x3fromConic(double[] matrix) {
		// Axx
		setEntry(0, 0, matrix[0]);
		// Axy
		setEntry(0, 1, matrix[3]);
		setEntry(1, 0, matrix[3]);
		// Ayy
		setEntry(1, 1, matrix[1]);
		// Bx
		setEntry(2, 0, matrix[4]);
		setEntry(0, 2, matrix[4]);
		// By
		setEntry(2, 1, matrix[5]);
		setEntry(1, 2, matrix[5]);
		// C
		setEntry(2, 2, matrix[2]);

		setIsUndefined(false);

	}

}
