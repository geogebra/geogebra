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

package org.geogebra.common.kernel.geos;

import java.util.HashSet;

import org.apache.commons.math3.complex.Complex;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.ValueType;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVecInterface;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.mathIT.Riemann;

import com.google.j2objc.annotations.Weak;

/**
 * 
 * @author Markus
 */

final public class GeoVec2D extends ValidExpression
		implements VectorValue, GeoVecInterface {

	private double x = Double.NaN;
	private double y = Double.NaN;
	private static final int MAXIT = 100; // Maximum number of iterations
											// allowed in Ei.

	private int mode; // POLAR or CARTESIAN

	@Weak
	private Kernel kernel;
	private StringBuilder sbToString;

	/**
	 * Creates new GeoVec2D
	 * 
	 * @param kernel
	 *            kernel
	 */
	public GeoVec2D(Kernel kernel) {
		this.kernel = kernel;
	}

	/**
	 * Creates new GeoVec2D with coordinates (x,y)
	 * 
	 * @param kernel
	 *            kernel
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 */
	public GeoVec2D(Kernel kernel, double x, double y) {
		this(kernel);
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates new GeoVec2D with coordinates (a[0],a[1])
	 * 
	 * @param kernel
	 *            kernel
	 * @param a
	 *            coordinates
	 */
	public GeoVec2D(Kernel kernel, double[] a) {
		this(kernel);
		x = a[0];
		y = a[1];
	}

	/**
	 * Copy constructor
	 * 
	 * @param v
	 *            vector to copy
	 */
	public GeoVec2D(GeoVec2D v) {
		this(v.kernel);
		x = v.x;
		y = v.y;
		mode = v.mode;
	}

	/**
	 * @return true for imaginary unit
	 */
	public boolean isImaginaryUnit() {
		return mode == Kernel.COORD_COMPLEX && MyDouble.exactEqual(x, 0)
				&& MyDouble.exactEqual(y, 1);
	}

	@Override
	public GeoVec2D deepCopy(Kernel kernel2) {
		return new GeoVec2D(this);
	}

	@Override
	public void resolveVariables(EvalInfo info) {
		// do nothing
	}

	/**
	 * Creates new GeoVec2D as vector between Points P and Q
	 * 
	 * @param kernel
	 *            kernel
	 * @param p
	 *            start point
	 * @param q
	 *            end point
	 */
	public GeoVec2D(Kernel kernel, GeoPoint p, GeoPoint q) {
		this(kernel);
		x = q.getX() - p.getX();
		y = q.getY() - p.getY();
	}

	/**
	 * @param x
	 *            new x-coord
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @param y
	 *            new y-coord
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @param x
	 *            new x-coord
	 * @param y
	 *            new y-coord
	 */
	public void setCoords(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @param a
	 *            array with coords
	 */
	public void setCoords(double[] a) {
		x = a[0];
		y = a[1];
	}

	/**
	 * Copy coords from other vector
	 * 
	 * @param v
	 *            orher vector
	 */
	public void setCoords(GeoVec2D v) {
		x = v.x;
		y = v.y;
	}

	/**
	 * @param r
	 *            radius
	 * @param phi
	 *            phase
	 */
	public void setPolarCoords(double r, double phi) {
		x = r * Math.cos(phi);
		y = r * Math.sin(phi);
	}

	/**
	 * @return x-coord
	 */
	@Override
	public double getX() {
		return x;
	}

	/**
	 * @return y-coord
	 */
	@Override
	public double getY() {
		return y;
	}

	/**
	 * @return radius (polar)
	 */
	public double getR() {
		return MyMath.length(x, y);
	}

	/**
	 * @return phase (polar)
	 */
	public double getPhi() {
		return Math.atan2(y, x);
	}

	/**
	 * @return coordinates as array
	 */
	public double[] getCoords() {
		double[] res = { x, y };
		return res;
	}

	/**
	 * Calculates the eucilidian length of this 2D vector. The result is
	 * sqrt(x^2 + y^2).
	 * 
	 * @return length of this vector
	 */
	public double length() {
		return MyMath.length(x, y);
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
	 * Returns a new vector with the same direction and orientation, but length
	 * 1.
	 * 
	 * @return unit vector with same direction
	 */
	public GeoVec2D getUnitVector() {
		double len = this.length();
		return new GeoVec2D(kernel, x / len, y / len);
	}

	/**
	 * Returns the coordinates of a vector with the same direction and
	 * orientation, but length 1.
	 * 
	 * @return normalized coords
	 */
	public double[] getUnitCoords() {
		double len = this.length();
		double[] res = { x / len, y / len };
		return res;
	}

	/**
	 * Calculates the inner product of this vector and vector v.
	 * 
	 * @param v
	 *            other vector
	 * @return this * v
	 */
	public double inner(GeoVec2D v) {
		return x * v.x + y * v.y;
	}

	/**
	 * Yields true if the coordinates of this vector are equal to those of
	 * vector v.
	 * 
	 * @param v
	 *            other vector
	 * @return true if both vectors have equal coords
	 */
	public boolean isEqual(GeoVec2D v) {
		return DoubleUtil.isEqual(x, v.x) && DoubleUtil.isEqual(y, v.y);
	}

	/**
	 * Yields true if this vector and v are linear dependent This is done by
	 * calculating the determinant of this vector an v: this = v <=> det(this,
	 * v) = nullvector.
	 * 
	 * @param v
	 *            other vector
	 * @return true if this is linear dependent on v
	 */
	public boolean linDep(GeoVec2D v) {
		// v = l* w <=> det(v, w) = o
		return DoubleUtil.isZero(det(this, v));
	}

	/**
	 * calculates the determinant of u and v. det(u,v) = u1*v2 - u2*v1
	 * 
	 * @param u
	 *            u
	 * @param v
	 *            v
	 * @return determinant of {u,v}
	 */
	public static double det(GeoVec2D u, GeoVec2D v) {
		return u.x * v.y - u.y * v.x;
		/*
		 * // symmetric operation // det(u,v) = -det(v,u) if (u.objectID <
		 * v.objectID) { return u.x * v.y - u.y * v.x; } else { return -(v.x *
		 * u.y - v.y * u.x); }
		 */
	}

	/**
	 * translate this vector by vector v
	 * 
	 * @param v
	 *            translation vector
	 */
	public void translate(GeoVec2D v) {
		x += v.x;
		y += v.y;
	}

	/**
	 * rotate this vector by angle phi
	 * 
	 * @param phi
	 *            angle
	 */
	public void rotate(double phi) {
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		double x0 = x * cos - y * sin;
		y = x * sin + y * cos;
		x = x0;
	}

	/**
	 * mirror this point at point Q
	 * 
	 * @param Q
	 *            mirror point
	 */
	public void mirror(Coords Q) {
		x = 2.0 * Q.getX() - x;
		y = 2.0 * Q.getY() - y;
	}

	/**
	 * mirror transform with angle phi [ cos(phi) sin(phi) ] [ sin(phi)
	 * -cos(phi) ]
	 * 
	 * @param phi
	 *            parameter
	 */
	public void mirror(double phi) {
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);

		double x0 = x * cos + y * sin;
		y = x * sin - y * cos;
		x = x0;
	}

	/**
	 * returns this + a
	 * 
	 * @param a
	 *            addend
	 * @return this + a
	 */
	public GeoVec2D add(GeoVec2D a) {
		GeoVec2D res = new GeoVec2D(kernel, 0, 0);
		add(this, a, res);
		return res;
	}

	/**
	 * c = a + b
	 * 
	 * @param a
	 *            addend
	 * @param b
	 *            addend
	 * @param c
	 *            result
	 */
	public static void add(GeoVec2D a, GeoVec2D b, GeoVec2D c) {
		c.x = a.x + b.x;
		c.y = a.y + b.y;
		if (a.getToStringMode() == Kernel.COORD_COMPLEX
				|| b.getToStringMode() == Kernel.COORD_COMPLEX) {
			c.setMode(Kernel.COORD_COMPLEX);
		}
	}

	/**
	 * (xc,yc) = (xa + b , yx) ie complex + real for complex nos or (xc,yc) =
	 * (xa + b , yx + b) for Points/Vectors
	 * 
	 * @param a
	 *            addend
	 * @param b
	 *            addend
	 * @param c
	 *            result
	 */
	public static void add(GeoVec2D a, NumberValue b, GeoVec2D c) {

		if (a.getToStringMode() == Kernel.COORD_COMPLEX) {
			c.x = a.x + b.getDouble();
			c.y = a.y;
			c.setMode(Kernel.COORD_COMPLEX);
		} else {
			c.x = a.x + b.getDouble();
			c.y = a.y + b.getDouble();
		}
	}

	/**
	 * vector + 2D list (to give another vector)
	 * 
	 * @param a
	 *            addend
	 * @param b
	 *            addend
	 * @param c
	 *            result
	 */
	public static void add(GeoVec2D a, ListValue b, GeoVec2D c) {
		MyList list = b.getMyList();
		if (list.size() != 2) {
			c.x = Double.NaN;
			c.y = Double.NaN;
			return;
		}

		double enX = list.getListElement(0).evaluateDouble();
		double enY = list.getListElement(1).evaluateDouble();

		if (Double.isNaN(enX) || Double.isNaN(enY)) {
			c.x = Double.NaN;
			c.y = Double.NaN;
			return;
		}

		c.x = a.x + enX;
		c.y = a.y + enY;
	}

	/*
	 * vector - 2D list (to give another vector)
	 */
	/**
	 * @param a
	 *            minuend
	 * @param b
	 *            subtrahend
	 * @param c
	 *            result
	 * @param reverse
	 *            true to compute subtrahend - minuend
	 */
	public static void sub(GeoVec2D a, ListValue b, GeoVec2D c,
			boolean reverse) {

		MyList list = b.getMyList();
		if (list.size() != 2) {
			c.x = Double.NaN;
			c.y = Double.NaN;
			return;
		}

		double enX = list.getListElement(0).evaluateDouble();
		double enY = list.getListElement(1).evaluateDouble();

		if (reverse) {
			c.x = a.x - enX;
			c.y = a.y - enY;
		} else {
			c.x = enX - a.x;
			c.y = enY - a.y;
		}
	}

	/**
	 * (xc,yc) = (b - xa, -yx) ie real - complex or (xc,yc) = (b - xa, b - yx)
	 * for Vectors/Points
	 * 
	 * @param b
	 *            minuend
	 * @param a
	 *            subtrahend
	 * @param c
	 *            result
	 */
	public static void sub(NumberValue b, GeoVec2D a, GeoVec2D c) {
		if (a.getToStringMode() == Kernel.COORD_COMPLEX) {
			c.x = b.getDouble() - a.x;
			c.y = -a.y;
			c.setMode(Kernel.COORD_COMPLEX);
		} else {
			c.x = b.getDouble() - a.x;
			c.y = b.getDouble() - a.y;
		}
	}

	/**
	 * (xc,yc) = (xa - b , yx) ie complex - real or (xc,yc) = (xa - b , yx - b)
	 * for Vectors/Points
	 * 
	 * @param a
	 *            minuend
	 * @param b
	 *            subtrahend
	 * @param c
	 *            result
	 */
	public static void sub(GeoVec2D a, NumberValue b, GeoVec2D c) {
		if (a.getToStringMode() == Kernel.COORD_COMPLEX) {
			c.x = a.x - b.getDouble();
			c.y = a.y;
			c.setMode(Kernel.COORD_COMPLEX);
		} else {
			c.x = a.x - b.getDouble();
			c.y = a.y - b.getDouble();
		}
	}

	/**
	 * returns this - a
	 * 
	 * @param a
	 *            subtrahend
	 * @return this - subtrahend
	 */
	public GeoVec2D sub(GeoVec2D a) {
		GeoVec2D res = new GeoVec2D(kernel, 0, 0);
		sub(this, a, res);
		return res;
	}

	/**
	 * c = a - b
	 * 
	 * @param a
	 *            minuend
	 * @param b
	 *            subtrahend
	 * @param c
	 *            result
	 */
	public static void sub(GeoVec2D a, GeoVec2D b, GeoVec2D c) {
		c.x = a.x - b.x;
		c.y = a.y - b.y;
		if (a.getToStringMode() == Kernel.COORD_COMPLEX
				|| b.getToStringMode() == Kernel.COORD_COMPLEX) {
			c.setMode(Kernel.COORD_COMPLEX);
		}
	}

	/**
	 * Multiplies this vector by b
	 * 
	 * @param b
	 *            factor
	 * @return reference to self
	 */
	public GeoVec2D mult(double b) {
		x = b * x;
		y = b * y;
		return this;
	}

	private GeoVec2D mult(GeoVec2D b) {
		double x0 = x;
		double y0 = y;
		x = x0 * b.x - y0 * b.y;
		y = x0 * b.y + y0 * b.x;
		return this;
	}

	/**
	 * Extend definition of Ei to complex numbers
	 * 
	 * @return exponential integral
	 */
	public GeoVec2D ei() {
		GeoVec2D log = new GeoVec2D(kernel);
		GeoVec2D.complexLog(this, log);
		GeoVec2D ret = new GeoVec2D(kernel, MyDouble.EULER_GAMMA, 0).add(log)
				.add(this);
		GeoVec2D add = new GeoVec2D(kernel, x, y);
		for (int i = 2; i < MAXIT; i++) {
			add.mult(this);
			add.mult((i - 1) / (double) i / i);
			ret = ret.add(add);

		}
		return ret;
	}

	/**
	 * c = a * b
	 * 
	 * @param a
	 *            factor
	 * @param b
	 *            factor
	 * @param c
	 *            result
	 */
	public static void mult(GeoVec2D a, double b, GeoVec2D c) {
		c.x = a.x * b;
		c.y = a.y * b;
	}

	/**
	 * c = a / b Michael Borcherds 2007-12-09
	 * 
	 * @param a
	 *            dividend
	 * @param b
	 *            divisor
	 * @param c
	 *            result
	 * 
	 */
	public static void complexDivide(GeoVec2D a, GeoVec2D b, GeoVec2D c) {

		Complex out = new Complex(a.x, a.y);
		out = out.divide(new Complex(b.x, b.y));
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);

	}

	/**
	 * c = a / b Michael Borcherds 2008-08-12
	 * 
	 * @param a
	 *            dividend
	 * @param b
	 *            divisor
	 * @param c
	 *            result
	 * 
	 */
	public static void complexDivide(NumberValue a, GeoVec2D b,
			GeoVec2D c) {
		// NB temporary variables *crucial*: a and c can be the same variable
		// double x1=a.getDouble(), x2 = b.x, y2 = b.y;
		// complex division
		// c.x = (x1 * x2 )/(x2 * x2 + y2 * b.y);
		// c.y = ( - x1 * y2)/(x2 * x2 + y2 * b.y);

		Complex out = new Complex(a.getDouble(), 0);
		out = out.divide(new Complex(b.x, b.y));
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = a * b Michael Borcherds 2007-12-09
	 * 
	 * @param a
	 *            factor
	 * @param b
	 *            factor
	 * @param c
	 *            result
	 */
	public static void complexMultiply(GeoVec2D a, GeoVec2D b,
			GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.multiply(new Complex(b.x, b.y));
		c.x = out.getReal();
		c.y = out.getImaginary();

		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = a ^ b Michael Borcherds 2009-03-10
	 * 
	 * @param a
	 *            base
	 * @param b
	 *            power
	 * @param c
	 *            result
	 */
	public static void complexPower(GeoVec2D a, NumberValue b,
			GeoVec2D c) {

		double power = b.getDouble();
		if (a.x == 0 && a.y == 0 && power > 0) {
			c.x = 0;
			c.y = 0;
		} else {
			Complex out = new Complex(a.x, a.y);
			if (power == Math.round(power) && power < 6 && power > 0) {
				for (int i = 1; i < (int) power; i++) {
					double cx = c.x * out.getReal() - c.y * out.getImaginary();
					c.y = c.y * out.getReal() + c.x * out.getImaginary();
					c.x = cx;
				}
			} else {
				out = out.pow(power);
				c.x = out.getReal();
				c.y = out.getImaginary();
			}
		}
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = sqrt(a) Michael Borcherds 2010-02-07
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexSqrt(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.sqrt();
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = sin(a)
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexSin(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.sin();
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = cos(a)
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexCos(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.cos();
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = tan(a)
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexTan(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.tan();
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = sinh(a)
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexSinh(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.sinh();
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = cosh(a)
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexCosh(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.cosh();
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = tanh(a)
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexTanh(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.tanh();
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = sec(a)
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexSec(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = Complex.ONE.divide(out.cos());

		c.x = out.getReal();
		c.y = out.getImaginary();

		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = csc(a)
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexCsc(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = Complex.ONE.divide(out.sin());

		c.x = out.getReal();
		c.y = out.getImaginary();

		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = cot(a)
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexCot(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = Complex.ONE.divide(out.tan());

		c.x = out.getReal();
		c.y = out.getImaginary();

		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = sech(a)
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexSech(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = Complex.ONE.divide(out.cosh());

		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = csc(a)
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexCsch(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = Complex.ONE.divide(out.sinh());

		c.x = out.getReal();
		c.y = out.getImaginary();

		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = cot(a)
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexCoth(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = Complex.ONE.divide(out.tanh());

		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = zeta(a) Michael Borcherds 2010-02-07
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexZeta(GeoVec2D a, GeoVec2D c) {
		double[] s = { a.x, a.y };
		s = Riemann.zeta(s);
		c.x = s[0]; // real
		c.y = s[1]; // imaginary
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = cbrt(a) Michael Borcherds 2010-02-07
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexCbrt(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.pow(new Complex(1 / 3d, 0));
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = conjugate(a) Michael Borcherds 2010-02-07
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            c
	 */
	public static void complexConjugate(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.conjugate();
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = a ^ b Michael Borcherds 2009-03-10
	 * 
	 * @param a
	 *            base
	 * @param b
	 *            exponent
	 * @param c
	 *            result
	 */
	public static void complexPower(NumberValue a, GeoVec2D b,
			GeoVec2D c) {
		Complex out;
		if (MyDouble.exactEqual(a.getDouble(), Math.E)) {
			// special case for e^(i theta)
			// (more accurate)
			out = new Complex(b.x, b.y);
			out = out.exp();

		} else {
			out = new Complex(a.getDouble(), 0);
			out = out.pow(new Complex(b.x, b.y));
		}
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = e ^ a Michael Borcherds 2009-03-10
	 * 
	 * @param a
	 *            power
	 * @param c
	 *            result
	 */
	public static void complexExp(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.exp();
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = natural log(a) Michael Borcherds 2009-03-10
	 * 
	 * @param a
	 *            a
	 * @param c
	 *            logaritmus of a
	 */
	public static void complexLog(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.log();
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = abs(a) Michael Borcherds 2009-03-10
	 * 
	 * @param a
	 *            a
	 * @return absolute value of a
	 */
	public static double complexAbs(GeoVec2D a) {
		Complex out = new Complex(a.x, a.y);

		return out.abs();
	}

	/**
	 * c = a ^ b Michael Borcherds 2009-03-14
	 * 
	 * @param a
	 *            base
	 * @param b
	 *            exponent
	 * @param c
	 *            result
	 */
	public static void complexPower(GeoVec2D a, GeoVec2D b, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.pow(new Complex(b.x, b.y));
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = a * b Michael Borcherds 2007-12-09
	 * 
	 * @param a
	 *            factor
	 * @param b
	 *            factor
	 * @param c
	 *            result
	 */
	public static void complexMultiply(GeoVec2D a, NumberValue b,
			GeoVec2D c) {
		// NB temporary variables *crucial*: a and c can be the same variable
		// double x1=a.x,y1=a.y,x2=b.getDouble();
		// do multiply
		// c.x = (x1 * x2);
		// c.y = (x2 * y1);
		Complex out = new Complex(a.x, a.y);
		out = out.multiply(new Complex(b.getDouble(), 0));
		c.x = out.getReal();
		c.y = out.getImaginary();

		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * see also GeoVec3D#vectorProduct()
	 * 
	 * @param a
	 *            factor
	 * @param b
	 *            factor
	 * @param c
	 *            vector product
	 */
	public static void vectorProduct(GeoVecInterface a, GeoVecInterface b,
			MyDouble c) {
		c.set(a.getX() * b.getY() - a.getY() * b.getX());
	}

	/**
	 * @param a
	 *            factor
	 * @param b
	 *            factor
	 * @param c
	 *            inner product
	 */
	public static void inner(GeoVec2D a, GeoVec2D b, MyDouble c) {
		c.set(a.x * b.x + a.y * b.y);
	}

	/**
	 * c = a / b
	 * 
	 * @param a
	 *            vector
	 * @param b
	 *            divisor
	 * @param c
	 *            result
	 */
	public static void div(GeoVec2D a, double b, GeoVec2D c) {
		c.x = a.x / b;
		c.y = a.y / b;
	}

	@Override
	public String toString(StringTemplate tpl) {
		if (isImaginaryUnit()) {
			return tpl.getImaginary();
		} else if (mode == Kernel.COORD_COMPLEX) {
			initStringBuilder();
			sbToString.setLength(0);
			sbToString.append(tpl.leftBracket());
			sbToString.append(kernel.format(x, tpl));
			sbToString.append(" ");
			kernel.formatSignedCoefficient(y, sbToString, tpl);
			sbToString.append(tpl.getImaginary());
			sbToString.append(tpl.rightBracket());
			return sbToString.toString();
		}
		initStringBuilder();
		sbToString.setLength(0);
		if (tpl.hasCASType()) {
			sbToString.append("point");
		}
		sbToString.append('(');
		sbToString.append(kernel.format(x, tpl));
		sbToString.append(", ");
		sbToString.append(kernel.format(y, tpl));
		sbToString.append(')');
		return sbToString.toString();
	}

	private void initStringBuilder() {
		if (sbToString == null) {
			sbToString = new StringBuilder(50);
		}
	}

	/**
	 * interface VectorValue implementation Make a copy to make sure eg
	 * imaginary(i*5*x) returns 5*x
	 */
	@Override
	public GeoVec2D getVector() {
		return new GeoVec2D(this);
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
	public int getToStringMode() {
		return mode;
	}

	@Override
	public ExpressionValue evaluate(StringTemplate tpl) {
		return getVector();
	}

	@Override
	public HashSet<GeoElement> getVariables(SymbolicMode symbolicMode) {
		return null;
	}

	@Override
	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(tpl);
	}

	// abstract methods of GeoElement

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	public ValueType getValueType() {
		return this.mode != Kernel.COORD_COMPLEX ? ValueType.NONCOMPLEX2D
				: ValueType.COMPLEX;
	}

	@Override
	public boolean evaluatesToVectorNotPoint() {
		return this.mode != Kernel.COORD_COMPLEX;
	}

	@Override
	public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	/**
	 * multiplies 2D vector by a 2x2 matrix
	 * 
	 * @param list
	 *            2x2 matrix
	 */
	public void multiplyMatrix(MyList list) {
		if (list.getMatrixCols() != 2 || list.getMatrixRows() != 2) {
			return;
		}

		double a, b, c, d;

		a = MyList.getCell(list, 0, 0).evaluateDouble();
		b = MyList.getCell(list, 1, 0).evaluateDouble();
		c = MyList.getCell(list, 0, 1).evaluateDouble();
		d = MyList.getCell(list, 1, 1).evaluateDouble();

		matrixTransform(a, b, c, d);
	}

	/**
	 * ret = list * v
	 * 
	 * @param list
	 *            matrix (assume 2x2)
	 * @param v
	 *            vector
	 * @param ret
	 *            list * v
	 */
	static public void multiplyMatrix(MyList list, GeoVecInterface v,
			GeoVec2D ret) {

		double a, b, c, d;

		a = MyList.getCell(list, 0, 0).evaluateDouble();
		b = MyList.getCell(list, 1, 0).evaluateDouble();
		c = MyList.getCell(list, 0, 1).evaluateDouble();
		d = MyList.getCell(list, 1, 1).evaluateDouble();

		Double x1 = a * v.getX() + b * v.getY();
		Double y1 = c * v.getX() + d * v.getY();

		ret.x = x1;
		ret.y = y1;

	}

	/**
	 * multiplies 2D vector by a 2x2 matrix
	 * 
	 * @param list
	 *            2x2 matrix
	 */
	public void multiplyMatrixLeft(MyList list) {
		if (list.getMatrixCols() != 2 || list.getMatrixRows() != 2) {
			return;
		}

		double a, b, c, d;

		a = MyList.getCell(list, 0, 0).evaluateDouble();
		b = MyList.getCell(list, 1, 0).evaluateDouble();
		c = MyList.getCell(list, 0, 1).evaluateDouble();
		d = MyList.getCell(list, 1, 1).evaluateDouble();

		matrixTransform(a, c, b, d);
	}

	/**
	 * (1,2)*{{2,0},{0,3}} Transforms the object using the matrix a00 a01 a10
	 * a11
	 * 
	 * @param a
	 *            a00
	 * @param b
	 *            a01
	 * @param c
	 *            a10
	 * @param d
	 *            a11
	 */
	public void matrixTransform(double a, double b, double c, double d) {

		Double x1 = a * x + b * y;
		Double y1 = c * x + d * y;

		x = x1;
		y = y1;
	}

	/**
	 * multiplies 2D vector by a 3x3 affine matrix a b c d e f g h i
	 * 
	 * @param list
	 *            3x3 matrix
	 * @param rt
	 *            GeoVec3D (as ExpressionValue) to get homogeneous coords from
	 */
	public void multiplyMatrixAffine(MyList list, ExpressionValue rt) {
		if (list.getMatrixCols() != 3 || list.getMatrixRows() != 3) {
			return;
		}

		double xx = x, yy = y, zz = 1;

		boolean vector = false;

		if ((rt instanceof GeoPoint) || (rt instanceof GeoLine)) {
			GeoVec3D p = (GeoVec3D) rt;
			// use homogeneous coordinates if available
			xx = p.x;
			yy = p.y;
			zz = p.z;

		} else if (rt instanceof VectorNDValue) {
			GeoVecInterface v = ((VectorNDValue) rt).getVector();
			xx = v.getX();
			yy = v.getY();

			// consistent with 3D vectors
			zz = 0;
			vector = true;

		} else if (rt instanceof GeoPointND) { // 3D point
			GeoPointND p = (GeoPointND) rt;
			// use inhomogeneous coordinates
			xx = p.getInhomX();
			yy = p.getInhomY();
			zz = 1;

		} else {
			Log.warn("error in GeoVec2D.multiplyMatrixAffine"
					+ (rt == null ? "null" : rt.getValueType()));
		}

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
		double z1 = g * xx + h * yy + i * zz;

		if (!vector) {
			x = x / z1;
			y = y / z1;
		} else {
			if (!DoubleUtil.isZero(z1)) {
				// for a Vector, if z1!=0 then the answer can't be represented
				// by a 2D vector
				// so set undefined
				// won't happen when 3rd row of matrix is (0,0,1)
				x = Double.NaN;
				y = Double.NaN;
			}
		}
	}

	@Override
	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	/**
	 * Transforms the object using the matrix a00 a01 a02 a10 a11 a12 a20 a21
	 * a22
	 * 
	 * @param a00
	 *            a00
	 * @param a01
	 *            a01
	 * @param a02
	 *            a02
	 * @param a10
	 *            a10
	 * @param a11
	 *            a11
	 * @param a12
	 *            a12
	 * @param a20
	 *            a20
	 * @param a21
	 *            a21
	 * @param a22
	 *            a22
	 */
	public void matrixTransform(double a00, double a01, double a02, double a10,
			double a11, double a12, double a20, double a21, double a22) {

		double xx = x;
		double yy = y;
		double zz = 1;

		double x1 = a00 * xx + a01 * yy + a02 * zz;
		double y1 = a10 * xx + a11 * yy + a12 * zz;
		double z1 = a20 * xx + a21 * yy + a22 * zz;
		x = x1 / z1;
		y = y1 / z1;
	}

	/**
	 * @return kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	@Override
	public boolean hasCoords() {
		return true;
	}

	@Override
	public double getZ() {
		return 0;
	}

	/**
	 * @return (Math.round(x), Math.round(y))
	 */
	public GeoVec2D round() {
		return new GeoVec2D(kernel, Math.round(x), Math.round(y));
	}

	/**
	 * @return (Math.floor(x), Math.floor(y))
	 */
	public GeoVec2D floor() {
		return new GeoVec2D(kernel, Math.floor(x), Math.floor(y));
	}

	/**
	 * @return (Math.ceil(x), Math.ceil(y))
	 */
	public GeoVec2D ceil() {
		return new GeoVec2D(kernel, Math.ceil(x), Math.ceil(y));
	}

	@Override
	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	@Override
	public int getDimension() {
		return 2;
	}

	@Override
	public ExpressionValue getUndefinedCopy(Kernel kernel1) {
		return new GeoVec2D(kernel1, Double.NaN, Double.NaN);
	}

	@Override
	public double[] getPointAsDouble() {
		return new double[] { x, y, 0 };
	}

	@Override
	public ExpressionValue derivative(FunctionVariable fv, Kernel kernel1) {
		// eg derivative of i
		// needed for plotting a(x, y) = abs(x + y i) in 3d
		return (new MyDouble(kernel1, 0)).wrap();
	}

}
