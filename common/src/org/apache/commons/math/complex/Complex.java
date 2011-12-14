/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.complex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.FieldElement;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.MathUtils;
import org.apache.commons.math.util.FastMath;

/**
 * Representation of a Complex number - a number which has both a
 * real and imaginary part.
 * <p>
 * Implementations of arithmetic operations handle <code>NaN</code> and
 * infinite values according to the rules for {@link java.lang.Double}
 * arithmetic, applying definitional formulas and returning <code>NaN</code> or
 * infinite values in real or imaginary parts as these arise in computation.
 * See individual method javadocs for details.</p>
 * <p>
 * {@link #equals} identifies all values with <code>NaN</code> in either real
 * or imaginary part - e.g., <pre>
 * <code>1 + NaNi  == NaN + i == NaN + NaNi.</code></pre></p>
 *
 * implements Serializable since 2.0
 *
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 */
public class Complex implements FieldElement<Complex>, Serializable  {

    /** The square root of -1. A number representing "0.0 + 1.0i" */
    public static final Complex I = new Complex(0.0, 1.0);

    // CHECKSTYLE: stop ConstantName
    /** A complex number representing "NaN + NaNi" */
    public static final Complex NaN = new Complex(Double.NaN, Double.NaN);
    // CHECKSTYLE: resume ConstantName

    /** A complex number representing "+INF + INFi" */
    public static final Complex INF = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

    /** A complex number representing "1.0 + 0.0i" */
    public static final Complex ONE = new Complex(1.0, 0.0);

    /** A complex number representing "0.0 + 0.0i" */
    public static final Complex ZERO = new Complex(0.0, 0.0);

    /** Serializable version identifier */
    private static final long serialVersionUID = -6195664516687396620L;

    /** The imaginary part. */
    private final double imaginary;

    /** The real part. */
    private final double real;

    /** Record whether this complex number is equal to NaN. */
    private final transient boolean isNaN;

    /** Record whether this complex number is infinite. */
    private final transient boolean isInfinite;

    /**
     * Create a complex number given the real and imaginary parts.
     *
     * @param real the real part
     * @param imaginary the imaginary part
     */
    public Complex(double real, double imaginary) {
        super();
        this.real = real;
        this.imaginary = imaginary;

        isNaN = Double.isNaN(real) || Double.isNaN(imaginary);
        isInfinite = !isNaN &&
        (Double.isInfinite(real) || Double.isInfinite(imaginary));
    }

    /**
     * Return the absolute value of this complex number.
     * <p>
     * Returns <code>NaN</code> if either real or imaginary part is
     * <code>NaN</code> and <code>Double.POSITIVE_INFINITY</code> if
     * neither part is <code>NaN</code>, but at least one part takes an infinite
     * value.</p>
     *
     * @return the absolute value
     */
    public double abs() {
        if (isNaN()) {
            return Double.NaN;
        }

        if (isInfinite()) {
            return Double.POSITIVE_INFINITY;
        }

        if (FastMath.abs(real) < FastMath.abs(imaginary)) {
            if (imaginary == 0.0) {
                return FastMath.abs(real);
            }
            double q = real / imaginary;
            return FastMath.abs(imaginary) * FastMath.sqrt(1 + q * q);
        } else {
            if (real == 0.0) {
                return FastMath.abs(imaginary);
            }
            double q = imaginary / real;
            return FastMath.abs(real) * FastMath.sqrt(1 + q * q);
        }
    }

    /**
     * Return the sum of this complex number and the given complex number.
     * <p>
     * Uses the definitional formula
     * <pre>
     * (a + bi) + (c + di) = (a+c) + (b+d)i
     * </pre></p>
     * <p>
     * If either this or <code>rhs</code> has a NaN value in either part,
     * {@link #NaN} is returned; otherwise Inifinite and NaN values are
     * returned in the parts of the result according to the rules for
     * {@link java.lang.Double} arithmetic.</p>
     *
     * @param rhs the other complex number
     * @return the complex number sum
     * @throws NullPointerException if <code>rhs</code> is null
     */
    public Complex add(Complex rhs) {
        return createComplex(real + rhs.getReal(),
            imaginary + rhs.getImaginary());
    }

    /**
     * Return the conjugate of this complex number. The conjugate of
     * "A + Bi" is "A - Bi".
     * <p>
     * {@link #NaN} is returned if either the real or imaginary
     * part of this Complex number equals <code>Double.NaN</code>.</p>
     * <p>
     * If the imaginary part is infinite, and the real part is not NaN,
     * the returned value has infinite imaginary part of the opposite
     * sign - e.g. the conjugate of <code>1 + POSITIVE_INFINITY i</code>
     * is <code>1 - NEGATIVE_INFINITY i</code></p>
     *
     * @return the conjugate of this Complex object
     */
    public Complex conjugate() {
        if (isNaN()) {
            return NaN;
        }
        return createComplex(real, -imaginary);
    }

    /**
     * Return the quotient of this complex number and the given complex number.
     * <p>
     * Implements the definitional formula
     * <pre><code>
     *    a + bi          ac + bd + (bc - ad)i
     *    ----------- = -------------------------
     *    c + di         c<sup>2</sup> + d<sup>2</sup>
     * </code></pre>
     * but uses
     * <a href="http://doi.acm.org/10.1145/1039813.1039814">
     * prescaling of operands</a> to limit the effects of overflows and
     * underflows in the computation.</p>
     * <p>
     * Infinite and NaN values are handled / returned according to the
     * following rules, applied in the order presented:
     * <ul>
     * <li>If either this or <code>rhs</code> has a NaN value in either part,
     *  {@link #NaN} is returned.</li>
     * <li>If <code>rhs</code> equals {@link #ZERO}, {@link #NaN} is returned.
     * </li>
     * <li>If this and <code>rhs</code> are both infinite,
     * {@link #NaN} is returned.</li>
     * <li>If this is finite (i.e., has no infinite or NaN parts) and
     *  <code>rhs</code> is infinite (one or both parts infinite),
     * {@link #ZERO} is returned.</li>
     * <li>If this is infinite and <code>rhs</code> is finite, NaN values are
     * returned in the parts of the result if the {@link java.lang.Double}
     * rules applied to the definitional formula force NaN results.</li>
     * </ul></p>
     *
     * @param rhs the other complex number
     * @return the complex number quotient
     * @throws NullPointerException if <code>rhs</code> is null
     */
    public Complex divide(Complex rhs) {
        if (isNaN() || rhs.isNaN()) {
            return NaN;
        }

        double c = rhs.getReal();
        double d = rhs.getImaginary();
        if (c == 0.0 && d == 0.0) {
            return NaN;
        }

        if (rhs.isInfinite() && !isInfinite()) {
            return ZERO;
        }

        if (FastMath.abs(c) < FastMath.abs(d)) {
            double q = c / d;
            double denominator = c * q + d;
            return createComplex((real * q + imaginary) / denominator,
                (imaginary * q - real) / denominator);
        } else {
            double q = d / c;
            double denominator = d * q + c;
            return createComplex((imaginary * q + real) / denominator,
                (imaginary - real * q) / denominator);
        }
    }

    /**
     * Test for the equality of two Complex objects.
     * <p>
     * If both the real and imaginary parts of two Complex numbers
     * are exactly the same, and neither is <code>Double.NaN</code>, the two
     * Complex objects are considered to be equal.</p>
     * <p>
     * All <code>NaN</code> values are considered to be equal - i.e, if either
     * (or both) real and imaginary parts of the complex number are equal
     * to <code>Double.NaN</code>, the complex number is equal to
     * <code>Complex.NaN</code>.</p>
     *
     * @param other Object to test for equality to this
     * @return true if two Complex objects are equal, false if
     *         object is null, not an instance of Complex, or
     *         not equal to this Complex instance
     *
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Complex){
            Complex rhs = (Complex)other;
            if (rhs.isNaN()) {
                return this.isNaN();
            } else {
                return (real == rhs.real) && (imaginary == rhs.imaginary);
            }
        }
        return false;
    }

    /**
     * Get a hashCode for the complex number.
     * <p>
     * All NaN values have the same hash code.</p>
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        if (isNaN()) {
            return 7;
        }
        return 37 * (17 * MathUtils.hash(imaginary) +
            MathUtils.hash(real));
    }

    /**
     * Access the imaginary part.
     *
     * @return the imaginary part
     */
    public double getImaginary() {
        return imaginary;
    }

    /**
     * Access the real part.
     *
     * @return the real part
     */
    public double getReal() {
        return real;
    }

    /**
     * Returns true if either or both parts of this complex number is NaN;
     * false otherwise
     *
     * @return  true if either or both parts of this complex number is NaN;
     * false otherwise
     */
    public boolean isNaN() {
        return isNaN;
    }

    /**
     * Returns true if either the real or imaginary part of this complex number
     * takes an infinite value (either <code>Double.POSITIVE_INFINITY</code> or
     * <code>Double.NEGATIVE_INFINITY</code>) and neither part
     * is <code>NaN</code>.
     *
     * @return true if one or both parts of this complex number are infinite
     * and neither part is <code>NaN</code>
     */
    public boolean isInfinite() {
        return isInfinite;
    }

    /**
     * Return the product of this complex number and the given complex number.
     * <p>
     * Implements preliminary checks for NaN and infinity followed by
     * the definitional formula:
     * <pre><code>
     * (a + bi)(c + di) = (ac - bd) + (ad + bc)i
     * </code></pre>
     * </p>
     * <p>
     * Returns {@link #NaN} if either this or <code>rhs</code> has one or more
     * NaN parts.
     * </p>
     * Returns {@link #INF} if neither this nor <code>rhs</code> has one or more
     * NaN parts and if either this or <code>rhs</code> has one or more
     * infinite parts (same result is returned regardless of the sign of the
     * components).
     * </p>
     * <p>
     * Returns finite values in components of the result per the
     * definitional formula in all remaining cases.
     *  </p>
     *
     * @param rhs the other complex number
     * @return the complex number product
     * @throws NullPointerException if <code>rhs</code> is null
     */
    public Complex multiply(Complex rhs) {
        if (isNaN() || rhs.isNaN()) {
            return NaN;
        }
        if (Double.isInfinite(real) || Double.isInfinite(imaginary) ||
            Double.isInfinite(rhs.real)|| Double.isInfinite(rhs.imaginary)) {
            // we don't use Complex.isInfinite() to avoid testing for NaN again
            return INF;
        }
        return createComplex(real * rhs.real - imaginary * rhs.imaginary,
                real * rhs.imaginary + imaginary * rhs.real);
    }

    /**
     * Return the product of this complex number and the given scalar number.
     * <p>
     * Implements preliminary checks for NaN and infinity followed by
     * the definitional formula:
     * <pre><code>
     * c(a + bi) = (ca) + (cb)i
     * </code></pre>
     * </p>
     * <p>
     * Returns {@link #NaN} if either this or <code>rhs</code> has one or more
     * NaN parts.
     * </p>
     * Returns {@link #INF} if neither this nor <code>rhs</code> has one or more
     * NaN parts and if either this or <code>rhs</code> has one or more
     * infinite parts (same result is returned regardless of the sign of the
     * components).
     * </p>
     * <p>
     * Returns finite values in components of the result per the
     * definitional formula in all remaining cases.
     *  </p>
     *
     * @param rhs the scalar number
     * @return the complex number product
     */
    public Complex multiply(double rhs) {
        if (isNaN() || Double.isNaN(rhs)) {
            return NaN;
        }
        if (Double.isInfinite(real) || Double.isInfinite(imaginary) ||
            Double.isInfinite(rhs)) {
            // we don't use Complex.isInfinite() to avoid testing for NaN again
            return INF;
        }
        return createComplex(real * rhs, imaginary * rhs);
    }

    /**
     * Return the additive inverse of this complex number.
     * <p>
     * Returns <code>Complex.NaN</code> if either real or imaginary
     * part of this Complex number equals <code>Double.NaN</code>.</p>
     *
     * @return the negation of this complex number
     */
    public Complex negate() {
        if (isNaN()) {
            return NaN;
        }

        return createComplex(-real, -imaginary);
    }

    /**
     * Return the difference between this complex number and the given complex
     * number.
      * <p>
     * Uses the definitional formula
     * <pre>
     * (a + bi) - (c + di) = (a-c) + (b-d)i
     * </pre></p>
     * <p>
     * If either this or <code>rhs</code> has a NaN value in either part,
     * {@link #NaN} is returned; otherwise inifinite and NaN values are
     * returned in the parts of the result according to the rules for
     * {@link java.lang.Double} arithmetic. </p>
     *
     * @param rhs the other complex number
     * @return the complex number difference
     * @throws NullPointerException if <code>rhs</code> is null
     */
    public Complex subtract(Complex rhs) {
        if (isNaN() || rhs.isNaN()) {
            return NaN;
        }

        return createComplex(real - rhs.getReal(),
            imaginary - rhs.getImaginary());
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/InverseCosine.html" TARGET="_top">
     * inverse cosine</a> of this complex number.
     * <p>
     * Implements the formula: <pre>
     * <code> acos(z) = -i (log(z + i (sqrt(1 - z<sup>2</sup>))))</code></pre></p>
     * <p>
     * Returns {@link Complex#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code> or infinite.</p>
     *
     * @return the inverse cosine of this complex number
     * @since 1.2
     */
    public Complex acos() {
        if (isNaN()) {
            return Complex.NaN;
        }

        return this.add(this.sqrt1z().multiply(Complex.I)).log()
              .multiply(Complex.I.negate());
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/InverseSine.html" TARGET="_top">
     * inverse sine</a> of this complex number.
     * <p>
     * Implements the formula: <pre>
     * <code> asin(z) = -i (log(sqrt(1 - z<sup>2</sup>) + iz)) </code></pre></p>
     * <p>
     * Returns {@link Complex#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code> or infinite.</p>
     *
     * @return the inverse sine of this complex number.
     * @since 1.2
     */
    public Complex asin() {
        if (isNaN()) {
            return Complex.NaN;
        }

        return sqrt1z().add(this.multiply(Complex.I)).log()
              .multiply(Complex.I.negate());
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/InverseTangent.html" TARGET="_top">
     * inverse tangent</a> of this complex number.
     * <p>
     * Implements the formula: <pre>
     * <code> atan(z) = (i/2) log((i + z)/(i - z)) </code></pre></p>
     * <p>
     * Returns {@link Complex#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code> or infinite.</p>
     *
     * @return the inverse tangent of this complex number
     * @since 1.2
     */
    public Complex atan() {
        if (isNaN()) {
            return Complex.NaN;
        }

        return this.add(Complex.I).divide(Complex.I.subtract(this)).log()
            .multiply(Complex.I.divide(createComplex(2.0, 0.0)));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/Cosine.html" TARGET="_top">
     * cosine</a>
     * of this complex number.
     * <p>
     * Implements the formula: <pre>
     * <code> cos(a + bi) = cos(a)cosh(b) - sin(a)sinh(b)i</code></pre>
     * where the (real) functions on the right-hand side are
     * {@link java.lang.Math#sin}, {@link java.lang.Math#cos},
     * {@link MathUtils#cosh} and {@link MathUtils#sinh}.</p>
     * <p>
     * Returns {@link Complex#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.</p>
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * cos(1 &plusmn; INFINITY i) = 1 &#x2213; INFINITY i
     * cos(&plusmn;INFINITY + i) = NaN + NaN i
     * cos(&plusmn;INFINITY &plusmn; INFINITY i) = NaN + NaN i</code></pre></p>
     *
     * @return the cosine of this complex number
     * @since 1.2
     */
    public Complex cos() {
        if (isNaN()) {
            return Complex.NaN;
        }

        return createComplex(FastMath.cos(real) * MathUtils.cosh(imaginary),
            -FastMath.sin(real) * MathUtils.sinh(imaginary));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/HyperbolicCosine.html" TARGET="_top">
     * hyperbolic cosine</a> of this complex number.
     * <p>
     * Implements the formula: <pre>
     * <code> cosh(a + bi) = cosh(a)cos(b) + sinh(a)sin(b)i</code></pre>
     * where the (real) functions on the right-hand side are
     * {@link java.lang.Math#sin}, {@link java.lang.Math#cos},
     * {@link MathUtils#cosh} and {@link MathUtils#sinh}.</p>
     * <p>
     * Returns {@link Complex#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.</p>
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * cosh(1 &plusmn; INFINITY i) = NaN + NaN i
     * cosh(&plusmn;INFINITY + i) = INFINITY &plusmn; INFINITY i
     * cosh(&plusmn;INFINITY &plusmn; INFINITY i) = NaN + NaN i</code></pre></p>
     *
     * @return the hyperbolic cosine of this complex number.
     * @since 1.2
     */
    public Complex cosh() {
        if (isNaN()) {
            return Complex.NaN;
        }

        return createComplex(MathUtils.cosh(real) * FastMath.cos(imaginary),
            MathUtils.sinh(real) * FastMath.sin(imaginary));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/ExponentialFunction.html" TARGET="_top">
     * exponential function</a> of this complex number.
     * <p>
     * Implements the formula: <pre>
     * <code> exp(a + bi) = exp(a)cos(b) + exp(a)sin(b)i</code></pre>
     * where the (real) functions on the right-hand side are
     * {@link java.lang.Math#exp}, {@link java.lang.Math#cos}, and
     * {@link java.lang.Math#sin}.</p>
     * <p>
     * Returns {@link Complex#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.</p>
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * exp(1 &plusmn; INFINITY i) = NaN + NaN i
     * exp(INFINITY + i) = INFINITY + INFINITY i
     * exp(-INFINITY + i) = 0 + 0i
     * exp(&plusmn;INFINITY &plusmn; INFINITY i) = NaN + NaN i</code></pre></p>
     *
     * @return <i>e</i><sup><code>this</code></sup>
     * @since 1.2
     */
    public Complex exp() {
        if (isNaN()) {
            return Complex.NaN;
        }

        double expReal = FastMath.exp(real);
        return createComplex(expReal *  FastMath.cos(imaginary), expReal * FastMath.sin(imaginary));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/NaturalLogarithm.html" TARGET="_top">
     * natural logarithm</a> of this complex number.
     * <p>
     * Implements the formula: <pre>
     * <code> log(a + bi) = ln(|a + bi|) + arg(a + bi)i</code></pre>
     * where ln on the right hand side is {@link java.lang.Math#log},
     * <code>|a + bi|</code> is the modulus, {@link Complex#abs},  and
     * <code>arg(a + bi) = {@link java.lang.Math#atan2}(b, a)</code></p>
     * <p>
     * Returns {@link Complex#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.</p>
     * <p>
     * Infinite (or critical) values in real or imaginary parts of the input may
     * result in infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * log(1 &plusmn; INFINITY i) = INFINITY &plusmn; (&pi;/2)i
     * log(INFINITY + i) = INFINITY + 0i
     * log(-INFINITY + i) = INFINITY + &pi;i
     * log(INFINITY &plusmn; INFINITY i) = INFINITY &plusmn; (&pi;/4)i
     * log(-INFINITY &plusmn; INFINITY i) = INFINITY &plusmn; (3&pi;/4)i
     * log(0 + 0i) = -INFINITY + 0i
     * </code></pre></p>
     *
     * @return ln of this complex number.
     * @since 1.2
     */
    public Complex log() {
        if (isNaN()) {
            return Complex.NaN;
        }

        return createComplex(FastMath.log(abs()),
            FastMath.atan2(imaginary, real));
    }

    /**
     * Returns of value of this complex number raised to the power of <code>x</code>.
     * <p>
     * Implements the formula: <pre>
     * <code> y<sup>x</sup> = exp(x&middot;log(y))</code></pre>
     * where <code>exp</code> and <code>log</code> are {@link #exp} and
     * {@link #log}, respectively.</p>
     * <p>
     * Returns {@link Complex#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code> or infinite, or if <code>y</code>
     * equals {@link Complex#ZERO}.</p>
     *
     * @param x the exponent.
     * @return <code>this</code><sup><code>x</code></sup>
     * @throws NullPointerException if x is null
     * @since 1.2
     */
    public Complex pow(Complex x) {
        if (x == null) {
            throw new NullPointerException();
        }
        return this.log().multiply(x).exp();
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/Sine.html" TARGET="_top">
     * sine</a>
     * of this complex number.
     * <p>
     * Implements the formula: <pre>
     * <code> sin(a + bi) = sin(a)cosh(b) - cos(a)sinh(b)i</code></pre>
     * where the (real) functions on the right-hand side are
     * {@link java.lang.Math#sin}, {@link java.lang.Math#cos},
     * {@link MathUtils#cosh} and {@link MathUtils#sinh}.</p>
     * <p>
     * Returns {@link Complex#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.</p>
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * sin(1 &plusmn; INFINITY i) = 1 &plusmn; INFINITY i
     * sin(&plusmn;INFINITY + i) = NaN + NaN i
     * sin(&plusmn;INFINITY &plusmn; INFINITY i) = NaN + NaN i</code></pre></p>
     *
     * @return the sine of this complex number.
     * @since 1.2
     */
    public Complex sin() {
        if (isNaN()) {
            return Complex.NaN;
        }

        return createComplex(FastMath.sin(real) * MathUtils.cosh(imaginary),
            FastMath.cos(real) * MathUtils.sinh(imaginary));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/HyperbolicSine.html" TARGET="_top">
     * hyperbolic sine</a> of this complex number.
     * <p>
     * Implements the formula: <pre>
     * <code> sinh(a + bi) = sinh(a)cos(b)) + cosh(a)sin(b)i</code></pre>
     * where the (real) functions on the right-hand side are
     * {@link java.lang.Math#sin}, {@link java.lang.Math#cos},
     * {@link MathUtils#cosh} and {@link MathUtils#sinh}.</p>
     * <p>
     * Returns {@link Complex#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.</p>
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * sinh(1 &plusmn; INFINITY i) = NaN + NaN i
     * sinh(&plusmn;INFINITY + i) = &plusmn; INFINITY + INFINITY i
     * sinh(&plusmn;INFINITY &plusmn; INFINITY i) = NaN + NaN i</code></pre></p>
     *
     * @return the hyperbolic sine of this complex number
     * @since 1.2
     */
    public Complex sinh() {
        if (isNaN()) {
            return Complex.NaN;
        }

        return createComplex(MathUtils.sinh(real) * FastMath.cos(imaginary),
            MathUtils.cosh(real) * FastMath.sin(imaginary));
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/SquareRoot.html" TARGET="_top">
     * square root</a> of this complex number.
     * <p>
     * Implements the following algorithm to compute <code>sqrt(a + bi)</code>:
     * <ol><li>Let <code>t = sqrt((|a| + |a + bi|) / 2)</code></li>
     * <li><pre>if <code> a &#8805; 0</code> return <code>t + (b/2t)i</code>
     *  else return <code>|b|/2t + sign(b)t i </code></pre></li>
     * </ol>
     * where <ul>
     * <li><code>|a| = {@link Math#abs}(a)</code></li>
     * <li><code>|a + bi| = {@link Complex#abs}(a + bi) </code></li>
     * <li><code>sign(b) =  {@link MathUtils#indicator}(b) </code>
     * </ul></p>
     * <p>
     * Returns {@link Complex#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.</p>
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * sqrt(1 &plusmn; INFINITY i) = INFINITY + NaN i
     * sqrt(INFINITY + i) = INFINITY + 0i
     * sqrt(-INFINITY + i) = 0 + INFINITY i
     * sqrt(INFINITY &plusmn; INFINITY i) = INFINITY + NaN i
     * sqrt(-INFINITY &plusmn; INFINITY i) = NaN &plusmn; INFINITY i
     * </code></pre></p>
     *
     * @return the square root of this complex number
     * @since 1.2
     */
    public Complex sqrt() {
        if (isNaN()) {
            return Complex.NaN;
        }

        if (real == 0.0 && imaginary == 0.0) {
            return createComplex(0.0, 0.0);
        }

        double t = FastMath.sqrt((FastMath.abs(real) + abs()) / 2.0);
        if (real >= 0.0) {
            return createComplex(t, imaginary / (2.0 * t));
        } else {
            return createComplex(FastMath.abs(imaginary) / (2.0 * t),
                MathUtils.indicator(imaginary) * t);
        }
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/SquareRoot.html" TARGET="_top">
     * square root</a> of 1 - <code>this</code><sup>2</sup> for this complex
     * number.
     * <p>
     * Computes the result directly as
     * <code>sqrt(Complex.ONE.subtract(z.multiply(z)))</code>.</p>
     * <p>
     * Returns {@link Complex#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.</p>
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.</p>
     *
     * @return the square root of 1 - <code>this</code><sup>2</sup>
     * @since 1.2
     */
    public Complex sqrt1z() {
        return createComplex(1.0, 0.0).subtract(this.multiply(this)).sqrt();
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/Tangent.html" TARGET="_top">
     * tangent</a> of this complex number.
     * <p>
     * Implements the formula: <pre>
     * <code>tan(a + bi) = sin(2a)/(cos(2a)+cosh(2b)) + [sinh(2b)/(cos(2a)+cosh(2b))]i</code></pre>
     * where the (real) functions on the right-hand side are
     * {@link java.lang.Math#sin}, {@link java.lang.Math#cos},
     * {@link MathUtils#cosh} and {@link MathUtils#sinh}.</p>
     * <p>
     * Returns {@link Complex#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.</p>
     * <p>
     * Infinite (or critical) values in real or imaginary parts of the input may
     * result in infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * tan(1 &plusmn; INFINITY i) = 0 + NaN i
     * tan(&plusmn;INFINITY + i) = NaN + NaN i
     * tan(&plusmn;INFINITY &plusmn; INFINITY i) = NaN + NaN i
     * tan(&plusmn;&pi;/2 + 0 i) = &plusmn;INFINITY + NaN i</code></pre></p>
     *
     * @return the tangent of this complex number
     * @since 1.2
     */
    public Complex tan() {
        if (isNaN()) {
            return Complex.NaN;
        }

        double real2 = 2.0 * real;
        double imaginary2 = 2.0 * imaginary;
        double d = FastMath.cos(real2) + MathUtils.cosh(imaginary2);

        return createComplex(FastMath.sin(real2) / d, MathUtils.sinh(imaginary2) / d);
    }

    /**
     * Compute the
     * <a href="http://mathworld.wolfram.com/HyperbolicTangent.html" TARGET="_top">
     * hyperbolic tangent</a> of this complex number.
     * <p>
     * Implements the formula: <pre>
     * <code>tan(a + bi) = sinh(2a)/(cosh(2a)+cos(2b)) + [sin(2b)/(cosh(2a)+cos(2b))]i</code></pre>
     * where the (real) functions on the right-hand side are
     * {@link java.lang.Math#sin}, {@link java.lang.Math#cos},
     * {@link MathUtils#cosh} and {@link MathUtils#sinh}.</p>
     * <p>
     * Returns {@link Complex#NaN} if either real or imaginary part of the
     * input argument is <code>NaN</code>.</p>
     * <p>
     * Infinite values in real or imaginary parts of the input may result in
     * infinite or NaN values returned in parts of the result.<pre>
     * Examples:
     * <code>
     * tanh(1 &plusmn; INFINITY i) = NaN + NaN i
     * tanh(&plusmn;INFINITY + i) = NaN + 0 i
     * tanh(&plusmn;INFINITY &plusmn; INFINITY i) = NaN + NaN i
     * tanh(0 + (&pi;/2)i) = NaN + INFINITY i</code></pre></p>
     *
     * @return the hyperbolic tangent of this complex number
     * @since 1.2
     */
    public Complex tanh() {
        if (isNaN()) {
            return Complex.NaN;
        }

        double real2 = 2.0 * real;
        double imaginary2 = 2.0 * imaginary;
        double d = MathUtils.cosh(real2) + FastMath.cos(imaginary2);

        return createComplex(MathUtils.sinh(real2) / d, FastMath.sin(imaginary2) / d);
    }



    /**
     * <p>Compute the argument of this complex number.
     * </p>
     * <p>The argument is the angle phi between the positive real axis and the point
     * representing this number in the complex plane. The value returned is between -PI (not inclusive)
     * and PI (inclusive), with negative values returned for numbers with negative imaginary parts.
     * </p>
     * <p>If either real or imaginary part (or both) is NaN, NaN is returned.  Infinite parts are handled
     * as java.Math.atan2 handles them, essentially treating finite parts as zero in the presence of
     * an infinite coordinate and returning a multiple of pi/4 depending on the signs of the infinite
     * parts.  See the javadoc for java.Math.atan2 for full details.</p>
     *
     * @return the argument of this complex number
     */
    public double getArgument() {
        return FastMath.atan2(getImaginary(), getReal());
    }

    /**
     * <p>Computes the n-th roots of this complex number.
     * </p>
     * <p>The nth roots are defined by the formula: <pre>
     * <code> z<sub>k</sub> = abs<sup> 1/n</sup> (cos(phi + 2&pi;k/n) + i (sin(phi + 2&pi;k/n))</code></pre>
     * for <i><code>k=0, 1, ..., n-1</code></i>, where <code>abs</code> and <code>phi</code> are
     * respectively the {@link #abs() modulus} and {@link #getArgument() argument} of this complex number.
     * </p>
     * <p>If one or both parts of this complex number is NaN, a list with just one element,
     *  {@link #NaN} is returned.</p>
     * <p>if neither part is NaN, but at least one part is infinite, the result is a one-element
     * list containing {@link #INF}.</p>
     *
     * @param n degree of root
     * @return List<Complex> all nth roots of this complex number
     * @throws IllegalArgumentException if parameter n is less than or equal to 0
     * @since 2.0
     */
    public List<Complex> nthRoot(int n) throws IllegalArgumentException {

        if (n <= 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.CANNOT_COMPUTE_NTH_ROOT_FOR_NEGATIVE_N,
                    n);
        }

        List<Complex> result = new ArrayList<Complex>();

        if (isNaN()) {
            result.add(Complex.NaN);
            return result;
        }

        if (isInfinite()) {
            result.add(Complex.INF);
            return result;
        }

        // nth root of abs -- faster / more accurate to use a solver here?
        final double nthRootOfAbs = FastMath.pow(abs(), 1.0 / n);

        // Compute nth roots of complex number with k = 0, 1, ... n-1
        final double nthPhi = getArgument()/n;
        final double slice = 2 * FastMath.PI / n;
        double innerPart = nthPhi;
        for (int k = 0; k < n ; k++) {
            // inner part
            final double realPart      = nthRootOfAbs *  FastMath.cos(innerPart);
            final double imaginaryPart = nthRootOfAbs *  FastMath.sin(innerPart);
            result.add(createComplex(realPart, imaginaryPart));
            innerPart += slice;
        }

        return result;
    }

    /**
     * Create a complex number given the real and imaginary parts.
     *
     * @param realPart the real part
     * @param imaginaryPart the imaginary part
     * @return a new complex number instance
     * @since 1.2
     */
    protected Complex createComplex(double realPart, double imaginaryPart) {
        return new Complex(realPart, imaginaryPart);
    }

    /**
     * <p>Resolve the transient fields in a deserialized Complex Object.</p>
     * <p>Subclasses will need to override {@link #createComplex} to deserialize properly</p>
     * @return A Complex instance with all fields resolved.
     * @since 2.0
     */
    protected final Object readResolve() {
        return createComplex(real, imaginary);
    }

    /** {@inheritDoc} */
    public ComplexField getField() {
        return ComplexField.getInstance();
    }

}
