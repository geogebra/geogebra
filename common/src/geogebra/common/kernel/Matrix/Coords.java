/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.Matrix;

import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

/**
 * 
 * A Ggb3DVector is composed of {x1,x2,...,xn} coordinates in double precision.
 * This class provides methods for basic linear algebra calculus.
 * 
 * @author ggb3D
 * 
 */
public class Coords extends CoordMatrix {

	private double norm, sqNorm;
	private boolean calcNorm = true;
	private boolean calcSqNorm = true;
	
	/** vx 3D vector */
	public static final Coords VX = new Coords(1,0,0,0);
	/** vy 3D vector */
	public static final Coords VY = new Coords(0,1,0,0);
	/** vz 3D vector */
	public static final Coords VZ = new Coords(0,0,1,0);

	// /////////////////////////////////////////////////:
	// Constructors

	/**
	 * creates a vector of the dimension specified by rows.
	 * 
	 * @param rows
	 *            number of rows
	 */
	public Coords(int rows) {

		super(rows, 1);

	}

	/**
	 * creates a vector with values vals
	 * 
	 * @param vals
	 *            values {x1, x2, ...}
	 */
	public Coords(double[] vals) {

		super(vals.length, 1);

		for (int i = 0; i < vals.length; i++)
			val[i] = vals[i];

	}

	/**
	 * creates a 2D vector with the specified values
	 * 
	 * @param u
	 * @param v
	 */
	public Coords(double u, double v) {
		super(2, 1);
		val[0] = u;
		val[1] = v;
	}

	/**
	 * creates a 3D vector with the specified values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Coords(double x, double y, double z) {
		super(3, 1);
		val[0] = x;
		val[1] = y;
		val[2] = z;
	}

	final static public Coords Coords4DLastEqualTo0(double x, double y, double z) {
		Coords ret = new Coords(4);
		ret.val[0] = x;
		ret.val[1] = y;
		ret.val[2] = z;
		return ret;
	}

	/**
	 * creates a 3D vector/point with the specified values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public Coords(double x, double y, double z, double w) {
		super(4, 1);
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
	 * sets v to vals0
	 * 
	 * @param vals0
	 *            values {x1, x2, ...}
	 */
	public void set(double[] vals0) {
		// Application.debug("-------------val.length = "+val.length+"\n-------------vals0.length = "+vals0.length);
		for (int i = 0; i < vals0.length; i++)
			val[i] = vals0[i];

		calcNorm = calcSqNorm = true;
	}
	
	/**
	 * set values from v
	 * @param v coords
	 */
	public void set(Coords v) {
		set(v.val);
	}
	

	@Override
	public void set(double val0) {
		super.set(val0);
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
		return val[getRows() - 1];
	}

	/**
	 * sets the "x-coord"
	 * 
	 * @param val
	 */
	public void setX(double val) {
		this.val[0] = val;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets the "y-coord"
	 * 
	 * @param val
	 */
	public void setY(double val) {
		this.val[1] = val;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets the "z-coord"
	 * 
	 * @param val
	 */
	public void setZ(double val) {
		this.val[2] = val;
		calcNorm = calcSqNorm = true;
	}

	/**
	 * sets the "w-coord"
	 * 
	 * @param val
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

		return this.getRows();

	}

	/**
	 * returns a copy of the vector
	 * 
	 * @return a copy of the vector
	 */
	public Coords copyVector() {

		Coords result = new Coords(rows);
		for (int i = 0; i < rows; i++)
			result.val[i] = val[i];

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
	 */
	public Coords subVector(int start, int end) {
		int r = end - start + 1;
		Coords result = new Coords(r);

		for (int i = 0; i < r; i++)
			result.val[i] = val[start + i - 1];

		return result;

	}

	/**
	 * returns the subvector composed of this without the row number row
	 * 
	 * @param row
	 *            number of the row to remove
	 * @return vector composed of this without the row number row
	 */
	public Coords subVector(int row) {
		int r = rows;
		Coords result = new Coords(r - 1);

		int shift = 0;
		for (int i = 0; i < r; i++) {
			if (i == row)
				shift = 1;
			else
				result.val[i] = val[i + shift];
		}

		return result;

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
		for (int i = 0; i < len; i++)
			res += val[i] * v.val[i];
		return res;
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
	 */
	final public Coords crossProduct(Coords v) {

		Coords ret = new Coords(3);

		ret.setCrossProduct(this, v);

		return ret;
	}

	/**
	 * 
	 * @param v
	 * @return 4-length vector equal to cross product this ^ v
	 */
	final public Coords crossProduct4(Coords v) {

		Coords ret = new Coords(4);

		ret.setCrossProduct(this, v);

		return ret;
	}

	/**
	 * set x,y,z values according to v1 ^ v2 cross product
	 * 
	 * @param v1
	 * @param v2
	 */
	final public void setCrossProduct(Coords v1, Coords v2) {
		val[0] = v1.val[1] * v2.val[2] - v1.val[2] * v2.val[1];
		val[1] = v1.val[2] * v2.val[0] - v1.val[0] * v2.val[2];
		val[2] = v1.val[0] * v2.val[1] - v1.val[1] * v2.val[0];
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
	 */
	public void calcNorm() {
		norm = Math.sqrt(this.dotproduct(this));
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
			sqNorm = this.dotproduct(this);
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
	 */
	public Coords normalized(boolean checkOneDirection) {
		Coords ret = new Coords(getLength());
		calcNorm();
		double normInv = 1 / getNorm();
		int len = getLength();
		for (int i = 0; i < len; i++) {
			double v = val[i] * normInv;
			// check if v is near to be one direction vector
			if (checkOneDirection && Kernel.isEqual(Math.abs(v), 1)) {
				if (v < 0)
					ret.val[i] = -1;
				else
					ret.val[i] = 1;
				for (int j = 0; j < i; j++)
					ret.val[j] = 0;
				for (int j = i + 1; j < len; j++)
					ret.val[j] = 0;
				break;
			}
			ret.val[i] = v;
		}
		return ret;
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

		if (recalcNorm)
			calcNorm();
		double normInv = 1 / getNorm();
		int len = getLength();
		for (int i = 0; i < len; i++)
			val[i] *= normInv;

		norm = sqNorm = 1.0;

		return this;
	}

	/**
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
	 * returns the distance between this and a 3D-line represented by the matrix
	 * {V O}
	 * 
	 * @param O
	 *            origin of the line
	 * @param V
	 *            direction of the line
	 * @return distance between this and the line
	 */
	public double distLine(Coords O, Coords V) {

		Coords OM = this.sub(O);
		Coords N = V.normalized();
		Coords OH = N.mul(OM.dotproduct(N)); // TODO optimize
		Coords HM = OM.sub(OH);

		return HM.norm();
	}

	/**
	 * returns this projected on the plane represented by the matrix (third
	 * vector used for direction). If direction is parallel to the plane, return
	 * infinite point (direction vector).
	 * <p>
	 * Attempt this to be of dimension 4, and the matrix to be of dimension 4*4.
	 * 
	 * @param m
	 *            matrix {v1 v2 v3 o} where (o,v1,v2) is a coord sys fo the
	 *            plane, and v3 the direction used for projection
	 * @return two vectors {globalCoords,inPlaneCoords}: the point projected,
	 *         and the original point in plane coords
	 */
	public Coords[] projectPlane(CoordMatrix m) {
		Coords inPlaneCoords, globalCoords;

		if (Kernel.isEqual(
				(m.getVx().crossProduct(m.getVy())).dotproduct(m.getVz()), 0,
				Kernel.STANDARD_PRECISION)) {
			// direction of projection is parallel to the plane : point is
			// infinite
			// Application.printStacktrace("infinity");
			inPlaneCoords = new Coords(new double[] { 0, 0, -1, 0 });
			globalCoords = m.getVz().copyVector();
		} else {
			// m*inPlaneCoords=this
			inPlaneCoords = m.solve(this);

			// globalCoords=this-inPlaneCoords_z*plane_vz
			globalCoords = this.add(m.getColumn(3).mul(-inPlaneCoords.get(3)));
		}

		return new Coords[] { globalCoords, inPlaneCoords };

	}

	/**
	 * returns this projected on the plane represented by the matrix, with
	 * vector v used for direction.
	 * <p>
	 * Attempt this to be of dimension 4, the matrix to be of dimension 4*4, and
	 * the vector to be of dimension 4.
	 * 
	 * @param m
	 *            matrix {v1 v2 ?? o} where (o,v1,v2) is a coord sys fo the
	 *            plane, and v3
	 * @param v
	 *            the direction used for projection
	 * @return two vectors {globalCoords,inPlaneCoords}: the point projected,
	 *         and the original point in plane coords
	 */
	public Coords[] projectPlaneThruV(CoordMatrix m, Coords v) {

		CoordMatrix m1 = new CoordMatrix(4, 4);
		m1.set(new Coords[] { m.getColumn(1), m.getColumn(2), v, m.getColumn(4) });

		return projectPlane(m1);
	}

	/**
	 * returns this projected on the plane represented by the matrix, with
	 * vector v used for direction.
	 * <p>
	 * If v is parallel to plane, then plane third vector is used instead
	 * 
	 * @param m
	 *            matrix {v1 v2 v3 o} where (o,v1,v2) is a coord sys fo the
	 *            plane, and v3
	 * @param v
	 *            the direction used for projection (v3 is used instead if v is
	 *            parallel to the plane)
	 * @return two vectors {globalCoords,inPlaneCoords}: the point projected,
	 *         and the original point in plane coords
	 */
	public Coords[] projectPlaneThruVIfPossible(CoordMatrix m, Coords v) {

		// check if v is parallel to plane
		Coords v3 = m.getColumn(3);
		if (Kernel.isEqual(v3.dotproduct(v), 0.0,
				Kernel.STANDARD_PRECISION))
			return projectPlane(m);

		// if not, use v for direction
		CoordMatrix m1 = new CoordMatrix(4, 4);
		m1.set(new Coords[] { m.getColumn(1), m.getColumn(2), v, m.getColumn(4) });

		return projectPlane(m1);
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
	 * @return two vectors {globalCoords,inPlaneCoords}: the point projected,
	 *         and the original point in plane coords
	 */
	public Coords[] projectPlaneThruVIfPossible(CoordMatrix m,
			Coords oldCoords, Coords v) {

		// Application.debug(this+"\nold=\n"+oldCoords);

		// check if v is parallel to plane
		Coords v3 = m.getColumn(3);
		if (Kernel.isZero(v3.dotproduct(v))) {
			Coords firstProjection = oldCoords.projectLine(this, v)[0];
			return firstProjection.projectPlane(m);
		}

		// if not, use v for direction
		CoordMatrix m1 = new CoordMatrix(4, 4);
		m1.set(new Coords[] { m.getColumn(1), m.getColumn(2), v, m.getColumn(4) });

		return projectPlane(m1);
	}

	/**
	 * calculates projection of this on the 3D-line represented by the matrix {V
	 * O}.
	 * 
	 * @param O
	 *            origin of the line
	 * @param V
	 *            direction of the line
	 * @return {point projected, {parameter on the line, normalized parameter} }
	 */
	public Coords[] projectLine(Coords O, Coords V) {

		Coords OM = this.sub(O);
		Coords N = V.normalized();
		double parameter = OM.dotproduct(N);
		Coords OH = N.mul(parameter);
		Coords H = O.add(OH); 

		return new Coords[] { H,
				new Coords(new double[] { parameter / V.norm(), parameter }) };
	}

	/**
	 * calculates projection of this as far as possible to the 3D-line
	 * represented by the matrix {V O} regarding V2 direction.
	 * 
	 * @param O
	 *            origin of the line
	 * @param V
	 *            direction of the line
	 * @param V2
	 *            direction of projection
	 * @return point projected
	 */
	public Coords projectNearLine(Coords O, Coords V, Coords V2) {

		Coords V3 = V.crossProduct(V2);

		if (Kernel.isEqual(V3.norm(), 0.0,
				Kernel.STANDARD_PRECISION)) {
			return this.copyVector();
		}
		CoordMatrix m = new CoordMatrix(4, 4);
		m.set(new Coords[] { V, V3, V2, O });
		return this.projectPlane(m)[0];
	}

	/**
	 * project this on the line (O,V) in the direction V2.
	 * <p>
	 * returns the point of (O,V) that is the nearest to line (this,V2).
	 * <p>
	 * if V and V2 are parallel, return O.
	 * 
	 * @param O
	 *            origin of the line where this is projected
	 * @param V
	 *            direction of the line where this is projected
	 * @param V2
	 *            direction of projection
	 * @return {point projected, {coord of the proj. point on the line, distance
	 *         between this and the proj. point}}
	 */
	public Coords[] projectOnLineWithDirection(Coords O, Coords V, Coords V2) {

		Coords V3 = V.crossProduct(V2);

		if (Kernel.isEqual(V3.norm(), 0.0,
				Kernel.STANDARD_PRECISION)) {
			return new Coords[] { O,
					new Coords(new double[] { 0, this.sub(O).norm() }) };
		}
		CoordMatrix m = new CoordMatrix(4, 4);
		m.set(new Coords[] { V2, V3, V, this });
		Coords[] result = O.projectPlane(m);
		return new Coords[] {
				result[0],
				new Coords(new double[] { -result[1].get(3),
						this.sub(result[0]).norm() }) };
	}

	/**
	 * returns this-v
	 * 
	 * @param v
	 *            vector subtracted
	 * @return this-v
	 */
	public Coords sub(Coords v) {
		int i;
		Coords result = new Coords(rows);
		for (i = 0; i < rows; i++)
			result.val[i] = val[i] - v.val[i];

		return result;
	}

	/**
	 * returns n-1 length vector, all coordinates divided by the n-th.
	 * <p>
	 * If this={x1,x2,xn}, it returns {x1/xn,x2/xn,...,x(n-1)}
	 * 
	 * @return {x1/xn,x2/xn,...,x(n-1)/xn}
	 */
	public Coords getInhomCoords() {
		int r = rows;
		Coords result = new Coords(r - 1);

		double wdiv = 1 / val[r - 1];
		for (int i = 0; i < r - 1; i++)
			result.val[i] = val[i] * wdiv;

		return result;
	}

	/**
	 * returns n length vector, all coordinates divided by the n-th.
	 * 
	 * @return {x1/xn,x2/xn,...,x(n-1)/xn,1}
	 */
	public Coords getInhomCoordsInSameDimension() {

		int r = rows;

		if (Kernel.isEqual(val[r - 1], 1))
			return this;

		Coords result = new Coords(r);

		double wdiv = 1 / val[r - 1];
		for (int i = 0; i < r - 1; i++)
			result.val[i] = val[i] * wdiv;

		result.val[r - 1] = 1;

		return result;
	}

	/**
	 * returns n length vector, all coordinates divided by the n-th.
	 * <p>
	 * If this={x1,x2,xn}, it returns {x1/xn,x2/xn,...,1}
	 * 
	 * @return {x1/xn,x2/xn,...,1}
	 */
	public Coords getCoordsLast1() {
		int len = getLength();
		Coords result = new Coords(len);
		double lastCoord = val[len - 1];
		if (lastCoord != 0.0) {
			double lastCoordInv = 1 / lastCoord;
			for (int i = 0; i < len; i++)
				result.val[i] = val[i] * lastCoordInv;
		} else
			result.set(this);
		return result;
	}

	/**
	 * 
	 * @return this with (n-1) coord removed
	 */
	public Coords projectInfDim() {
		int len = getLength();
		Coords result = new Coords(len - 1);
		for (int i = 0; i < len - 1; i++)
			result.val[i] = val[i];
		result.val[len - 2] = val[len - 1];
		return result;
	}

	/**
	 * Return true if this==v for the precision given (ie each coordinates are
	 * not different more than precision).
	 * 
	 * @param v
	 *            vector compared with
	 * @param precision
	 * @return true if the vectors are equal
	 */
	public boolean equalsForKernel(Coords v, double precision) {
		int len = getLength();
		for (int i = 0; i < len; i++)
			if (!Kernel.isEqual(val[i], v.val[i], precision))
				return false;

		return true;
	}

	public boolean isEqual(Coords v) {
		return equalsForKernel(v, Kernel.EPSILON);
	}

	/**
	 * check if all entries are zero
	 * 
	 * @param precision
	 * @return
	 */
	public boolean isZero() {
		int len = getLength();
		for (int i = 0; i < len; i++)
			if (!Kernel.isEqual(val[i], 0, Kernel.EPSILON))
				return false;
		return true;
	}

	/**
	 * Return true if all coordinates are not different from val more than
	 * precision.
	 * 
	 * @param val
	 *            value compared with
	 * @param precision
	 * @return true if all coordinates are not different from val more than
	 *         precision.
	 */
	public boolean equalsForKernel(double val, double precision) {
		int len = getLength();
		for (int i = 0; i < len; i++)
			if (!Kernel.isEqual(this.val[i], val, precision))
				return false;

		return true;

	}

	/**
	 * Assume that "this" is a non-zero vector in 3-space. This method returns
	 * an array v of two vectors {v[0], v[1]} (rows=4) so that (this, v[0],
	 * v[1]) is a right-handed orthonormal system.
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

		Coords vn2 = this.crossProduct(vn1);
		vn2.normalize();

		return new Coords[] { vn1, vn2 };
	}

	// ///////////////////////////////////////////////////
	// BASIC OPERATIONS
	// ///////////////////////////////////////////////////

	public Coords add(Coords v) {

		return (Coords) super.add(v);
	}

	@Override
	public Coords mul(double val0) {

		return (Coords) super.mul(val0);
	}

	// /////////////////////////////////////////////////:
	/** for testing the package */
	public static synchronized void main(String[] args) {

		Coords v1 = new Coords(2);
		v1.val[0] = 3.0;
		v1.val[1] = 4.0;

		App.debug("v1.v1 = " + v1.dotproduct(v1));
	}

	/**
	 * if the ND hom coords is in x-y plane, return this coords
	 * 
	 * @param coordsND
	 * @return
	 */
	public Coords getCoordsIn2DView() {

		int dim = getRows() - 1;
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
				if (Double.isNaN(get(i)) || !Kernel.isZero(get(i)))
					return new Coords(Double.NaN, Double.NaN, Double.NaN);
			}
			// get(3) to get(dim) are all zero
			return new Coords(get(1), get(2), get(dim + 1));
		}
	}

	/**
	 * this=(r,g,b,...) color representation
	 * 
	 * @return gray scale intensity
	 */
	public double getGrayScale() {
		return 0.2989 * getX() + 0.5870 * getY() + 0.1140 * getZ();
	}

	public void convertToGrayScale() {
		double gray = getGrayScale();
		setX(gray);
		setY(gray);
		setZ(gray);
	}
}