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

import org.apache.commons.math3.complex.Complex;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.Geo3DVecInterface;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVecInterface;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

import com.google.j2objc.annotations.Weak;

/**
 * 
 * @author Michael adapted from GeoVec2D
 */
final public class Geo3DVec extends ValidExpression
		implements Vector3DValue, Geo3DVecInterface {

	private double x = Double.NaN;
	private double y = Double.NaN;
	private double z = Double.NaN;
	private int mode = Kernel.COORD_CARTESIAN_3D;
	private StringBuilder sbToString = new StringBuilder(50);

	@Weak
	private Kernel kernel;

	/**
	 * Creates new GeoVec2D
	 * 
	 * @param kernel
	 *            kernel
	 */
	public Geo3DVec(Kernel kernel) {
		this.kernel = kernel;
	}

	/**
	 * Creates new GeoVec3D with coordinates (x,y)
	 * 
	 * @param kernel
	 *            kernel
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public Geo3DVec(Kernel kernel, double x, double y, double z) {
		this(kernel);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Copy constructor
	 * 
	 * @param v
	 *            original
	 */
	public Geo3DVec(Geo3DVec v) {
		this(v.kernel);
		x = v.x;
		y = v.y;
		z = v.z;
		mode = v.mode;
	}

	@Override
	public Geo3DVec deepCopy(Kernel kernel1) {
		return new Geo3DVec(this);
	}

	@Override
	public void resolveVariables(EvalInfo info) {
		// no variables ?
	}

	/**
	 * Creates new GeoVec3D as vector between Points P and Q
	 * 
	 * @param kernel
	 *            kernel
	 * @param p
	 *            start point
	 * @param q
	 *            end point
	 */
	public Geo3DVec(Kernel kernel, GeoPoint3D p, GeoPoint3D q) {
		this(kernel);
		x = q.getX() - p.getX();
		y = q.getY() - p.getY();
		z = q.getZ() - p.getZ();
	}

	/**
	 * @param x
	 *            x-coord
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @param y
	 *            y-coord
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @param z
	 *            z-coord
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public void setCoords(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * @param a
	 *            [x,y,z], further elements ignored
	 */
	public void setCoords(double[] a) {
		x = a[0];
		y = a[1];
		z = a[2];
	}

	/**
	 * Copy coords from source to this
	 * 
	 * @param v
	 *            source vector
	 */
	public void setCoords(GeoVec3D v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getZ() {
		return z;
	}

	/**
	 * Calculates the eucilidian length of this 2D vector. The result is
	 * sqrt(x^2 + y^2).
	 */
	@Override
	public double length() {
		return length(x, y, z);
	}

	/**
	 * Calculates the euclidian length sqrt(a^2 + b^2).
	 */
	private static double length(double a, double b, double c) {
		return Math.sqrt(a * a + b * b + c * c);
	}

	/**
	 * Changes this vector to a vector with the same direction and orientation,
	 * but length 1.
	 */
	public void makeUnitVector() {
		double len = this.length();
		x = x / len;
		y = y / len;
	}

	/**
	 * c = a + b
	 * 
	 * @param a
	 *            addend
	 * @param b
	 *            addend
	 * @param c
	 *            sum
	 */
	public static void add(Geo3DVec a, Geo3DVec b, Geo3DVec c) {
		c.x = a.x + b.x;
		c.y = a.y + b.y;
		c.z = a.z + b.z;
	}

	/**
	 * c = a + b
	 * 
	 * @param a
	 *            addend
	 * @param b
	 *            addend
	 * @param c
	 *            sum
	 */
	public static void add(Geo3DVec a, GeoVec2D b, Geo3DVec c) {
		c.x = a.x + b.getX();
		c.y = a.y + b.getY();
		c.z = a.z;
	}

	/**
	 * c = Vector (Cross) Product of a and b
	 * 
	 * @param a
	 *            vector
	 * @param b
	 *            vector
	 * @param c
	 *            product
	 */
	public static void vectorProduct(GeoVecInterface a, GeoVecInterface b,
			Geo3DVec c) {
		// tempX/Y needed because a and c can be the same variable
		double tempX = a.getY() * b.getZ() - a.getZ() * b.getY();
		double tempY = -a.getX() * b.getZ() + a.getZ() * b.getX();
		c.z = a.getX() * b.getY() - a.getY() * b.getX();
		c.x = tempX;
		c.y = tempY;
	}

	/**
	 * c = a - b
	 * 
	 * @param a
	 *            vector
	 * @param b
	 *            vector
	 * @param c
	 *            result
	 */
	public static void sub(Geo3DVec a, Geo3DVec b, Geo3DVec c) {
		c.x = a.x - b.x;
		c.y = a.y - b.y;
		c.z = a.z - b.z;
	}

	/**
	 * c = a - b
	 * 
	 * @param a
	 *            vector
	 * 
	 * @param b
	 *            vector
	 * @param c
	 *            result
	 * */
	public static void sub(Geo3DVec a, GeoVec2D b, Geo3DVec c) {
		c.x = a.x - b.getX();
		c.y = a.y - b.getY();
		c.z = a.z;
	}

	/**
	 * c = a - b
	 * 
	 * @param a
	 *            vector
	 * @param b
	 *            vector
	 * @param c
	 *            result
	 */
	public static void sub(GeoVec2D a, Geo3DVec b, Geo3DVec c) {
		c.x = a.getX() - b.x;
		c.y = a.getY() - b.y;
		c.z = -b.z;
	}

	/**
	 * c = a * b
	 * 
	 * @param a
	 *            factor
	 * @param b
	 *            factor
	 * @param c
	 *            product
	 */
	public static void mult(Geo3DVec a, double b, Geo3DVec c) {
		c.x = a.x * b;
		c.y = a.y * b;
		c.z = a.z * b;
	}

	/**
	 * Store inner product of two vectors in a number
	 * 
	 * @param a
	 *            1st vector
	 * @param b
	 *            2nd vector
	 * @param c
	 *            output number
	 */
	public static void inner(GeoVecInterface a, GeoVecInterface b,
			MyDouble c) {
		c.set(a.getX() * b.getX() + a.getY() * b.getY() + a.getZ() * b.getZ());
	}

	/**
	 * Multiplies two vectors as complex numbers. Returns undefined if they are
	 * not 2D (z=0)
	 * 
	 * @param a
	 *            factor
	 * @param b
	 *            factor
	 * @param c
	 *            product
	 */
	public static void complexMultiply(GeoVecInterface a,
			GeoVecInterface b, GeoVec2D c) {

		if (!DoubleUtil.isZero(a.getZ()) || !DoubleUtil.isZero(b.getZ())) {
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

	/**
	 * c = a / b
	 * 
	 * @param a
	 *            dividend
	 * @param b
	 *            divisor
	 * @param c
	 *            ratio
	 */
	public static void div(Geo3DVec a, double b, Geo3DVec c) {
		c.x = a.x / b;
		c.y = a.y / b;
		c.z = a.z / b;
	}

	@Override
	public String toString(StringTemplate tpl) {
		sbToString.setLength(0);
		sbToString.append('(');
		sbToString.append(kernel.format(x, tpl));
		sbToString.append(", ");
		sbToString.append(kernel.format(y, tpl));
		sbToString.append(')');
		return sbToString.toString();
	}

	/**
	 * interface VectorValue implementation
	 */
	@Override
	public Geo3DVec getVector() {
		return this;
	}

	@Override
	public boolean isConstant() {
		return true;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public HashSet<GeoElement> getVariables(SymbolicMode symbolicMode) {
		return null;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	@Override
	public double[] getPointAsDouble() {
		return new double[] { getX(), getY(), getZ() };
	}

	@Override
	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	@Override
	public boolean isEqual(Geo3DVecInterface vec) {
		Geo3DVec v = (Geo3DVec) vec;
		return DoubleUtil.isEqual(x, v.x) && DoubleUtil.isEqual(y, v.y)
				&& DoubleUtil.isEqual(z, v.z);
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
		GeoVecInterface v = rt.getVector();
		double xx = v.getX();
		double yy = v.getY();
		double zz = v.getZ();

		double a = MyList.getCell(list, 0, 0).evaluateDouble();
		double b = MyList.getCell(list, 1, 0).evaluateDouble();
		double c = MyList.getCell(list, 2, 0).evaluateDouble();
		double d = MyList.getCell(list, 0, 1).evaluateDouble();
		double e = MyList.getCell(list, 1, 1).evaluateDouble();
		double f = MyList.getCell(list, 2, 1).evaluateDouble();
		double g = MyList.getCell(list, 0, 2).evaluateDouble();
		double h = MyList.getCell(list, 1, 2).evaluateDouble();
		double i = MyList.getCell(list, 2, 2).evaluateDouble();

		x = a * xx + b * yy + c * zz;
		y = d * xx + e * yy + f * zz;
		z = g * xx + h * yy + i * zz;
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

		if (vector && !DoubleUtil.isZero(w)) {
			x = Double.NaN;
			y = Double.NaN;
			z = Double.NaN;
			return;
		}

		double a = MyList.getCell(list, 0, 0).evaluateDouble();
		double b = MyList.getCell(list, 1, 0).evaluateDouble();
		double c = MyList.getCell(list, 2, 0).evaluateDouble();
		double d = MyList.getCell(list, 3, 0).evaluateDouble();

		double e = MyList.getCell(list, 0, 1).evaluateDouble();
		double f = MyList.getCell(list, 1, 1).evaluateDouble();
		double g = MyList.getCell(list, 2, 1).evaluateDouble();
		double h = MyList.getCell(list, 3, 1).evaluateDouble();

		double i = MyList.getCell(list, 0, 2).evaluateDouble();
		double j = MyList.getCell(list, 1, 2).evaluateDouble();
		double k = MyList.getCell(list, 2, 2).evaluateDouble();
		double l = MyList.getCell(list, 3, 2).evaluateDouble();

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
		GeoVecInterface v = rt.getVector();
		double xx = v.getX();
		double yy = v.getY();

		double a = MyList.getCell(list, 0, 0).evaluateDouble();
		double b = MyList.getCell(list, 1, 0).evaluateDouble();
		double d = MyList.getCell(list, 0, 1).evaluateDouble();
		double e = MyList.getCell(list, 1, 1).evaluateDouble();
		double g = MyList.getCell(list, 0, 2).evaluateDouble();
		double h = MyList.getCell(list, 1, 2).evaluateDouble();

		x = a * xx + b * yy;
		y = d * xx + e * yy;
		z = g * xx + h * yy;
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

		GeoVecInterface v = rt.getVector();
		double xx = v.getX();
		double yy = v.getY();
		double zz = v.getZ();

		double a = MyList.getCell(list, 0, 0).evaluateDouble();
		double b = MyList.getCell(list, 1, 0).evaluateDouble();
		double c = MyList.getCell(list, 2, 0).evaluateDouble();
		double d = MyList.getCell(list, 0, 1).evaluateDouble();
		double e = MyList.getCell(list, 1, 1).evaluateDouble();
		double f = MyList.getCell(list, 2, 1).evaluateDouble();

		ret.setCoords(a * xx + b * yy + c * zz, d * xx + e * yy + f * zz);
	}

	@Override
	public int getToStringMode() {
		return this.mode;
	}

	@Override
	public Geo3DVec round() {
		return new Geo3DVec(kernel, Math.round(x), Math.round(y),
				Math.round(z));
	}

	@Override
	public Geo3DVec floor() {
		return new Geo3DVec(kernel, Math.floor(x), Math.floor(y),
				Math.floor(z));
	}

	@Override
	public Geo3DVec ceil() {
		return new Geo3DVec(kernel, Math.ceil(x), Math.ceil(y), Math.ceil(z));
	}

	@Override
	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	@Override
	public double arg() {
		return Math.atan2(y, x);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.VECTOR3D;
	}

	@Override
	public int getDimension() {
		return 3;
	}

	@Override
	public void mult(double d) {
		this.x *= d;
		this.y *= d;
		this.z *= d;
	}

	@Override
	public void setMode(int mode) {
		this.mode = mode;

	}

	@Override
	public ExpressionValue getUndefinedCopy(Kernel kernel1) {
		return kernel1.getManager3D().newGeo3DVec(Double.NaN, Double.NaN,
				Double.NaN);
	}

	/**
	 * @return kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

}
