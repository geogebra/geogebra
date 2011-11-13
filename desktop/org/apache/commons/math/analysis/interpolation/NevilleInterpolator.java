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
package org.apache.commons.math.analysis.interpolation;

import java.io.Serializable;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm;

/**
 * Implements the <a href="http://mathworld.wolfram.com/NevillesAlgorithm.html">
 * Neville's Algorithm</a> for interpolation of real univariate functions. For
 * reference, see <b>Introduction to Numerical Analysis</b>, ISBN 038795452X,
 * chapter 2.
 * <p>
 * The actual code of Neville's evalution is in PolynomialFunctionLagrangeForm,
 * this class provides an easy-to-use interface to it.</p>
 *
 * @version $Revision: 799857 $ $Date: 2009-08-01 15:07:12 +0200 (sam. 01 août 2009) $
 * @since 1.2
 */
public class NevilleInterpolator implements UnivariateRealInterpolator,
    Serializable {

    /** serializable version identifier */
    static final long serialVersionUID = 3003707660147873733L;

    /**
     * Computes an interpolating function for the data set.
     *
     * @param x the interpolating points array
     * @param y the interpolating values array
     * @return a function which interpolates the data set
     * @throws MathException if arguments are invalid
     */
    public PolynomialFunctionLagrangeForm interpolate(double x[], double y[])
        throws MathException {
        return new PolynomialFunctionLagrangeForm(x, y);
    }
}
