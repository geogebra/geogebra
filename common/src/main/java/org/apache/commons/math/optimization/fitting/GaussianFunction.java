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

import org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.ZeroException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * A Gaussian function.  Specifically:
 * <p>
 * <tt>f(x) = a + b*exp(-((x - c)^2 / (2*d^2)))</tt>
 * <p>
 * Notation key:
 * <ul>
 * <li><tt>x^n</tt>: <tt>x</tt> raised to the power of <tt>n</tt>
 * <li><tt>exp(x)</tt>: <i>e</i><tt>^x</tt>
 * </ul>
 * References:
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/Gaussian_function">Wikipedia:
 *   Gaussian function</a>
 * </ul>
 *
 * @see GaussianDerivativeFunction
 * @see ParametricGaussianFunction
 * @since 2.2
 * @version $Revision: 1037327 $ $Date: 2010-11-20 21:57:37 +0100 (sam. 20 nov. 2010) $
 */
public class GaussianFunction implements DifferentiableUnivariateRealFunction, Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -3195385616125629512L;

    /** Parameter a of this function. */
    private final double a;

    /** Parameter b of this function. */
    private final double b;

    /** Parameter c of this function. */
    private final double c;

    /** Parameter d of this function. */
    private final double d;

    /**
     * Constructs an instance with the specified parameters.
     *
     * @param a <tt>a</tt> parameter value
     * @param b <tt>b</tt> parameter value
     * @param c <tt>c</tt> parameter value
     * @param d <tt>d</tt> parameter value
     *
     * @throws IllegalArgumentException if <code>d</code> is 0
     */
    public GaussianFunction(double a, double b, double c, double d) {
        if (d == 0.0) {
            throw new ZeroException();
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    /**
     * Constructs an instance with the specified parameters.
     *
     * @param parameters <tt>a</tt>, <tt>b</tt>, <tt>c</tt>, and <tt>d</tt>
     *        parameter values
     *
     * @throws IllegalArgumentException if <code>parameters</code> is null,
     *         <code>parameters</code> length is not 4, or if
     *         <code>parameters[3]</code> is 0
     */
    public GaussianFunction(double[] parameters) {
        if (parameters == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
        }
        if (parameters.length != 4) {
            throw new DimensionMismatchException(4, parameters.length);
        }
        if (parameters[3] == 0.0) {
            throw new ZeroException();
        }
        this.a = parameters[0];
        this.b = parameters[1];
        this.c = parameters[2];
        this.d = parameters[3];
    }

    /** {@inheritDoc} */
    public UnivariateRealFunction derivative() {
        return new GaussianDerivativeFunction(b, c, d);
    }

    /** {@inheritDoc} */
    public double value(double x) {
        final double xMc = x - c;
        return a + b * Math.exp(-xMc * xMc / (2.0 * (d * d)));
    }

    /**
     * Gets <tt>a</tt> parameter value.
     *
     * @return <tt>a</tt> parameter value
     */
    public double getA() {
        return a;
    }

    /**
     * Gets <tt>b</tt> parameter value.
     *
     * @return <tt>b</tt> parameter value
     */
    public double getB() {
        return b;
    }

    /**
     * Gets <tt>c</tt> parameter value.
     *
     * @return <tt>c</tt> parameter value
     */
    public double getC() {
        return c;
    }

    /**
     * Gets <tt>d</tt> parameter value.
     *
     * @return <tt>d</tt> parameter value
     */
    public double getD() {
        return d;
    }
}
