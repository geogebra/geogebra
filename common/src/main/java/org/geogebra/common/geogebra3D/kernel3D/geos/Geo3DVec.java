/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * GeoVec2D.java
 *
 * Created on 31. August 2001, 11:34
 */

package org.geogebra.common.geogebra3D.kernel3D.geos;

import java.util.HashSet;

import org.apache.commons.math.complex.Complex;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVecInterface;

/**
 * 
 * @author Michael adapted from GeoVec2D
 * @version
 */
final public class Geo3DVec extends ValidExpression implements Vector3DValue,
		org.geogebra.common.kernel.kernelND.Geo3DVec {

	public double x = Double.NaN;
	public double y = Double.NaN;
	public double z = Double.NaN;
	private int mode = Kernel.COORD_CARTESIAN_3D;

	private Kernel kernel;

	/** Creates new GeoVec2D */
	public Geo3DVec(Kernel kernel) {
		this.kernel = kernel;
	}

	/** Creates new GeoVec3D with coordinates (x,y) */
	public Geo3DVec(Kernel kernel, double x, double y, double z) {
		this(kernel);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/** Copy constructor */
	public Geo3DVec(Geo3DVec v) {
		this(v.kernel);
		x = v.x;
		y = v.y;
		z = v.z;
		mode = v.mode;
	}

	public ExpressionValue deepCopy(Kernel kernel1) {
		return new Geo3DVec(this);
	}

	public void resolveVariables() {
		// no variables ?
	}

	/** Creates new GeoVec3D as vector between Points P and Q */
	public Geo3DVec(Kernel kernel, GeoPoint3D p, GeoPoint3D q) {
		this(kernel);
		x = q.getX() - p.getX();
		y = q.getY() - p.getY();
		z = q.getZ() - p.getZ();
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public void setCoords(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setCoords(double[] a) {
		x = a[0];
		y = a[1];
		z = a[2];
	}

	public void setCoords(GeoVec3D v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	final public double getX() {
		return x;
	}

	final public double getY() {
		return y;
	}

	final public double getZ() {
		return z;
	}

	// final public double getR() { return length(x, y); }
	// final public double getPhi() { return Math.atan2(y, x); }

	final public double[] getCoords() {
		double[] res = { x, y, z };
		return res;
	}

	/**
	 * Calculates the eucilidian length of this 2D vector. The result is
	 * sqrt(x^2 + y^2).
	 */
	final public double length() {
		return length(x, y, z);
	}

	/**
	 * Calculates the eucilidian length of this 2D vector. The result is
	 * sqrt(a[0]^2 + a[1]^2).
	 */
	final public static double length(double[] a) {
		return length(a[0], a[1], a[2]);
	}

	/**
	 * Calculates the euclidian length sqrt(a^2 + b^2).
	 */
	final public static double length(double a, double b, double c) {
		return Math.sqrt(a * a + b * b + c * c);

	}

	/**
	 * Changes this vector to a vector with the same direction and orientation,
	 * but length 1.
	 */
	final public void makeUnitVector() {
		double len = this.length();
		x = x / len;
		y = y / len;
	}

	/**
	 * Yields true if the coordinates of this vector are equal to those of
	 * vector v.
	 */
	final public boolean equals(GeoVec2D v) {
		return Kernel.isEqual(x, v.getX()) && Kernel.isEqual(y, v.getY());
	}

	/**
	 * Yields true if this vector and v are linear dependent This is done by
	 * calculating the determinant of this vector an v: this = v <=> det(this,
	 * v) = nullvector.
	 */
	// final public boolean linDep(GeoVec2D v) {
	// // v = l* w <=> det(v, w) = o
	// return kernel.isZero(det(this, v));
	// }

	/**
	 * calculates the determinant of u and v. det(u,v) = u1*v2 - u2*v1
	 */

	/** returns this + a */
	// /final public GeoVec2D add(GeoVec2D a) {
	// / GeoVec2D res = new GeoVec2D(kernel, 0,0);
	// add(this, a, res);
	// return res;
	// }

	/** c = a + b */
	final public static void add(Geo3DVec a, Geo3DVec b, Geo3DVec c) {
		c.x = a.x + b.x;
		c.y = a.y + b.y;
		c.z = a.z + b.z;
	}

	/** c = a + b */
	final public static void add(Geo3DVec a, GeoVec2D b, Geo3DVec c) {
		c.x = a.x + b.getX();
		c.y = a.y + b.getY();
		c.z = a.z;
	}

	/** c = a + b */
	final public static void add(GeoVec2D a, Geo3DVec b, Geo3DVec c) {
		c.x = a.getX() + b.x;
		c.y = a.getY() + b.y;
		c.z = b.z;
	}

	/** c = Vector (Cross) Product of a and b */
	final public static void vectorProduct(GeoVecInterface a,
			GeoVecInterface b, Geo3DVec c) {
		// tempX/Y needed because a and c can be the same variable
		double tempX = a.getY() * b.getZ() - a.getZ() * b.getY();
		double tempY = -a.getX() * b.getZ() + a.getZ() * b.getX();
		c.z = a.getX() * b.getY() - a.getY() * b.getX();
		c.x = tempX;
		c.y = tempY;
	}

	/** returns this - a */
	// final public GeoVec2D sub(GeoVec2D a) {
	// GeoVec2D res = new GeoVec2D(kernel, 0,0);
	// sub(this, a, res);
	// return res;
	// }

	/** c = a - b */
	final public static void sub(Geo3DVec a, Geo3DVec b, Geo3DVec c) {
		c.x = a.x - b.x;
		c.y = a.y - b.y;
		c.z = a.z - b.z;
	}

	/** c = a - b */
	final public static void sub(Geo3DVec a, GeoVec2D b, Geo3DVec c) {
		c.x = a.x - b.getX();
		c.y = a.y - b.getY();
		c.z = a.z;
	}

	/** c = a - b */
	final public static void sub(GeoVec2D a, Geo3DVec b, Geo3DVec c) {
		c.x = a.getX() - b.x;
		c.y = a.getY() - b.y;
		c.z = -b.z;
	}

	/** c = a * b */
	final public static void mult(Geo3DVec a, double b, Geo3DVec c) {
		c.x = a.x * b;
		c.y = a.y * b;
		c.z = a.z * b;
	}

	final public static void inner(GeoVecInterface a, GeoVecInterface b,
			MyDouble c) {
		c.set(a.getX() * b.getX() + a.getY() * b.getY() + a.getZ() * b.getZ());
	}

	final public static void complexMultiply(GeoVecInterface a,
			GeoVecInterface b, GeoVec2D c) {

		if (!Kernel.isZero(a.getZ()) || !Kernel.isZero(b.getZ())) {
			c.setX(Double.NaN);
			c.setY(Double.NaN);
			c.setMode(Kernel.COORD_COMPLEX);
			return;
		}

		Complex out = new Complex(a.getX(), a.getY());
		out = out.multiply(new Complex(b.getX(), b.getY()));
		c.setX(out.getReal());
		c.setY(out.getImaginary());

		c.setMode(Kernel.COORD_COMPLEX);
	}

	/** c = a / b */
	final public static void div(Geo3DVec a, double b, Geo3DVec c) {
		c.x = a.x / b;
		c.y = a.y / b;
		c.z = a.z / b;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append('(');
		sbToString.append(kernel.format(x, tpl));
		sbToString.append(", ");
		sbToString.append(kernel.format(y, tpl));
		sbToString.append(')');
		return sbToString.toString();
	}

	private StringBuilder sbToString = new StringBuilder(50);

	/**
	 * interface VectorValue implementation
	 */
	final public Geo3DVec getVector() {
		return this;
	}

	final public boolean isConstant() {
		return true;
	}

	final public boolean isLeaf() {
		return true;
	}

	final public HashSet<GeoElement> getVariables() {
		return null;
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(tpl);
	}

	final public boolean isNumberValue() {
		return false;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	@Override
	public boolean evaluatesTo3DVector() {
		return true;
	}

	public double[] getPointAsDouble() {
		return new double[] { getX(), getY(), getZ() };
	}

	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	public Kernel getKernel() {
		return kernel;
	}

	public boolean isEqual(org.geogebra.common.kernel.kernelND.Geo3DVec vec) {
		Geo3DVec v = (Geo3DVec) vec;
		return Kernel.isEqual(x, v.x) && Kernel.isEqual(y, v.y)
				&& Kernel.isEqual(z, v.z);
	}

	/**
	 * multiplies 3D vector/point by a 3x3 matrix a b c d e f g h i
	 * 
	 * @param list
	 *            3x3 matrix
	 * @param rt
	 *            VectorNDValue (as ExpressionValue) to get coords from
	 */
	public void multiplyMatrix3x3(MyList list, VectorNDValue rt) {

		double a, b, c, d, e, f, g, h, i, xx, yy, zz;

		GeoVecInterface v = rt.getVector();
		xx = v.getX();
		yy = v.getY();
		zz = v.getZ();

		a = MyList.getCell(list, 0, 0).evaluateDouble();
		b = MyList.getCell(list, 1, 0).evaluateDouble();
		c = MyList.getCell(list, 2, 0).evaluateDouble();
		d = MyList.getCell(list, 0, 1).evaluateDouble();
		e = MyList.getCell(list, 1, 1).evaluateDouble();
		f = MyList.getCell(list, 2, 1).evaluateDouble();
		g = MyList.getCell(list, 0, 2).evaluateDouble();
		h = MyList.getCell(list, 1, 2).evaluateDouble();
		i = MyList.getCell(list, 2, 2).evaluateDouble();

		x = a * xx + b * yy + c * zz;
		y = d * xx + e * yy + f * zz;
		z = g * xx + h * yy + i * zz;

		return;
	}

	/**
	 * multiplies 3D vector/point by a 4x4 matrix a b c d e f g h i
	 * 
	 * @param list
	 *            4x4 matrix
	 * @param rt
	 *            VectorNDValue (as ExpressionValue) to get coords from
	 */
	public void multiplyMatrix4x4(MyList list, VectorNDValue rt) {

		double m, n, o, p, xx, yy, zz, ww;

		boolean vector = false;
		if (rt instanceof GeoPointND) { // 3D point
			GeoPointND point = (GeoPointND) rt;
			// use homogeneous coordinates
			Coords coords = point.getCoordsInD3();
			xx = coords.getX();
			yy = coords.getY();
			zz = coords.getZ();
			ww = coords.getW();
		} else {
			GeoVecInterface v = rt.getVector();
			xx = v.getX();
			yy = v.getY();
			zz = v.getZ();
			ww = 0;
			vector = true;
		}

		m = MyList.getCell(list, 0, 3).evaluateDouble();
		n = MyList.getCell(list, 1, 3).evaluateDouble();
		o = MyList.getCell(list, 2, 3).evaluateDouble();
		p = MyList.getCell(list, 3, 3).evaluateDouble();

		double w = m * xx + n * yy + o * zz + p * ww;

		if (vector && !Kernel.isZero(w)) {
			x = Double.NaN;
			y = Double.NaN;
			z = Double.NaN;
			return;
		}

		double a, b, c, d, e, f, g, h, i, j, k, l;

		a = MyList.getCell(list, 0, 0).evaluateDouble();
		b = MyList.getCell(list, 1, 0).evaluateDouble();
		c = MyList.getCell(list, 2, 0).evaluateDouble();
		d = MyList.getCell(list, 3, 0).evaluateDouble();

		e = MyList.getCell(list, 0, 1).evaluateDouble();
		f = MyList.getCell(list, 1, 1).evaluateDouble();
		g = MyList.getCell(list, 2, 1).evaluateDouble();
		h = MyList.getCell(list, 3, 1).evaluateDouble();

		i = MyList.getCell(list, 0, 2).evaluateDouble();
		j = MyList.getCell(list, 1, 2).evaluateDouble();
		k = MyList.getCell(list, 2, 2).evaluateDouble();
		l = MyList.getCell(list, 3, 2).evaluateDouble();

		x = a * xx + b * yy + c * zz + d * ww;
		y = e * xx + f * yy + g * zz + h * ww;
		z = i * xx + j * yy + k * zz + l * ww;

		if (!vector) {
			x = x / w;
			y = y / w;
			z = z / w;
		}

	}

	/**
	 * multiplies 3D vector/point by a 2x3 matrix a b d e g h
	 * 
	 * @param list
	 *            2x3 matrix
	 * @param rt
	 *            VectorNDValue (as ExpressionValue) to get coords from
	 */
	public void multiplyMatrix3x2(MyList list, VectorNDValue rt) {

		double a, b, d, e, g, h, xx, yy;

		GeoVecInterface v = rt.getVector();
		xx = v.getX();
		yy = v.getY();

		a = MyList.getCell(list, 0, 0).evaluateDouble();
		b = MyList.getCell(list, 1, 0).evaluateDouble();
		d = MyList.getCell(list, 0, 1).evaluateDouble();
		e = MyList.getCell(list, 1, 1).evaluateDouble();
		g = MyList.getCell(list, 0, 2).evaluateDouble();
		h = MyList.getCell(list, 1, 2).evaluateDouble();

		x = a * xx + b * yy;
		y = d * xx + e * yy;
		z = g * xx + h * yy;

		return;
	}

	/**
	 * multiplies 3D vector/point by a 2x3 matrix a b c d e f
	 * 
	 * @param list
	 *            2x3 matrix
	 * @param rt
	 *            VectorNDValue (as ExpressionValue) to get coords from
	 * @param ret
	 *            2D vector / point with computed coords
	 */
	static public void multiplyMatrix(MyList list, VectorNDValue rt,
			GeoVec2D ret) {

		double a, b, c, d, e, f, xx, yy, zz;

		GeoVecInterface v = rt.getVector();
		xx = v.getX();
		yy = v.getY();
		zz = v.getZ();

		a = MyList.getCell(list, 0, 0).evaluateDouble();
		b = MyList.getCell(list, 1, 0).evaluateDouble();
		c = MyList.getCell(list, 2, 0).evaluateDouble();
		d = MyList.getCell(list, 0, 1).evaluateDouble();
		e = MyList.getCell(list, 1, 1).evaluateDouble();
		f = MyList.getCell(list, 2, 1).evaluateDouble();

		ret.setCoords(a * xx + b * yy + c * zz, d * xx + e * yy + f * zz);

		return;
	}

	@Override
	public int getMode() {
		return this.mode;
	}

	public Geo3DVec round() {
		return new Geo3DVec(kernel, Math.round(x), Math.round(y), Math.round(z));
	}

	public Geo3DVec floor() {
		return new Geo3DVec(kernel, Math.floor(x), Math.floor(y), Math.floor(z));
	}

	public Geo3DVec ceil() {
		return new Geo3DVec(kernel, Math.ceil(x), Math.ceil(y), Math.ceil(z));
	}

	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	public double arg() {
		return Math.atan2(y, x);
	}

}
