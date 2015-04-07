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
 * Implements the <a href="http://mathworld.wolfram.com/SimpsonsRule.html">
 * Simpson's Rule</a> for integration of real univariate functions. For
 * reference, see <b>Introduction to Numerical Analysis</b>, ISBN 038795452X,
 * chapter 3.
 * <p>
 * This implementation employs basic trapezoid rule as building blocks to
 * calculate the Simpson's rule of alternating 2/3 and 4/3.</p>
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 * @since 1.2
 */
public class SimpsonIntegrator extends UnivariateRealIntegratorImpl {

    /**
     * Construct an integrator for the given function.
     *
     * @param f function to integrate
     * @deprecated as of 2.0 the integrand function is passed as an argument
     * to the {@link #integrate(UnivariateRealFunction, double, double)}method.
     */
    @Deprecated
    public SimpsonIntegrator(UnivariateRealFunction f) {
        super(f, 64);
    }

    /**
     * Construct an integrator.
     */
    public SimpsonIntegrator() {
        super(64);
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

        clearResult();
        verifyInterval(min, max);
        verifyIterationCount();

        TrapezoidIntegrator qtrap = new TrapezoidIntegrator();
        if (minimalIterationCount == 1) {
            final double s = (4 * qtrap.stage(f, min, max, 1) - qtrap.stage(f, min, max, 0)) / 3.0;
            setResult(s, 1);
            return result;
        }
        // Simpson's rule requires at least two trapezoid stages.
        double olds = 0;
        double oldt = qtrap.stage(f, min, max, 0);
        for (int i = 1; i <= maximalIterationCount; ++i) {
            final double t = qtrap.stage(f, min, max, i);
            final double s = (4 * t - oldt) / 3.0;
            if (i >= minimalIterationCount) {
                final double delta = FastMath.abs(s - olds);
                final double rLimit =
                    relativeAccuracy * (FastMath.abs(olds) + FastMath.abs(s)) * 0.5;
                if ((delta <= rLimit) || (delta <= absoluteAccuracy)) {
                    setResult(s, i);
                    return result;
                }
            }
            olds = s;
            oldt = t;
        }
        throw new MaxIterationsExceededException(maximalIterationCount);
    }

    /** {@inheritDoc} */
    @Override
    protected void verifyIterationCount() throws IllegalArgumentException {
        super.verifyIterationCount();
        // at most 64 bisection refinements
        if (maximalIterationCount > 64) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.INVALID_ITERATIONS_LIMITS,
                    0, 64);
        }
    }
}
