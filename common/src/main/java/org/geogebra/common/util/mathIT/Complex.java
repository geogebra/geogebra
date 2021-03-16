/*
 * Complex.java - Class providing static methods for complex numbers.
 *
 * Copyright (C) 2004-2012 Andreas de Vries
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301  USA
 * 
 * As a special exception, the copyright holders of this program give you permission 
 * to link this program with independent modules to produce an executable, 
 * regardless of the license terms of these independent modules, and to copy and 
 * distribute the resulting executable under terms of your choice, provided that 
 * you also meet, for each linked independent module, the terms and conditions of 
 * the license of that module. An independent module is a module which is not derived 
 * from or based on this program. If you modify this program, you may extend 
 * this exception to your version of the program, but you are not obligated to do so. 
 * If you do not wish to do so, delete this exception statement from your version.
 */

package org.geogebra.common.util.mathIT;

import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cosh;
import static java.lang.Math.log;
import static java.lang.Math.sinh;
import static java.lang.Math.tan;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.util.NumberFormatAdapter;

/**
 * This class enables the creation of objects representing complex numbers, as
 * well the implementation of mathematical functions of complex numbers by
 * static methods. A complex number <i>z</i> &#8712;
 * <span style="font-size:large;">&#8450;</span> is uniquely determined by
 * <i>z</i> = <i>x</i> + i<i>y</i>, where <i>x</i> and <i>y</i> are real numbers
 * and i = &#8730;-1. In the class <code>Complex</code>, a complex number
 * <i>z</i> is internally represented by a <code>double</code>-array of length
 * 2, where <code>z[0]</code> = <i>x</i> = Re <i>z</i>, and <code>z[1]</code> =
 * <i>y</i> = Im <i>z</i>. In the sequel this representation is called <i>array
 * representation</i> of complex numbers. It is the purpose of the static
 * methods to provide this fast representation directly without generating
 * complex number objects.
 * 
 * @author Andreas de Vries
 * @version 1.1
 */
public class Complex {
	// private static final long serialVersionUID = -1679819632;
	/**
	 * Accuracy up to which equality of double values are computed in methods of
	 * this class. Its current value is {@value}. It is used, for instance, in
	 * the methods {@link #gamma(Complex)}, {@link #lnGamma(Complex)}, or
	 * {@link #pow(Complex)}.
	 */
	private static final double ACCURACY = 1e-10;
	/** Constant 0 &#8712; <span style="font-size:large;">&#8450;</span>. */
	private static final Complex ZERO = new Complex(0., 0.);
	/** Constant 1 &#8712; <span style="font-size:large;">&#8450;</span>. */
	private static final Complex ONE = new Complex(1., 0.);
	/** Constant i &#8712; <span style="font-size:large;">&#8450;</span>. */
	// private static final Complex I = new Complex(.0, 1.);
	/**
	 * Constant 0 &#8712; <span style="font-size:large;">&#8450;</span> in the
	 * array representation.
	 */
	static final double[] ZERO_ = { 0., 0. };
	/**
	 * Constant 1 &#8712; <span style="font-size:large;">&#8450;</span> in the
	 * array representation.
	 */
	static final double[] ONE_ = { 1., 0. };
	/**
	 * Constant i &#8712; <span style="font-size:large;">&#8450;</span> in the
	 * array representation.
	 */
	static final double[] I_ = { .0, 1. };

	/** Object attribute representing a complex number. */
	private double[] z;

	/**
	 * Creates a complex number <i>z</i> = <i>x</i> + i<i>y</i> with real part
	 * <i>x</i> and imaginary part <i>y</i>.
	 * 
	 * @param x
	 *            the real part of the complex number
	 * @param y
	 *            the imaginary part of the complex number
	 */
	public Complex(double x, double y) {
		z = new double[] { x, y };
	}

	/**
	 * Creates a complex number <i>z</i> = <i>z</i>[0] + i<i>z</i>[1] from the
	 * "array representation," i.e., with real part <i>z</i>[0] and imaginary
	 * part <i>z</i>[1].
	 * 
	 * @param z
	 *            an array with z[0] representing the real part and z[1]
	 *            representing the imaginary part of the complex number
	 */
	public Complex(double[] z) {
		this.z = new double[z.length];
		System.arraycopy(z, 0, this.z, 0, z.length);
	}

	/**
	 * Returns the real part Re<i>z</i> of this complex number <i>z</i>. For
	 * <i>z</i> = <i>x</i> + i<i>y</i> it is defined as Re<i>z</i> = <i>x</i>.
	 * 
	 * @return Re<i>z</i>
	 */
	public double getRe() {
		return z[0];
	}

	/**
	 * Returns the imaginary part Im<i>z</i> of this complex number <i>z</i>.
	 * For <i>z</i> = <i>x</i> + i<i>y</i> it is defined as Im<i>z</i> =
	 * <i>y</i>.
	 * 
	 * @return Im<i>z</i>
	 */
	public double getIm() {
		return z[1];
	}

	/**
	 * Returns the absolute value, or complex modulus, |<i>z</i>| of <i>z</i>
	 * &#8712; <span style="font-size:large;">&#8450;</span>. For <i>z</i> =
	 * <i>x</i> + i<i>y</i> it is defined as |<i>z</i>| = &#8730;(<i>x</i>
	 * <sup>2</sup> + <i>y</i><sup>2</sup>).
	 * 
	 * @param z
	 *            the complex number <i>z</i> in the array representation
	 * @return |<i>z</i>|
	 */
	public static double abs(double[] z) {
		double x;
		double h;

		if (Math.abs(z[0]) == 0 && Math.abs(z[1]) == 0) {
			x = 0.0;
		} else if (Math.abs(z[0]) >= Math.abs(z[1])) {
			h = z[1] / z[0];
			x = Math.abs(z[0]) * Math.sqrt(1 + h * h);
		} else {
			h = z[0] / z[1];
			x = Math.abs(z[1]) * Math.sqrt(1 + h * h);
		}
		return x;
	}

	/**
	 * Returns the absolute value, or complex modulus, |<i>z</i>| of <i>z</i>
	 * &#8712; <span style="font-size:large;">&#8450;</span> of the complex
	 * number <i>z</i>. For <i>z</i> = <i>x</i> + i<i>y</i> it is defined as |
	 * <i>z</i>| = &#8730;(<i>x</i><sup>2</sup> + <i>y</i><sup>2</sup>).
	 * 
	 * @param z
	 *            a complex number
	 * @return |<i>z</i>|
	 */
	public static double abs(Complex z) {
		return abs(new double[] { z.z[0], z.z[1] });
	}

	/**
	 * Returns the absolute value, or complex modulus, |<i>z</i>| of <i>z</i>
	 * &#8712; <span style="font-size:large;">&#8450;</span> of this complex
	 * number <i>z</i>. For <i>z</i> = <i>x</i> + i<i>y</i> it is defined as |
	 * <i>z</i>| = &#8730;(<i>x</i><sup>2</sup> + <i>y</i><sup>2</sup>).
	 * 
	 * @return |<code>this</code>|
	 */
	public double abs() {
		return abs(new double[] { z[0], z[1] });
	}

	/**
	 * Returns the sum of two complex numbers <i>x</i> and <i>y</i>. For
	 * <i>x</i> = <i>x</i><sub>0</sub> + i<i>x</i><sub>1</sub> and <i>y</i> =
	 * <i>y</i><sub>0</sub> + i<i>y</i><sub>1</sub>, we have
	 * <p style="text-align:center;">
	 * <i>x + y</i> = <i>x</i><sub>0</sub> + <i>y</i><sub>0</sub> + i (<i>x</i>
	 * <sub>1</sub> + <i>y</i><sub>1</sub>)
	 * </p>
	 * 
	 * @param x
	 *            the first addend in the array representation
	 * @param y
	 *            the second addend in the array representation
	 * @return the sum <i>x</i> + <i>y</i>
	 * @see #add(Complex)
	 */
	public static double[] add(double[] x, double[] y) {
		return new double[] { x[0] + y[0], x[1] + y[1] };
	}

	/**
	 * Returns the sum of this number and the complex number <i>z</i>. For
	 * <i>x</i> = <i>x</i><sub>0</sub> + i<i>x</i><sub>1</sub> and <i>y</i> =
	 * <i>y</i><sub>0</sub> + i<i>y</i><sub>1</sub>, we have
	 * <p style="text-align:center;">
	 * <i>x + y</i> = <i>x</i><sub>0</sub> + <i>y</i><sub>0</sub> + i (<i>x</i>
	 * <sub>1</sub> + <i>y</i><sub>1</sub>)
	 * </p>
	 * 
	 * @param other
	 *            the addend
	 * @return the sum <code>this</code> + <i>z</i>
	 * @see #plus(Complex)
	 * @see #add(double[],double[])
	 */
	public Complex add(Complex other) {
		return new Complex(this.z[0] + other.z[0], this.z[1] + other.z[1]);
	}

	/**
	 * Returns the argument of the complex number <i>z</i>. If <i>z</i> =
	 * <i>x</i> + i<i>y</i>, we have arg(<i>z</i>) = arctan(<i>y</i>/<i>x</i>).
	 * 
	 * @param z
	 *            a complex number
	 * @return the argument of <i>z</i>
	 * @see #arg(Complex)
	 */
	public static double arg(double[] z) {
		return atan2(z[1], z[0]);
	}

	/**
	 * Returns the argument of the complex number <i>z</i>. If <i>z</i> =
	 * <i>x</i> + i<i>y</i>, we have arg(<i>z</i>) = arctan(<i>y</i>/<i>x</i>).
	 * 
	 * @param z
	 *            a complex number
	 * @return the argument of <i>z</i>
	 * @see #arg()
	 * @see #arg(double[])
	 */
	public static double arg(Complex z) {
		return atan2(z.z[1], z.z[0]);
	}

	/**
	 * Returns the argument of this complex number <i>z</i>. If <i>z</i> =
	 * <i>x</i> + i<i>y</i>, we have arg(<i>z</i>) = arctan(<i>y</i>/<i>x</i>).
	 * 
	 * @return the argument of <code>this</code>
	 * @see #arg(Complex)
	 * @see #arg(double[])
	 */
	public double arg() {
		return arg(this);
	}

	/**
	 * Returns the cosine of a complex number <i>z</i>.
	 * 
	 * @param z
	 *            the argument
	 * @return the cosine
	 */
	public static double[] cos(double[] z) {
		double[] result = new double[2];

		result[0] = Math.cos(z[0]) * cosh(z[1]);
		result[1] = Math.sin(z[0]) * sinh(z[1]);
		return result;
	}

	/**
	 * Returns the cosine of this complex number.
	 * 
	 * @param z
	 *            the argument
	 * @return the cosine
	 */
	public static Complex cos(Complex z) {
		return new Complex(cos(new double[] { z.z[0], z.z[1] }));
	}

	/**
	 * divides a real number <i>x</i> by a complex number <i>y</i>. This method
	 * is implemented avoiding overflows.
	 * 
	 * @param x
	 *            the dividend
	 * @param y
	 *            the divisor
	 * @return the complex number <i>x/y</i>
	 * @see #divide(double, Complex)
	 * @see #divide(double[], double[])
	 */
	public static double[] divide(double x, double[] y) {
		double[] w = new double[2];
		double h;

		if (Math.abs(y[0]) <= ACCURACY && Math.abs(y[1]) <= ACCURACY) {
			if (x > 0) {
				w[0] = Double.POSITIVE_INFINITY;
			} else if (x < 0) {
				w[0] = Double.NEGATIVE_INFINITY;
			} else {
				w[0] = 1;
			}
			return w;
		}

		if (Math.abs(y[0]) >= Math.abs(y[1])) {
			h = y[1] / y[0];
			w[0] = x / (y[0] + y[1] * h);
			w[1] = -x * h / (y[0] + y[1] * h);
		} else {
			h = y[0] / y[1];
			w[0] = x * h / (y[0] * h + y[1]);
			w[1] = -x / (y[0] * h + y[1]);
		}

		return w;
	}

	/**
	 * divides a real number <i>x</i> by a complex number <i>y</i>. This method
	 * is implemented avoiding overflows.
	 * 
	 * @param x
	 *            the dividend
	 * @param y
	 *            the divisor
	 * @return <i>x/y</i>
	 * @see #divide(double, double[])
	 */
	public static Complex divide(double x, Complex y) {
		return new Complex(divide(x, new double[] { y.z[0], y.z[1] }));
	}

	/**
	 * divides two complex numbers <i>x</i> and <i>y</i>. This method is
	 * implemented avoiding overflows.
	 * 
	 * @param x
	 *            dividend
	 * @param y
	 *            divisor
	 * @return <i>x/y</i>
	 * @see #divide(double, double[])
	 */
	public static double[] divide(double[] x, double[] y) {
		double[] w = new double[2];
		double h;

		if (Math.abs(y[0]) <= ACCURACY && Math.abs(y[1]) <= ACCURACY) {
			if (x[0] > 0) {
				w[0] = Double.POSITIVE_INFINITY;
			} else if (x[0] < 0) {
				w[0] = Double.NEGATIVE_INFINITY;
			} else {
				w[0] = 1;
			}
			if (x[1] > 0) {
				w[1] = Double.POSITIVE_INFINITY;
			} else if (x[1] < 0) {
				w[1] = Double.NEGATIVE_INFINITY;
			} else {
				w[1] = 0;
			} // if Im x == 0, x/0 is real!
			return w;
		}

		if (Math.abs(y[0]) >= Math.abs(y[1])) {
			h = y[1] / y[0];
			w[0] = (x[0] + x[1] * h) / (y[0] + y[1] * h);
			w[1] = (x[1] - x[0] * h) / (y[0] + y[1] * h);
		} else {
			h = y[0] / y[1];
			w[0] = (x[0] * h + x[1]) / (y[0] * h + y[1]);
			w[1] = (x[1] * h - x[0]) / (y[0] * h + y[1]);
		}

		return w;
	}

	/**
	 * divides this complex numbers by <i>z</i>. This method is implemented
	 * avoiding overflows.
	 * 
	 * @param other
	 *            divisor
	 * @return <code>this</code>/<i>z</i>
	 * @see #divide(double[], double[])
	 */
	public Complex divide(Complex other) {
		return new Complex(divide(new double[] { this.z[0], this.z[1] },
				new double[] { other.z[0], other.z[1] }));
	}

	/**
	 * The exponential function of a complex number <i>z</i>. The following
	 * formula holds for <i>z</i> = <i>x</i> + i<i>y</i>:
	 * <p style="text-align:center">
	 * exp(<i>z</i>) = e<sup><i>x</i></sup> (cos<i>y</i> + i sin <i>y</i>).
	 * </p>
	 * 
	 * @param z
	 *            a complex number
	 * @return exp(<i>z</i>)
	 * @see #ln(double[])
	 */
	public static double[] exp(double[] z) {
		if (z[0] > 709) {
			return multiply(Double.POSITIVE_INFINITY, ONE_);
		}

		double[] w = { Math.exp(z[0]) * Math.cos(z[1]),
				Math.exp(z[0]) * Math.sin(z[1]) };
		return w;
	}

	/**
	 * The exponential function of the complex number <i>z</i>. The following
	 * formula holds for <i>z</i> = <i>x</i> + i<i>y</i>:
	 * <p style="text-align:center">
	 * exp(<i>z</i>) = e<sup><i>x</i></sup> (cos<i>y</i> + i sin <i>y</i>),
	 * </p>
	 * where <i>z</i> is this complex number.
	 * 
	 * @param z
	 *            the argument
	 * @return exp(<i>z</i>) where <i>z</i> is this complex number
	 * @see #ln(Complex)
	 */
	public static Complex exp(Complex z) {
		return new Complex(exp(new double[] { z.z[0], z.z[1] }));
	}

	/**
	 * The Euler gamma function &#915;(<i>z</i>) of a complex number <i>z</i>.
	 * For Re <i>z</i> &gt; 0, it is computed according to the method of
	 * Lanczos. Otherwise, the following formula of Weierstrass is applied,
	 * which holds for any <i>z</i> but converges more slowly.
	 * <table style="margin:auto;" summary="">
	 * <tr>
	 * <td>&#915;(<i>z</i>)</td>
	 * <td align="center">&nbsp; = &nbsp;</td>
	 * <td>
	 * <table summary="" border="0">
	 * <tr>
	 * <td align="center">1</td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td align="center"><i>z</i> e<sup><i>&#947;z</i></sup></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>
	 * <table summary="" border="0">
	 * <tr>
	 * <td align="center" class="small">&#8734;</td>
	 * </tr>
	 * <tr>
	 * <td align="center" style="font-size:xx-large;">&#928;</td>
	 * </tr>
	 * <tr>
	 * <td align="center" class="small"><i>n</i>=1</td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td align="center" style="font-size:xx-large;">[</td>
	 * <td align="center" style="font-size:xx-large;">(</td>
	 * <td>1 +</td>
	 * <td>
	 * <table summary="" border="0">
	 * <tr>
	 * <td align="center"><i>z</i></td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td align="center"><i>n</i></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td align="center" style="font-size:xx-large;">)
	 * <sup><span style="font-size:small;">-1</span></sup></td>
	 * <td>e<sup><i>z/n</i></sup></td>
	 * <td align="center" style="font-size:xx-large;">]</td>
	 * </tr>
	 * </table>
	 * It is approximated up to a relative error of 10<sup>6</sup> times the
	 * given {@link #ACCURACY accuracy}. Here &#947; denotes the
	 * {@link MyDouble#EULER_GAMMA Euler-Mascheroni constant}.
	 * 
	 * @param z
	 *            a complex number
	 * @return &#915;(<i>z</i>)
	 * @see #gamma(Complex)
	 * @see #lnGamma(double[])
	 * @see MyDouble#EULER_GAMMA
	 */
	public static double[] gamma(double[] z) {
		if (z[0] < 0) { // Weierstrass form:
			double[] w = divide(ONE_,
					multiply(z,
							power(Math.E, multiply(MyDouble.EULER_GAMMA, z))));
			int nMax = (int) (1e-6 / ACCURACY);
			double[] z_n;
			for (int n = 1; n <= nMax; n++) {
				z_n = multiply(1.0 / n, z);
				w = multiply(w, add(ONE_, z_n));
				w = divide(w, power(Math.E, z_n));
			}
			w = divide(1.0, w);
			return w;
		}

		double[] x = { z[0], z[1] };
		double[] c = { 76.18009173, -86.50532033, 24.01409822, -1.231739516,
				.00120858003, -5.36382e-6 };

		boolean reflec;

		if (x[0] >= 1.) {
			reflec = false;
			x[0] = x[0] - 1.;
		} else {
			reflec = true;
			x[0] = 1. - x[0];
		}

		double[] xh = { x[0] + .5, x[1] };
		double[] xgh = { x[0] + 5.5, x[1] };
		double[] s = ONE_;
		double[] anum = { x[0], x[1] };
		for (int i = 0; i < c.length; ++i) {
			anum = add(anum, ONE_);
			s = add(s, divide(c[i], anum));
		}
		s = multiply(2.506628275, s);
		// g = pow(xgh, xh) * s / exp(xgh);
		double[] g = multiply(power(xgh, xh), s);
		g = divide(g, power(Math.E, xgh));
		if (reflec) {
			// result = PI x / (g * sin(PI x));
			if (Math.abs(x[1]) > 709) { // sin( 710 i ) = Infinity !!
				return ZERO_;
			}
			double[] result = multiply(PI, x);
			result = divide(result, multiply(g, sin(multiply(PI, x))));
			return result;
		}
		return g;
	}

	/**
	 * The Euler gamma function &#915;(<i>z</i>) of a complex number <i>z</i>.
	 * For Re <i>z</i> &gt; 0, it is computed according to the method of
	 * Lanczos. Otherwise, the following formula of Weierstrass is applied,
	 * which holds for any <i>z</i> but converges more slowly.
	 * <table style="margin:auto;" summary="">
	 * <tr>
	 * <td>&#915;(<i>z</i>)</td>
	 * <td align="center">&nbsp; = &nbsp;</td>
	 * <td>
	 * <table summary="" border="0">
	 * <tr>
	 * <td align="center">1</td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td align="center"><i>z</i> e<sup><i>&#947;z</i></sup></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>
	 * <table summary="" border="0">
	 * <tr>
	 * <td align="center" class="small">&#8734;</td>
	 * </tr>
	 * <tr>
	 * <td align="center" style="font-size:xx-large;">&#928;</td>
	 * </tr>
	 * <tr>
	 * <td align="center" class="small"><i>n</i>=1</td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td align="center" style="font-size:xx-large;">[</td>
	 * <td align="center" style="font-size:xx-large;">(</td>
	 * <td>1 +</td>
	 * <td>
	 * <table summary="" border="0">
	 * <tr>
	 * <td align="center"><i>z</i></td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td align="center"><i>n</i></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td align="center" style="font-size:xx-large;">)
	 * <sup><span style="font-size:small;">-1</span></sup></td>
	 * <td>e<sup><i>z/n</i></sup></td>
	 * <td align="center" style="font-size:xx-large;">]</td>
	 * </tr>
	 * </table>
	 * It is approximated up to a relative error of 10<sup>6</sup> times the
	 * given {@link #ACCURACY accuracy}. Here &#947; denotes the
	 * {@link MyDouble#EULER_GAMMA Euler-Mascheroni constant}.
	 * 
	 * @param z
	 *            a complex number
	 * @return &#915;(<i>z</i>)
	 * @see #exp(double[])
	 * @see MyDouble#EULER_GAMMA
	 */
	public static Complex gamma(Complex z) {
		return new Complex(gamma(new double[] { z.z[0], z.z[1] }));
	}

	/**
	 * Returns the natural logarithm of the cosine of a complex number <i>z</i>.
	 *
	 * @param z
	 *            the argument
	 * @return the natural logarithm of the cosine of <i>z</i>
	 */
	public static double[] lnCos(double[] z) {
		double[] result = new double[2];

		if (Math.abs(z[1]) <= 709) {
			result[0] = Math.cos(z[0]) * cosh(z[1]); // since JDK 1.5:
			// result[0] = cos(z[0]) * ( exp(z[1]) + exp(-z[1]) ) / 2;
			result[1] = Math.sin(z[0]) * sinh(z[1]); // since JDK 1.5
			// result[1] = sin(z[0]) * ( exp(z[1]) - exp(-z[1]) ) / 2;
			result = ln(result);
		} else { // approximately cosh y = sinh y = e^|y| / 2:
			// ln |cos z| = ln |y| - ln 2 for z = x + iy
			result[0] = Math.abs(z[1]) - log(2);
			// arg |sin z| = arctan( sgn y cot x ) for z = x + iy:
			if (z[1] < 0) {
				result[1] = atan(-1 / tan(z[0]));
			} else {
				result[1] = atan(1 / tan(z[0]));
			}
		}
		return result;
	}

	/**
	 * Logarithm of this complex number <i>z</i>. It is defined by ln <i>z</i> =
	 * ln |<i>z</i>| + i arg(<i>z</i>).
	 * 
	 * @param z
	 *            the argument
	 * @return ln <i>z</i> where <i>z</i> is this complex number
	 * @see #abs(Complex)
	 * @see #arg(Complex)
	 * @see #exp(Complex)
	 * @see #ln(double[])
	 */
	public static Complex ln(Complex z) {
		return new Complex(ln(new double[] { z.z[0], z.z[1] }));
	}

	/**
	 * Logarithm of a complex number <i>z</i>. It is defined by ln <i>z</i> = ln
	 * |<i>z</i>| + i arg(<i>z</i>).
	 * 
	 * @param z
	 *            complex number
	 * @return ln <i>z</i>
	 * @see #abs(double[])
	 * @see #arg(double[])
	 * @see #exp(double[])
	 * @see #ln(Complex)
	 */
	public static double[] ln(double[] z) {
		double[] result = { log(abs(z)), arg(z) };
		return result;
	}

	/**
	 * Logarithm of the Euler gamma function of a complex number <i>z</i>. For
	 * Re <i>z</i> &gt; 0, it is computed according to the method of Lanczos.
	 * Otherwise, the following formula is applied, which holds for any <i>z</i>
	 * but converges more slowly.
	 * <table style="margin:auto;" summary="">
	 * <tr>
	 * <td>ln &#915;(<i>z</i>)</td>
	 * <td align="center">&nbsp; = &nbsp;</td>
	 * <td>- ln <i>z</i> - <i>&#947;z</i></td>
	 * <td>+</td>
	 * <td>
	 * <table summary="" border="0">
	 * <tr>
	 * <td align="center" class="small">&#8734;</td>
	 * </tr>
	 * <tr>
	 * <td align="center" style="font-size:xx-large;">&#931;</td>
	 * </tr>
	 * <tr>
	 * <td align="center" class="small"><i>n</i>=1</td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td align="center" style="font-size:xx-large;">[</td>
	 * <td>
	 * <table summary="" border="0">
	 * <tr>
	 * <td align="center"><i>z</i></td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td align="center"><i>n</i></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>- ln</td>
	 * <td align="center" style="font-size:xx-large;">(</td>
	 * <td>1 +</td>
	 * <td>
	 * <table summary="" border="0">
	 * <tr>
	 * <td align="center"><i>z</i></td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td align="center"><i>n</i></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td align="center" style="font-size:xx-large;">)</td>
	 * <td align="center" style="font-size:xx-large;">]</td>
	 * </tr>
	 * </table>
	 * Here &#947; denotes the {@link MyDouble#EULER_GAMMA Euler-Mascheroni
	 * constant}.
	 * 
	 * @param z
	 *            a complex number
	 * @return ln &#915;(<i>z</i>)
	 * @see #lnGamma(Complex)
	 * @see #gamma(double[])
	 */
	public static double[] lnGamma(double[] z) {
		if (z[0] < 0) {
			double[] w = add(ln(z), multiply(MyDouble.EULER_GAMMA, z));
			w = multiply(-1.0, w);
			int nMax = (int) (Math.abs(z[0]) / ACCURACY);
			if (nMax > 10000) {
				nMax = 10000;
			}
			double[] z_n;
			for (int n = 1; n <= nMax; n++) {
				z_n = multiply(1.0 / n, z);
				w = add(w, z_n);
				w = subtract(w, ln(add(ONE_, z_n)));
			}
			return w;
		}

		double[] x = { z[0], z[1] };
		double[] c = { 76.18009173, -86.50532033, 24.01409822, -1.231739516,
				.00120858003, -5.36382e-6 };

		boolean reflec;

		if (x[0] >= 1.) {
			reflec = false;
			x[0] = x[0] - 1.;
		} else {
			reflec = true;
			x[0] = 1. - x[0];
		}

		double[] xh = { x[0] + .5, x[1] };
		double[] xgh = { x[0] + 5.5, x[1] };
		double[] s = ONE_;
		double[] anum = { x[0], x[1] };
		for (int i = 0; i < c.length; ++i) {
			anum = add(anum, ONE_);
			s = add(s, divide(c[i], anum));
		}
		s = multiply(2.506628275, s);
		// g = xh * log(xgh) + log(s) - xgh;
		double[] g = multiply(xh, ln(xgh));
		g = add(g, ln(s));
		g = subtract(g, xgh);
		if (reflec) {
			// result = log(xx * PI) - g - log( sin(xx * PI) );
			double[] result = ln(multiply(PI, x));
			result = subtract(result, g);
			result = subtract(result, lnSin(multiply(PI, x)));
			return result;
		}
		return g;
	}

	/**
	 * Logarithm of the Euler gamma function of a complex number <i>z</i>. For
	 * Re <i>z</i> &gt; 0, it is computed according to the method of Lanczos.
	 * Otherwise, the following formula is applied, which holds for any <i>z</i>
	 * but converges more slowly.
	 * <table style="margin:auto;" summary="">
	 * <tr>
	 * <td>ln &#915;(<i>z</i>)</td>
	 * <td align="center">&nbsp; = &nbsp;</td>
	 * <td>- ln <i>z</i> - <i>&#947;z</i></td>
	 * <td>+</td>
	 * <td>
	 * <table summary="" border="0">
	 * <tr>
	 * <td align="center" class="small">&#8734;</td>
	 * </tr>
	 * <tr>
	 * <td align="center" style="font-size:xx-large;">&#931;</td>
	 * </tr>
	 * <tr>
	 * <td align="center" class="small"><i>n</i>=1</td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td align="center" style="font-size:xx-large;">[</td>
	 * <td>
	 * <table summary="" border="0">
	 * <tr>
	 * <td align="center"><i>z</i></td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td align="center"><i>n</i></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td>- ln</td>
	 * <td align="center" style="font-size:xx-large;">(</td>
	 * <td>1 +</td>
	 * <td>
	 * <table summary="" border="0">
	 * <tr>
	 * <td align="center"><i>z</i></td>
	 * </tr>
	 * <tr>
	 * <td style="height:1px;">
	 * <hr>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td align="center"><i>n</i></td>
	 * </tr>
	 * </table>
	 * </td>
	 * <td align="center" style="font-size:xx-large;">)</td>
	 * <td align="center" style="font-size:xx-large;">]</td>
	 * </tr>
	 * </table>
	 * Here &#947; denotes the {@link MyDouble#EULER_GAMMA Euler-Mascheroni
	 * constant}.
	 * 
	 * @param z
	 *            a complex number
	 * @return ln &#915;(<i>z</i>)
	 * @see #gamma(Complex)
	 * @see #lnGamma(double[])
	 */
	public static Complex lnGamma(Complex z) {
		return new Complex(lnGamma(new double[] { z.z[0], z.z[1] }));
	}

	/**
	 * Returns the natural logarithm of the sine of a complex number <i>z</i>.
	 * 
	 * @param z
	 *            a complex number in the array representation
	 * @return lnSin <i>z</i>
	 */
	public static Complex lnSin(Complex z) {
		return new Complex(lnSin(new double[] { z.z[0], z.z[1] }));
	}

	/**
	 * Returns the natural logarithm of the sine of a complex number <i>z</i>.
	 * 
	 * @param z
	 *            a complex number in the array representation
	 * @return lnSin <i>z</i>
	 */
	public static double[] lnSin(double[] z) {
		double[] result = new double[2];

		if (Math.abs(z[1]) <= 709) {
			result[0] = Math.sin(z[0]) * cosh(z[1]); // since JDK 1.5:
			// result[0] = sin(z[0]) * ( exp(z[1]) + exp(-z[1]) ) / 2;
			result[1] = Math.cos(z[0]) * sinh(z[1]); // since JDK 1.5
			// result[1] = cos(z[0]) * ( exp(z[1]) - exp(-z[1]) ) / 2;
			result = ln(result);
		} else { // approximately cosh y = sinh y = e^|y| / 2:
			// ln |sin z| = ln |y| - ln 2 for z = x + iy
			result[0] = Math.abs(z[1]) - log(2);
			// arg |sin z| = arctan( sgn y cot x ) for z = x + iy:
			if (z[1] < 0) {
				result[1] = atan(-1 / tan(z[0]));
			} else {
				result[1] = atan(1 / tan(z[0]));
			}
		}
		return result;
	}

	/**
	 * subtracts <i>z</i> from this complex number. We have <code>this</code>
	 * .subtract(<i>z</i>) = <code>this</code> - <i>z</i> = Re (
	 * <code>this</code> - <i>y</i>) + Im (<code>this</code> - <i>z</i>).
	 * 
	 * @param other
	 *            a complex number
	 * @return the difference <code>this</code> - <i>z</i>
	 * @see #subtract(Complex)
	 */
	public Complex minus(Complex other) {
		return new Complex(this.z[0] - other.z[0], this.z[1] - other.z[1]);
	}

	/**
	 * The product of a real number <i>x</i> and a complex number <i>z</i>.
	 * 
	 * @param x
	 *            a real number
	 * @param z
	 *            a complex number in the array representation
	 * @return the product <i>xz</i>
	 */
	public static double[] multiply(double x, double[] z) {
		return new double[] { x * z[0], x * z[1] };
	}

	/**
	 * The product of a real number <i>x</i> with this complex number.
	 * 
	 * @param x
	 *            a real number
	 * @return the product <i>xz</i> where <i>z</i> is this complex number
	 */
	public Complex multiply(double x) {
		return new Complex(x * z[0], x * z[1]);
	}

	/**
	 * The product of two complex numbers. For <i>x</i> = <i>x</i><sub>0</sub> +
	 * i<i>x</i><sub>1</sub> and <i>y</i> = <i>y</i><sub>0</sub> + i<i>y</i>
	 * <sub>1</sub>, we have
	 * <p style="text-align:center">
	 * <i>xy</i> = <i>x</i><sub>0</sub><i>y</i><sub>0</sub> - <i>x</i>
	 * <sub>1</sub><i>y</i><sub>1</sub> + i (<i>x</i><sub>1</sub><i>y</i>
	 * <sub>0</sub> + <i>x</i><sub>0</sub><i>y</i><sub>1</sub>)
	 * </p>
	 * 
	 * @param x
	 *            the first factor in the array representation
	 * @param y
	 *            the second factor in the array representation
	 * @return the product <i>xy</i>
	 */
	public static Complex multiply(Complex x, Complex y) {
		return new Complex(x.z[0] * y.z[0] - x.z[1] * y.z[1],
				x.z[1] * y.z[0] + x.z[0] * y.z[1]);
	}

	/**
	 * The product of two complex numbers. For <i>x</i> = <i>x</i><sub>0</sub> +
	 * i<i>x</i><sub>1</sub> and <i>y</i> = <i>y</i><sub>0</sub> + i<i>y</i>
	 * <sub>1</sub>, we have
	 * <p style="text-align:center">
	 * <i>xy</i> = <i>x</i><sub>0</sub><i>y</i><sub>0</sub> - <i>x</i>
	 * <sub>1</sub><i>y</i><sub>1</sub> + i (<i>x</i><sub>1</sub><i>y</i>
	 * <sub>0</sub> + <i>x</i><sub>0</sub><i>y</i><sub>1</sub>)
	 * </p>
	 * 
	 * @param x
	 *            the first factor in the array representation
	 * @param y
	 *            the second factor in the array representation
	 * @return the product <i>xy</i>
	 */
	public static double[] multiply(double[] x, double[] y) {
		return new double[] { x[0] * y[0] - x[1] * y[1],
				x[1] * y[0] + x[0] * y[1] };
	}

	/**
	 * Returns the product of this complex number and the complex number
	 * <i>z</i>. For <i>x</i> = <i>x</i><sub>0</sub> + i<i>x</i><sub>1</sub> and
	 * <i>y</i> = <i>y</i><sub>0</sub> + i<i>y</i><sub>1</sub>, we have
	 * <p style="text-align:center">
	 * <i>xy</i> = <i>x</i><sub>0</sub><i>y</i><sub>0</sub> - <i>x</i>
	 * <sub>1</sub><i>y</i><sub>1</sub> + i (<i>x</i><sub>1</sub><i>y</i>
	 * <sub>0</sub> + <i>x</i><sub>0</sub><i>y</i><sub>1</sub>)
	 * </p>
	 * 
	 * @param other
	 *            a complex number
	 * @return the product <code>this</code>&#x2219;<i>z</i>
	 */
	public Complex multiply(Complex other) {
		return new Complex(multiply(new double[] { this.z[0], this.z[1] },
				new double[] { other.z[0], other.z[1] }));
	}

	/**
	 * Returns the sum of this number and the complex number <i>z</i>. For
	 * <i>x</i> = <i>x</i><sub>0</sub> + i<i>x</i><sub>1</sub> and <i>y</i> =
	 * <i>y</i><sub>0</sub> + i<i>y</i><sub>1</sub>, we have
	 * <p style="text-align:center">
	 * <i>x + y</i> = <i>x</i><sub>0</sub> + <i>y</i><sub>0</sub> + i (<i>x</i>
	 * <sub>1</sub> + <i>y</i><sub>1</sub>)
	 * </p>
	 * 
	 * @param other
	 *            the addend
	 * @return the sum <code>this</code> + <i>z</i>
	 * @see #add(Complex)
	 */
	public Complex plus(Complex other) {
		return new Complex(this.z[0] + other.z[0], this.z[1] + other.z[1]);
	}

	/**
	 * Returns <i>x<sup>s</sup></i> for a real number <i>x</i> and a complex
	 * number <i>s</i>. For <i>s</i> = <i>s</i><sub>0</sub> + i<i>s</i>
	 * <sub>1</sub>, we have
	 * <p style="text-align:center">
	 * <i>x<sup>s</sup></i> = <i>x</i><sup><i>s</i><sub>0</sub></sup> [ cos(
	 * <i>s</i><sub>1</sub> ln <i>x</i> ) + i sin( <i>s</i><sub>1</sub> ln
	 * <i>x</i> ) ].
	 * </p>
	 * <p>
	 * if <i>x</i> &gt; 0, and
	 * </p>
	 * <p style="text-align:center">
	 * <i>x<sup>s</sup></i> = |<i>x</i>|<sup><i>s</i><sub>0</sub></sup> [ cos(
	 * <i>s</i><sub>1</sub> ln |<i>x</i>| + <i>s</i><sub>0</sub>&#960;) + i sin(
	 * <i>s</i><sub>1</sub> ln |<i>x</i>| + <i>s</i><sub>0</sub>&#960;) ].
	 * </p>
	 * 
	 * @param x
	 *            a real number as the base
	 * @param s
	 *            a complex number as the exponent
	 * @return <i>z<sup>s</sup></i>
	 * @see #pow(Complex)
	 * @see #power(double,double[])
	 */
	public static Complex power(double x, Complex s) {
		return new Complex(power(x, new double[] { s.z[0], s.z[1] }));
	}

	/**
	 * Returns <i>x<sup>s</sup></i> for a real number <i>x</i> and a complex
	 * number <i>s</i>. For <i>s</i> = <i>s</i><sub>0</sub> + i<i>s</i>
	 * <sub>1</sub>, we have
	 * <p style="text-align:center">
	 * <i>x<sup>s</sup></i> = <i>x</i><sup><i>s</i><sub>0</sub></sup> [ cos(
	 * <i>s</i><sub>1</sub> ln <i>x</i> ) + i sin( <i>s</i><sub>1</sub> ln
	 * <i>x</i> ) ].
	 * </p>
	 * <p>
	 * if <i>x</i> &gt; 0, and
	 * </p>
	 * <p style="text-align:center">
	 * <i>x<sup>s</sup></i> = |<i>x</i>|<sup><i>s</i><sub>0</sub></sup> [ cos(
	 * <i>s</i><sub>1</sub> ln |<i>x</i>| + <i>s</i><sub>0</sub>&#960;) + i sin(
	 * <i>s</i><sub>1</sub> ln |<i>x</i>| + <i>s</i><sub>0</sub>&#960;) ].
	 * </p>
	 * 
	 * @param x
	 *            a real number as the base
	 * @param s
	 *            a complex number in the array representation as the exponent
	 * @return <i>z<sup>s</sup></i>
	 * @see #power(double,double[])
	 * @see #pow(Complex)
	 */
	public static double[] power(double x, double[] s) {
		double absX = Math.abs(x);
		double[] w = new double[2];

		if (abs(s) < ACCURACY) { // s=0?
			w = ONE_;
			return w;
		} else if (absX < ACCURACY) { // x=0?
			return w; // w=0
		}

		w[0] = Math.pow(x, s[0]);
		w[1] = w[0];
		if (x > 0) {
			w[0] *= Math.cos(s[1] * log(absX));
			w[1] *= Math.sin(s[1] * log(absX));
		} else {
			w[0] *= Math.cos(s[1] * log(absX) + s[0] * PI);
			w[1] *= Math.sin(s[1] * log(absX) + s[0] * PI);
		}

		return w;
	}

	/**
	 * Returns <i>z<sup>s</sup></i> where <i>z</i> is this complex number, and
	 * <i>s</i> is a complex number. For <i>z</i> = <i>r</i> e<sup>i
	 * <i>&#966;</i></sup> (that is, <i>r</i> = |<i>z</i>| and <i>&#966;</i> =
	 * arg <i>z</i>), and <i>s</i> = <i>x</i> + i<i>y</i>, we have
	 * <p style="text-align:center">
	 * <i>z<sup>s</sup></i> &nbsp; = &nbsp; <i>r<sup>x</sup></i> e<sup>-
	 * <i>y&#966;</i></sup> [ cos( <i>x&#966;</i> + <i>y</i> ln <i>r</i> ) + i
	 * sin( <i>x&#966;</i> + <i>y</i> ln <i>r</i> ) ].
	 * </p>
	 * 
	 * @param s
	 *            the exponent
	 * @return <i>z<sup>s</sup></i> where <i>z</i> is this complex number
	 * @see #power(double[],double[])
	 */
	public Complex pow(Complex s) {
		double r = abs();

		if (s.abs() < ACCURACY) { // s=0?
			return ONE;
		} else if (r < ACCURACY) { // z=0?
			return ZERO;
		}

		double phi = arg();
		double phase = s.z[0] * phi + s.z[1] * log(r);
		double[] w = new double[2];
		w[0] = Math.pow(r, s.z[0]) * Math.exp(-s.z[1] * phi);
		w[1] = w[0];

		w[0] *= Math.cos(phase);
		w[1] *= Math.sin(phase);

		return new Complex(w);
	}

	/**
	 * Returns <i>z<sup>s</sup></i> for two complex numbers <i>z</i>, <i>s</i>.
	 * For <i>z</i> = <i>r</i> e<sup>i<i>&#966;</i></sup> (that is, <i>r</i> = |
	 * <i>z</i>| and <i>&#966;</i> = arg <i>z</i>), and <i>s</i> = <i>x</i> + i
	 * <i>y</i>, we have
	 * <p style="text-align:center">
	 * <i>z<sup>s</sup></i> &nbsp; = &nbsp; <i>r<sup>x</sup></i> e<sup>-
	 * <i>y&#966;</i></sup> [ cos( <i>x&#966;</i> + <i>y</i> ln <i>r</i> ) + i
	 * sin( <i>x&#966;</i> + <i>y</i> ln <i>r</i> ) ].
	 * </p>
	 * 
	 * @param z
	 *            the base
	 * @param s
	 *            the exponent
	 * @return <i>z<sup>s</sup></i>
	 * @see #power(double,double[])
	 */
	public static double[] power(double[] z, double[] s) {
		double r = abs(z);

		if (abs(s) < ACCURACY) { // s=0?
			return ONE_;
		} else if (r < ACCURACY) { // z=0?
			return ZERO_; // w=0
		}

		double phi = arg(z);
		double phase = s[0] * phi + s[1] * log(r);
		double[] w = new double[2];
		w[0] = Math.pow(r, s[0]) * Math.exp(-s[1] * phi);
		w[1] = w[0];

		w[0] *= Math.cos(phase);
		w[1] *= Math.sin(phase);

		return w;
	}

	/**
	 * Returns the reciprocal of a complex number <i>y</i>. This method is
	 * implemented avoiding overflows.
	 * 
	 * @param y
	 *            a complex number
	 * @return 1/<i>y</i>
	 * @see #divide(double, double[])
	 * @see #reciprocal()
	 */
	public static double[] reciprocal(double[] y) {
		double[] w = new double[2];
		double h;

		if (Math.abs(y[0]) == 0 && Math.abs(y[1]) == 0) {
			w[0] = Double.POSITIVE_INFINITY;
			return w;
		}

		if (Math.abs(y[0]) >= Math.abs(y[1])) {
			h = y[1] / y[0];
			w[0] = 1 / (y[0] + y[1] * h);
			w[1] = -h / (y[0] + y[1] * h);
		} else {
			h = y[0] / y[1];
			w[0] = h / (y[0] * h + y[1]);
			w[1] = -1 / (y[0] * h + y[1]);
		}
		return w;
	}

	/**
	 * Returns the reciprocal of this number. This method is implemented
	 * avoiding overflows.
	 * 
	 * @return 1/<code>this</code>
	 * @see #reciprocal(double[])
	 */
	public Complex reciprocal() {
		double[] y = z;
		double[] w = new double[2];
		double h;

		if (Math.abs(y[0]) == 0 && Math.abs(y[1]) == 0) {
			w[0] = Double.POSITIVE_INFINITY;
			return new Complex(w);
		}

		if (Math.abs(y[0]) >= Math.abs(y[1])) {
			h = y[1] / y[0];
			w[0] = 1 / (y[0] + y[1] * h);
			w[1] = -h / (y[0] + y[1] * h);
		} else {
			h = y[0] / y[1];
			w[0] = h / (y[0] * h + y[1]);
			w[1] = -1 / (y[0] * h + y[1]);
		}
		return new Complex(w);
	}

	/**
	 * Returns the sine of a complex number <i>z</i>.
	 * 
	 * @param z
	 *            a complex number in the array representation
	 * @return sin <i>z</i>
	 */
	public static double[] sin(double[] z) {
		return new double[] { Math.sin(z[0]) * cosh(z[1]),
				Math.cos(z[0]) * sinh(z[1]) };
	}

	/**
	 * Returns the sine of a complex number <i>z</i>.
	 * 
	 * @param z
	 *            a complex number
	 * @return sin <i>z</i>
	 */
	public static Complex sin(Complex z) {
		return new Complex(Math.sin(z.z[0]) * cosh(z.z[1]),
				Math.cos(z.z[0]) * sinh(z.z[1]));
	}

	/**
	 * Returns the sine of this complex number.
	 * 
	 * @return sin <i>z</i>
	 */
	public Complex sin() {
		return new Complex(Math.sin(z[0]) * cosh(z[1]),
				Math.cos(z[0]) * sinh(z[1]));
	}

	/**
	 * Returns the square root of a complex number <i>z</i>.
	 * 
	 * @param z
	 *            a complex number in the array representation
	 * @return the suare root of <i>z</i>
	 */
	public static double[] sqrt(double[] z) {
		double[] y = { 0., 0. };
		double w;
		double h;

		if (Math.abs(z[0]) != 0 || Math.abs(z[1]) != 0) {
			if (Math.abs(z[0]) >= Math.abs(y[1])) {
				h = z[1] / z[0];
				w = Math.sqrt((1 + Math.sqrt(1 + h * h)) / 2);
				w = Math.sqrt(z[0]) * w;
			} else {
				h = Math.abs(z[0] / z[1]);
				w = Math.sqrt((h + Math.sqrt(1 + h * h)) / 2);
				w = Math.sqrt(z[1]) * w;
			}

			if (z[0] >= 0.) {
				y[0] = w;
				y[1] = z[1] / (2 * w);
			} else if (z[0] < 0 && z[1] >= 0) {
				y[0] = Math.abs(z[1]) / (2 * w);
				y[1] = w;
			} else if (z[0] < 0 && z[1] < 0) {
				y[0] = Math.abs(z[1]) / (2 * w);
				y[1] = -w;
			}
		}

		return y;
	}

	/**
	 * Returns the square root of a complex number <i>z</i>.
	 * 
	 * @param z
	 *            a complex number
	 * @return the suare root of <i>z</i>
	 */
	public static Complex sqrt(Complex z) {
		return new Complex(sqrt(new double[] { z.z[0], z.z[1] }));
	}

	/**
	 * subtracts two complex numbers <i>x</i> and <i>y</i>. We have subtract(
	 * <i>x</i>, <i>y</i>) = <i>x</i> - <i>y</i> = Re (<i>x</i> - <i>y</i>) + Im
	 * (<i>x</i> - <i>y</i>).
	 * 
	 * @param x
	 *            a complex number in the array representation
	 * @param y
	 *            a complex number in the array representation
	 * @return the difference <i>x</i> - <i>y</i>
	 */
	public static double[] subtract(double[] x, double[] y) {
		double[] result = new double[2];
		result[0] = x[0] - y[0];
		result[1] = x[1] - y[1];
		return result;
	}

	/**
	 * subtracts <i>z</i> from this complex number. We have <code>this</code>
	 * .subtract(<i>z</i>) = <code>this</code> - <i>z</i> = Re (
	 * <code>this</code> - <i>y</i>) + Im (<code>this</code> - <i>z</i>).
	 * 
	 * @param other
	 *            a complex number
	 * @return the difference <code>this</code> - <i>z</i>
	 * @see #minus(Complex)
	 * @see #subtract(double[],double[])
	 */
	public Complex subtract(Complex other) {
		return new Complex(this.z[0] - other.z[0], this.z[1] - other.z[1]);
	}

	/**
	 * Returns a string representation of this complex number in a "readable"
	 * standard format.
	 * 
	 * @return a string representing <i>z</i>
	 */
	@Override
	public String toString() {
		return toString(new double[] { z[0], z[1] });
	}

	/**
	 * Returns a string representation of the complex number <i>z</i> in a
	 * "readable" standard format.
	 * 
	 * @param z
	 *            the complex number to be formatted
	 * @return a string representing <i>z</i>
	 * @see #toString(double[])
	 */
	public static String toString(Complex z) {
		return toString(new double[] { z.z[0], z.z[1] });
	}

	/**
	 * displays a complex number to the "readable" format <code>digit</code>. If
	 * the real or the imaginary part are too large or too small, scientific
	 * notation is used.
	 * 
	 * @param z
	 *            the complex number to be formatted
	 * @return a string representing <i>z</i> in the specified decimal format
	 * @see #toString(double[])
	 */
	public static String toString(double[] z) {
		NumberFormatAdapter nf = FormatFactory.getPrototype()
				.getNumberFormat(13);
		return nf.format(z[0]) + "+" + nf.format(z[1]) + "i";
	}

	/** for test purposes ... */

}
