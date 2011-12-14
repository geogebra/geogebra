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
package org.apache.commons.math.analysis.polynomials;

import org.apache.commons.math.DuplicateSampleAbscissaException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * Implements the representation of a real polynomial function in
 * <a href="http://mathworld.wolfram.com/LagrangeInterpolatingPolynomial.html">
 * Lagrange Form</a>. For reference, see <b>Introduction to Numerical
 * Analysis</b>, ISBN 038795452X, chapter 2.
 * <p>
 * The approximated function should be smooth enough for Lagrange polynomial
 * to work well. Otherwise, consider using splines instead.</p>
 *
 * @version $Revision: 1073498 $ $Date: 2011-02-22 21:57:26 +0100 (mar. 22 févr. 2011) $
 * @since 1.2
 */
public class PolynomialFunctionLagrangeForm implements UnivariateRealFunction {

    /**
     * The coefficients of the polynomial, ordered by degree -- i.e.
     * coefficients[0] is the constant term and coefficients[n] is the
     * coefficient of x^n where n is the degree of the polynomial.
     */
    private double coefficients[];

    /**
     * Interpolating points (abscissas).
     */
    private final double x[];

    /**
     * Function values at interpolating points.
     */
    private final double y[];

    /**
     * Whether the polynomial coefficients are available.
     */
    private boolean coefficientsComputed;

    /**
     * Construct a Lagrange polynomial with the given abscissas and function
     * values. The order of interpolating points are not important.
     * <p>
     * The constructor makes copy of the input arrays and assigns them.</p>
     *
     * @param x interpolating points
     * @param y function values at interpolating points
     * @throws IllegalArgumentException if input arrays are not valid
     */
    public PolynomialFunctionLagrangeForm(double x[], double y[])
        throws IllegalArgumentException {

        verifyInterpolationArray(x, y);
        this.x = new double[x.length];
        this.y = new double[y.length];
        System.arraycopy(x, 0, this.x, 0, x.length);
        System.arraycopy(y, 0, this.y, 0, y.length);
        coefficientsComputed = false;
    }

    /** {@inheritDoc} */
    public double value(double z) throws FunctionEvaluationException {
        try {
            return evaluate(x, y, z);
        } catch (DuplicateSampleAbscissaException e) {
            throw new FunctionEvaluationException(z, e.getSpecificPattern(), e.getGeneralPattern(), e.getArguments());
        }
    }

    /**
     * Returns the degree of the polynomial.
     *
     * @return the degree of the polynomial
     */
    public int degree() {
        return x.length - 1;
    }

    /**
     * Returns a copy of the interpolating points array.
     * <p>
     * Changes made to the returned copy will not affect the polynomial.</p>
     *
     * @return a fresh copy of the interpolating points array
     */
    public double[] getInterpolatingPoints() {
        double[] out = new double[x.length];
        System.arraycopy(x, 0, out, 0, x.length);
        return out;
    }

    /**
     * Returns a copy of the interpolating values array.
     * <p>
     * Changes made to the returned copy will not affect the polynomial.</p>
     *
     * @return a fresh copy of the interpolating values array
     */
    public double[] getInterpolatingValues() {
        double[] out = new double[y.length];
        System.arraycopy(y, 0, out, 0, y.length);
        return out;
    }

    /**
     * Returns a copy of the coefficients array.
     * <p>
     * Changes made to the returned copy will not affect the polynomial.</p>
     * <p>
     * Note that coefficients computation can be ill-conditioned. Use with caution
     * and only when it is necessary.</p>
     *
     * @return a fresh copy of the coefficients array
     */
    public double[] getCoefficients() {
        if (!coefficientsComputed) {
            computeCoefficients();
        }
        double[] out = new double[coefficients.length];
        System.arraycopy(coefficients, 0, out, 0, coefficients.length);
        return out;
    }

    /**
     * Evaluate the Lagrange polynomial using
     * <a href="http://mathworld.wolfram.com/NevillesAlgorithm.html">
     * Neville's Algorithm</a>. It takes O(N^2) time.
     * <p>
     * This function is made public static so that users can call it directly
     * without instantiating PolynomialFunctionLagrangeForm object.</p>
     *
     * @param x the interpolating points array
     * @param y the interpolating values array
     * @param z the point at which the function value is to be computed
     * @return the function value
     * @throws DuplicateSampleAbscissaException if the sample has duplicate abscissas
     * @throws IllegalArgumentException if inputs are not valid
     */
    public static double evaluate(double x[], double y[], double z) throws
        DuplicateSampleAbscissaException, IllegalArgumentException {

        verifyInterpolationArray(x, y);

        int nearest = 0;
        final int n = x.length;
        final double[] c = new double[n];
        final double[] d = new double[n];
        double min_dist = Double.POSITIVE_INFINITY;
        for (int i = 0; i < n; i++) {
            // initialize the difference arrays
            c[i] = y[i];
            d[i] = y[i];
            // find out the abscissa closest to z
            final double dist = FastMath.abs(z - x[i]);
            if (dist < min_dist) {
                nearest = i;
                min_dist = dist;
            }
        }

        // initial approximation to the function value at z
        double value = y[nearest];

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < n-i; j++) {
                final double tc = x[j] - z;
                final double td = x[i+j] - z;
                final double divider = x[j] - x[i+j];
                if (divider == 0.0) {
                    // This happens only when two abscissas are identical.
                    throw new DuplicateSampleAbscissaException(x[i], i, i+j);
                }
                // update the difference arrays
                final double w = (c[j+1] - d[j]) / divider;
                c[j] = tc * w;
                d[j] = td * w;
            }
            // sum up the difference terms to get the final value
            if (nearest < 0.5*(n-i+1)) {
                value += c[nearest];    // fork down
            } else {
                nearest--;
                value += d[nearest];    // fork up
            }
        }

        return value;
    }

    /**
     * Calculate the coefficients of Lagrange polynomial from the
     * interpolation data. It takes O(N^2) time.
     * <p>
     * Note this computation can be ill-conditioned. Use with caution
     * and only when it is necessary.</p>
     *
     * @throws ArithmeticException if any abscissas coincide
     */
    protected void computeCoefficients() throws ArithmeticException {

        final int n = degree() + 1;
        coefficients = new double[n];
        for (int i = 0; i < n; i++) {
            coefficients[i] = 0.0;
        }

        // c[] are the coefficients of P(x) = (x-x[0])(x-x[1])...(x-x[n-1])
        final double[] c = new double[n+1];
        c[0] = 1.0;
        for (int i = 0; i < n; i++) {
            for (int j = i; j > 0; j--) {
                c[j] = c[j-1] - c[j] * x[i];
            }
            c[0] *= -x[i];
            c[i+1] = 1;
        }

        final double[] tc = new double[n];
        for (int i = 0; i < n; i++) {
            // d = (x[i]-x[0])...(x[i]-x[i-1])(x[i]-x[i+1])...(x[i]-x[n-1])
            double d = 1;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    d *= x[i] - x[j];
                }
            }
            if (d == 0.0) {
                // This happens only when two abscissas are identical.
                for (int k = 0; k < n; ++k) {
                    if ((i != k) && (x[i] == x[k])) {
                        throw MathRuntimeException.createArithmeticException(
                              LocalizedFormats.IDENTICAL_ABSCISSAS_DIVISION_BY_ZERO,
                              i, k, x[i]);
                    }
                }
            }
            final double t = y[i] / d;
            // Lagrange polynomial is the sum of n terms, each of which is a
            // polynomial of degree n-1. tc[] are the coefficients of the i-th
            // numerator Pi(x) = (x-x[0])...(x-x[i-1])(x-x[i+1])...(x-x[n-1]).
            tc[n-1] = c[n];     // actually c[n] = 1
            coefficients[n-1] += t * tc[n-1];
            for (int j = n-2; j >= 0; j--) {
                tc[j] = c[j+1] + tc[j+1] * x[i];
                coefficients[j] += t * tc[j];
            }
        }

        coefficientsComputed = true;
    }

    /**
     * Verifies that the interpolation arrays are valid.
     * <p>
     * The arrays features checked by this method are that both arrays have the
     * same length and this length is at least 2.
     * </p>
     * <p>
     * The interpolating points must be distinct. However it is not
     * verified here, it is checked in evaluate() and computeCoefficients().
     * </p>
     *
     * @param x the interpolating points array
     * @param y the interpolating values array
     * @throws IllegalArgumentException if not valid
     * @see #evaluate(double[], double[], double)
     * @see #computeCoefficients()
     */
    public static void verifyInterpolationArray(double x[], double y[])
        throws IllegalArgumentException {

        if (x.length != y.length) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, x.length, y.length);
        }

        if (x.length < 2) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.WRONG_NUMBER_OF_POINTS, 2, x.length);
        }

    }
}
