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

import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.exception.ZeroException;
import org.apache.commons.math.exception.NullArgumentException;

/**
 * The derivative of {@link GaussianFunction}.  Specifically:
 * <p>
 * <tt>f'(x) = (-b / (d^2)) * (x - c) * exp(-((x - c)^2) / (2*(d^2)))</tt>
 * <p>
 * Notation key:
 * <ul>
 * <li><tt>x^n</tt>: <tt>x</tt> raised to the power of <tt>n</tt>
 * <li><tt>exp(x)</tt>: <i>e</i><tt>^x</tt>
 * </ul>
 *
 * @since 2.2
 * @version $Revision: 1037327 $ $Date: 2010-11-20 21:57:37 +0100 (sam. 20 nov. 2010) $
 */
public class GaussianDerivativeFunction implements UnivariateRealFunction, Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -6500229089670174766L;

    /** Parameter b of this function. */
    private final double b;

    /** Parameter c of this function. */
    private final double c;

    /** Square of the parameter d of this function. */
    private final double d2;

    /**
     * Constructs an instance with the specified parameters.
     *
     * @param b <tt>b</tt> parameter value
     * @param c <tt>c</tt> parameter value
     * @param d <tt>d</tt> parameter value
     *
     * @throws IllegalArgumentException if <code>d</code> is 0
     */
    public GaussianDerivativeFunction(double b, double c, double d) {
        if (d == 0.0) {
            throw new ZeroException();
        }
        this.b = b;
        this.c = c;
        this.d2 = d * d;
    }

    /**
     * Constructs an instance with the specified parameters.
     *
     * @param parameters <tt>b</tt>, <tt>c</tt>, and <tt>d</tt> parameter values
     *
     * @throws IllegalArgumentException if <code>parameters</code> is null,
     *         <code>parameters</code> length is not 3, or if
     *         <code>parameters[2]</code> is 0
     */
    public GaussianDerivativeFunction(double[] parameters) {
        if (parameters == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
        }
        if (parameters.length != 3) {
            throw new DimensionMismatchException(3, parameters.length);
        }
        if (parameters[2] == 0.0) {
            throw new ZeroException();
        }
        this.b = parameters[0];
        this.c = parameters[1];
        this.d2 = parameters[2] * parameters[2];
    }

    /** {@inheritDoc} */
    public double value(double x) {
        final double xMc = x - c;
        return (-b / d2) * xMc * Math.exp(-(xMc * xMc) / (2.0 * d2));
    }

}
