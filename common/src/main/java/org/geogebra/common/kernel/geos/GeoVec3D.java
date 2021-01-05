/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoVec3D.java
 *
 * Created on 31. August 2001, 11:22
 */

package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.kernelND.CoordStyle;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 *
 * @author Markus
 */
public abstract class GeoVec3D extends GeoElement
		implements Traceable, CoordStyle {
	/** x coordinate */
	public double x = Double.NaN;
	/** y coordinate */
	public double y = Double.NaN;
	/** z coordinate */
	public double z = Double.NaN;

	private boolean trace;
	/**
	 * For backward compatibility
	 */
	public boolean hasUpdatePrevilege = false;
	private StringBuilder sbToString = new StringBuilder(50);

	/**
	 * @param c
	 *            construction
	 */
	public GeoVec3D(Construction c) {
		super(c);
	}

	/**
	 * Creates new GeoVec3D with coordinates (x,y,z) and label
	 * 
	 * @param c
	 *            construction
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public GeoVec3D(Construction c, double x, double y, double z) {
		this(c);
		setCoords(x, y, z);
	}

	@Override
	public boolean isDefined() {
		return (!(Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)));
	}

	@Override
	public void setUndefined() {
		setUndefinedCoords();
		updateGeo(false); // TODO hide undefined elements in algebraView
	}

	/**
	 * set undefined coords
	 */
	protected void setUndefinedCoords() {
		setCoords(Double.NaN, Double.NaN, Double.NaN);
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined();
	}

	@Override
	public void set(GeoElementND geo) {
		if (geo instanceof GeoVec3D) {
			GeoVec3D v = (GeoVec3D) geo;
			setCoords(v.x, v.y, v.z);
			reuseDefinition(geo);
		} else {
			setUndefined();
		}
	}

	/**
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public abstract void setCoords(double x, double y, double z);

	/**
	 * Set coords from source vector
	 * 
	 * @param v
	 *            source vector
	 */
	public abstract void setCoords(GeoVec3D v);

	/**
	 * @return x-coord
	 */
	final public double getX() {
		return x;
	}

	/**
	 * @return y-coord
	 */
	final public double getY() {
		return y;
	}

	/**
	 * @return z-coord
	 */
	final public double getZ() {
		return z;
	}

	/**
	 * @param ret
	 *            array to store coords
	 */
	final public void getCoords(double[] ret) {
		ret[0] = x;
		ret[1] = y;
		ret[2] = z;
	}

	/**
	 * @return this vector as coords
	 */
	final public Coords getCoords() {
		Coords coords = new Coords(x, y, z);
		return coords;
	}

	/**
	 * Writes x and y to the array res.
	 * 
	 * @param res
	 *            array to store x and y
	 */
	public void getInhomCoords(double[] res) {
		res[0] = x;
		res[1] = y;
	}

	// POLAR or CARTESIAN mode
	/**
	 * @return true if using POLAR style
	 */
	final public boolean isPolar() {
		return getToStringMode() == Kernel.COORD_POLAR;
	}

	/**
	 * Sets the coord style
	 * 
	 * @param mode
	 *            new coord style
	 */
	@Override
	public void setMode(int mode) {
		toStringMode = mode;
	}

	/**
	 * Changes coord style to POLAR
	 */
	@Override
	public void setPolar() {
		toStringMode = Kernel.COORD_POLAR;
	}

	/**
	 * Changes coord style to CARTESIAN
	 */
	@Override
	public void setCartesian() {
		toStringMode = Kernel.COORD_CARTESIAN;
	}

	/**
	 * Changes coord style to COMPLEX
	 */
	@Override
	public void setComplex() {
		toStringMode = Kernel.COORD_COMPLEX;
	}

	/**
	 * Changes coord style to CARTESIAN 3D
	 */
	@Override
	public void setCartesian3D() {
		toStringMode = Kernel.COORD_CARTESIAN_3D;
	}

	@Override
	public void setSpherical() {
		setMode(Kernel.COORD_SPHERICAL);
	}

	@Override
	public boolean isTraceable() {
		return true;
	}

	@Override
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	@Override
	public boolean getTrace() {
		return trace;
	}

	// G.Sturr 2010-5-14: no longer needed
	/*
	 * public void setSpreadsheetTrace(boolean spreadsheetTrace) {
	 * this.spreadsheetTrace = spreadsheetTrace;
	 * 
	 * if (spreadsheetTrace) resetTraceColumns(); }
	 * 
	 * 
	 * 
	 * public boolean getSpreadsheetTrace() { return spreadsheetTrace; }
	 * 
	 */
	// END G.Sturr

	/**
	 * Yields true if this vector and v are linear dependent This is done by
	 * calculating the cross product of this vector an v: this lin.dep. v
	 * <=> this.cross(v) = nullvector.
	 * 
	 * @param v
	 *            other vector
	 * @return true if this and other vector are linear dependent
	 */
	final public boolean linDep(GeoVec3D v) {
		// v lin.dep this <=> angle(v,w) ~ 0 <=> cross(v,w) << |this|*|v|
		// use Math.max(abs coords) as norm to avoid overflow / underflow
		double n1 = Math.max(Math.abs(x), Math.max(Math.abs(y), Math.abs(z)));
		double n2 = Math.max(Math.abs(v.x),
				Math.max(Math.abs(v.y), Math.abs(v.z)));

		double x1 = x / n1;
		double y1 = y / n1;
		double z1 = z / n1;

		double x2 = v.x / n2;
		double y2 = v.y / n2;
		double z2 = v.z / n2;

		return DoubleUtil.isEqual(x1 * y2, x2 * y1)
				&& DoubleUtil.isEqual(z1 * y2, z2 * y1)
				&& DoubleUtil.isEqual(x1 * z2, x2 * z1);
	}

	/**
	 * @return tue if all coords are zero
	 */
	final public boolean isZero() {
		return DoubleUtil.isZero(x) && DoubleUtil.isZero(y) && DoubleUtil.isZero(z);
	}

	/**
	 * Calculates the cross product of this vector and vector v. The result ist
	 * returned as a new GeoVec3D.
	 */
	// final public GeoVec3D cross(GeoVec3D v) {
	// GeoVec3D res = new GeoVec3D(v.cons);
	// cross(this, v, res);
	// return res;
	// }

	/**
	 * Calculates the cross product of vectors u and v. The result is stored in
	 * w.
	 * 
	 * @param u
	 *            vector u
	 * @param v
	 *            vector v
	 * @param w
	 *            vector to store u x v
	 */
	final public static void cross(GeoVec3D u, GeoVec3D v, GeoVec3D w) {
		w.setCoords(u.y * v.z - u.z * v.y, u.z * v.x - u.x * v.z,
				u.x * v.y - u.y * v.x);
	}

	/**
	 * Calculates the cross product of vectors u and v.
	 * 
	 * @param u
	 *            vector u
	 * @param v
	 *            vector v
	 * @return the cross product of vectors u and v.
	 */
	final public static Coords cross(GeoVec3D u, GeoVec3D v) {
		Coords ret = new Coords(3);
		ret.setX(u.y * v.z - u.z * v.y);
		ret.setY(u.z * v.x - u.x * v.z);
		ret.setZ(u.x * v.y - u.y * v.x);

		return ret;

	}

	/**
	 * Calculates the line through the points A and B. The result is stored in
	 * g.
	 * 
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @param g
	 *            line to store the result
	 */
	final public static void lineThroughPoints(GeoPoint A, GeoPoint B,
			GeoLine g) {
		// note: this could be done simply using cross(A, B, g)
		// but we want to avoid large coefficients in the line
		// and we want AB to be the direction vector of the line

		if (!(A.isDefined() && B.isDefined())) {
			g.setUndefined();
			return;
		}

		if (A.isInfinite()) { // A is direction
			if (B.isInfinite()) {
				// g is undefined
				g.setUndefined();
			} else {
				// through point B
				g.setCoords(A.getY(), -A.getX(),
						A.getX() * B.getInhomY() - A.getY() * B.getInhomX());
			}
		} else { // through point A
			if (B.isInfinite()) {
				// B is direction
				g.setCoords(-B.getY(), B.getX(),
						A.getInhomX() * B.getY() - A.getInhomY() * B.getX());
			} else {
				// through point B
				g.setCoords(A.getInhomY() - B.getInhomY(),
						B.getInhomX() - A.getInhomX(),
						A.getInhomX() * B.getInhomY()
								- A.getInhomY() * B.getInhomX());
			}
		}
	}

	/**
	 * Calculates the line through the points A and B. The result is stored in
	 * g.
	 * 
	 * @param A
	 *            first point
	 * @param B
	 *            second point
	 * @param g
	 *            line to store result
	 */
	final public static void lineThroughPointsCoords(Coords A, Coords B,
			GeoLine g) {
		// note: this could be done simply using cross(A, B, g)
		// but we want to avoid large coefficients in the line
		// and we want AB to be the direction vector of the line
		if (!(A.getLength() == 3 && B.getLength() == 3)) {
			g.setUndefined();
			return;
		}

		if (!(A.isFinite() && B.isFinite())) {
			g.setUndefined();
			return;
		}

		if (DoubleUtil.isZero(A.getZ())) { // A is direction
			if (DoubleUtil.isZero(B.getZ())) {
				// g is undefined
				g.setUndefined();
			} else {
				// through point B
				g.setCoords(A.getY(), -A.getX(),
						A.getX() * B.getInhom(1) - A.getY() * B.getInhom(0));
			}
		} else { // through point A
			if (DoubleUtil.isZero(B.getZ())) {
				// B is direction
				g.setCoords(B.getY(), -B.getX(),
						B.getX() * A.getInhom(1) - B.getY() * A.getInhom(0));
			} else {
				// through point B
				double aInhomX = A.getInhom(0);
				double aInhomY = A.getInhom(1);
				double bInhomX = B.getInhom(0);
				double bInhomY = B.getInhom(1);
				g.setCoords(aInhomY - bInhomY, bInhomX - aInhomX,
						aInhomX * bInhomY - aInhomY * bInhomX);
			}
		}
	}

	/**
	 * Calculates the line through the point A with direction v. The result is
	 * stored in g.
	 * 
	 * @param A
	 *            start point
	 * @param v
	 *            direction vector
	 * @param g
	 *            line to store result
	 */
	final public static void lineThroughPointVector(GeoPoint A, GeoVec3D v,
			GeoLine g) {
		// note: this could be done simply using cross(A, v, g)
		// but we want to avoid large coefficients in the line
		// and we want v to be the direction vector of the line

		if (A.isInfinite()) { // A is direction
			g.setUndefined();
		} else { // through point A
					// v is direction
			g.setCoords(-v.y, v.x, A.getInhomX() * v.y - A.getInhomY() * v.x);
		}
	}

	/**
	 * Calculates the cross product of vectors u and v. The result is stored in
	 * w.
	 * 
	 * @param u
	 *            vector u
	 * @param vx
	 *            x(v)
	 * @param vy
	 *            y(v)
	 * @param vz
	 *            z(v)
	 * @param w
	 *            vector to store u * v
	 */
	final public static void cross(GeoVec3D u, double vx, double vy, double vz,
			GeoVec3D w) {

		double x = u.y * vz - u.z * vy;
		double y = u.z * vx - u.x * vz;
		double z = u.x * vy - u.y * vx;

		// more accurate but slower
		// could be used if needed
		// from
		// https://github.com/apache/commons-math/blob/3.6.1-release/src/main/java/org/apache/commons/math3/geometry/euclidean/threed/Vector3D.java
		// double x = MathArrays.linearCombination(u.y, vz, -u.z, vy);
		// double y = MathArrays.linearCombination(u.z, vx, -u.x, vz);
		// double z = MathArrays.linearCombination(u.x, vy, -u.y, vx);

		w.setCoords(x, y, z);
	}

	/**
	 * Calculates the cross product of vectors u and v. The result is stored in
	 * w.
	 * 
	 * @param ux
	 *            x(u)
	 * @param uy
	 *            y(u)
	 * @param uz
	 *            z(u)
	 * @param vx
	 *            x(v)
	 * @param vy
	 *            y(v)
	 * @param vz
	 *            z(v)
	 * @param w
	 *            vector to store u*v
	 */
	final public static void cross(double ux, double uy, double uz, double vx,
			double vy, double vz, GeoVec3D w) {
		w.setCoords(uy * vz - uz * vy, uz * vx - ux * vz, ux * vy - uy * vx);
	}

	/**
	 * Calculates the cross product of vectors u and v. The result is stored in
	 * w.
	 * 
	 * @param u
	 *            first input
	 * @param v
	 *            second input
	 * @param w
	 *            vector to store u*v
	 */
	final public static void cross(double[] u, double[] v, double[] w) {
		w[0] = u[1] * v[2] - u[2] * v[1];
		w[1] = u[2] * v[0] - u[0] * v[2];
		w[2] = u[0] * v[1] - u[1] * v[0];
	}

	/**
	 * Calculates the inner product of this vector and vector v.
	 * 
	 * @param v
	 *            other vector
	 * @return inner product
	 */
	final public double inner(GeoVec3D v) {
		return x * v.x + y * v.y + z * v.z;
	}

	/**
	 * Changes orientation of this vector. v is changed to -v.
	 */
	final public void changeSign() {
		setCoords(-x, -y, -z);
	}

	/**
	 * returns -v
	 */
	// final public GeoVec3D getNegVec() {
	// return new GeoVec3D(cons, -x, -y, -z);
	// }

	/** returns this + a */
	// final public GeoVec3D add(GeoVec3D a) {
	// GeoVec3D res = new GeoVec3D(cons);
	// add(this, a, res);
	// return res;
	// }

	/**
	 * c = a + b
	 * 
	 * @param a
	 *            vector a
	 * @param b
	 *            vector b
	 * @param c
	 *            vector to store a+b
	 **/
	final public static void add(GeoVec3D a, GeoVec3D b, GeoVec3D c) {
		c.setCoords(a.x + b.x, a.y + b.y, a.z + b.z);
	}

	/** returns this - a */
	// final public GeoVec3D sub(GeoVec3D a) {
	// GeoVec3D res = new GeoVec3D(cons);
	// sub(this, a, res);
	// return res;
	// }

	/**
	 * c = a - b
	 * 
	 * @param a
	 *            vector a
	 * @param b
	 *            vector b
	 * @param c
	 *            vector to store a-b
	 */
	final public static void sub(GeoVec3D a, GeoVec3D b, GeoVec3D c) {
		c.setCoords(a.x - b.x, a.y - b.y, a.z - b.z);
	}

	@Override
	public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append('(');
		sbToString.append(x);
		sbToString.append(", ");
		sbToString.append(y);
		sbToString.append(", ");
		sbToString.append(z);
		sbToString.append(')');
		return sbToString.toString();
	}

	/**
	 * returns all class-specific xml tags for saveXML Geogebra File Format
	 */
	@Override
	protected void getXMLtags(StringBuilder sb) {
		super.getXMLtags(sb);

		sb.append("\t<coords");
		sb.append(" x=\"");
		sb.append(x);
		sb.append("\"");
		sb.append(" y=\"");
		sb.append(y);
		sb.append("\"");
		sb.append(" z=\"");
		sb.append(z);
		sb.append("\"");
		sb.append("/>\n");

	}

	@Override
	public void getXMLtagsMinimal(StringBuilder sb, StringTemplate tpl) {
		sb.append(regrFormat(x) + " " + regrFormat(y) + " " + regrFormat(z));
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	public void setZero() {
		x = 0;
		y = 0;
		z = 0;
	}

	/**
	 * @param phi
	 *            angle of rotation
	 */
	protected void rotateXY(NumberValue phi) {
		double ph = phi.getDouble();
		double cos = Math.cos(ph);
		double sin = Math.sin(ph);

		double x0 = x * cos - y * sin;
		y = x * sin + y * cos;
		x = x0;
	}

	/**
	 * mirror transform with angle phi [ cos(phi) sin(phi) ] [ sin(phi)
	 * -cos(phi) ]
	 * 
	 * @param phi
	 *            parameter of mirror transform
	 */
	protected final void mirrorXY(double phi) {
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		double x0 = x * cos + y * sin;
		y = x * sin - y * cos;
		x = x0;
	}

	@Override
	public boolean hasCoords() {
		return true;
	}

	@Override
	public boolean hasSpecialEditor() {
		return toStringMode != Kernel.COORD_COMPLEX && (isIndependent()
				|| getDefinition() != null && getDefinition().unwrap() instanceof MyVecNDNode);
	}
}
