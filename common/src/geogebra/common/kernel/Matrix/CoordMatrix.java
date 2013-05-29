/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.Matrix;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;

/**
 * Simple matrix description with basic linear algebra methods.
 * 
 * @author ggb3D
 * 
 */
public class CoordMatrix {

	/**
	 * the 2x2 matrix represented by val = {1,2,3,4} is <br>
	 * <code>
	 * | 1  3 |  <br>
	 * | 2  4 |
	 * </code>
	 */
	protected double[] val;

	/** number of rows of the matrix */
	protected int rows;
	/** number of columns of the matrix */
	protected int columns;
	/** says if the matrix is transposed or not */
	protected boolean transpose = false; // transposing the matrix is logical
											// operation

	/** says if the matrix is singular or not */
	private boolean isSingular = false;

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
	 * creates a GgbMatrix or a GgbVector if a_columns==1
	 * 
	 * @param a_rows
	 *            number of rows
	 * @param a_columns
	 *            number of columns
	 * @return a_rows*a_columns matrix (or vector)
	 */
	static final public CoordMatrix GgbMatrixOrVector(int a_rows, int a_columns) {
		if (a_columns == 1) {
			return new Coords(a_rows);
		}
		return new CoordMatrix(a_rows, a_columns);
	}

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
		this.val = val;
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
		setIsSingular(false);

		this.rows = rows;
		this.columns = columns;
		transpose = false;

		val = new double[columns * rows];
		for (int i = 0; i < columns * rows; i++) {
			val[i] = 0.0;
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
	 * @return matrix
	 */	
	public static final CoordMatrix Rotation3x3(double angle) {
		
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		CoordMatrix m = new CoordMatrix(3, 3);
		m.set(1,1, cos); m.set(1,2, -sin);
		m.set(2,1, sin); m.set(2,2,  cos);
		m.set(3,3, 1);
		
		return m;
	}
	
	
	/**
	 * 3x3 rotation matrix around vector
	 * @param u vector of rotation
	 * @param angle angle of rotation
	 * @return matrix
	 */
	public static final CoordMatrix Rotation3x3(Coords u, double angle) {
		
		double ux = u.getX();
		double uy = u.getY();
		double uz = u.getZ();
		
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		
		double[] vals = new double[9];
		vals[0] = ux*ux*(1-c) + c;
		vals[1] = ux*uy*(1-c) + uz*s;
		vals[2] = ux*uz*(1-c) - uy*s;
		
		vals[3] = ux*uy*(1-c) - uz*s;
		vals[4] = uy*uy*(1-c) + c;
		vals[5] = uy*uz*(1-c) + ux*s;
		
		vals[6] = ux*uz*(1-c) + uy*s;
		vals[7] = uy*uz*(1-c) - ux*s;
		vals[8] = uz*uz*(1-c) + c;
		
		return new CoordMatrix(3, 3, vals);

	}

	// /////////////////////////////////////////////////:
	// setters and getters

	/**
	 * returns double[] describing the matrix for openGL
	 * 
	 * @return the matrix as a double[]
	 */
	public double[] get() {

		return val;
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
		if (transpose) {
			return val[(i - 1) * rows + (j - 1)];
		}
		return val[(j - 1) * rows + (i - 1)];
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

		Coords ret = new Coords(getRows());
		for (int i = 1; i <= getRows(); i++) {
			ret.set(i, get(i, j));
		}

		return ret;

	}

	/**
	 * returns GgbMatrix as a GeoList eg { {1,2}, {3,4} }
	 * 
	 * @param outputList
	 * @param cons
	 * @return eg { {1,2}, {3,4} }
	 */
	public GeoList getGeoList(GeoList outputList, Construction cons) {

		if (isSingular) {
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
		int i;
		for (i = 1; i <= V.getLength(); i++) {
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
		if (transpose) {
			val[(i - 1) * rows + (j - 1)] = val0;
		} else {
			// Application.debug("i="+i+",j="+j+",rows="+rows);
			val[(j - 1) * rows + (i - 1)] = val0;
		}
	}

	/**
	 * sets all values to val0
	 * 
	 * @param val0
	 *            value
	 */
	public void set(double val0) {

		for (int i = 0; i < columns * rows; i++) {
			val[i] = val0;
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

		if (!transpose) {
			return rows;
		}
		return columns;
	}

	/**
	 * returns number of columns
	 * 
	 * @return number of columns
	 */
	public int getColumns() {

		if (!transpose) {
			return columns;
		}
		return rows;
	}

	/**
	 * transpose the copy (logically)
	 * 
	 * @return true if the resulting matrix is transposed
	 */
	public boolean transpose() {

		transpose = !transpose;
		return transpose;
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
	 * returns a transposed copy of the matrix
	 * 
	 * @return transposed copy of the matrix
	 */
	public CoordMatrix transposeCopy() {

		this.transpose();
		CoordMatrix result = this.copy();
		this.transpose();

		return result;

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

		boolean result = true;

		for (int i = 0; (i < columns * rows) && (result); i++) {
			result = result && (!Double.isNaN(val[i]));
		}

		return result;
	}

	/** @return false if at least one value is infinite */
	public boolean isFinite() {

		boolean result = true;

		for (int i = 0; (i < columns * rows) && (result); i++) {
			result = result && (!Double.isInfinite(val[i]));
		}

		return result;
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

		CoordMatrix result = GgbMatrixOrVector(getRows(), getColumns());

		for (int i = 1; i <= result.getRows(); i++) {
			for (int j = 1; j <= result.getColumns(); j++) {
				result.set(i, j, val0 * get(i, j));
			}
		}

		return result;
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

		CoordMatrix result = GgbMatrixOrVector(getRows(), getColumns());
		// resulting matrix has the same dimension than this
		// and is a GgbVector if this has 1 column

		for (int i = 1; i <= result.getRows(); i++) {
			for (int j = 1; j <= result.getColumns(); j++) {
				result.set(i, j, get(i, j) + m.get(i, j));
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

		for (int i = 1; i <= result.getRows(); i++) {

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
	protected void mul(CoordMatrix m, CoordMatrix result) {

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

	/**
	 * returns inverse matrix (2x2 or larger). You must check with isSquare()
	 * before calling this
	 * 
	 * @return inverse matrix
	 * */
	public CoordMatrix inverse() {

		CoordMatrix ret = new CoordMatrix(getRows(), getColumns());

		double d = this.det();

		if (Kernel.isEqual(d, 0.0, Kernel.STANDARD_PRECISION)) {
			ret.setIsSingular(true);
			return ret;
		}

		double signe_i = 1.0;
		for (int i = 1; i <= getRows(); i++) {
			double signe = signe_i;
			for (int j = 1; j <= getColumns(); j++) {
				ret.set(i, j, (subMatrix(j, i).det()) * signe / d);
				signe = -signe;
			}
			signe_i = -signe_i;
		}

		return ret;

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
		// GgbVector ret;
		CoordMatrix mInv = this.inverse(); // TODO: use gauss pivot to optimize
		return mInv.mul(v);
	}

	/*
	 * returns whether the matrix is singular, eg after an inverse
	 */
	/**
	 * @return true if the matrix is singular
	 */
	public boolean isSingular() {
		return isSingular;
	}

	/**
	 * sets if the matrix is singular
	 * 
	 * @param isSingular
	 */
	public void setIsSingular(boolean isSingular) {
		this.isSingular = isSingular;
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

	// /////////////////////////////////////////////////:
	// testing the package
	/**
	 * testing the package
	 * 
	 * @param args
	 */
	public static synchronized void main(String[] args) {

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
	}

}