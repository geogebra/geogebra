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

package org.apache.commons.math.optimization.fitting;

import java.io.Serializable;

import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.exception.ZeroException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.optimization.fitting.ParametricRealFunction;

/**
 * A Gaussian function.  Specifically:
 * <p>
 * <tt>f(x) = a + b*exp(-((x - c)^2 / (2*d^2)))</tt>
 * <p>
 * The parameters have the following meaning:
 * <ul>
 * <li><tt>a</tt> is a constant offset that shifts <tt>f(x)</tt> up or down
 * <li><tt>b</tt> is the height of the peak
 * <li><tt>c</tt> is the position of the center of the peak
 * <li><tt>d</tt> is related to the FWHM by <tt>FWHM = 2*sqrt(2*ln(2))*d</tt>
 * </ul>
 * Notation key:
 * <ul>
 * <li><tt>x^n</tt>: <tt>x</tt> raised to the power of <tt>n</tt>
 * <li><tt>exp(x)</tt>: <i>e</i><tt>^x</tt>
 * <li><tt>sqrt(x)</tt>: the square root of <tt>x</tt>
 * <li><tt>ln(x)</tt>: the natural logarithm of <tt>x</tt>
 * </ul>
 * References:
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/Gaussian_function">Wikipedia:
 *   Gaussian function</a>
 * </ul>
 *
 * @since 2.2
 * @version $Revision: 1037327 $ $Date: 2010-11-20 21:57:37 +0100 (sam. 20 nov. 2010) $
 */
public class ParametricGaussianFunction implements ParametricRealFunction, Serializable {

    /** Serializable version Id. */
    private static final long serialVersionUID = -3875578602503903233L;

    /**
     * Constructs an instance.
     */
    public ParametricGaussianFunction() {
    }

    /**
     * Computes value of function <tt>f(x)</tt> for the specified <tt>x</tt> and
     * parameters <tt>a</tt>, <tt>b</tt>, <tt>c</tt>, and <tt>d</tt>.
     *
     * @param x <tt>x</tt> value
     * @param parameters values of <tt>a</tt>, <tt>b</tt>, <tt>c</tt>, and
     *        <tt>d</tt>
     *
     * @return value of <tt>f(x)</tt> evaluated at <tt>x</tt> with the specified
     *         parameters
     *
     * @throws IllegalArgumentException if <code>parameters</code> is invalid as
     *         determined by {@link #validateParameters(double[])}
     * @throws ZeroException if <code>parameters</code> values are
     *         invalid as determined by {@link #validateParameters(double[])}
     */
    public double value(double x, double[] parameters) throws ZeroException {
        validateParameters(parameters);
        final double a = parameters[0];
        final double b = parameters[1];
        final double c = parameters[2];
        final double d = parameters[3];
        final double xMc = x - c;
        return a + b * Math.exp(-xMc * xMc / (2.0 * (d * d)));
    }

    /**
     * Computes the gradient vector for a four variable version of the function
     * where the parameters, <tt>a</tt>, <tt>b</tt>, <tt>c</tt>, and <tt>d</tt>,
     * are considered the variables, not <tt>x</tt>.  That is, instead of
     * computing the gradient vector for the function <tt>f(x)</tt> (which would
     * just be the derivative of <tt>f(x)</tt> with respect to <tt>x</tt> since
     * it's a one-dimensional function), computes the gradient vector for the
     * function <tt>f(a, b, c, d) = a + b*exp(-((x - c)^2 / (2*d^2)))</tt>
     * treating the specified <tt>x</tt> as a constant.
     * <p>
     * The components of the computed gradient vector are the partial
     * derivatives of <tt>f(a, b, c, d)</tt> with respect to each variable.
     * That is, the partial derivative of <tt>f(a, b, c, d)</tt> with respect to
     * <tt>a</tt>, the partial derivative of <tt>f(a, b, c, d)</tt> with respect
     * to <tt>b</tt>, the partial derivative of <tt>f(a, b, c, d)</tt> with
     * respect to <tt>c</tt>, and the partial derivative of <tt>f(a, b, c,
     * d)</tt> with respect to <tt>d</tt>.
     *
     * @param x <tt>x</tt> value to be used as constant in <tt>f(a, b, c,
     *        d)</tt>
     * @param parameters values of <tt>a</tt>, <tt>b</tt>, <tt>c</tt>, and
     *        <tt>d</tt> for computation of gradient vector of <tt>f(a, b, c,
     *        d)</tt>
     *
     * @return gradient vector of <tt>f(a, b, c, d)</tt>
     *
     * @throws IllegalArgumentException if <code>parameters</code> is invalid as
     *         determined by {@link #validateParameters(double[])}
     * @throws ZeroException if <code>parameters</code> values are
     *         invalid as determined by {@link #validateParameters(double[])}
     */
    public double[] gradient(double x, double[] parameters) throws ZeroException {

        validateParameters(parameters);
        final double b = parameters[1];
        final double c = parameters[2];
        final double d = parameters[3];

        final double xMc  = x - c;
        final double d2   = d * d;
        final double exp  = Math.exp(-xMc * xMc / (2 * d2));
        final double f    = b * exp * xMc / d2;

        return new double[] { 1.0, exp, f, f * xMc / d };

    }

    /**
     * Validates parameters to ensure they are appropriate for the evaluation of
     * the <code>value</code> and <code>gradient</code> methods.
     *
     * @param parameters values of <tt>a</tt>, <tt>b</tt>, <tt>c</tt>, and
     *        <tt>d</tt>
     *
     * @throws IllegalArgumentException if <code>parameters</code> is
     *         <code>null</code> or if <code>parameters</code> does not have
     *         length == 4
     * @throws ZeroException if <code>parameters[3]</code>
     *         (<tt>d</tt>) is 0
     */
    private void validateParameters(double[] parameters) throws ZeroException {
        if (parameters == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
        }
        if (parameters.length != 4) {
            throw new DimensionMismatchException(4, parameters.length);
        }
        if (parameters[3] == 0.0) {
            throw new ZeroException();
        }
    }

}
