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
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * Implements the <a href="http://mathworld.wolfram.com/BrentsMethod.html">
 * Brent algorithm</a> for  finding zeros of real univariate functions.
 * <p>
 * The function should be continuous but not necessarily smooth.</p>
 *
 * @version $Revision:670469 $ $Date:2008-06-23 10:01:38 +0200 (lun., 23 juin 2008) $
 */
public class BrentSolver extends UnivariateRealSolverImpl {

    /**
     * Default absolute accuracy
     * @since 2.1
     */
    public static final double DEFAULT_ABSOLUTE_ACCURACY = 1E-6;

    /** Default maximum number of iterations
     * @since 2.1
     */
    public static final int DEFAULT_MAXIMUM_ITERATIONS = 100;

    /** Serializable version identifier */
    private static final long serialVersionUID = 7694577816772532779L;

    /**
     * Construct a solver for the given function.
     *
     * @param f function to solve.
     * @deprecated as of 2.0 the function to solve is passed as an argument
     * to the {@link #solve(UnivariateRealFunction, double, double)} or
     * {@link UnivariateRealSolverImpl#solve(UnivariateRealFunction, double, double, double)}
     * method.
     */
    @Deprecated
    public BrentSolver(UnivariateRealFunction f) {
        super(f, DEFAULT_MAXIMUM_ITERATIONS, DEFAULT_ABSOLUTE_ACCURACY);
    }

    /**
     * Construct a solver with default properties.
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public BrentSolver() {
        super(DEFAULT_MAXIMUM_ITERATIONS, DEFAULT_ABSOLUTE_ACCURACY);
    }

    /**
     * Construct a solver with the given absolute accuracy.
     *
     * @param absoluteAccuracy lower bound for absolute accuracy of solutions returned by the solver
     * @since 2.1
     */
    public BrentSolver(double absoluteAccuracy) {
        super(DEFAULT_MAXIMUM_ITERATIONS, absoluteAccuracy);
    }

    /**
     * Contstruct a solver with the given maximum iterations and absolute accuracy.
     *
     * @param maximumIterations maximum number of iterations
     * @param absoluteAccuracy lower bound for absolute accuracy of solutions returned by the solver
     * @since 2.1
     */
    public BrentSolver(int maximumIterations, double absoluteAccuracy) {
        super(maximumIterations, absoluteAccuracy);
    }

    /** {@inheritDoc} */
    @Deprecated
    public double solve(double min, double max)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        return solve(f, min, max);
    }

    /** {@inheritDoc} */
    @Deprecated
    public double solve(double min, double max, double initial)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        return solve(f, min, max, initial);
    }

    /**
     * Find a zero in the given interval with an initial guess.
     * <p>Throws <code>IllegalArgumentException</code> if the values of the
     * function at the three points have the same sign (note that it is
     * allowed to have endpoints with the same sign if the initial point has
     * opposite sign function-wise).</p>
     *
     * @param f function to solve.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @param initial the start value to use (must be set to min if no
     * initial point is known).
     * @return the value where the function is zero
     * @throws MaxIterationsExceededException the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating  the function
     * @throws IllegalArgumentException if initial is not between min and max
     * (even if it <em>is</em> a root)
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public double solve(final UnivariateRealFunction f,
                        final double min, final double max, final double initial)
        throws MaxIterationsExceededException, FunctionEvaluationException {

        clearResult();
        if ((initial < min) || (initial > max)) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.INVALID_INTERVAL_INITIAL_VALUE_PARAMETERS,
                  min, initial, max);
        }

        // return the initial guess if it is good enough
        double yInitial = f.value(initial);
        if (FastMath.abs(yInitial) <= functionValueAccuracy) {
            setResult(initial, 0);
            return result;
        }

        // return the first endpoint if it is good enough
        double yMin = f.value(min);
        if (FastMath.abs(yMin) <= functionValueAccuracy) {
            setResult(min, 0);
            return result;
        }

        // reduce interval if min and initial bracket the root
        if (yInitial * yMin < 0) {
            return solve(f, min, yMin, initial, yInitial, min, yMin);
        }

        // return the second endpoint if it is good enough
        double yMax = f.value(max);
        if (FastMath.abs(yMax) <= functionValueAccuracy) {
            setResult(max, 0);
            return result;
        }

        // reduce interval if initial and max bracket the root
        if (yInitial * yMax < 0) {
            return solve(f, initial, yInitial, max, yMax, initial, yInitial);
        }

        throw MathRuntimeException.createIllegalArgumentException(
              LocalizedFormats.SAME_SIGN_AT_ENDPOINTS, min, max, yMin, yMax);

    }

    /**
     * Find a zero in the given interval with an initial guess.
     * <p>Throws <code>IllegalArgumentException</code> if the values of the
     * function at the three points have the same sign (note that it is
     * allowed to have endpoints with the same sign if the initial point has
     * opposite sign function-wise).</p>
     *
     * @param f function to solve.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @param initial the start value to use (must be set to min if no
     * initial point is known).
     * @param maxEval Maximum number of evaluations.
     * @return the value where the function is zero
     * @throws MaxIterationsExceededException the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating  the function
     * @throws IllegalArgumentException if initial is not between min and max
     * (even if it <em>is</em> a root)
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
     * <p>
     * Requires that the values of the function at the endpoints have opposite
     * signs. An <code>IllegalArgumentException</code> is thrown if this is not
     * the case.</p>
     *
     * @param f the function to solve
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @return the value where the function is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
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

        double ret = Double.NaN;

        double yMin = f.value(min);
        double yMax = f.value(max);

        // Verify bracketing
        double sign = yMin * yMax;
        if (sign > 0) {
            // check if either value is close to a zero
            if (FastMath.abs(yMin) <= functionValueAccuracy) {
                setResult(min, 0);
                ret = min;
            } else if (FastMath.abs(yMax) <= functionValueAccuracy) {
                setResult(max, 0);
                ret = max;
            } else {
                // neither value is close to zero and min and max do not bracket root.
                throw MathRuntimeException.createIllegalArgumentException(
                        LocalizedFormats.SAME_SIGN_AT_ENDPOINTS, min, max, yMin, yMax);
            }
        } else if (sign < 0){
            // solve using only the first endpoint as initial guess
            ret = solve(f, min, yMin, max, yMax, min, yMin);
        } else {
            // either min or max is a root
            if (yMin == 0.0) {
                ret = min;
            } else {
                ret = max;
            }
        }

        return ret;
    }

    /**
     * Find a zero in the given interval.
     * <p>
     * Requires that the values of the function at the endpoints have opposite
     * signs. An <code>IllegalArgumentException</code> is thrown if this is not
     * the case.</p>
     *
     * @param f the function to solve
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @param maxEval Maximum number of evaluations.
     * @return the value where the function is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
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
     * Find a zero starting search according to the three provided points.
     * @param f the function to solve
     * @param x0 old approximation for the root
     * @param y0 function value at the approximation for the root
     * @param x1 last calculated approximation for the root
     * @param y1 function value at the last calculated approximation
     * for the root
     * @param x2 bracket point (must be set to x0 if no bracket point is
     * known, this will force starting with linear interpolation)
     * @param y2 function value at the bracket point.
     * @return the value where the function is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     */
    private double solve(final UnivariateRealFunction f,
                         double x0, double y0,
                         double x1, double y1,
                         double x2, double y2)
    throws MaxIterationsExceededException, FunctionEvaluationException {

        double delta = x1 - x0;
        double oldDelta = delta;

        int i = 0;
        while (i < maximalIterationCount) {
            if (FastMath.abs(y2) < FastMath.abs(y1)) {
                // use the bracket point if is better than last approximation
                x0 = x1;
                x1 = x2;
                x2 = x0;
                y0 = y1;
                y1 = y2;
                y2 = y0;
            }
            if (FastMath.abs(y1) <= functionValueAccuracy) {
                // Avoid division by very small values. Assume
                // the iteration has converged (the problem may
                // still be ill conditioned)
                setResult(x1, i);
                return result;
            }
            double dx = x2 - x1;
            double tolerance =
                FastMath.max(relativeAccuracy * FastMath.abs(x1), absoluteAccuracy);
            if (FastMath.abs(dx) <= tolerance) {
                setResult(x1, i);
                return result;
            }
            if ((FastMath.abs(oldDelta) < tolerance) ||
                    (FastMath.abs(y0) <= FastMath.abs(y1))) {
                // Force bisection.
                delta = 0.5 * dx;
                oldDelta = delta;
            } else {
                double r3 = y1 / y0;
                double p;
                double p1;
                // the equality test (x0 == x2) is intentional,
                // it is part of the original Brent's method,
                // it should NOT be replaced by proximity test
                if (x0 == x2) {
                    // Linear interpolation.
                    p = dx * r3;
                    p1 = 1.0 - r3;
                } else {
                    // Inverse quadratic interpolation.
                    double r1 = y0 / y2;
                    double r2 = y1 / y2;
                    p = r3 * (dx * r1 * (r1 - r2) - (x1 - x0) * (r2 - 1.0));
                    p1 = (r1 - 1.0) * (r2 - 1.0) * (r3 - 1.0);
                }
                if (p > 0.0) {
                    p1 = -p1;
                } else {
                    p = -p;
                }
                if (2.0 * p >= 1.5 * dx * p1 - FastMath.abs(tolerance * p1) ||
                        p >= FastMath.abs(0.5 * oldDelta * p1)) {
                    // Inverse quadratic interpolation gives a value
                    // in the wrong direction, or progress is slow.
                    // Fall back to bisection.
                    delta = 0.5 * dx;
                    oldDelta = delta;
                } else {
                    oldDelta = delta;
                    delta = p / p1;
                }
            }
            // Save old X1, Y1
            x0 = x1;
            y0 = y1;
            // Compute new X1, Y1
            if (FastMath.abs(delta) > tolerance) {
                x1 = x1 + delta;
            } else if (dx > 0.0) {
                x1 = x1 + 0.5 * tolerance;
            } else if (dx <= 0.0) {
                x1 = x1 - 0.5 * tolerance;
            }
            y1 = f.value(x1);
            if ((y1 > 0) == (y2 > 0)) {
                x2 = x0;
                y2 = y0;
                delta = x1 - x0;
                oldDelta = delta;
            }
            i++;
        }
        throw new MaxIterationsExceededException(maximalIterationCount);
    }
}
