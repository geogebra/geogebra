/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.matrix;

import java.util.ArrayList;

import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

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
	// public double[] val;
	protected final Coords[] vectors;

	/** number of rows of the matrix */
	protected int rows;
	/** number of columns of the matrix */
	protected int columns;
	private CoordMatrix inverse;

	private PivotSolRes pivotSolRes;

	private PivotSolResDegenerate pivotSolResDegenerate;

	private PivotInverseMatrix pivotInverseMatrix;

	private double[][] pivotMatrix;

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
		for (int j = 0; j < columns; j++) {
			vectors[j] = new Coords(rows);
			for (int i = 0; i < rows; i++) {
				vectors[j].set(i + 1, val[j * rows + i]);
			}
		}
	}

	/**
	 * create matrix composed with vectors
	 * 
	 * @param vectors
	 *            vectors
	 */
	public CoordMatrix(Coords... vectors) {
		this.vectors = vectors;
		this.rows = vectors[0].getLength();
		this.columns = vectors.length;
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
		vectors = new Coords[columns];
		initialise(rows, columns);

	}

	/**
	 * init the matrix (all values to 0)
	 * 
	 * @param r
	 *            number of rows
	 * @param c
	 *            number of columns
	 */
	private void initialise(int r, int c) {
		this.rows = r;
		this.columns = c;

		for (int i = 0; i < c; i++) {
			vectors[i] = new Coords(r);
		}

	}

	/**
	 * returns n*n identity matrix
	 * 
	 * @param n
	 *            dimension
	 * @return the identity matrix
	 */
	public static final CoordMatrix identity(int n) {
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
	public static final CoordMatrix scaleMatrix(Coords v) {
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
	 *            values on diagonal (determines dimension)
	 * @return diagonal matrix
	 */
	public static final CoordMatrix diagonalMatrix(double[] vals) {
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
	public static final CoordMatrix translationMatrix(Coords v) {
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
	public static final CoordMatrix rotation3DMatrix(int axe, double angle) {
		CoordMatrix m = new CoordMatrix(4, 4);
        setRotation3DMatrix(axe, angle, m);
		return m;

	}

    /**
     * returns 3d rotation homogenic matrix, dim 4x4
     *
     * @param axe
     *            axis of rotation
     * @param angle
     *            angle of rotation
     */
    public static final void setRotation3DMatrix(int axe, double angle, CoordMatrix m) {
        m.set(0);
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
    }

	/**
	 * 3x3 rotation matrix around oz
	 * 
	 * @param angle
	 *            angle of rotation
	 * @param m
	 *            output matrix
	 */
	public static final void rotation3x3(double angle, CoordMatrix m) {
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		m.set(1, 1, cos);
		m.set(1, 2, -sin);
		m.set(2, 1, sin);
		m.set(2, 2, cos);
		m.set(3, 3, 1);
	}

	/**
	 * 3x3 rotation matrix around vector
	 * 
	 * @param u
	 *            vector of rotation
	 * @param angle
	 *            angle of rotation
	 * @param m
	 *            output matrix
	 */
	public static final void rotation3x3(Coords u, double angle,
			CoordMatrix m) {

		double ux = u.getX();
		double uy = u.getY();
		double uz = u.getZ();

		double c = Math.cos(angle);
		double s = Math.sin(angle);

		Coords[] vectors = m.vectors;
		vectors[0].setX(ux * ux * (1 - c) + c);
		vectors[0].setY(ux * uy * (1 - c) + uz * s);
		vectors[0].setZ(ux * uz * (1 - c) - uy * s);

		vectors[1].setX(ux * uy * (1 - c) - uz * s);
		vectors[1].setY(uy * uy * (1 - c) + c);
		vectors[1].setZ(uy * uz * (1 - c) + ux * s);

		vectors[2].setX(ux * uz * (1 - c) + uy * s);
		vectors[2].setY(uy * uz * (1 - c) - ux * s);
		vectors[2].setZ(uz * uz * (1 - c) + c);
	}

	// /////////////////////////////////////////////////:
	// setters and getters

	/**
	 * set double[] describing the matrix for openGL
	 * 
	 * @param val
	 *            values set
	 * 
	 */
	public void get(double[] val) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				val[i + j * rows] = get(i + 1, j + 1);
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
		return vectors[j - 1].get(i);
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

		return vectors[j - 1];

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
		vectors[j - 1].set(i, val0);
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
	 * transpose all values of m
	 * 
	 * @param m
	 *            source matrix
	 */
	public void setTranspose(CoordMatrix m) {
		for (int i = 1; i <= m.getRows(); i++) {
			for (int j = 1; j <= m.getColumns(); j++) {
				this.set(i, j, m.get(j, i));
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
	 * 
	 * @param result
	 *            matrix
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
	 * 
	 * @param result
	 *            matrix
	 */
	public void transposeCopy(CoordMatrix result) {
		for (int i = 1; i <= result.getRows(); i++) {
			for (int j = 1; j <= result.getColumns(); j++) {
				result.set(i, j, get(j, i));
			}
		}

	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();

		for (int i = 1; i <= getRows(); i++) {

			for (int j = 1; j <= getColumns(); j++) {
				double v = get(i, j);
				if (DoubleUtil.isZero(v)) {
					v = 0;
				}
				s.append("  ");
				s.append(v);
			}
			s.append('\n');
		}

		return s.toString();
	}

    /**
     *
     * @param digits
     *            digits length
     * @param precision
     *            decimal precision
     * @return string representation with +/-XXXX for too large values
     */
    public String toString(int digits, int precision) {
        StringBuilder s = new StringBuilder();
        for (int i = 1; i <= getRows(); i++) {
            for (int j = 1; j <= getColumns(); j++) {
                s.append("  ");
                StringUtil.toString(get(i, j), digits, precision, s);
            }
            s.append('\n');
        }
        return s.toString();
    }

	/**
	 * returns false if one value equals NaN
	 * 
	 * @return false if one value equals NaN
	 */
	public boolean isDefined() {
		for (int i = 0; i < columns; i++) {
			if (!vectors[i].isDefined()) {
				return false;
			}
		}

		return true;
	}

	/** @return false if at least one value is infinite */
	public boolean isFinite() {
		for (int i = 0; i < columns; i++) {
			if (!vectors[i].isFinite()) {
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
	 * 
	 * @param v
	 *            factor
	 */
	public void mulInside(double v) {
		for (int i = 0; i < columns; i++) {
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
	 * returns this + m, perform addition only on m existing values (leave other
	 * unchanged)
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
	 * 
	 *         deprecated create result vector and use
	 *         {@link Coords#setMul(CoordMatrix, Coords)} instead
	 */

	public Coords mul(Coords v) {

		Coords result = new Coords(getRows());

		return result.setMul(this, v);
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
				for (int n = 1; n <= getColumns(); n++) {
					r += get(i, n) * m.get(n, j);
				}

				result.set(i, j, r);
			}
		}
	}

	/**
	 * set this to m1 * m2
	 * 
	 * @param m1
	 *            first matrix
	 * @param m2
	 *            second matrix
	 * @return this
	 */
	public CoordMatrix setMul(CoordMatrix m1, CoordMatrix m2) {

		for (int i = 1; i <= getRows(); i++) {
			for (int j = 1; j <= getColumns(); j++) {

				double r = 0;
				for (int n = 1; n <= m1.getColumns(); n++) {
					r += m1.get(i, n) * m2.get(n, j);
				}

				set(i, j, r);
			}
		}

		return this;
	}

	/**
	 * set this to m1 * m2, with multiplying only 3x3 interior matrix
	 * 
	 * @param m1
	 *            first matrix
	 * @param m2
	 *            second matrix
	 * @return this
	 */
	public CoordMatrix setMul3x3(CoordMatrix m1, CoordMatrix m2) {
		for (int i = 1; i <= 3; i++) {
			for (int j = 1; j <= 3; j++) {
				double r = 0;
				for (int n = 1; n <= 3; n++) {
					r += m1.get(i, n) * m2.get(n, j);
				}
				set(i, j, r);
			}
		}
		return this;
	}

	/**
	 * set this to transpose(m1) * m2
	 * 
	 * @param m1
	 *            first matrix
	 * @param m2
	 *            second matrix
	 * @return this
	 */
	public CoordMatrix setMulT1(CoordMatrix m1, CoordMatrix m2) {

		for (int i = 1; i <= getRows(); i++) {
			for (int j = 1; j <= getColumns(); j++) {

				double r = 0;
				for (int n = 1; n <= m1.getRows(); n++) {
					r += m1.get(n, i) * m2.get(n, j);
				}

				set(i, j, r);
			}
		}

		return this;
	}

	/**
	 * 
	 * @param m
	 *            matrix
	 * @return 4x4 matrix with multiplication made only in 3x3 up-left submatrix
	 */
	protected CoordMatrix4x4 mul3x3(CoordMatrix m) {

		CoordMatrix4x4 result = new CoordMatrix4x4();

		for (int i = 1; i <= 3; i++) {
			for (int j = 1; j <= 3; j++) {

				double r = 0;
				for (int n = 1; n <= 3; n++) {
					r += get(i, n) * m.get(n, j);
				}

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
		if (isSingular()) {
			return false;
		}
		return getRows() == getColumns();
	}

	/**
	 * returns inverse matrix (2x2 or larger). You must check with isSquare()
	 * before calling this
	 * 
	 * @return inverse matrix
	 */
	public CoordMatrix inverse() {

		if (inverse == null) {
			inverse = new CoordMatrix(getRows(), getColumns());
		}

		if (pivotInverseMatrix == null) {
			pivotInverseMatrix = new PivotInverseMatrix();
			pivotInverseMatrix.matrixRes = new double[columns * columns];
			for (int c = 0; c < columns; c++) {
				pivotInverseMatrix.matrixRes[c * rows + c] = 1;
			}
			pivotInverseMatrix.inverse = inverse.vectors;
			pivotInverseMatrix.columns = columns;
		} else {
			for (int c = 0; c < columns; c++) {
				for (int r = 0; r < rows; r++) {
					pivotInverseMatrix.matrixRes[c * rows + r] = 0;
				}
				pivotInverseMatrix.matrixRes[c * rows + c] = 1;
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
	 * 
	 *         deprecated create result and use {@link #solve(Coords, Coords)}
	 *         instead
	 */

	public Coords solve(Coords v) {

		Coords sol = new Coords(v.getLength());
		pivot(sol, v);
		return sol;

	}

	/**
	 * returns sol that makes this * sol = v
	 * 
	 * @param v
	 *            vector
	 * @param sol
	 *            sol vector
	 * @return solving vector
	 */
	public Coords solve(Coords v, Coords sol) {

		pivot(sol, v);
		return sol;

	}

	/**
	 * @param matrixForSolve
	 *            matrix used for solving
	 * @param sol
	 *            solution vector
	 * @param res
	 *            result vector
	 * @param columns
	 *            matrix columns
	 */
	static synchronized final public void solve(double[][] matrixForSolve,
			double[] sol, Coords res,
			Coords... columns) {

		int size = res.getLength();

		for (int i = 0; i < size; i++) {
			columns[i].copy(matrixForSolve[i]);
		}

		PivotSolRes pivotSolRes = new PivotSolRes();
		pivotSolRes.res = new double[size];
		res.copy(pivotSolRes.res);

		pivotSolRes.sol = sol;

		pivot(matrixForSolve, pivotSolRes);

	}

	static abstract private class PivotAbstract {
		protected PivotAbstract() {
			//
		}

		/**
		 * divide first value for last pivot step
		 * 
		 * @param index
		 *            index for last pivot step
		 * @param factor
		 *            factor to divide
		 */
		abstract public void divideFirst(int index, double factor);

		/**
		 * perform the last pivot step
		 * 
		 * @param stack
		 *            stack
		 * @param matrix
		 *            matrix
		 */
		public void lastStep(ArrayList<Integer> stack, double[][] matrix) {
			int index = stack.get(0);
			divideFirst(index, matrix[index][0]);
		}

		/**
		 * divide res value at step
		 * 
		 * @param step
		 *            step index
		 * @param value
		 *            value to divide
		 */
		abstract public void divideRes(int step, double value);

		/**
		 * sub value at step to value at l, multiplied by coef
		 * 
		 * @param l
		 *            line where sub is done
		 * @param step
		 *            line to sub
		 * @param coef
		 *            multiply factor
		 */
		abstract public void subRes(int l, int step, double coef);

		/**
		 * calc sol at this index
		 * 
		 * @param index
		 *            index
		 * @param step
		 *            step
		 * @param matrix
		 *            pivot matrix
		 * @param stack
		 *            column to compute
		 * @param value
		 *            TODO
		 */
		abstract public void calcSol(int index, int step, double[][] matrix,
				ArrayList<Integer> stack, double value);

		public void divideAndSub(double[][] matrix, ArrayList<Integer> stack,
				int step, int index, double value) {

			// divide step line by value in matrix and res
			for (int i : stack) {
				matrix[i][step] /= value;
			}
			divideRes(step, value);

			// sub step line in each line above
			for (int l = 0; l < step; l++) {
				double coef = matrix[index][l];
				for (int i : stack) {
					matrix[i][l] -= coef * matrix[i][step];
				}
				subRes(l, step, coef);
			}

		}

		/**
		 * handle all-zeros step in matrix
		 * 
		 * @param value
		 *            value
		 * @param step
		 *            step
		 * @return true if value is zero
		 */
		public boolean handleZeroValue(double value, int step) {
			return false;
		}
	}

	static private class PivotSolResDegenerate extends PivotSolRes {
		private boolean[] nonZeroIndices;

		protected PivotSolResDegenerate() {
		}

		@Override
		public void divideAndSub(double[][] matrix, ArrayList<Integer> stack,
				int step, int index, double value) {

			if (DoubleUtil.isZero(value)) {
				// no non-zero value at this step: pass
				return;
			}

			super.divideAndSub(matrix, stack, step, index, value);

		}

		/**
		 * factor == 0, we need res == 0
		 * 
		 * @param index
		 *            factor index
		 */
		private void divideFirst0(int index) {

			if (DoubleUtil.isZero(res[0])) {
				sol[index] = 1; // arbitrary non-zero value
			} else {
				sol[index] = Double.NaN; // not possible
			}

		}

		@Override
		public void lastStep(ArrayList<Integer> stack, double[][] matrix) {
			// String str = "\n++++++++++++ last step : ";
			// for (int i : stack) {
			// str += i + ", ";
			// }
			// Log.debug(str);
			int index0 = 0;
			for (int index : stack) {
				double factor = matrix[index][0];
				if (!DoubleUtil.isZero(factor)) {
					divideFirst(index, factor);
					nonZeroIndices[index] = true;
					manageZeroSteps(); // set sol = 1 for zero steps
					return;
				}
				index0 = index;
			}
			divideFirst0(index0);
			manageZeroSteps(); // set sol = 1 for zero steps
		}

		@Override
		public void calcSol(int index, int step, double[][] matrix,
				ArrayList<Integer> stack, double value) {
			double s = res[step]; // value at (step, index) is 1
			if (DoubleUtil.isZero(value)) {
				if (DoubleUtil.isZero(s)) {
					s = 1; // arbitrary non-zero value
				} else {
					s = Double.NaN; // not possible
				}
			} else {
				nonZeroIndices[index] = true;
			}

			// String str = "\n---- calcSol\nvalue = " + value + "\nstep = "
			// + step + "\nindex = " + index + "\ns = " + s + "\nstack = ";

			for (int i : stack) {
				s -= matrix[i][step] * sol[i]; // sub for non-zero matrix coeffs
				// str += i + ",";
			}
			sol[index] = s;

			// str += "\nmatrix=\n";
			// for (int i = 0 ; i < matrix.length;i++){
			// for (int j = 0 ; j < matrix[i].length;j++){
			// str += matrix[i][j] + " ";
			// }
			// str += "\n";
			//
			// }
			//
			// Log.debug(str);

		}

		@Override
		public boolean handleZeroValue(double value, int step) {
			return DoubleUtil.isZero(value);
		}

		public void init(int length) {
			nonZeroIndices = new boolean[length];
		}

		private void manageZeroSteps() {

			for (int i = 0; i < nonZeroIndices.length; i++) {
				if (!nonZeroIndices[i]) {
					if (DoubleUtil.isZero(res[i])) {
						sol[i] = 1; // arbitrary non-zero value
					} else {
						sol[i] = Double.NaN; // not possible
					}
				}
			}

		}

	}

	static private class PivotSolRes extends PivotAbstract {

		/**
		 * solution vector
		 */
		public double[] sol;

		/**
		 * result vector
		 */
		public double[] res;

		protected PivotSolRes() {
			super();
		}

		@Override
		public void divideFirst(int index, double factor) {
			sol[index] = res[0] / factor;
		}

		@Override
		public void divideRes(int step, double value) {
			res[step] /= value;
		}

		@Override
		public void subRes(int l, int step, double coef) {
			res[l] -= coef * res[step];
		}

		@Override
		public void calcSol(int index, int step, double[][] matrix,
				ArrayList<Integer> stack, double value) {
			double s = res[step]; // value at (step, index) is 1
			for (int i : stack) {
				s -= matrix[i][step] * sol[i]; // sub for non-zero matrix coeffs
			}
			sol[index] = s;
		}

	}

	static private class PivotInverseMatrix extends PivotAbstract {

		public int columns;

		public double[] matrixRes;

		public Coords[] inverse;

		protected PivotInverseMatrix() {

		}

		@Override
		public void divideFirst(int index, double factor) {
			for (int i = 0; i < columns; i++) {
				inverse[i].set(index + 1, matrixRes[i * columns] / factor);
			}
		}

		@Override
		public void divideRes(int step, double value) {
			for (int i = 0; i < columns; i++) {
				matrixRes[step + i * columns] /= value;
			}
		}

		@Override
		public void subRes(int l, int step, double coef) {
			for (int i = 0; i < columns; i++) {
				matrixRes[l + i * columns] -= coef
						* matrixRes[step + i * columns];
			}
		}

		@Override
		public void calcSol(int index, int step, double[][] matrix,
				ArrayList<Integer> stack, double value) {
			for (int j = 0; j < columns; j++) {
				double s = matrixRes[step + j * columns]; // value at (step,
															// index) is 1

				for (int i : stack) {
					s -= matrix[i][step] * inverse[j].get(i + 1); // sub for
																	// non-zero
																	// matrix
																	// coeffs
				}
				inverse[j].set(index + 1, s);
			}
		}

	}

	private void updatePivotMatrix() {
		if (pivotMatrix == null) {
			pivotMatrix = new double[columns][];
		}
		for (int c = 0; c < columns; c++) {
			pivotMatrix[c] = new double[rows];
			for (int r = 0; r < rows; r++) {
				pivotMatrix[c][r] = get(r + 1, c + 1);
			}
		}
	}

	/**
	 * makes Gauss pivot about this matrix and compute sol so that this * sol =
	 * ret
	 * 
	 * @param sol
	 *            solution
	 * @param res
	 *            result
	 */
	public void pivot(Coords sol, Coords res) {
		updatePivotMatrix();

		if (pivotSolRes == null) {
			pivotSolRes = new PivotSolRes();
		}
		pivotSolRes.res = new double[res.getLength()];
		for (int r = 0; r < rows; r++) {
			pivotSolRes.res[r] = res.val[r];
		}

		pivotSolRes.sol = sol.val;

		pivot(pivotMatrix, pivotSolRes);
	}

	/**
	 * makes Gauss pivot about this matrix and compute sol so that this * sol =
	 * ret
	 * 
	 * @param sol
	 *            solution
	 * @param res
	 *            result
	 */
	public void pivotDegenerate(Coords sol, Coords res) {
		updatePivotMatrix();

		if (pivotSolResDegenerate == null) {
			pivotSolResDegenerate = new PivotSolResDegenerate();
		}
		pivotSolResDegenerate.init(pivotMatrix.length);
		pivotSolResDegenerate.res = new double[res.getLength()];
		for (int r = 0; r < rows; r++) {
			pivotSolResDegenerate.res[r] = res.val[r];
		}

		pivotSolResDegenerate.sol = sol.val;

		pivot(pivotMatrix, pivotSolResDegenerate);
	}

	/**
	 * makes Gauss pivot about the matrix and compute sol so that matrix * sol =
	 * ret
	 * 
	 * @param matrix
	 *            array of columns
	 * @param psr
	 *            pivot solution-result
	 */
	static final public void pivot(double[][] matrix, PivotAbstract psr) {
		int size = matrix.length;
		ArrayList<Integer> stack = new ArrayList<>();
		for (int i = size - 1; i >= 0; i--) {
			stack.add(i);
		}
		pivot(matrix, psr, size - 1, stack);
		// psr.manageZeroSteps();
	}

	/**
	 * one step Gauss pivot
	 *
	 */
	static final private void pivot(double[][] matrix, PivotAbstract psr,
			final int step, ArrayList<Integer> stack) {

		// Log.debug("XXXXX pivot : step = " + step);

		// last step
		if (step == 0) {
			psr.lastStep(stack, matrix);
		} else {
			// look for the biggest value at step line
			int stackIndex = 0;
			int index = stack.get(0);
			double value = matrix[index][step];
			// Log.debug("index = " + index + " , value = " + value);
			for (int currentStackIndex = 1; currentStackIndex < stack
					.size(); currentStackIndex++) {
				int currentIndex = stack.get(currentStackIndex);
				double currentValue = matrix[currentIndex][step];
				// Log.debug("currentIndex = " + currentIndex
				// + " , currentValue = " + currentValue);
				if (Math.abs(currentValue) > Math.abs(value)) {
					stackIndex = currentStackIndex;
					index = currentIndex;
					value = currentValue;
				}
			}

			if (psr.handleZeroValue(value, step)) {
				// ignore this step
				pivot(matrix, psr, step - 1, stack);
			} else {
				// divide step line by value in matrix and res
				psr.divideAndSub(matrix, stack, step, index, value);

				// remove current index and apply pivot at next step
				stack.remove(stackIndex);
				pivot(matrix, psr, step - 1, stack);

				// calc sol at this index
				psr.calcSol(index, step, matrix, stack, value);

				// re-add current index for pivot caller
				stack.add(index);
			}
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
	 *            ignored (assume true)
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
	 * 
	 * @param v
	 *            vector
	 */
	public void addToOrigin(Coords v) {
		addToColumn(v, getColumns());
	}

	/**
	 * sub vector values to origin
	 * 
	 * @param v
	 *            vector
	 */
	public void subToOrigin(Coords v) {
		subToColumn(v, getColumns());
	}

	/**
	 * add vector values to vx
	 * 
	 * @param v
	 *            vector
	 */
	public void addToVx(Coords v) {
		addToColumn(v, 1);
	}

	/**
	 * add vector values to vy
	 * 
	 * @param v
	 *            vector
	 */
	public void addToVy(Coords v) {
		addToColumn(v, 2);
	}

	/**
	 * add vector values to vz
	 * 
	 * @param v
	 *            vector
	 */
	public void addToVz(Coords v) {
		addToColumn(v, 3);
	}

	/**
	 * add vector values to column j
	 * 
	 * @param v
	 *            vector
	 * @param j
	 *            column
	 */
	public void addToColumn(Coords v, int j) {
		for (int i = 1; i <= v.getLength(); i++) {
			set(i, j, get(i, j) + v.get(i));
		}
	}

	/**
	 * sub vector values to column j
	 * 
	 * @param v
	 *            vector
	 * @param j
	 *            column
	 */
	public void subToColumn(Coords v, int j) {
		for (int i = 1; i <= v.getLength(); i++) {
			set(i, j, get(i, j) - v.get(i));
		}
	}

	/**
	 * multiply column j by v
	 * 
	 * @param v
	 *            value
	 * @param j
	 *            column
	 */
	public void mulColumn(double v, int j) {
		for (int i = 1; i <= getRows(); i++) {
			set(i, j, get(i, j) * v);
		}
	}

	/**
	 * multiply origin column by v
	 * 
	 * @param v
	 *            value
	 */
	public void mulOrigin(double v) {
		mulColumn(v, getColumns());
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
	 * 
	 * @param val
	 *            flat array
	 */
	public void getForGL(float[] val) {
		int index = 0;
		for (int x = 0; x < columns; x++) {
			for (int y = 0; y < rows; y++) {
				val[index] = (float) get(y + 1, x + 1);
				index++;
			}
		}
	}

	/**
	 *
	 * set values from openGL format
	 *
	 * @param val
	 *            flat array
	 */
	public void setFromGL(float[] val) {
		int index = 0;
		for (int x = 0; x < columns; x++) {
			for (int y = 0; y < rows; y++) {
				set(y + 1, x + 1, val[index]);
				index++;
			}
		}
	}

	/**
	 * sub value at each diagonal coeff
	 * 
	 * @param value
	 *            value
	 */
	public void subToDiagonal(double value) {
		for (int i = 0; i < rows; i++) {
			vectors[i].val[i] -= value;
		}

	}

	/**
	 * set 3x3 sub matrix to diagonal equal to v
	 * 
	 * @param v
	 *            value
	 */
	public void setDiagonal3(double v) {
		vectors[0].val[0] = v;
		vectors[0].val[1] = 0;
		vectors[0].val[2] = 0;

		vectors[1].val[0] = 0;
		vectors[1].val[1] = v;
		vectors[1].val[2] = 0;

		vectors[2].val[0] = 0;
		vectors[2].val[1] = 0;
		vectors[2].val[2] = v;
	}

	// /////////////////////////////////////////////////:
	// testing the package
	/**
	 * testing the package
	 * 
	 */
	public static void test() {
		CoordMatrix matrix = new CoordMatrix4x4();
		matrix.setVx(new Coords(1, 1, -1, 0));
		matrix.setVy(new Coords(-1, 1, -1, 0));
		matrix.setVz(new Coords(1, 2, 5, 0));
		matrix.setOrigin(new Coords(4, 5, 6, 1));

		Log.debug("==== MATRIX ====\n" + matrix.toString());

		Log.debug("==== INVERSE ====\n" + matrix.inverse().toString());

		// matrix.inverse = new CoordMatrix(4, 4);
		matrix.pivotInverseMatrix = new PivotInverseMatrix();
		matrix.pivotInverseMatrix.matrixRes = new double[4 * 4];
		for (int c = 0; c < 4; c++) {
			matrix.pivotInverseMatrix.matrixRes[c * 4 + c] = 1;
		}
		matrix.pivotInverseMatrix.inverse = matrix.inverse.vectors;
		matrix.pivotInverseMatrix.columns = matrix.getColumns();
		double[][] matrixD = new double[matrix.columns][];
		for (int c = 0; c < matrix.columns; c++) {
			matrixD[c] = new double[matrix.rows];
			for (int r = 0; r < matrix.rows; r++) {
				matrixD[c][r] = matrix.get(r + 1, c + 1);
			}
		}
		pivot(matrixD, matrix.pivotInverseMatrix);

		Log.debug(
				"==== PIVOT INVERSE ====\n" + matrix.inverse.toString());

		Log.debug("==== MATRIX * INVERSE ====\n"
				+ matrix.mul(matrix.inverse).toString());
	}

}