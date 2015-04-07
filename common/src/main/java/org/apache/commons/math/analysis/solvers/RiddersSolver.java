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

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;

/**
 * Implements the <a href="http://mathworld.wolfram.com/RiddersMethod.html">
 * Ridders' Method</a> for root finding of real univariate functions. For
 * reference, see C. Ridders, <i>A new algorithm for computing a single root
 * of a real continuous function </i>, IEEE Transactions on Circuits and
 * Systems, 26 (1979), 979 - 980.
 * <p>
 * The function should be continuous but not necessarily smooth.</p>
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 * @since 1.2
 */
public class RiddersSolver extends UnivariateRealSolverImpl {

    /**
     * Construct a solver for the given function.
     *
     * @param f function to solve
     * @deprecated as of 2.0 the function to solve is passed as an argument
     * to the {@link #solve(UnivariateRealFunction, double, double)} or
     * {@link UnivariateRealSolverImpl#solve(UnivariateRealFunction, double, double, double)}
     * method.
     */
    @Deprecated
    public RiddersSolver(UnivariateRealFunction f) {
        super(f, 100, 1E-6);
    }

    /**
     * Construct a solver.
     * @deprecated in 2.2
     */
    @Deprecated
    public RiddersSolver() {
        super(100, 1E-6);
    }

    /** {@inheritDoc} */
    @Deprecated
    public double solve(final double min, final double max)
        throws ConvergenceException, FunctionEvaluationException {
        return solve(f, min, max);
    }

    /** {@inheritDoc} */
    @Deprecated
    public double solve(final double min, final double max, final double initial)
        throws ConvergenceException, FunctionEvaluationException {
        return solve(f, min, max, initial);
    }

    /**
     * Find a root in the given interval with initial value.
     * <p>
     * Requires bracketing condition.</p>
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param initial the start value to use
     * @param maxEval Maximum number of evaluations.
     * @return the point at which the function value is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     */
    @Override
    public double solve(int maxEval, final UnivariateRealFunction f,
                        final double min, final double max, final double initial)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        setMaximalIterationCount(maxEval);
        return solve(f, min, max, initial);
    }

    /**
     * Find a root in the given interval with initial value.
     * <p>
     * Requires bracketing condition.</p>
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param initial the start value to use
     * @return the point at which the function value is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public double solve(final UnivariateRealFunction f,
                        final double min, final double max, final double initial)
        throws MaxIterationsExceededException, FunctionEvaluationException {

        // check for zeros before verifying bracketing
        if (f.value(min) == 0.0) { return min; }
        if (f.value(max) == 0.0) { return max; }
        if (f.value(initial) == 0.0) { return initial; }

        verifyBracketing(min, max, f);
        verifySequence(min, initial, max);
        if (isBracketing(min, initial, f)) {
            return solve(f, min, initial);
        } else {
            return solve(f, initial, max);
        }
    }

    /**
     * Find a root in the given interval.
     * <p>
     * Requires bracketing condition.</p>
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param maxEval Maximum number of evaluations.
     * @return the point at which the function value is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     */
    @Override
    public double solve(int maxEval, final UnivariateRealFunction f,
                        final double min, final double max)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        setMaximalIterationCount(maxEval);
        return solve(f, min, max);
    }

    /**
     * Find a root in the given interval.
     * <p>
     * Requires bracketing condition.</p>
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @return the point at which the function value is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public double solve(final UnivariateRealFunction f,
                        final double min, final double max)
        throws MaxIterationsExceededException, FunctionEvaluationException {

        // [x1, x2] is the bracketing interval in each iteration
        // x3 is the midpoint of [x1, x2]
        // x is the new root approximation and an endpoint of the new interval
        double x1 = min;
        double y1 = f.value(x1);
        double x2 = max;
        double y2 = f.value(x2);

        // check for zeros before verifying bracketing
        if (y1 == 0.0) {
            return min;
        }
        if (y2 == 0.0) {
            return max;
        }
        verifyBracketing(min, max, f);

        int i = 1;
        double oldx = Double.POSITIVE_INFINITY;
        while (i <= maximalIterationCount) {
            // calculate the new root approximation
            final double x3 = 0.5 * (x1 + x2);
            final double y3 = f.value(x3);
            if (FastMath.abs(y3) <= functionValueAccuracy) {
                setResult(x3, i);
                return result;
            }
            final double delta = 1 - (y1 * y2) / (y3 * y3);  // delta > 1 due to bracketing
            final double correction = (MathUtils.sign(y2) * MathUtils.sign(y3)) *
                                      (x3 - x1) / FastMath.sqrt(delta);
            final double x = x3 - correction;                // correction != 0
            final double y = f.value(x);

            // check for convergence
            final double tolerance = FastMath.max(relativeAccuracy * FastMath.abs(x), absoluteAccuracy);
            if (FastMath.abs(x - oldx) <= tolerance) {
                setResult(x, i);
                return result;
            }
            if (FastMath.abs(y) <= functionValueAccuracy) {
                setResult(x, i);
                return result;
            }

            // prepare the new interval for next iteration
            // Ridders' method guarantees x1 < x < x2
            if (correction > 0.0) {             // x1 < x < x3
                if (MathUtils.sign(y1) + MathUtils.sign(y) == 0.0) {
                    x2 = x;
                    y2 = y;
                } else {
                    x1 = x;
                    x2 = x3;
                    y1 = y;
                    y2 = y3;
                }
            } else {                            // x3 < x < x2
                if (MathUtils.sign(y2) + MathUtils.sign(y) == 0.0) {
                    x1 = x;
                    y1 = y;
                } else {
                    x1 = x3;
                    x2 = x;
                    y1 = y3;
                    y2 = y;
                }
            }
            oldx = x;
            i++;
        }
        throw new MaxIterationsExceededException(maximalIterationCount);
    }
}
