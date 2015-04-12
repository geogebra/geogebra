/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.Matrix;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;

/**
 * Simple matrix description with basic linear algebra methods.
 * 
 * @author ggb3D
 * 
 */
public class CoordMatrix {

	/**
	 * the 2x2 matrix represented by Coords[] = {{1,2},{3,4}} is <br>
	 * <code>
	 * | 1  3 |  <br>
	 * | 2  4 |
	 * </code>
	 */
	//public double[] val;
	protected Coords[] vectors;
	
	/** number of rows of the matrix */
	protected int rows;
	/** number of columns of the matrix */
	protected int columns;


	// for rotations
	/** rotation around x-axis */
	public static final int X_AXIS = 1;
	/** rotation around y-axis */
	public static final int Y_AXIS = 2;
	/** rotation around z-axis */
	public static final int Z_AXIS = 3;

	// /////////////////////////////////////////////////:
	// Constructors

	

	/**
	 * see class description
	 * 
	 * @param rows
	 *            number of rows
	 * @param columns
	 *            number of columns
	 * @param val
	 *            values
	 */
	public CoordMatrix(int rows, int columns, double[] val) {
		this.rows = rows;
		this.columns = columns;
		
		this.vectors = new Coords[columns];
		for (int j = 0 ; j < columns ; j++){
			vectors[j] = new Coords(rows);
			for (int i = 0 ; i < rows ; i++){
				vectors[j].set(i+1, val[j*rows + i]);
			}
		}
	}

	/**
	 * creates an empty rows * columns matrix (all values set to 0)
	 * 
	 * @param rows
	 *            number of rows
	 * @param columns
	 *            number of values
	 */
	public CoordMatrix(int rows, int columns) {

		initialise(rows, columns);

	}

	/**
	 * init the matrix (all values to 0)
	 * 
	 * @param rows
	 *            number of rows
	 * @param columns
	 *            number of columns
	 */
	private void initialise(int rows, int columns) {

		this.rows = rows;
		this.columns = columns;

		vectors = new Coords[columns];
		for (int i = 0; i < columns; i++) {
			vectors[i] = new Coords(rows);
		}

	}

	/**
	 * TODO doc
	 * 
	 * @param inputList
	 */
	public CoordMatrix(GeoList inputList) {

		int cols = inputList.size();
		if (!inputList.isDefined() || cols == 0) {
			setIsSingular(true);
			return;
		}

		GeoElement geo = inputList.get(0);

		if (!geo.isGeoList()) {
			setIsSingular(true);
			return;
		}

		int rows = ((GeoList) geo).size();

		if (rows == 0) {
			setIsSingular(true);
			return;
		}

		initialise(rows, cols);

		GeoList columnList;

		for (int r = 0; r < rows; r++) {
			geo = inputList.get(r);
			if (!geo.isGeoList()) {
				setIsSingular(true);
				return;
			}
			columnList = (GeoList) geo;
			if (columnList.size() != columns) {
				setIsSingular(true);
				return;
			}
			for (int c = 0; c < rows; c++) {
				geo = columnList.get(c);
				if (!geo.isGeoNumeric()) {
					setIsSingular(true);
					return;
				}

				set(r + 1, c + 1, ((GeoNumeric) geo).getValue());
			}
		}

	}

	/**
	 * returns n*n identity matrix
	 * 
	 * @param n
	 *            dimension
	 * @return the identity matrix
	 */
	public static final CoordMatrix Identity(int n) {

		CoordMatrix m = new CoordMatrix(n, n);

		for (int i = 1; i <= n; i++) {
			m.set(i, i, 1.0);
		}

		return m;

	}

	/**
	 * returns scale homogenic matrix, dim v.length+1
	 * 
	 * @param v
	 *            scaling vector
	 * @return scale matrix
	 */
	public static final CoordMatrix ScaleMatrix(double[] v) {

		return ScaleMatrix(new Coords(v));

	}

	/**
	 * returns scale homogenic matrix, dim v.length+1
	 * 
	 * @param v
	 *            scaling vector
	 * @return scale matrix
	 */
	public static final CoordMatrix ScaleMatrix(Coords v) {

		int n = v.getLength();
		CoordMatrix m = new CoordMatrix(n + 1, n + 1);

		for (int i = 1; i <= n; i++) {
			m.set(i, i, v.get(i));
		}
		m.set(n + 1, n + 1, 1.0);

		return m;

	}

	/**
	 * returns diagonal matrix
	 * 
	 * @param vals
	 * @return diagonal matrix
	 */
	public static final CoordMatrix DiagonalMatrix(double vals[]) {

		int n = vals.length;
		CoordMatrix m = new CoordMatrix(n, n);

		for (int i = 1; i <= n; i++) {
			m.set(i, i, vals[i - 1]);
		}

		return m;

	}

	/**
	 * returns translation homogenic matrix, dim v.length+1
	 * 
	 * @param v
	 *            translation vector
	 * @return traslation matrix
	 */
	public static final CoordMatrix TranslationMatrix(double[] v) {

		return TranslationMatrix(new Coords(v));

	}

	/**
	 * returns translation homogenic matrix, dim v.length+1
	 * 
	 * @param v
	 *            translation vector
	 * @return traslation matrix
	 */
	public static final CoordMatrix TranslationMatrix(Coords v) {

		int n = v.getLength();
		CoordMatrix m = new CoordMatrix(n + 1, n + 1);

		for (int i = 1; i <= n; i++) {
			m.set(i, i, 1.0);
			m.set(i, n + 1, v.get(i));
		}
		m.set(n + 1, n + 1, 1.0);

		return m;

	}

	/**
	 * returns 3d rotation homogenic matrix, dim 4x4
	 * 
	 * @param axe
	 *            axis of rotation
	 * @param angle
	 *            angle of rotation
	 * @return rotation matrix
	 */
	public static final CoordMatrix Rotation3DMatrix(int axe, double angle) {

		CoordMatrix m = new CoordMatrix(4, 4);

		switch (axe) {

		case Z_AXIS:
			m.set(1, 1, Math.cos(angle));
			m.set(1, 2, -Math.sin(angle));
			m.set(2, 1, Math.sin(angle));
			m.set(2, 2, Math.cos(angle));
			m.set(3, 3, 1.0);
			break;
		case X_AXIS:
			m.set(1, 1, 1.0);
			m.set(2, 2, Math.cos(angle));
			m.set(2, 3, -Math.sin(angle));
			m.set(3, 2, Math.sin(angle));
			m.set(3, 3, Math.cos(angle));
			break;
		case Y_AXIS:
			m.set(2, 2, 1.0);
			m.set(3, 3, Math.cos(angle));
			m.set(3, 1, -Math.sin(angle));
			m.set(1, 3, Math.sin(angle));
			m.set(1, 1, Math.cos(angle));
			break;
		default:
			break;
		}

		m.set(4, 4, 1.0);

		return m;

	}
	
	/**
	 * 3x3 rotation matrix around oz
	 * @param angle angle of rotation
	 */	
	public static final void Rotation3x3(double angle, CoordMatrix m) {
		
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		m.set(1,1, cos); m.set(1,2, -sin);
		m.set(2,1, sin); m.set(2,2,  cos);
		m.set(3,3, 1);
	}
	
	
	/**
	 * 3x3 rotation matrix around vector
	 * @param u vector of rotation
	 * @param angle angle of rotation
	 * @return matrix
	 */
	public static final void Rotation3x3(Coords u, double angle, CoordMatrix m) {
		
		double ux = u.getX();
		double uy = u.getY();
		double uz = u.getZ();
		
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		
		Coords[] vectors = m.vectors;
		vectors[0].setX(ux*ux*(1-c) + c);
		vectors[0].setY(ux*uy*(1-c) + uz*s);
		vectors[0].setZ(ux*uz*(1-c) - uy*s);
		
		vectors[1].setX(ux*uy*(1-c) - uz*s);
		vectors[1].setY(uy*uy*(1-c) + c);
		vectors[1].setZ(uy*uz*(1-c) + ux*s);
		
		vectors[2].setX(ux*uz*(1-c) + uy*s);
		vectors[2].setY(uy*uz*(1-c) - ux*s);
		vectors[2].setZ(uz*uz*(1-c) + c);

	}

	// /////////////////////////////////////////////////:
	// setters and getters

	/**
	 * set double[] describing the matrix for openGL
	 * @param val values set
	 * 
	 */
	public void get(double[] val) {

		for (int i = 0 ; i < rows ; i++){
			for (int j = 0 ; j < columns ; j++){
				val[i+j*rows] = get(i+1, j+1);
			}		
		}
	}

	/**
	 * returns m(i,j)
	 * 
	 * @param i
	 *            number of row
	 * @param j
	 *            number of column
	 * @return value
	 */
	public double get(int i, int j) {
		return vectors[j-1].get(i);
	}

	/**
	 * returns this minus the row i and the column j
	 * 
	 * @param i
	 *            row to remove
	 * @param j
	 *            column to remove
	 * @return sub-matrix
	 */
	public CoordMatrix subMatrix(int i, int j) {
		CoordMatrix ret = new CoordMatrix(getRows() - 1, getColumns() - 1);

		for (int i1 = 1; i1 < i; i1++) {
			for (int j1 = 1; j1 < j; j1++) {
				ret.set(i1, j1, get(i1, j1));
			}
			for (int j1 = j + 1; j1 <= getColumns(); j1++) {
				ret.set(i1, j1 - 1, get(i1, j1));
			}
		}

		for (int i1 = i + 1; i1 <= getRows(); i1++) {
			for (int j1 = 1; j1 < j; j1++) {
				ret.set(i1 - 1, j1, get(i1, j1));
			}
			for (int j1 = j + 1; j1 <= getColumns(); j1++) {
				ret.set(i1 - 1, j1 - 1, get(i1, j1));
			}
		}

		return ret;
	}

	/**
	 * returns the column number j
	 * 
	 * @param j
	 *            number of column
	 * @return the column
	 */
	public Coords getColumn(int j) {
		
		return vectors[j-1];

	}

	/**
	 * returns GgbMatrix as a GeoList eg { {1,2}, {3,4} }
	 * 
	 * @param outputList
	 * @param cons
	 * @return eg { {1,2}, {3,4} }
	 */
	public GeoList getGeoList(GeoList outputList, Construction cons) {

		if (isSingular()) {
			outputList.setDefined(false);
			return outputList;
		}

		outputList.clear();
		outputList.setDefined(true);

		for (int r = 0; r < rows; r++) {
			GeoList columnList = new GeoList(cons);
			for (int c = 0; c < columns; c++) {
				GeoNumeric num = new GeoNumeric(cons);
				num.setValue(get(r + 1, c + 1));
				columnList.add(num);
			}
			outputList.add(columnList);
		}

		return outputList;

	}

	/**
	 * sets V to column j of m, rows=V.getLength()
	 * 
	 * @param V
	 *            the new column
	 * @param j
	 *            number of the column
	 */
	public void set(Coords V, int j) {
		for (int i = 1; i <= V.getLength(); i++) {
			set(i, j, V.get(i));
		}
	}

	/**
	 * sets m(V[]), all V[j].getLength equal rows and V.Length=columns
	 * 
	 * @param V
	 *            the vectors
	 */
	public void set(Coords[] V) {
		int j;
		for (j = 0; j < V.length; j++) {
			set(V[j], j + 1);
		}
	}

	/**
	 * sets m(i,j) to val0
	 * 
	 * @param i
	 *            number of row
	 * @param j
	 *            number of columns
	 * @param val0
	 *            value
	 */
	public void set(int i, int j, double val0) {		
			vectors[j-1].set(i, val0);
	}

	/**
	 * sets all values to val0
	 * 
	 * @param val0
	 *            value
	 */
	public void set(double val0) {

		for (int i = 0; i < columns; i++) {			
			vectors[i].set(val0);
		}
	}

	/**
	 * copies all values of m
	 * 
	 * @param m
	 *            source matrix
	 */
	public void set(CoordMatrix m) {

		for (int i = 1; i <= m.getRows(); i++) {
			for (int j = 1; j <= m.getColumns(); j++) {
				this.set(i, j, m.get(i, j));
			}
		}
	}

	/**
	 * returns number of rows
	 * 
	 * @return number of rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * returns number of columns
	 * 
	 * @return number of columns
	 */
	public int getColumns() {
		return columns;
	}

	
	/**
	 * returns a copy of the matrix
	 * 
	 * @return copy of the matrix
	 */
	public CoordMatrix copy() {

		CoordMatrix result = new CoordMatrix(getRows(), getColumns());

		for (int i = 1; i <= result.getRows(); i++) {
			for (int j = 1; j <= result.getColumns(); j++) {
				result.set(i, j, get(i, j));
			}
		}

		return result;

	}
	
	/**
	 * copy the matrix into result
	 * @param result matrix
	 */
	public void copy(CoordMatrix result) {

		for (int i = 1; i <= result.getRows(); i++) {
			for (int j = 1; j <= result.getColumns(); j++) {
				result.set(i, j, get(i, j));
			}
		}


	}


	/**
	 * returns a transposed copy of the matrix
	 * 
	 * @return transposed copy of the matrix
	 */
	public CoordMatrix transposeCopy() {

		CoordMatrix result = new CoordMatrix(columns, rows);
		transposeCopy(result);

		return result;

	}

	/**
	 * copy this transposed into result
	 * @param result matrix
	 */
	public void transposeCopy(CoordMatrix result) {
		
		for (int i = 1; i <= result.getRows(); i++) {
			for (int j = 1; j <= result.getColumns(); j++) {
				result.set(i, j, get(j, i));
			}
		}

	}


	/** prints the matrix to the screen */
	public void SystemPrint() {

		App.debug(toString());
	}

	@Override
	public String toString() {
		String s = "";

		for (int i = 1; i <= getRows(); i++) {

			for (int j = 1; j <= getColumns(); j++) {
				double v = get(i, j);
				if (Kernel.isZero(v))
					v = 0;
				s += "  " + v;
			}
			s += "\n";
		}

		return s;
	}

	/**
	 * returns false if one value equals NaN
	 * 
	 * @return false if one value equals NaN
	 */
	public boolean isDefined() {


		for (int i = 0; i < columns ; i++) {
			if (!vectors[i].isDefined()){
				return false;
			}
		}

		return true;
	}
	
	
	/** @return false if at least one value is infinite */
	public boolean isFinite() {


		for (int i = 0; i < columns; i++) {
			if (!vectors[i].isFinite()){
				return false;
			}
		}

		return true;
	}

	// /////////////////////////////////////////////////:
	// basic operations

	// multiplication by a real
	/**
	 * returns this * val0
	 * 
	 * @param val0
	 *            value
	 * @return this*val0
	 */
	public CoordMatrix mul(double val0) {

		CoordMatrix result = new CoordMatrix(getRows(), getColumns());

		for (int i = 1; i <= result.getRows(); i++) {
			for (int j = 1; j <= result.getColumns(); j++) {
				result.set(i, j, val0 * get(i, j));
			}
		}

		return result;
	}
	
	/**
	 * multiply all values by v
	 * @param v factor
	 */
	public void mulInside(double v){
		for (int i = 0 ; i < columns; i++){
			vectors[i].mulInside(v);
		}
	}

	// matrix addition
	/**
	 * returns this + m
	 * 
	 * @param m
	 *            a matrix
	 * @return sum matrix (or vector)
	 */
	public CoordMatrix add(CoordMatrix m) {

		CoordMatrix result = new CoordMatrix(getRows(), getColumns());
		// resulting matrix has the same dimension than this
		// and is a GgbVector if this has 1 column

		for (int i = 1; i <= result.getRows(); i++) {
			for (int j = 1; j <= result.getColumns(); j++) {
				result.set(i, j, get(i, j) + m.get(i, j));
			}
		}

		return result;

	}
	
	/**
	 * returns this + m, perform addition only on m existing values
	 * (leave other unchanged)
	 * 
	 * @param m
	 *            a matrix
	 * @return sum matrix (or vector)
	 */
	public CoordMatrix addSmaller(CoordMatrix m) {

		CoordMatrix result = new CoordMatrix(getRows(), getColumns());
		// resulting matrix has the same dimension than this
		// and is a GgbVector if this has 1 column

		for (int i = 1; i <= m.getRows(); i++) {
			for (int j = 1; j <= m.getColumns(); j++) {
				result.set(i, j, get(i, j) 
						+ m.get(i, j));
			}
		}

		return result;

	}

	// vector multiplication
	/**
	 * returns this * v
	 * 
	 * @param v
	 *            vector
	 * @return resulting vector
	 */
	public Coords mul(Coords v) {

		Coords result = new Coords(getRows());

		for (int i = 1; i <= result.getLength(); i++) {

			double r = 0;
			for (int n = 1; n <= getColumns(); n++)
				r += get(i, n) * v.get(n);

			result.set(i, r);
		}

		return result;
	}

	// matrix multiplication
	/**
	 * returns this * m
	 * 
	 * @param m
	 *            matrix
	 * @return resulting matrix
	 */
	public CoordMatrix mul(CoordMatrix m) {

		CoordMatrix result = new CoordMatrix(getRows(), m.getColumns());

		/*
		 * for(int i=1;i<=result.getRows();i++){ for(int
		 * j=1;j<=result.getColumns();j++){
		 * 
		 * double r = 0; for (int n=1; n<=getColumns(); n++)
		 * r+=get(i,n)*m.get(n,j);
		 * 
		 * result.set(i,j,r); } }
		 */

		this.mul(m, result);

		return result;
	}

	/**
	 * this * m -> result
	 * 
	 * @param m
	 *            matrix
	 * @param result
	 *            resulting matrix
	 */
	public void mul(CoordMatrix m, CoordMatrix result) {

		for (int i = 1; i <= result.getRows(); i++) {
			for (int j = 1; j <= result.getColumns(); j++) {

				double r = 0;
				for (int n = 1; n <= getColumns(); n++)
					r += get(i, n) * m.get(n, j);

				result.set(i, j, r);
			}
		}
	}
	
	/**
	 * set this to m1 * m2
	 * @param m1 first matrix
	 * @param m2 second matrix
	 * @return this
	 */
	public CoordMatrix setMul(CoordMatrix m1, CoordMatrix m2) {

		for (int i = 1; i <= getRows(); i++) {
			for (int j = 1; j <= getColumns(); j++) {

				double r = 0;
				for (int n = 1; n <= m1.getColumns(); n++){
					r += m1.get(i, n) * m2.get(n, j);
				}

				set(i, j, r);
			}
		}
		
		return this;
	}
	
	/**
	 * set this to transpose(m1) * m2
	 * @param m1 first matrix
	 * @param m2 second matrix
	 * @return this
	 */
	public CoordMatrix setMulT1(CoordMatrix m1, CoordMatrix m2) {

		for (int i = 1; i <= getRows(); i++) {
			for (int j = 1; j <= getColumns(); j++) {

				double r = 0;
				for (int n = 1; n <= m1.getRows(); n++){
					r += m1.get(n, i) * m2.get(n, j);
				}

				set(i, j, r);
			}
		}
		
		return this;
	}
	/**
	 * 
	 * @param m matrix
	 * @return 4x4 matrix with multiplication made only in 3x3 up-left submatrix
	 */
	protected CoordMatrix4x4 mul3x3(CoordMatrix m) {

		CoordMatrix4x4 result = new CoordMatrix4x4();
		
		for (int i = 1; i <= 3; i++) {
			for (int j = 1; j <= 3; j++) {

				double r = 0;
				for (int n = 1; n <= 3; n++)
					r += get(i, n) * m.get(n, j);

				result.set(i, j, r);
			}
		}
		
		return result;
	}

	/**
	 * returns determinant
	 * 
	 * @return determinant of the matrix
	 */
	public double det() {

		double ret = 0.0;

		if (getRows() == 1) {
			ret = get(1, 1);
		} else {
			double signe = 1.0;
			for (int j = 1; j <= getColumns(); j++) {
				ret += get(1, j) * signe * (subMatrix(1, j).det());
				signe = -signe;
			}
		}

		return ret;
	}

	/**
	 * says if the matrix is a square-matrix
	 * 
	 * @return true if the matrix is a square-matrix
	 */
	public boolean isSquare() {
		if (isSingular())
			return false;
		return getRows() == getColumns();
	}
	
	
	private CoordMatrix inverse;

	/**
	 * returns inverse matrix (2x2 or larger). You must check with isSquare()
	 * before calling this
	 * 
	 * @return inverse matrix
	 * */
	public CoordMatrix inverse() {

		if (inverse == null){
			inverse = new CoordMatrix(getRows(), getColumns());
		}


		if (pivotInverseMatrix == null){
			pivotInverseMatrix = new PivotInverseMatrix();
			pivotInverseMatrix.matrixRes = new double[columns*columns];
			for (int c = 0; c < columns ; c++){
				pivotInverseMatrix.matrixRes[c*rows + c] = 1;
			}
			pivotInverseMatrix.inverse = inverse.vectors;
			pivotInverseMatrix.columns = columns;
		}else{
			for (int c = 0; c < columns ; c++){
				for (int r = 0 ; r < rows ; r++){
					pivotInverseMatrix.matrixRes[c*rows + r] = 0;
				}
				pivotInverseMatrix.matrixRes[c*rows + c] = 1;
			}
		}
		
		updatePivotMatrix();
		pivot(pivotMatrix, pivotInverseMatrix);


		return inverse;

	}

	// /////////////////////////////////////////////////:
	// more linear operations
	/**
	 * returns ret that makes this * ret = v
	 * 
	 * @param v
	 *            vector
	 * @return solving vector
	 */
	public Coords solve(Coords v) {
		
		Coords sol = new Coords(v.getLength());
		pivot(sol, v);
		return sol;
		
	}
	
	
	static final public void solve(double[] sol, Coords res, Coords... columns){
		
		int size = res.getLength();
		
		double[][] matrix = new double[size][];
		for (int i = 0 ; i < size ; i++){
			matrix[i] = new double[size];
			columns[i].copy(matrix[i]);
		}

		PivotSolRes pivotSolRes = new PivotSolRes();
		pivotSolRes.res = new double[size];
		res.copy(pivotSolRes.res);
		
		pivotSolRes.sol = sol;
		
		pivot(matrix, pivotSolRes);
		
	}
	
	
	private interface PivotInterface {
		/**
		 * divide first value for last pivot step
		 * @param index index for last pivot step
		 * @param factor factor to divide
		 */
		public void divideFirst(int index, double factor);
		
		/**
		 * divide res value at step
		 * @param step step index
		 * @param value value to divide
		 */
		public void divideRes(int step, double value);
		
		/**
		 * sub value at step to value at l, multiplied by coef
		 * @param l line where sub is done
		 * @param step line to sub
		 * @param coef multiply factor
		 */
		public void subRes(int l, int step, double coef);

		/**
		 * calc sol at this index
		 * @param index index
		 * @param step step
		 * @param matrix pivot matrix
		 * @param stack column to compute
		 */
		public void calcSol(int index, int step, double[][] matrix, ArrayList<Integer> stack);
	}
	
	static private class PivotSolRes implements PivotInterface {
		
		/**
		 * solution vector
		 */
		public double[] sol;
		
		/**
		 * result vector
		 */
		public double[] res;
		
		public PivotSolRes(){
			
		}
		
		public void divideFirst(int index, double factor){
			sol[index] = res[0] / factor;
		}
		
		public void divideRes(int step, double value){
			res[step] /= value;
		}
		
		public void subRes(int l, int step, double coef){
			res[l] -= coef * res[step];
		}

		public void calcSol(int index, int step, double[][] matrix, ArrayList<Integer> stack){
			double s = res[step]; // value at (step, index) is 1
			for (int i : stack){
				s -= matrix[i][step] * sol[i]; // sub for non-zero matrix coeffs
			}
			sol[index] = s;	
		}
		
	}
	
	static private class PivotInverseMatrix implements PivotInterface {
		
		public int columns;
		
		public double[] matrixRes;
		
		public Coords[] inverse;

		public PivotInverseMatrix() {

		}

		public void divideFirst(int index, double factor){
			for (int i = 0 ; i < columns ; i++){
				inverse[i].set(index+1, matrixRes[i*columns] / factor);
				//System.out.println(inverse[index + i*columns] +" , "+ matrixRes[i*columns]);
			}
		}
		
		public void divideRes(int step, double value){
			for (int i = 0 ; i < columns ; i++){
				matrixRes[step + i*columns] /= value;
			}
		}
		
		public void subRes(int l, int step, double coef){
			for (int i = 0 ; i < columns ; i++){
				matrixRes[l + i*columns] -= coef * matrixRes[step + i*columns];
			}
		}

		public void calcSol(int index, int step, double[][] matrix, ArrayList<Integer> stack){
			for (int j = 0 ; j < columns ; j++){
				double s = matrixRes[step + j*columns]; // value at (step, index) is 1

				for (int i : stack){
					s -= matrix[i][step] * inverse[j].get(i+1); // sub for non-zero matrix coeffs
				}
				inverse[j].set(index+1, s);	
			}
		}

	}
	
	private PivotSolRes pivotSolRes;
	
	private PivotInverseMatrix pivotInverseMatrix;
	
	
	private double[][] pivotMatrix;
	
	private void updatePivotMatrix(){
		if (pivotMatrix == null){
			pivotMatrix = new double[columns][];
		}		
		for (int c = 0 ; c < columns ; c++){
			pivotMatrix[c] = new double[rows];
			for (int r = 0 ; r < rows ; r++){
				pivotMatrix[c][r] = get(r+1, c+1);
			}
		}
	}

	/**
	 * makes Gauss pivot about this matrix 
	 * and compute sol so that this * sol = ret
	 * @param sol solution
	 * @param res result
	 */
	public void pivot(Coords sol, Coords res){
		
		updatePivotMatrix();
		
		if (pivotSolRes == null){
			pivotSolRes = new PivotSolRes();
		}
		pivotSolRes.res = new double[res.getLength()];
		for (int r = 0 ; r < rows ; r++){
			pivotSolRes.res[r] = res.val[r];
		}
		
		pivotSolRes.sol = sol.val;
		
		pivot(pivotMatrix, pivotSolRes);
	}
	
	/**
	 * makes Gauss pivot about the matrix 
	 * and compute sol so that matrix * sol = ret
	 * @param matrix array of columns
	 * @param psr pivot solution-result
	 */
	static final public void pivot(double[][] matrix, PivotInterface psr){
		int size = matrix.length;
		ArrayList<Integer> stack = new ArrayList<Integer>();
		for (int i = size - 1 ; i >= 0 ; i--){
			stack.add(i);
		}
		pivot(matrix, psr, size - 1, stack);
	}
	/**
	 * one step Gauss pivot 
	 *
	 */
	static final private void pivot(double[][] matrix, PivotInterface psr, 
			final int step, ArrayList<Integer> stack){
		
		// last step
		if (step == 0){
			int index = stack.get(0);
			psr.divideFirst(index, matrix[index][0]);
		
		}else{
			// look for the biggest value at step line
			int stackIndex = 0;
			int index = stack.get(0);
			double value = matrix[index][step];
			for (int currentStackIndex = 1 ; currentStackIndex < stack.size() ; currentStackIndex++){
				int currentIndex = stack.get(currentStackIndex);
				double currentValue = matrix[currentIndex][step];
				if (Math.abs(currentValue) > Math.abs(value)){
					stackIndex = currentStackIndex;
					index = currentIndex;
					value = currentValue;					
				}
			}
			
			
			// divide step line by value in matrix and res
			for (int i : stack){
				matrix[i][step] /= value;
			}
			psr.divideRes(step, value);
			
			// sub step line in each line above
			for (int l = 0 ; l < step ; l++){
				double coef = matrix[index][l];
				for (int i : stack){
					matrix[i][l] -= coef * matrix[i][step];
				}
				psr.subRes(l, step, coef);
			}
			
			
			// remove current index and apply pivot at next step
			stack.remove(stackIndex);
			pivot(matrix, psr, step - 1, stack);
			
			// calc sol at this index
			psr.calcSol(index, step, matrix, stack);
			
			// re-add current index for pivot caller
			stack.add(index);
			
		}
		
	}
	
	
	
	

	/*
	 * returns whether the matrix is singular, eg after an inverse
	 */
	/**
	 * @return true if the matrix is singular
	 */
	public boolean isSingular() {
		return Double.isNaN(vectors[0].get(1));
	}

	/**
	 * sets if the matrix is singular
	 * 
	 * @param isSingular
	 */
	public void setIsSingular(boolean isSingular) {
		vectors[0].set(1, Double.NaN);
	}

	// /////////////////////////////////////////////////
	// SETTERS AND GETTERS

	/**
	 * return origin of the matrix
	 * 
	 * @return origin
	 */
	public Coords getOrigin() {
		return getColumn(getColumns());
	}

	/**
	 * return "x-axis" vector
	 * 
	 * @return "x-axis" vector
	 */
	public Coords getVx() {
		return getColumn(1);
	}

	/**
	 * return "y-axis" vector
	 * 
	 * @return "y-axis" vector
	 */
	public Coords getVy() {
		return getColumn(2);
	}

	/**
	 * return "z-axis" vector
	 * 
	 * @return "z-axis" vector
	 */
	public Coords getVz() {
		return getColumn(3);
	}

	/**
	 * set origin of the matrix
	 * 
	 * @param v
	 *            origin
	 */
	public void setOrigin(Coords v) {
		set(v, getColumns());
	}
	
	/**
	 * add vector values to origin
	 * @param v vector
	 */
	public void addToOrigin(Coords v){
		addToColumn(v, getColumns());
	}
	
	/**
	 * sub vector values to origin
	 * @param v vector
	 */
	public void subToOrigin(Coords v){
		subToColumn(v, getColumns());
	}
	
	/**
	 * add vector values to vx
	 * @param v vector
	 */
	public void addToVx(Coords v){
		addToColumn(v, 1);
	}
	
	/**
	 * add vector values to vy
	 * @param v vector
	 */
	public void addToVy(Coords v){
		addToColumn(v, 2);
	}
	
	/**
	 * add vector values to vz
	 * @param v vector
	 */
	public void addToVz(Coords v){
		addToColumn(v, 3);
	}
	
	/**
	 * add vector values to column j
	 * @param v vector
	 * @param j column
	 */
	public void addToColumn(Coords v, int j){
		for (int i = 1 ; i <= v.getLength() ; i++){
			set(i, j, get(i, j) + v.get(i));
		}
	}

	/**
	 * sub vector values to column j
	 * @param v vector
	 * @param j column
	 */
	public void subToColumn(Coords v, int j){
		for (int i = 1 ; i <= v.getLength() ; i++){
			set(i, j, get(i, j) - v.get(i));
		}
	}

	/**
	 * multiply column j by v
	 * @param v value
	 * @param j column
	 */
	public void mulColumn(double v, int j){
		for (int i = 1 ; i <= getRows() ; i++){
			set(i, j, get(i, j) * v);
		}
	}
	
	/**
	 * multiply origin column by v
	 * @param v value
	 */
	public void mulOrigin(double v){
		mulColumn(v,getColumns());
	}

	/**
	 * return "x-axis" vector
	 * 
	 * @param v
	 *            "x-axis" vector
	 */
	public void setVx(Coords v) {
		set(v, 1);
	}

	/**
	 * return "y-axis" vector
	 * 
	 * @param v
	 *            "y-axis" vector
	 */
	public void setVy(Coords v) {
		set(v, 2);
	}

	/**
	 * return "z-axis" vector
	 * 
	 * @param v
	 *            "z-axis" vector
	 */
	public void setVz(Coords v) {
		set(v, 3);
	}

	
	
	/**
	 * 
	 * set values in openGL format
	 */
	public void getForGL(float[] val){

		int index = 0;
		for (int x = 0 ; x < columns ; x++){
			for (int y = 0 ; y < rows ; y++){
				val[index] = (float) get(y+1, x+1);
				index++;
			}
		
		}
		
	}
	
	
	// /////////////////////////////////////////////////:
	// testing the package
	/**
	 * testing the package
	 * 
	 * @param args
	 */
	public static synchronized void main(String[] args) {

		/*
		CoordMatrix m1 = CoordMatrix.Identity(3);
		m1.set(1, 2, 5.0);
		m1.set(3, 1, 4.0);
		m1.set(3, 2, 3.0);
		m1.transpose();
		App.debug("m1");
		m1.SystemPrint();

		CoordMatrix m2 = new CoordMatrix(3, 4);
		m2.set(1, 1, 1.0);
		m2.set(2, 2, 2.0);
		m2.set(3, 3, 3.0);
		m2.set(1, 4, 4.0);
		m2.set(2, 4, 3.0);
		m2.set(3, 4, 1.0);
		m2.set(3, 2, -1.0);
		App.debug("m2");
		m2.SystemPrint();

		CoordMatrix m4 = m1.add(m2);
		App.debug("m4");
		m4.SystemPrint();

		CoordMatrix m5 = m1.mul(m2);
		App.debug("m5");
		m5.SystemPrint();

		App.debug("subMatrix");
		m5.subMatrix(2, 3).SystemPrint();

		m1.set(1, 2, -2.0);
		m1.set(3, 1, -9.0);
		m1.set(3, 2, -8.0);
		App.debug("m1");
		m1.SystemPrint();
		App.debug("det m1 = " + m1.det());

		App.debug("inverse");
		CoordMatrix m4inv = m4.inverse();
		m4inv.SystemPrint();
		m4.mul(m4inv).SystemPrint();
		m4inv.mul(m4).SystemPrint();
		
		*/
		
		/*
		double[][] matrix = {
				{1, 0, 0, 0},
				{1, 1, 0, 0},
				{0, 0, 1, 0},
				{0, 0, 0, 1}
		};
		
		double[] res = {2, 3, 4, 1};
		
		double[] sol = new double[4];
		
		pivot(matrix, sol, res);
		
		String s = "==== SOL ====\n";
		for (int i = 0 ; i < sol.length ; i++){
			s+=sol[i]+", ";
		}
		System.out.println(s);
		*/
		
		CoordMatrix matrix = new CoordMatrix4x4();
		matrix.setVx(new Coords(1, 1, -1, 0));
		matrix.setVy(new Coords(-1, 1, -1, 0));
		matrix.setVz(new Coords(1, 2, 5, 0));
		matrix.setOrigin(new Coords(4, 5, 6, 1));
				
		System.out.println("==== MATRIX ====\n"+matrix.toString());
		
		System.out.println("==== INVERSE ====\n"+matrix.inverse().toString());
		
		//matrix.inverse = new CoordMatrix(4, 4);
		matrix.pivotInverseMatrix = new PivotInverseMatrix();
		matrix.pivotInverseMatrix.matrixRes = new double[4*4];
		for (int c = 0; c < 4 ; c++){
			matrix.pivotInverseMatrix.matrixRes[c*4 + c] = 1;
		}
		matrix.pivotInverseMatrix.inverse = matrix.inverse.vectors;
		matrix.pivotInverseMatrix.columns = matrix.getColumns();
		double[][] matrixD = new double[matrix.columns][];
		for (int c = 0 ; c < matrix.columns ; c++){
			matrixD[c] = new double[matrix.rows];
			for (int r = 0 ; r < matrix.rows ; r++){
				matrixD[c][r] = matrix.get(r+1, c+1);
			}
		}
		pivot(matrixD, matrix.pivotInverseMatrix);
		
		System.out.println("==== PIVOT INVERSE ====\n"+matrix.inverse.toString());
		
		System.out.println("==== MATRIX * INVERSE ====\n"+matrix.mul(matrix.inverse).toString());
		
		/*
		matrix.pivot(sol, res);
		
		System.out.println("==== SOL ====\n"+sol.toString());
		
		System.out.println("==== SOL INV ====\n"+matrix.solve(res).toString());
		
		
		System.out.println("==== VERIF ====\n"+matrix.mul(sol).toString());
		
		
		long time = System.currentTimeMillis();
		int loop = 10000;
		for (int i = 0 ; i < loop ; i++){
			Coords ret = matrix.solve(res);
		}
		long delay1 = System.currentTimeMillis() - time;
		System.out.println("==== matrix.solve : "+delay1);
		
		time = System.currentTimeMillis();
		for (int i = 0 ; i < loop ; i++){
			Coords ret = new Coords(4);
			matrix.pivot(ret, res);
		}
		long delay2 = System.currentTimeMillis() - time;
		System.out.println("==== matrix.pivot : "+delay2);
		System.out.println("=========== ratio : "+(delay1/delay2));
		*/
	}

}