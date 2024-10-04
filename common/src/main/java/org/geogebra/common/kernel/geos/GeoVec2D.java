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

import java.util.Set;

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
		return new double[]{ x, y };
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
		return new double[]{ x / len, y / len };
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

	@Override
	public boolean isEqual(GeoVecInterface v) {
		return DoubleUtil.isEqual(x, v.getX()) && DoubleUtil.isEqual(y, v.getY())
				&& DoubleUtil.isZero(v.getZ());
	}

	/**
	 * Yields true if this vector and v are linear dependent This is done by
	 * calculating the determinant of this vector and v:
	 * this = const * v &lt;=&gt; det(this, v) = zero vector.
	 * 
	 * @param v
	 *            other vector
	 * @return true if this is linear dependent on v
	 */
	public boolean linDep(GeoVec2D v) {
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
	 * Add value of a
	 */
	private void add(GeoVec2D a) {
		x += a.x;
		y += a.y;
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

		double enX = list.get(0).evaluateDouble();
		double enY = list.get(1).evaluateDouble();

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

		double enX = list.get(0).evaluateDouble();
		double enY = list.get(1).evaluateDouble();

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
		c.x = a.x - b.getX();
		c.y = a.y - b.getY();
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
		GeoVec2D ret = new GeoVec2D(kernel, MyDouble.EULER_GAMMA, 0);
		ret.add(log);
		ret.add(this);
		GeoVec2D add = new GeoVec2D(kernel, x, y);
		for (int i = 2; i < MAXIT; i++) {
			add.mult(this);
			add.mult((i - 1) / (double) i / i);
			ret.add(add);
		}
		ret.setMode(Kernel.COORD_COMPLEX);
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
		fromComplex(c, out);
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
		Complex out = new Complex(a.getDouble(), 0);
		out = out.divide(new Complex(b.x, b.y));
		fromComplex(c, out);
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
		fromComplex(c, out);
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
		Complex out = new Complex(a.x, a.y).sqrt();
		fromComplex(c, out);
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
		Complex out = new Complex(a.x, a.y).sin();
		fromComplex(c, out);
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
		Complex out = new Complex(a.x, a.y).cos();
		fromComplex(c, out);
	}

	/**
	 * c = tan(a)
	 * 
	 * @param a
	 *            input
	 * @param c
	 *            output
	 */
	public static void complexTan(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y).tan();
		fromComplex(c, out);
	}

	/**
	 * c = atan(a)
	 * @param a input
	 * @param c output
	 */
	public static void complexAtan(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y).atan();
		fromComplex(c, out);
	}

	/**
	 * c = asin(a)
	 * @param a input
	 * @param c output
	 */
	public static void complexAsin(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y).asin();
		fromComplex(c, out);
	}

	/**
	 * c = acos(a)
	 * @param a input
	 * @param c output
	 */
	public static void complexAcos(GeoVec2D a, GeoVec2D c) {
		Complex out = new Complex(a.x, a.y).acos();
		fromComplex(c, out);
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
		fromComplex(c, out);
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
		fromComplex(c, out);
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
		Complex out = new Complex(a.x, a.y).tanh();
		fromComplex(c, out);
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
		fromComplex(c, out);
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
		fromComplex(c, out);
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
		fromComplex(c, out);
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
		fromComplex(c, out);
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
		fromComplex(c, out);
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
		fromComplex(c, out);
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
		fromComplex(c, out);
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
		fromComplex(c, out);
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
		fromComplex(c, out);
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
		fromComplex(c, out);
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
		fromComplex(c, out);
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
		fromComplex(c, out);
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

	private static void fromComplex(GeoVec2D c, Complex out) {
		c.x = out.getReal();
		c.y = out.getImaginary();
		c.setMode(Kernel.COORD_COMPLEX);
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
	public void getVariables(Set<GeoElement> variables, SymbolicMode symbolicMode) {
		// constant
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

		a = MyList.getCellAsDouble(list, 0, 0);
		b = MyList.getCellAsDouble(list, 1, 0);
		c = MyList.getCellAsDouble(list, 0, 1);
		d = MyList.getCellAsDouble(list, 1, 1);

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

		a = MyList.getCellAsDouble(list, 0, 0);
		b = MyList.getCellAsDouble(list, 1, 0);
		c = MyList.getCellAsDouble(list, 0, 1);
		d = MyList.getCellAsDouble(list, 1, 1);

		double x1 = a * v.getX() + b * v.getY();
		double y1 = c * v.getX() + d * v.getY();

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

		a = MyList.getCellAsDouble(list, 0, 0);
		b = MyList.getCellAsDouble(list, 1, 0);
		c = MyList.getCellAsDouble(list, 0, 1);
		d = MyList.getCellAsDouble(list, 1, 1);

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

		double x1 = a * x + b * y;
		double y1 = c * x + d * y;

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

		double a = MyList.getCellAsDouble(list, 0, 0);
		double b = MyList.getCellAsDouble(list, 1, 0);
		double c = MyList.getCellAsDouble(list, 2, 0);
		double d = MyList.getCellAsDouble(list, 0, 1);
		double e = MyList.getCellAsDouble(list, 1, 1);
		double f = MyList.getCellAsDouble(list, 2, 1);
		double g = MyList.getCellAsDouble(list, 0, 2);
		double h = MyList.getCellAsDouble(list, 1, 2);
		double i = MyList.getCellAsDouble(list, 2, 2);

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

	/**
	 * Multiply this by a factor
	 * @param r factor
	 */
	public void dilate(double r) {
		x = x * r;
		y = y * r;
	}
}
