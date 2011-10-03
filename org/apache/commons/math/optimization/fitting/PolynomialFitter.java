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

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.OptimizationException;

/** This class implements a curve fitting specialized for polynomials.
 * <p>Polynomial fitting is a very simple case of curve fitting. The
 * estimated coefficients are the polynomial coefficients. They are
 * searched by a least square estimator.</p>
 * @version $Revision: 1073270 $ $Date: 2011-02-22 10:19:27 +0100 (mar. 22 févr. 2011) $
 * @since 2.0
 */

public class PolynomialFitter {

    /** Fitter for the coefficients. */
    private final CurveFitter fitter;

    /** Polynomial degree. */
    private final int degree;

    /** Simple constructor.
     * <p>The polynomial fitter built this way are complete polynomials,
     * ie. a n-degree polynomial has n+1 coefficients.</p>
     * @param degree maximal degree of the polynomial
     * @param optimizer optimizer to use for the fitting
     */
    public PolynomialFitter(int degree, final DifferentiableMultivariateVectorialOptimizer optimizer) {
        this.fitter = new CurveFitter(optimizer);
        this.degree = degree;
    }

    /** Add an observed weighted (x,y) point to the sample.
     * @param weight weight of the observed point in the fit
     * @param x abscissa of the point
     * @param y observed value of the point at x, after fitting we should
     * have P(x) as close as possible to this value
     */
    public void addObservedPoint(double weight, double x, double y) {
        fitter.addObservedPoint(weight, x, y);
    }

    /**
     * Remove all observations.
     * @since 2.2
     */
    public void clearObservations() {
        fitter.clearObservations();
    }

    /** Get the polynomial fitting the weighted (x, y) points.
     * @return polynomial function best fitting the observed points
     * @exception OptimizationException if the algorithm failed to converge
     */
    public PolynomialFunction fit() throws OptimizationException {
        try {
            return new PolynomialFunction(fitter.fit(new ParametricPolynomial(), new double[degree + 1]));
        } catch (FunctionEvaluationException fee) {
            // should never happen
            throw new RuntimeException(fee);
        }
    }

    /** Dedicated parametric polynomial class. */
    private static class ParametricPolynomial implements ParametricRealFunction {

        /** {@inheritDoc} */
        public double[] gradient(double x, double[] parameters) {
            final double[] gradient = new double[parameters.length];
            double xn = 1.0;
            for (int i = 0; i < parameters.length; ++i) {
                gradient[i] = xn;
                xn *= x;
            }
            return gradient;
        }

        /** {@inheritDoc} */
        public double value(final double x, final double[] parameters) {
            double y = 0;
            for (int i = parameters.length - 1; i >= 0; --i) {
                y = y * x + parameters[i];
            }
            return y;
        }

    }

}
