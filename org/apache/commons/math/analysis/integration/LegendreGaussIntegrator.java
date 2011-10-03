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
package org.apache.commons.math.analysis.integration;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * Implements the <a href="http://mathworld.wolfram.com/Legendre-GaussQuadrature.html">
 * Legendre-Gauss</a> quadrature formula.
 * <p>
 * Legendre-Gauss integrators are efficient integrators that can
 * accurately integrate functions with few functions evaluations. A
 * Legendre-Gauss integrator using an n-points quadrature formula can
 * integrate exactly 2n-1 degree polynomials.
 * </p>
 * <p>
 * These integrators evaluate the function on n carefully chosen
 * abscissas in each step interval (mapped to the canonical [-1  1] interval).
 * The evaluation abscissas are not evenly spaced and none of them are
 * at the interval endpoints. This implies the function integrated can be
 * undefined at integration interval endpoints.
 * </p>
 * <p>
 * The evaluation abscissas x<sub>i</sub> are the roots of the degree n
 * Legendre polynomial. The weights a<sub>i</sub> of the quadrature formula
 * integrals from -1 to +1 &int; Li<sup>2</sup> where Li (x) =
 * &prod; (x-x<sub>k</sub>)/(x<sub>i</sub>-x<sub>k</sub>) for k != i.
 * </p>
 * <p>
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 * @since 1.2
 */

public class LegendreGaussIntegrator extends UnivariateRealIntegratorImpl {

    /** Abscissas for the 2 points method. */
    private static final double[] ABSCISSAS_2 = {
        -1.0 / FastMath.sqrt(3.0),
         1.0 / FastMath.sqrt(3.0)
    };

    /** Weights for the 2 points method. */
    private static final double[] WEIGHTS_2 = {
        1.0,
        1.0
    };

    /** Abscissas for the 3 points method. */
    private static final double[] ABSCISSAS_3 = {
        -FastMath.sqrt(0.6),
         0.0,
         FastMath.sqrt(0.6)
    };

    /** Weights for the 3 points method. */
    private static final double[] WEIGHTS_3 = {
        5.0 / 9.0,
        8.0 / 9.0,
        5.0 / 9.0
    };

    /** Abscissas for the 4 points method. */
    private static final double[] ABSCISSAS_4 = {
        -FastMath.sqrt((15.0 + 2.0 * FastMath.sqrt(30.0)) / 35.0),
        -FastMath.sqrt((15.0 - 2.0 * FastMath.sqrt(30.0)) / 35.0),
         FastMath.sqrt((15.0 - 2.0 * FastMath.sqrt(30.0)) / 35.0),
         FastMath.sqrt((15.0 + 2.0 * FastMath.sqrt(30.0)) / 35.0)
    };

    /** Weights for the 4 points method. */
    private static final double[] WEIGHTS_4 = {
        (90.0 - 5.0 * FastMath.sqrt(30.0)) / 180.0,
        (90.0 + 5.0 * FastMath.sqrt(30.0)) / 180.0,
        (90.0 + 5.0 * FastMath.sqrt(30.0)) / 180.0,
        (90.0 - 5.0 * FastMath.sqrt(30.0)) / 180.0
    };

    /** Abscissas for the 5 points method. */
    private static final double[] ABSCISSAS_5 = {
        -FastMath.sqrt((35.0 + 2.0 * FastMath.sqrt(70.0)) / 63.0),
        -FastMath.sqrt((35.0 - 2.0 * FastMath.sqrt(70.0)) / 63.0),
         0.0,
         FastMath.sqrt((35.0 - 2.0 * FastMath.sqrt(70.0)) / 63.0),
         FastMath.sqrt((35.0 + 2.0 * FastMath.sqrt(70.0)) / 63.0)
    };

    /** Weights for the 5 points method. */
    private static final double[] WEIGHTS_5 = {
        (322.0 - 13.0 * FastMath.sqrt(70.0)) / 900.0,
        (322.0 + 13.0 * FastMath.sqrt(70.0)) / 900.0,
        128.0 / 225.0,
        (322.0 + 13.0 * FastMath.sqrt(70.0)) / 900.0,
        (322.0 - 13.0 * FastMath.sqrt(70.0)) / 900.0
    };

    /** Abscissas for the current method. */
    private final double[] abscissas;

    /** Weights for the current method. */
    private final double[] weights;

    /**
     * Build a Legendre-Gauss integrator.
     * @param n number of points desired (must be between 2 and 5 inclusive)
     * @param defaultMaximalIterationCount maximum number of iterations
     * @exception IllegalArgumentException if the number of points is not
     * in the supported range
     */
    public LegendreGaussIntegrator(final int n, final int defaultMaximalIterationCount)
        throws IllegalArgumentException {
        super(defaultMaximalIterationCount);
        switch(n) {
        case 2 :
            abscissas = ABSCISSAS_2;
            weights   = WEIGHTS_2;
            break;
        case 3 :
            abscissas = ABSCISSAS_3;
            weights   = WEIGHTS_3;
            break;
        case 4 :
            abscissas = ABSCISSAS_4;
            weights   = WEIGHTS_4;
            break;
        case 5 :
            abscissas = ABSCISSAS_5;
            weights   = WEIGHTS_5;
            break;
        default :
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.N_POINTS_GAUSS_LEGENDRE_INTEGRATOR_NOT_SUPPORTED,
                    n, 2, 5);
        }

    }

    /** {@inheritDoc} */
    @Deprecated
    public double integrate(final double min, final double max)
        throws ConvergenceException,  FunctionEvaluationException, IllegalArgumentException {
        return integrate(f, min, max);
    }

    /** {@inheritDoc} */
    public double integrate(final UnivariateRealFunction f, final double min, final double max)
        throws ConvergenceException,  FunctionEvaluationException, IllegalArgumentException {

        clearResult();
        verifyInterval(min, max);
        verifyIterationCount();

        // compute first estimate with a single step
        double oldt = stage(f, min, max, 1);

        int n = 2;
        for (int i = 0; i < maximalIterationCount; ++i) {

            // improve integral with a larger number of steps
            final double t = stage(f, min, max, n);

            // estimate error
            final double delta = FastMath.abs(t - oldt);
            final double limit =
                FastMath.max(absoluteAccuracy,
                         relativeAccuracy * (FastMath.abs(oldt) + FastMath.abs(t)) * 0.5);

            // check convergence
            if ((i + 1 >= minimalIterationCount) && (delta <= limit)) {
                setResult(t, i);
                return result;
            }

            // prepare next iteration
            double ratio = FastMath.min(4, FastMath.pow(delta / limit, 0.5 / abscissas.length));
            n = FastMath.max((int) (ratio * n), n + 1);
            oldt = t;

        }

        throw new MaxIterationsExceededException(maximalIterationCount);

    }

    /**
     * Compute the n-th stage integral.
     * @param f the integrand function
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param n number of steps
     * @return the value of n-th stage integral
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function
     */
    private double stage(final UnivariateRealFunction f,
                         final double min, final double max, final int n)
        throws FunctionEvaluationException {

        // set up the step for the current stage
        final double step     = (max - min) / n;
        final double halfStep = step / 2.0;

        // integrate over all elementary steps
        double midPoint = min + halfStep;
        double sum = 0.0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < abscissas.length; ++j) {
                sum += weights[j] * f.value(midPoint + halfStep * abscissas[j]);
            }
            midPoint += step;
        }

        return halfStep * sum;

    }

}
