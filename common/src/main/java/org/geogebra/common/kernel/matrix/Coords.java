/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.matrix;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.ScientificFormatAdapter;
import org.geogebra.common.util.StringUtil;

/**
 * 
 * A Ggb3DVector is composed of {x1,x2,...,xn} coordinates in double precision.
 * This class provides methods for basic linear algebra calculus.
 * 
 * @author ggb3D
 * 
 */
public class Coords implements AnimatableValue<Coords> {

	private double norm;
	private double sqNorm;
	private boolean calcNorm = true;
	private boolean calcSqNorm = true;

	/** origin 3D point */
	public static final Coords O = new Coords(0, 0, 0, 1);
	/** zero vector */
	public static final Coords ZERO = new Coords(0, 0, 0, 0);
	/** vx 3D vector */
	public static final Coords VX = new Coords(1, 0, 0, 0);
	/** vy 3D vector */
	public static final Coords VY = new Coords(0, 1, 0, 0);
	/** vz 3D vector */
	public static final Coords VZ = new Coords(0, 0, 1, 0);
	/** vz 3D vector, down orientation */
	public static final Coords VZm = new Coords(0, 0, -1, 0);
	/** undefined vector */
	public static final Coords UNDEFINED = new Coords(Double.NaN, Double.NaN,
			Double.NaN, Double.NaN) {
		@Override
		public boolean isNotFinalUndefined() {
			return false;
		}

		@Override
		public boolean isFinalUndefined() {
			return true;
		}
	};
	/** undefined vector */
	public static final Coords UNDEFINED3VALUE0 = new Coords(0, 0, 0) {
		@Override
		public boolean isNotFinalUndefined() {
			return false;
		}

		@Override
		public boolean isFinalUndefined() {
			return true;
		}
	};

	public static final Coords BLACK = new Coords(0, 0, 0, 1);
	public static final Coords DARK_GRAY = new Coords(68.0 / 255.0,
			68.0 / 255.0, 68.0 / 255.0, 1);

	final public double[] val;

	private int rows;

	private double[][] matrixForSolve;

	/**
	 * 
	 * @return (x,y,z,1) coords
	 */
	public static final Coords createInhomCoorsInD3() {
		Coords ret = new Coords(4);
		ret.setW(1);
		return ret;
	}

	// /////////////////////////////////////////////////:
	// Constructors

	/**
	 * creates a vector of the dimension specified by rows.
	 * 
	 * @param rows
	 *            number of rows
	 */
	public Coords(int rows) {

		this.rows = rows;
		// transpose = false;

		val = new double[rows];
		/*
		 * for (int i = 0; i < rows; i++) { val[i] = 0.0; }
		 */

	}

	/**
	 * creates a vector with values vals
	 * 
	 * @param vals
	 *            values {x1, x2, ...}
	 */
	public Coords(double[] vals) {

		this(vals.length);

		for (int i = 0; i < vals.length; i++) {
			val[i] = vals[i];
		}

	}

	/**
	 * creates a vector with same values as v
	 * 
	 * @param v
	 *            vector
	 */
	public Coords(Coords v) {
		this(v.val);
	}

	/**
	 * creates a 2D vector with the specified values
	 * 
	 * @param u
	 *            u
	 * @param v
	 *            v
	 */
	public Coords(double u, double v) {
		this(2);
		val[0] = u;
		val[1] = v;
	}

	/**
	 * creates a 3D vector with the specified values
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param z
	 *            z
	 */
	public Coords(double x, double y, double z) {
		this(3);
		val[0] = x;
		val[1] = y;
		val[2] = z;
	}

	/**
	 * creates a 3D vector/point with the specified values
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param z
	 *            z
	 * @param w
	 *            w
	 */
	public Coords(double x, double y, double z, double w) {
		this(4);
		val[0] = x;
		val[1] = y;
		val[2] = z;
		val[3] = w;
	}

	// /////////////////////////////////////////////////:
	// setters and getters
	/**
	 * sets v(i) to val0
	 * 
	 * @param i
	 *            number of the row
	 * @param val0
	 *            value
	 */
	public void set(int i, double val0) {
		val[i - 1] = val0;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * set four first values
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param z
	 *            z
	 * @param w
	 *            w
	 */
	public void set(double x, double y, double z, double w) {
		val[0] = x;
		val[1] = y;
		val[2] = z;
		val[3] = w;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets v to vals0
	 * 
	 * @param vals0
	 *            values {x1, x2, ...}
	 */
	public void set(double[] vals0) {
		// Application.debug("-------------val.length =
		// "+val.length+"\n-------------vals0.length = "+vals0.length);
		for (int i = 0; i < vals0.length; i++) {
			val[i] = vals0[i];
		}

		calcNorm = calcSqNorm = true;
	}

	/**
	 * set 3 first values
	 * 
	 * @param x
	 *            first value
	 * @param y
	 *            second value
	 * @param z
	 *            third value
	 */
	public void set(double x, double y, double z) {
		val[0] = x;
		val[1] = y;
		val[2] = z;
	}

	/**
	 * set this values to v's
	 * 
	 * @param v
	 *            coords
	 * @param length
	 *            length first values only are updated
	 */
	public void setValues(Coords v, int length) {
		for (int i = 0; i < length; i++) {
			val[i] = v.val[i];
		}
	}

	/**
	 * set values from v
	 * 
	 * @param v
	 *            coords
	 */
	public void set(Coords v) {
		set(v.val);
	}

	/**
	 * set 3 first values from v
	 * 
	 * @param v
	 *            coords
	 */
	public void set3(Coords v) {
		val[0] = v.val[0];
		val[1] = v.val[1];
		val[2] = v.val[2];
	}

	/**
	 * set 4 first values from v
	 * 
	 * @param v
	 *            coords
	 */
	public void set4(Coords v) {
		val[0] = v.val[0];
		val[1] = v.val[1];
		val[2] = v.val[2];
		val[3] = v.val[3];
	}

	/**
	 * set 2 first values from v
	 * 
	 * @param v
	 *            coords
	 */
	public void set2(Coords v) {
		val[0] = v.val[0];
		val[1] = v.val[1];
	}

	/**
	 * Set all coords to the same number an recompute norm. (useful for 0 and
	 * infinite vectors)
	 * 
	 * @param val0
	 *            coordinate
	 */
	public void set(double val0) {
		for (int i = 0; i < rows; i++) {
			val[i] = val0;
		}
		norm = Math.sqrt(rows) * Math.abs(val0);
		calcNorm = calcSqNorm = true;
	}

	/**
	 * returns v(i)
	 * 
	 * @param i
	 *            number of the row
	 * @return value
	 */
	public double get(int i) {
		return val[i - 1];

	}

	/**
	 * Get with check for array bounds.
	 * 
	 * @param i
	 *            index
	 * @return i-th coord or 0 if out of bounds
	 */
	public double getChecked(int i) {
		return i > val.length ? 0 : val[i - 1];
	}

	/**
	 * @param ret
	 *            copy of this
	 * 
	 */
	public void copy(double[] ret) {
		for (int i = 0; i < rows; i++) {
			ret[i] = val[i];
		}
	}

	/**
	 * returns v "x-coord"
	 * 
	 * @return x-coord
	 */
	public double getX() {
		return val[0];
	}

	/**
	 * returns v "y-coord"
	 * 
	 * @return y-coord
	 */
	public double getY() {
		return val[1];
	}

	/**
	 * returns v "z-coord"
	 * 
	 * @return z-coord
	 */
	public double getZ() {
		if (val.length > 2) {
			return val[2];
		}
		return 0; // z coord for 2D points
	}

	/**
	 * returns v "w-coord"
	 * 
	 * @return w-coord
	 */
	public double getW() {
		return val[3];
	}

	/**
	 * returns v last coord
	 * 
	 * @return last coord
	 */
	public double getLast() {
		return val[rows - 1];
	}

	/**
	 * sets the "x-coord"
	 * 
	 * @param val
	 *            val
	 */
	public void setX(double val) {
		this.val[0] = val;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets the "y-coord"
	 * 
	 * @param val
	 *            val
	 */
	public void setY(double val) {
		this.val[1] = val;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets the "z-coord"
	 * 
	 * @param val
	 *            val
	 */
	public void setZ(double val) {
		this.val[2] = val;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets the "w-coord"
	 * 
	 * @param val
	 *            val
	 */
	public void setW(double val) {
		this.val[3] = val;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * returns number of rows of the vector
	 * 
	 * @return number of rows
	 */
	public int getLength() {
		return rows;
	}

	/**
	 * returns a copy of the vector
	 * 
	 * @return a copy of the vector
	 */
	public Coords copyVector() {

		Coords result = new Coords(rows);
		for (int i = 0; i < rows; i++) {
			result.val[i] = val[i];
		}

		return result;

	}

	/**
	 * returns the start-end subvector
	 * 
	 * @param start
	 *            number of starting row
	 * @param end
	 *            number of end row
	 * @return vector with rows between start and end
	 * 
	 *         deprecated create vector and use
	 *         {@link #setSubVector(Coords, int, int)} instead
	 */

	public Coords subVector(int start, int end) {
		int r = end - start + 1;
		Coords result = new Coords(r);

		for (int i = 0; i < r; i++) {
			result.val[i] = val[start + i - 1];
		}

		return result;

	}

	/**
	 * set this to start-end subvector of v
	 * 
	 * @param v
	 *            vector
	 * 
	 * @param start
	 *            number of starting row
	 * @param end
	 *            number of end row
	 * @return this
	 */
	public Coords setSubVector(Coords v, int start, int end) {
		int r = end - start + 1;
		for (int i = 0; i < r; i++) {
			val[i] = v.val[start + i - 1];
		}

		return this;

	}

	/**
	 * returns the subvector composed of this without the row number row
	 * 
	 * @param row
	 *            number of the row to remove
	 * @return vector composed of this without the row number row
	 * 
	 *         deprecated create vector and use
	 *         {@link #setSubVector(Coords, int)} instead
	 */

	public Coords subVector(int row) {
		int r = rows;
		Coords result = new Coords(r - 1);

		int shift = 0;
		for (int i = 0; i < r; i++) {
			if (i == row) {
				shift = 1;
			} else {
				result.val[i] = val[i + shift];
			}
		}

		return result;

	}

	/**
	 * set this to subvector composed of v without the row number row
	 * 
	 * @param v
	 *            vector
	 * 
	 * @param row
	 *            number of the row to remove
	 * @return this
	 */
	public Coords setSubVector(Coords v, int row) {

		for (int i = 0; i < row; i++) {
			val[i] = v.val[i];
		}
		for (int i = row; i < rows; i++) {
			val[i] = v.val[i + 1];
		}

		return this;

	}

	// /////////////////////////////////////////////////:
	// basic operations

	/**
	 * returns dot product this * v.
	 * <p>
	 * If this={x1,x2,...} and v={x'1,x'2,...}, the dot product is
	 * x1*x'1+x2*x'2+...
	 * 
	 * @param v
	 *            vector multiplied with
	 * @return value of the dot product
	 */
	public double dotproduct(Coords v) {
		int len = Math.min(getLength(), v.getLength());
		double res = 0;
		for (int i = 0; i < len; i++) {
			res += val[i] * v.val[i];
		}
		return res;
	}

	/**
	 * returns dot product this * v in dimension 3
	 * <p>
	 * If this={x1,x2,x3} and v={x'1,x'2,x'3}, the dot product is
	 * x1*x'1+x2*x'2+x3*x'3
	 * 
	 * @param v
	 *            vector multiplied with
	 * @return value of the dot product
	 */
	public double dotproduct3(Coords v) {
		double res = 0;
		for (int i = 0; i < 3; i++) {
			res += val[i] * v.val[i];
		}
		return res;
	}

	/**
	 * Assume that (u,v) are orthogonal
	 * 
	 * @param u
	 *            vector
	 * @param v
	 *            vector
	 * @return true if crossProduct(u,v)*w is almost zero
	 */
	final public boolean isDependentToOrtho(Coords u, Coords v) {
		double value = (u.getY() * v.getZ() - u.getZ() * v.getY()) * getX()
				+ (u.getZ() * v.getX() - u.getX() * v.getZ()) * getY()
				+ (u.getX() * v.getY() - u.getY() * v.getX()) * getZ();
		return DoubleUtil.isZero(value);
	}

	/**
	 * returns cross product this * v. Attempt that the two vectors are of
	 * dimension 3.
	 * <p>
	 * If this={x,y,z} and v={x',y',z'}, then cross
	 * product={yz'-y'z,zx'-z'x,xy'-yx'}
	 * 
	 * @param v
	 *            vector multiplied with
	 * @return vector resulting of the cross product
	 * 
	 *         deprecated create vector and use
	 *         {@link #setCrossProduct3(Coords, Coords)} instead
	 */

	final public Coords crossProduct(Coords v) {

		Coords ret = new Coords(3);

		ret.setCrossProduct3(this, v);

		return ret;
	}

	/**
	 * 
	 * @param v
	 *            v
	 * @return 4-length vector equal to cross product this ^ v
	 * 
	 *         deprecated create vector and use
	 *         {@link #setCrossProduct4(Coords, Coords)} instead
	 */

	final public Coords crossProduct4(Coords v) {
		Coords ret = new Coords(4);
		ret.setCrossProduct4(this, v);
		return ret;
	}

	/**
	 * set x,y,z values according to v1 ^ v2 cross product
	 * 
	 * @param v1
	 *            v1
	 * @param v2
	 *            v2
	 */
	final public void setCrossProduct3(Coords v1, Coords v2) {
		val[0] = v1.val[1] * v2.val[2] - v1.val[2] * v2.val[1];
		val[1] = v1.val[2] * v2.val[0] - v1.val[0] * v2.val[2];
		val[2] = v1.val[0] * v2.val[1] - v1.val[1] * v2.val[0];
		calcNorm = calcSqNorm = true;
	}

	/**
	 * set x,y,z values according to v1 ^ v2 cross product; set w to 0
	 * 
	 * @param v1
	 *            v1
	 * @param v2
	 *            v2
	 */
	final public void setCrossProduct4(Coords v1, Coords v2) {
		setCrossProduct3(v1, v2);
		setW(0);
	}

	/**
	 * @param v1
	 *            vector
	 * @param v2
	 *            vector
	 * @return this dot (v1 cross v2)
	 */
	final public double dotCrossProduct(Coords v1, Coords v2) {
		return val[0] * (v1.val[1] * v2.val[2] - v1.val[2] * v2.val[1])
				+ val[1] * (v1.val[2] * v2.val[0] - v1.val[0] * v2.val[2])
				+ val[2] * (v1.val[0] * v2.val[1] - v1.val[1] * v2.val[0]);
	}

	/**
	 * Assuming this is a 3D vector
	 * 
	 * @param v
	 *            vector
	 * @return true if this and v are linear independent
	 */
	final public boolean isLinearIndependent(Coords v) {
		double value;

		value = val[1] * v.val[2] - val[2] * v.val[1];
		if (!DoubleUtil.isZero(value)) {
			return true;
		}
		value = val[2] * v.val[0] - val[0] * v.val[2];
		if (!DoubleUtil.isZero(value)) {
			return true;
		}
		value = val[0] * v.val[1] - val[1] * v.val[0];
		if (!DoubleUtil.isZero(value)) {
			return true;
		}

		return false;

	}

	/**
	 * @param v
	 *            vector
	 * @return whether this and v are independent
	 */
	final public boolean isLinearIndependentAllCoords(Coords v) {
		int index = 0;
		boolean notFound = true;
		double r1 = 0, r2 = 0;

		// first try to find non zero coords
		while (notFound && index < getLength()) {
			double c1 = val[index];
			double c2 = v.val[index];
			if (DoubleUtil.isZero(c1)) {
				if (!DoubleUtil.isZero(c2)) {
					return true;
				}
				// c1 and c2 are 0: continue
			} else {
				// c1 is not 0
				if (DoubleUtil.isZero(c2)) {
					return true;
				}
				// c2 is not 0
				notFound = false;
				r1 = c1;
				r2 = c2;
			}
			index++;
		}

		// with non zero coords, verify other coords are dependents
		while (index < getLength()) {
			double value = r1 * v.val[index] - val[index] * r2;
			if (!DoubleUtil.isZero(value)) {
				return true;
			}
			index++;
		}

		// all coords are lin dep
		return false;

	}

	/**
	 * returns the scalar norm.
	 * <p>
	 * If this={x1,x2,...}, then norm=sqrt(x1*x1+x2*x2+...). Same result as
	 * Math.sqrt(this.dotproduct(this))
	 * 
	 * @return the scalar norm
	 */
	public double norm() {
		if (calcNorm) {
			calcNorm();
			calcNorm = false;
		}
		return norm;
	}

	/**
	 * calc the norm
	 * 
	 * @return the norm
	 */
	public double calcNorm() {
		calcSquareNorm();
		norm = Math.sqrt(sqNorm);
		return norm;
	}

    /**
     *
     * @return norm about x,y,z only
     */
	public double calcNorm3() {
	    return Math.sqrt(this.dotproduct3(this));
    }

	/**
	 * calc the square norm
	 * 
	 * @return the square norm
	 */
	public double calcSquareNorm() {
		sqNorm = 0;
		for (int i = 0; i < val.length; i++) {
			sqNorm += val[i] * val[i];
		}
		return sqNorm;
	}

	/**
	 * The norm must be already calculated by calcNorm()
	 * 
	 * @return the norm
	 */
	public double getNorm() {
		return norm;
	}

	/**
	 * returns the square of the scalar norm.
	 * <p>
	 * If this={x1,x2,...}, then norm=x1*x1+x2*x2+... Same result as
	 * this.dotproduct(this)
	 * 
	 * @return the scalar norm
	 */
	public double squareNorm() {
		if (calcSqNorm) {
			calcSquareNorm();
			calcSqNorm = false;
		}
		return sqNorm;
	}

	/**
	 * returns this normalized WARNING : recalc the norm
	 * 
	 * @return this normalized
	 */
	public Coords normalized() {
		return normalized(false);
	}

	/**
	 * returns this normalized WARNING : recalc the norm
	 * 
	 * @param checkOneDirection
	 *            check if one of the result coord is near to 1 (for Kernel)
	 * @return this normalized
	 * 
	 *         deprecated create vector and use
	 *         {@link #setNormalized(Coords, boolean)} instead
	 */

	public Coords normalized(boolean checkOneDirection) {
		Coords ret = new Coords(getLength());
		calcNorm();
		double normInv = 1 / getNorm();
		int len = getLength();
		for (int i = 0; i < len; i++) {
			double v = val[i] * normInv;
			// check if v is near to be one direction vector
			if (checkOneDirection && DoubleUtil.isEqual(Math.abs(v), 1)) {
				if (v < 0) {
					ret.val[i] = -1;
				} else {
					ret.val[i] = 1;
				}
				for (int j = 0; j < i; j++) {
					ret.val[j] = 0;
				}
				for (int j = i + 1; j < len; j++) {
					ret.val[j] = 0;
				}
				break;
			}
			ret.val[i] = v;
		}
		return ret;
	}

	/**
	 * set this equal to normalized vector. Warning: recalc vector's norm
	 * 
	 * @param vector
	 *            vector
	 * 
	 * @return this
	 */
	public Coords setNormalized(Coords vector) {
		return setNormalized(vector, false);
	}

	/**
	 * set this equal to normalized vector. Warning: recalc vector's norm
	 * 
	 * @param vector
	 *            vector
	 * 
	 * @param checkOneDirection
	 *            check if one of the result coord is near to 1 (for Kernel)
	 * @return this
	 */
	public Coords setNormalized(Coords vector, boolean checkOneDirection) {
		vector.calcNorm();
		double normInv = 1 / vector.getNorm();
		for (int i = 0; i < rows; i++) {
			double v = vector.val[i] * normInv;
			// check if v is near to be one direction vector
			if (checkOneDirection && DoubleUtil.isEqual(Math.abs(v), 1)) {
				if (v < 0) {
					val[i] = -1;
				} else {
					val[i] = 1;
				}
				for (int j = 0; j < i; j++) {
					val[j] = 0;
				}
				for (int j = i + 1; j < rows; j++) {
					val[j] = 0;
				}
				break;
			}
			val[i] = v;
		}
		return this;
	}

	/**
	 * put this normalized in ret (WARNING : recalc the norm)
	 * 
	 * @param ret
	 *            output: normalized coords
	 */
	public void normalized(Coords ret) {
		calcNorm();
		double normInv = 1 / getNorm();
		for (int i = 0; i < ret.rows; i++) {
			double v = val[i] * normInv;
			ret.val[i] = v;
		}
	}

	/**
	 * WARNING : recalc the norm set this to norm=1
	 * 
	 * @return this normalized
	 */
	public Coords normalize() {
		normalize(true);
		return this;
	}

	/**
	 * 
	 * @param recalcNorm
	 *            says if the norm has to be recalculated
	 * @return this normalized
	 */
	public Coords normalize(boolean recalcNorm) {

		if (recalcNorm) {
			calcNorm();
		}
		double normInv = 1 / getNorm();
		int len = getLength();
		for (int i = 0; i < len; i++) {
			val[i] *= normInv;
		}

		norm = sqNorm = 1.0;

		return this;
	}

	/**
	 * deprecated use distance3 instead
	 * returns the distance between this and v
	 * 
	 * @param v
	 *            second vector
	 * @return (this-v).norm()
	 */
	public double distance(Coords v) {
		return this.sub(v).norm();
	}
	
	/**
	 * returns the distance between this and v
	 * 
	 * @param v
	 *            second vector
	 * @return (this-v).norm()
	 */
	public double distance3(Coords v) {
		return MyMath.length(val[0] - v.val[0], val[1] - v.val[1], val[2] - v.val[2]);
	}

	/**
	 * 
	 * Calc square distance to v - only on x, y, z coords
	 * 
	 * @param v
	 *            coords
	 * @return square distance
	 */
	public double squareDistance3(Coords v) {
		double x = getX() - v.getX();
		double y = getY() - v.getY();
		double z = getZ() - v.getZ();

		return x * x + y * y + z * z;
	}

	/**
	 * returns the shortest vector between this and a 3D-line represented by the
	 * matrix {V O}
	 * 
	 * @param lineO
	 *            origin of the line
	 * @param V
	 *            direction of the line
	 * @return shortest vector between this and the line
	 */
	private Coords vectorToLine(Coords lineO, Coords V) {
		Coords OM = this.sub(lineO);
		Coords N = V.normalized();
		Coords OH = N.mul(OM.dotproduct(N)); // TODO optimize
		return OM.sub(OH);
	}

	/**
	 * returns the distance between this and a 3D-line represented by the matrix
	 * {V O}
	 * 
	 * @param lineO
	 *            origin of the line
	 * @param V
	 *            direction of the line
	 * @return distance between this and the line
	 */
	public double distLine(Coords lineO, Coords V) {
		return vectorToLine(lineO, V).norm();
	}

	/**
	 * returns the square distance between this and a 3D-line represented by the
	 * matrix {V O} (only computed on x, y, z)
	 * 
	 * @param lineO
	 *            origin of the line
	 * @param V
	 *            direction of the line
	 * @return distance between this and the line
	 */
	public double squareDistLine3(Coords lineO, Coords V) {
		Coords v = vectorToLine(lineO, V);
		return v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ();
	}

	/**
	 * 
	 * @param o
	 *            point of the plane
	 * @param vn
	 *            normal direction to the plane
	 * @return distance of this to the plane
	 */
	public double distPlane(Coords o, Coords vn) {
		return Math.abs(distPlaneOriented(o, vn));
	}

	/**
	 * 
	 * @param o
	 *            point of the plane
	 * @param vn
	 *            normal direction to the plane
	 * @return oriented distance of this to the plane
	 */
	public double distPlaneOriented(Coords o, Coords vn) {
		return this.sub(o).dotproduct(vn);
	}

	/**
	 * returns this projected on the plane represented by the matrix (third
	 * vector used for direction). If direction is parallel to the plane, return
	 * infinite point (direction vector).
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4.
	 * 
	 * 
	 * set two vectors {globalCoords,inPlaneCoords}: the point projected, and
	 * the original point in plane coords
	 * 
	 * @param m
	 *            matrix {v1 v2 v3 o} where (o,v1,v2) is a coord sys fo the
	 *            plane, and v3 the direction used for projection
	 * @param globalCoords
	 *            output coords (global)
	 * @param inPlaneCoords
	 *            output coords (in plane)
	 * 
	 */
	public void projectPlane(CoordMatrix m, Coords globalCoords,
			Coords inPlaneCoords) {

		projectPlane(m.getVx(), m.getVy(), m.getVz(), m.getOrigin(),
				globalCoords, inPlaneCoords);
	}

	/**
	 * returns this projected on the plane represented by the matrix (third
	 * vector used for direction), Does check if direction is parallel to the
	 * plane.
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4.
	 * 
	 * @param vx
	 *            vx
	 * @param vy
	 *            vy
	 * @param vz
	 *            vz
	 * @param o
	 *
	 *            matrix {vx vy vz o} where (o,vx,vy) is a coord sys for the
	 *            plane, and vz the direction used for projection
	 * @param globalCoords
	 *            output coords (global)
	 * @param inPlaneCoords
	 *            output coords (in plane)
	 */
	public void projectPlane(Coords vx, Coords vy, Coords vz, Coords o,
			Coords globalCoords, Coords inPlaneCoords) {
		projectPlane(vx, vy, vz, o, globalCoords.val, inPlaneCoords.val);
	}

	/**
	 * returns this projected on the plane represented by the matrix (third
	 * vector used for direction), Does check if direction is parallel to the
	 * plane.
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4.
	 * 
	 * @param vx
	 *            vx
	 * @param vy
	 *            vy
	 * @param vz
	 *            vz
	 * @param o
	 *
	 *            matrix {vx vy vz o} where (o,vx,vy) is a coord sys for the
	 *            plane, and vz the direction used for projection
	 * @param globalCoords
	 *            output coords (global)
	 * @param inPlaneCoords
	 *            output coords (in plane)
	 */
	public void projectPlane(Coords vx, Coords vy, Coords vz, Coords o,
			double[] globalCoords, double[] inPlaneCoords) {

		if (DoubleUtil.isEqual((vx.crossProduct(vy)).dotproduct(vz), 0,
				Kernel.STANDARD_PRECISION)) {
			// direction of projection is parallel to the plane : point is
			// infinite
			inPlaneCoords[0] = 0; // x
			inPlaneCoords[1] = 0; // y
			inPlaneCoords[2] = -1; // z
			inPlaneCoords[3] = 0; // w
			globalCoords[0] = vz.getX(); // x
			globalCoords[1] = vz.getY(); // y
			globalCoords[2] = vz.getZ(); // z
			globalCoords[3] = vz.getW(); // w
			return;
		}

		// direction is not parallel to the plane
		projectPlaneNoCheck(vx, vy, vz, o, globalCoords, inPlaneCoords);
	}

	/**
	 * returns this projected on the plane represented by the matrix (third
	 * vector used for direction), Does check if direction is parallel to the
	 * plane.
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4.
	 * 
	 * @param m
	 *            plane matrix
	 * @param inPlaneCoords
	 *            output coords (in plane)
	 */
	public void projectPlaneInPlaneCoords(CoordMatrix m, Coords inPlaneCoords) {
		projectPlaneInPlaneCoords(m.getVx(), m.getVy(), m.getVz(),
				m.getOrigin(), inPlaneCoords);
	}

	/**
	 * returns this projected on the plane represented by the matrix (third
	 * vector used for direction), Does check if direction is parallel to the
	 * plane.
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4.
	 * 
	 * @param vx
	 *            vx
	 * @param vy
	 *            vy
	 * @param vz
	 *            vz
	 * @param o
	 *
	 *            matrix {vx vy vz o} where (o,vx,vy) is a coord sys for the
	 *            plane, and vz the direction used for projection
	 * @param inPlaneCoords
	 *            output coords (in plane)
	 */
	public void projectPlaneInPlaneCoords(Coords vx, Coords vy, Coords vz,
			Coords o, Coords inPlaneCoords) {

		if (vz.isDependentToOrtho(vx, vy)) {
			// direction of projection is parallel to the plane : point is
			// infinite
			// Application.printStacktrace("infinity");
			inPlaneCoords.setX(0);
			inPlaneCoords.setY(0);
			inPlaneCoords.setZ(-1);
			inPlaneCoords.setW(0);
			return;
		}

		// direction is not parallel to the plane
		projectPlaneNoCheckInPlaneCoords(vx, vy, vz, o, inPlaneCoords.val);
	}

	/**
	 * returns this projected on the plane represented by the matrix (third
	 * vector used for direction), Does check if direction is parallel to the
	 * plane.
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4.
	 * 
	 * @param m
	 *            plane matrix
	 * @param globalCoords
	 *            output coords (global)
	 */
	public void projectPlane(CoordMatrix m, Coords globalCoords) {
		projectPlane(m.getVx(), m.getVy(), m.getVz(), m.getOrigin(),
				globalCoords);
	}

	/**
	 * returns this projected on the plane represented by the matrix (third
	 * vector used for direction), Does check if direction is parallel to the
	 * plane.
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4.
	 * 
	 * @param vx
	 *            vx
	 * @param vy
	 *            vy
	 * @param vz
	 *            vz
	 * @param o
	 *
	 *            matrix {vx vy vz o} where (o,vx,vy) is a coord sys for the
	 *            plane, and vz the direction used for projection
	 * @param globalCoords
	 *            output coords (global)
	 */
	public void projectPlane(Coords vx, Coords vy, Coords vz, Coords o,
			Coords globalCoords) {

		if (vz.isDependentToOrtho(vx, vy)) {
			// direction of projection is parallel to the plane : point is
			// infinite
			// Application.printStacktrace("infinity");
			globalCoords.set(vz);
			return;
		}

		// direction is not parallel to the plane
		// we can use globalCoords twice as it will be set at this end
		projectPlaneNoCheck(vx, vy, vz, o, globalCoords.val, globalCoords.val);
	}

	/**
	 * project on plane with known inverse matrix
	 * 
	 * @param m
	 *            inverse matrix
	 * @return 3D point in plane coords (z = distance(point, plane))
	 */
	final public Coords projectPlaneWithInverseMatrix(CoordMatrix m) {
		return m.mul(this);
	}

	/**
	 * returns this projected on the plane represented by the matrix (third
	 * vector used for direction), no check if direction is parallel to the
	 * plane.
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4.
	 * 
	 * @param vx
	 *            vx
	 * @param vy
	 *            vy
	 * @param vz
	 *            vz
	 * @param o
	 *
	 *            matrix {vx vy vz o} where (o,vx,vy) is a coord sys for the
	 *            plane, and vz the direction used for projection
	 * @param globalCoords
	 *            output coords (global)
	 * @param inPlaneCoords
	 *            output coords (in plane)
	 */
	public void projectPlaneNoCheck(Coords vx, Coords vy, Coords vz, Coords o,
			double[] globalCoords, double[] inPlaneCoords) {

		// project in plane coords
		projectPlaneNoCheckInPlaneCoords(vx, vy, vz, o, inPlaneCoords);

		// globalCoords=this-inPlaneCoords_z*plane_vz
		double coeff = -inPlaneCoords[2]; // inPlaneCoords may use globalCoords
											// for memory
		vz.mul(coeff, globalCoords);
		this.add(globalCoords, globalCoords);

		// note : globalCoords must be set at the end (when dummy inPlaneCoords)
	}

	/**
	 * returns this projected on the plane represented by the matrix (third
	 * vector used for direction), Does check if direction is parallel to the
	 * plane.
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4.
	 * 
	 * @param vx
	 *            vx
	 * @param vy
	 *            vy
	 * @param vz
	 *            vz
	 * @param o
	 *
	 *            matrix {vx vy vz o} where (o,vx,vy) is a coord sys for the
	 *            plane, and vz the direction used for projection
	 * @param inPlaneCoords
	 *            output coords (in plane)
	 */
	public void projectPlaneNoCheckInPlaneCoords(Coords vx, Coords vy,
			Coords vz, Coords o, double[] inPlaneCoords) {

		// m*inPlaneCoords=this
		int size = getLength();
		if (matrixForSolve == null) {
			matrixForSolve = new double[size][size];
		}
		CoordMatrix.solve(matrixForSolve, inPlaneCoords, this, vx, vy, vz, o);

	}

	/**
	 * returns this projected on the plane represented by the matrix, with
	 * vector v used for direction.
	 * <p>
	 * Attempt this to be of dimension 4, the matrix to be of dimension 4*4, and
	 * the vector to be of dimension 4.
	 * 
	 * 
	 * set two vectors {globalCoords,inPlaneCoords}: the point projected, and
	 * the original point in plane coords
	 * 
	 * @param m
	 *            matrix {v1 v2 ?? o} where (o,v1,v2) is a coord sys fo the
	 *            plane, and v3
	 * @param v
	 *            the direction used for projection
	 * @param globalCoords
	 *            output: global coords
	 * @param inPlaneCoords
	 *            output: coords in plane
	 */
	public void projectPlaneThruV(CoordMatrix m, Coords v, Coords globalCoords,
			Coords inPlaneCoords) {
		projectPlane(m.getVx(), m.getVy(), v, m.getOrigin(), globalCoords,
				inPlaneCoords);
	}

	public void projectPlaneThruV(CoordMatrix m, Coords v,
			Coords globalCoords) {
		projectPlane(m.getVx(), m.getVy(), v, m.getOrigin(), globalCoords);
	}

	/**
	 * returns this projected on the plane represented by the matrix (third
	 * vector used for direction), Does check if direction is parallel to the
	 * plane.
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4.
	 * 
	 * @param m
	 *            plane matrix
	 * @param v
	 *            vz
	 * @param inPlaneCoords
	 *            output coords (in plane)
	 */
	public void projectPlaneThruVInPlaneCoords(CoordMatrix m, Coords v,
			Coords inPlaneCoords) {
		projectPlaneInPlaneCoords(m.getVx(), m.getVy(), v, m.getOrigin(),
				inPlaneCoords);
	}

	/**
	 * returns this projected on the plane represented by the matrix, with
	 * vector v used for direction.
	 * <p>
	 * If v is parallel to plane, then plane third vector is used instead
	 * 
	 * 
	 * set two vectors {globalCoords,inPlaneCoords}: the point projected, and
	 * the original point in plane coords
	 * 
	 * @param m
	 *            matrix {v1 v2 v3 o} where (o,v1,v2) is a coord sys fo the
	 *            plane, and v3
	 * @param v
	 *            the direction used for projection (v3 is used instead if v is
	 *            parallel to the plane)
	 * @param globalCoords
	 *            output: global coords
	 * @param inPlaneCoords
	 *            output: coords in plane
	 */
	public void projectPlaneThruVIfPossible(CoordMatrix m, Coords v,
			Coords globalCoords, Coords inPlaneCoords) {

		// check if v is parallel to plane
		Coords v3 = m.getColumn(3);
		if (DoubleUtil.isEqual(v3.dotproduct(v), 0.0, Kernel.STANDARD_PRECISION)) {
			projectPlane(m, globalCoords, inPlaneCoords);
			return;
		}

		// if not, use v for direction
		projectPlane(m.getVx(), m.getVy(), v, m.getOrigin(), globalCoords,
				inPlaneCoords);
	}

	/**
	 * returns this projected on the plane represented by the matrix, with
	 * vector v used for direction.
	 * <p>
	 * If v is parallel to plane, then plane third vector is used instead
	 * 
	 * 
	 * set two vectors {globalCoords,inPlaneCoords}: the point projected, and
	 * the original point in plane coords
	 * 
	 * @param m
	 *            matrix {v1 v2 v3 o} where (o,v1,v2) is a coord sys fo the
	 *            plane, and v3
	 * @param v
	 *            the direction used for projection (v3 is used instead if v is
	 *            parallel to the plane)
	 * @param globalCoords
	 *            output: global coords
	 */
	public void projectPlaneThruVIfPossible(CoordMatrix m, Coords v,
			Coords globalCoords) {
		// check if v is parallel to plane
		Coords v3 = m.getColumn(3);
		if (DoubleUtil.isEqual(v3.dotproduct(v), 0.0, Kernel.STANDARD_PRECISION)) {
			projectPlane(m, globalCoords);
			return;
		}

		projectPlane(m.getVx(), m.getVy(), v, m.getOrigin(), globalCoords);
	}

	/**
	 * returns this projected on the plane represented by the matrix (third
	 * vector used for direction), Does check if direction is parallel to the
	 * plane.
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4.
	 * 
	 * @param vx
	 *            vx
	 * @param vy
	 *            vy
	 * @param vz
	 *            vz
	 * @param o
	 *
	 *            matrix {vx vy (vz or v) o} where (o,vx,vy) is a coord sys for
	 *            the plane, v is projection direction (if not parallel to
	 *            plane; otherwise vz is used)
	 * @param v
	 *            projection direction if
	 * @param globalCoords
	 *            output coords (global)
	 */
	public void projectPlaneThruVIfPossible(Coords vx, Coords vy, Coords vz,
			Coords o, Coords v, Coords globalCoords) {
		// check if v is parallel to plane
		if (DoubleUtil.isEqual(vz.dotproduct(v), 0.0, Kernel.STANDARD_PRECISION)) {
			projectPlane(vx, vy, vz, o, globalCoords);
			return;
		}

		projectPlane(vx, vy, v, o, globalCoords);
	}

	/**
	 * returns this projected on the plane represented by the matrix, with
	 * vector v used for direction.
	 * <p>
	 * If v is parallel to plane, then plane third vector is used instead
	 * 
	 * 
	 * set two vectors {globalCoords,inPlaneCoords}: the point projected, and
	 * the original point in plane coords
	 * 
	 * @param m
	 *            matrix {v1 v2 v3 o} where (o,v1,v2) is a coord sys fo the
	 *            plane, and v3
	 * @param v
	 *            the direction used for projection (v3 is used instead if v is
	 *            parallel to the plane)
	 * @param inPlaneCoords
	 *            output: coords in plane
	 */
	public void projectPlaneThruVIfPossibleInPlaneCoords(CoordMatrix m,
			Coords v, Coords inPlaneCoords) {

		// check if v is parallel to plane
		Coords v3 = m.getColumn(3);
		if (DoubleUtil.isEqual(v3.dotproduct(v), 0.0, Kernel.STANDARD_PRECISION)) {
			projectPlaneInPlaneCoords(m, inPlaneCoords);
			return;
		}

		// if not, use v for direction
		projectPlaneInPlaneCoords(m.getVx(), m.getVy(), v, m.getOrigin(),
				inPlaneCoords);
	}

	/**
	 * returns this projected on the plane represented by the matrix, with
	 * vector v used for direction.
	 * <p>
	 * If v is parallel to plane, first project old position of the line
	 * (this,v), then project the result using plane third vector
	 * 
	 * @param m
	 *            matrix {v1 v2 v3 o} where (o,v1,v2) is a coord sys fo the
	 *            plane, and v3
	 * @param oldCoords
	 *            old position of this
	 * @param v
	 *            the direction used for projection (v3 is used instead if v is
	 *            parallel to the plane)
	 * @param globalCoords
	 *            output: the point projected,
	 * @param inPlaneCoords
	 *            output: the original point in plane coords
	 */
	public void projectPlaneThruVIfPossible(CoordMatrix m, Coords oldCoords,
			Coords v, Coords globalCoords, Coords inPlaneCoords) {

		// Application.debug(this+"\nold=\n"+oldCoords);

		// check if v is parallel to plane
		Coords v3 = m.getColumn(3);
		if (DoubleUtil.isZero(v3.dotproduct(v))) {
			Coords firstProjection = Coords.createInhomCoorsInD3();
			oldCoords.projectLine(this, v, firstProjection, null);
			firstProjection.projectPlane(m, globalCoords, inPlaneCoords);
			return;
		}

		// if not, use v for direction
		projectPlane(m.getVx(), m.getVy(), v, m.getOrigin(), globalCoords,
				inPlaneCoords);
	}

	/**
	 * calculates projection of this on the 3D-line represented by the matrix {V
	 * O}.
	 * 
	 * @param o
	 *            origin of the line
	 * @param v
	 *            direction of the line
	 * @param h
	 *            point projected
	 * @param parameters
	 *            {parameter on the line, normalized parameter}
	 */
	public void projectLine(Coords o, Coords v, Coords h, double[] parameters) {
		this.sub(o, h); // OM
		Coords N = v.normalized();
		double parameter = h.dotproduct(N); // OM.N
		N.mul(parameter, h); // OH
		o.add(h, h);

		if (parameters == null) {
			return;
		}

		parameters[0] = parameter / v.norm();
		parameters[1] = parameter;

	}

	/**
	 * calculates projection of this on the 3D-line represented by the matrix {V
	 * O}.
	 * 
	 * @param o
	 *            origin of the line
	 * @param P
	 *            point on the line
	 * @param H
	 *            point projected
	 * @param parameters
	 *            {parameter on the line, normalized parameter}
	 */
	public void projectLineSub(Coords o, Coords P, Coords H,
			double[] parameters) {
		this.sub(o, H); // OM
		Coords V = P.sub(o);
		double vn = V.norm();
		Coords N = V.normalize();
		double parameter = H.dotproduct(N); // OM.N
		N.mul(parameter, H); // OH
		o.add(H, H);

		if (parameters == null) {
			return;
		}

		parameters[0] = parameter / vn;
		parameters[1] = parameter;

	}

	/**
	 * calculates projection of this on the 3D-line represented by the matrix {V
	 * O}.
	 * 
	 * @param o
	 *            origin of the line
	 * @param V
	 *            direction of the line
	 * @param H
	 *            point projected
	 */
	public void projectLine(Coords o, Coords V, Coords H) {
		this.sub(o, H); // OM
		Coords N = V.normalized();
		double parameter = H.dotproduct(N); // OM.N
		N.mul(parameter, H); // OH
		o.add(H, H);
	}

	/**
	 * calculates projection of this as close as possible to the 3D-line
	 * represented by the matrix {V O} regarding V2 direction.
	 * 
	 * @param o
	 *            origin of the line
	 * @param V
	 *            direction of the line
	 * @param V2
	 *            direction of projection
	 * @param project
	 *            output point projected
	 */
	public void projectNearLine(Coords o, Coords V, Coords V2, Coords project) {
		Coords V3 = V.crossProduct(V2);

		if (DoubleUtil.isEqual(V3.norm(), 0.0, Kernel.STANDARD_PRECISION)) {
			project.set(this);
			return;
		}

		projectPlane(V, V3, V2, o, project);
	}

	/**
	 * Calc the parameter on (O,V) of the point of (O,V) that is the nearest to
	 * line (this,V2).
	 * 
	 * If V and V2 are parallel, return O.
	 * 
	 * @param o
	 *            origin of the line where this is projected
	 * @param V
	 *            direction of the line where this is projected
	 * @param V2
	 *            direction of projection
	 * @param tmp
	 *            temp coords
	 * @return parameter of the proj. point on the line
	 */
	public double projectedParameterOnLineWithDirection(Coords o, Coords V,
			Coords V2, Coords tmp) {
		Coords V3 = V.crossProduct4(V2);

		if (V3.isZero()) {
			return 0;
		}

		o.projectPlaneInPlaneCoords(V2, V3, V, this, tmp);
		return -tmp.getZ();
	}

	/**
	 * returns this-v
	 * 
	 * @param v
	 *            vector subtracted
	 * @return this-v
	 * 
	 *         deprecated create vector and use {@link #setSub(Coords, Coords)}
	 *         instead
	 */

	public Coords sub(Coords v) {
		int i;
		Coords result = new Coords(rows);
		for (i = 0; i < rows; i++) {
			result.val[i] = val[i] - v.val[i];
		}

		return result;
	}

	/**
	 * 
	 * @param v
	 *            vector
	 * @param result
	 *            gets this - v
	 */
	public void sub(Coords v, Coords result) {
		for (int i = 0; i < result.rows; i++) {
			result.val[i] = val[i] - v.val[i];
		}
	}

	/**
	 * set this to v1 - v2
	 * 
	 * @param v1
	 *            vector
	 * @param v2
	 *            vector
	 * @return this
	 */
	public Coords setSub(Coords v1, Coords v2) {
		for (int i = 0; i < rows; i++) {
			val[i] = v1.val[i] - v2.val[i];
		}

		return this;
	}

	/**
	 * set this to v1 - v2 (only first 3 values)
	 * 
	 * @param v1
	 *            vector
	 * @param v2
	 *            vector
	 * @return this
	 */
	public Coords setSub3(Coords v1, Coords v2) {
		for (int i = 0; i < 3; i++) {
			val[i] = v1.val[i] - v2.val[i];
		}

		return this;
	}

	/**
	 * returns n-1 length vector, all coordinates divided by the n-th.
	 * <p>
	 * If this={x1,x2,xn}, it returns {x1/xn,x2/xn,...,x(n-1)}
	 * 
	 * @return {x1/xn,x2/xn,...,x(n-1)/xn}
	 * 
	 *         deprecated create vector and use {@link #setInhomCoords(Coords)}
	 *         instead
	 */

	public Coords getInhomCoords() {
		int r = rows;
		Coords result = new Coords(r - 1);

		double wdiv = 1 / val[r - 1];
		for (int i = 0; i < r - 1; i++) {
			result.val[i] = val[i] * wdiv;
		}

		return result;
	}

	/**
	 * @param i
	 *            index
	 * @return inhomogenous coord at given index
	 */
	public double getInhom(int i) {
		int r = rows;
		double wdiv = 1 / val[r - 1];
		return val[i] * wdiv;
	}

	/**
	 * If v={x1,x2,xn}, this gets {x1/xn,x2/xn,...,x(n-1)}
	 * 
	 * @param v
	 *            vector
	 * 
	 * @return this
	 */
	public Coords setInhomCoords(Coords v) {

		double wdiv = 1 / v.val[rows - 1];
		for (int i = 0; i < rows - 1; i++) {
			val[i] = v.val[i] * wdiv;
		}

		return this;
	}

	/**
	 * returns n length vector, all coordinates divided by the n-th.
	 * 
	 * @return {x1/xn,x2/xn,...,x(n-1)/xn,1}
	 * 
	 *         deprecated create vector and use
	 *         {@link #setInhomCoordsInSameDimension(Coords)} instead
	 */

	public Coords getInhomCoordsInSameDimension() {

		int r = rows;

		if (DoubleUtil.isEqual(val[r - 1], 1)) {
			return this;
		}

		Coords result = new Coords(r);

		double wdiv = 1 / val[r - 1];
		for (int i = 0; i < r - 1; i++) {
			result.val[i] = val[i] * wdiv;
		}

		result.val[r - 1] = 1;

		return result;
	}

	/**
	 * 
	 * If v={x1,x2,xn}, this gets {x1/xn,x2/xn,...,x(n-1, 1)}
	 * 
	 * @param v
	 *            vector
	 * 
	 * @return this
	 */
	public Coords setInhomCoordsInSameDimension(Coords v) {

		setInhomCoords(v);
		val[rows - 1] = 1;
		return this;
	}

	/**
	 * set values in inhom coords
	 */
	public void setInhomCoords() {
		if (DoubleUtil.isEqual(val[rows - 1], 1)) {
			return;
		}

		double wdiv = 1 / val[rows - 1];
		for (int i = 0; i < rows - 1; i++) {
			val[i] *= wdiv;
		}

		val[rows - 1] = 1;
	}

	/**
	 * returns n length vector, all coordinates divided by the n-th.
	 * <p>
	 * If this={x1,x2,xn}, it returns {x1/xn,x2/xn,...,1}
	 * 
	 * @return {x1/xn,x2/xn,...,1}
	 * 
	 *         deprecated create vector and use {@link #setCoordsLast1(Coords)}
	 *         instead
	 */

	public Coords getCoordsLast1() {
		int len = getLength();
		Coords result = new Coords(len);
		double lastCoord = val[len - 1];
		if (lastCoord != 0.0) {
			double lastCoordInv = 1 / lastCoord;
			for (int i = 0; i < len; i++) {
				result.val[i] = val[i] * lastCoordInv;
			}
		} else {
			result.set(this);
		}
		return result;
	}

	/**
	 *
	 * If v={x1,x2,xn}, this gets {x1/xn,x2/xn,...,1}
	 * 
	 * @param v
	 *            vector
	 * 
	 * @return this
	 */
	public Coords setCoordsLast1(Coords v) {
		double lastCoord = v.val[rows - 1];
		if (lastCoord != 0.0) {
			double lastCoordInv = 1 / lastCoord;
			for (int i = 0; i < rows; i++) {
				val[i] = v.val[i] * lastCoordInv;
			}
		} else {
			set(v);
		}
		return this;
	}

	/**
	 * 
	 * @return this with (n-1) coord removed deprecated create vector and use
	 *         {@link #setProjectInfDim(Coords)} instead
	 */

	public Coords projectInfDim() {
		int len = getLength();
		Coords result = new Coords(len - 1);
		for (int i = 0; i < len - 1; i++) {
			result.val[i] = val[i];
		}
		result.val[len - 2] = val[len - 1];
		return result;
	}

	/**
	 * set this equal to v with (n-1) coord removed
	 * 
	 * @param v
	 *            vector
	 * @return this
	 */
	public Coords setProjectInfDim(Coords v) {
		for (int i = 0; i < v.rows - 1; i++) {
			val[i] = v.val[i];
		}
		val[rows - 2] = v.val[rows - 1];
		return this;
	}

	/**
	 * Return true if this==v for the precision given (ie each coordinates are
	 * not different more than precision).
	 * 
	 * @param v
	 *            vector compared with
	 * @param precision
	 *            precision
	 * @return true if the vectors are equal
	 */
	public boolean equalsForKernel(Coords v, double precision) {
		int len = getLength();
		for (int i = 0; i < len; i++) {
			if (!DoubleUtil.isEqual(val[i], v.val[i], precision)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Return true if this==v for the precision given (ie each coordinates are
	 * not different more than precision).
	 * 
	 * @param v
	 *            vector compared with
	 * @return true if the vectors are equal
	 */
	public boolean equalsForKernel(Coords v) {
		int len = getLength();
		for (int i = 0; i < len; i++) {
			if (!DoubleUtil.isEqual(val[i], v.val[i])) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Return true if this==v for x,y,z, for the precision given (ie each
	 * coordinates are not different more than precision).
	 * 
	 * @param v
	 *            vector compared with
	 * @return true if the vectors are equal
	 */
	public boolean equalsForKernel3(Coords v) {
		for (int i = 0; i < 3; i++) {
			if (!DoubleUtil.isEqual(val[i], v.val[i])) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Return true if this==v for Kernel.STANDARD_PRECISION precision (ie each
	 * coordinates are not different more than precision).
	 * 
	 * @param v
	 *            vector compared with
	 * @return true if the vectors are equal
	 */
	public boolean isEqual(Coords v) {
		return equalsForKernel(v, Kernel.STANDARD_PRECISION);
	}

	/**
	 * Check if all entries are within standard precision from 0.
	 * 
	 * @return whether all entries are zero
	 */
	public boolean isZero() {
		int len = getLength();
		for (int i = 0; i < len; i++) {
			if (!DoubleUtil.isEqual(val[i], 0, Kernel.STANDARD_PRECISION)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param dim
	 *            number of coords that must be 0
	 * @return whether first dim coords are 0
	 */
	public boolean isZero(int dim) {
		for (int i = 0; i < dim; i++) {
			if (!DoubleUtil.isEqual(val[i], 0, Kernel.STANDARD_PRECISION)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return true if all coordinates are not different from val more than
	 * precision.
	 * 
	 * @param value
	 *            value compared with
	 * @param precision
	 *            precision
	 * @return true if all coordinates are not different from val more than
	 *         precision.
	 */
	public boolean equalsForKernel(double value, double precision) {
		int len = getLength();
		for (int i = 0; i < len; i++) {
			if (!DoubleUtil.isEqual(this.val[i], value, precision)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Assume that "this" is a non-zero vector in 3-space. This method returns
	 * an array v of two vectors {v[0], v[1]} (rows=4) so that (this, v[0],
	 * v[1]) is a right-handed orthonormal system.
	 * 
	 * deprecated create vectors and use
	 * {@link #completeOrthonormal(Coords, Coords)} instead
	 * 
	 * @return two orthonormal vectors
	 */

	public Coords[] completeOrthonormal() {
		Coords vn1 = new Coords(4);

		if (val[0] != 0) {
			vn1.val[0] = -val[1];
			vn1.val[1] = val[0];
			vn1.normalize();
		} else {
			vn1.val[0] = 1.0;
		}

		Coords vn2 = this.crossProduct4(vn1);
		vn2.normalize();

		return new Coords[] { vn1, vn2 };
	}

	/**
	 * Assume that "this" is a non-zero vector in 3-space. This method sets the
	 * vectors vn1, vn2 (rows=4) so that (this, vn1, vn2) is a right-handed
	 * orthonormal system.
	 * 
	 * @param vn1
	 *            vector (length 4)
	 * @param vn2
	 *            vector (length 4)
	 */
	public void completeOrthonormal(Coords vn1, Coords vn2) {
		if (val[0] != 0) {
			vn1.val[0] = -val[1];
			vn1.val[1] = val[0];
			vn1.val[2] = 0;
			vn1.val[3] = 0;
			vn1.normalize();
		} else {
			vn1.val[0] = 1.0;
			vn1.val[1] = 0.0;
			vn1.val[2] = 0.0;
			vn1.val[3] = 0.0;
		}

		vn2.setCrossProduct4(this, vn1);
		vn2.setW(0);
		vn2.normalize();
	}

	/**
	 * Assume that "this" is a non-zero vector in 3-space. This method sets the
	 * vectors vn1, vn2 (rows=3) so that (this, vn1, vn2) is a right-handed
	 * orthonormal system.
	 * 
	 * @param vn1
	 *            vector (length 3)
	 * @param vn2
	 *            vector (length 3)
	 */
	public void completeOrthonormal3(Coords vn1, Coords vn2) {
		if (val[0] != 0) {
			vn1.val[0] = -val[1];
			vn1.val[1] = val[0];
			vn1.val[2] = 0;
			vn1.normalize();
		} else {
			vn1.val[0] = 1.0;
			vn1.val[1] = 0.0;
			vn1.val[2] = 0.0;
		}

		vn2.setCrossProduct3(this, vn1);
		vn2.normalize();
	}

	/**
	 * Assume that "this" is a non-zero vector in 3-space. This method sets the
	 * vector vn1 so that (this, vn1) is orthonormal
	 * 
	 * @param vn1
	 *            vector (length 4)
	 */
	public void completeOrthonormal(Coords vn1) {
		if (val[2] != 0) {
			vn1.val[2] = -val[1];
			vn1.val[1] = val[2];
			vn1.val[0] = 0;
			vn1.val[3] = 0;
			vn1.normalize();
		} else {
			vn1.val[0] = 0.0;
			vn1.val[1] = 0.0;
			vn1.val[2] = 1.0;
			vn1.val[3] = 0.0;
		}
	}

	/**
	 * Assume that "this" is a non-zero vector in 3-space. This method sets the
	 * vector vn1 so that (this, vn1) is orthonormal. If this is in xOy plane,
	 * then vn1 will.
	 * 
	 * @param vn1
	 *            vector (length 4)
	 */
	public void completeOrthonormalKeepInXOYPlaneIfPossible(Coords vn1) {
		vn1.val[3] = 0;
		completeOrthonormalKeepInXOYPlaneIfPossible3(vn1);
	}

	/**
	 * Assume that "this" is a non-zero vector in 3-space. This method sets the
	 * vector vn1 so that (this, vn1) is orthonormal. If this is in xOy plane,
	 * then vn1 will.
	 * 
	 * @param vn1
	 *            vector (length 3)
	 */
	public void completeOrthonormalKeepInXOYPlaneIfPossible3(Coords vn1) {
		if (!DoubleUtil.isZero(val[0]) || !DoubleUtil.isZero(val[1])) {
			vn1.val[0] = -val[1];
			vn1.val[1] = val[0];
			vn1.val[2] = 0;
			vn1.normalize();
		} else {
			vn1.val[0] = 1.0;
			vn1.val[1] = 0.0;
			vn1.val[2] = 0.0;
		}
	}

	// ///////////////////////////////////////////////////
	// BASIC OPERATIONS
	// ///////////////////////////////////////////////////

	/**
	 * 
	 * @param v
	 *            vector to add
	 * @return
	 * 
	 * 		deprecated create vector and use {@link #setAdd(Coords, Coords)}
	 *         or {@link #setAdd3(Coords, Coords)} instead
	 */

	public Coords add(Coords v) {
		Coords result = new Coords(rows);

		for (int i = 0; i < rows && i < v.rows; i++) {
			result.val[i] = val[i] + v.val[i];
		}

		return result;
	}

	/**
	 * put this + v into result
	 * 
	 * @param v
	 *            vector
	 * @param result
	 *            result
	 */
	public void add(Coords v, double[] result) {
		for (int i = 0; i < rows; i++) {
			result[i] = val[i] + v.val[i];
		}
	}

	/**
	 * put this + v into result
	 * 
	 * @param v
	 *            vector
	 * @param result
	 *            result
	 */
	public void add(double[] v, double[] result) {
		for (int i = 0; i < rows; i++) {
			result[i] = val[i] + v[i];
		}
	}

	/**
	 * 
	 * @param v
	 *            vector in smaller dim than this
	 * @return
	 * 
	 * 		deprecated create vector and use {@link #setAdd(Coords, Coords)}
	 *         or {@link #setAdd3(Coords, Coords)} instead
	 */

	public Coords addSmaller(Coords v) {
		Coords result = new Coords(rows);

		for (int i = 0; i < v.rows; i++) {
			result.val[i] = val[i] + v.val[i];
		}

		return result;
	}

	/**
	 * add values of v inside this
	 * 
	 * @param v
	 *            vector
	 * @return this
	 */
	public Coords addInside(Coords v) {
		for (int i = 0; i < v.val.length; i++) {
			val[i] += v.val[i];
		}
		return this;
	}

	/**
	 * add coeff * v inside this
	 * 
	 * @param v
	 *            vector
	 * @param coeff
	 *            coefficient
	 * @return this
	 */
	public Coords addInsideMul(Coords v, double coeff) {
		for (int i = 0; i < v.val.length; i++) {
			val[i] += coeff * v.val[i];
		}
		return this;
	}

	/**
	 * add v inside this
	 * 
	 * @param v
	 *            value
	 */
	public void addInside(double v) {
		for (int i = 0; i < rows; i++) {
			val[i] += v;
		}
	}

	/**
	 * 
	 * @param val0
	 *            val0
	 * @return
	 * 
	 * 		deprecated create vector and use {@link #setMul(Coords, double)}
	 *         instead
	 */

	public Coords mul(double val0) {

		Coords result = new Coords(rows);

		for (int i = 0; i < rows; i++) {
			result.val[i] = val[i] * val0;
		}

		return result;
	}

	/**
	 * @return a copy of this coords object
	 */
	public Coords copy() {

		Coords result = new Coords(rows);

		for (int i = 0; i < rows; i++) {
			result.val[i] = val[i];
		}

		return result;
	}

	/**
	 * 
	 * @param val0
	 *            factor
	 * @param res
	 *            gets this * val0
	 */
	public void mul(double val0, Coords res) {
		for (int i = 0; i < res.rows && i < rows; i++) {
			res.val[i] = val[i] * val0;
		}
	}

	/**
	 * set this to v * val0
	 * 
	 * @param v
	 *            vector
	 * @param val0
	 *            value
	 * @return this
	 */
	public Coords setMul(Coords v, double val0) {
		for (int i = 0; i < rows && i < v.rows; i++) {
			val[i] = v.val[i] * val0;
		}

		return this;
	}

	/**
	 * set this to v * val0 (only 3 first values)
	 * 
	 * @param v
	 *            vector
	 * @param val0
	 *            value
	 * @return this
	 */
	public Coords setMul3(Coords v, double val0) {
		for (int i = 0; i < 3; i++) {
			val[i] = v.val[i] * val0;
		}

		return this;
	}

	/**
	 * 
	 * @param val0
	 *            factor
	 * @param res
	 *            gets this * val0
	 */
	public void mul(double val0, double[] res) {
		for (int i = 0; i < res.length && i < rows; i++) {
			res[i] = val[i] * val0;
		}
	}

	/**
	 * 
	 * @param v
	 *            vector
	 * @param res
	 *            gets this + v
	 */
	public void add(Coords v, Coords res) {
		for (int i = 0; i < res.rows; i++) {
			res.val[i] = v.val[i] + val[i];
		}
	}

	/**
	 * set this to v1 + v2
	 * 
	 * @param v1
	 *            vector
	 * @param v2
	 *            vector
	 * @return this
	 */
	public Coords setAdd(Coords v1, Coords v2) {
		for (int i = 0; i < rows; i++) {
			val[i] = v1.val[i] + v2.val[i];
		}

		return this;
	}

	/**
	 * set this to v1 + v2 (for 3 first coords)
	 * 
	 * @param v1
	 *            vector
	 * @param v2
	 *            vector
	 * @return this
	 */
	public Coords setAdd3(Coords v1, Coords v2) {
		for (int i = 0; i < 3; i++) {
			val[i] = v1.val[i] + v2.val[i];
		}

		return this;
	}

	/**
	 * set this as barycenter for vectors in v
	 * 
	 * @param v
	 *            vectors
	 * @return this
	 */
	public Coords setBarycenter(Coords... v) {
		double f = 1.0 / v.length;
		for (int i = 0; i < rows; i++) {
			val[i] = 0;
			for (int j = 0; j < v.length; j++) {
				val[i] += v[j].val[i];
			}
			val[i] *= f;
		}

		return this;
	}

	/**
	 * if the ND hom coords is in x-y plane, return this coords
	 * 
	 * @return
	 * 
	 * 		deprecated create 3 rows vector and use
	 *         {@link #setCoordsIn2DView(Coords)} instead
	 */

	public Coords getCoordsIn2DView() {

		int dim = rows - 1;
		switch (dim) {
		case 2:
			return new Coords(getX(), getY(), getZ());
		case -1:
		case 0:
			return new Coords(0, 0, getX());
		case 1:
			return new Coords(getX(), 0, getY());
		default:
			for (int i = 3; i <= dim; i++) {
				if (Double.isNaN(get(i)) || !DoubleUtil.isZero(get(i))) {
					return new Coords(Double.NaN, Double.NaN, Double.NaN);
				}
			}
			// get(3) to get(dim) are all zero
			return new Coords(get(1), get(2), get(dim + 1));
		}
	}

	/**
	 * if the ND hom coords is in x-y plane, set this to v coords
	 * 
	 * @param v
	 *            vector
	 * @return this
	 * 
	 */
	public Coords setCoordsIn2DView(Coords v) {

		int dim = v.rows - 1;
		switch (dim) {
		case 2:
			setX(v.getX());
			setY(v.getY());
			setZ(v.getZ());
			break;
		case -1:
		case 0:
			setX(0);
			setY(0);
			setZ(v.getX());
			break;
		case 1:
			setX(v.getX());
			setY(0);
			setZ(v.getY());
			break;
		default:
			for (int i = 3; i <= dim; i++) {
				if (Double.isNaN(v.get(i)) || !DoubleUtil.isZero(v.get(i))) {
					setX(Double.NaN);
					setY(Double.NaN);
					setZ(Double.NaN);
					return this;
				}
			}
			// get(3) to get(dim) are all zero
			setX(v.get(1));
			setY(v.get(2));
			setZ(v.get(dim + 1));
		}

		return this;
	}

	/**
	 * this=(r,g,b,...) color representation
	 * 
	 * @return gray scale intensity
	 */
	public double getGrayScale() {
		return 0.2989 * getX() + 0.5870 * getY() + 0.1140 * getZ();
	}

	/**
	 * Convert to (greyScale, greyScale, greyScale), see getGrayScale().
	 */
	public void convertToGrayScale() {
		double gray = getGrayScale();
		setX(gray);
		setY(gray);
		setZ(gray);
	}

	/**
	 * assume this is equal to (x,y,z,w)
	 * 
	 * @return true if define a defined point
	 */
	public boolean isPointDefined() {
		if (DoubleUtil.isZero(getW())) {
			return false;
		}

		return isDefined();
	}

	/**
	 * returns false if one value equals NaN
	 * 
	 * @return false if one value equals NaN
	 */
	@Override
	public boolean isDefined() {
		if (val == null) {
			return false;
		}

		for (int i = 0; i < rows; i++) {
			if (Double.isNaN(val[i])) {
				return false;
			}
		}

		return true;
	}

	/**
	 * set this to undefined
	 */
	@Override
	public void setUndefined() {
		val[0] = Double.NaN;
	}

	/**
	 * set all values to Double.POSITIVE_INFINITY
	 */
	public void setPositiveInfinity() {
		for (int i = 0; i < rows; i++) {
			val[i] = Double.POSITIVE_INFINITY;
		}
	}

	/**
	 * set all values to Double.NEGATIVE_INFINITY
	 */
	public void setNegativeInfinity() {
		for (int i = 0; i < rows; i++) {
			val[i] = Double.NEGATIVE_INFINITY;
		}
	}

	/**
	 * set 3 floats array
	 * 
	 * @param ret
	 *            output array
	 */
	public void get3ForGL(float[] ret) {
		for (int i = 0; i < 3; i++) {
			ret[i] = (float) val[i];
		}
	}

	/**
	 * set 4 floats array
	 * 
	 * @param ret
	 *            output array
	 */
	public void get4ForGL(float[] ret) {
		for (int i = 0; i < 4; i++) {
			ret[i] = (float) val[i];
		}
	}

	/**
	 * check for first non-zero value ; reverse all values if this one is
	 * negative
	 */
	public void checkReverseForFirstValuePositive() {
		boolean zero = true;
		int i = 0;
		while (i < val.length && zero) {
			if (!DoubleUtil.isZero(val[i])) {
				zero = false;
			} else {
				i++;
			}
		}

		if (!zero && val[i] < 0) {
			while (i < val.length) {
				val[i] *= -1;
				i++;
			}
		}

	}

	/**
	 * returns double[] describing the matrix for openGL
	 * 
	 * @return the matrix as a double[]
	 */
	public double[] get() {
		return val;
	}

	/**
	 * get values and set it in ret
	 * 
	 * @param ret
	 *            ret
	 */
	public void get(double[] ret) {
		for (int i = 0; i < ret.length; i++) {
			ret[i] = val[i];
		}
	}

	/**
	 * get values and set it in ret
	 * 
	 * @param ret
	 *            ret
	 */
	public void get(float[] ret) {
		for (int i = 0; i < ret.length; i++) {
			ret[i] = (float) val[i];
		}
	}

	/** @return false if at least one value is infinite */
	public boolean isFinite() {
		for (int i = 0; i < rows; i++) {
			if (Double.isInfinite(val[i])) {
				return false;
			}
		}

		return true;
	}

	/**
	 * multiply all values by v
	 * 
	 * @param v
	 *            factor
	 * @return this
	 */
	public Coords mulInside(double v) {
		for (int i = 0; i < val.length; i++) {
			val[i] *= v;
		}

		return this;
	}

	/**
	 * mul 3 first values by v
	 * 
	 * @param v
	 *            value
	 * @return this
	 */
	public Coords mulInside3(double v) {
		for (int i = 0; i < 3; i++) {
			val[i] *= v;
		}
		return this;
	}

	/**
	 * mul x, y, z by factors
	 * 
	 * @param sx
	 *            x scale
	 * @param sy
	 *            y scale
	 * @param sz
	 *            z scale
	 */
	public void mulInside(double sx, double sy, double sz) {
		val[0] *= sx;
		val[1] *= sy;
		val[2] *= sz;
	}
	
	/**
	 * mul coords by x, y, z factors
	 * 
	 * @param coords
	 *            coords
	 * @param sx
	 *            x scale
	 * @param sy
	 *            y scale
	 * @param sz
	 *            z scale
	 */
	public void setMul(Coords coords, double sx, double sy, double sz) {
		val[0] = coords.val[0] * sx;
		val[1] = coords.val[1] * sy;
		val[2] = coords.val[2] * sz;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("(");

		for (int i = 0; i < val.length; i++) {
			s.append(val[i]);
			s.append(i == val.length - 1 ? ')' : ',');
		}

		return s.toString();
	}

	/**
	 * 
	 * @param precision
	 *            decimal precision
	 * @return string representation with decimal precision
	 */
	public String toString(int precision) {
		ScientificFormatAdapter nf = FormatFactory.getPrototype()
				.getScientificFormat(2, 10, false);
		StringBuilder s = new StringBuilder("(");
		for (int i = 0; i < val.length; i++) {
			if (val[i] > 0) {
				s.append('+');
			}
			if (val[i] == 0) {
				s.append("+0.").append(StringUtil.repeat('0', precision));
			} else {
				StringUtil.appendFormat(s, val[i], nf);
			}
			s.append(i == val.length - 1 ? ")" : "  ");
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
		StringBuilder s = new StringBuilder("(");
		for (int i = 0; i < val.length; i++) {
			StringUtil.toString(val[i], digits, precision, s);
			s.append(i == val.length - 1 ? ")" : "  ");
		}
		return s.toString();
	}

	/**
	 * set this = m*v
	 * 
	 * @param m
	 *            matrix
	 * @param v
	 *            vector
	 * @return this
	 */
	public Coords setMul(CoordMatrix m, Coords v) {

		for (int i = 1; i <= getLength(); i++) {

			double r = 0;
			for (int n = 1; n <= m.getColumns(); n++) {
				r += m.get(i, n) * v.get(n);
			}

			set(i, r);
		}

		return this;
	}

	/**
	 * set this = m*v for x, y, z
	 * 
	 * @param m
	 *            matrix
	 * @param v
	 *            vector
	 * @return this
	 */
	public Coords setMul3(CoordMatrix m, Coords v) {
		for (int i = 1; i <= 3; i++) {
			double r = 0;
			for (int n = 1; n <= m.getColumns(); n++) {
				r += m.get(i, n) * v.get(n);
			}
			set(i, r);
		}

		return this;
	}

	/**
	 * set this = m*v
	 * 
	 * @param m
	 *            matrix
	 * @param v
	 *            vector
	 * @return this
	 */
	public Coords setMul(CoordMatrix m, double[] v) {

		for (int i = 1; i <= getLength(); i++) {

			double r = 0;
			for (int j = 0; j < v.length; j++) {
				r += m.get(i, j + 1) * v[j];
			}

			set(i, r);
		}

		return this;
	}

	/**
	 * set this = m*(x,y,z,1)
	 * 
	 * @param m
	 *            matrix
	 * @param x
	 *            point x coord
	 * @param y
	 *            point y coord
	 * @param z
	 *            point z coord
	 * @return this
	 */
	public Coords setMulPoint(CoordMatrix m, double x, double y, double z) {

		for (int i = 1; i <= getLength(); i++) {
			set(i, m.get(i, 1) * x + m.get(i, 2) * y + m.get(i, 3) * z
					+ m.get(i, 4));
		}

		return this;
	}

	/**
	 * @return true if not a final (constant) undefined Coords
	 */
	public boolean isNotFinalUndefined() {
		return true;
	}

	/**
	 * @return true if a final (constant) undefined Coords
	 */
	public boolean isFinalUndefined() {
		return false;
	}

	/**
	 * set 2D barycenter from the two points
	 * 
	 * @param param1
	 *            param1
	 * @param param2
	 *            param2
	 * @param leftPoint
	 *            leftPoint
	 * @param rightPoint
	 *            rightPoint
	 */
	public void set(double param1, double param2, MyPoint leftPoint,
			MyPoint rightPoint) {
		val[0] = param2 * leftPoint.x + param1 * rightPoint.x;
		val[1] = param2 * leftPoint.y + param1 * rightPoint.y;
		val[2] = 1.0;
	}

	@Override
	public void setAnimatableValue(Coords other) {
		set3(other);
	}

	@Override
	public boolean equalsForAnimation(Coords other) {
		boolean neg = false;
		boolean negSet = false;
		for (int i = 0; i < 3; i++) {
			if (negSet) {
				if (neg) {
					if (!DoubleUtil.isEqual(val[i], -other.val[i])) {
						return false;
					}
				} else {
					if (!DoubleUtil.isEqual(val[i], other.val[i])) {
						return false;
					}
				}
			} else {
				if (DoubleUtil.isEqual(val[i], 0)
						&& DoubleUtil.isEqual(other.val[i], 0)) {
					negSet = false;
				} else if (DoubleUtil.isEqual(val[i], other.val[i])) {
					neg = false;
					negSet = true;
				} else if (DoubleUtil.isEqual(val[i], -other.val[i])) {
					neg = true;
					negSet = true;
				} else {
					return false;
				}
			}
		}

		return true;
	}

}