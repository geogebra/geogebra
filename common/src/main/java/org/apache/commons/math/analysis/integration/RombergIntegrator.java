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

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * Implements the <a href="http://mathworld.wolfram.com/RombergIntegration.html">
 * Romberg Algorithm</a> for integration of real univariate functions. For
 * reference, see <b>Introduction to Numerical Analysis</b>, ISBN 038795452X,
 * chapter 3.
 * <p>
 * Romberg integration employs k successive refinements of the trapezoid
 * rule to remove error terms less than order O(N^(-2k)). Simpson's rule
 * is a special case of k = 2.</p>
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 * @since 1.2
 */
public class RombergIntegrator extends UnivariateRealIntegratorImpl {

    /**
     * Construct an integrator for the given function.
     *
     * @param f function to integrate
     * @deprecated as of 2.0 the integrand function is passed as an argument
     * to the {@link #integrate(UnivariateRealFunction, double, double)}method.
     */
    @Deprecated
    public RombergIntegrator(UnivariateRealFunction f) {
        super(f, 32);
    }

    /**
     * Construct an integrator.
     */
    public RombergIntegrator() {
        super(32);
    }

    /** {@inheritDoc} */
    @Deprecated
    public double integrate(final double min, final double max)
        throws MaxIterationsExceededException, FunctionEvaluationException, IllegalArgumentException {
        return integrate(f, min, max);
    }

    /** {@inheritDoc} */
    public double integrate(final UnivariateRealFunction f, final double min, final double max)
        throws MaxIterationsExceededException, FunctionEvaluationException, IllegalArgumentException {

        final int m = maximalIterationCount + 1;
        double previousRow[] = new double[m];
        double currentRow[]  = new double[m];

        clearResult();
        verifyInterval(min, max);
        verifyIterationCount();

        TrapezoidIntegrator qtrap = new TrapezoidIntegrator();
        currentRow[0] = qtrap.stage(f, min, max, 0);
        double olds = currentRow[0];
        for (int i = 1; i <= maximalIterationCount; ++i) {

            // switch rows
            final double[] tmpRow = previousRow;
            previousRow = currentRow;
            currentRow = tmpRow;

            currentRow[0] = qtrap.stage(f, min, max, i);
            for (int j = 1; j <= i; j++) {
                // Richardson extrapolation coefficient
                final double r = (1L << (2 * j)) - 1;
                final double tIJm1 = currentRow[j - 1];
                currentRow[j] = tIJm1 + (tIJm1 - previousRow[j - 1]) / r;
            }
            final double s = currentRow[i];
            if (i >= minimalIterationCount) {
                final double delta  = FastMath.abs(s - olds);
                final double rLimit = relativeAccuracy * (FastMath.abs(olds) + FastMath.abs(s)) * 0.5;
                if ((delta <= rLimit) || (delta <= absoluteAccuracy)) {
                    setResult(s, i);
                    return result;
                }
            }
            olds = s;
        }
        throw new MaxIterationsExceededException(maximalIterationCount);
    }

    /** {@inheritDoc} */
    @Override
    protected void verifyIterationCount() throws IllegalArgumentException {
        super.verifyIterationCount();
        // at most 32 bisection refinements due to higher order divider
        if (maximalIterationCount > 32) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.INVALID_ITERATIONS_LIMITS,
                    0, 32);
        }
    }
}
