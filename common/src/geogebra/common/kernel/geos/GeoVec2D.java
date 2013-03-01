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

package geogebra.common.kernel.geos;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.ListValue;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.main.App;
import geogebra.common.util.MyMath;
import geogebra.common.util.Riemann;
import geogebra.common.util.Unicode;

import java.util.HashSet;

import org.apache.commons.math.complex.Complex;

/**
 * 
 * @author Markus
 */

final public class GeoVec2D extends ValidExpression implements
		VectorValue {

	private double x = Double.NaN;
	private double y = Double.NaN;

	private int mode; // POLAR or CARTESIAN

	private Kernel kernel;

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
		return mode == Kernel.COORD_COMPLEX && x == 0 && y == 1;
	}

	public ExpressionValue deepCopy(Kernel kernel2) {
		return new GeoVec2D(this);
	}

	public void resolveVariables(boolean forEquation) {
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
	 * @return radius (polar)
	 */
	final public double getR() {
		return MyMath.length(x, y);
	}

	/**
	 * @return phase (polar)
	 */
	final public double getPhi() {
		return Math.atan2(y, x);
	}

	/**
	 * @return coordinates as array
	 */
	final public double[] getCoords() {
		double[] res = { x, y };
		return res;
	}

	/**
	 * Calculates the eucilidian length of this 2D vector. The result is
	 * sqrt(x^2 + y^2).
	 * 
	 * @return length of this vector
	 */
	final public double length() {
		return MyMath.length(x, y);
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
	 * Returns a new vector with the same direction and orientation, but length
	 * 1.
	 * 
	 * @return unit vector with same direction
	 */
	final public GeoVec2D getUnitVector() {
		double len = this.length();
		return new GeoVec2D(kernel, x / len, y / len);
	}

	/**
	 * Returns the coordinates of a vector with the same direction and
	 * orientation, but length 1.
	 * 
	 * @return normalized coords
	 */
	final public double[] getUnitCoords() {
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
	final public double inner(GeoVec2D v) {
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
	final public boolean isEqual(GeoVec2D v) {
		return Kernel.isEqual(x, v.x) && Kernel.isEqual(y, v.y);
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
	final public boolean linDep(GeoVec2D v) {
		// v = l* w <=> det(v, w) = o
		return Kernel.isZero(det(this, v));
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
	final public static double det(GeoVec2D u, GeoVec2D v) {
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
	final public void translate(GeoVec2D v) {
		x += v.x;
		y += v.y;
	}

	/**
	 * rotate this vector by angle phi
	 * 
	 * @param phi
	 *            angle
	 */
	final public void rotate(double phi) {
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
	final public void mirror(GeoPoint Q) {
		x = 2.0 * Q.getInhomX() - x;
		y = 2.0 * Q.getInhomY() - y;
	}

	/**
	 * mirror transform with angle phi [ cos(phi) sin(phi) ] [ sin(phi)
	 * -cos(phi) ]
	 * 
	 * @param phi
	 *            parameter
	 */
	final public void mirror(double phi) {
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
	final public GeoVec2D add(GeoVec2D a) {
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
	final public static void add(GeoVec2D a, GeoVec2D b, GeoVec2D c) {
		c.x = a.x + b.x;
		c.y = a.y + b.y;
		if (a.getMode() == Kernel.COORD_COMPLEX
				|| b.getMode() == Kernel.COORD_COMPLEX)
			c.setMode(Kernel.COORD_COMPLEX);
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
	 * */
	final public static void add(GeoVec2D a, NumberValue b, GeoVec2D c) {

		if (a.getMode() == Kernel.COORD_COMPLEX) {
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
	 * */
	final public static void add(GeoVec2D a, ListValue b, GeoVec2D c) {
		MyList list = b.getMyList();
		if (list.size() != 2) {
			c.x = Double.NaN;
			c.y = Double.NaN;
			return;
		}

		NumberValue enX = list.getListElement(0).evaluateNum();
		NumberValue enY = list.getListElement(1).evaluateNum();

		if (!enX.isNumberValue() || !enY.isNumberValue()) {
			c.x = Double.NaN;
			c.y = Double.NaN;
			return;
		}

		c.x = a.x + enX.getDouble();
		c.y = a.y + enY.getDouble();
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
	final public static void sub(GeoVec2D a, ListValue b, GeoVec2D c,
			boolean reverse) {

		MyList list = b.getMyList();
		if (list.size() != 2) {
			c.x = Double.NaN;
			c.y = Double.NaN;
			return;
		}

		NumberValue enX = list.getListElement(0).evaluateNum();
		NumberValue enY = list.getListElement(1).evaluateNum();

		if (reverse) {
			c.x = a.x - enX.getDouble();
			c.y = a.y - enY.getDouble();
		} else {
			c.x = enX.getDouble() - a.x;
			c.y = enY.getDouble() - a.y;
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
	 * */
	final public static void sub(NumberValue b, GeoVec2D a, GeoVec2D c) {
		if (a.getMode() == Kernel.COORD_COMPLEX) {
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
	 * */
	final public static void sub(GeoVec2D a, NumberValue b, GeoVec2D c) {
		if (a.getMode() == Kernel.COORD_COMPLEX) {
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
	final public GeoVec2D sub(GeoVec2D a) {
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
	final public static void sub(GeoVec2D a, GeoVec2D b, GeoVec2D c) {
		c.x = a.x - b.x;
		c.y = a.y - b.y;
		if (a.getMode() == Kernel.COORD_COMPLEX
				|| b.getMode() == Kernel.COORD_COMPLEX)
			c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * Multiplies this vector by b
	 * 
	 * @param b
	 *            factor
	 */
	final public void mult(double b) {
		x = b * x;
		y = b * y;
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
	final public static void mult(GeoVec2D a, double b, GeoVec2D c) {
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
	 * */
	final public static void complexDivide(GeoVec2D a, GeoVec2D b, GeoVec2D c) {

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
	 * */
	final public static void complexDivide(NumberValue a, GeoVec2D b, GeoVec2D c) {
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
	final public static void complexMultiply(GeoVec2D a, GeoVec2D b, GeoVec2D c) {
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
	final public static void complexPower(GeoVec2D a, NumberValue b, GeoVec2D c) {		
		Complex out = new Complex(a.x, a.y);
		out = out.log().multiply(b.getDouble()).exp();
		c.x = out.getReal();
		c.y = out.getImaginary();
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
	final public static void complexSqrt(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.sqrt();
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
	final public static void complexZeta(GeoVec2D a, GeoVec2D c) {
		double[] s = {a.x, a.y};
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
	final public static void complexCbrt(GeoVec2D a, GeoVec2D c) {
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
	final public static void complexConjugate(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y);
		out = out.conjugate();
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
	}

	/**
	 * c = sqrt(a) Michael Borcherds 2010-02-07
	 * 
	 * @param a
	 *            a
	 * @return argument of a
	 */
	final public static double arg(GeoVec2D a) {
		return Math.atan2(a.y, a.x);
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
	final public static void complexPower(NumberValue a, GeoVec2D b, GeoVec2D c) {
		Complex out = new Complex(a.getDouble(), 0);
		out = out.pow(new Complex(b.x, b.y));
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
	final public static void complexExp(GeoVec2D a, GeoVec2D c) {
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
	final public static void complexLog(GeoVec2D a, GeoVec2D c) {
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
	final public static double complexAbs(GeoVec2D a) {
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
	final public static void complexPower(GeoVec2D a, GeoVec2D b, GeoVec2D c) {
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
	final public static void complexMultiply(GeoVec2D a, NumberValue b,
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
	final public static void vectorProduct(GeoVec2D a, GeoVec2D b, MyDouble c) {
		c.set(a.x * b.y - a.y * b.x);
	}

	/**
	 * @param a
	 *            factor
	 * @param b
	 *            factor
	 * @param c
	 *            inner product
	 */
	final public static void inner(GeoVec2D a, GeoVec2D b, MyDouble c) {
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
	final public static void div(GeoVec2D a, double b, GeoVec2D c) {
		c.x = a.x / b;
		c.y = a.y / b;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		if (isImaginaryUnit()) {
			switch (tpl.getStringType()) {
			case MPREDUCE:
				return "i";

			default:
				// case GEOGEBRA:
				// case GEOGEBRA_XML:
				// case LATEX:
				return Unicode.IMAGINARY;
			}
		}

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
	final public GeoVec2D getVector() {
		if (this.isImaginaryUnit())
			return new GeoVec2D(this);
		return this;
	}

	final public boolean isConstant() {
		return true;
	}

	final public boolean isLeaf() {
		return true;
	}

	final public int getMode() {
		return mode;
	}

	@Override
	final public ExpressionValue evaluate(StringTemplate tpl) {
		return getVector();
	}

	final public HashSet<GeoElement> getVariables() {
		return null;
	}

	final public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(tpl);
	}

	// abstract methods of GeoElement
	/*
	 * final public GeoElement copy() { return new GeoVec2D(this); }
	 * 
	 * final public void set(GeoElement geo) { GeoVec2D v = (GeoVec2D) geo;
	 * this.x = v.x; this.y = v.y; }
	 * 
	 * final public boolean isDefined() { return true; }
	 */

	final public boolean isNumberValue() {
		return false;
	}

	final public boolean isVectorValue() {
		return true;
	}

	final public boolean isBooleanValue() {
		return false;
	}

	final public boolean isPolynomialInstance() {
		return false;
	}

	final public boolean isTextValue() {
		return false;
	}

	final public boolean isExpressionNode() {
		return false;
	}

	public boolean isListValue() {
		return false;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	/**
	 * multiplies 2D vector by a 2x2 matrix
	 * 
	 * @param list
	 *            2x2 matrix
	 */
	public void multiplyMatrix(MyList list) {
		if (list.getMatrixCols() != 2 || list.getMatrixRows() != 2)
			return;

		double a, b, c, d;

		a = (MyList.getCell(list, 0, 0).evaluateNum()).getDouble();
		b = (MyList.getCell(list, 1, 0).evaluateNum()).getDouble();
		c = (MyList.getCell(list, 0, 1).evaluateNum()).getDouble();
		d = (MyList.getCell(list, 1, 1).evaluateNum()).getDouble();

		matrixTransform(a, b, c, d);
	}

	/**
	 * Transforms the object using the matrix
	 * a00 a01
	 * a10 a11
	 * @param a a00
	 * @param b a01
	 * @param c a10
	 * @param d a11
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
		if (list.getMatrixCols() != 3 || list.getMatrixRows() != 3)
			return;

		double a, b, c, d, e, f, g, h, i, z1, xx = x, yy = y, zz = 1;

		boolean vector = false;

		if ((rt instanceof GeoPoint) || (rt instanceof GeoLine)) {
			GeoVec3D p = (GeoVec3D) rt;
			// use homogeneous coordinates if available
			xx = p.x;
			yy = p.y;
			zz = p.z;
		} else if (rt.isVectorValue()) {
			GeoVec2D v = ((VectorValue) rt).getVector();
			xx = v.x;
			yy = v.y;

			// consistent with 3D vectors
			zz = 0;
			vector = true;

		} else
			App.debug("error in GeoVec2D");

		a = (MyList.getCell(list, 0, 0).evaluateNum()).getDouble();
		b = (MyList.getCell(list, 1, 0).evaluateNum()).getDouble();
		c = (MyList.getCell(list, 2, 0).evaluateNum()).getDouble();
		d = (MyList.getCell(list, 0, 1).evaluateNum()).getDouble();
		e = (MyList.getCell(list, 1, 1).evaluateNum()).getDouble();
		f = (MyList.getCell(list, 2, 1).evaluateNum()).getDouble();
		g = (MyList.getCell(list, 0, 2).evaluateNum()).getDouble();
		h = (MyList.getCell(list, 1, 2).evaluateNum()).getDouble();
		i = (MyList.getCell(list, 2, 2).evaluateNum()).getDouble();

		x = a * xx + b * yy + c * zz;
		y = d * xx + e * yy + f * zz;
		z1 = g * xx + h * yy + i * zz;

		if (!vector) {
			x = x / z1;
			y = y / z1;
		} else {
			if (!Kernel.isZero(z1)) {
				// for a Vector, if z1!=0 then the answer can't be represented
				// by a 2D vector
				// so set undefined
				// won't happen when 3rd row of matrix is (0,0,1)
				x = Double.NaN;
				y = Double.NaN;
			}
		}

		return;
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}

	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}
	
	/**
	 * Transforms the object using the matrix
	 * a00 a01 a02
	 * a10 a11 a12
	 * a20 a21 a22
	 * @param a00 a00
	 * @param a01 a01
	 * @param a02 a02
	 * @param a10 a10
	 * @param a11 a11
	 * @param a12 a12
	 * @param a20 a20
	 * @param a21 a21
	 * @param a22 a22
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
		return;

	}

	public Kernel getKernel() {
		return kernel;
	}
	
	@Override
	public boolean hasCoords() {
		return true;
	}

}
