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
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;


/**
 * Implements a modified version of the
 * <a href="http://mathworld.wolfram.com/SecantMethod.html">secant method</a>
 * for approximating a zero of a real univariate function.
 * <p>
 * The algorithm is modified to maintain bracketing of a root by successive
 * approximations. Because of forced bracketing, convergence may be slower than
 * the unrestricted secant algorithm. However, this implementation should in
 * general outperform the
 * <a href="http://mathworld.wolfram.com/MethodofFalsePosition.html">
 * regula falsi method.</a></p>
 * <p>
 * The function is assumed to be continuous but not necessarily smooth.</p>
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 */
public class SecantSolver extends UnivariateRealSolverImpl {

    /**
     * Construct a solver for the given function.
     * @param f function to solve.
     * @deprecated as of 2.0 the function to solve is passed as an argument
     * to the {@link #solve(UnivariateRealFunction, double, double)} or
     * {@link UnivariateRealSolverImpl#solve(UnivariateRealFunction, double, double, double)}
     * method.
     */
    @Deprecated
    public SecantSolver(UnivariateRealFunction f) {
        super(f, 100, 1E-6);
    }

    /**
     * Construct a solver.
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public SecantSolver() {
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
     * Find a zero in the given interval.
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param initial the start value to use (ignored)
     * @param maxEval Maximum number of evaluations.
     * @return the value where the function is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if min is not less than max or the
     * signs of the values of the function at the endpoints are not opposites
     */
    @Override
    public double solve(int maxEval, final UnivariateRealFunction f,
                        final double min, final double max, final double initial)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        setMaximalIterationCount(maxEval);
        return solve(f, min, max, initial);
    }

    /**
     * Find a zero in the given interval.
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param initial the start value to use (ignored)
     * @return the value where the function is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if min is not less than max or the
     * signs of the values of the function at the endpoints are not opposites
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public double solve(final UnivariateRealFunction f,
                        final double min, final double max, final double initial)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        return solve(f, min, max);
    }

    /**
     * Find a zero in the given interval.
     * @param f the function to solve
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @param maxEval Maximum number of evaluations.
     * @return the value where the function is zero
     * @throws MaxIterationsExceededException  if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if min is not less than max or the
     * signs of the values of the function at the endpoints are not opposites
     */
    @Override
    public double solve(int maxEval, final UnivariateRealFunction f,
                        final double min, final double max)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        setMaximalIterationCount(maxEval);
        return solve(f, min, max);
    }

    /**
     * Find a zero in the given interval.
     * @param f the function to solve
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @return the value where the function is zero
     * @throws MaxIterationsExceededException  if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if min is not less than max or the
     * signs of the values of the function at the endpoints are not opposites
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public double solve(final UnivariateRealFunction f,
                        final double min, final double max)
        throws MaxIterationsExceededException, FunctionEvaluationException {

        clearResult();
        verifyInterval(min, max);

        // Index 0 is the old approximation for the root.
        // Index 1 is the last calculated approximation  for the root.
        // Index 2 is a bracket for the root with respect to x0.
        // OldDelta is the length of the bracketing interval of the last
        // iteration.
        double x0 = min;
        double x1 = max;
        double y0 = f.value(x0);
        double y1 = f.value(x1);

        // Verify bracketing
        if (y0 * y1 >= 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.SAME_SIGN_AT_ENDPOINTS, min, max, y0, y1);
        }

        double x2 = x0;
        double y2 = y0;
        double oldDelta = x2 - x1;
        int i = 0;
        while (i < maximalIterationCount) {
            if (FastMath.abs(y2) < FastMath.abs(y1)) {
                x0 = x1;
                x1 = x2;
                x2 = x0;
                y0 = y1;
                y1 = y2;
                y2 = y0;
            }
            if (FastMath.abs(y1) <= functionValueAccuracy) {
                setResult(x1, i);
                return result;
            }
            if (FastMath.abs(oldDelta) <
                FastMath.max(relativeAccuracy * FastMath.abs(x1), absoluteAccuracy)) {
                setResult(x1, i);
                return result;
            }
            double delta;
            if (FastMath.abs(y1) > FastMath.abs(y0)) {
                // Function value increased in last iteration. Force bisection.
                delta = 0.5 * oldDelta;
            } else {
                delta = (x0 - x1) / (1 - y0 / y1);
                if (delta / oldDelta > 1) {
                    // New approximation falls outside bracket.
                    // Fall back to bisection.
                    delta = 0.5 * oldDelta;
                }
            }
            x0 = x1;
            y0 = y1;
            x1 = x1 + delta;
            y1 = f.value(x1);
            if ((y1 > 0) == (y2 > 0)) {
                // New bracket is (x0,x1).
                x2 = x0;
                y2 = y0;
            }
            oldDelta = x2 - x1;
            i++;
        }
        throw new MaxIterationsExceededException(maximalIterationCount);
    }

}
