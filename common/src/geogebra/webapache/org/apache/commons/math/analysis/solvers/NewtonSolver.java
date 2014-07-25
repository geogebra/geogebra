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

package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * Implements <a href="http://mathworld.wolfram.com/NewtonsMethod.html">
 * Newton's Method</a> for finding zeros of real univariate functions.
 * <p>
 * The function should be continuous but not necessarily smooth.</p>
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 */
public class NewtonSolver extends UnivariateRealSolverImpl {

    /**
     * Construct a solver for the given function.
     * @param f function to solve.
     * @deprecated as of 2.0 the function to solve is passed as an argument
     * to the {@link #solve(UnivariateRealFunction, double, double)} or
     * {@link UnivariateRealSolverImpl#solve(UnivariateRealFunction, double, double, double)}
     * method.
     */
    @Deprecated
    public NewtonSolver(DifferentiableUnivariateRealFunction f) {
        super(f, 100, 1E-6);
    }

    /**
     * Construct a solver.
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public NewtonSolver() {
        super(100, 1E-6);
    }

    /** {@inheritDoc} */
    @Deprecated
    public double solve(final double min, final double max)
        throws MaxIterationsExceededException, FunctionEvaluationException  {
        return solve(f, min, max);
    }

    /** {@inheritDoc} */
    @Deprecated
    public double solve(final double min, final double max, final double startValue)
        throws MaxIterationsExceededException, FunctionEvaluationException  {
        return solve(f, min, max, startValue);
    }

    /**
     * Find a zero near the midpoint of <code>min</code> and <code>max</code>.
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param maxEval Maximum number of evaluations.
     * @return the value where the function is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the function or derivative
     * @throws IllegalArgumentException if min is not less than max
     */
    @Override
    public double solve(int maxEval, final UnivariateRealFunction f,
                        final double min, final double max)
        throws MaxIterationsExceededException, FunctionEvaluationException  {
        setMaximalIterationCount(maxEval);
        return solve(f, min, max);
    }

    /**
     * Find a zero near the midpoint of <code>min</code> and <code>max</code>.
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @return the value where the function is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the function or derivative
     * @throws IllegalArgumentException if min is not less than max
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public double solve(final UnivariateRealFunction f,
                        final double min, final double max)
        throws MaxIterationsExceededException, FunctionEvaluationException  {
        return solve(f, min, max, UnivariateRealSolverUtils.midpoint(min, max));
    }

    /**
     * Find a zero near the value <code>startValue</code>.
     *
     * @param f the function to solve
     * @param min the lower bound for the interval (ignored).
     * @param max the upper bound for the interval (ignored).
     * @param startValue the start value to use.
     * @param maxEval Maximum number of evaluations.
     * @return the value where the function is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the function or derivative
     * @throws IllegalArgumentException if startValue is not between min and max or
     * if function is not a {@link DifferentiableUnivariateRealFunction} instance
     */
    @Override
    public double solve(int maxEval, final UnivariateRealFunction f,
                        final double min, final double max, final double startValue)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        setMaximalIterationCount(maxEval);
        return solve(f, min, max, startValue);
    }

    /**
     * Find a zero near the value <code>startValue</code>.
     *
     * @param f the function to solve
     * @param min the lower bound for the interval (ignored).
     * @param max the upper bound for the interval (ignored).
     * @param startValue the start value to use.
     * @return the value where the function is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the function or derivative
     * @throws IllegalArgumentException if startValue is not between min and max or
     * if function is not a {@link DifferentiableUnivariateRealFunction} instance
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public double solve(final UnivariateRealFunction f,
                        final double min, final double max, final double startValue)
        throws MaxIterationsExceededException, FunctionEvaluationException {

        try {

            final UnivariateRealFunction derivative =
                ((DifferentiableUnivariateRealFunction) f).derivative();
            clearResult();
            verifySequence(min, startValue, max);

            double x0 = startValue;
            double x1;

            int i = 0;
            while (i < maximalIterationCount) {

                x1 = x0 - (f.value(x0) / derivative.value(x0));
                if (FastMath.abs(x1 - x0) <= absoluteAccuracy) {
                    setResult(x1, i);
                    return x1;
                }

                x0 = x1;
                ++i;
            }

            throw new MaxIterationsExceededException(maximalIterationCount);
        } catch (ClassCastException cce) {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.FUNCTION_NOT_DIFFERENTIABLE);
        }
    }

}
